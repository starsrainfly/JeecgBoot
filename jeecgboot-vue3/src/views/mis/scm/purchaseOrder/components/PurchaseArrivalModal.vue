<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose title="采购到货入库申请" :width="1200" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="PurchaseArrivalForm"/>

    <!-- 操作提示与拆行按钮 -->
    <div style="margin: 8px 0;">
      <a-button size="small" type="primary" ghost @click="handleSplitRow" pre-icon="ant-design:split-cells-outlined">拆分多批次</a-button>
      <span style="margin-left: 12px; color: #999;">同一物料到货多个批次时：勾选该行 → 点"拆分多批次" → 在新行填批次和数量</span>
    </div>

    <JVxeTable
      keep-source
      resizable
      ref="arrivalDetail"
      :loading="detailTable.loading"
      :columns="detailTable.columns"
      :dataSource="detailTable.dataSource"
      :height="320"
      :rowNumber="true"
      :rowSelection="true"
      :toolbar="true"
      @valueChange="handleDetailValueChange"
    />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, reactive, nextTick } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm, FormSchema } from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable';
  import { JVxeTypes } from '/@/components/jeecg/JVxeTable/types';
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { defHttp } from '/@/utils/http/axios';
  import { purchaseOrderDetailList } from '../PurchaseOrder.api';
  // TODO: 确认你们材料入库这两个文件的实际路径，按项目结构调整
  import { stockInDetailColumns as baseColumns } from '/@/views/mis/wms/MaterialIn/StockIn.data';
  import { saveOrUpdate as saveStockIn } from '/@/views/mis/wms/MaterialIn/StockIn.api';

  import dayjs from 'dayjs';  // 顶部确保有导入

  const emit = defineEmits(['register', 'success']);
  const { createMessage } = useMessage();

  // ========== 子表列：共享入库单列配置，覆盖差异化属性 ==========
  const arrivalColumns = [
    {
      title: '采购明细id',
      key: 'sourceDetailId',
      type: JVxeTypes.input,
      width: '120px',
      visible: false,
    },
    ...baseColumns.map(col => {
      const key = col.key as string;
      // 从采购单带出的字段禁改
      if (['goodsType', 'goodsId', 'goodsCode', 'goodsName', 'goodsSpec', 'unit',
        'currency', 'exchangeRate', 'unitPrice', 'totalAmount', 'goodsColor'].includes(key)) {
        return { ...col, disabled: true };
      }
      // 质检状态由仓库审核时填写，到货申请不显示
      if (key === 'qcStatus') return { ...col, visible: false };
      if (key === 'applyQty') return { ...col, title: '本次到货数量' };
      return col;
    }),
  ];

  // ========== 主表表单 ==========
  const arrivalFormSchema: FormSchema[] = [
    { label: '入库单号', field: 'stockInNo', component: 'Input', dynamicDisabled: true },
    { label: '采购单号', field: 'sourceOrderNo', component: 'Input', dynamicDisabled: true },
    { label: '供应商', field: 'supplierName', component: 'Input', dynamicDisabled: true },
    { label: '采购员', field: 'purchaserName', component: 'Input', dynamicDisabled: true },
    {
      label: '入库仓库', field: 'warehouseId', component: 'JDictSelectTag',
      componentProps: { dictCode: "mis_warehouse,name,id" },
      dynamicRules: () => [{ required: true, message: '请选择入库仓库!' }],
    },
    { label: '要求到货日期', field: 'expectedDate', component: 'Input', dynamicDisabled: true },
    { label: '备注', field: 'remark', component: 'Input' },
    // 隐藏字段
    { label: '', field: 'stockInType', component: 'Input', show: false },
    { label: '', field: 'sourceOrderType', component: 'Input', show: false },
    { label: '', field: 'sourceOrderId', component: 'Input', show: false },
    { label: '', field: 'supplierId', component: 'Input', show: false },
    { label: '', field: 'purchaserId', component: 'Input', show: false },
  ];

  const refKeys = ref(['arrivalDetail']);
  const activeKey = ref('arrivalDetail');
  const arrivalDetail = ref();
  const tableRefs = { arrivalDetail };
  const detailTable = reactive({
    loading: false,
    dataSource: [] as any[],
    columns: arrivalColumns,
  });

  const [registerForm, { resetFields, setFieldsValue }] = useForm({
    schemas: arrivalFormSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 8 },
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await reset();
    setModalProps({ confirmLoading: false });
    const order = data.record;
    // 主表预填
    await setFieldsValue({
      stockInType: 'PURCHASE',
      sourceOrderType: 'PURCHASE',
      sourceOrderId: order.id,
      sourceOrderNo: order.orderNo,
      supplierId: order.supplierId,
      supplierName: order.supplierName,
      purchaserId: order.purchaserId,
      purchaserName: order.purchaserName,
      warehouseId: order.warehouseId,       // 建议入库仓库，可改
      expectedDate: order.expectedDate,
    });
    // 明细预填：按剩余可到货数量生成，已全部到货的行不出现在弹窗里
    detailTable.loading = true;
    try {
      const details = await defHttp.get({ url: purchaseOrderDetailList, params: { id: order.id } });
      const rows = (details || [])
        .filter(d => Number(d.remainingQty) > 0)
        .map(d => ({
          sourceDetailId: d.id,             // 关键：回写采购明细靠它
          goodsId: d.goodsId,
          goodsType: d.goodsType,
          goodsCode: d.goodsCode,
          goodsName: d.goodsName,
          goodsSpec: d.goodsSpec,
          unit: d.unit,
          applyQty: d.remainingQty,         // 默认本次到货 = 剩余量，可改
          actualQty: d.remainingQty,
          currency: order.currencyCode || 'CNY',
          exchangeRate: order.exchangeRate || 1,
          unitPrice: d.unitPrice,
          totalAmount: +(Number(d.remainingQty || 0) * Number(d.unitPrice || 0)).toFixed(2),
          remark: d.remark,
        }));
      detailTable.dataSource = rows;
      if (rows.length === 0) {
        createMessage.warning('该采购单明细已全部到货');
      }
    } finally {
      detailTable.loading = false;
    }
  });

  const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] =
    useJvxeMethod(requestAddOrEdit, classifyIntoFormData, tableRefs, activeKey, refKeys);

  async function reset() {
    await resetFields();
    detailTable.dataSource = [];
  }

  /** 拆分多批次：复制选中行的物料信息，批次/数量/COA留空 */
  function handleSplitRow() {
    // 获取 vxe-table 原生实例，再取复选框选中行
    const $table = arrivalDetail.value?.getXTable?.();
    const selected = $table ? $table.getCheckboxRecords() : [];
    if (selected.length !== 1) {
      createMessage.warning('请先勾选一行要拆分的物料');
      return;
    }
    const src = selected[0];
    arrivalDetail.value?.addRows([{
      sourceDetailId: src.sourceDetailId,
      goodsId: src.goodsId,
      goodsType: src.goodsType,
      goodsCode: src.goodsCode,
      goodsName: src.goodsName,
      goodsSpec: src.goodsSpec,
      unit: src.unit,
      currency: src.currency,
      exchangeRate: src.exchangeRate,
      unitPrice: src.unitPrice,
      applyQty: null,
      actualQty: null,
      totalAmount: null,
    }]);
  }

  /** 子表值联动：本次到货数量 → 实收数量/金额 */
  function handleDetailValueChange({ column, row, value, target }) {
    if (!['applyQty', 'actualQty', 'unitPrice', 'productionDate', 'shelfLife'].includes(column.key)) return;
    const merged = { ...row, [column.key]: value };
    const updates: any = {};
   //  // 改到货数量时，实收未填则同步
   // // if (column.key === 'applyQty' && (row.actualQty === '' || row.actualQty == null)) {
   //    updates.actualQty = value;
   //    merged.actualQty = value;
   // // }
   //  const qty = Number(merged.actualQty ?? merged.applyQty) || 0;
   //  updates.totalAmount = +(qty * (Number(merged.unitPrice) || 0)).toFixed(2);
   //  target.setValues([{ rowKey: row.id, values: updates }]);
    // ===== 数量/金额联动（保持原有）=====
    if (['applyQty', 'actualQty', 'unitPrice'].includes(column.key)) {
      if (column.key === 'applyQty') {
        updates.actualQty = value;
        merged.actualQty = value;
      }
      const qty = Number(merged.actualQty ?? merged.applyQty) || 0;
      updates.totalAmount = +(qty * (Number(merged.unitPrice) || 0)).toFixed(2);
    }

    // ===== 日期联动：生产日期 + 保质天数 = 失效日期 =====
    if (['productionDate', 'shelfLife'].includes(column.key)) {
      // 从当前行取最新值（如果本次改的是A，B从row里取）
      const pDate = column.key === 'productionDate' ? value : row.productionDate;
      const life  = column.key === 'shelfLife'      ? value : row.shelfLife;

      if (pDate && life && !isNaN(Number(life)) && Number(life) > 0) {
        const expiry = dayjs(pDate).add(Number(life), 'day');
        updates.expiryDate = expiry.format('YYYY-MM-DD');  // ← 改为你实际的失效日期key
      }
    }

    // 统一回写
    if (Object.keys(updates).length > 0) {
      target.setValues([{ rowKey: row.id, values: updates }]);
    }
  }

  function classifyIntoFormData(allValues) {
    let main = Object.assign({}, allValues.formValue);
    delete main.expectedDate; // 仅展示用，不是入库单字段
    const detailList = (allValues.tablesValue[0].tableData || []).map(row => {
      const qty = Number(row.actualQty ?? row.applyQty) || 0;
      return { ...row, totalAmount: +(qty * (Number(row.unitPrice) || 0)).toFixed(2) };
    });
    return { ...main, isProduct: '0', stockInDetailList: detailList };
  }

  async function requestAddOrEdit(values) {
    try {
      setModalProps({ confirmLoading: true });
      await saveStockIn(values, false); // 调现有入库申请 add 接口
      createMessage.success('到货入库申请已提交，等待仓库审核');
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>

<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="StockInForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="入库明细表" key="stockInDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="stockInDetail"
          :loading="stockInDetailTable.loading"
          :columns="stockInDetailTable.columns"
          :dataSource="stockInDetailTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          @edit-closed="handleValueChange"
        >
          <!-- 物料选择：输入框 + 搜索图标触发弹窗 -->
          <template #goodsCode="{ row }">
            <a-input
              :value="row.goodsCode"
              readonly
              :disabled="formDisabled"
              :placeholder="formDisabled ? '' : '点击选择物料'"
              :style="{ cursor: formDisabled ? 'default' : 'pointer' }"
              @click="openMaterialSelect(row)"
            >
              <template #suffix>
                <Icon
                  v-if="!formDisabled"
                  icon="ant-design:search-outlined"
                  style="color: #1890ff; font-size: 14px;"
                />
              </template>
            </a-input>
          </template>
        </JVxeTable>
      </a-tab-pane>
    </a-tabs>

    <!-- 物料选择弹窗（与配料模块共用） -->
    <MaterialSelectModal @register="registerMaterialModal" @select="handleMaterialSelect" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive} from 'vue';
  import {BasicModal, useModalInner, useModal} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import {formSchema, stockInDetailColumns} from '../StockIn.data';
  import {saveOrUpdate, stockInDetailList} from '../StockIn.api';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
  import MaterialSelectModal from '/@/components/MaterialSelect';
  import { Icon } from '/@/components/Icon';

  // Emits声明
  const emit = defineEmits(['register','success']);
  const isUpdate = ref(true);
  const formDisabled = ref(false);
  const refKeys = ref(['stockInDetail']);
  const activeKey = ref('stockInDetail');
  const stockInDetail = ref();
  const tableRefs = {stockInDetail};
  const stockInDetailTable = reactive({
    loading: false,
    dataSource: [],
    columns: stockInDetailColumns
  });

  // ===== 物料选择弹窗注册 =====
  const [registerMaterialModal, { openModal: openMaterialModal }] = useModal();
  const currentSelectRow = ref<any>(null);
  const currentSelectRowIndex = ref<number>(-1);

  //表单配置
  const [registerForm, {setProps, resetFields, setFieldsValue, validate}] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 6}
  });
  //表单赋值
  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    //重置表单
    await reset();
    setModalProps({confirmLoading: false, showCancelBtn: data?.showFooter, showOkBtn: data?.showFooter});
    isUpdate.value = !!data?.isUpdate;
    formDisabled.value = !data?.showFooter;
    if (unref(isUpdate)) {
      //表单赋值
      await setFieldsValue({
        ...data.record,
      });
      requestSubTableData(stockInDetailList, {id: data?.record?.id}, stockInDetailTable)
    }
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter })
  });
  //方法配置
  const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] = useJvxeMethod(
    requestAddOrEdit, classifyIntoFormData, tableRefs, activeKey, refKeys
  );

  //设置标题
  const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

  async function reset(){
    await resetFields();
    activeKey.value = 'stockInDetail';
    stockInDetailTable.dataSource = [];
  }

  // ==================== 物料选择逻辑（与配方/采购保持一致） ====================

  /** 打开物料选择弹窗 */
  function openMaterialSelect(row: any) {
    if (formDisabled.value) return;  // ← 详情模式禁用
    currentSelectRow.value = row;
    // 获取行索引，用于 setValues 的 rowKey（新增行 id 为空时最可靠）
    const tableInstance = stockInDetail.value;
    if (tableInstance) {
      const allData = tableInstance.getTableData ? tableInstance.getTableData() :
        (tableInstance.getData ? tableInstance.getData() : stockInDetailTable.dataSource);
      currentSelectRowIndex.value = allData.indexOf(row);
    }
    // ← 关键修复：与配方/采购保持一致，传入 {} 作为弹窗数据
    openMaterialModal(true, {});
  }

  /** 物料选择回调 — 回填当前行 */
  function handleMaterialSelect(record: any) {
    const row = currentSelectRow.value;
    if (!row) return;
    console.log('[StockInModal] handleMaterialSelect record:', record);

    // 1. 直接更新行对象（确保隐藏字段 goodsId 被赋值）
    row.goodsId   = record.id;
    row.goodsCode = record.material_code || record.code || record.materialCode;
    row.goodsName = record.material_name || record.name || record.materialName;
    row.goodsSpec = record.material_spec || record.spec || record.materialSpec;
    row.goodsType = record.material_type || record.type || record.materialType;

    // 2. 通过 setValues 刷新可见列（使用索引作为 rowKey）
    const tableInstance = stockInDetail.value;
    if (tableInstance && currentSelectRowIndex.value >= 0) {
      tableInstance.setValues([{
        rowKey: currentSelectRowIndex.value,
        values: {
          goodsCode: row.goodsCode,
          goodsName: row.goodsName,
          goodsSpec: row.goodsSpec,
          goodsType: row.goodsType,
        }
      }]);
    }

    // 3. 兜底：强制刷新 dataSource，确保 JVxeTable 内部状态同步
    if (currentSelectRowIndex.value >= 0) {
      stockInDetailTable.dataSource[currentSelectRowIndex.value] = { ...row };
      stockInDetailTable.dataSource = [...stockInDetailTable.dataSource];
    }

    currentSelectRow.value = null;
    currentSelectRowIndex.value = -1;
  }

  // ==================== 核心：实时计算逻辑 ====================

  /**
   * 处理编辑完成事件
   */
  function handleValueChange({ row, column, cellValue }: any) {
    const field = column.key || column.field;
    const tableInstance = stockInDetail.value;
    if (!tableInstance) return;

    const rowIndex = tableInstance.getRowIndex ? tableInstance.getRowIndex(row) : stockInDetailTable.dataSource.indexOf(row);
    const rowKey = row.id !== undefined ? row.id : rowIndex;

    const valuesToSet: any = {};

    // 1. 申请数量变化时，自动填充实收数量（始终同步，不管是否为空）
    if (field === 'applyQty') {
      const applyQty = parseFloat(row.applyQty);
      // 始终将申请数量同步到实收数量（如果需要用户手动修改，可以注释掉这行）
      valuesToSet.actualQty = applyQty;
      row.actualQty = applyQty;
    }

    // 2. 币种变化时，自动获取汇率
    if (field === 'currency') {
      const exchangeRate = getExchangeRateByCurrency(cellValue);
      valuesToSet.exchangeRate = exchangeRate;
      row.exchangeRate = exchangeRate;
    }

    if (['productionDate', 'shelfLife'].includes(field)) {
      const expiryDate = calculateExpiryDate(row);
      if (expiryDate) {
        valuesToSet.expiryDate = expiryDate;
        row.expiryDate = expiryDate;
      }
    }

    // 3. 重新计算金额（当 申请数量、实收数量、单价、汇率 任一变化时）
    if (['applyQty', 'actualQty', 'unitPrice', 'exchangeRate', 'currency'].includes(field)) {
      const totalAmount = calculateTotalAmount(row);
      valuesToSet.totalAmount = totalAmount;
      row.totalAmount = totalAmount;
    }

    // 4. 使用 setValues 批量更新单元格（关键！必须调用才能刷新视图）
    if (Object.keys(valuesToSet).length > 0) {
      console.log('设置值到行', rowKey, ':', valuesToSet);

      tableInstance.setValues([{
        rowKey: rowKey,
        values: valuesToSet
      }]);
    }
  }
  /**
   * 计算并设置行金额
   */
  function calculateTotalAmount(row: any) {
    // 实收数量优先，如果没有则使用申请数量
    const qty = row.actualQty !== undefined && row.actualQty !== null && row.actualQty !== ''
      ? parseFloat(row.actualQty)
      : parseFloat(row.applyQty || 0);

    const unitPrice = parseFloat(row.unitPrice || 0);
    const exchangeRate = parseFloat(row.exchangeRate || 1);
    console.log("qty:",qty)
    console.log("unitPrice:",unitPrice)
    console.log("exchangeRate:",exchangeRate)

    // 计算：数量 * 单价 * 汇率
    const totalAmount = qty * unitPrice * exchangeRate;
    console.log("totalAmount:",totalAmount)
    // 设置金额（保留2位小数）
    return isNaN(totalAmount) ? 0 : Math.round(totalAmount * 100) / 100;
  }
  // 自动填充实收数量
  function autoFillActualQty(row: any) {
    if (row.applyQty !== undefined && row.applyQty !== null && row.applyQty !== '' &&
      (row.actualQty === undefined || row.actualQty === null || row.actualQty === '')) {
      row.actualQty = parseFloat(row.applyQty);
    }
  }
  // 根据币种获取汇率
  function getExchangeRateByCurrency(currencyCode: string) {
    // 这里可以扩展为调用后端API获取实时汇率
    const rates: Record<string, number> = {
      'CNY': 1,      // 人民币
      'USD': 7.2,    // 美元
      'EUR': 7.8,    // 欧元
      'JPY': 0.06,   // 日元
      'GBP': 9.2,    // 英镑
      'KRW': 0.0055, // 韩元
    };
    return rates[currencyCode] || 1;
  }

  /**
   * 计算失效日期
   * 公式：生产日期 + 保质天数 = 失效日期
   */
  function calculateExpiryDate(row: any): string | null {
    // 获取生产日期
    const productionDate = row.productionDate;
    // 获取保质天数
    const shelfLife = parseInt(row.shelfLife);

    // 校验数据有效性
    if (!productionDate || isNaN(shelfLife) || shelfLife < 0) {
      return null;
    }

    // 解析生产日期
    const date = new Date(productionDate);
    if (isNaN(date.getTime())) {
      return null;
    }

    // 计算失效日期（生产日期 + 保质天数）
    date.setDate(date.getDate() + shelfLife);

    // 格式化为 YYYY-MM-DD 格式（与 JVxeTypes.date 格式一致）
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  function classifyIntoFormData(allValues: any) {
    let main = Object.assign({}, allValues.formValue)
    // 处理子表数据，确保金额已正确计算
    const detailList = allValues.tablesValue[0].tableData.map((item: any) => {
      // 重新计算每一行的金额
      item.totalAmount = calculateTotalAmount(item);
      return item;
    });
    return {
      ...main, // 展开
      stockInDetailList: detailList,
    }
  }
  //表单提交事件
  async function requestAddOrEdit(values: any) {
    try {
      setModalProps({confirmLoading: true});
      //提交表单
      await saveOrUpdate(values, isUpdate.value);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({confirmLoading: false});
    }
  }
</script>

<style lang="less" scoped>
  /** 时间和数字输入框样式 */
  :deep(.ant-input-number) {
    width: 100%;
  }

  :deep(.ant-calendar-picker) {
    width: 100%;
  }
</style>

<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1200" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="PurchaseOrderForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="采购明细" key="purchaseOrderDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="purchaseOrderDetail"
          :loading="purchaseOrderDetailTable.loading"
          :columns="purchaseOrderDetailTable.columns"
          :dataSource="purchaseOrderDetailTable.dataSource"
          :height="360"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          @valueChange="handleDetailValueChange"
        >
          <!-- 物料编码：自定义选择插槽 -->
          <template #goodsCode="{ row }">
            <a-input
              :value="row.goodsCode"
              readonly
              :disabled="formDisabled"
              :placeholder="formDisabled ? '' : '点击选择物料'"
              :style="{ cursor: formDisabled ? 'default' : 'pointer' }"
              @click="openMaterialSelect(row)"
            >
<!--              <template #suffix>-->
<!--                <SearchOutlined  style="color: #1890ff" />-->
<!--              </template>-->
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

    <!-- 物料选择弹窗 -->
    <MaterialSelectModal @register="registerMaterialModal" @select="handleMaterialSelect" />

  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive, nextTick} from 'vue';
  import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import {formSchema, purchaseOrderDetailColumns, calcDetailAmount, sumOrderAmounts} from '../PurchaseOrder.data';
  import {saveOrUpdate, purchaseOrderDetailList, getLatestRate} from '../PurchaseOrder.api';
  import dayjs from 'dayjs';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import MaterialSelectModal from '/@/components/MaterialSelect';

  const emit = defineEmits(['register','success']);
  const isUpdate = ref(true);
  const formDisabled = ref(false);
  const refKeys = ref(['purchaseOrderDetail', ]);
  const activeKey = ref('purchaseOrderDetail');
  const purchaseOrderDetail = ref();
  const tableRefs = {purchaseOrderDetail, };
  const purchaseOrderDetailTable = reactive({
    loading: false,
    dataSource: [],
    columns: purchaseOrderDetailColumns
  })
  //表单配置
  const [registerForm, {setProps, resetFields, setFieldsValue, validate}] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 8},
    onValuesChange: handleFormValuesChange,
  });
  //表单赋值
  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    await reset();
    setModalProps({confirmLoading: false, showCancelBtn: data?.showFooter, showOkBtn: data?.showFooter});
    isUpdate.value = !!data?.isUpdate;
    formDisabled.value = !data?.showFooter;
    if (unref(isUpdate)) {
      await setFieldsValue({ ...data.record });
      requestSubTableData(purchaseOrderDetailList, {id: data?.record?.id}, purchaseOrderDetailTable)
    } else {
      // 新增默认值
      await setFieldsValue({
        orderDate: dayjs().format('YYYY-MM-DD'),
        currencyCode: 'CNY',
        exchangeRate: 1,
      });
    }
    setProps({ disabled: !data?.showFooter })
  });
  //方法配置
  const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] = useJvxeMethod(requestAddOrEdit, classifyIntoFormData, tableRefs, activeKey, refKeys);

  const title = computed(() => (!unref(isUpdate) ? '新增采购订单' : !unref(formDisabled) ? '编辑采购订单' : '采购订单详情'));

  // ===== 物料选择弹窗 =====
  const [registerMaterialModal, { openModal: openMaterialModal }] = useModal();
  let currentEditRow: any = null;

  async function reset(){
    await resetFields();
    activeKey.value = 'purchaseOrderDetail';
    purchaseOrderDetailTable.dataSource = [];
  }

  /** 主表字段联动：币种变化 → 自动带最新汇率 */
  async function handleFormValuesChange(changedValues) {
    if (changedValues.currencyCode !== undefined) {
      const code = changedValues.currencyCode;
      if (!code) return;
      if (code === 'CNY') {
        setFieldsValue({ exchangeRate: 1 });
        return;
      }
      try {
        const rate = await getLatestRate({ currencyCode: code });
        setFieldsValue({ exchangeRate: rate ?? null }); // 查不到留空手填
      } catch (e) {
        // 汇率接口未就绪时忽略，手工填写
      }
    }
  }

  /** 子表值变化：数量/单价/税率 → 行金额 + 主表汇总 */
  function handleDetailValueChange({ column, row, value, target }) {
    if (!['orderQty', 'unitPrice', 'taxRate'].includes(column.key)) return;
    const merged = { ...row, [column.key]: value };
    const amounts = calcDetailAmount(merged);
    target.setValues([{ rowKey: row.id, values: amounts }]);
    nextTick(() => {
      const rows = purchaseOrderDetail.value?.getTableData?.() ?? [];
      setFieldsValue(sumOrderAmounts(rows));
    });
  }

  function classifyIntoFormData(allValues) {
    let main = Object.assign({}, allValues.formValue);
    // 提交前兜底重算，保证金额与行数据一致
    const detailList = (allValues.tablesValue[0].tableData || []).map(row => ({
      ...row,
      ...calcDetailAmount(row),
    }));
    return {
      ...main,
      ...sumOrderAmounts(detailList),
      purchaseOrderDetailList: detailList,
    };
  }
  //表单提交事件
  async function requestAddOrEdit(values) {
    try {
      setModalProps({confirmLoading: true});
      await saveOrUpdate(values, isUpdate.value);
      closeModal();
      emit('success');
    } finally {
      setModalProps({confirmLoading: false});
    }
  }



  function openMaterialSelect(row: any) {
    if (formDisabled.value) return;
    currentEditRow = row;
    openMaterialModal(true, {});
  }

  function handleMaterialSelect(record: any) {
    if (!currentEditRow) return;
    const rowKey = currentEditRow.id ?? currentEditRow._X_ROW_KEY;
    const values = {
      goodsId: record.id,
      goodsCode: record.materialCode,
      goodsName: record.materialName,
      goodsSpec: record.materialSpec,
      // ⚠️ 确认物料表返回的字段名是 materialType 还是 material_type
      goodsType: record.materialType || record.material_type,
    };
    purchaseOrderDetail.value?.setValues([{ rowKey, values }]);
    Object.assign(currentEditRow, values);
    currentEditRow = null;
  }

</script>

<style lang="less" scoped>
  :deep(.ant-input-number) {
    width: 100%;
  }
  :deep(.ant-calendar-picker) {
    width: 100%;
  }
</style>

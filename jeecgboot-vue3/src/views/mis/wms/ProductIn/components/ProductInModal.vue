<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="StockInForm"/>
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
          <template #goodsCode="{ row }">
            <a-input
              :value="row.goodsCode"
              readonly
              :disabled="formDisabled"
              placeholder="点击选择产品"
              style="cursor: pointer"
              @click="openProductSelect(row)"
            >
              <template #suffix>
                <Icon v-if="!formDisabled" icon="ant-design:search-outlined" style="color: #1890ff" />
              </template>
            </a-input>
          </template>
        </JVxeTable>
      </a-tab-pane>
    </a-tabs>

    <ProductSelectModal @register="registerProductModal" @select="handleProductSelect" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive, watch} from 'vue';
  import {BasicModal, useModalInner, useModal} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import {formSchema as originalFormSchema, stockInDetailColumns} from '../ProductIn.data';
  import {saveOrUpdate, stockInDetailList} from '../ProductIn.api';
  import {useMessage} from "/@/hooks/web/useMessage";
  import ProductSelectModal from '/@/components/ProductSelect'
  import { Icon } from '/@/components/Icon';

  const { createMessage } = useMessage();
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

  // ===== 产品选择弹窗 =====
  const [registerProductModal, { openModal: openProductModal }] = useModal();
  const currentSelectRow = ref<any>(null);
  const currentSelectRowIndex = ref<number>(-1);

  // 批次信息
  const batchInfo = reactive({
    productId: '',
    productCode: '',
    productName: '',
    productSpec: '',
    productColor: '',
    unit: '',
    batchNo: '',
    batchActualQty: null as number | null,
    batchRemainQty: null as number | null,
    batchInStockQty: null as number | null,
    productionBatchId: '',
  });

  // 包装 formSchema，拦截 JPopup 回写
  const formSchema = computed(() => {
    return originalFormSchema.map(item => {
      if (item.field === 'sourceOrderNo' && item.component === 'JPopup') {
        return {
          ...item,
          componentProps: ({ formActionType }) => {
            const {setFieldsValue, getFieldsValue} = formActionType;
            const wrappedSetFieldsValue = async (values) => {
              await setFieldsValue(values);
              if (values && (values.sourceOrderId || values.productId)) {
                setTimeout(() => {
                  const formData = getFieldsValue();
                  batchInfo.productId = formData.productId || '';
                  batchInfo.productCode = formData.productCode || '';
                  batchInfo.productName = formData.productName || '';
                  batchInfo.productSpec = formData.productSpec || '';
                  batchInfo.unit = formData.unit || '';
                  batchInfo.batchNo = formData.sourceOrderNo || '';
                  batchInfo.batchActualQty = formData.batchActualQty || null;
                  batchInfo.batchRemainQty = formData.batchRemainQty || null;
                  batchInfo.productionBatchId = formData.sourceOrderId || '';
                  autoFillDetailTable();
                }, 100);
              }
            };
            const originalItem = originalFormSchema.find(i => i.field === 'sourceOrderNo');
            const originalProps = typeof originalItem?.componentProps === 'function'
              ? originalItem.componentProps({ formActionType })
              : (originalItem?.componentProps || {});
            return {
              ...originalProps,
              setFieldsValue: wrappedSetFieldsValue,
            };
          }
        };
      }
      return item;
    });
  });

  // 表单配置（注意：使用 formSchema.value 解包 ComputedRef）
  const [registerForm, {setProps, resetFields, setFieldsValue, validate, getFieldsValue}] = useForm({
    schemas: formSchema.value,
    showActionButtonGroup: false,
    baseColProps: {span: 6},
  });

  // 表单赋值
  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    await reset();
    setModalProps({confirmLoading: false, showCancelBtn: data?.showFooter, showOkBtn: data?.showFooter});
    isUpdate.value = !!data?.isUpdate;
    formDisabled.value = !data?.showFooter;

    if (data?.fromBatch && data?.record) {
      const record = data.record;
      await setFieldsValue({ ...record });
      batchInfo.productId = record.productId || '';
      batchInfo.productCode = record.productCode || '';
      batchInfo.productName = record.productName || '';
      batchInfo.productSpec = record.productSpec || '';
      batchInfo.productColor = record.productColor || '';
      batchInfo.unit = record.unit || '';
      batchInfo.batchNo = record.sourceOrderNo || '';
      batchInfo.batchActualQty = record.batchActualQty || null;
      batchInfo.batchRemainQty = record.batchRemainQty || null;
      batchInfo.batchInStockQty = record.batchInStockQty || null;
      batchInfo.productionBatchId = record.sourceOrderId || '';
      autoFillDetailTable();
    } else if (unref(isUpdate)) {
      await setFieldsValue({ ...data.record });
      requestSubTableData(stockInDetailList, {id: data?.record?.id}, stockInDetailTable)
    } else {
      await setFieldsValue({
        stockInType: 'PRODUCTION',
        isProduct: '1',
      });
    }
    setProps({ disabled: !data?.showFooter })
  });

  // 监听主表字段变化
  watch(() => getFieldsValue(), (newVal, oldVal) => {
    if (newVal?.sourceOrderId && newVal.sourceOrderId !== oldVal?.sourceOrderId) {
      batchInfo.productId = newVal.productId || '';
      batchInfo.productCode = newVal.productCode || '';
      batchInfo.productName = newVal.productName || '';
      batchInfo.productSpec = newVal.productSpec || '';
      batchInfo.productColor = newVal.productColor || '';
      batchInfo.unit = newVal.unit || '';
      batchInfo.batchNo = newVal.sourceOrderNo || '';
      batchInfo.batchActualQty = newVal.batchActualQty || null;
      batchInfo.batchRemainQty = newVal.batchRemainQty || null;
      batchInfo.batchInStockQty = newVal.batchInStockQty || null;
      batchInfo.productionBatchId = newVal.sourceOrderId || '';
      autoFillDetailTable();
    }
    if (newVal?.stockInType !== oldVal?.stockInType && newVal?.stockInType !== 'PRODUCTION') {
      clearBatchInfo();
    }
  }, { deep: true });

  function autoFillDetailTable() {
    const tableInstance = stockInDetail.value;
    if (!tableInstance) return;
    tableInstance.removeRows();
    const remainQty = batchInfo.batchRemainQty || 0;
    const instockQty = batchInfo.batchInStockQty || 0;
    const actualQty = batchInfo.batchActualQty || 0;
    if (remainQty <= 0) {
      createMessage.warning('该批次已入库完毕（已入' + instockQty + '，计划/实际' + actualQty + '）');
    }
    const newRow = {
      goodsId: batchInfo.productId,
      goodsCode: batchInfo.productCode,
      goodsName: batchInfo.productName,
      goodsSpec: batchInfo.productSpec,
      goodsColor: batchInfo.productColor,
      unit: batchInfo.unit,
      goodsType: 'PRODUCT',
      applyQty: remainQty,
      actualQty: remainQty,
      batchNo: batchInfo.batchNo,
      productionBatchId: batchInfo.productionBatchId,
      _batchRemainQty: remainQty,
      _batchInStockQty: instockQty,
      _batchActualQty: actualQty,
      productionDate: formatDate(new Date()),
      shelfLife: 365,
      qcStatus: 'WAIT_CHECK',
    };
    newRow.expiryDate = calculateExpiryDate(newRow);
    tableInstance.pushRows([newRow]);
  }

  function formatDate(date: Date | string | number): string {
    if (!date) return '';
    const d = new Date(date);
    if (isNaN(d.getTime())) return '';
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
  }

  function clearBatchInfo() {
    Object.keys(batchInfo).forEach(k => batchInfo[k] = k.includes('Qty') ? null : '');
    stockInDetailTable.dataSource = [];
  }

  const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] = useJvxeMethod(
    requestAddOrEdit, classifyIntoFormData, tableRefs, activeKey, refKeys
  );

  const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

  async function reset(){
    await resetFields();
    activeKey.value = 'stockInDetail';
    stockInDetailTable.dataSource = [];
  }

  // ==================== 产品选择逻辑 ====================

  function openProductSelect(row: any) {
    if (formDisabled.value) return;
    currentSelectRow.value = row;
    const tableInstance = stockInDetail.value;
    if (tableInstance) {
      const allData = tableInstance.getTableData ? tableInstance.getTableData() :
        (tableInstance.getData ? tableInstance.getData() : stockInDetailTable.dataSource);
      currentSelectRowIndex.value = allData.indexOf(row);
    }
    openProductModal(true, {});
  }

  function handleProductSelect(record: any) {
    const row = currentSelectRow.value;
    if (!row) return;
    row.goodsId = record.id;
    row.goodsCode = record.productCode;
    row.goodsName = record.productName;
    row.goodsSpec = record.productSpec;
    row.goodsColor = record.productColor;
    const tableInstance = stockInDetail.value;
    if (tableInstance && currentSelectRowIndex.value >= 0) {
      tableInstance.setValues([{
        rowKey: currentSelectRowIndex.value,
        values: {
          goodsCode: row.goodsCode,
          goodsName: row.goodsName,
          goodsSpec: row.goodsSpec,
        }
      }]);
    }
    if (currentSelectRowIndex.value >= 0) {
      stockInDetailTable.dataSource[currentSelectRowIndex.value] = { ...row };
      stockInDetailTable.dataSource = [...stockInDetailTable.dataSource];
    }
    currentSelectRow.value = null;
    currentSelectRowIndex.value = -1;
  }

  // ==================== 实时计算逻辑 ====================

  function handleValueChange({ row, column, cellValue }: any) {
    const field = column.key || column.field;
    const tableInstance = stockInDetail.value;
    if (!tableInstance) return;
    const rowIndex = tableInstance.getRowIndex ? tableInstance.getRowIndex(row) : stockInDetailTable.dataSource.indexOf(row);
    const rowKey = row.id !== undefined ? row.id : rowIndex;
    const valuesToSet: any = {};
    if (field === 'applyQty') {
      const applyQty = parseFloat(row.applyQty);
      valuesToSet.actualQty = applyQty;
      row.actualQty = applyQty;
    }
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
    if (['applyQty', 'actualQty', 'unitPrice', 'exchangeRate', 'currency'].includes(field)) {
      const totalAmount = calculateTotalAmount(row);
      valuesToSet.totalAmount = totalAmount;
      row.totalAmount = totalAmount;
    }
    if (Object.keys(valuesToSet).length > 0) {
      tableInstance.setValues([{ rowKey, values: valuesToSet }]);
    }
  }

  function calculateTotalAmount(row: any) {
    const qty = row.actualQty !== undefined && row.actualQty !== null && row.actualQty !== ''
      ? parseFloat(row.actualQty)
      : parseFloat(row.applyQty || 0);
    const unitPrice = parseFloat(row.unitPrice || 0);
    const exchangeRate = parseFloat(row.exchangeRate || 1);
    const totalAmount = qty * unitPrice * exchangeRate;
    return isNaN(totalAmount) ? 0 : Math.round(totalAmount * 100) / 100;
  }

  function getExchangeRateByCurrency(currencyCode: string) {
    const rates: Record<string, number> = {
      'CNY': 1, 'USD': 7.2, 'EUR': 7.8, 'JPY': 0.06, 'GBP': 9.2, 'KRW': 0.0055,
    };
    return rates[currencyCode] || 1;
  }

  function calculateExpiryDate(row: any): string | null {
    const productionDate = row.productionDate;
    const shelfLife = parseInt(row.shelfLife);
    if (!productionDate || isNaN(shelfLife) || shelfLife < 0) return null;
    const date = new Date(productionDate);
    if (isNaN(date.getTime())) return null;
    date.setDate(date.getDate() + shelfLife);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
  }

  function classifyIntoFormData(allValues: any) {
    let main = Object.assign({}, allValues.formValue);
    const detailList = allValues.tablesValue[0].tableData.map((item: any) => {
      item.totalAmount = calculateTotalAmount(item);
      return item;
    });
    return { ...main, stockInDetailList: detailList };
  }

  async function requestAddOrEdit(values: any) {
    try {
      setModalProps({confirmLoading: true});
      await saveOrUpdate(values, isUpdate.value);
      closeModal();
      emit('success');
    } finally {
      setModalProps({confirmLoading: false});
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-input-number) { width: 100%; }
  :deep(.ant-calendar-picker) { width: 100%; }
</style>

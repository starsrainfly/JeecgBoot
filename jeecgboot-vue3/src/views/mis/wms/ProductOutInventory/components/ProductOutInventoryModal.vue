<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="StockOutForm"/>
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="出库明细表" key="stockOutDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="stockOutDetail"
          :loading="stockOutDetailTable.loading"
          :columns="stockOutDetailTable.columns"
          :dataSource="stockOutDetailTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
        />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref, reactive, watch } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import { formSchema as originalFormSchema, stockOutDetailColumns } from '../ProductOutInventory.data';
  import { saveOrUpdate, stockOutDetailList } from '../ProductOutInventory.api';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
  import { useMessage } from "@/hooks/web/useMessage";

  const { createMessage } = useMessage();
  const emit = defineEmits(['register', 'success']);

  const isUpdate = ref(true);
  const formDisabled = ref(false);
  const refKeys = ref(['stockOutDetail']);
  const activeKey = ref('stockOutDetail');
  const stockOutDetail = ref();
  const tableRefs = { stockOutDetail };
  const stockOutDetailTable = reactive({
    loading: false,
    dataSource: [],
    columns: stockOutDetailColumns
  });

  // 存储订单信息用于子表填充
  const orderInfo = reactive({
    detailIds: [],
    productIds: [],
    productCodes: [],
    productNames: [],
    productSpecs: [],
    productColors: [],
    units: [],
    remainQtys: [],
    unitPrices: [],
    packageIds: [],
    packageNames: [],
    remarks: [],
  });

  // 包装 JPopup 的 setFieldsValue 以捕获订单选择
  const formSchema = computed(() => {
    return originalFormSchema.map(item => {
      if (item.field === 'sourceOrderCode' && item.component === 'JPopup') {
        return {
          ...item,
          componentProps: ({ formActionType }) => {
            const { setFieldsValue, getFieldsValue } = formActionType;

            const wrappedSetFieldsValue = async (values) => {
              await setFieldsValue(values);
              console.log('=== JPopup 回写字段 ===', values);

              if (values && (values.sourceOrderId || values._detailIds)) {
                setTimeout(() => {
                  const formData = getFieldsValue();
                  console.log('=== 回写后完整表单 ===', formData);
                  handleOrderSelect(formData);
                }, 100);
              }
            };

            const originalProps = typeof originalFormSchema.find(i => i.field === 'sourceOrderCode')?.componentProps === 'function'
              ? originalFormSchema.find(i => i.field === 'sourceOrderCode').componentProps({ formActionType })
              : {};

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

  const [registerForm, { setProps, resetFields, setFieldsValue, validate, getFieldsValue }] = useForm({
    schemas: formSchema.value,
    showActionButtonGroup: false,
    baseColProps: { span: 6 },
    labelWidth:100,
  });

  // 监听表单变化（备用，如果 JPopup 包装不触发）
  watch(() => getFieldsValue(), (newVal, oldVal) => {
    console.log("watch newVal:", newVal);
    if (newVal?.sourceOrderId && newVal.sourceOrderId !== oldVal?.sourceOrderId) {
      handleOrderSelect(newVal);
    }
  }, { deep: true });

  // 处理订单选择
  async function handleOrderSelect(formData) {
    console.log('=== handleOrderSelect ===', formData);

    if (!formData?._detailIds) {
      console.log('没有 _detailIds');
      return;
    }

    const customerIds = splitAndUnique(formData.customerId);
    const customerNames = splitAndUnique(formData.customerName);
    const customerCodes = splitAndUnique(formData.customerCode || '');
    const companyIds = splitAndUnique(formData.companyId);
    const companyNames = splitAndUnique(formData.companyName || '');
    const companyCodes = splitAndUnique(formData.companyCode || '');
    const salesmanIds = splitAndUnique(formData.salesmanId || '');
    const salesmanNames = splitAndUnique(formData.salesmanName || '');
    const orderNos = splitAndUnique(formData.sourceOrderCode);

    const consignees = safeSplit(formData.consignee ||'');
    const consigneePhones = safeSplit(formData.consigneePhone ||'');
    const deliverAddresses = safeSplit(formData.deliverAddress ||'');

    if (customerIds.length > 1) {
      createMessage.error('只能选择同一客户的订单');
      clearOrderSelection();
      return;
    }
    if (companyIds.length > 1) {
      createMessage.error('只能选择同一公司的订单');
      clearOrderSelection();
      return;
    }

    const detailIds = safeSplit(formData._detailIds);
    const productIds = safeSplit(formData._productIds);
    const productCodes = safeSplit(formData._productCodes);
    const productNames = safeSplit(formData._productNames);
    const productSpecs = safeSplit(formData._productSpecs);
    const productColors = safeSplit(formData._productColors || '');
    const units = safeSplit(formData._units);
    const remainQtys = safeSplit(formData._remainQtys).map(v => parseFloat(v) || 0);
    const unitPrices = safeSplit(formData._unitPrices).map(v => parseFloat(v) || 0);
    const packageIds = safeSplit(formData._packageIds || '');
    const packageNames = safeSplit(formData._packageNames || '');
    const remarks = safeSplit(formData._remarks || '');


    const len = detailIds.length;
    if (len === 0) {
      createMessage.warning('没有可出库的明细');
      return;
    }
    if (productIds.length !== len || productCodes.length !== len || remainQtys.length !== len) {
      createMessage.error('订单明细数据不完整');
      return;
    }

    await setFieldsValue({
      customerId: customerIds[0],
      customerName: customerNames[0],
      customerCode: customerCodes[0] || '',
      companyId: companyIds[0],
      companyName: companyNames[0] || '',
      companyCode: companyCodes[0] || '',
      salesmanId: salesmanIds[0] || '',
      salesmanName: salesmanNames[0] || '',
      sourceOrderCode: orderNos[0] || '',
      consignee: consignees[0] || '',       // 如果有
      consigneePhone: consigneePhones[0] || '',
      deliverAddress: deliverAddresses[0] || '',
    });

    const rows = [];
    for (let i = 0; i < len; i++) {
      if (remainQtys[i] <= 0) continue;
      rows.push({
        goodsId: productIds[i],
        goodsType: 'PRODUCT',
        goodsCode: productCodes[i],
        goodsName: productNames[i],
        goodsSpec: productSpecs[i],
        goodsColor: productColors[i] || '',
        unit: units[i] || 'kg',
        applyQty: remainQtys[i],
        actualQty: null,
        salesPrice: unitPrices[i],
        salesTotal: remainQtys[i] * unitPrices[i],
        sourceDetailId: detailIds[i],
        batchNo: null,
        packageId: packageIds[i] || '',
        packageName: packageNames[i] || '',
        remark: remarks[i] || '',
      });
    }

    const tableInstance = stockOutDetail.value;
    if (tableInstance?.removeRows && tableInstance?.pushRows) {
      tableInstance.removeRows();
      tableInstance.pushRows(rows);
    } else {
      stockOutDetailTable.dataSource = rows;
    }

    if (rows.length > 0) {
      createMessage.success(`已加载 ${rows.length} 条明细，来自 ${orderNos.length} 个订单`);
    }
  }

  const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] = useJvxeMethod(
    requestAddOrEdit, classifyIntoFormData, tableRefs, activeKey, refKeys
  );

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await reset();
    setModalProps({ confirmLoading: false, showCancelBtn: data?.showFooter, showOkBtn: data?.showFooter });
    isUpdate.value = !!data?.isUpdate;
    formDisabled.value = !data?.showFooter;

    if (unref(isUpdate)) {
      await setFieldsValue({ ...data.record });
      requestSubTableData(stockOutDetailList, { id: data?.record?.id }, stockOutDetailTable);

      if (data.record?.sourceOrderId) {
        setTimeout(() => {
          const values = getFieldsValue();
          if (values?.sourceOrderId) handleOrderSelect(values);
        }, 300);
      }
    }

    setProps({ disabled: !data?.showFooter });
  });

  const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

  async function reset() {
    await resetFields();
    activeKey.value = 'stockOutDetail';
    stockOutDetailTable.dataSource = [];
  }

  function classifyIntoFormData(allValues) {
    let main = Object.assign({}, allValues.formValue);
    return {
      ...main,
      stockOutDetailList: allValues.tablesValue[0].tableData,
    };
  }

  async function requestAddOrEdit(values) {
    try {
      setModalProps({ confirmLoading: true });
      await saveOrUpdate(values, isUpdate.value);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }

  function splitAndUnique(val) {
    if (!val || typeof val !== 'string') return [];
    return [...new Set(val.split(',').map(s => s.trim()).filter(s => s !== ''))];
  }

  function safeSplit(val) {
    if (!val || typeof val !== 'string') return [];
    return val.split(',').map(s => s.trim()).filter(s => s !== '');
  }

  function clearOrderSelection() {
    setFieldsValue({
      // 订单相关
      sourceOrderId: '',
      sourceOrderCode: '',

      // 客户相关
      customerId: '',
      customerName: '',
      customerCode: '',

      // 公司相关
      companyId: '',
      companyName: '',
      companyCode: '',

      // 业务员相关
      salesmanId: '',
      salesmanName: '',

      // 收货信息
      consignee: '',
      consigneePhone: '',
      deliverAddress: '',

      // 明细隐藏字段
      _detailIds: '',
      _productIds: '',
      _productCodes: '',
      _productNames: '',
      _productSpecs: '',
      _productColors: '',
      _units: '',
      _remainQtys: '',
      _unitPrices: '',
      _packageIds: '',
      _packageNames: '',
      _remarks: '',
    });
    stockOutDetailTable.dataSource = [];
  }
</script>

<style lang="less" scoped>
  :deep(.ant-input-number) { width: 100%; }
  :deep(.ant-calendar-picker) { width: 100%; }
</style>

<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1200" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="ReceiptOrderForm"/>

    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="收款明细" key="receiptOrderDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="receiptOrderDetail"
          :loading="receiptOrderDetailTable.loading"
          :columns="receiptOrderDetailTable.columns"
          :dataSource="receiptOrderDetailTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
        >
          <template #planSelectSlot="{ row, rowIndex }">
            <a-input-group compact class="plan-select-group">
              <a-input v-model:value="row.planNo" placeholder="请选择计划" readonly class="plan-input" />
              <a-button type="primary"  class="plan-btn" @click="handleOpenPlanSelect(row, rowIndex)" :disabled="formDisabled">
                <Icon icon="ant-design:search-outlined" />
              </a-button>
            </a-input-group>
          </template>
        </JVxeTable>
      </a-tab-pane>
    </a-tabs>

    <!-- 关键：和 PriceOfferModal 完全一致 -->
    <PlanSelectModal @register="registerPlanModal" @success="onPlanSelected" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive} from 'vue';
  import {BasicModal, useModalInner, useModal} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import {formSchema, receiptOrderDetailColumns} from '../ReceiptOrder.data';
  import {saveOrUpdate, receiptOrderDetailList} from '../ReceiptOrder.api';
  import PlanSelectModal from './PlanSelectModal.vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { Icon } from '/@/components/Icon';

  const emit = defineEmits(['register','success']);
  const { createMessage } = useMessage();

  const mainCustomerId = ref('');
  const currentSelectRow = ref<any>(null);
  const currentRowIndex = ref<number>(-1);

  const isUpdate = ref(true);
  const formDisabled = ref(false);
  const refKeys = ref(['receiptOrderDetail']);
  const activeKey = ref('receiptOrderDetail');
  const receiptOrderDetail = ref();
  const tableRefs = {receiptOrderDetail};
  const receiptOrderDetailTable = reactive({
    loading: false,
    dataSource: [],
    columns: receiptOrderDetailColumns
  })

  // 关键：和 PriceOfferModal 完全一致
  const [registerPlanModal, { openModal: openPlanModalBase }] = useModal();

  const [registerForm, {setProps, resetFields, setFieldsValue, validate, getFieldsValue}] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 8},
    labelWidth:120,
    onValuesChange: (changedValues) => {
      if (changedValues.customerId !== undefined) {
        mainCustomerId.value = changedValues.customerId || '';
      }
    },
  });

  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    await reset();
    setModalProps({confirmLoading: false, showCancelBtn: data?.showFooter, showOkBtn: data?.showFooter});
    isUpdate.value = !!data?.isUpdate;
    formDisabled.value = !data?.showFooter;

    if (unref(isUpdate)) {
      await setFieldsValue({...data.record});
      mainCustomerId.value = data.record?.customerId || '';
      requestSubTableData(receiptOrderDetailList, {id: data?.record?.id}, receiptOrderDetailTable)
    }
    setProps({ disabled: !data?.showFooter })
  });

  const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] = useJvxeMethod(requestAddOrEdit, classifyIntoFormData, tableRefs, activeKey, refKeys);

  const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

  async function reset(){
    await resetFields();
    activeKey.value = 'receiptOrderDetail';
    receiptOrderDetailTable.dataSource = [];
    mainCustomerId.value = '';
    currentSelectRow.value = null;
    currentRowIndex.value = -1;
  }

  function classifyIntoFormData(allValues) {
    let main = Object.assign({}, allValues.formValue)
    return {
      ...main,
      receiptOrderDetailList: allValues.tablesValue[0].tableData,
    }
  }

  // 关键：和 openPriceModal 完全一致
  const handleOpenPlanSelect = (row, index) => {
    const formData = formRef.value?.getFieldsValue?.() || {};
    if (!formData.customerId) {
      createMessage.warning('请先选择客户');
      return;
    }
    mainCustomerId.value = formData.customerId;
    currentSelectRow.value = row;
    currentRowIndex.value = index;

    const usedPlanIds = receiptOrderDetailTable.dataSource
      .filter((item, idx) => idx !== index && item.planId)
      .map(item => item.planId);

    // 关键：和 openPriceModalBase 完全一致
    openPlanModalBase(true, {
      customerId: formData.customerId,
      excludePlanIds: usedPlanIds,
    });
  };

  const onPlanSelected = (record) => {
    if (!currentSelectRow.value || !record) return;
    const row = currentSelectRow.value;
    row.planId = record.id;
    row.planNo = record.planNo;
    row.planName = record.planName;
    row.salesOrderId = record.salesOrderId;
    row.salesOrderNo = record.salesOrderNo;
    row.planAmount = record.planAmount;
    row.alreadyReceipt = record.paidAmount || 0;
    row.receiptAmount = record.unpaidAmount || 0;

    const dataSource = [...receiptOrderDetailTable.dataSource];
    dataSource[currentRowIndex.value] = { ...row };
    receiptOrderDetailTable.dataSource = dataSource;
  };

  async function requestAddOrEdit(values) {
    try {
      setModalProps({confirmLoading: true});
      const details = values.receiptOrderDetailList || [];
      if (details.length === 0) {
        createMessage.warning('请至少添加一条收款明细');
        throw new Error('明细不能为空');
      }
      for (const row of details) {
        if (!row.planId) {
          createMessage.warning('请选择收款计划');
          throw new Error('计划未选择');
        }
        if (!row.receiptAmount || row.receiptAmount <= 0) {
          createMessage.warning('请输入有效的收款金额');
          throw new Error('金额无效');
        }
        const maxAmount = (row.planAmount || 0) - (row.alreadyReceipt || 0);
        if (row.receiptAmount > maxAmount) {
          createMessage.warning(`计划 ${row.planNo} 收款金额不能超过 ${maxAmount}`);
          throw new Error('金额超限');
        }
      }
      await saveOrUpdate(values, isUpdate.value);
      closeModal();
      emit('success');
    } catch (e) {
      console.error(e);
      throw e;
    } finally {
      setModalProps({confirmLoading: false});
    }
  }
</script>

<style lang="less" scoped>
  .plan-select-group {
    display: flex;
    width: 100%;
    .plan-input { flex: 1; }
    .plan-btn { width: 32px; padding: 0; }
  }
  :deep(.ant-input-number) { width: 100%; }
  :deep(.ant-calendar-picker) { width: 100%; }

  /* 强制压缩所有表单项的底部间距 */
  :deep(.ant-form-item) {
    margin-bottom: 4px !important;
  }
  /* 压缩 Divider 的上下间距 */
  :deep(.ant-divider-horizontal) {
    margin-top: 4px !important;
    margin-bottom: 4px !important;
  }
  /* 备注的 textarea 也压缩 */
  :deep(.ant-form-item-control-input) {
    min-height: auto !important;
  }

  /* 针对备注字段的表单项 */
  :deep(.remark-field) .ant-form-item-label {
    width: 25% !important;
    text-align: right !important;
  }
  :deep(.remark-field) .ant-form-item-control {
    width: 75% !important;
  }
</style>

<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="SalesOrderForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="销售订单明细表" key="salesOrderLine" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="salesOrderLine"
          :loading="salesOrderLineTable.loading"
          :columns="salesOrderLineTable.columns"
          :dataSource="salesOrderLineTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
        >
          <!-- 插槽名对应 slotName -->

          <template #productCodeSlot="{ row, rowIndex }">
            <a-input-group compact>
              <a-input
                v-model:value="row.productCode"
                style="width: 65%"
                placeholder="选择报价产品"
                readonly
              />
              <a-button type="primary" @click="openPriceModal(row, rowIndex)">选择</a-button>
            </a-input-group>
          </template>
        </JVxeTable>
      </a-tab-pane>
    </a-tabs>
    <!-- 报价选择弹窗 -->
    <PriceOfferModal @register="registerPriceModal" @success="onPriceSelected" />
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref,reactive} from 'vue';
    import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import { JVxeTable } from '/@/components/jeecg/JVxeTable'
    import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
    import {formSchema, salesOrderLineColumns, setPriceSelectModalOpener} from '../SalesOrder.data';
    import {saveOrUpdate,salesOrderLineList} from '../SalesOrder.api';
    import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
    import JPopup from '/@/components/Form/src/jeecg/components/JPopup.vue';
    import PriceOfferModal from './PriceOfferModal.vue';
    import { useMessage } from '/@/hooks/web/useMessage';
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const formDisabled = ref(false);
    const refKeys = ref(['salesOrderLine', ]);
    const activeKey = ref('salesOrderLine');
    const salesOrderLine = ref();
    const tableRefs = {salesOrderLine, };
    const salesOrderLineTable = reactive({
          loading: false,
          dataSource: [],
          columns:salesOrderLineColumns
    })
    const { createMessage } = useMessage();

    // 报价弹窗
    const [registerPriceModal, { openModal: openPriceModalBase }] = useModal();
    const currentSelectRow = ref(null);
    const currentSelectIndex = ref(-1);

    const openPriceModal = (row, index) => {
      const formData = formRef.value?.getFieldsValue?.() || {};
      if (!formData.customerId) {
        createMessage.warning('请先选择客户');
        return;
      }

      currentSelectRow.value = row;
      currentSelectIndex.value = index;

      openPriceModalBase(true, {
        customerId: formData.customerId,
        customerCode: formData.customerCode || '',  // 确保传客户编码
        customerName: formData.customerName || '',   // 确保传客户名称
        salesmanId: formData.salesmanId || '',
      });
    };

    const onPriceSelected = (record) => {
      if (!currentSelectRow.value || !record) return;

      const row = currentSelectRow.value;
      row.offerDetailId = record.offerDetailId;
      row.productCode = record.productCode;
      row.productName = record.productName;
      row.customProductCode = record.customProductCode;
      row.customProductName = record.customProductName;
      row.customProductSpec = record.customProductSpec;
      row.packageId = record.packageId;
      row.packageName = record.packageName;
      row.packageSpec = record.packageSpec;
      row.packageCapacity = record.packageCapacity;
      row.priceType = record.priceType;
      row.unit = record.unit;
      row.qtyMin = record.qtyMin;
      row.qtyMax = record.qtyMax;
      row.unitPrice = Number(record.unitPrice);
      row.taxRate = record.taxRate;
      row.orderQty = record.minOrderQty;
      row.qtyStep = record.qtyStep;
      row.effectiveDate = record.effectiveDate;
      row.expiryDate = record.expiryDate;

      // 强制刷新
      const dataSource = salesOrderLineTable.dataSource;
      dataSource[currentSelectIndex.value] = { ...row };
      salesOrderLineTable.dataSource = [...dataSource];

      createMessage.success('已选择报价产品');
    };

    //表单配置
    const [registerForm, {setProps,resetFields, setFieldsValue, validate}] = useForm({
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 6}
    });
     //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //重置表单
        await reset();
        setModalProps({confirmLoading: false,showCancelBtn:data?.showFooter,showOkBtn:data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        formDisabled.value = !data?.showFooter;
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
             requestSubTableData(salesOrderLineList, {id:data?.record?.id}, salesOrderLineTable)
        }
        // 隐藏底部时禁用整个表单
       setProps({ disabled: !data?.showFooter })

    });
    //方法配置
    const formRef = ref()
    const [handleChangeTabs,handleSubmit,requestSubTableData] = useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys);



    //设置标题
    const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

    async function reset(){
      await resetFields();
      activeKey.value = 'salesOrderLine';
      salesOrderLineTable.dataSource = [];
    }
    function classifyIntoFormData(allValues) {
         let main = Object.assign({}, allValues.formValue)
         return {
           ...main, // 展开
           salesOrderLineList: allValues.tablesValue[0].tableData,
         }
       }
    //表单提交事件
    async function requestAddOrEdit(values) {
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

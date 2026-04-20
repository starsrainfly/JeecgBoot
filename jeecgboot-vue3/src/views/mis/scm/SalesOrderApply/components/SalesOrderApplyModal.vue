<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="SalesOrderForm" />
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="销售订单明细表" key="salesOrderDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="salesOrderDetail"
          :loading="salesOrderDetailTable.loading"
          :columns="salesOrderDetailTable.columns"
          :dataSource="salesOrderDetailTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          @edit-closed="handleCellValueChange"
          >
          <!-- 插槽名对应 slotName productCode 报价单弹窗-->
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
    import {ref, computed, unref,reactive, nextTick} from 'vue';
    import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import { JVxeTable } from '/@/components/jeecg/JVxeTable'
    import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
    import {formSchema,salesOrderDetailColumns} from '../SalesOrderApply.data';
    import {saveOrUpdate,salesOrderDetailList} from '../SalesOrderApply.api';
    import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
    import {useMessage} from "@/hooks/web/useMessage";
    import PriceOfferModal from './PriceOfferModal.vue';
    import dayjs from "dayjs";
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const formDisabled = ref(false);
    const refKeys = ref(['salesOrderDetail', ]);
    const activeKey = ref('salesOrderDetail');
    const salesOrderDetail = ref();
    const tableRefs = {salesOrderDetail, };
    const salesOrderDetailTable = reactive({
          loading: false,
          dataSource: [],
          columns:salesOrderDetailColumns
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
      row.offerId = record.offerId;
      row.offerDetailId = record.offerDetailId;
      row.offerNo = record.offerNo;
      row.productId = record.productId;
      row.productCode = record.productCode;
      row.productName = record.productName;
      row.customProductCode = record.customProductCode;
      row.customProductName = record.customProductName;
      row.customProductSpec = record.customProductSpec;
      row.packageId = record.packageId;
      row.packageName = record.packageName;
      row.packageSpec = record.packageSpec;
      row.packageCapacity = record.packageCapacity;
      row.packageCapacityUnit = record.packageCapacityUnit;
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

      // 选择报价单后立即计算金额
      const qty = Number(row.orderQty) || 0;
      const unitPrice = Number(row.unitPrice) || 0;
      const taxRate = Number(row.taxRate) || 0;

      const detailAmount = Math.round(qty * unitPrice * 100) / 100;
      const netAmount = Math.round(detailAmount / (1 + taxRate / 100) * 100) / 100;
      const taxAmount = Math.round((detailAmount - netAmount) * 100) / 100;

      row.detailAmount = detailAmount;
      row.netAmount = netAmount;
      row.taxAmount = taxAmount;

      // 强制刷新
      const dataSource = salesOrderDetailTable.dataSource;
      dataSource[currentSelectIndex.value] = { ...row };
      salesOrderDetailTable.dataSource = [...dataSource];

      // 触发计算
      calcOrderTotal();

      createMessage.success('已选择报价产品');
    };

    //表单配置
    const [registerForm, {setProps,resetFields, setFieldsValue, validate,getFieldsValue}] = useForm({
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 6},
        labelWidth:120,

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
             requestSubTableData(salesOrderDetailList, {id:data?.record?.id}, salesOrderDetailTable)
        }
        else{
          // 新增：设置默认日期
          const today = dayjs().format('YYYY-MM-DD');
          await setFieldsValue({
            orderDate: today,
            deliveryDate: dayjs().add(3, 'day').format('YYYY-MM-DD')
          });
        }
        // 隐藏底部时禁用整个表单
       setProps({ disabled: !data?.showFooter })
    });
    //方法配置
    const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] = useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys);



    //设置标题
    const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

    async function reset(){
      await resetFields();
      activeKey.value = 'salesOrderDetail';
      salesOrderDetailTable.dataSource = [];
    }
    function classifyIntoFormData(allValues) {
         let main = Object.assign({}, allValues.formValue)
         return {
           ...main, // 展开
           salesOrderDetailList: allValues.tablesValue[0].tableData,
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

    // 子表单元格值变化事件 - 自动计算
    const handleCellValueChange = ({ row, column, rowIndex,$table }) => {
      const field = column.key || column.property;

      if (['orderQty', 'unitPrice', 'taxRate'].includes(field)) {
        const qty = Number(row.orderQty) || 0;
        const unitPrice = Number(row.unitPrice) || 0;
        const taxRate = Number(row.taxRate) || 0;

        const detailAmount = Math.round(qty * unitPrice * 100) / 100;
        const netAmount = Math.round(detailAmount / (1 + taxRate / 100) * 100) / 100;
        const taxAmount = Math.round((detailAmount - netAmount) * 100) / 100;

        row.detailAmount = detailAmount;
        row.netAmount = netAmount;
        row.taxAmount = taxAmount;

        // 安全地刷新数据源
        try {
          if (rowIndex !== undefined && rowIndex >= 0) {
            const dataSource = [...salesOrderDetailTable.dataSource];
            dataSource[rowIndex] = { ...row };
            salesOrderDetailTable.dataSource = dataSource;
          }
        } catch (e) {
          // 忽略 JVxeTable 内部渲染错误
          console.warn('JVxeTable refresh warning:', e);
        }

        calcOrderTotal();
      }
    };

    // 计算订单汇总
    // const calcOrderTotal = () => {
    //   const details = salesOrderDetailTable.dataSource || [];
    //
    //   const orderTotal = details.reduce((sum, item) => sum + (Number(item.detailAmount) || 0), 0);
    //   const orderNet = details.reduce((sum, item) => sum + (Number(item.netAmount) || 0), 0);
    //   const orderTax = details.reduce((sum, item) => sum + (Number(item.taxAmount) || 0), 0);
    //
    //   //nextTick(() => {formRef.value?.
    //     setFieldsValue({
    //       orderTotal: Math.round(orderTotal * 100) / 100,
    //       orderNet: Math.round(orderNet * 100) / 100,
    //       orderTax: Math.round(orderTax * 100) / 100,
    //     });
    //  // });
    // };
    const calcOrderTotal = () => {
      const details = salesOrderDetailTable.dataSource || [];

      const orderTotal = details.reduce((sum, item) => sum + (Number(item.detailAmount) || 0), 0);
      const orderNet = details.reduce((sum, item) => sum + (Number(item.netAmount) || 0), 0);
      const orderTax = details.reduce((sum, item) => sum + (Number(item.taxAmount) || 0), 0);

      console.log('calcOrderTotal:', {
        detailCount: details.length,
        orderTotal,
        orderNet,
        orderTax,
        firstDetail: details[0]?.detailAmount
      });

      // 使用解构的 setFieldsValue
      nextTick(() => {
        try {
          setFieldsValue({
            orderTotal: Math.round(orderTotal * 100) / 100,
            orderNet: Math.round(orderNet * 100) / 100,
            orderTax: Math.round(orderTax * 100) / 100,
          });
          console.log('setFieldsValue success');
        } catch (e) {
          console.error('setFieldsValue failed:', e);
        }
      });
    };
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

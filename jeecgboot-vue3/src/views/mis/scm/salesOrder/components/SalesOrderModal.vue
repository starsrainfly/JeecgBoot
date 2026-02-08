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
          />
      </a-tab-pane>
    </a-tabs>
    <!--选择价格表-->
    <PriceSelectModal @register="registerPriceModal" @select="handlePriceSelected" />
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

    import PriceSelectModal from './PriceSelectModal.vue'
    // 2. 注册弹窗
    const [registerPriceModal, { openModal: openPriceModal }] = useModal()

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

      setPriceSelectModalOpener((row, rowIndex, tableRef) => {
        const values = formRef.value?.getFieldsValue?.() || {};
        console.log('准备打开价格弹窗，参数:', { customerId: values.customerId, salesmanId: values.salesmanId });
        openPriceModal({
          customerId: values.customerId,
          salesmanId: values.salesmanId,
        });
      });

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

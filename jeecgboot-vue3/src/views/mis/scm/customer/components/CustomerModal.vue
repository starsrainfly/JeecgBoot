<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="CustomerForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="收货地址" key="customerAddress" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="customerAddress"
          :loading="customerAddressTable.loading"
          :columns="customerAddressTable.columns"
          :dataSource="customerAddressTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          />
      </a-tab-pane>
      <a-tab-pane tab="客户质证" key="customerQualification" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="customerQualification"
          :loading="customerQualificationTable.loading"
          :columns="customerQualificationTable.columns"
          :dataSource="customerQualificationTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          />
      </a-tab-pane>
      <a-tab-pane tab="客户联系人" key="customerContact" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="customerContact"
          :loading="customerContactTable.loading"
          :columns="customerContactTable.columns"
          :dataSource="customerContactTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          />
      </a-tab-pane>
      <a-tab-pane tab="客户销售员" key="customerSalesman" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="customerSalesman"
          :loading="customerSalesmanTable.loading"
          :columns="customerSalesmanTable.columns"
          :dataSource="customerSalesmanTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="salesmanformDisabled"
          :toolbar="true"
          />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref,reactive} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import { JVxeTable } from '/@/components/jeecg/JVxeTable'
    import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
    import {formSchema,customerAddressColumns,customerQualificationColumns,customerContactColumns,customerSalesmanColumns} from '../Customer.data';
    import {saveOrUpdate,customerAddressList,customerQualificationList,customerContactList,customerSalesmanList} from '../Customer.api';
    import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
    import {useMessage} from "@/hooks/web/useMessage";
    import { useUserStore } from "@/store/modules/user";
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const formDisabled = ref(false);
    const salesmanformDisabled = ref(false); //管理员可编辑

    const refKeys = ref(['customerAddress', 'customerQualification', 'customerContact', 'customerSalesman', ]);
    const activeKey = ref('customerAddress');
    const customerAddress = ref();
    const customerQualification = ref();
    const customerContact = ref();
    const customerSalesman = ref();
    const { createMessage } = useMessage();

    const tableRefs = {customerAddress, customerQualification, customerContact, customerSalesman, };
    const customerAddressTable = reactive({
          loading: false,
          dataSource: [],
          columns:customerAddressColumns
    })
    const customerQualificationTable = reactive({
          loading: false,
          dataSource: [],
          columns:customerQualificationColumns
    })
    const customerContactTable = reactive({
          loading: false,
          dataSource: [],
          columns:customerContactColumns
    })
    const customerSalesmanTable = reactive({
          loading: false,
          dataSource: [],
          columns:customerSalesmanColumns
    })
    const userStore = useUserStore();
    const userInfo = userStore.getUserInfo;
    const isAdmin = userInfo.roles?.includes('admin') || userInfo.username === 'admin';


    //表单配置
    const [registerForm, {setProps,resetFields, setFieldsValue, validate}] = useForm({
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 6},
        labelCol:{ span: 6 },
        wrapperCol:{ span: 18 },
    });
     //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //重置表单
        await reset();
        setModalProps({confirmLoading: false,showCancelBtn:data?.showFooter,showOkBtn:data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        formDisabled.value = !data?.showFooter;
        salesmanformDisabled.value = !isAdmin || !data?.showFooter;
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
             requestSubTableData(customerAddressList, {id:data?.record?.id}, customerAddressTable)
             requestSubTableData(customerQualificationList, {id:data?.record?.id}, customerQualificationTable)
             requestSubTableData(customerContactList, {id:data?.record?.id}, customerContactTable)
             requestSubTableData(customerSalesmanList, {id:data?.record?.id}, customerSalesmanTable)
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
      activeKey.value = 'customerAddress';
      customerAddressTable.dataSource = [];
      customerQualificationTable.dataSource = [];
      customerContactTable.dataSource = [];
      customerSalesmanTable.dataSource = [];
    }
    function classifyIntoFormData(allValues) {
         let main = Object.assign({}, allValues.formValue)
         return {
           ...main, // 展开
           customerAddressList: allValues.tablesValue[0].tableData,
           customerQualificationList: allValues.tablesValue[1].tableData,
           customerContactList: allValues.tablesValue[2].tableData,
           customerSalesmanList: allValues.tablesValue[3].tableData,
         }
       }
    //表单提交事件
    async function requestAddOrEdit(values) {
        try {
            setModalProps({confirmLoading: true});
          console.log('提交前 values:', values);
          // 【新增校验】客户地址至少一条
          const addressList = values.customerAddressList || values.misCustomerAddressList;
          if (!addressList || addressList.length === 0) {
            createMessage.error('客户地址不能为空，请至少添加一条地址');
            setModalProps({ confirmLoading: false });
            return; // 阻断提交
          }
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

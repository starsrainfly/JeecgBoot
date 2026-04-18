<template>
  <div>
    <BasicForm @register="registerForm" ref="formRef"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated  @change="handleChangeTabs">
      <a-tab-pane tab="客户地址" key="customerAddress" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="customerAddress"
          v-if="customerAddressTable.show"
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
          v-if="customerQualificationTable.show"
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
          v-if="customerContactTable.show"
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
          v-if="customerSalesmanTable.show"
          :loading="customerSalesmanTable.loading"
          :columns="customerSalesmanTable.columns"
          :dataSource="customerSalesmanTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
        />
      </a-tab-pane>
    </a-tabs>

    <div style="width: 100%;text-align: center" v-if="!formDisabled">
      <a-button @click="handleSubmit" pre-icon="ant-design:check" type="primary">提 交</a-button>
    </div>
  </div>
</template>

<script lang="ts">

  import {BasicForm, useForm} from '/@/components/Form/index';
  import { computed, defineComponent, reactive, ref, unref } from 'vue';
  import {defHttp} from '/@/utils/http/axios';
  import { propTypes } from '/@/utils/propTypes';
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils';
  import {getBpmFormSchema,customerAddressColumns,customerQualificationColumns,customerContactColumns,customerSalesmanColumns} from '../Customer.data';
  import {saveOrUpdate,customerAddressList,customerQualificationList,customerContactList,customerSalesmanList} from '../Customer.api';

  export default defineComponent({
    name: "CustomerForm",
    components:{
      BasicForm,
    },
    props:{
      formData: propTypes.object.def({}),
      formBpm: propTypes.bool.def(true),
    },
    setup(props){
      const [registerForm, { setFieldsValue, setProps }] = useForm({
        labelWidth: 150,
        schemas: getBpmFormSchema(props.formData),
        showActionButtonGroup: false,
        baseColProps: {span: 6},
      });

      const formDisabled = computed(()=>{
        if(props.formData.disabled === false){
          return false;
        }
        return true;
      });

      const refKeys = ref(['customerAddress', 'customerQualification', 'customerContact', 'customerSalesman', ]);
      const activeKey = ref('customerAddress');
      const customerAddress = ref();
      const customerQualification = ref();
      const customerContact = ref();
      const customerSalesman = ref();
      const tableRefs = {customerAddress, customerQualification, customerContact, customerSalesman, };
      const customerAddressTable = reactive({
        loading: false,
        dataSource: [],
        columns:customerAddressColumns,
        show: false
      })
      const customerQualificationTable = reactive({
        loading: false,
        dataSource: [],
        columns:customerQualificationColumns,
        show: false
      })
      const customerContactTable = reactive({
        loading: false,
        dataSource: [],
        columns:customerContactColumns,
        show: false
      })
      const customerSalesmanTable = reactive({
        loading: false,
        dataSource: [],
        columns:customerSalesmanColumns,
        show: false
      })

      const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] = useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys,validateSubForm);

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
        await saveOrUpdate(values, true);
      }

      const queryByIdUrl = '/scm/customer/queryById';
      async function initFormData(){
        let params = {id: props.formData.dataId};
        const data = await defHttp.get({url: queryByIdUrl, params});
        //设置表单的值
        await setFieldsValue({...data});
        requestSubTableData(customerAddressList, {id: data.id}, customerAddressTable, ()=>{
          customerAddressTable.show = true;
        });
        requestSubTableData(customerQualificationList, {id: data.id}, customerQualificationTable, ()=>{
          customerQualificationTable.show = true;
        });
        requestSubTableData(customerContactList, {id: data.id}, customerContactTable, ()=>{
          customerContactTable.show = true;
        });
        requestSubTableData(customerSalesmanList, {id: data.id}, customerSalesmanTable, ()=>{
          customerSalesmanTable.show = true;
        });
        //默认是禁用
        await setProps({disabled: formDisabled.value})
      }

      initFormData();

      return {
        registerForm,
        formDisabled,
        formRef,
        handleSubmit,
        activeKey,
        handleChangeTabs,
        customerAddress,
        customerQualification,
        customerContact,
        customerSalesman,
        customerAddressTable,
        customerQualificationTable,
        customerContactTable,
        customerSalesmanTable,
      }
    }
  });
</script>

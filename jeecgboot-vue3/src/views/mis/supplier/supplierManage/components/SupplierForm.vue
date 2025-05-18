<template>
  <div>
    <BasicForm @register="registerForm" ref="formRef"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated  @change="handleChangeTabs">
      <a-tab-pane tab="供应商质证表" key="supplierQualification" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="supplierQualification"
          v-if="supplierQualificationTable.show"
          :loading="supplierQualificationTable.loading"
          :columns="supplierQualificationTable.columns"
          :dataSource="supplierQualificationTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
        />
      </a-tab-pane>
      <a-tab-pane tab="供应商联系人" key="supplierContact" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="supplierContact"
          v-if="supplierContactTable.show"
          :loading="supplierContactTable.loading"
          :columns="supplierContactTable.columns"
          :dataSource="supplierContactTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
        />
      </a-tab-pane>
      <a-tab-pane tab="供应商采购员" key="supplierPurchaser" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="supplierPurchaser"
          v-if="supplierPurchaserTable.show"
          :loading="supplierPurchaserTable.loading"
          :columns="supplierPurchaserTable.columns"
          :dataSource="supplierPurchaserTable.dataSource"
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
  import {getBpmFormSchema,supplierQualificationColumns,supplierContactColumns,supplierPurchaserColumns} from '../Supplier.data';
  import {saveOrUpdate,supplierQualificationList,supplierContactList,supplierPurchaserList} from '../Supplier.api';

  export default defineComponent({
    name: "SupplierForm",
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
        baseColProps: {span: 6}
      });

      const formDisabled = computed(()=>{
        if(props.formData.disabled === false){
          return false;
        }
        return true;
      });

      const refKeys = ref(['supplierQualification', 'supplierContact', 'supplierPurchaser', ]);
      const activeKey = ref('supplierQualification');
      const supplierQualification = ref();
      const supplierContact = ref();
      const supplierPurchaser = ref();
      const tableRefs = {supplierQualification, supplierContact, supplierPurchaser, };
      const supplierQualificationTable = reactive({
        loading: false,
        dataSource: [],
        columns:supplierQualificationColumns,
        show: false
      })
      const supplierContactTable = reactive({
        loading: false,
        dataSource: [],
        columns:supplierContactColumns,
        show: false
      })
      const supplierPurchaserTable = reactive({
        loading: false,
        dataSource: [],
        columns:supplierPurchaserColumns,
        show: false
      })

      const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] = useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys,validateSubForm);

      function classifyIntoFormData(allValues) {
        let main = Object.assign({}, allValues.formValue)
        return {
          ...main, // 展开
          supplierQualificationList: allValues.tablesValue[0].tableData,
          supplierContactList: allValues.tablesValue[1].tableData,
          supplierPurchaserList: allValues.tablesValue[2].tableData,
        }
      }

      //表单提交事件
      async function requestAddOrEdit(values) {
        await saveOrUpdate(values, true);
      }

      const queryByIdUrl = '/supplier/supplier/queryById';
      async function initFormData(){
        let params = {id: props.formData.dataId};
        const data = await defHttp.get({url: queryByIdUrl, params});
        //设置表单的值
        await setFieldsValue({...data});
        requestSubTableData(supplierQualificationList, {id: data.id}, supplierQualificationTable, ()=>{
          supplierQualificationTable.show = true;
        });
        requestSubTableData(supplierContactList, {id: data.id}, supplierContactTable, ()=>{
          supplierContactTable.show = true;
        });
        requestSubTableData(supplierPurchaserList, {id: data.id}, supplierPurchaserTable, ()=>{
          supplierPurchaserTable.show = true;
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
        supplierQualification,
        supplierContact,
        supplierPurchaser,
        supplierQualificationTable,
        supplierContactTable,
        supplierPurchaserTable,
      }
    }
  });
</script>
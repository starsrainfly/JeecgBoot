<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="SupplierForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="供应商质证表" key="supplierQualification" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="supplierQualification"
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
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref,reactive} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import { JVxeTable } from '/@/components/jeecg/JVxeTable'
    import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
    import {formSchema,supplierQualificationColumns,supplierContactColumns,supplierPurchaserColumns} from '../Supplier.data';
    import {saveOrUpdate,supplierQualificationList,supplierContactList,supplierPurchaserList} from '../Supplier.api';
    import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const formDisabled = ref(false);
    const refKeys = ref(['supplierQualification', 'supplierContact', 'supplierPurchaser', ]);
    const activeKey = ref('supplierQualification');
    const supplierQualification = ref();
    const supplierContact = ref();
    const supplierPurchaser = ref();
    const tableRefs = {supplierQualification, supplierContact, supplierPurchaser, };
    const supplierQualificationTable = reactive({
          loading: false,
          dataSource: [],
          columns:supplierQualificationColumns
    })
    const supplierContactTable = reactive({
          loading: false,
          dataSource: [],
          columns:supplierContactColumns
    })
    const supplierPurchaserTable = reactive({
          loading: false,
          dataSource: [],
          columns:supplierPurchaserColumns
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
             requestSubTableData(supplierQualificationList, {id:data?.record?.id}, supplierQualificationTable)
             requestSubTableData(supplierContactList, {id:data?.record?.id}, supplierContactTable)
             requestSubTableData(supplierPurchaserList, {id:data?.record?.id}, supplierPurchaserTable)
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
      activeKey.value = 'supplierQualification';
      supplierQualificationTable.dataSource = [];
      supplierContactTable.dataSource = [];
      supplierPurchaserTable.dataSource = [];
    }
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
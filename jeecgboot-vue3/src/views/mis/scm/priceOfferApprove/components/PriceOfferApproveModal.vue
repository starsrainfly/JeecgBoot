<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1024" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="PriceOfferForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="报价单明细" key="priceOfferDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="priceOfferDetail"
          :loading="priceOfferDetailTable.loading"
          :columns="priceOfferDetailTable.columns"
          :dataSource="priceOfferDetailTable.dataSource"
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
    import {formSchema,priceOfferDetailColumns} from '../PriceOfferApprove.data';
    import {saveOrUpdate,priceOfferDetailList, Approve} from '../PriceOfferApprove.api';
    import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const formDisabled = ref(false);
    const refKeys = ref(['priceOfferDetail', ]);
    const activeKey = ref('priceOfferDetail');
    const priceOfferDetail = ref();
    const tableRefs = {priceOfferDetail, };
    const priceOfferDetailTable = reactive({
          loading: false,
          dataSource: [],
          columns:priceOfferDetailColumns
    })

    const isAuditMode = ref(false); // 新增：是否为审核模式

    //表单配置
    const [registerForm, {setProps,resetFields, setFieldsValue, validate}] = useForm({
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 8}
    });
     //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //重置表单
        await reset();
        // 新增：判断是否为审核模式
        isAuditMode.value = data?.isAudit === true;
        setModalProps({confirmLoading: false,showCancelBtn:data?.showFooter,showOkBtn:data?.showFooter,
          title:data?.title || getModalTitle()});
        isUpdate.value = !!data?.isUpdate;
        formDisabled.value = !data?.showFooter;
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
             requestSubTableData(priceOfferDetailList, {id:data?.record?.id}, priceOfferDetailTable)
        }
        // 隐藏底部时禁用整个表单
       setProps({ disabled: !data?.showFooter })
    });
    //方法配置
    const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] = useJvxeMethod(
      handleSubmitMethod,classifyIntoFormData,tableRefs,activeKey,refKeys);

    // 使用 ref 来存储当前提交方法，以便动态切换
    const submitMethodRef = ref(requestAddOrEdit);
    // 在 useModalInner 中切换
    // submitMethodRef.value = isAuditMode.value ? requestAudit : requestAddOrEdit;
    // 不使用 ref，而是使用一个函数来动态判断
    async function handleSubmitMethod(values) {
      if (isAuditMode.value) {
        console.log('执行审核提交');
        return requestApprove(values);
      } else {
        console.log('执行编辑/新增提交');
        return requestAddOrEdit(values);
      }
    }
    /**
     * 获取弹窗标题
     */
    function getModalTitle() {
      if (isAuditMode.value) return '审核';
      if (!unref(isUpdate)) return '新增';
      if (!unref(formDisabled)) return '编辑';
      return '详情';
    }
    //设置标题
    //const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));
    const title = computed(() => {
      return getModalTitle();

    });
    //设置标题
   // const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

    async function reset(){
      await resetFields();
      activeKey.value = 'priceOfferDetail';
      priceOfferDetailTable.dataSource = [];
      isAuditMode.value = false;
      submitMethodRef.value = requestAddOrEdit; // 重置提交方法
    }
    function classifyIntoFormData(allValues) {
         let main = Object.assign({}, allValues.formValue)
         return {
           ...main, // 展开
           priceOfferDetailList: allValues.tablesValue[0].tableData,
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

    async function requestApprove(values){
      try {
        setModalProps({confirmLoading: true});
        //提交表单
        await Approve(values);
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

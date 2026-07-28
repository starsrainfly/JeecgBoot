<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="900" @ok="handleSubmit">
    <BasicForm @register="registerForm" name="DispatchForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm, FormSchema } from '/@/components/Form';
  import { dispatchTask } from '../ProductionTask.api';

  const emit = defineEmits(['register', 'success']);
  const isReDispatch = ref(false);

  // ===== 通用字段：只读概要 + 可编辑项 =====
  const baseSchemas: FormSchema[] = [
    { label: 'id', field: 'id', component: 'Input', show: false },
    { label: '工单编号', field: 'taskNo', component: 'Input', dynamicDisabled: true },
    { label: '工单名称', field: 'taskName', component: 'Input', dynamicDisabled: true },
    {
      label: '工单类型', field: 'taskType', component: 'JDictSelectTag',
      componentProps: { dictCode: 'mes_task_type', disabled: true },
    },
    { label: '批次号', field: 'batchNo', component: 'Input', dynamicDisabled: true },
    { label: '产品编号', field: 'productCode', component: 'Input', dynamicDisabled: true },
    { label: '产品名称', field: 'productName', component: 'Input', dynamicDisabled: true },
    { label: '工序', field: 'sequence', component: 'InputNumber', dynamicDisabled: true },
    { label: '计划设备', field: 'planEquipmentName', component: 'Input', dynamicDisabled: true },
    {
      label: '操作说明', field: 'taskDesc', component: 'InputTextArea',
      dynamicDisabled: true, colProps: { span: 24 },
    },
    {
      label: '指派操作员', field: 'assignedOperatorId', component: 'JDictSelectTag',
      componentProps: { dictCode: "sys_user where del_flag='0' and status='1',realname,id" },
      dynamicRules: () => [{ required: true, message: '请选择指派操作员!' }],
    },
    {
      label: '生产备注', field: 'productRemark', component: 'InputTextArea',
      colProps: { span: 24 },
    },
  ];

  // ===== 质检工单（taskType='qc'）追加字段 =====
  const qcSchemas: FormSchema[] = [
    {
      label: '质检状态', field: 'qcStatus', component: 'Select',
      componentProps: {
        disabled: true,
        // TODO: 按你实际的质检状态字典调整选项，或换成 JDictSelectTag + dictCode
        options: [
          { label: '待检', value: '0' },
          { label: '已报检', value: '1' },
          { label: '合格', value: '2' },
          { label: '不合格', value: '3' },
        ],
      },
    },
    {
      label: '质检备注', field: 'qcRemark', component: 'InputTextArea',
      colProps: { span: 24 },
    },
  ];

  const [registerForm, { setProps, resetFields, setFieldsValue, validate, scrollToField }] = useForm({
    schemas: baseSchemas,
    showActionButtonGroup: false,
    labelWidth: 100,
    baseColProps: { span: 12 },
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    setModalProps({ confirmLoading: false });
    const record = data?.record || {};
    isReDispatch.value = record.status === 'ASSIGNED';
    // 质检工单追加质检字段
    const schemas = record.taskType === 'qc' ? [...baseSchemas, ...qcSchemas] : baseSchemas;
    setProps({ schemas });
    await setFieldsValue({ ...record });
  });

  const title = computed(() => (unref(isReDispatch) ? '重新派工' : '工单派工'));

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      await dispatchTask(values);
      closeModal();
      emit('success');
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-input-number) { width: 100%; }
</style>

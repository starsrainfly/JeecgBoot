<template>
  <BasicForm @register="registerForm" />
</template>

<script lang="ts" setup>
  import { BasicForm, useForm } from '/@/components/Form';
  import { singleMoveFormSchema } from '../StockMoveTask.data';

  const props = defineProps<{
    record?: Recordable;
  }>();

  const emit = defineEmits(['register']);

  const [registerForm, { validate, resetFields, setFieldsValue, updateSchema }] = useForm({
    labelWidth: 100,
    schemas: singleMoveFormSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 12 },
  });

  // 暴露方法给父组件
  defineExpose({
    validate,
    resetFields,
    setFieldsValue,
    updateSchema,
  });
</script>

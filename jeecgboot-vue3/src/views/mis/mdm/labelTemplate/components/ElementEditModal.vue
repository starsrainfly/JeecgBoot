<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="500"
    @ok="handleSubmit"
  >
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';

  const emit = defineEmits(['register', 'save']);
  const { createMessage } = useMessage();

  const isUpdate = ref(false);
  const recordData = ref<any>({});

  const title = computed(() => unref(isUpdate) ? '编辑元素' : '添加元素');

  const typeOptions = [
    { label: '文本', value: 'text' },
    { label: '一维码', value: 'barcode' },
    { label: '二维码', value: 'qrcode' },
  ];

  const fieldOptions = [
    { label: '公司名称', value: 'companyName' },
    { label: '产品名称', value: 'productName' },
    { label: '产品编码', value: 'productCode' },
    { label: '颜色', value: 'color' },
    { label: '批次条码', value: 'batchNo' },
    { label: '批次号文字', value: 'batchNoText' },
    { label: '生产日期', value: 'produceDate' },
    { label: '有效期', value: 'expiryDate' },
    { label: '规格', value: 'spec' },
    { label: '二维码', value: 'qrCode' },
    { label: '质检状态', value: 'qcStatus' },
  ];

  // 二维码内容格式选项
  const qrFormatOptions = [
    { label: '标准JSON（产品+批次+日期）', value: 'standard_json' },
    { label: '精简（仅批次号）', value: 'batch_only' },
    { label: '完整（含颜色）', value: 'full_json' },
  ];

  const formSchemas = [
    {
      label: '元素类型',
      field: 'type',
      component: 'ASelect',
      componentProps: { options: typeOptions },
      dynamicRules: () => [{ required: true, message: '请选择元素类型!' }],
    },
    {
      label: '字段标识',
      field: 'field',
      component: 'ASelect',
      componentProps: { options: fieldOptions },
      dynamicRules: () => [{ required: true, message: '请选择字段标识!' }],
    },
    {
      label: '显示标签',
      field: 'label',
      component: 'Input',
      dynamicRules: () => [{ required: true, message: '请输入显示标签!' }],
    },
    {
      label: 'X坐标(mm)',
      field: 'x',
      component: 'InputNumber',
      dynamicRules: () => [{ required: true, message: '请输入X坐标!' }],
    },
    {
      label: 'Y坐标(mm)',
      field: 'y',
      component: 'InputNumber',
      dynamicRules: () => [{ required: true, message: '请输入Y坐标!' }],
    },
    // 文本类型特有
    {
      label: '字体大小',
      field: 'fontSize',
      component: 'InputNumber',
      defaultValue: 10,
      componentProps: { min: 6, max: 20 },
      ifShow: ({ values }) => values.type === 'text',
    },
    {
      label: '加粗',
      field: 'bold',
      component: 'Switch',
      defaultValue: false,
      ifShow: ({ values }) => values.type === 'text',
    },
    {
      label: '固定值',
      field: 'value',
      component: 'Input',
      ifShow: ({ values }) => values.type === 'text',
    },
    // 条码类型特有
    {
      label: '宽度(mm)',
      field: 'width',
      component: 'InputNumber',
      defaultValue: 30,
      componentProps: { min: 10 },
      ifShow: ({ values }) => values.type === 'barcode',
    },
    {
      label: '高度(mm)',
      field: 'height',
      component: 'InputNumber',
      defaultValue: 10,
      componentProps: { min: 5 },
      ifShow: ({ values }) => values.type === 'barcode',
    },
    // 二维码类型特有 - 新增内容格式
    // {
    //   label: '内容格式',
    //   field: 'qrFormat',
    //   component: 'Select',
    //   componentProps: {
    //     options: [
    //       { label: '标准JSON（产品+批次+日期）', value: 'standard_json' },
    //       { label: '精简（仅批次号）', value: 'batch_only' },
    //       { label: '完整（含颜色）', value: 'full_json' },
    //     ]
    //   },
    //   defaultValue: 'standard_json',
    //   ifShow: ({ values }) => values.type === 'qrcode',
    //
    // },

    {
      label: '尺寸(mm)',
      field: 'size',
      component: 'InputNumber',
      defaultValue: 18,
      componentProps: { min: 10, max: 30 },
      ifShow: ({ values }) => values.type === 'qrcode',
    },
  ];

  const [registerForm, { validate, setFieldsValue, resetFields }] = useForm({
    schemas: formSchemas,
    showActionButtonGroup: false,
    labelWidth: 100,
    baseColProps: { span: 24 },
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data: any) => {
    await resetFields();
    isUpdate.value = !!data?.isUpdate;
    recordData.value = {};

    if (data?.record) {
      recordData.value = { ...data.record };
      // 确保 qrFormat 有值
      if (data.record.type === 'qrcode' && !data.record.qrFormat) {
        data.record.qrFormat = 'standard_json';
      }
      await setFieldsValue(data.record);
    } else {
      await setFieldsValue({
        type: 'text',
        x: 2,
        y: 2,
        fontSize: 10,
        bold: false,
        width: 30,
        height: 10,
        size: 18,
        qrFormat: 'standard_json',
      });
    }

    setModalProps({ confirmLoading: false });
  });

  async function handleSubmit() {
    try {
      setModalProps({ confirmLoading: true });
      const values = await validate();

      if (values.type === 'barcode') {
        values.format = 'CODE128';
      }

      const result = { ...recordData.value, ...values };
      emit('save', result);
      closeModal();
    } catch (error: any) {
      if (error?.errorFields) {
        return Promise.reject(error);
      }
      createMessage.error(error?.message || '保存失败');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

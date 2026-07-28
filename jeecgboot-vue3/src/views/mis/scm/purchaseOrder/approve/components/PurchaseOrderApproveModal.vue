<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose title="采购订单审核" :width="1200" @ok="handleSubmit">
    <!-- 主表信息（只读展示） -->
    <a-descriptions bordered size="small" :column="3">
      <a-descriptions-item label="采购单号">{{ order.orderNo }}</a-descriptions-item>
      <a-descriptions-item label="申请日期">{{ order.orderDate }}</a-descriptions-item>
      <a-descriptions-item label="供应商">{{ order.supplierName }}</a-descriptions-item>
      <a-descriptions-item label="采购员">{{ order.purchaserName }}</a-descriptions-item>
      <a-descriptions-item label="含税总额">{{ order.orderTotal }}</a-descriptions-item>
      <a-descriptions-item label="要求到货日期">{{ order.expectedDate }}</a-descriptions-item>
      <a-descriptions-item label="备注" :span="3">{{ order.remark }}</a-descriptions-item>
    </a-descriptions>

    <!-- 明细（只读） -->
    <JVxeTable
      keep-source
      resizable
      :columns="purchaseOrderDetailColumns"
      :dataSource="detailList"
      :height="260"
      :rowNumber="true"
      :disabled="true"
      :toolbar="false"
      style="margin-top: 12px"
    />

    <!-- 审核操作区 -->
    <BasicForm @register="registerForm" style="margin-top: 12px" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '@/components/Modal';
  import { BasicForm, useForm } from '@/components/Form';
  import { JVxeTable } from '@/components/jeecg/JVxeTable';
  import { useMessage } from '@/hooks/web/useMessage';
  import { purchaseOrderDetailColumns } from '../../PurchaseOrder.data';
  import { approveFormSchema } from '../PurchaseOrderApprove.data';
  import { approve, purchaseOrderDetailList } from '../../PurchaseOrder.api';
  import { defHttp } from '@/utils/http/axios';

  const emit = defineEmits(['register', 'success']);
  const { createMessage } = useMessage();

  const order = ref<any>({});
  const detailList = ref<any[]>([]);

  const [registerForm, { validate, resetFields }] = useForm({
    schemas: approveFormSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    labelWidth: 90,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    order.value = data.record;
    detailList.value = [];
    // 加载明细
    const list = await defHttp.get({ url: purchaseOrderDetailList, params: { id: data.record.id } });
    detailList.value = list || [];
  });

  async function handleSubmit() {
    const values = await validate();
    if (values.approveStatus === '2' && !(values.approveRemark || '').trim()) {
      createMessage.error('拒绝时必须填写审核备注');
      return;
    }
    try {
      setModalProps({ confirmLoading: true });
      await approve({
        id: order.value.id,
        approveStatus: values.approveStatus,
        approveRemark: values.approveRemark,
      });
      createMessage.success(values.approveStatus === '1' ? '审核通过' : '已拒绝');
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

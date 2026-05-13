<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    :width="800"
    @ok="handleSubmit"
    :confirmLoading="confirmLoading"
  >
    <!-- 原位置信息展示 -->
    <div v-if="!isBatch && currentRecord" class="mb-4 p-4 bg-gray-50 rounded">
      <h4 class="font-bold mb-2">原位置信息</h4>
      <a-descriptions :column="2" size="small" bordered>
        <a-descriptions-item label="物料编码">{{ currentRecord.goodsCode }}</a-descriptions-item>
        <a-descriptions-item label="物料名称">{{ currentRecord.goodsName }}</a-descriptions-item>
        <a-descriptions-item label="批号">{{ currentRecord.batchNo || '-' }}</a-descriptions-item>
        <a-descriptions-item label="当前库存">{{ currentRecord.quantity }} {{ currentRecord.unit }}</a-descriptions-item>
        <a-descriptions-item label="原仓库">{{ currentRecord.warehouseId_dictText }}</a-descriptions-item>
        <a-descriptions-item label="原区域">{{ currentRecord.areaId_dictText }}</a-descriptions-item>
        <a-descriptions-item label="原货架">{{ currentRecord.shelfId_dictText || '-' }}</a-descriptions-item>
        <a-descriptions-item label="原货位">{{ currentRecord.locationId_dictText || '-' }}</a-descriptions-item>
      </a-descriptions>
    </div>

    <!-- 批量移库表格 -->
    <div v-else-if="isBatch" class="mb-4">
      <a-alert :message="`已选择 ${moveRecords.length} 条库存记录`" type="info" show-icon class="mb-2" />
      <BasicTable
        :columns="batchColumns"
        :dataSource="moveRecords"
        :pagination="false"
        :showIndexColumn="true"
        size="small"
        bordered
      />
    </div>

    <!-- 移库表单 -->
    <StockMoveTaskForm ref="formRef" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, nextTick } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable } from '/@/components/Table';
  import { Descriptions, Alert } from 'ant-design-vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { doMove, batchMove } from '../StockMoveTask.api';
  import StockMoveTaskForm from './StockMoveTaskForm.vue';

  const ADescriptions = Descriptions;
  const ADescriptionsItem = Descriptions.Item;
  const AAlert = Alert;

  const emit = defineEmits(['success', 'register']);

  const { createMessage } = useMessage();
  const confirmLoading = ref(false);
  const formRef = ref<any>(null);

  const currentRecord = ref<any>(null);
  const moveRecords = ref<any[]>([]);
  const isBatch = ref(false);

  const modalTitle = computed(() => isBatch.value ? '批量移库' : '移库作业');

  const batchColumns = [
    { title: '物料编码', dataIndex: 'goodsCode', width: 120 },
    { title: '物料名称', dataIndex: 'goodsName', width: 150 },
    { title: '批号', dataIndex: 'batchNo', width: 100 },
    { title: '数量', dataIndex: 'quantity', width: 100 },
    { title: '原仓库', dataIndex: 'warehouseId_dictText', width: 120 },
    { title: '原区域', dataIndex: 'areaId_dictText', width: 120 },
  ];

  const [registerModal, { closeModal }] = useModalInner((data) => {
    nextTick(() => {
      formRef.value?.resetFields();
      moveRecords.value = [];
      currentRecord.value = null;
      isBatch.value = false;

      if (data.record && !data.isBatch) {
        // 单条移库
        currentRecord.value = data.record;
        moveRecords.value = [data.record];
        isBatch.value = false;

        formRef.value?.setFieldsValue({
          fromStockId: data.record.id,
          moveQty: data.record.quantity,
        });
      } else if (data.records && data.isBatch) {
        // 批量移库
        moveRecords.value = data.records;
        isBatch.value = true;
      }
    });
  });

  async function handleSubmit() {
    try {
      const values = await formRef.value?.validate();
      if (!values) return;

      confirmLoading.value = true;

      if (isBatch.value) {
        // 批量移库
        const batchData = moveRecords.value.map((record) => ({
          fromStockId: record.id,
          toWarehouseId: values.toWarehouseId,
          toAreaId: values.toAreaId,
          toShelfId: values.toShelfId,
          toLocationId: values.toLocationId,
          moveQty: record.quantity,
          moveReason: values.moveReason,
          remark: values.remark,
        }));
        await batchMove(batchData);
        createMessage.success('批量移库成功');
      } else {
        // 单条移库
        const params = {
          fromStockId: currentRecord.value.id,
          toWarehouseId: values.toWarehouseId,
          toAreaId: values.toAreaId,
          toShelfId: values.toShelfId,
          toLocationId: values.toLocationId,
          moveQty: values.moveQty,
          moveReason: values.moveReason,
          remark: values.remark,
        };
        await doMove(params);
        createMessage.success('移库成功');
      }

      closeModal();
      emit('success');
    } catch (error) {
      console.error('移库失败:', error);
      createMessage.error(error.message || '移库失败');
    } finally {
      confirmLoading.value = false;
    }
  }
</script>

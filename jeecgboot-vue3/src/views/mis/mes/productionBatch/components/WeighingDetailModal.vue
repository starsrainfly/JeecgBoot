<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="配料详情"
    :width="1200"
    :footer="null"
  >
    <!-- 批次基本信息 -->
    <a-descriptions :column="3" size="small" bordered class="mb-4">
      <a-descriptions-item label="批次号">{{ batchInfo.batchNo }}</a-descriptions-item>
      <a-descriptions-item label="产品">{{ batchInfo.productName }}</a-descriptions-item>
      <a-descriptions-item label="计划产量">{{ batchInfo.plannedQty }} kg</a-descriptions-item>
<!--      <a-descriptions-item label="配料状态">-->
<!--        <a-tag :color="weighingStatusColor">{{ weighingStatusText }}</a-tag>-->
<!--      </a-descriptions-item>-->
      <a-descriptions-item label="配料完成度">{{ weighingProgress }}</a-descriptions-item>
      <a-descriptions-item label="实际总投料">{{ totalActualWeight }} kg</a-descriptions-item>
    </a-descriptions>

    <!-- 物料称重明细 -->
    <a-card title="物料称重明细" size="small">
      <a-table
        :columns="bomColumns"
        :dataSource="bomDataSource"
        size="small"
        :pagination="false"
        bordered
      >
        <!-- 进度列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'progress'">
            <a-progress
              :percent="record.progressPercent"
              size="small"
              :status="record.isComplete ? 'success' : 'normal'"
            />
          </template>
          <template v-if="column.key === 'weighingRecords'">
            <a-tag v-for="(item, idx) in record.weighingRecords" :key="idx" size="small">
              {{ item.operator }}: {{ item.actualQty }}kg
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 称重记录流水 -->
    <a-card title="称重记录流水" size="small" class="mt-4">
      <a-table
        :columns="recordColumns"
        :dataSource="weighingRecords"
        size="small"
        :pagination="{ pageSize: 10 }"
      />
    </a-card>
  </BasicModal>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  //import { getWeighingDetail } from '../ProductionBatch.api';

  const batchInfo = ref({});
  const bomDataSource = ref([]);
  const weighingRecords = ref([]);

  const weighingProgress = computed(() => {
    // 从前端传入的 record 或后端返回的汇总信息中获取
    // 如果后端在 getWeighingDetail 中也返回了汇总信息，可以在这里使用
    // 例如: return `${batchInfo.value.completedBom} / ${batchInfo.value.totalBom}`;
    // 这里暂时沿用原来的逻辑，从 bomDataSource 计算
    const completed = bomDataSource.value.filter(item => item.isComplete).length;
    const total = bomDataSource.value.length;
    return `${completed} / ${total}`;
  });

  const totalActualWeight = computed(() => {
    // 从前端传入的 record 或后端返回的汇总信息中获取
    // 这里暂时沿用原来的逻辑，从 bomDataSource 计算
    return bomDataSource.value.reduce((sum, item) => sum + parseFloat(item.totalActualQty || 0), 0);
  });

  const bomColumns = [
    { title: '序号', dataIndex: 'serialNo', width: 60 },
    { title: '物料编码', dataIndex: 'materialCode', width: 120 },
    { title: '物料名称', dataIndex: 'materialName', width: 150 },
    { title: '规格', dataIndex: 'materialSpec', width: 120 },
    { title: '配比', dataIndex: 'proportion', width: 80 },
    { title: '计划量(kg)', dataIndex: 'plannedQty', width: 100 },
    { title: '实际量(kg)', dataIndex: 'totalActualQty', width: 100 },
    { title: '完成进度', key: 'progress', width: 150 },
    { title: '称重记录', key: 'weighingRecords', width: 200 },
  ];

  const recordColumns = [
    { title: '时间', dataIndex: 'createTime', width: 150 },
    { title: '物料', dataIndex: 'materialName', width: 150 },
    { title: '称重(kg)', dataIndex: 'actualQty', width: 100 },
    { title: '操作员', dataIndex: 'operatorName', width: 100 },
  ];

  const [registerModal, { setModalProps }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: true });
    try {
      batchInfo.value = data;
      const detail = await getWeighingDetail(data.id);

      // 处理物料明细
      bomDataSource.value = detail.bomList.map(bom => ({
        ...bom,
        progressPercent: Math.round((bom.totalActualQty / bom.plannedQty) * 100),
        isComplete: bom.totalActualQty >= bom.plannedQty,
      }));

      // 称重记录流水
      weighingRecords.value = detail.weighingRecords;
    } finally {
      setModalProps({ confirmLoading: false });
    }
  });
</script>

<style scoped>

</style>

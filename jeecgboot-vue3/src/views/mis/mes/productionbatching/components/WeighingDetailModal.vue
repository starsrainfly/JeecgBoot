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
      <a-descriptions-item label="计划产量">{{ formatNumber(batchInfo.plannedQty) }} kg</a-descriptions-item>
      <a-descriptions-item label="配料完成度">{{ weighingProgress }}</a-descriptions-item>
      <a-descriptions-item label="实际总投料">{{ formatNumber(totalActualWeight) }} kg</a-descriptions-item>
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
              {{ item.operator }}: {{ formatNumber(item.actualQty) }}kg
            </a-tag>
          </template>
          <template v-if="column.key === 'plannedQty'">
            {{ formatNumber(record.plannedQty) }}
          </template>
          <template v-if="column.key === 'totalActualQty'">
            {{ formatNumber(record.totalActualQty) }}
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
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'actualQty'">
            {{ formatNumber(record.actualQty) }}
          </template>
        </template>
      </a-table>
    </a-card>
  </BasicModal>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { getWeighingDetail } from '../ProductionBatch.api';

  const batchInfo = ref<any>({});
  const bomDataSource = ref<any[]>([]);
  const weighingRecords = ref<any[]>([]);

  // 辅助函数：精确到4位小数
  function round4(num: any): number {
    if (num === null || num === undefined || num === '') return 0;
    return Math.round(parseFloat(num) * 10000) / 10000;
  }

  // 辅助函数：格式化数字显示（最多4位小数，去掉末尾0）
  function formatNumber(num: any): string | number {
    if (num === null || num === undefined || num === '') return '-';
    const val = parseFloat(num);
    if (isNaN(val)) return '-';
    // 精确到4位小数，然后去掉末尾的0
    return parseFloat(val.toFixed(4));
  }

  // 判断配料是否完成（精确到4位小数比较）
  function isWeighingComplete(totalActualQty: any, plannedQty: any): boolean {
    const actual = round4(totalActualQty);
    const planned = round4(plannedQty);
    return actual >= planned;
  }

  const weighingProgress = computed(() => {
    // 使用精确到4位小数的比较
    const completed = bomDataSource.value.filter(item =>
      isWeighingComplete(item.totalActualQty, item.plannedQty)
    ).length;
    const total = bomDataSource.value.length;
    return `${completed} / ${total}`;
  });

  const totalActualWeight = computed(() => {
    // 精确到4位小数求和
    return bomDataSource.value.reduce((sum, item) => {
      return round4(sum + round4(item.totalActualQty));
    }, 0);
  });

  const bomColumns = [
    { title: '序号', dataIndex: 'serialNo', width: 60 },
    { title: '物料编码', dataIndex: 'materialCode', width: 120 },
    { title: '物料名称', dataIndex: 'materialName', width: 150 },
    { title: '规格', dataIndex: 'materialSpec', width: 120 },
    { title: '配比', dataIndex: 'proportion', width: 80 },
    { title: '计划量(kg)', key: 'plannedQty', dataIndex: 'plannedQty', width: 100 },
    { title: '实际量(kg)', key: 'totalActualQty', dataIndex: 'totalActualQty', width: 100 },
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

      // 处理物料明细 - 使用4位小数精度
      bomDataSource.value = detail.bomList.map((bom: any) => {
        const planned = round4(bom.plannedQty);
        const actual = round4(bom.totalActualQty);
        const percent = planned > 0 ? Math.round((actual / planned) * 100) : 0;

        return {
          ...bom,
          plannedQty: planned,
          totalActualQty: actual,
          progressPercent: percent,
          isComplete: isWeighingComplete(actual, planned),
        };
      });

      // 称重记录流水 - 同样处理精度
      weighingRecords.value = (detail.weighingRecords || []).map((record: any) => ({
        ...record,
        actualQty: round4(record.actualQty),
      }));
    } finally {
      setModalProps({ confirmLoading: false });
    }
  });
</script>

<style scoped>
  .mb-4 {
    margin-bottom: 16px;
  }
  .mt-4 {
    margin-top: 16px;
  }
</style>

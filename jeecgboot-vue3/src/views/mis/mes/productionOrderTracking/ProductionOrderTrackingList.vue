<template>
  <div class="p-2">
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
      </template>

      <!-- 自定义单元格 -->
      <template v-slot:bodyCell="{ column, record, text }">
        <!-- 批次进度 -->
        <template v-if="column.dataIndex === 'batchProgress'">
          <div v-if="(record.totalBatches || 0) > 0">
            <a-tooltip :title="`已入库 ${record.completedBatches || 0} / 共 ${record.totalBatches} 批`">
              <a-progress
                :percent="getBatchPercent(record)"
                size="small"
                :stroke-color="getProgressColor(record)"
              />
            </a-tooltip>
            <div style="font-size: 12px; color: #666;">
              质检合格: {{ record.qcPassBatches || 0 }} 批
            </div>
          </div>
          <span v-else style="color: #999;">—</span>
        </template>

        <!-- 计划完工日期 -->
        <template v-else-if="column.dataIndex === 'plannedEndDate'">
          <span :class="getEndDateClass(record)">{{ formatDate(text) }}</span>
        </template>

        <!-- 其他列默认显示 -->
        <template v-else>{{ text }}</template>
      </template>

      <!-- 操作列 -->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>

    <!-- 批次明细弹窗 -->
    <ProductionBatchModal @register="registerModal" />
  </div>
</template>

<script lang="ts" name="mes-productionOrder-tracking" setup>
  import {ref, reactive} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import {useModal} from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage'
  import ProductionBatchModal from './components/ProductionBatchModal.vue'
  import {columns, searchFormSchema} from './ProductionOrderTracking.data';
  import {getList, getExportUrl} from './ProductionOrderTracking.api';

  const queryParam = reactive<any>({});

  const [registerModal, {openModal}] = useModal();

  const { prefixCls, tableContext, onExportXls } = useListPage({
    tableProps: {
      title: '生产订单进度跟踪',
      api: getList,
      columns,
      canResize: false,
      formConfig: {
        labelWidth: 100,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        fieldMapToTime: [
          ['planDate', ['beginDate', 'endDate'], 'YYYY-MM-DD'],
        ],
      },
      // ===== 补上 actionColumn =====
      actionColumn: {
        width: 120,
        fixed: 'right',
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: "生产订单进度跟踪",
      url: getExportUrl,
      params: queryParam,
    },
  });

  const [registerTable, {reload}] = tableContext;

  function handleExport() {
    console.log('导出');
  }

  function getBatchPercent(record) {
    const total = Number(record.totalBatches) || 0;
    const completed = Number(record.completedBatches) || 0;
    if (total === 0) return 0;
    return Math.round((completed / total) * 100);
  }

  function getProgressColor(record) {
    const percent = getBatchPercent(record);
    if (percent === 100) return '#52c41a';
    if (percent >= 50) return '#faad14';
    return '#ff4d4f';
  }

  function getEndDateClass(record) {
    if (!record.plannedEndDate || record.status === '3') return '';
    const today = new Date();
    today.setHours(0,0,0,0);
    const end = new Date(record.plannedEndDate);
    if (isNaN(end.getTime())) return '';
    if (end < today) return 'text-red font-bold';
    const diff = Math.ceil((end.getTime() - today.getTime()) / 86400000);
    if (diff <= 3) return 'text-orange';
    return '';
  }

  // 日期格式化
  function formatDate(date) {
    if (!date) return '-';
    if (typeof date === 'string') {
      return date.length > 10 ? date.substring(0, 10) : date;
    }
    try {
      const d = new Date(date);
      if (isNaN(d.getTime())) return '-';
      return d.toISOString().substring(0, 10);
    } catch {
      return '-';
    }
  }

  function getTableAction(record) {
    return [
      {
        label: '查看批次',
        onClick: () => {
          openModal(true, {
            orderId: record.id,
            orderNo: record.orderNo,
            productName: record.productName,
          });
        },
      },
    ];
  }
</script>

<style lang="less" scoped>
  .text-red { color: #f5222d; }
  .text-orange { color: #fa8c16; }
  .font-bold { font-weight: bold; }
</style>

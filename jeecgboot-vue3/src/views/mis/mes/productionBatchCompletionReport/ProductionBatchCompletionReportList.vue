<!-- views/mes/productionBatchCompletionReport/ProductionBatchCompletionReportList.vue -->
<template>
  <div class="p-2">
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">
          导出
        </a-button>
      </template>

      <template #bodyCell="{ column, record, text }">
        <!-- 入库状态：优先显示字典文本，颜色按数值判断 -->
        <template v-if="column.dataIndex === 'inStockStatus'">
          <a-tag :color="getInStockColor(text)">
            {{ record.inStockStatus_dictText || getInStockText(text) }}
          </a-tag>
        </template>
        <!-- 质检状态 -->
        <template v-else-if="column.dataIndex === 'qcStatus'">
          <a-tag :color="getQcColor(record.qcStatus)">
            {{ record.qcStatus_dictText || record.qcStatus }}
          </a-tag>
        </template>
        <!-- 批次状态 -->
        <template v-else-if="column.dataIndex === 'status'">
          <a-tag :color="getStatusColor(text)">
            {{ record.status_dictText || text }}
          </a-tag>
        </template>
        <!-- 配料状态 -->
        <template v-else-if="column.dataIndex === 'weighingStatus'">
          <a-tag :color="getWeighingColor(text)">
            {{ record.weighingStatus_dictText || text }}
          </a-tag>
        </template>
        <template v-else>{{ text }}</template>
      </template>

      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>

    <ProductionBatchCompletionDetailModal @register="registerModal" />
  </div>
</template>

<script lang="ts" name="mes-productionBatch-completion-report" setup>
  import { reactive } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import ProductionBatchCompletionDetailModal from './components/ProductionBatchCompletionDetailModal.vue';
  import { columns, searchFormSchema } from './ProductionBatchCompletionReport.data';
  import { getList, getExportUrl } from './ProductionBatchCompletionReport.api';

  const queryParam = reactive<any>({});

  const [registerModal, { openModal }] = useModal();

  const { prefixCls, tableContext, onExportXls } = useListPage({
    tableProps: {
      title: '生产批次完工报表',
      api: getList,
      columns,
      canResize: false,
      formConfig: {
        labelWidth: 100,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        fieldMapToTime: [['productionDate', ['beginDate', 'endDate'], 'YYYY-MM-DD']],
      },
      actionColumn: {
        width: 100,
        fixed: 'right',
        title: '操作',
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: '生产批次完工报表',
      url: getExportUrl,
      params: queryParam,
    },
  });

  const [registerTable, { reload }] = tableContext;

  // ========== 入库状态（字典 mes_in_stock_status：0未入库 1部分入库 2已入库）==========
  function getInStockColor(status) {
    const s = String(status);
    if (s === '2') return 'green';
    if (s === '1') return 'orange';
    return 'default';
  }
  function getInStockText(status) {
    const s = String(status);
    if (s === '2') return '已入库';
    if (s === '1') return '部分入库';
    if (s === '0') return '未入库';
    return status || '-';
  }

  // ========== 质检状态 ==========
  function getQcColor(status) {
    const s = String(status || '').toUpperCase();
    if (s === 'PASS') return 'green';
    if (s === 'FAIL') return 'red';
    if (s === 'WAIT_CHECK') return 'orange';
    return 'default';
  }

  // ========== 批次状态 ==========
  function getStatusColor(status) {
    const s = String(status || '').toUpperCase();
    if (s === 'COMPLETED' || s === '3') return 'green';
    if (s === 'WEIGHED') return 'blue';
    if (s === 'WEIGHING') return 'cyan';
    if (s === 'PRODUCTION' || s === '2') return 'processing';
    return 'default';
  }

  // ========== 配料状态 ==========
  function getWeighingColor(status) {
    const s = String(status || '').toUpperCase();
    if (s === 'WEIGHED') return 'green';
    if (s === 'WEIGHING') return 'blue';
    return 'default';
  }

  function getTableAction(record) {
    return [
      {
        label: '查看详情',
        onClick: () => {
          openModal(true, {
            batchId: record.id,
            record: record,
          });
        },
      },
    ];
  }
</script>

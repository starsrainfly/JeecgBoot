<template>
  <div>
    <BasicTable @register="registerTable" :rowClassName="getRowClassName">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出 </a-button>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable } from '@/components/Table';
  import { useListPage } from '@/hooks/system/useListPage';
  import { useMethods } from '/@/hooks/system/useMethods';
  import { monthlyReport, getExportMonthlyReportUrl } from './stockReport.api';
  import { columns, searchFormSchema } from './StockMonthlyReport.data';

  const { handleExportXls } = useMethods();

  const { tableContext } = useListPage({
    tableProps: {
      title: '收发存月报（按物料）',
      api: monthlyReport,
      columns,
      canResize: true,
      pagination: false,
      formConfig: {
        schemas: searchFormSchema,
        showAdvancedButton: true,
        labelWidth: 100,
        baseColProps: { span: 6 },
        actionColOptions: { span: 8 },
        transformDateFunc: (date) => date.format('YYYY-MM-DD'),
      },
    },
    exportConfig: {
      name: '收发存月报',
      url: getExportMonthlyReportUrl,
    },
  });

  const [registerTable, { getForm }] = tableContext;

  function getRowClassName(record) {
    if (record.closingQty < 0) return 'stock-negative-row';
    return '';
  }

  function onExportXls() {
    const formData = getForm().getFieldsValue();
    handleExportXls('收发存月报', getExportMonthlyReportUrl, formData);
  }
</script>

<style scoped>
  :deep(.stock-negative-row) {
    background-color: #fff1f0 !important;
  }
  :deep(.stock-negative-row td) {
    color: #cf1322 !important;
  }
</style>

<template>
  <div>
    <BasicTable @register="registerTable">
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
  import { outSummaryByCustomer, getExportOutSummaryUrl } from './stockReport.api';
  import { columns, searchFormSchema } from './StockOutSummary.data';

  const { handleExportXls } = useMethods();

  const { tableContext } = useListPage({
    tableProps: {
      title: '出库汇总表（按客户）',
      api: outSummaryByCustomer,
      columns,
      canResize: true,
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
      name: '出库汇总表',
      url: getExportOutSummaryUrl,
    },
  });

  const [registerTable, { getForm }] = tableContext;

  function onExportXls() {
    const formData = getForm().getFieldsValue();
    handleExportXls('出库汇总表', getExportOutSummaryUrl, formData);
  }
</script>

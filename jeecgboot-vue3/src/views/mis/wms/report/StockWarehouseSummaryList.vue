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
  import { warehouseSummary, getExportWarehouseSummaryUrl } from './stockReport.api';
  import { columns, searchFormSchema } from './StockWarehouseSummary.data';

  const { handleExportXls } = useMethods();

  const { tableContext } = useListPage({
    tableProps: {
      title: '库存收发存汇总（按仓库）',
      api: warehouseSummary,
      columns,
      canResize: true,
      pagination: false,
      formConfig: {
        schemas: searchFormSchema,
        showAdvancedButton: false,
        labelWidth: 100,
        baseColProps: { span: 6 },
        actionColOptions: { span: 8 },
        transformDateFunc: (date) => date.format('YYYY-MM-DD'),
      },
    },
    exportConfig: {
      name: '仓库收发存汇总',
      url: getExportWarehouseSummaryUrl,
    },
  });

  const [registerTable, { getForm }] = tableContext;

  function onExportXls() {
    const formData = getForm().getFieldsValue();
    handleExportXls('仓库收发存汇总', getExportWarehouseSummaryUrl, formData);
  }
</script>

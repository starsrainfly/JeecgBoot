<template>
  <div class="p-2">
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
      </template>

      <template v-slot:bodyCell="{ column, record, text }">
        <template v-if="column.dataIndex === 'outstanding'">
          <span :class="Number(text) > 0 ? 'text-red font-bold' : ''">{{ text }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'overdueDays'">
          <span :class="Number(text) > 0 ? 'text-red' : ''">{{ text || '-' }}</span>
        </template>
        <template v-else>{{ text }}</template>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="scm-customer-statement" setup>
  import {reactive} from 'vue';
  import {BasicTable, useTable} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {columns, searchFormSchema} from './CustomerStatement.data';
  import {getList, getExportUrl} from './CustomerStatement.api';

  const queryParam = reactive<any>({});

  const { prefixCls, tableContext, onExportXls } = useListPage({
    tableProps: {
      title: '客户对账单',
      api: getList,
      columns,
      canResize: false,
      formConfig: {
        labelWidth: 100,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        fieldMapToTime: [
          ['orderDate', ['beginDate', 'endDate'], 'YYYY-MM-DD'],
        ],
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: "客户对账单",
      url: getExportUrl,
      params: queryParam,
    },
  });

  const [registerTable, {reload}] = tableContext;
</script>

<style lang="less" scoped>
  .text-red { color: #f5222d; }
  .font-bold { font-weight: bold; }
</style>

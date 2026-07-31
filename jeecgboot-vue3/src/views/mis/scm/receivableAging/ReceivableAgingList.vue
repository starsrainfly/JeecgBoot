<template>
  <div class="p-2">
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
      </template>

      <template v-slot:bodyCell="{ column, record, text }">
        <template v-if="column.dataIndex === 'totalOutstanding'">
          <span class="text-red font-bold">{{ text }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'aging6plus' && Number(text) > 0">
          <span class="text-red">{{ text }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'aging3to6' && Number(text) > 0">
          <span class="text-orange">{{ text }}</span>
        </template>
        <template v-else>{{ text }}</template>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="scm-receivable-aging" setup>
  import {reactive} from 'vue';
  import {BasicTable, useTable} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {columns, searchFormSchema} from './ReceivableAging.data';
  import {getList, getExportUrl} from './ReceivableAging.api';

  const queryParam = reactive<any>({});

  const { prefixCls, tableContext, onExportXls } = useListPage({
    tableProps: {
      title: '应收账龄分析',
      api: getList,
      columns,
      canResize: false,
      formConfig: {
        labelWidth: 100,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
      },
      pagination: false, // 账龄分析一般数据量不大，不分页
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: "应收账龄分析",
      url: getExportUrl,
      params: queryParam,
    },
  });

  const [registerTable, {reload}] = tableContext;
</script>

<style lang="less" scoped>
  .text-red { color: #f5222d; }
  .text-orange { color: #fa8c16; }
  .font-bold { font-weight: bold; }
</style>

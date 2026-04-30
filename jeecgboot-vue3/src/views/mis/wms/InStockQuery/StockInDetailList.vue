<template>
  <div>
    <BasicTable @register="registerTable" :searchInfo="searchInfo">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="mainId!=''"> 导出</a-button>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, inject, watch} from 'vue';
  import {BasicTable, useTable} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {stockInDetailColumns} from './StockIn.data';
  import {stockInDetailList, stockInDetailExportXlsUrl} from './StockIn.api';
  import { mapTableTotalSummary } from '/@/utils/common/compUtils';

  const mainId = inject('mainId') || '';
  const searchInfo = {};

  // ========== 子表汇总函数 - 必须在 useListPage 之前定义 ==========
  function handleDetailSummary(tableData: Recordable[]) {
    // 自动计算多个字段的合计
    const totals = mapTableTotalSummary(tableData, ['actualQty', 'applyQty', 'totalAmount']);

    return [
      {
        ...totals,
        goodsName: '合计',
        goodsCode: '',
        stockInNo: '',
        _row: '合计',
      }
    ];
  }

  // ========== useListPage 调用 ==========
  const {prefixCls, tableContext, onExportXls} = useListPage({
    tableProps: {
      api: stockInDetailList,
      columns: stockInDetailColumns,
      canResize: false,
      useSearchForm: false,
      showIndexColumn: true,
      pagination: false,
      bordered: true,
      summaryFunc: handleDetailSummary,
    },
    exportConfig: {
      name: '入库明细表',
      url: stockInDetailExportXlsUrl,
      params: {
        'id': mainId
      }
    }
  });

  const [registerTable, {reload}] = tableContext;

  watch(mainId, () => {
      searchInfo['id'] = unref(mainId);
      reload();
    }
  );
</script>

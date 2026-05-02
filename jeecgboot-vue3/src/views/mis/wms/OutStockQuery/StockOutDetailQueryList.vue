<!-- OutStoreQuery/StockOutDetailQueryList.vue -->
<template>
  <div class="p-2">
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="wms-stockOut-detail-query" setup>
  import {ref, reactive} from 'vue';
  import {BasicTable, useTable} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {stockOutDetailQueryColumns, detailQuerySearchSchema} from './StockOutQuery.data';
  import {listDetailAll, getDetailExportUrl} from './StockOutQuery.api';
  import { mapTableTotalSummary } from '/@/utils/common/compUtils';
  const queryParam = reactive<any>({});
  const summaryData = ref<Recordable>({});

  // 合计行渲染 — 直接取后端返回的summary
  function handleSummary(tableData: Recordable[]) {
    const totals = mapTableTotalSummary(tableData, ['applyQty', 'actualQty', 'costTotal', 'salesTotal', 'overQty']);

    return [
      {
        ...totals,
        stockOutNo: '',
        stockOutType: '',
        customerName: '',
        warehouseName: '',
        requesterName: '',
        goodsCode: '',
        goodsName: '合计',
        goodsSpec: '',
        unit: '',
        batchNo: '',
        productionBatchNo: '',
        costPrice: '',
        salesPrice: '',
        expiryDate: '',
        productionDate: '',
        shelfLife: '',
        overFlag: '',
        status: '',
        approveStatus: '',
        stockOutTime: '',
        requiredDate: '',
        consignee: '',
        consigneePhone: '',
        deliverAddress: '',
        sourceOrderCode: '',
        sourceType: '',
        masterRemark: '',
        remark: '',
        createTime: '',
        _row: '合计',
      }
    ];
  }
  // // 1. 把 api 逻辑抽出来，保持函数引用
  // async function fetchList(params) {
  //   const res = await listDetailAll(params);
  //   if (res.success && res.result) {
  //     summaryData.value = res.result.summary || {};
  //     return {
  //       records: res.result.records,
  //       total: res.result.total,
  //     };
  //   }
  //   return res;
  // }
  const { prefixCls, tableContext, onExportXls } = useListPage({
    tableProps: {
      title: '出库明细查询',
      api:  listDetailAll,
      columns: stockOutDetailQueryColumns,
      canResize: false,
      formConfig: {
        labelWidth: 100,
        schemas: detailQuerySearchSchema,
      },
      beforeFetch: (params) => {
        if (params.stockOutTime) {
          const arr = params.stockOutTime.split(',');
          params.stockOutTime_begin = arr[0];
          params.stockOutTime_end = arr[1];
          params.stockOutTime = null;
        }
        if (params.requiredDate) {
          const arr = params.requiredDate.split(',');
          params.requiredDate_begin = arr[0];
          params.requiredDate_end = arr[1];
          params.requiredDate = null;
        }
        return Object.assign(params, queryParam);
      },
      pagination: {
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['10', '20', '50', '100'],
      },
      summaryFunc: handleSummary,
    },
    exportConfig: {
      name: "出库明细汇总表",
      url: getDetailExportUrl,
      params: queryParam,
    }
  });

  const [registerTable, {reload}] = tableContext;
</script>

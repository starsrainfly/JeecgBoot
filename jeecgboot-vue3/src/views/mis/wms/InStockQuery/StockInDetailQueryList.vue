<template>
  <div class="p-2">
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="wms-stockIn-detail-query" setup>
  import {ref, reactive} from 'vue';
  import {BasicTable, useTable} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {stockInDetailQueryColumns, detailQuerySearchSchema} from './StockIn.data';
  import {listDetailAll, getDetailExportUrl} from './StockIn.api';
  import { mapTableTotalSummary } from '/@/utils/common/compUtils';

  const queryParam = reactive<any>({});

  // 汇总函数
  function handleSummary(tableData: Recordable[]) {
    const totals = mapTableTotalSummary(tableData, ['applyQty', 'actualQty', 'totalAmount']);
    return [
      {
        ...totals,
        stockInNo: '',
        stockInType_dictText: '',
        supplierName: '',
        customerName: '',
        warehouseName_dictText: '',
        goodsName: '合计',
        goodsCode: '',
        goodsSpec: '',
        unit_dictText: '',
        unitPrice: '',
        batchNo: '',
        serialNo: '',
        qcStatus_dictText: '',
        productionDate: '',
        expiryDate: '',
        _row: '合计',
      }
    ];
  }

  const { prefixCls, tableContext, onExportXls } = useListPage({
    tableProps: {
      title: '入库明细查询',
      api: listDetailAll,
      columns: stockInDetailQueryColumns,
      canResize: false,
      formConfig: {
        labelWidth: 100,
        schemas: detailQuerySearchSchema,
        fieldMapToNumber: [],
        fieldMapToTime: [],
      },
      beforeFetch: (params) => {
        console.log("params:",params)
        if(params.stockInTime){
          const stockInTimeArr = params.stockInTime.split(',');
          params.stockInTime_begin = stockInTimeArr[0] + "00:00:00";
          params.stockInTime_end = stockInTimeArr[1] + "23:59:59";
          params.stockInTime = null;
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
      name: "入库明细汇总表",
      url: getDetailExportUrl,
      params: queryParam,
    }
  });

  const [registerTable, {reload}] = tableContext;
</script>

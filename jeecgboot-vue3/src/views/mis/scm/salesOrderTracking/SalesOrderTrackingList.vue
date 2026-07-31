<template>
  <div class="p-2">
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="handleExport"> 导出</a-button>
      </template>

      <!-- 交货日期颜色 -->
      <template v-slot:bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'deliveryDate'">
          <span :class="getDeliveryDateClass(record)">{{ record.deliveryDate }}</span>
        </template>
        <template v-if="column.dataIndex === 'receivableDate'">
          <span :class="getReceivableDateClass(record)">{{ record.receivableDate }}</span>
        </template>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="scm-salesOrder-tracking" setup>
  import {ref, reactive} from 'vue';
  import {BasicTable, useTable} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {columns, searchFormSchema} from './SalesOrderTracking.data';
  import {getList} from './SalesOrderTracking.api';

  const queryParam = reactive<any>({});

  const { prefixCls, tableContext } = useListPage({
    tableProps: {
      title: '销售订单执行跟踪',
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
      actionColumn: {
        width: 120,
        fixed: 'right',
        slots: { customRender: 'action' },
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
  });

  const [registerTable, {reload}] = tableContext;

  function handleExport() {
    // TODO: 如需导出，后续补exportUrl
    console.log('导出');
  }

  // 交货日期预警：有未发货且已逾期 → 红色
  function getDeliveryDateClass(record) {
    if (!record.deliveryDate) return '';
    const undelivered = Number(record.undeliveredQty) || 0;
    if (undelivered <= 0) return '';
    const today = new Date();
    today.setHours(0,0,0,0);
    const delivery = new Date(record.deliveryDate);
    if (delivery < today) return 'text-red font-bold';
    return '';
  }

  // 应收日期预警：有未收款且已逾期 → 红色
  function getReceivableDateClass(record) {
    if (!record.receivableDate) return '';
    const unreceived = Number(record.unreceivedAmount) || 0;
    if (unreceived <= 0) return '';
    const today = new Date();
    today.setHours(0,0,0,0);
    const receivable = new Date(record.receivableDate);
    if (receivable < today) return 'text-red font-bold';
    return '';
  }
</script>

<style lang="less" scoped>
  .text-red { color: #f5222d; }
  .font-bold { font-weight: bold; }
</style>

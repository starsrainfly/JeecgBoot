<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleExport">导出</a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'changeRate'">
          <span v-if="record.changeRate == null">-</span>
          <span v-else :style="{ color: Math.abs(record.changeRate) > 5 ? '#ff4d4f' : '#52c41a', fontWeight: Math.abs(record.changeRate) > 5 ? 'bold' : 'normal' }">
            {{ record.changeRate > 0 ? '+' : '' }}{{ record.changeRate }}%
          </span>
        </template>
        <template v-if="column.key === 'changeAmount'">
          <span v-if="record.changeAmount == null">-</span>
          <span v-else :style="{ color: record.changeAmount > 0 ? '#ff4d4f' : '#52c41a' }">
            {{ record.changeAmount > 0 ? '+' : '' }}{{ record.changeAmount }}
          </span>
        </template>
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '查看快照',
              onClick: handleViewSnapshot.bind(null, record),
            },
          ]"
        />
      </template>
    </BasicTable>

    <CostCalcModal @register="registerModal" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { list } from './ProductCostReport.api';
  import CostCalcModal from '../costCalc/components/CostCalcModal.vue';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();

  const columns = [
    { title: '产品编码', dataIndex: 'productCode', width: 120 },
    { title: '产品名称', dataIndex: 'productName', width: 150 },
    { title: '规格型号', dataIndex: 'productSpec', width: 120 },
    { title: '颜色', dataIndex: 'productColor', width: 80 },
    { title: '配方编码', dataIndex: 'recipeCode', width: 120 },
    { title: '配方名称', dataIndex: 'recipeName', width: 150 },
    { title: '核算日期', dataIndex: 'calcDate', width: 110 },
    { title: '核算类型', dataIndex: 'calcType', width: 100, customRender: ({ text }: any) => (text === 'MONTHLY' ? '月度自动' : '手动保存') },
    { title: '最新成本(元/kg)', dataIndex: 'totalCostLatest', width: 140, align: 'right', customRender: ({ text }: any) => (text ? Number(text).toFixed(4) : '-') },
    { title: '平均成本(元/kg)', dataIndex: 'totalCostAvg', width: 140, align: 'right', customRender: ({ text }: any) => (text ? Number(text).toFixed(4) : '-') },
    { title: '上期最新成本', dataIndex: 'lastPeriodCost', width: 140, align: 'right', customRender: ({ text }: any) => (text ? Number(text).toFixed(4) : '-') },
    { title: '涨跌额', key: 'changeAmount', dataIndex: 'changeAmount', width: 100, align: 'right' },
    { title: '涨跌率(%)', key: 'changeRate', dataIndex: 'changeRate', width: 100, align: 'right' },
    {
      title: '操作',
      key: 'action',
      width: 100,
      slots: { customRender: 'action' },
      fixed: 'right',
    },
  ];

  const [registerTable] = useTable({
    title: '产品成本核算报表',
    api: async (params) => {
      const res = await list(params);
      return res.result || res;
    },
    columns,
    formConfig: {
      labelWidth: 100,
      baseColProps: { span: 6 },
      actionColOptions: { span: 6 },
      schemas: [
        { field: 'productCode', label: '产品编码', component: 'Input' },
        { field: 'productName', label: '产品名称', component: 'Input' },
        {
          field: 'calcType',
          label: '核算类型',
          component: 'Select',
          componentProps: {
            options: [
              { label: '手动保存', value: 'MANUAL' },
              { label: '月度自动', value: 'MONTHLY' },
            ],
          },
        },
        {
          field: 'startDate',
          label: '开始日期',
          component: 'DatePicker',
          componentProps: { valueFormat: 'YYYY-MM-DD' },
        },
        {
          field: 'endDate',
          label: '结束日期',
          component: 'DatePicker',
          componentProps: { valueFormat: 'YYYY-MM-DD' },
        },
      ],
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
  });

  function handleViewSnapshot(record: any) {
    openModal(true, { mode: 'snapshot', calcId: record.id });
  }

  function handleExport() {
    createMessage.info('导出功能请对接后端 /scm/costCalcReport/exportXls');
  }
</script>

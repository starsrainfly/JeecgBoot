<template>
  <div class="admin-dashboard">
    <!-- 顶部统计卡片 -->
    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="本月销售额"
          :value="data.monthSalesAmount"
          color="#1890ff"
          icon="DollarOutlined"
          prefix="¥"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="本月回款"
          :value="data.monthReceiptAmount"
          color="#52c41a"
          icon="BankOutlined"
          prefix="¥"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="待生产工单"
          :value="data.pendingProduceOrderCount"
          color="#faad14"
          icon="ToolOutlined"
          :link="{ path: '/mis/mes/ProductionOrder' }"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="待审核单据"
          :value="data.pendingAuditCount"
          color="#ff4d4f"
          icon="AuditOutlined"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="库存预警"
          :value="data.stockWarningCount"
          color="#eb2f96"
          icon="WarningOutlined"
          :link="{ path: '/mis/wms/stock/StockList' }"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
<!--        <StatCard-->
<!--          title="本月出库"-->
<!--          :value="data.monthOutAmount"-->
<!--          color="#722ed1"-->
<!--          icon="ExportOutlined"-->
<!--          prefix="¥"-->
<!--        />-->
        <StatCard
          title="材料出库"
          :value="data.monthMaterialOutAmount"
          color="#722ed1"
          icon="ExportOutlined"
          prefix="¥"
          :link="{ path: '/mis/wms/MaterialOutApprove' }"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="产品出库"
          :value="data.monthProductOutAmount"
          color="#eb2f96"
          icon="ExportOutlined"
          prefix="¥"
          :link="{ path: '/mis/wms/ProductOutApprove' }"
        />
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="[16, 16]" style="margin-top: 16px;">
      <a-col :xs="24" :lg="12">
        <a-card title="近30天销售趋势" :bordered="false" :loading="loading" style="min-height: 350px;">
          <div ref="salesTrendChartRef" style="height: 300px;"></div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="本月收支对比" :bordered="false" :loading="loading" style="min-height: 350px;">
          <div ref="incomeExpenseChartRef" style="height: 300px;"></div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" style="margin-top: 16px;">
      <a-col :xs="24" :lg="12">
        <a-card title="生产工单状态分布" :bordered="false" :loading="loading" style="min-height: 350px;">
          <div ref="produceStatusChartRef" style="height: 300px;"></div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="待审核单据分布" :bordered="false" :loading="loading" style="min-height: 350px;">
          <div ref="pendingAuditChartRef" style="height: 300px;"></div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 列表区域 -->
    <a-row :gutter="[16, 16]" style="margin-top: 16px;">
      <a-col :xs="24" :lg="12">
        <a-card title="最近待审核销售订单" :bordered="false" :loading="loading" style="min-height: 400px;">
          <a-table :dataSource="data.recentSalesOrderList" :columns="salesOrderColumns" size="small" rowKey="id" />
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="最近待生产工单" :bordered="false" :loading="loading" style="min-height: 400px;">
          <a-table :dataSource="data.recentProduceOrderList" :columns="produceOrderColumns" size="small" rowKey="id" />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, onActivated, nextTick } from 'vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { defHttp } from '/@/utils/http/axios';
  import * as echarts from 'echarts';
  import {
    DollarOutlined, BankOutlined, ToolOutlined,
    AuditOutlined, WarningOutlined, ExportOutlined,
  } from '@ant-design/icons-vue';
  import StatCard from '../warehouse/components/StatCard.vue';

  const { createMessage } = useMessage();

  const loading = ref(false);
  const data = ref<any>({
    monthSalesAmount: 0,
    monthReceiptAmount: 0,
    pendingProduceOrderCount: 0,
    pendingAuditCount: 0,
    stockWarningCount: 0,
    monthOutAmount: 0,
    monthMaterialOutAmount:0,
    monthProductOutAmount:0,
    salesTrendList: [],
    monthIncomeExpense: {},
    produceOrderStatusList: [],
    pendingAuditDistList: [],
    recentSalesOrderList: [],
    recentProduceOrderList: [],
  });

  const salesTrendChartRef = ref();
  const incomeExpenseChartRef = ref();
  const produceStatusChartRef = ref();
  const pendingAuditChartRef = ref();

  let salesTrendChart: echarts.ECharts | null = null;
  let incomeExpenseChart: echarts.ECharts | null = null;
  let produceStatusChart: echarts.ECharts | null = null;
  let pendingAuditChart: echarts.ECharts | null = null;

  const salesOrderColumns = [
    { title: '订单号', dataIndex: 'orderNo', width: 140 },
    { title: '客户', dataIndex: 'customerName' },
    { title: '业务员', dataIndex: 'salesmanName', width: 100 },
    { title: '金额', dataIndex: 'orderTotal', width: 100, align: 'right' },
    { title: '销售审核', dataIndex: 'salesApproveStatus', width: 90 },
    { title: '财务审核', dataIndex: 'financeApproveStatus', width: 90 },
  ];

  const produceOrderColumns = [
    { title: '工单号', dataIndex: 'orderNo', width: 140 },
    { title: '产品', dataIndex: 'productName' },
    { title: '计划量', dataIndex: 'plannedQty', width: 100, align: 'right' },
    { title: '状态', dataIndex: 'status', width: 90 },
    { title: '计划开工', dataIndex: 'plannedStartDate', width: 110 },
  ];

  async function loadData() {
    loading.value = true;
    try {
      const res = await defHttp.get({ url: '/dashboard/manager' });
      const result = res.result || res;
      if (result) {
        Object.assign(data.value, result);
        nextTick(() => initCharts());
      }
    } catch (error) {
      createMessage.error('加载数据失败');
    } finally {
      loading.value = false;
    }
  }

  function initCharts() {
    initSalesTrendChart();
    initIncomeExpenseChart();
    initProduceStatusChart();
    initPendingAuditChart();
  }

  function initSalesTrendChart() {
    if (!salesTrendChartRef.value || !data.value.salesTrendList?.length) return;
    if (salesTrendChart) salesTrendChart.dispose();

    salesTrendChart = echarts.init(salesTrendChartRef.value);
    const dates = data.value.salesTrendList.map((item: any) => item.date?.substring(5));
    const amounts = data.value.salesTrendList.map((item: any) => item.salesAmount || 0);

    salesTrendChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value', name: '金额(元)' },
      series: [{
        name: '销售额',
        type: 'line',
        data: amounts,
        smooth: true,
        itemStyle: { color: '#1890ff' },
        areaStyle: { color: 'rgba(24,144,255,0.1)' }
      }]
    });
  }

  function initIncomeExpenseChart() {
    if (!incomeExpenseChartRef.value) return;
    if (incomeExpenseChart) incomeExpenseChart.dispose();

    incomeExpenseChart = echarts.init(incomeExpenseChartRef.value);
    const { salesAmount = 0, receiptAmount = 0, outAmount = 0 } = data.value.monthIncomeExpense || {};

    incomeExpenseChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['销售额', '回款额', '出库成本'] },
      xAxis: { type: 'category', data: ['本月'] },
      yAxis: { type: 'value', name: '金额(元)' },
      series: [
        { name: '销售额', type: 'bar', data: [salesAmount], itemStyle: { color: '#1890ff' } },
        { name: '回款额', type: 'bar', data: [receiptAmount], itemStyle: { color: '#52c41a' } },
        { name: '出库成本', type: 'bar', data: [outAmount], itemStyle: { color: '#ff4d4f' } },
      ]
    });
  }

  function initProduceStatusChart() {
    if (!produceStatusChartRef.value || !data.value.produceOrderStatusList?.length) return;
    if (produceStatusChart) produceStatusChart.dispose();

    produceStatusChart = echarts.init(produceStatusChartRef.value);
    const list = data.value.produceOrderStatusList;

    produceStatusChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: list.map((item: any) => ({ name: item.statusText, value: item.count })),
        label: { formatter: '{b}: {c} ({d}%)' }
      }]
    });
  }

  function initPendingAuditChart() {
    if (!pendingAuditChartRef.value || !data.value.pendingAuditDistList?.length) return;
    if (pendingAuditChart) pendingAuditChart.dispose();

    pendingAuditChart = echarts.init(pendingAuditChartRef.value);
    const list = data.value.pendingAuditDistList;

    pendingAuditChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      xAxis: { type: 'category', data: list.map((item: any) => item.auditTypeText) },
      yAxis: { type: 'value', name: '数量' },
      series: [{
        type: 'bar',
        data: list.map((item: any) => ({
          value: item.count,
          itemStyle: {
            color: item.auditType === 'SALES' ? '#1890ff' :
              item.auditType === 'FINANCE' ? '#52c41a' :
                item.auditType === 'IN' ? '#faad14' : '#ff4d4f'
          }
        })),
        barWidth: '50%'
      }]
    });
  }

  function handleResize() {
    salesTrendChart?.resize();
    incomeExpenseChart?.resize();
    produceStatusChart?.resize();
    pendingAuditChart?.resize();
  }

  onActivated(() => loadData());
  onMounted(() => {
    loadData();
    window.addEventListener('resize', handleResize);
  });
  onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
    salesTrendChart?.dispose();
    incomeExpenseChart?.dispose();
    produceStatusChart?.dispose();
    pendingAuditChart?.dispose();
  });
</script>

<style scoped lang="less">
  .admin-dashboard {
    padding: 16px;
    :deep(.ant-card) { border-radius: 8px; }
  }
</style>

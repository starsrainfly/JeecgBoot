<template>
  <div class="dashboard-container">
    <!-- 指标卡片 -->
    <a-row :gutter="16" class="metric-row">
      <a-col :xs="24" :sm="12" :md="8" :lg="4" v-for="item in metrics" :key="item.key">
        <a-card :body-style="{ padding: '20px' }" class="metric-card">
          <div class="metric-icon" :style="{ background: item.color }">
            <Icon :icon="item.icon" />
          </div>
          <div class="metric-content">
            <div class="metric-value">{{ item.value }}</div>
            <div class="metric-label">{{ item.label }}</div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 快捷操作 -->
    <a-row :gutter="16" class="action-row">
      <a-col :span="24">
        <a-card title="快捷操作" size="small">
          <a-space>
            <a-button type="primary" @click="goTo('/mis/scm/priceOffer/PriceOfferList')">
              <Icon icon="ant-design:file-add-outlined" /> 我的报价
            </a-button>
            <a-button type="primary" @click="goTo('/mis/scm/SalesOrderApply')">
              <Icon icon="ant-design:shopping-cart-outlined" /> 我的订单
            </a-button>
            <a-button type="primary" @click="goTo('/mis/scm/receivable/plan')">
              <Icon icon="ant-design:car-outlined" /> 收款计划
            </a-button>
            <a-button @click="goTo('/mis/scm/receivable/receipt')">
              <Icon icon="ant-design:money-collect-outlined" /> 登记收款
            </a-button>
            <a-button @click="goTo('/mis/scm/mycustomer/MyCustomerList')">
              <Icon icon="ant-design:team-outlined" /> 我的客户
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>

    <!-- 待办 + 图表 -->
    <a-row :gutter="16" class="content-row">
      <!-- 左侧：待办列表 -->
      <a-col :xs="24" :lg="12">
        <a-card title="我的待办" size="small" class="todo-card">
          <a-tabs v-model:activeKey="activeTab">
            <a-tab-pane key="quote" tab="待审核报价">
              <a-table
                :dataSource="dashboardData.pendingQuotes"
                :columns="quoteColumns"
                size="small"
                :pagination="false"
                @row-click="(record) => goTo(`/scm/priceOffer/detail/${record.id}`)"
              />
            </a-tab-pane>

            <a-tab-pane key="order" tab="待审核订单">
              <a-table
                :dataSource="dashboardData.pendingOrders"
                :columns="orderColumns"
                size="small"
                :pagination="false"
              >
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'financeApproveStatus'">
                    <a-tag :color="record.financeApproveStatus === '0' ? 'orange' : 'default'">
                      {{ record.financeApproveStatus === '0' ? '待财务审' : '' }}
                    </a-tag>
                    <a-tag :color="record.salesApproveStatus === '0' ? 'blue' : 'default'">
                      {{ record.salesApproveStatus === '0' ? '待销售审' : '' }}
                    </a-tag>
                  </template>
                </template>
              </a-table>
            </a-tab-pane>

            <a-tab-pane key="delivery" tab="待发货">
              <a-table
                :dataSource="dashboardData.pendingDeliveryOrders"
                :columns="deliveryColumns"
                size="small"
                :pagination="false"
              />
            </a-tab-pane>

            <a-tab-pane key="near" tab="近7天到期">
              <a-table
                :dataSource="dashboardData.nearDeliveryOrders"
                :columns="nearColumns"
                size="small"
                :pagination="false"
              >
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'daysRemain'">
                    <a-tag :color="record.daysRemain <= 3 ? 'red' : 'orange'">
                      剩{{ record.daysRemain }}天
                    </a-tag>
                  </template>
                </template>
              </a-table>
            </a-tab-pane>

            <a-tab-pane key="payment" tab="待收款">
              <a-table
                :dataSource="dashboardData.pendingPaymentPlans"
                :columns="paymentColumns"
                size="small"
                :pagination="false"
              >
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'unpaidAmount'">
                    <span style="color: #f5222d; font-weight: bold;">¥{{ record.unpaidAmount }}</span>
                  </template>
                </template>
              </a-table>
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </a-col>

      <!-- 右侧：图表 -->
      <a-col :xs="24" :lg="12">
        <a-card title="业绩趋势" size="small" class="chart-card">
          <div ref="trendChartRef" style="height: 320px;"></div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 底部：TOP5 + 分布 -->
    <a-row :gutter="16" class="bottom-row">
      <a-col :xs="24" :lg="12">
        <a-card title="本月产品销售TOP5" size="small">
          <div ref="top5ChartRef" style="height: 280px;"></div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="客户类型分布" size="small">
          <div ref="typeChartRef" style="height: 280px;"></div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted, computed, nextTick } from 'vue';
  import { useRouter } from 'vue-router';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { defHttp } from '/@/utils/http/axios';
  import { Icon } from '/@/components/Icon';
  import * as echarts from 'echarts';

  const router = useRouter();
  const { createMessage } = useMessage();

  const activeTab = ref('quote');
  const dashboardData = ref<any>({});
  const trendChartRef = ref<HTMLDivElement>();
  const top5ChartRef = ref<HTMLDivElement>();
  const typeChartRef = ref<HTMLDivElement>();

  let trendChart: echarts.ECharts;
  let top5Chart: echarts.ECharts;
  let typeChart: echarts.ECharts;

  // 指标卡片
  const metrics = computed(() => [
    {
      key: 'monthOrderAmount',
      label: '本月订单金额',
      value: `¥${dashboardData.value.monthOrderAmount || '0.00'}`,
      icon: 'ant-design:dollar-outlined',
      color: '#52c41a'
    },
    {
      key: 'monthReceiptAmount',
      label: '本月回款',
      value: `¥${dashboardData.value.monthReceiptAmount || '0.00'}`,
      icon: 'ant-design:money-collect-outlined',
      color: '#1890ff'
    },
    {
      key: 'pendingQuoteCount',
      label: '待审核报价',
      value: dashboardData.value.pendingQuoteCount || 0,
      icon: 'ant-design:file-exclamation-outlined',
      color: '#faad14'
    },
    {
      key: 'pendingOrderCount',
      label: '待审核订单',
      value: dashboardData.value.pendingOrderCount || 0,
      icon: 'ant-design:shopping-outlined',
      color: '#eb2f96'
    },
    {
      key: 'monthNewCustomerCount',
      label: '本月新客户',
      value: dashboardData.value.monthNewCustomerCount || 0,
      icon: 'ant-design:user-add-outlined',
      color: '#722ed1'
    },
    {
      key: 'monthUnpaidAmount',
      label: '本月待收款',
      value: `¥${dashboardData.value.monthUnpaidAmount || '0.00'}`,
      icon: 'ant-design:alert-outlined',
      color: '#f5222d'
    },
  ]);

  // 表格列定义
  const quoteColumns = [
    { title: '报价单号', dataIndex: 'offerNo', key: 'offerNo', width: 140 },
    { title: '客户', dataIndex: 'customerName', key: 'customerName', ellipsis: true },
    { title: '客户类型', dataIndex: 'customerType', key: 'customerType', width: 100 },
    { title: '报价日期', dataIndex: 'offerDate', key: 'offerDate', width: 120 },
  ];

  const orderColumns = [
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 140 },
    { title: '客户', dataIndex: 'customerName', key: 'customerName', ellipsis: true },
    { title: '订单金额', dataIndex: 'orderTotal', key: 'orderTotal', width: 120, align: 'right' },
    { title: '审核状态', key: 'financeApproveStatus', width: 140 },
    { title: '交货日期', dataIndex: 'deliveryDate', key: 'deliveryDate', width: 120 },
  ];

  const deliveryColumns = [
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 140 },
    { title: '客户', dataIndex: 'customerName', key: 'customerName', ellipsis: true },
    { title: '待发数量', dataIndex: 'remainQty', key: 'remainQty', width: 100, align: 'right' },
    { title: '交货日期', dataIndex: 'deliveryDate', key: 'deliveryDate', width: 120 },
  ];

  const nearColumns = [
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 140 },
    { title: '客户', dataIndex: 'customerName', key: 'customerName', ellipsis: true },
    { title: '待发数量', dataIndex: 'remainQty', key: 'remainQty', width: 100, align: 'right' },
    { title: '剩余天数', key: 'daysRemain', width: 100, align: 'center' },
  ];

  const paymentColumns = [
    { title: '计划单号', dataIndex: 'planNo', key: 'planNo', width: 140 },
    { title: '订单号', dataIndex: 'salesOrderNo', key: 'salesOrderNo', width: 140 },
    { title: '客户', dataIndex: 'customerName', key: 'customerName', ellipsis: true },
    { title: '待收金额', key: 'unpaidAmount', width: 120, align: 'right' },
    { title: '计划日期', dataIndex: 'planDate', key: 'planDate', width: 120 },
    { title: '期数', dataIndex: 'planStage', key: 'planStage', width: 80, align: 'center' },
  ];

  // 加载数据
  const loadData = async () => {
    try {
      const res = await defHttp.get({ url: '/scm/salesDashboard/data' });
      if (res) {
        dashboardData.value = res;
        nextTick(() => {
          initCharts();
        });
      }
    } catch (error) {
      createMessage.error('加载数据失败');
    }
  };

  const initCharts = () => {
    // 趋势图
    if (trendChartRef.value) {
      trendChart = echarts.init(trendChartRef.value);
      const trendData = dashboardData.value.monthTrend || [];
      trendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['订单金额', '订单数'] },
        xAxis: {
          type: 'category',
          data: trendData.map((item: any) => item.month),
          axisLabel: { rotate: 45 }
        },
        yAxis: [
          { type: 'value', name: '金额(元)' },
          { type: 'value', name: '单数' }
        ],
        series: [
          {
            name: '订单金额',
            type: 'line',
            data: trendData.map((item: any) => item.amount),
            smooth: true,
            areaStyle: { opacity: 0.1 }
          },
          {
            name: '订单数',
            type: 'bar',
            yAxisIndex: 1,
            data: trendData.map((item: any) => item.orderCount)
          }
        ]
      });
    }

    // TOP5图
    if (top5ChartRef.value) {
      top5Chart = echarts.init(top5ChartRef.value);
      const top5Data = dashboardData.value.productTop5 || [];
      top5Chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        xAxis: {
          type: 'category',
          data: top5Data.map((item: any) => item.productName),
          axisLabel: { rotate: 30, interval: 0 }
        },
        yAxis: { type: 'value', name: '金额(元)' },
        series: [{
          type: 'bar',
          data: top5Data.map((item: any) => item.totalAmount),
          itemStyle: { color: '#1890ff' }
        }]
      });
    }

    // 客户类型分布
    if (typeChartRef.value) {
      typeChart = echarts.init(typeChartRef.value);
      const typeData = dashboardData.value.customerTypeDist || [];
      typeChart.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: typeData.map((item: any) => ({
            name: item.customerType || '未分类',
            value: item.customerCount
          })),
          label: { formatter: '{b}: {c} ({d}%)' }
        }]
      });
    }
  };

  const goTo = (path: string) => {
    router.push(path);
  };

  onMounted(() => {
    loadData();
    window.addEventListener('resize', () => {
      trendChart?.resize();
      top5Chart?.resize();
      typeChart?.resize();
    });
  });
</script>

<style lang="less" scoped>
  .dashboard-container {
    padding: 16px;
    background: #f0f2f5;
    min-height: calc(100vh - 84px);

    .metric-row {
      margin-bottom: 16px;

      .metric-card {
        display: flex;
        align-items: center;

        .metric-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          font-size: 24px;
          margin-right: 12px;
        }

        .metric-content {
          flex: 1;

          .metric-value {
            font-size: 24px;
            font-weight: bold;
            color: #262626;
            line-height: 1;
          }

          .metric-label {
            font-size: 14px;
            color: #8c8c8c;
            margin-top: 4px;
          }
        }
      }
    }

    .action-row {
      margin-bottom: 16px;
    }

    .content-row {
      margin-bottom: 16px;

      .todo-card {
        :deep(.ant-tabs-nav) {
          margin-bottom: 8px;
        }
      }
    }

    .bottom-row {
      margin-bottom: 16px;
    }
  }
</style>

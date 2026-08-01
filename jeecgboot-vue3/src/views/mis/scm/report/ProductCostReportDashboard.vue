<template>
  <div class="dashboard-container">
    <!-- 筛选栏 -->
    <a-card class="search-card" size="small" :bordered="false">
      <div class="search-bar">
        <span class="search-label">核算期间：</span>
        <a-range-picker
          v-model:value="dateRange"
          valueFormat="YYYY-MM-DD"
          :placeholder="['开始日期', '结束日期']"
          style="width: 260px"
        />
        <a-button type="primary" @click="loadData">
          <template #icon><SearchOutlined /></template>
          查询
        </a-button>
        <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
      </div>
    </a-card>

    <!-- 统计卡片 -->
    <a-row :gutter="16" class="stat-row">
      <a-col :xs="24" :sm="12" :md="6">
        <a-card class="stat-card" :bordered="false">
          <div class="stat-title">本期核算产品数</div>
          <div class="stat-value">
            <span class="num">{{ data.calcProductCount || 0 }}</span>
            <span class="sep">/</span>
            <span class="total">{{ data.totalProductCount || 0 }}</span>
          </div>
          <div class="stat-desc">占总产品 {{ calcPercent }}%</div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card class="stat-card" :bordered="false">
          <div class="stat-title">本期总成本金额</div>
          <div class="stat-value">
            <span class="num">{{ formatNum(data.totalCostAmount, 4) }}</span>
            <span class="unit">万元</span>
          </div>
          <div class="stat-desc">各单位成本之和</div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card class="stat-card" :bordered="false">
          <div class="stat-title">成本上涨产品数</div>
          <div class="stat-value">
            <span class="num rise">{{ data.riseCount || 0 }}</span>
          </div>
          <div class="stat-desc">环比上期上涨</div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card class="stat-card" :bordered="false">
          <div class="stat-title">成本异常产品数</div>
          <div class="stat-value">
            <span class="num abnormal">{{ data.abnormalCount || 0 }}</span>
          </div>
          <div class="stat-desc">环比涨跌绝对值 &gt; 5%</div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 趋势图 + TOP10成本最高 -->
    <a-row :gutter="16" class="chart-row">
      <a-col :xs="24" :lg="12">
        <a-card title="近6个月成本趋势" :bordered="false">
          <div ref="trendChartRef" style="height: 320px;"></div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="TOP10 单位成本最高" :bordered="false">
          <a-table
            :columns="top10Columns"
            :data-source="data.top10HighCost"
            size="small"
            :pagination="false"
            rowKey="id"
            :scroll="{ y: 260 }"
          >
            <template #bodyCell="{ column, text }">
              <template v-if="column.dataIndex === 'totalCostLatest'">
                {{ text ? Number(text).toFixed(4) : '-' }}
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>

    <!-- TOP10涨幅 + 快捷入口 -->
    <a-row :gutter="16" class="chart-row">
      <a-col :xs="24" :lg="12">
        <a-card title="TOP10 涨幅最大" :bordered="false">
          <a-table
            :columns="top10RiseColumns"
            :data-source="data.top10Rise"
            size="small"
            :pagination="false"
            rowKey="id"
            :scroll="{ y: 260 }"
          >
            <template #bodyCell="{ column, text }">
              <template v-if="column.dataIndex === 'changeRate'">
                <span :style="{ color: text > 0 ? '#ff4d4f' : '#52c41a', fontWeight: 'bold' }">
                  {{ text > 0 ? '+' : '' }}{{ text }}%
                </span>
              </template>
              <template v-if="column.dataIndex === 'totalCostLatest'">
                {{ text ? Number(text).toFixed(4) : '-' }}
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="快捷入口" :bordered="false">
          <div class="quick-links">
            <a-button type="primary" block size="large" @click="goToList">
              查看成本核算明细报表
            </a-button>
            <a-button block size="large" style="margin-top: 12px" @click="goToSnapshot">
              查看成本核算快照
            </a-button>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted, nextTick } from 'vue';
  import { useRouter } from 'vue-router';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import { dashboard } from './ProductCostReport.api';
  import { useMessage } from '/@/hooks/web/useMessage';
  import * as echarts from 'echarts';

  const { createMessage } = useMessage();
  const router = useRouter();

  const dateRange = ref<string[]>([]);
  const data = ref<any>({});
  const trendChartRef = ref<HTMLDivElement>();
  let trendChart: echarts.ECharts | null = null;

  const calcPercent = computed(() => {
    const calc = data.value.calcProductCount || 0;
    const total = data.value.totalProductCount || 1;
    return ((calc / total) * 100).toFixed(1);
  });

  const top10Columns = [
    { title: '产品编码', dataIndex: 'productCode', width: 100 },
    { title: '产品名称', dataIndex: 'productName', width: 140, ellipsis: true },
    { title: '规格', dataIndex: 'productSpec', width: 100, ellipsis: true },
    { title: '最新成本(元/kg)', dataIndex: 'totalCostLatest', width: 130, align: 'right' },
  ];

  const top10RiseColumns = [
    { title: '产品编码', dataIndex: 'productCode', width: 100 },
    { title: '产品名称', dataIndex: 'productName', width: 140, ellipsis: true },
    { title: '最新成本', dataIndex: 'totalCostLatest', width: 110, align: 'right' },
    { title: '涨跌率', dataIndex: 'changeRate', width: 90, align: 'right' },
  ];

  async function loadData() {
    const params: any = {};
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }
    try {
      const res = await dashboard(params);
      data.value = res || {};
      nextTick(() => initTrendChart());
    } catch (e) {
      createMessage.error('加载看板数据失败');
    }
  }

  function handleReset() {
    dateRange.value = [];
    loadData();
  }

  function initTrendChart() {
    if (!trendChartRef.value) return;
    if (trendChart) {
      trendChart.dispose();
    }
    trendChart = echarts.init(trendChartRef.value);

    const trendData = data.value.trendData || [];
    const months = trendData.map((d: any) => d.month);
    const values = trendData.map((d: any) => d.totalCost);

    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: months,
        axisLabel: { rotate: 30 },
      },
      yAxis: {
        type: 'value',
        name: '单位成本合计(元/kg)',
      },
      series: [
        {
          name: '成本合计',
          type: 'line',
          smooth: true,
          data: values,
          areaStyle: {
            color: new (echarts as any).graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(24,144,255,0.3)' },
              { offset: 1, color: 'rgba(24,144,255,0.05)' },
            ]),
          },
          itemStyle: { color: '#1890ff' },
          lineStyle: { width: 2 },
        },
      ],
    });
  }

  function goToList() {
    router.push('/mis/scm/report/productCost');
  }

  function goToSnapshot() {
    router.push('/mis/scm/costCalc/snapshot');
  }

  function formatNum(val: any, digits = 2) {
    if (val === null || val === undefined) return '0';
    const n = Number(val);
    return isNaN(n) ? '0' : n.toFixed(digits);
  }

  onMounted(() => {
    loadData();
    window.addEventListener('resize', () => trendChart?.resize());
  });
</script>

<style scoped>
  .dashboard-container { padding: 16px; }
  .search-card { margin-bottom: 16px; }
  .search-bar {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: nowrap;
  }
  .search-label {
    font-size: 14px;
    color: rgba(0,0,0,0.85);
    white-space: nowrap;
    flex-shrink: 0;
  }
  .stat-row { margin-bottom: 16px; }
  .stat-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 8px;
  }
  .stat-card :deep(.ant-card-body) { padding: 20px; }
  .stat-title { font-size: 14px; opacity: 0.9; margin-bottom: 8px; }
  .stat-value { display: flex; align-items: baseline; gap: 4px; }
  .stat-value .num { font-size: 28px; font-weight: bold; }
  .stat-value .unit { font-size: 14px; opacity: 0.8; }
  .stat-value .sep { font-size: 18px; opacity: 0.7; margin: 0 4px; }
  .stat-value .total { font-size: 18px; opacity: 0.8; }
  .stat-value .rise { color: #ffccc7; }
  .stat-value .abnormal { color: #ffd8bf; }
  .stat-desc { font-size: 12px; opacity: 0.7; margin-top: 4px; }

  /* 卡片颜色区分 */
  .stat-row .ant-col:nth-child(1) .stat-card { background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%); }
  .stat-row .ant-col:nth-child(2) .stat-card { background: linear-gradient(135deg, #722ed1 0%, #eb2f96 100%); }
  .stat-row .ant-col:nth-child(3) .stat-card { background: linear-gradient(135deg, #fa8c16 0%, #fadb14 100%); }
  .stat-row .ant-col:nth-child(4) .stat-card { background: linear-gradient(135deg, #f5222d 0%, #fa541c 100%); }

  .chart-row { margin-bottom: 16px; }
  .quick-links { padding: 20px 0; }
</style>

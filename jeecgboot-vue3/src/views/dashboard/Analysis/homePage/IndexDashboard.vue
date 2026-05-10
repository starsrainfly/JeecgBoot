<template>
  <div class="p-4 dashboard-container">
    <!-- 第一行：核心指标卡 -->
    <CoreIndicatorCard :loading="loading" :data-list="coreData" class="enter-y" />

    <!-- 第二行：Tab切换区 -->
    <a-card :loading="tabLoading" class="!my-4 enter-y" :bordered="false">
      <a-tabs v-model:activeKey="activeTab" size="large">
        <template #rightExtra>
          <a-radio-group v-model:value="dateRange" @change="handleDateChange">
            <a-radio-button value="week">近7日</a-radio-button>
            <a-radio-button value="month">近30日</a-radio-button>
          </a-radio-group>
        </template>

        <!-- ========== 生产概览 ========== -->
        <a-tab-pane tab="生产概览" key="production">
          <a-row :gutter="24">
            <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
              <Bar :chartData="productionTrend" height="35vh" :seriesColor="themeColor" />
            </a-col>
            <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
              <a-card title="批次状态分布" :bordered="false">
                <div ref="batchPieRef" :style="{ height: '30vh', width: '100%' }"></div>
              </a-card>
            </a-col>
          </a-row>
        </a-tab-pane>

        <!-- ========== 仓储概览 ========== -->
        <a-tab-pane tab="仓储概览" key="warehouse">
          <a-row :gutter="24">
            <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
              <BarMulti
                :chartData="warehouseTrend"
                height="35vh"
                :seriesColor="[themeColor, '#67B962']"
                :option="{ legend: { top: 'bottom' } }"
              />
            </a-col>
            <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
              <a-card title="库存预警TOP5" :bordered="false">
                <a-list :data-source="warningList" size="small">
                  <template #renderItem="{ item, index }">
                    <a-list-item>
                      <a-list-item-meta>
                        <template #title>
                          <span :style="{ color: item.warningType === '0' ? 'red' : 'orange' }">
                            {{ index + 1 }}. {{ item.materialName }}
                          </span>
                        </template>
                        <template #description>
                          库存: {{ item.availableQty }} | 安全: {{ item.safetyStock }} | 缺口: {{ item.shortageQty }}
                        </template>
                      </a-list-item-meta>
                      <Tag :color="item.warningType === '0' ? 'red' : 'orange'">
                        {{ item.warningType === '0' ? '缺货' : '预警' }}
                      </Tag>
                    </a-list-item>
                  </template>
                </a-list>
              </a-card>
            </a-col>
          </a-row>
        </a-tab-pane>

        <!-- ========== 销售概览 ========== -->
        <a-tab-pane tab="销售概览" key="sales">
          <a-row :gutter="24">
            <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
              <a-card title="近12月销售额与回款趋势" :bordered="false">
                <LineMulti
                  :chartData="salesTrend"
                  height="32vh"
                  type="line"
                  :option="{ legend: { top: 'bottom' } }"
                />
              </a-card>
            </a-col>
            <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
              <a-card :bordered="false">
                <template #title>
                  <a-radio-group v-model:value="salesRankType" size="small">
                    <a-radio-button value="product">产品销量</a-radio-button>
                    <a-radio-button value="salesman">业务员</a-radio-button>
                  </a-radio-group>
                </template>
                <!-- 产品销量TOP5 -->
                <a-list v-if="salesRankType === 'product'" :data-source="productRank" size="small">
                  <template #renderItem="{ item, index }">
                    <a-list-item>
                      <a-list-item-meta>
                        <template #avatar>
                          <a-badge :count="index + 1" :number-style="{ backgroundColor: index < 3 ? '#ff4d4f' : '#999' }" />
                        </template>
                        <template #title>{{ item.productName }}</template>
                        <template #description>
                          销量: {{ item.qty }} | 金额: ¥{{ formatMoney(item.amount) }}
                        </template>
                      </a-list-item-meta>
                      <span class="rank-value">{{ item.qty }}件</span>
                    </a-list-item>
                  </template>
                </a-list>
                <!-- 业务员业绩TOP5 -->
                <a-list v-else :data-source="salesmanRank" size="small">
                  <template #renderItem="{ item, index }">
                    <a-list-item>
                      <a-list-item-meta>
                        <template #avatar>
                          <Avatar :style="{ backgroundColor: index < 3 ? themeColor : '#999' }">
                            {{ item.salesmanName?.charAt(0) || '?' }}
                          </Avatar>
                        </template>
                        <template #title>{{ item.salesmanName }}</template>
                        <template #description>
                          订单: {{ item.orderCount }}单 | 回款: ¥{{ formatMoney(item.collectionAmount) }}
                        </template>
                      </a-list-item-meta>
                      <span class="rank-value">¥{{ formatMoney(item.orderAmount) }}</span>
                    </a-list-item>
                  </template>
                </a-list>
              </a-card>
              <a-card title="今日订单状态" :bordered="false" class="mt-4">
                <div ref="salesPieRef" :style="{ height: '16vh', width: '100%' }"></div>
              </a-card>
            </a-col>
          </a-row>
        </a-tab-pane>

        <!-- ========== 综合看板 ========== -->
        <a-tab-pane tab="综合看板" key="overview">
          <a-row :gutter="16">
            <a-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24" class="mb-4">
              <a-card title="生产模块" :bordered="false" :body-style="{ padding: '12px' }">
                <div class="module-card">
                  <div class="module-item">
                    <span class="label">在制批次</span>
                    <span class="value text-orange">{{ overviewData.production.inProgress }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">今日完工</span>
                    <span class="value text-green">{{ overviewData.production.completed }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">待开工</span>
                    <span class="value">{{ overviewData.production.pending }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">延期批次</span>
                    <span class="value text-red">{{ overviewData.production.delayed }}</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24" class="mb-4">
              <a-card title="仓储模块" :bordered="false" :body-style="{ padding: '12px' }">
                <div class="module-card">
                  <div class="module-item">
                    <span class="label">库存总项</span>
                    <span class="value">{{ overviewData.warehouse.totalItems }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">库存预警</span>
                    <span class="value text-red">{{ overviewData.warehouse.warning }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">今日入库</span>
                    <span class="value text-green">{{ overviewData.warehouse.todayIn }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">今日出库</span>
                    <span class="value text-blue">{{ overviewData.warehouse.todayOut }}</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24" class="mb-4">
              <a-card title="销售模块" :bordered="false" :body-style="{ padding: '12px' }">
                <div class="module-card">
                  <div class="module-item">
                    <span class="label">本月订单</span>
                    <span class="value">{{ overviewData.sales.monthOrder }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">本月金额</span>
                    <span class="value text-blue">¥{{ formatMoney(overviewData.sales.monthAmount) }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">本月回款</span>
                    <span class="value text-green">¥{{ formatMoney(overviewData.sales.monthReceipt) }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">待发货</span>
                    <span class="value text-purple">{{ overviewData.sales.toDeliver }}</span>
                  </div>
                </div>
              </a-card>
            </a-col>
            <a-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24" class="mb-4">
              <a-card title="综合指标" :bordered="false" :body-style="{ padding: '12px' }">
                <div class="module-card">
                  <div class="module-item">
                    <span class="label">待审单据</span>
                    <span class="value text-red">{{ overviewData.total.pendingAudit }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">本月入库额</span>
                    <span class="value">¥{{ formatMoney(overviewData.total.inAmount) }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">本月出库额</span>
                    <span class="value">¥{{ formatMoney(overviewData.total.outAmount) }}</span>
                  </div>
                  <div class="module-item">
                    <span class="label">库存周转</span>
                    <span class="value text-green">{{ overviewData.total.turnover }}天</span>
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 第三行：待办提醒 + 快捷入口 -->
    <a-row :gutter="24" class="enter-y">
      <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
        <a-card title="待办提醒" :bordered="false">
          <a-list :data-source="pendingList" size="small">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #avatar>
                    <Badge :count="item.count" :overflow-count="99">
                      <Avatar :style="{ backgroundColor: pendingTypeMap[item.type]?.color || '#999' }">
                        <Icon :icon="getPendingIcon(item.type)" />
                      </Avatar>
                    </Badge>
                  </template>
                  <template #title>
                    <a @click="goPage(item)">{{ pendingTypeMap[item.type]?.label || item.type }}</a>
                  </template>
                  <template #description>
                    {{ item.description }}
                  </template>
                </a-list-item-meta>
                <span style="color: #999; font-size: 12px">{{ item.time }}</span>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
      <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
        <QuickNav :loading="loading" />
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { Badge, Avatar, Tag } from 'ant-design-vue';
  import { Icon } from '/@/components/Icon';
  import CoreIndicatorCard from '../components/CoreIndicatorCard.vue';
  import QuickNav from '../components/QuickNav.vue';
  import Bar from '/@/components/chart/Bar.vue';
  import BarMulti from '/@/components/chart/BarMulti.vue';
  import LineMulti from '/@/components/chart/LineMulti.vue';
  import { useECharts } from '/@/hooks/web/useECharts';
  import { useRootSetting } from '/@/hooks/setting/useRootSetting';
  import { getWarehouseData, getSalesSummary, getProductionData } from '../api';
  import { coreIndicatorList, pendingTypeMap } from '../data';
  import { useMessage } from '/@/hooks/web/useMessage';

  const router = useRouter();
  const { getThemeColor } = useRootSetting();
  const { createMessage } = useMessage();

  const loading = ref(true);
  const tabLoading = ref(false);
  const activeTab = ref('production');
  const dateRange = ref('week');
  const salesRankType = ref('product');
  const themeColor = computed(() => getThemeColor.value);

  // 核心指标
  const coreData = ref(coreIndicatorList);

  // 各模块原始数据
  const warehouseData = ref<any>({});
  const salesData = ref<any>({});
  const productionData = ref<any>({});

  // 组装后的图表数据
  const productionTrend = ref([]);
  const warehouseTrend = ref([]);
  const salesTrend = ref([]);
  const warningList = ref([]);
  const productRank = ref([]);
  const salesmanRank = ref([]);
  const pendingList = ref([]);

  // 综合看板数据
  const overviewData = ref({
    production: { inProgress: 0, completed: 0, pending: 0, delayed: 0 },
    warehouse: { totalItems: 0, warning: 0, todayIn: 0, todayOut: 0 },
    sales: { monthOrder: 0, monthAmount: 0, monthReceipt: 0, toDeliver: 0 },
    total: { pendingAudit: 0, inAmount: 0, outAmount: 0, turnover: 0 },
  });

  // 图表refs
  const batchPieRef = ref<HTMLDivElement | null>(null);
  const salesPieRef = ref<HTMLDivElement | null>(null);
  const { setOptions: setBatchPieOptions } = useECharts(batchPieRef as any);
  const { setOptions: setSalesPieOptions } = useECharts(salesPieRef as any);

  // 格式化金额
  function formatMoney(val: number) {
    if (!val) return '0';
    if (val >= 10000) return (val / 10000).toFixed(1) + '万';
    return val.toLocaleString();
  }

  // ========== 并发加载3个接口 ==========
  async function loadAllData() {
    loading.value = true;

    try {
      const [wmsRes, salesRes, prodRes] = await Promise.all([
        getWarehouseData({ dateRange: dateRange.value }).catch(() => null),
        getSalesSummary({ dateRange: dateRange.value }).catch(() => null),
        getProductionData({ dateRange: dateRange.value }).catch(() => null),
      ]);

      // 处理仓储数据
      if (wmsRes?.success || wmsRes?.stockWarningCount !== undefined) {
        warehouseData.value =  wmsRes.result || wmsRes;
        assembleWarehouseData();
      } else {
        initMockWarehouse();
      }

      // 处理销售数据
      if (salesRes?.success || salesRes?.monthOrderAmount !== undefined) {
        salesData.value =  salesRes.result || salesRes;
        assembleSalesData();
      } else {
        initMockSales();
      }

      // 处理生产数据
      if (prodRes?.success || prodRes?.runningBatchCount !== undefined) {
        productionData.value =  prodRes.result || prodRes;
        assembleProductionData();
      } else {
        initMockProduction();
      }

      assembleOverview();
      assemblePending();
      updateCoreIndicators();
      initCharts();

    } catch (e) {
      createMessage.error('加载数据失败');
      initMockAll();
    } finally {
      loading.value = false;
    }
  }

  // ========== 数据组装方法 ==========

  function assembleWarehouseData() {
    const d = warehouseData.value;

    // 出入库趋势：trendList → BarMulti格式
    if (d.trendList?.length) {
      warehouseTrend.value = [];
      d.trendList.forEach((item: any) => {
        const date = item.date?.substring(5) || item.date;
        warehouseTrend.value.push(
          { type: '入库', name: date, value: Number(item.inQty || 0) },
          { type: '出库', name: date, value: Number(item.outQty || 0) }
        );
      });
    }

    // 库存预警
    warningList.value = d.warningMaterialList || [];

    // 综合看板-仓储
    overviewData.value.warehouse = {
      totalItems: Number(d.stockTotalItems || 0),
      warning: Number(d.stockWarningCount || 0),
      todayIn: Number(d.todayInQty || 0),
      todayOut: Number(d.todayOutQty || 0),
    };
  }

  function assembleSalesData() {
    const d = salesData.value;

    // 销售额+回款趋势：monthTrend → LineMulti格式
    salesTrend.value = [];
    const trend = d.monthTrend || [];
    trend.forEach((item: any) => {
      const month = item.month || item.date;
      salesTrend.value.push(
        { name: month, type: '销售额', value: Number(item.amount || 0), color: '#eb2f96' },
        { name: month, type: '回款额', value: Number(item.receiptAmount || 0), color: '#52c41a' }
      );
    });

    // 产品销量TOP5：productTop5
    productRank.value = (d.productTop5 || []).map((item: any) => ({
      productName: item.productName || item.product_name,
      qty: Number(item.qty || item.totalQty || 0),
      amount: Number(item.amount || item.totalAmount || 0),
    }));

    // 业务员业绩TOP5：salesmanTop5（后端新增字段）
    salesmanRank.value = (d.salesmanTop5 || []).map((item: any) => ({
      salesmanName: item.salesmanName,
      orderCount: Number(item.orderCount || 0),
      orderAmount: Number(item.orderAmount || 0),
      collectionAmount: Number(item.collectionAmount || 0),
    }));

    // 综合看板-销售
    overviewData.value.sales = {
      monthOrder: Number(d.monthOrderCount || 0),
      monthAmount: Number(d.monthOrderAmount || 0),
      monthReceipt: Number(d.monthReceiptAmount || 0),
      toDeliver: Number(d.pendingDeliveryOrders?.length || 0),
    };
  }

  function assembleProductionData() {
    const d = productionData.value;

    // 生产趋势：weekTrend → Bar格式
    productionTrend.value = (d.weekTrend || []).map((item: any) => ({
      name: item.day || item.date || item.name,
      value: Number(item.actualQty || item.value || 0),
    }));

    // 综合看板-生产
    overviewData.value.production = {
      inProgress: Number(d.runningBatchCount || 0),
      completed: Number(d.monthActualQty || 0),
      pending: Number(d.pendingBatchCount || 0),
      delayed: 0, // 后端暂无，先给0
    };
  }

  function assembleOverview() {
    const w = warehouseData.value;
    const s = salesData.value;
    const p = productionData.value;

    // 待审单据汇总
    const pendingAudit =
      Number(w?.pendingMaterialInCount || 0) +
      Number(w?.pendingProductInCount || 0) +
      Number(w?.pendingMaterialOutCount || 0) +
      Number(w?.pendingProductOutCount || 0) +
      Number(s?.pendingQuoteCount || 0) +
      Number(s?.pendingOrderCount || 0) +
      Number(p?.pendingTaskCount || 0);

    overviewData.value.total = {
      pendingAudit,
      inAmount: Number(w?.monthInAmount || 0),
      outAmount: Number(w?.monthOutAmount || 0),
      turnover: 0, // 后端暂无
    };
  }

  function assemblePending() {
    const w = warehouseData.value;
    const s = salesData.value;
    const p = productionData.value;
    const list = [];

    if (Number(w?.pendingMaterialInCount) > 0) {
      list.push({ type: 'STOCK_IN', count: Number(w.pendingMaterialInCount), description: '材料入库待审核', time: '实时' });
    }
    if (Number(w?.pendingProductInCount) > 0) {
      list.push({ type: 'STOCK_IN', count: Number(w.pendingProductInCount), description: '产品入库待审核', time: '实时' });
    }
    if (Number(w?.pendingMaterialOutCount) > 0) {
      list.push({ type: 'STOCK_OUT', count: Number(w.pendingMaterialOutCount), description: '材料出库待审核', time: '实时' });
    }
    if (Number(w?.pendingProductOutCount) > 0) {
      list.push({ type: 'STOCK_OUT', count: Number(w.pendingProductOutCount), description: '产品出库待审核', time: '实时' });
    }
    if (Number(s?.pendingQuoteCount) > 0) {
      list.push({ type: 'QUOTE', count: Number(s.pendingQuoteCount), description: '报价单待审核', time: '实时' });
    }
    if (Number(s?.pendingOrderCount) > 0) {
      list.push({ type: 'SALES_ORDER', count: Number(s.pendingOrderCount), description: '销售订单待审核', time: '实时' });
    }
    if (Number(p?.pendingTaskCount) > 0) {
      list.push({ type: 'PRODUCTION', count: Number(p.pendingTaskCount), description: '工单待派工', time: '实时' });
    }

    pendingList.value = list.length > 0 ? list : [
      { type: 'STOCK_IN', count: 3, description: '入库单待审核', time: '10分钟前' },
      { type: 'STOCK_OUT', count: 5, description: '出库单待审核', time: '30分钟前' },
      { type: 'SALES_ORDER', count: 2, description: '销售订单待审核', time: '1小时前' },
    ];
  }

  function updateCoreIndicators() {
    coreData.value = [
      { ...coreIndicatorList[0], value: overviewData.value.warehouse.todayIn, total: overviewData.value.total.inAmount },
      { ...coreIndicatorList[1], value: overviewData.value.warehouse.todayOut, total: overviewData.value.total.outAmount },
      { ...coreIndicatorList[2], value: overviewData.value.production.inProgress, total: overviewData.value.production.completed },
      { ...coreIndicatorList[3], value: overviewData.value.total.pendingAudit, total: 0 },
    ];
  }

  // ========== 模拟数据（接口未就绪时降级）==========

  function initMockWarehouse() {
    warehouseTrend.value = [];
    for (let i = 0; i < 7; i++) {
      warehouseTrend.value.push(
        { type: '入库', name: `${i + 1}日`, value: Math.floor(Math.random() * 100) + 20 },
        { type: '出库', name: `${i + 1}日`, value: Math.floor(Math.random() * 80) + 15 }
      );
    }
    warningList.value = [
      { materialName: 'PVC原料-白色', availableQty: 50, safetyStock: 200, shortageQty: 150, warningType: '0' },
      { materialName: '纸箱-小号', availableQty: 120, safetyStock: 500, shortageQty: 380, warningType: '1' },
    ];
    overviewData.value.warehouse = { totalItems: 1286, warning: 15, todayIn: 23, todayOut: 18 };
  }

  function initMockSales() {
    salesTrend.value = [];
    for (let i = 0; i < 7; i++) {
      salesTrend.value.push(
        { name: `${i + 1}日`, type: '销售额', value: Math.floor(Math.random() * 50) + 10, color: '#eb2f96' },
        { name: `${i + 1}日`, type: '回款额', value: Math.floor(Math.random() * 40) + 5, color: '#52c41a' }
      );
    }
    productRank.value = [
      { productName: '产品A-型号X1', qty: 1250, amount: 625000 },
      { productName: '产品B-型号Y2', qty: 980, amount: 490000 },
    ];
    salesmanRank.value = [
      { salesmanName: '张三', orderCount: 45, orderAmount: 850000, collectionAmount: 720000 },
      { salesmanName: '李四', orderCount: 38, orderAmount: 620000, collectionAmount: 580000 },
    ];
    overviewData.value.sales = { monthOrder: 156, monthAmount: 2850000, monthReceipt: 2100000, toDeliver: 23 };
  }

  function initMockProduction() {
    productionTrend.value = Array.from({ length: 7 }, (_, i) => ({
      name: `${i + 1}日`, value: Math.floor(Math.random() * 50) + 10,
    }));
    overviewData.value.production = { inProgress: 35, completed: 12, pending: 8, delayed: 0 };
  }

  function initMockAll() {
    initMockWarehouse();
    initMockSales();
    initMockProduction();
    assembleOverview();
    assemblePending();
    updateCoreIndicators();
    initCharts();
  }

  // ========== 图表初始化 ==========

  function initCharts() {
    // 批次状态饼图
    setBatchPieOptions({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
        data: productionData.value?.taskStatusDist?.map((item: any) => ({
          name: item.status || item.name,
          value: Number(item.count || item.value || 0),
          itemStyle: { color: getStatusColor(item.status) }
        })) || [
          { name: '待派工', value: 12, itemStyle: { color: '#999' } },
          { name: '已派工', value: 35, itemStyle: { color: themeColor.value } },
          { name: '进行中', value: 8, itemStyle: { color: '#faad14' } },
          { name: '已完成', value: 20, itemStyle: { color: '#52c41a' } },
        ]
      }]
    });

    // 销售订单状态饼图
    setSalesPieOptions({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0, itemWidth: 10, itemHeight: 10 },
      series: [{
        type: 'pie',
        radius: ['30%', '60%'],
        center: ['50%', '45%'],
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { show: true, formatter: '{b}: {c}' },
        data: [
          { name: '待审核', value: 8, itemStyle: { color: '#faad14' } },
          { name: '已审核', value: 15, itemStyle: { color: '#52c41a' } },
          { name: '发货中', value: 12, itemStyle: { color: '#1890ff' } },
          { name: '已完成', value: 6, itemStyle: { color: '#999' } },
        ]
      }]
    });
  }

  function getStatusColor(status: string) {
    const map: Record<string, string> = {
      'PENDING': '#999',
      'ASSIGNED': themeColor.value,
      'PROCESSING': '#faad14',
      'COMPLETED': '#52c41a',
    };
    return map[status] || '#999';
  }

  function getPendingIcon(type: string) {
    const iconMap = {
      'STOCK_IN': 'ant-design:import-outlined',
      'STOCK_OUT': 'ant-design:export-outlined',
      'PRODUCTION': 'ant-design:build-outlined',
      'QUOTE': 'ant-design:dollar-outlined',
      'SALES_ORDER': 'ant-design:shopping-outlined',
      'DELIVERY': 'ant-design:car-outlined',
    };
    return iconMap[type] || 'ant-design:bell-outlined';
  }

  function goPage(item: any) {
    const link = pendingTypeMap[item.type]?.link || '/';
    if (link) router.push(link);
  }

  function handleDateChange() {
    loadAllData();
  }

  onMounted(() => {
    loadAllData();
  });
</script>

<style lang="less" scoped>
  .dashboard-container {
    :deep(.ant-card) { border-radius: 8px; }
    :deep(.ant-list-item) { padding: 10px 0; }
  }

  .module-card {
    .module-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px solid #f0f0f0;
      &:last-child { border-bottom: none; }
      .label { color: rgba(0, 0, 0, 0.45); font-size: 14px; }
      .value {
        font-size: 18px; font-weight: 600; color: #262626;
        &.text-green { color: #52c41a; }
        &.text-red { color: #ff4d4f; }
        &.text-orange { color: #faad14; }
        &.text-blue { color: #1890ff; }
        &.text-purple { color: #722ed1; }
      }
    }
  }

  .rank-value { font-weight: 600; color: #262626; }
</style>

<template>
  <div class="warehouse-dashboard">
    <!-- 顶部统计卡片 -->
    <a-row :gutter="[16, 16]">
      <!-- 材料入库审核 -->
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="待材料入库"
          :value="data.pendingMaterialInCount"
          color="#1890ff"
          icon="InboxOutlined"
          :link="{ path: '/mis/wms/MaterialInApprove/MaterialInApproveList', query: { approveStatus: '0' }}"
        />
      </a-col>

      <!-- 产品入库审核 -->
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="待产品入库"
          :value="data.pendingProductInCount"
          color="#52c41a"
          icon="InboxOutlined"
          :link="{ path: '/mis/wms/ProductInApprove/ProductInApproveList', query: { approveStatus: '0' }}"
        />
      </a-col>

      <!-- 材料出库审核 -->
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="待材料出库"
          :value="data.pendingMaterialOutCount"
          color="#faad14"
          icon="ExportOutlined"
          :link="{ path: '/mis/wms/MaterialOutApprove/MaterialOutApproveList', query: { approveStatus: '0' }}"
        />
      </a-col>

      <!-- 产品出库审核 -->
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="待产品出库"
          :value="data.pendingProductOutCount"
          color="#fa8c16"
          icon="ExportOutlined"
          :link="{ path: '/mis/wms/ProductOutApprove/ProductOutApproveList', query: { approveStatus: '0' }}"
        />
      </a-col>

      <!-- 库存预警 -->
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="库存预警"
          :value="data.stockWarningCount"
          color="#ff4d4f"
          icon="WarningOutlined"
          :link="{ path: '/mis/wms/stock/StockList' }"
        />
      </a-col>

      <!-- 库存锁定 -->
      <a-col :xs="24" :sm="12" :md="8" :lg="4" :xl="4">
        <StatCard
          title="库存锁定"
          :value="data.lockedQty"
          color="#eb2f96"
          icon="LockOutlined"
          suffix="件"
        />
      </a-col>
    </a-row>

    <!-- 快捷操作 -->
    <a-row :gutter="[16, 16]" style="margin-top: 16px;">
      <a-col :span="24">
        <a-card title="快捷操作" :bordered="false" size="small">
          <a-space>
            <a-button type="primary" @click="handleQuick('materialIn')">
              <PlusOutlined /> 材料入库
            </a-button>
            <a-button type="primary" @click="handleQuick('productIn')">
              <PlusOutlined /> 产品入库
            </a-button>
            <a-button type="primary" @click="handleQuick('materialOut')">
              <MinusOutlined /> 材料出库
            </a-button>
            <a-button type="primary" @click="handleQuick('productOut')">
              <MinusOutlined /> 产品出库
            </a-button>
            <a-button @click="handleQuick('print')">
              <PrinterOutlined /> 标签打印
            </a-button>
            <a-button @click="handleQuick('scan')">
              <ScanOutlined /> 扫码发货
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表 + 待办 -->
    <a-row :gutter="[16, 16]" style="margin-top: 16px;">
      <a-col :xs="24" :lg="16">
        <a-card title="近7天出入库趋势" :bordered="false" :loading="loading" style="min-height: 400px;">
          <div ref="trendChartRef" style="height: 300px;"></div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="8">
        <a-card title="待审核入库单" :bordered="false" :loading="loading" style="min-height: 400px;">
          <template #extra>
            <a @click="goToList">查看更多</a>
          </template>
          <a-list :data-source="data.pendingInList" size="small">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #title>
                    <a @click="handleView(item)">{{ item.stockInNo }}</a>
                    <a-tag color="orange" style="margin-left: 8px;">待审核</a-tag>
                  </template>
                  <template #description>
                    {{ item.supplierName }} | {{ item.stockInType_dictText }} | {{ formatDate(item.createTime) }}
                  </template>
                </a-list-item-meta>
                <div style="color: #1890ff; font-weight: bold;">
                  ¥{{ formatMoney(item.totalAmount) }}
                </div>
              </a-list-item>
            </template>
            <template #empty>
              <a-empty description="暂无待审核入库单" :image="simpleImage" />
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>

    <!-- 预警区块 -->
    <a-row :gutter="[16, 16]" style="margin-top: 16px;">
      <!-- 库存预警（缺货/积压） -->
      <a-col :xs="24" :lg="12">
        <a-card title="库存预警物料 Top5" :bordered="false" :loading="loading" style="min-height: 400px;">
          <a-table
            :dataSource="data.warningMaterialList"
            :columns="warningColumns"
            :pagination="false"
            size="small"
            rowKey="materialId"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'warningType'">
                <a-tag v-if="record.warningType === '0'" color="red">缺货</a-tag>
                <a-tag v-else-if="record.warningType === '1'" color="orange">积压</a-tag>
                <a-tag v-else-if="record.warningType === '2'" color="blue">近效期</a-tag>
                <a-tag v-else color="green">正常</a-tag>
              </template>
              <template v-else-if="column.key === 'shortageQty'">
                <span v-if="record.warningType === '0'" style="color: #ff4d4f; font-weight: bold;">
                  {{ record.shortageQty }}
                </span>
                            <span v-else-if="record.warningType === '1'" style="color: #faad14; font-weight: bold;">
                  +{{ record.shortageQty }}
                </span>
                            <span v-else style="color: #999;">-</span>
                          </template>
                          <template v-else-if="column.key === 'nearestExpiryDate'">
                <span :style="{ color: record.remainDays <= 7 ? '#ff4d4f' : '#faad14' }">
                  {{ record.nearestExpiryDate || '-' }}
                </span>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>

      <!-- 效期预警 -->
      <a-col :xs="24" :lg="12">
        <a-card title="效期预警批次" :bordered="false" :loading="loading" style="min-height: 400px;">
          <template #extra>
            <a-tag color="blue">{{ data.expiryAlertList?.length || 0 }} 批次</a-tag>
          </template>
          <a-table
            :dataSource="data.expiryAlertList"
            :columns="expiryColumns"
            :pagination="false"
            size="small"
            rowKey="stockId"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'remainDays'">
                <a-tag :color="getExpiryColor(record.remainDays)">
                  {{ record.remainDays }}天
                </a-tag>
              </template>
              <template v-else-if="column.key === 'location'">
                {{ record.warehouseId || '-' }}/{{ record.areaId || '-' }}/{{ record.shelfId || '-' }}
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, nextTick } from 'vue';
  import { useRouter } from 'vue-router';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { defHttp } from '/@/utils/http/axios';
  import { Empty } from 'ant-design-vue';
  import * as echarts from 'echarts';
  import {
    InboxOutlined, ExportOutlined, WarningOutlined, PlusOutlined,
    MinusOutlined, LockOutlined, PrinterOutlined, ScanOutlined,
  } from '@ant-design/icons-vue';
  import StatCard from './components/StatCard.vue';

  const router = useRouter();
  const { createMessage } = useMessage();
  const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;

  const loading = ref(false);
  const data = ref<any>({
    pendingMaterialInCount: 0,
    pendingProductInCount: 0,
    pendingMaterialOutCount: 0,
    pendingProductOutCount: 0,
    stockWarningCount: 0,
    lockedQty: 0,
    todayInQty: 0,
    todayOutQty: 0,
    trendList: [],
    pendingInList: [],
    warningMaterialList: [],
    expiryAlertList: [],
  });

  const trendChartRef = ref<HTMLElement>();
  let trendChart: echarts.ECharts | null = null;

  const warningColumns = [
    { title: '物料编码', dataIndex: 'materialCode', key: 'materialCode', width: 110 },
    { title: '物料名称', dataIndex: 'materialName', key: 'materialName', ellipsis: true },
    { title: '规格', dataIndex: 'spec', key: 'spec', width: 90 },
    { title: '可用库存', dataIndex: 'availableQty', key: 'availableQty', width: 90, align: 'center' },
    { title: '预警类型', key: 'warningType', width: 90, align: 'center' },
    { title: '缺口/超量', key: 'shortageQty', width: 90, align: 'center' },
    { title: '最近效期', dataIndex: 'nearestExpiryDate', key: 'nearestExpiryDate', width: 110, align: 'center' },
  ];

  const expiryColumns = [
    { title: '物料编码', dataIndex: 'materialCode', key: 'materialCode', width: 110 },
    { title: '物料名称', dataIndex: 'materialName', key: 'materialName', ellipsis: true },
    { title: '批次号', dataIndex: 'batchNo', key: 'batchNo', width: 120 },
    { title: '数量', dataIndex: 'quantity', key: 'quantity', width: 80, align: 'center' },
    { title: '剩余天数', key: 'remainDays', width: 90, align: 'center' },
    { title: '效期', dataIndex: 'expiryDate', key: 'expiryDate', width: 110, align: 'center' },
    { title: '库位', key: 'location', width: 140 },
  ];

  async function loadData() {
    loading.value = true;
    try {
      const res = await defHttp.get({ url: '/wms/dashboard/warehouse' });
      console.log("res:",res)
      if (res) {
       // data.value = res.result;
        const result = res;
        console.log("result:",result)
        data.value = res;
        // // 逐个赋值，确保响应式
        // data.value.pendingMaterialInCount = result.pendingMaterialInCount || 0;
        // data.value.pendingProductInCount = result.pendingProductInCount || 0;
        // data.value.pendingMaterialOutCount = result.pendingMaterialOutCount || 0;
        // data.value.pendingProductOutCount = result.pendingProductOutCount || 0;
        // data.value.stockWarningCount = result.stockWarningCount || 0;
        // data.value.lockedQty = result.lockedQty || 0;
        // data.value.todayInQty = result.todayInQty || 0;
        // data.value.todayOutQty = result.todayOutQty || 0;
        // data.value.trendList = result.trendList || [];
        // data.value.pendingInList = result.pendingInList || [];
        // data.value.warningMaterialList = result.warningMaterialList || [];
        // data.value.expiryAlertList = result.expiryAlertList || [];
        nextTick(() => initTrendChart());
      }
    } catch (error) {
      createMessage.error('加载仪表盘数据失败');
    } finally {
      loading.value = false;
    }
  }

  function initTrendChart() {
    if (!trendChartRef.value || !data.value.trendList?.length) return;
    if (trendChart) trendChart.dispose();

    trendChart = echarts.init(trendChartRef.value);
    const dates = data.value.trendList.map((item: any) => item.date?.substring(5));
    const inData = data.value.trendList.map((item: any) => item.inQty || 0);
    const outData = data.value.trendList.map((item: any) => item.outQty || 0);

    trendChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
      legend: { data: ['入库', '出库'], bottom: 0 },
      grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value', name: '数量' },
      series: [
        { name: '入库', type: 'bar', data: inData, itemStyle: { color: '#1890ff' }, barWidth: '30%' },
        { name: '出库', type: 'bar', data: outData, itemStyle: { color: '#52c41a' }, barWidth: '30%' },
      ]
    });
  }

  function handleQuick(type: string) {
    const routes: Record<string, string> = {
      materialIn: '/mis/wms/MaterialIn/StockInList',
      productIn: '/mis/wms/ProductIn/ProductInList',
      materialOut: '/mis/wms/MaterialOut/StockOutList',
      productOut: '/mis/wms/ProductOut/ProductOutList',
      print: '/mis/mes/labelPrintTask',
      scan: '/mis/wms/DeliveryTask/DeliveryTaskList',
    };
    if (routes[type]) router.push(routes[type]);
  }

  function handleView(record: any) {
    router.push({ path: '/mis/wms/MaterialInApprove/MaterialInApproveList', query: { id: record.id } });
  }

  function goToList() {
    router.push({ path: '/mis/wms/MaterialInApprove/MaterialInApproveList', query: { approveStatus: '0' } });
  }

  function formatDate(date: string) {
    return date ? date.substring(0, 16).replace('T', ' ') : '-';
  }

  function formatMoney(amount: number) {
    return amount ? Number(amount).toFixed(2) : '0.00';
  }

  function getWarningColor(type: string) {
    return { '0': '#ff4d4f', '1': '#faad14', '2': '#1890ff' }[type] || 'default';
  }
  function getWarningText(type: string) {
    return { '0': '缺货', '1': '积压', '2': '近效期' }[type] || '-';
  }

  function getExpiryColor(days: number) {
    if (days <= 7) return 'red';
    if (days <= 15) return 'orange';
    return 'blue';
  }

  function handleResize() {
    trendChart?.resize();
  }

  onMounted(() => {
    loadData();
    window.addEventListener('resize', handleResize);
  });

  onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
    trendChart?.dispose();
  });
</script>

<style scoped lang="less">
  .warehouse-dashboard {
    padding: 16px;
    :deep(.ant-card) { border-radius: 8px; }
  }
</style>

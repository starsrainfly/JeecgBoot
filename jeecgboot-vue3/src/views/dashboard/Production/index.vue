<template>
  <div class="dashboard-container">
    <!-- 视图切换 -->
    <a-row class="view-toggle-row">
      <a-col :span="24" style="text-align: right; margin-bottom: 8px;">
        <a-radio-group v-model:value="viewType" @change="handleViewChange">
          <a-radio-button value="manager">管理者视图</a-radio-button>
          <a-radio-button value="worker">工人视图</a-radio-button>
        </a-radio-group>
      </a-col>
    </a-row>

    <!-- 指标卡片 -->
    <a-row :gutter="16" class="metric-row">
      <template v-if="isManagerView">
        <a-col :xs="24" :sm="12" :md="8" :lg="4" v-for="item in managerMetrics" :key="item.key">
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
      </template>
      <template v-else>
        <a-col :xs="24" :sm="12" :md="8" :lg="8" v-for="item in workerMetrics" :key="item.key">
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
      </template>
    </a-row>

    <!-- 快捷操作 -->
    <a-row :gutter="16" class="action-row">
      <a-col :span="24">
        <a-card title="快捷操作" size="small">
          <a-space>
            <template v-if="isManagerView">
              <a-button type="primary" @click="goTo('/mis/mes/productionplan')">
                <Icon icon="ant-design:calendar-outlined" /> 生产计划
              </a-button>
              <a-button type="primary" @click="goTo('/mis/mes/productionOrder')">
                <Icon icon="ant-design:profile-outlined" /> 生产订单
              </a-button>
              <a-button @click="goTo('/mis/mes/productionBatch')">
                <Icon icon="ant-design:appstore-outlined" /> 生产批次
              </a-button>
              <a-button @click="goTo('/mmis/mes/productionTask')">
                <Icon icon="ant-design:tool-outlined" /> 派工管理
              </a-button>
              <a-button @click="goTo('/mis/mes/productionMaterial')">
                <Icon icon="ant-design:export-outlined" /> 物料需求
              </a-button>
              <a-button @click="goTo('/mis/mes/labelPrintTask')">
                <Icon icon="ant-design:eye-outlined" /> 标签打印
              </a-button>
            </template>
            <template v-else>
              <a-button type="primary" @click="goTo('/mis/mes/myTask')">
                <Icon icon="ant-design:unordered-list-outlined" /> 我的工单
              </a-button>

            </template>
          </a-space>
        </a-card>
      </a-col>
    </a-row>

    <!-- 管理者视图内容 -->
    <template v-if="isManagerView">
      <a-row :gutter="16" class="content-row">
        <!-- 左侧：生产进度 -->
        <a-col :xs="24" :lg="12">
          <a-card title="生产进度" size="small" class="todo-card">
            <a-tabs v-model:activeKey="activeTab">
              <a-tab-pane key="today" tab="今日待开工">
                <a-table
                  :dataSource="dashboardData.todayBatches"
                  :columns="todayColumns"
                  size="small"
                  :pagination="false"
                />
              </a-tab-pane>
              <a-tab-pane key="running" tab="进行中">
                <a-table
                  :dataSource="dashboardData.runningBatches"
                  :columns="runningColumns"
                  size="small"
                  :pagination="false"
                >
                  <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'progress'">
                      <a-progress :percent="record.progress" size="small" />
                    </template>
                    <template v-if="column.key === 'status'">
                      <a-tag :color="getBatchStatusColor(record.status)">
                        {{ getBatchStatusText(record.status) }}
                      </a-tag>
                    </template>
                  </template>
                </a-table>
              </a-tab-pane>
              <a-tab-pane key="weigh" tab="待配料">
                <a-table
                  :dataSource="dashboardData.pendingWeighBatches"
                  :columns="weighColumns"
                  size="small"
                  :pagination="false"
                />
              </a-tab-pane>
              <a-tab-pane key="task" tab="待派工">
                <a-table
                  :dataSource="dashboardData.pendingTasks"
                  :columns="taskColumns"
                  size="small"
                  :pagination="false"
                />
              </a-tab-pane>
            </a-tabs>
          </a-card>
        </a-col>

        <!-- 右侧：图表 -->
        <a-col :xs="24" :lg="12">
          <a-card title="近7天产量趋势" size="small" class="chart-card">
            <div ref="weekTrendRef" style="height: 320px;"></div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 底部：预警 + 分布 -->
      <a-row :gutter="16" class="bottom-row">
        <a-col :xs="24" :lg="12">
          <a-card title="预警信息" size="small">
            <a-tabs size="small">
              <a-tab-pane key="safety" tab="安全库存">
                <a-table
                  :dataSource="dashboardData.safetyStockWarnings"
                  :columns="safetyColumns"
                  size="small"
                  :pagination="false"
                >
                  <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'shortageQty'">
                      <span style="color: #f5222d;">缺{{ record.shortageQty }}</span>
                    </template>
                  </template>
                </a-table>
              </a-tab-pane>
              <a-tab-pane key="equipment" tab="设备异常">
                <a-table
                  :dataSource="dashboardData.equipmentWarnings"
                  :columns="equipmentColumns"
                  size="small"
                  :pagination="false"
                >
                  <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'status'">
                      <a-tag color="red">异常</a-tag>
                    </template>
                  </template>
                </a-table>
              </a-tab-pane>
            </a-tabs>
          </a-card>
        </a-col>
        <a-col :xs="24" :lg="12">
          <a-row :gutter="16">
            <a-col :span="24">
              <a-card title="本月产品产量分布" size="small">
                <div ref="productDistRef" style="height: 200px;"></div>
              </a-card>
            </a-col>
            <a-col :span="24" style="margin-top: 16px;">
              <a-card title="本月工单状态分布" size="small">
                <div ref="taskStatusRef" style="height: 200px;"></div>
              </a-card>
            </a-col>
          </a-row>
        </a-col>
      </a-row>
    </template>

    <!-- 工人视图内容 -->
    <template v-else>
      <a-row :gutter="16">
        <a-col :span="24">
          <a-card title="我的工单" size="small">
            <a-table
              :dataSource="dashboardData.myTasks"
              :columns="myTaskColumns"
              size="small"
              :pagination="{ pageSize: 10 }"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'status'">
                  <a-tag :color="record.status === 'ASSIGNED' ? 'blue' : 'orange'">
                    {{ record.status === 'ASSIGNED' ? '已派工' : '进行中' }}
                  </a-tag>
                </template>
                <template v-if="column.key === 'action'">
                  <a-button type="link" size="small" @click="handleTask(record)">
                    处理
                  </a-button>
                </template>
              </template>
            </a-table>
          </a-card>
        </a-col>
      </a-row>
    </template>
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

  const viewType = ref('manager');
  const activeTab = ref('today');
  const dashboardData = ref<any>({});
  const weekTrendRef = ref<HTMLDivElement>();
  const productDistRef = ref<HTMLDivElement>();
  const taskStatusRef = ref<HTMLDivElement>();

  let weekTrendChart: echarts.ECharts;
  let productDistChart: echarts.ECharts;
  let taskStatusChart: echarts.ECharts;

  const isManagerView = computed(() => viewType.value === 'manager');

  // 批次状态映射
  const batchStatusMap: Record<string, { text: string; color: string }> = {
    PENDING: { text: '待配料', color: 'default' },
    WEIGHING: { text: '配料中', color: 'processing' },
    WEIGHED: { text: '配料完成', color: 'success' },
    PRODUCING: { text: '生产中', color: 'warning' },
    COMPLETED: { text: '完成', color: 'success' },
  };

  const getBatchStatusText = (status: string) => batchStatusMap[status]?.text || status;
  const getBatchStatusColor = (status: string) => batchStatusMap[status]?.color || 'default';

  // 管理者指标
  const managerMetrics = computed(() => [
    {
      key: 'monthPlanQty',
      label: '本月计划产量',
      value: `${dashboardData.value.monthPlanQty || '0.00'} kg`,
      icon: 'ant-design:calendar-outlined',
      color: '#1890ff'
    },
    {
      key: 'monthActualQty',
      label: '本月实际产量',
      value: `${dashboardData.value.monthActualQty || '0.00'} kg`,
      icon: 'ant-design:check-circle-outlined',
      color: '#52c41a'
    },
    {
      key: 'completionRate',
      label: '完工率',
      value: `${dashboardData.value.completionRate || '0.00'}%`,
      icon: 'ant-design:percentage-outlined',
      color: '#722ed1'
    },
    {
      key: 'runningBatchCount',
      label: '进行中批次',
      value: dashboardData.value.runningBatchCount || 0,
      icon: 'ant-design:loading-outlined',
      color: '#faad14'
    },
    {
      key: 'pendingBatchCount',
      label: '待配料批次',
      value: dashboardData.value.pendingBatchCount || 0,
      icon: 'ant-design:pause-circle-outlined',
      color: '#eb2f96'
    },
    {
      key: 'pendingTaskCount',
      label: '待派工工单',
      value: dashboardData.value.pendingTaskCount || 0,
      icon: 'ant-design:tool-outlined',
      color: '#13c2c2'
    },
  ]);

  // 工人指标
  const workerMetrics = computed(() => [
    {
      key: 'myMonthActualQty',
      label: '我的本月产量',
      value: `${dashboardData.value.myMonthActualQty || '0.00'} kg`,
      icon: 'ant-design:trophy-outlined',
      color: '#52c41a'
    },
    {
      key: 'myPendingTaskCount',
      label: '我的待办工单',
      value: dashboardData.value.myPendingTaskCount || 0,
      icon: 'ant-design:clock-circle-outlined',
      color: '#faad14'
    },
    {
      key: 'myCompletedTaskCount',
      label: '本月已完成',
      value: dashboardData.value.myCompletedTaskCount || 0,
      icon: 'ant-design:check-square-outlined',
      color: '#1890ff'
    },
  ]);

  // 表格列定义
  const todayColumns = [
    { title: '批次号', dataIndex: 'batchNo', key: 'batchNo', width: 130 },
    { title: '产品', dataIndex: 'productName', key: 'productName', ellipsis: true },
    { title: '计划量(kg)', dataIndex: 'plannedQty', key: 'plannedQty', width: 110, align: 'right' },
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 130 },
  ];

  const runningColumns = [
    { title: '批次号', dataIndex: 'batchNo', key: 'batchNo', width: 130 },
    { title: '产品', dataIndex: 'productName', key: 'productName', ellipsis: true },
    { title: '计划/实际(kg)', key: 'qty', width: 130,
      customRender: ({ record }: any) => `${record.actualQty || 0}/${record.plannedQty}` },
    { title: '状态', key: 'status', width: 100 },
    { title: '进度', key: 'progress', width: 120 },
  ];

  const weighColumns = [
    { title: '批次号', dataIndex: 'batchNo', key: 'batchNo', width: 130 },
    { title: '产品', dataIndex: 'productName', key: 'productName', ellipsis: true },
    { title: '计划量(kg)', dataIndex: 'plannedQty', key: 'plannedQty', width: 110, align: 'right' },
    { title: '工艺', dataIndex: 'routingName', key: 'routingName', width: 120 },
    { title: '生产日期', dataIndex: 'productionDate', key: 'productionDate', width: 120 },
  ];

  const taskColumns = [
    { title: '工单号', dataIndex: 'taskNo', key: 'taskNo', width: 130 },
    { title: '批次', dataIndex: 'batchNo', key: 'batchNo', width: 120 },
    { title: '产品', dataIndex: 'productName', key: 'productName', ellipsis: true },
    { title: '工序', dataIndex: 'stepName', key: 'stepName', width: 120 },
    { title: '设备', dataIndex: 'equipmentName', key: 'equipmentName', width: 120 },
    { title: '计划耗时(分)', dataIndex: 'planDuration', key: 'planDuration', width: 110, align: 'right' },
  ];

  const safetyColumns = [
    { title: '物料编码', dataIndex: 'materialCode', key: 'materialCode', width: 120 },
    { title: '物料名称', dataIndex: 'materialName', key: 'materialName', ellipsis: true },
    { title: '规格', dataIndex: 'goodsSpec', key: 'goodsSpec', width: 120 },
    { title: '当前库存', dataIndex: 'currentQty', key: 'currentQty', width: 100, align: 'right' },
    { title: '安全库存', dataIndex: 'safetyStockQty', key: 'safetyStockQty', width: 100, align: 'right' },
    { title: '缺口', key: 'shortageQty', width: 100, align: 'right' },
  ];

  const equipmentColumns = [
    { title: '设备编号', dataIndex: 'equipmentCode', key: 'equipmentCode', width: 120 },
    { title: '设备名称', dataIndex: 'equipmentName', key: 'equipmentName', ellipsis: true },
    { title: '类型', dataIndex: 'equipmentType', key: 'equipmentType', width: 100 },
    { title: '位置', dataIndex: 'location', key: 'location', width: 120 },
    { title: '状态', key: 'status', width: 80, align: 'center' },
  ];

  const myTaskColumns = [
    { title: '工单号', dataIndex: 'taskNo', key: 'taskNo', width: 130 },
    { title: '批次', dataIndex: 'batchNo', key: 'batchNo', width: 120 },
    { title: '产品', dataIndex: 'productName', key: 'productName', ellipsis: true },
    { title: '颜色', dataIndex: 'productColor', key: 'productColor', width: 80 },
    { title: '工序', dataIndex: 'stepName', key: 'stepName', width: 120 },
    { title: '操作要求', dataIndex: 'stepDesc', key: 'stepDesc', ellipsis: true },
    { title: '设备', dataIndex: 'equipmentName', key: 'equipmentName', width: 120 },
    { title: '计划耗时(分)', dataIndex: 'planDuration', key: 'planDuration', width: 110, align: 'right' },
    { title: '状态', key: 'status', width: 100, align: 'center' },
    { title: '操作', key: 'action', width: 80, align: 'center', fixed: 'right' },
  ];

  // 加载数据
  const loadData = async () => {
    try {
      const res = await defHttp.get({
        url: '/mes/productionDashboard/data',
        params: { viewType: viewType.value }
      });
      if (res) {
        dashboardData.value = res;
        if (isManagerView.value) {
          nextTick(() => {
            initCharts();
          });
        }
      }
    } catch (error) {
      createMessage.error('加载数据失败');
    }
  };

  const handleViewChange = () => {
    loadData();
  };

  const initCharts = () => {
    // 周趋势图
    if (weekTrendRef.value) {
      weekTrendChart = echarts.init(weekTrendRef.value);
      const trendData = dashboardData.value.weekTrend || [];
      weekTrendChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: trendData.map((item: any) => item.day)
        },
        yAxis: { type: 'value', name: '产量(kg)' },
        series: [{
          type: 'line',
          data: trendData.map((item: any) => item.actualQty),
          smooth: true,
          areaStyle: { opacity: 0.2, color: '#52c41a' },
          itemStyle: { color: '#52c41a' }
        }]
      });
    }

    // 产品分布图
    if (productDistRef.value) {
      productDistChart = echarts.init(productDistRef.value);
      const distData = dashboardData.value.productDist || [];
      productDistChart.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: '65%',
          data: distData.map((item: any) => ({
            name: item.productName,
            value: item.actualQty
          })),
          label: { formatter: '{b}: {c}kg' }
        }]
      });
    }

    // 工单状态分布
    if (taskStatusRef.value) {
      taskStatusChart = echarts.init(taskStatusRef.value);
      const statusData = dashboardData.value.taskStatusDist || [];
      const statusMap: Record<string, string> = {
        PENDING: '待派工',
        ASSIGNED: '已派工',
        PROCESSING: '进行中',
        COMPLETED: '已完成',
      };
      taskStatusChart.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: statusData.map((item: any) => ({
            name: statusMap[item.status] || item.status,
            value: item.taskCount
          })),
          label: { formatter: '{b}: {c}' }
        }]
      });
    }
  };

  const handleTask = (record: any) => {
    router.push(`/mes/productionTask/detail/${record.id}`);
  };

  const goTo = (path: string) => {
    router.push(path);
  };

  onMounted(() => {
    loadData();
    window.addEventListener('resize', () => {
      weekTrendChart?.resize();
      productDistChart?.resize();
      taskStatusChart?.resize();
    });
  });
</script>

<style lang="less" scoped>
  .dashboard-container {
    padding: 16px;
    background: #f0f2f5;
    min-height: calc(100vh - 84px);

    .view-toggle-row {
      margin-bottom: 8px;
    }

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
    }

    .bottom-row {
      margin-bottom: 16px;
    }
  }
</style>

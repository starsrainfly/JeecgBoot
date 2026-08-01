<!-- views/mes/productionBatchCompletionReport/components/ProductionBatchCompletionDetailModal.vue -->
<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    width="1050px"
    :footer="null"
  >
    <a-tabs v-model:activeKey="activeKey">
      <!-- Tab1: 批次信息 -->
      <a-tab-pane key="1" tab="批次信息">
        <a-descriptions :column="2" bordered size="small" class="mb-4">
          <a-descriptions-item label="订单编号">{{ batchRecord.orderNo || '-' }}</a-descriptions-item>
          <a-descriptions-item label="批次号">{{ batchRecord.batchNo || '-' }}</a-descriptions-item>
          <a-descriptions-item label="序号">{{ batchRecord.batchSeq || '-' }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="getStatusColor(batchRecord.status)">
              {{ batchRecord.status_dictText || batchRecord.status }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="产品编码">{{ batchRecord.productCode || '-' }}</a-descriptions-item>
          <a-descriptions-item label="产品名称">{{ batchRecord.productName || '-' }}</a-descriptions-item>
          <a-descriptions-item label="产品颜色">{{ batchRecord.productColor || '-' }}</a-descriptions-item>
          <a-descriptions-item label="配方编码">{{ batchRecord.recipeCode || '-' }}</a-descriptions-item>
          <a-descriptions-item label="计划生产量(Kg)">{{ batchRecord.plannedQty || '-' }}</a-descriptions-item>
          <a-descriptions-item label="实际生产量(Kg)">{{ batchRecord.actualQty || '-' }}</a-descriptions-item>
          <a-descriptions-item label="已入库量(Kg)">{{ batchRecord.inStockQty || '-' }}</a-descriptions-item>
          <a-descriptions-item label="剩余可入库(Kg)">{{ batchRecord.remainQty || '-' }}</a-descriptions-item>
          <a-descriptions-item label="生产日期">{{ formatDate(batchRecord.productionDate) }}</a-descriptions-item>
          <a-descriptions-item label="失效日期">{{ formatDate(batchRecord.expiryDate) }}</a-descriptions-item>
          <a-descriptions-item label="入库状态">
            <a-tag :color="getInStockColor(batchRecord.inStockStatus)">
              {{ batchRecord.inStockStatus_dictText || getInStockText(batchRecord.inStockStatus) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="质检状态">
            <a-tag :color="getQcColor(batchRecord.qcStatus)">
              {{ batchRecord.qcStatus_dictText || batchRecord.qcStatus }}
            </a-tag>
          </a-descriptions-item>
        </a-descriptions>

        <a-divider orientation="left">配料汇总</a-divider>
        <a-descriptions :column="3" bordered size="small">
          <a-descriptions-item label="物料总数">{{ batchRecord.totalBom ?? '-' }}</a-descriptions-item>
          <a-descriptions-item label="已完成物料">{{ batchRecord.completedBom ?? '-' }}</a-descriptions-item>
          <a-descriptions-item label="配料进度">
            <a-progress
              :percent="batchRecord.percent || 0"
              size="small"
              :stroke-color="batchRecord.percent === 100 ? '#52c41a' : '#1890ff'"
            />
          </a-descriptions-item>
          <a-descriptions-item label="配料状态">
            <a-tag :color="getWeighingColor(batchRecord.weighingStatus)">
              {{ batchRecord.weighingStatus_dictText || batchRecord.weighingStatus }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="实际总投料(Kg)">{{ batchRecord.totalActualWeight || '-' }}</a-descriptions-item>
          <a-descriptions-item label="配料开始时间">{{ formatDateTime(batchRecord.weighingStartTime) }}</a-descriptions-item>
          <a-descriptions-item label="配料完成时间">{{ formatDateTime(batchRecord.weighingEndTime) }}</a-descriptions-item>
        </a-descriptions>
      </a-tab-pane>

      <!-- Tab2: 实际用料 -->
      <a-tab-pane key="2" tab="实际用料" forceRender>
        <BasicTable
          @register="registerMaterialTable"
          :dataSource="materialData"
          :loading="materialLoading"
          :canResize="false"
          size="small"
        >
          <template #bodyCell="{ column, record, text }">
            <!-- 进度条：按4位小数重新计算 -->
            <template v-if="column?.dataIndex === 'progressPercent'">
              <a-progress :percent="calcProgress(record)" size="small" />
            </template>
            <!-- 是否完成：按4位小数重新计算 -->
            <template v-else-if="column?.dataIndex === 'isComplete'">
              <a-tag :color="calcIsComplete(record) ? 'green' : 'orange'">
                {{ calcIsComplete(record) ? '已完成' : '未完成' }}
              </a-tag>
            </template>
            <!-- 数值列统一4位小数 -->
            <template v-else-if="['plannedQty','totalActualQty','proportion'].includes(column?.dataIndex)">
              {{ formatNum(text, 4) }}
            </template>
            <!-- 其他普通列：安全渲染（不写v-else，避免column为undefined时渲染对象） -->
            <template v-else-if="column?.dataIndex">
              {{ text }}
            </template>
          </template>
        </BasicTable>
      </a-tab-pane>

      <!-- Tab3: 生产工单 -->
      <a-tab-pane key="3" tab="生产工单" forceRender>
        <BasicTable
          @register="registerTaskTable"
          :dataSource="taskData"
          :loading="taskLoading"
          :canResize="false"
          size="small"
        >
          <template #bodyCell="{ column, record, text }">
            <template v-if="column?.dataIndex === 'status'">
              <a-tag :color="getTaskStatusColor(record.status)">
                {{ record.status_dictText || record.status }}
              </a-tag>
            </template>
            <template v-else-if="column?.dataIndex === 'qcStatus'">
              <a-tag :color="getQcColor(record.qcStatus)">
                {{ record.qcStatus_dictText || record.qcStatus }}
              </a-tag>
            </template>
            <template v-else-if="column?.dataIndex === 'taskType'">
              <a-tag :color="record.taskType === 'qc' ? 'purple' : 'blue'">
                {{ record.taskType_dictText || record.taskType }}
              </a-tag>
            </template>
            <!-- 数值列统一处理 -->
            <template v-else-if="column?.dataIndex === 'actualDuration'">
              {{ formatNum(text, 0) }}
            </template>
            <!-- 安全默认分支 -->
            <template v-else-if="column?.dataIndex">
              {{ text }}
            </template>
          </template>
        </BasicTable>
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, nextTick } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable } from '/@/components/Table';
  import { Descriptions, DescriptionsItem, Divider, Tabs, TabPane, Progress, Tag } from 'ant-design-vue';
  import { getWeighingDetail, getTaskList } from '../ProductionBatchCompletionReport.api';

  const batchId = ref('');
  const batchRecord = ref<any>({});
  const activeKey = ref('1');
  const materialData = ref([]);
  const materialLoading = ref(false);
  const taskData = ref([]);
  const taskLoading = ref(false);

  const modalTitle = computed(() => {
    const no = batchRecord.value.batchNo || '批次';
    const name = batchRecord.value.productName || '';
    return `${no} ${name ? '- ' + name : ''} - 完工详情`;
  });

  const [registerModal] = useModalInner((data) => {
    batchId.value = data.batchId || '';
    batchRecord.value = data.record || {};
    activeKey.value = '1';
    materialData.value = [];
    taskData.value = [];
    nextTick(() => {
      if (batchId.value) {
        loadMaterialData();
        loadTaskData();
      }
    });
  });

  // Tab2 实际用料表格
  const materialColumns = [
    { title: '序号', dataIndex: 'serialNo', width: 60, align: 'center' },
    { title: '物料编码', dataIndex: 'materialCode', width: 120, align: 'center' },
    { title: '物料名称', dataIndex: 'materialName', width: 150, align: 'center', ellipsis: true },
    { title: '规格型号', dataIndex: 'materialSpec', width: 120, align: 'center', ellipsis: true },
    { title: '配比', dataIndex: 'proportion', width: 80, align: 'center' },
    { title: '计划用量', dataIndex: 'plannedQty', width: 100, align: 'center' },
    { title: '实际总称重', dataIndex: 'totalActualQty', width: 110, align: 'center' },
    { title: '完成进度', dataIndex: 'progressPercent', width: 140, align: 'center' },
    { title: '是否完成', dataIndex: 'isComplete', width: 90, align: 'center' },
  ];

  const [registerMaterialTable] = useTable({
    title: '实际用料明细',
    columns: materialColumns,
    pagination: false,
    showIndexColumn: false,
    size: 'small',
    canResize: false,
  });

  // Tab3 生产工单表格
  const taskColumns = [
    { title: '工单编号', dataIndex: 'taskNo', width: 140, align: 'center' },
    { title: '工单名称', dataIndex: 'taskName', width: 140, align: 'center', ellipsis: true },
    { title: '工序', dataIndex: 'sequence', width: 60, align: 'center' },
    { title: '工单类型', dataIndex: 'taskType', width: 90, align: 'center' },
    { title: '计划设备', dataIndex: 'planEquipmentName', width: 130, align: 'center', ellipsis: true },
    { title: '实际设备', dataIndex: 'actualEquipmentName', width: 130, align: 'center', ellipsis: true },
    { title: '指派操作员', dataIndex: 'assignedOperatorName', width: 100, align: 'center' },
    { title: '实际执行人', dataIndex: 'actualOperatorName', width: 100, align: 'center' },
    { title: '实际开始', dataIndex: 'actualStartTime', width: 150, align: 'center' },
    { title: '实际结束', dataIndex: 'actualEndTime', width: 150, align: 'center' },
    { title: '耗时(分)', dataIndex: 'actualDuration', width: 80, align: 'center' },
    { title: '状态', dataIndex: 'status', width: 90, align: 'center' },
    { title: '质检状态', dataIndex: 'qcStatus', width: 90, align: 'center' },
  ];

  const [registerTaskTable] = useTable({
    title: '生产工单明细',
    columns: taskColumns,
    pagination: false,
    showIndexColumn: true,
    size: 'small',
    canResize: false,
  });

  async function loadMaterialData() {
    materialLoading.value = true;
    try {
      const res = await getWeighingDetail(batchId.value);
      materialData.value = res.bomList || res.result?.bomList || [];
    } catch (e) {
      console.error('加载用料明细失败', e);
      materialData.value = [];
    } finally {
      materialLoading.value = false;
    }
  }

  async function loadTaskData() {
    taskLoading.value = true;
    try {
      const res = await getTaskList({
        batchId: batchId.value,
        pageSize: 100,
      });
      taskData.value = res.records || res.result?.records || [];
    } catch (e) {
      console.error('加载工单明细失败', e);
      taskData.value = [];
    } finally {
      taskLoading.value = false;
    }
  }

  // ========== 新增：统一精度计算与格式化 ==========

  /** 统一数值格式化（保留指定小数位，末尾0保留） */
  function formatNum(val, digits = 4) {
    if (val === null || val === undefined || val === '') return '-';
    const num = Number(val);
    if (isNaN(num)) return val;
    return num.toFixed(digits);
  }

  /** 按4位小数重新计算进度百分比 */
  function calcProgress(record) {
    const planned = Number(record.plannedQty);
    const actual = Number(record.totalActualQty);
    if (!planned || isNaN(planned) || isNaN(actual)) return 0;
    // 统一4位小数后计算比例
    const p = Math.round((actual / planned) * 100);
    return Math.min(p, 100);
  }

  /** 按4位小数重新判断是否完成（actual >= planned） */
  function calcIsComplete(record) {
    const planned = Number(record.plannedQty);
    const actual = Number(record.totalActualQty);
    if (!planned || isNaN(planned) || isNaN(actual)) return false;
    // 统一截断到4位小数后比较
    return Number(actual.toFixed(4)) >= Number(planned.toFixed(4));
  }

  // ========== 工具函数 ==========
  function formatDate(date) {
    if (!date) return '-';
    if (typeof date === 'string') return date.length > 10 ? date.substring(0, 10) : date;
    try {
      const d = new Date(date);
      return isNaN(d.getTime()) ? '-' : d.toISOString().substring(0, 10);
    } catch {
      return '-';
    }
  }
  function formatDateTime(date) {
    if (!date) return '-';
    if (typeof date === 'string') return date;
    try {
      const d = new Date(date);
      return isNaN(d.getTime()) ? '-' : d.toLocaleString('zh-CN', { hour12: false });
    } catch {
      return '-';
    }
  }

  // 入库状态（字典 mes_in_stock_status：0未入库 1部分入库 2已入库）
  function getInStockColor(status) {
    const s = String(status);
    if (s === '2') return 'green';
    if (s === '1') return 'orange';
    return 'default';
  }
  function getInStockText(status) {
    const s = String(status);
    if (s === '2') return '已入库';
    if (s === '1') return '部分入库';
    if (s === '0') return '未入库';
    return status || '-';
  }

  // 质检状态
  function getQcColor(status) {
    const s = String(status || '').toUpperCase();
    if (s === 'PASS') return 'green';
    if (s === 'FAIL') return 'red';
    if (s === 'WAIT_CHECK') return 'orange';
    return 'default';
  }

  // 批次状态
  function getStatusColor(status) {
    const s = String(status || '').toUpperCase();
    if (s === 'COMPLETED' || s === '3') return 'green';
    if (s === 'WEIGHED') return 'blue';
    if (s === 'WEIGHING') return 'cyan';
    if (s === 'PRODUCTION' || s === '2') return 'processing';
    return 'default';
  }

  // 配料状态
  function getWeighingColor(status) {
    const s = String(status || '').toUpperCase();
    if (s === 'WEIGHED') return 'green';
    if (s === 'WEIGHING') return 'blue';
    return 'default';
  }

  // 工单状态
  function getTaskStatusColor(status) {
    const s = String(status || '').toUpperCase();
    if (s === 'COMPLETED') return 'green';
    if (s === 'PROCESSING') return 'blue';
    if (s === 'ASSIGNED') return 'cyan';
    return 'default';
  }
</script>

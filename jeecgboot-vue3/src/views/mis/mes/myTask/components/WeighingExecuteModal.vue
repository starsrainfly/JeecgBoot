<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    :width="1400"
    :minHeight="700"
    :canFullscreen="true"
    :defaultFullscreen="true"
    :draggable="true"
    :useWrapper="true"
    :maskClosable="false"
    :keyboard="false"
    @ok="handleComplete"
    okText="完成配料"
    :okButtonProps="{ disabled: completionRate < 100 }"
  >
    <!-- 顶部信息 -->
    <a-alert
      :message="alertMessage"
      type="info"
      show-icon
      class="mb-4"
    />

    <a-row :gutter="16" class="h-[500px]">
      <!-- 左侧物料清单 -->
      <a-col :span="16" class="h-full">
        <a-card title="物料配料清单" size="small" class="h-full">
          <template #extra>
            <a-tag :color="completionColor">
              完成度: {{ completionRate }}%
            </a-tag>
          </template>

          <JVxeTable
            ref="bomTableRef"
            :height="420"
            :columns="bomColumns"
            :dataSource="bomDataSource"
            :loading="loading"
            :rowClassName="getRowClassName"
            @row-click="handleRowClick"
          >
            <!-- 已称重列 -->
            <template #totalActualQty="{ row }">
              <span :class="getWeightClass(row)" class="font-bold">
                {{ formatNumber(row.totalActualQty) }}
              </span>
              <a-progress
                v-if="getProgress(row) > 0"
                :percent="getProgress(row)"
                size="small"
                :status="getProgressStatus(row)"
                :showInfo="false"
                class="w-[50px] ml-2"
              />
            </template>

            <!-- 本次称重输入 -->
            <template #currentWeight="{ row }">
              <a-input-number
                v-model:value="row.currentWeight"
                :min="0"
                :max="getMaxWeight(row)"
                :precision="4"
                :disabled="isComplete(row) || row.isWeighing"
                style="width: 100%"
                placeholder="输入重量"
                @pressEnter="() => handleAddWeight(row)"
                @blur="() => handleInputBlur(row, 'currentWeight')"
              />
            </template>

            <!-- 操作列 -->
            <template #action="{ row }">
              <a-button
                v-if="!isComplete(row)"
                type="primary"
                size="small"
                :loading="row.isWeighing"
                :disabled="!row.currentWeight || row.currentWeight <= 0"
                @click.stop="handleAddWeight(row)"
              >
                添加
              </a-button>
              <a-tag v-else color="success">已完成</a-tag>
            </template>

            <!-- 状态列 -->
            <template #rowStatus="{ row }">
              <a-badge :status="getRowStatus(row).status" :text="getRowStatus(row).text" />
            </template>
          </JVxeTable>
        </a-card>
      </a-col>

      <!-- 右侧称重面板 -->
      <a-col :span="8" class="h-full">
        <a-card
          :title="rightPanelTitle"
          size="small"
          class="h-full"
        >
          <div v-if="showWeighingForm" class="weighing-form">
            <a-descriptions :column="1" size="small" bordered class="mb-4">
              <a-descriptions-item label="物料编码">{{ selectedMaterial.materialCode }}</a-descriptions-item>
              <a-descriptions-item label="规格型号">{{ selectedMaterial.materialSpec }}</a-descriptions-item>
              <a-descriptions-item label="配比">{{ selectedMaterial.proportion }}</a-descriptions-item>
              <a-descriptions-item label="需求数量">
                <span class="text-primary font-bold">{{formatWeightDisplay( selectedMaterial.plannedQty) }} kg</span>
              </a-descriptions-item>
              <a-descriptions-item label="已称重">
                <span :class="isComplete(selectedMaterial) ? 'text-success' : 'text-warning'" class="font-bold">
                  {{ formatWeightDisplay(selectedMaterial.totalActualQty) }} kg
                </span>
              </a-descriptions-item>
              <a-descriptions-item label="还需称重" v-if="!isComplete(selectedMaterial)">
                <span class="text-danger font-bold">{{ formatWeightDisplay(getRemainWeight(selectedMaterial)) }} kg</span>
              </a-descriptions-item>
            </a-descriptions>

            <!-- 大输入面板 -->
            <div class="big-input-area">
              <div class="input-label">本次称重 (kg)</div>
              <a-input-number
                v-model:value="currentWeightInput"
                class="big-input"
                :min="0"
                :max="getMaxWeight(selectedMaterial)"
                :precision="4"
                placeholder="0.0000"
                @pressEnter="handleBigAdd"
                @blur="handleBigInputBlur"
              />
              <div class="quick-btns">
                <a-button size="small" @click="quickFill(0.5)">50%</a-button>
                <a-button size="small" @click="quickFill(0.8)">80%</a-button>
                <a-button size="small" @click="quickFill(1)">100%</a-button>
                <a-button size="small" type="primary" @click="quickFill('remain')">剩余</a-button>
              </div>
            </div>

            <a-button
              type="primary"
              block
              size="large"
              :loading="submitting"
              :disabled="!currentWeightInput || currentWeightInput <= 0"
              @click="handleBigAdd"
              class="mt-4"
            >
              <PlusOutlined /> 确认添加称重
            </a-button>
          </div>

          <a-result
            v-else-if="showCompletedResult"
            status="success"
            title="该物料已完成配料"
          />

          <a-empty v-else description="请点击左侧物料进行称重" />
        </a-card>
      </a-col>
    </a-row>

    <!-- 本次称重记录 -->
    <a-card title="本次称重记录" size="small" class="mt-4">
      <a-table
        :columns="sessionColumns"
        :dataSource="sessionRecords"
        size="small"
        :pagination="{ pageSize: 20 }"
      >
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.key === 'action'">
            <a-popconfirm
              title="确定删除这条称重记录吗？"
              @confirm="handleDeleteRecord(record, index)"
            >
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </template>
          <template v-else-if="column.dataIndex === 'actualQty'">
            {{ formatWeightDisplay(record.actualQty) }}
          </template>
        </template>
      </a-table>
    </a-card>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStore } from '/@/store/modules/user';
  import { PlusOutlined } from '@ant-design/icons-vue';
  import { productionBatchBomList, queryMaterialActual, addMaterialActual, deleteMaterialActual, completeTask } from '../MyTask.api';

  // Emits
  const emit = defineEmits(['success', 'register']);

  // Hooks
  const { createMessage } = useMessage();
  const userStore = useUserStore();

  // Refs
  const bomTableRef = ref();
  const loading = ref(false);
  const submitting = ref(false);

  // Data
  const taskInfo = ref({});
  const batchId = ref('');
  const bomDataSource = ref([]);
  const selectedMaterial = ref(null);
  const currentWeightInput = ref(null);
  const sessionRecords = ref([]);

  // Modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false });
    taskInfo.value = data;
    batchId.value = data.batchId;
    selectedMaterial.value = null;
    currentWeightInput.value = null;
   // sessionRecords.value = [];

    await loadBomData();
  });

  // Computed
  const modalTitle = computed(() => {
    return `配料执行 - ${taskInfo.value.batchNo || ''}`;
  });

  const alertMessage = computed(() => {
    return `工单: ${taskInfo.value.taskNo || ''} | 产品: ${taskInfo.value.productName || ''} | 计划: ${taskInfo.value.plannedQty || 0}kg`;
  });
/**
 * 计算完成度
 * */
  const completionRate = computed(() => {
  if (!bomDataSource.value.length) return 0;
  const completedCount = bomDataSource.value.filter(item => isComplete(item)).length;
  const totalCount = bomDataSource.value.length;
  const rate = Math.round((completedCount / totalCount) * 100);
  console.log('完成度计算:', { completedCount, totalCount, rate });
  return rate;
  });

  const completionColor = computed(() => {
    return completionRate.value === 100 ? 'success' : 'processing';
  });

  const rightPanelTitle = computed(() => {
    return selectedMaterial.value ? `当前: ${selectedMaterial.value.materialName}` : '点击左侧物料开始称重';
  });

  const showWeighingForm = computed(() => {
    return selectedMaterial.value && !isComplete(selectedMaterial.value);
  });

  const showCompletedResult = computed(() => {
    return selectedMaterial.value && isComplete(selectedMaterial.value);
  });

  // Columns
  const bomColumns = [
    // {title: 'id', key: 'id', width: 60 },
    // {title: 'batchId', key: 'batchId', width: 60 },
    { title: '排序', key: 'serialNo', width: 60 },
    { title: '物料编码', key: 'materialCode', width: 110 },
    { title: '物料名称', key: 'materialName', width: 130 },
    { title: '规格', key: 'materialSpec', width: 120 },
    { title: '配比', key: 'proportion', width: 80 },
    { title: '需求(kg)', key: 'plannedQty', width: 90 },

    {
      title: '已称重(kg)',
      key: 'totalActualQty',
      width: 130,
      slots: { default: 'totalActualQty' }  // 修改这里
    },
    {
      title: '本次称重',
      key: 'currentWeight',
      width: 130,
      slots: { default: 'currentWeight' }  // 修改这里
    },
    {
      title: '操作',
      key: 'action',
      width: 90,
      fixed: 'right',
      slots: { default: 'action' }  // 修改这里
    },
    {
      title: '状态',
      key: 'rowStatus',
      width: 90,
      fixed: 'right',
      slots: { default: 'rowStatus' }  // 修改这里
    },
  ];

  const sessionColumns = [
    { title: '物料名称', dataIndex: 'materialName', key: 'materialName' },
    { title: '称重(kg)', dataIndex: 'actualQty', key: 'actualQty' },
    { title: '操作员', dataIndex: 'operatorName', key: 'operatorName' },
    { title: '时间', dataIndex: 'createTime', key: 'createTime' },
    { title: '操作', key: 'action', width: 80 },
  ];
  // ==================== 格式化方法 ====================

  /**
   * 格式化重量显示：去除末尾多余的0，最多保留4位小数
   * 例如：10.0000 -> 10, 10.5000 -> 10.5, 10.1230 -> 10.123
   */
  function formatWeightDisplay(val: number | string | undefined): string {
    if (val === undefined || val === null || val === '') return '0';

    // 转为数字
    const num = parseFloat(String(val));
    if (isNaN(num)) return '0';

    // 先限制到4位小数，然后去除末尾的0和点
    return parseFloat(num.toFixed(4)).toString();
  }
  /**
   * 处理输入框失焦事件，确保值被正确格式化
   */
  function handleInputBlur(row: any, field: string) {
    if (row[field] !== undefined && row[field] !== null) {
      // 重新赋值，触发精度限制
      const val = parseFloat(row[field]);
      if (!isNaN(val)) {
        row[field] = parseFloat(val.toFixed(4));
      }
    }
  }

  /**
   * 处理大输入面板失焦
   */
  function handleBigInputBlur() {
    if (currentWeightInput.value !== null && currentWeightInput.value !== undefined) {
      const val = parseFloat(String(currentWeightInput.value));
      if (!isNaN(val)) {
        currentWeightInput.value = parseFloat(val.toFixed(4));
      }
    }
  }

  // ==================== 业务方法 ====================
  // Methods
  async function loadBomData() {
    loading.value = true;
    try {
      const bomRes = await productionBatchBomList({ id: batchId.value });
      const bomList = bomRes || [];

      const actualRes = await queryMaterialActual({ batchId: batchId.value });
      const actualList = actualRes.records || [];
      console.log('后端返回的BOM数据1:', bomRes);
      console.log('actualList:', actualList);
      bomDataSource.value = bomList.map((bom) => {
        const related = actualList.filter((a) => a.batchBomId === bom.id);
        const totalActualQty = related.reduce((sum, item) => sum + (parseFloat(item.actualQty) || 0), 0);

        return {
          ...bom,
          totalActualQty: parseFloat(totalActualQty.toFixed(4)),
          currentWeight: undefined,
          isWeighing: false,
        };
      });
      // 关键修改：从 actualList 恢复称重记录，按时间倒序排列
      sessionRecords.value = actualList
        .map((item) => ({
          ...item,
          // 确保 actualQty 是数字
          actualQty: parseFloat(item.actualQty) || 0,
        }))
        .sort((a, b) => {
          // 按创建时间倒序，最新的在前面
          return new Date(b.createTime).getTime() - new Date(a.createTime).getTime();
        });
    } finally {
      loading.value = false;
    }
  }

  function isComplete(row) {
    const planned = parseFloat(row.plannedQty) || 0;
    const actual = parseFloat(row.totalActualQty) || 0;
    return actual >= planned - 0.0001; // 容差值避免精度问题
  }

  function getWeightClass(row) {
    if (isComplete(row)) return 'text-success';
    if (row.totalActualQty > 0) return 'text-warning';
    return 'text-secondary';
  }

  function getProgress(row) {
    if (!row.plannedQty) return 0;
    return Math.min(Math.round(((row.totalActualQty || 0) / row.plannedQty) * 100), 100);
  }

  function getProgressStatus(row) {
    if (isComplete(row)) return 'success';
    return 'normal';
  }

  function getRowStatus(row) {
    if (isComplete(row)) return { status: 'success', text: '完成' };
    if ((row.totalActualQty || 0) > 0) return { status: 'processing', text: '进行中' };
    return { status: 'default', text: '待配料' };
  }

  function getRowClassName(row) {
    if (selectedMaterial.value && selectedMaterial.value.id === row.id) return 'selected-row';
    if (isComplete(row)) return 'completed-row';
    return '';
  }

  function getMaxWeight(row) {
    const remain = (row.plannedQty || 0) - (row.totalActualQty || 0);
    return remain > 0 ? remain : 0;
  }

  function getRemainWeight(row) {
    return ((row.plannedQty || 0) - (row.totalActualQty || 0)).toFixed(4);
  }

  function formatNumber(val) {
    return parseFloat(val || 0).toFixed(4);
  }

  function handleRowClick(row) {
    selectedMaterial.value = row;
    currentWeightInput.value = null;
  }

  function quickFill(type) {
    if (!selectedMaterial.value) return;
    const planned = selectedMaterial.value.plannedQty || 0;
    const actual = selectedMaterial.value.totalActualQty || 0;
    const remain = planned - actual;

    if (type === 'remain') {
      currentWeightInput.value = remain > 0 ? remain : 0;
    } else {
      currentWeightInput.value = parseFloat((planned * type).toFixed(4));
    }
  }

  async function handleAddWeight(row) {
    if (!row.currentWeight || row.currentWeight <= 0) return;

    row.isWeighing = true;
    try {
      // 确保提交的值是4位小数精度
      const actualQty = parseFloat(parseFloat(row.currentWeight).toFixed(4));
      const params = {
        batchId: batchId.value,
        batchBomId: row.id,
        materialId: row.materialId,
        materialCode: row.materialCode,
        materialName: row.materialName,
        materialSpec: row.materialSpec,
        plannedQty: row.plannedQty,
        actualQty: actualQty,//row.currentWeight,
        operatorId: userStore.getUserInfo.id,
        operatorName: userStore.getUserInfo.realname,
        orderNo: taskInfo.value.orderNo || '',
        batchNo: taskInfo.value.batchNo,
        productId: taskInfo.value.productId,
        productCode: taskInfo.value.productCode,
        productName: taskInfo.value.productName,
        bomSerialNo: row.serialNo,
        unit:row.unit,
      };

      const res = await addMaterialActual(params);

      row.totalActualQty = parseFloat(((row.totalActualQty || 0) + actualQty).toFixed(4));
      row.currentWeight = undefined;

      sessionRecords.value.unshift({
        ...params,
        id: res.result ? res.result.id : Date.now(),
        createTime: new Date().toLocaleString(),
      });

     // createMessage.success(`成功添加 ${params.actualQty} kg`);
      createMessage.success(`成功添加 ${formatWeightDisplay(actualQty)} kg`);
      bomTableRef.value && bomTableRef.value.refreshData();

      if (isComplete(row)) {
        createMessage.success(`${row.materialName} 配料完成！`);
      }
    } finally {
      row.isWeighing = false;
    }
  }

  async function handleBigAdd() {
    if (!selectedMaterial.value || !currentWeightInput.value) return;
    await handleAddWeight({ ...selectedMaterial.value, currentWeight: currentWeightInput.value });
    currentWeightInput.value = null;
  }

  async function handleDeleteRecord(record, index) {
    try {
      await deleteMaterialActual({ id: record.id });

      const material = bomDataSource.value.find(m => m.id === record.batchBomId);
      if (material) {
        material.totalActualQty = (material.totalActualQty || 0) - (record.actualQty || 0);
      }

      sessionRecords.value.splice(index, 1);
      createMessage.success('删除成功');
      bomTableRef.value && bomTableRef.value.refreshData();
    } catch (error) {
      createMessage.error('删除失败');
    }
  }

  async function handleComplete() {
    if (completionRate.value < 100) {
      createMessage.warning('还有物料未完成配料');
      return;
    }

    setModalProps({ confirmLoading: true });
    try {

      await completeTask({id:taskInfo.value.id});
      emit('success');
      closeModal();
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style scoped>
  .text-success { color: #52c41a; }
  .text-warning { color: #faad14; }
  .text-danger { color: #ff4d4f; }
  .text-primary { color: #1890ff; }
  .text-secondary { color: #999; }

  :deep(.selected-row) {
    background-color: #e6f7ff !important;
  }

  :deep(.completed-row) {
    background-color: #f6ffed !important;
  }

  .big-input-area {
    text-align: center;
    padding: 20px;
    background: #fafafa;
    border-radius: 8px;
    border: 1px solid #d9d9d9;
  }

  .big-input-area .input-label {
    color: #666;
    margin-bottom: 12px;
    font-size: 14px;
  }

  .big-input-area .big-input {
    width: 100%;
  }

  .big-input-area .big-input :deep(.ant-input-number-input) {
    font-size: 28px;
    text-align: center;
    height: 50px;
    font-weight: bold;
  }

  .big-input-area .quick-btns {
    margin-top: 12px;
    display: flex;
    gap: 8px;
    justify-content: center;
  }
</style>

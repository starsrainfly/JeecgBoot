<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    width="1000px"
    :minHeight="500"
    :defaultFullscreen="false"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrapper">
      <a-spin size="large" tip="加载中..." />
    </div>

    <!-- 单条申请模式 -->
    <div v-else-if="applyMode === 'single'" class="apply-content">
      <a-descriptions bordered :column="2" size="small">
        <a-descriptions-item label="生产订单">{{ record.orderNo }}</a-descriptions-item>
        <a-descriptions-item label="生产批次">{{ record.batchNo }}</a-descriptions-item>
        <a-descriptions-item label="物料编码">{{ record.materialCode }}</a-descriptions-item>
        <a-descriptions-item label="物料名称">{{ record.materialName }}</a-descriptions-item>
        <a-descriptions-item label="规格型号">{{ record.materialSpec }}</a-descriptions-item>
        <a-descriptions-item label="单位">{{ record.unit }}</a-descriptions-item>
        <a-descriptions-item label="需求数量">{{ record.requiredQty }}</a-descriptions-item>
        <a-descriptions-item label="已发数量">{{ record.issuedQty }}</a-descriptions-item>
        <a-descriptions-item label="剩余待发" :span="2">
          <span style="color: #f5222d; font-weight: bold;">{{ formatNumber(remainingQty) }}</span>
        </a-descriptions-item>
      </a-descriptions>

      <a-divider />

      <a-form :model="formData" layout="vertical">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="申请数量" required>
              <a-input-number
                v-model:value="formData.quantity"
                :min="0"
                :max="remainingQty * 2"
                :precision="6"
                style="width: 100%"
                @change="handleSingleQtyChange"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="formData.quantity > remainingQty">
            <a-form-item label="备注">
              <a-input
                v-model:value="formData.remark"
                placeholder="超量申请，请填写原因"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24" v-if="formData.quantity <= remainingQty">
          <a-col :span="12">
            <a-form-item label="期望领料日期" required>
              <a-date-picker
                v-model:value="formData.expectDate"
                valueFormat="YYYY-MM-DD"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="备注">
              <a-input v-model:value="formData.remark" placeholder="备注" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 批量/按订单模式 -->
    <div v-else class="apply-content">
      <a-alert
        v-if="orderNo"
        :message="`生产订单: ${orderNo}`"
        type="info"
        show-icon
        style="margin-bottom: 16px;"
      />

      <!-- 步骤1：选择批次（仅按订单模式显示） -->
      <div v-if="applyMode === 'order' && !batchConfirmed" class="batch-select-section">
        <div class="section-title">步骤1：选择要申请的批次</div>
        <a-empty v-if="!loading && batchList.length === 0" description="暂无待发料的批次" />
        <a-checkbox-group v-else v-model:value="selectedBatchIds" class="batch-checkbox-group">
          <a-row>
            <a-col :span="8" v-for="batch in batchList" :key="batch.batchId" style="margin-bottom: 8px;">
              <a-checkbox :value="batch.batchId">
                {{ batch.batchNo }}
                <span style="color: #999; font-size: 12px;">
                  ({{ batch.materialCount }}种物料, 共{{ formatNumber(batch.totalRemainingQty) }})
                </span>
              </a-checkbox>
            </a-col>
          </a-row>
        </a-checkbox-group>

        <div style="margin-top: 16px;" v-if="batchList.length > 0">
          <a-button
            type="primary"
            @click="confirmBatchSelect"
            :disabled="selectedBatchIds.length === 0"
            :loading="loading"
          >
            确认选择 (已选{{ selectedBatchIds.length }}个批次)
          </a-button>
        </div>
      </div>

      <!-- 步骤2：确认物料明细 -->
      <div v-if="(applyMode === 'order' && batchConfirmed) || applyMode === 'batch'" class="material-section">
        <div class="section-title">
          {{ applyMode === 'order' ? '步骤2：确认物料明细' : '物料汇总明细' }}
        </div>

        <a-empty v-if="!loading && expandedMaterialList.length === 0" description="暂无待发料物料" />

        <!-- 修复：使用 :data-source 而非 :dataSource，确保响应式更新 -->
        <a-table
          v-else
          :data-source="expandedMaterialList"
          :columns="detailColumns"
          :pagination="false"
          size="small"
          bordered
          :scroll="{ y: 350 }"
          rowKey="uniqueKey"
        >
          <template #bodyCell="{ column, record: row, index }">
            <!-- 批次列 -->
            <template v-if="column.key === 'batchNo'">
              <a-tag color="blue" size="small">{{ row.currentBatchNo }}</a-tag>
            </template>

            <!-- 物料类型列 -->
            <template v-else-if="column.key === 'materialType'">
              <a-tag :color="getMaterialTypeColor(row.materialType)">
                {{ getMaterialTypeText(row.materialType) }}
              </a-tag>
            </template>

            <!-- 申请数量列 - 修复：使用自定义输入确保可编辑 -->
            <template v-else-if="column.key === 'quantity'">
              <div class="quantity-cell">
                <a-input-number
                  :value="row.batchQuantity"
                  :min="0"
                  :max="row.batchRemainingQty * 2"
                  :precision="6"
                  style="width: 100px"
                  @change="(val) => handleQtyChange(index, val)"
                />
                <a-tag v-if="row.isOverApply" color="orange" style="margin-left: 8px;">超量</a-tag>
              </div>
            </template>

            <!-- 备注列 - 超量时必填 -->
            <template v-else-if="column.key === 'remark'">
              <a-input
                v-if="row.isOverApply"
                :value="row.remark"
                placeholder="超量原因"
                size="small"
                style="width: 140px;"
                @change="(e) => handleRemarkChange(index, e.target.value)"
              />
              <a-input
                v-else
                :value="row.remark"
                placeholder="备注"
                size="small"
                style="width: 140px;"
                @change="(e) => handleRemarkChange(index, e.target.value)"
              />
            </template>

            <!-- 物料名称 -->
            <template v-else-if="column.key === 'materialName'">
              <span>{{ row.materialName }}</span>
              <a-tag v-if="row.isSameMaterial" color="green" size="small" style="margin-left: 4px;">合并</a-tag>
            </template>
          </template>

          <!-- 修复：按物料类型分组合计 -->
          <template #summary v-if="expandedMaterialList.length > 0">
            <a-table-summary>
              <!-- 源材料合计 -->
              <a-table-summary-row v-if="sourceMaterialSummary.count > 0">
                <a-table-summary-cell :col-span="6">
                  <strong>源材料合计</strong>
                </a-table-summary-cell>
                <a-table-summary-cell>{{ formatNumber(sourceMaterialSummary.remaining) }}</a-table-summary-cell>
                <a-table-summary-cell>
                  <span :style="{ color: sourceMaterialSummary.isOver ? '#ff4d4f' : '#1890ff', fontWeight: 'bold' }">
                    {{ formatNumber(sourceMaterialSummary.apply) }}
                  </span>
                </a-table-summary-cell>
                <a-table-summary-cell></a-table-summary-cell>
              </a-table-summary-row>

              <!-- 内包合计 -->
              <a-table-summary-row v-if="innerPackageSummary.count > 0" style="background-color: #f6ffed;">
                <a-table-summary-cell :col-span="6">
                  <strong>内包合计</strong>
                </a-table-summary-cell>
                <a-table-summary-cell>{{ formatNumber(innerPackageSummary.remaining) }}</a-table-summary-cell>
                <a-table-summary-cell>
                  <span :style="{ color: innerPackageSummary.isOver ? '#ff4d4f' : '#52c41a', fontWeight: 'bold' }">
                    {{ formatNumber(innerPackageSummary.apply) }}
                  </span>
                </a-table-summary-cell>
                <a-table-summary-cell></a-table-summary-cell>
              </a-table-summary-row>

              <!-- 外包合计 -->
              <a-table-summary-row v-if="outerPackageSummary.count > 0" style="background-color: #fff7e6;">
                <a-table-summary-cell :col-span="6">
                  <strong>外包合计</strong>
                </a-table-summary-cell>
                <a-table-summary-cell>{{ formatNumber(outerPackageSummary.remaining) }}</a-table-summary-cell>
                <a-table-summary-cell>
                  <span :style="{ color: outerPackageSummary.isOver ? '#ff4d4f' : '#fa8c16', fontWeight: 'bold' }">
                    {{ formatNumber(outerPackageSummary.apply) }}
                  </span>
                </a-table-summary-cell>
                <a-table-summary-cell></a-table-summary-cell>
              </a-table-summary-row>

              <!-- 总计 -->
              <a-table-summary-row style="background-color: #e6f7ff; font-weight: bold;">
                <a-table-summary-cell :col-span="6">
                  <strong>总计</strong>
                </a-table-summary-cell>
                <a-table-summary-cell :index="6">{{ formatNumber(totalExpandedRemaining) }}</a-table-summary-cell>
                <a-table-summary-cell :index="7">
                  <span :style="{ color: totalIsOver ? '#ff4d4f' : 'inherit', fontWeight: 'bold' }">
                    {{ formatNumber(totalExpandedApply) }}
                  </span>
                </a-table-summary-cell>
                <a-table-summary-cell :index="8"></a-table-summary-cell>
              </a-table-summary-row>
            </a-table-summary>
          </template>
        </a-table>

        <a-divider v-if="expandedMaterialList.length > 0" />

        <a-form :model="commonForm" layout="vertical" v-if="expandedMaterialList.length > 0">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="期望领料日期" required>
                <a-date-picker
                  v-model:value="commonForm.expectDate"
                  valueFormat="YYYY-MM-DD"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="备注">
                <a-input v-model:value="commonForm.remark" placeholder="备注" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>

        <div v-if="applyMode === 'order' && batchConfirmed" style="margin-top: 16px;">
          <a-button @click="resetBatchSelect">重新选择批次</a-button>
        </div>
      </div>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, nextTick } from 'vue';
  import { useModalInner, BasicModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import dayjs from 'dayjs';
  import { getBatchesByOrder, getMaterialSummary, submitStockOutApply } from '../ProductionMaterial.api';

  const { createMessage } = useMessage();
  const emit = defineEmits(['success', 'register']);

  // 物料类型枚举
  const MaterialType = {
    SOURCE: '0',      // 源材料
    INNER_PKG: '1',   // 内包
    OUTER_PKG: '2'    // 外包
  };

  const applyMode = ref('single');
  const record = ref<any>({});
  const records = ref<any[]>([]);
  const orderId = ref('');
  const orderNo = ref('');
  const orderIds = ref<string[]>([]);
  const batchList = ref<any[]>([]);
  const selectedBatchIds = ref<string[]>([]);
  const batchConfirmed = ref(false);
  const materialList = ref<any[]>([]);
  const loading = ref(false);

  const formData = ref({
    quantity: 0,
    expectDate: dayjs().add(1, 'day').format('YYYY-MM-DD'),
    remark: ''
  });

  const commonForm = ref({
    expectDate: dayjs().add(1, 'day').format('YYYY-MM-DD'),
    remark: ''
  });

  // 浮点数精度处理
  const safeNumber = (val: any, precision = 6): number => {
    if (!val || isNaN(Number(val))) return 0;
    return Number(Number(val).toFixed(precision));
  };

  const remainingQty = computed(() => {
    if (!record.value) return 0;
    return safeNumber(record.value.remainingQty) ||
      safeNumber((record.value.requiredQty || 0) - (record.value.issuedQty || 0));
  });

  // 修复：使用 ref 存储展开列表，确保可编辑
  const expandedMaterialList = ref<any[]>([]);

  // 计算属性：按物料类型分组合计
  const sourceMaterialSummary = computed(() => {
    const items = expandedMaterialList.value.filter(i => i.materialType === MaterialType.SOURCE);
    const remaining = items.reduce((sum, i) => sum + safeNumber(i.batchRemainingQty), 0);
    const apply = items.reduce((sum, i) => sum + safeNumber(i.batchQuantity), 0);
    return {
      count: items.length,
      remaining,
      apply,
      isOver: apply > remaining
    };
  });

  const innerPackageSummary = computed(() => {
    const items = expandedMaterialList.value.filter(i => i.materialType === MaterialType.INNER_PKG);
    const remaining = items.reduce((sum, i) => sum + safeNumber(i.batchRemainingQty), 0);
    const apply = items.reduce((sum, i) => sum + safeNumber(i.batchQuantity), 0);
    return {
      count: items.length,
      remaining,
      apply,
      isOver: apply > remaining
    };
  });

  const outerPackageSummary = computed(() => {
    const items = expandedMaterialList.value.filter(i => i.materialType === MaterialType.OUTER_PKG);
    const remaining = items.reduce((sum, i) => sum + safeNumber(i.batchRemainingQty), 0);
    const apply = items.reduce((sum, i) => sum + safeNumber(i.batchQuantity), 0);
    return {
      count: items.length,
      remaining,
      apply,
      isOver: apply > remaining
    };
  });

  const totalExpandedRemaining = computed(() => {
    return expandedMaterialList.value.reduce((sum, item) =>
      sum + safeNumber(item.batchRemainingQty), 0);
  });

  const totalExpandedApply = computed(() => {
    return expandedMaterialList.value.reduce((sum, item) =>
      sum + safeNumber(item.batchQuantity), 0);
  });

  const totalIsOver = computed(() => {
    return totalExpandedApply.value > totalExpandedRemaining.value;
  });

  const modalTitle = computed(() => {
    const titles: Record<string, string> = {
      single: '出库申请',
      batch: '批量出库申请',
      order: '按订单出库申请'
    };
    return titles[applyMode.value] || '出库申请';
  });

  // 表格列定义（增加物料类型列，移除超量原因列改为备注列）
  const detailColumns = [
    { title: '物料编码', dataIndex: 'materialCode', key: 'materialCode', width: 100, fixed: 'left' },
    { title: '物料名称', dataIndex: 'materialName', key: 'materialName', width: 140 },
    { title: '规格型号', dataIndex: 'materialSpec', key: 'materialSpec', width: 100 },
    { title: '类型', key: 'materialType', width: 80, align: 'center' },
    { title: '生产批次', key: 'batchNo', width: 160, align: 'center' },
    { title: '单位', dataIndex: 'unit', key: 'unit', width: 60, align: 'center' },
    { title: '剩余待发', dataIndex: 'batchRemainingQty', key: 'batchRemainingQty', width: 100, align: 'right' },
    { title: '本次申请', key: 'quantity', width: 180, align: 'center' },
    { title: '备注', key: 'remark', width: 160 },
  ];

  const [registerModal, { closeModal, setModalProps }] = useModalInner(async (data) => {
    applyMode.value = data.mode || 'single';
    record.value = data.record || {};
    records.value = data.records || [];
    orderId.value = data.orderId || '';
    orderNo.value = data.orderNo || '';
    orderIds.value = data.orderIds || (data.orderId ? [data.orderId] : []);

    batchConfirmed.value = false;
    selectedBatchIds.value = [];
    materialList.value = [];
    expandedMaterialList.value = [];
    batchList.value = [];

    await nextTick();

    if (applyMode.value === 'single') {
      formData.value.quantity = safeNumber(remainingQty.value);
      formData.value.expectDate = dayjs().add(1, 'day').format('YYYY-MM-DD');
      formData.value.remark = '';
    } else if (applyMode.value === 'batch') {
      await loadBatchMaterialsFromRecords(records.value);
    } else if (applyMode.value === 'order') {
      await loadBatchList();
    }
  });

  // 物料类型显示
  function getMaterialTypeText(type: number) {
    const map: Record<number, string> = {
      [MaterialType.SOURCE]: '源材料',
      [MaterialType.INNER_PKG]: '内包',
      [MaterialType.OUTER_PKG]: '外包'
    };
    return map[type] || '未知';
  }

  function getMaterialTypeColor(type: number) {
    const map: Record<number, string> = {
      [MaterialType.SOURCE]: 'blue',
      [MaterialType.INNER_PKG]: 'green',
      [MaterialType.OUTER_PKG]: 'orange'
    };
    return map[type] || 'default';
  }

  function formatNumber(val: any, precision = 2) {
    const num = safeNumber(val, precision);
    return num.toFixed(precision);
  }

  function handleSingleQtyChange(val: number) {
    formData.value.quantity = safeNumber(val);
  }

  // 修复：直接修改数组元素确保响应式
  function handleQtyChange(index: number, val: number) {
    if (index >= 0 && index < expandedMaterialList.value.length) {
      const item = expandedMaterialList.value[index];
      item.batchQuantity = safeNumber(val);
      item.isOverApply = item.batchQuantity > item.batchRemainingQty;
      // 触发更新
      expandedMaterialList.value = [...expandedMaterialList.value];
    }
  }

  function handleRemarkChange(index: number, val: string) {
    if (index >= 0 && index < expandedMaterialList.value.length) {
      expandedMaterialList.value[index].remark = val;
      expandedMaterialList.value = [...expandedMaterialList.value];
    }
  }

  async function loadBatchList() {
    if (!orderId.value) {
      createMessage.error('订单ID为空');
      return;
    }
    loading.value = true;
    try {
      const res = await getBatchesByOrder(orderId.value);
      if (res && Array.isArray(res)) {
        batchList.value = res.map(item => ({
          batchId: item.batchId || item.batch_id,
          batchNo: item.batchNo || item.batch_no,
          materialCount: item.materialCount || item.material_count || 0,
          totalRemainingQty: safeNumber(item.totalRemainingQty || item.total_remaining_qty)
        }));
      } else if (res && res.records) {
        batchList.value = res.records.map(item => ({
          batchId: item.batchId || item.batch_id,
          batchNo: item.batchNo || item.batch_no,
          materialCount: item.materialCount || item.material_count || 0,
          totalRemainingQty: safeNumber(item.totalRemainingQty || item.total_remaining_qty)
        }));
      } else {
        batchList.value = [];
      }
    } catch (error: any) {
      createMessage.error('加载批次列表失败: ' + (error.message || '未知错误'));
      batchList.value = [];
    } finally {
      loading.value = false;
    }
  }

  async function confirmBatchSelect() {
    if (selectedBatchIds.value.length === 0) {
      createMessage.warning('请至少选择一个批次');
      return;
    }
    loading.value = true;
    try {
      const res = await getMaterialSummary({
        batchIds: selectedBatchIds.value,
        orderId: orderId.value
      });
      processMaterialData(res);
      batchConfirmed.value = true;
    } catch (error: any) {
      createMessage.error('加载物料汇总失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }

  async function loadBatchMaterialsFromRecords(list: any[]) {
    if (!list || list.length === 0) {
      createMessage.warning('没有选中的记录');
      return;
    }
    loading.value = true;
    try {
      const materialReqIds = list.map(r => r.id).filter(id => id);
      if (materialReqIds.length === 0) {
        mergeMaterialsFallback(list);
        return;
      }
      const res = await getMaterialSummary({ materialReqIds: materialReqIds });
      processMaterialData(res);
    } catch (error: any) {
      mergeMaterialsFallback(list);
    } finally {
      loading.value = false;
    }
  }

  function processMaterialData(res: any) {
    let list: any[] = [];
    if (res && Array.isArray(res)) {
      list = res;
    } else if (res && res.records) {
      list = res.records;
    } else if (res && res.result) {
      list = res.result;
    }

    // 先处理原始物料数据
    const processedList = list.map((item: any) => {
      const batchNosStr = item.batchNos || item.batch_nos || '';
      const batchIdsStr = item.batchIds || item.batch_ids || '';
      const materialReqIdsStr = item.materialReqIds || item.material_req_ids || '';
      const remainingQty = safeNumber(item.remainingQty || item.remaining_qty || 0);

      // 获取物料类型：0-源材料，1-内包，2-外包
      const materialType = item.materialType !== undefined ? item.materialType :
        (item.material_type !== undefined ? item.material_type : MaterialType.SOURCE);

      return {
        materialId: item.materialId || item.material_id,
        materialCode: item.materialCode || item.material_code,
        materialName: item.materialName || item.material_name,
        materialSpec: item.materialSpec || item.material_spec,
        materialType: materialType,
        unit: item.unit,
        remainingQty: remainingQty,
        quantity: remainingQty,
        batchNos: batchNosStr ? batchNosStr.split(',') : [],
        batchIds: batchIdsStr ? batchIdsStr.split(',') : [],
        sourceIds: materialReqIdsStr ? materialReqIdsStr.split(',') : [],
        isOverApply: false,
        remark: ''
      };
    });

    materialList.value = processedList;

    // 展开为可编辑列表
    expandMaterialList(processedList);
  }

  // 修复：展开物料列表，使用 ref 存储以便编辑
  function expandMaterialList(list: any[]) {
    const expanded: any[] = [];
    let uniqueIndex = 0;

    list.forEach(item => {
      const batchIds = item.batchIds || [];
      const batchNos = item.batchNos || [];

      if (batchIds.length > 0) {
        const totalQty = safeNumber(item.quantity);
        const avgQty = safeNumber(totalQty / batchIds.length);
        const lastQty = safeNumber(totalQty - avgQty * (batchIds.length - 1));

        batchIds.forEach((batchId: string, idx: number) => {
          const batchRemaining = safeNumber(item.remainingQty / batchIds.length);

          expanded.push({
            ...item,
            uniqueKey: `${item.materialId}_${batchId}_${uniqueIndex++}`,
            currentBatchId: batchId,
            currentBatchNo: batchNos[idx] || batchId,
            batchQuantity: idx === batchIds.length - 1 ? lastQty : avgQty,
            batchRemainingQty: batchRemaining,
            isSameMaterial: idx > 0,
            isOverApply: false,
            remark: ''
          });
        });
      }
    });

    expandedMaterialList.value = expanded;
  }

  function mergeMaterialsFallback(list: any[]) {
    const map = new Map();
    list.forEach((item: any) => {
      const remaining = safeNumber(item.remainingQty) ||
        safeNumber((Number(item.requiredQty) || 0) - (Number(item.issuedQty) || 0));
      if (remaining <= 0) return;

      const key = item.materialId || item.material_code;
      const materialType = item.materialType !== undefined ? item.materialType :
        (item.material_type !== undefined ? item.material_type : MaterialType.SOURCE);

      if (map.has(key)) {
        const exist = map.get(key);
        exist.remainingQty = safeNumber(exist.remainingQty + remaining);
        exist.quantity = exist.remainingQty;
        exist.sourceIds.push(item.id);
        exist.batchIds.push(item.batchId || item.batch_id);
        exist.batchNos.push(item.batchNo || item.batch_no);
      } else {
        map.set(key, {
          materialId: item.materialId || item.material_id || item.material_code,
          materialCode: item.materialCode || item.material_code,
          materialName: item.materialName || item.material_name,
          materialSpec: item.materialSpec || item.material_spec,
          materialType: materialType,
          unit: item.unit,
          remainingQty: remaining,
          quantity: remaining,
          sourceIds: [item.id],
          batchIds: [item.batchId || item.batch_id],
          batchNos: [item.batchNo || item.batch_no],
          isOverApply: false,
          remark: ''
        });
      }
    });

    const mergedList = Array.from(map.values());
    materialList.value = mergedList;
    expandMaterialList(mergedList);
  }

  function resetBatchSelect() {
    batchConfirmed.value = false;
    selectedBatchIds.value = [];
    materialList.value = [];
    expandedMaterialList.value = [];
  }

  async function handleSubmit() {
    try {
      let submitData: any = {};

      if (applyMode.value === 'single') {
        if (formData.value.quantity <= 0) {
          createMessage.error('申请数量必须大于0');
          return;
        }

        submitData = {
          billType: 'production_issue',
          businessStatus: 'pending',
          productionOrderId: record.value.orderId,
          productionOrderCode: record.value.orderNo,
          expectDate: formData.value.expectDate,
          remark: formData.value.remark,
          detailList: [{
            productionOrderMaterialId: record.value.id,
            productionBatchId: record.value.batchId,
            stockItemCode: record.value.materialCode,
            itemName: record.value.materialName,
            specification: record.value.materialSpec,
            unit: record.value.unit,
            quantity: safeNumber(formData.value.quantity),
            isOverApply: formData.value.quantity > remainingQty.value
          }]
        };
      } else {
        const zeroItems = expandedMaterialList.value.filter((i: any) => safeNumber(i.batchQuantity) <= 0);
        if (zeroItems.length === expandedMaterialList.value.length) {
          createMessage.error('请至少申请一个物料');
          return;
        }

        // 按物料ID分组
        const materialGroup = new Map();
        expandedMaterialList.value.forEach((item: any) => {
          if (safeNumber(item.batchQuantity) <= 0) return;

          if (!materialGroup.has(item.materialId)) {
            materialGroup.set(item.materialId, {
              ...item,
              batchDetails: []
            });
          }
          const group = materialGroup.get(item.materialId);
          group.batchDetails.push({
            batchId: item.currentBatchId,
            batchNo: item.currentBatchNo,
            quantity: safeNumber(item.batchQuantity),
            isOverApply: item.isOverApply,
            remark: item.remark || ''
          });
        });

        submitData = {
          billType: 'production_issue',
          businessStatus: 'pending',
          productionOrderId: orderId.value || orderIds.value[0],
          productionOrderCode: orderNo.value,
          expectDate: commonForm.value.expectDate,
          remark: commonForm.value.remark,
          detailList: Array.from(materialGroup.values()).map((item: any) => ({
            productionBatchId: item.batchDetails[0].batchId,
            stockItemCode: item.materialCode,
            itemName: item.materialName,
            specification: item.materialSpec,
            unit: item.unit,
            quantity: safeNumber(item.batchDetails.reduce((sum: number, bd: any) => sum + bd.quantity, 0)),
            isOverApply: item.batchDetails.some((bd: any) => bd.isOverApply),
            remark: item.batchDetails.find((bd: any) => bd.remark)?.remark || '',
            productionOrderMaterialIds: item.sourceIds?.join(','),
            batchDetailList: item.batchDetails
          }))
        };
      }

      await submitStockOutApply(submitData);
      createMessage.success('出库申请提交成功，等待仓库审核');
      emit('success');
      closeModal();
    } catch (error: any) {
      createMessage.error('提交失败：' + error.message);
    }
  }

  function handleCancel() {
    closeModal();
  }
</script>

<style lang="less" scoped>
  .loading-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;
  }

  .apply-content {
    padding: 16px;
  }

  .section-title {
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 12px;
    color: #333;
  }

  .batch-select-section {
    background: #f5f5f5;
    padding: 16px;
    border-radius: 4px;
    margin-bottom: 16px;
  }

  .batch-checkbox-group {
    width: 100%;
  }

  .material-section {
    background: #fafafa;
    padding: 16px;
    border-radius: 4px;
  }

  .quantity-cell {
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>

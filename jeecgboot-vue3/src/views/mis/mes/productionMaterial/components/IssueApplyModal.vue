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
            <a-form-item label="领料仓库" required>
              <a-select
                v-model:value="warehouseId"
                placeholder="请选择仓库"
                style="width: 100%"
                :options="warehouseOptions.map(w => ({ label: w.name, value: w.id }))"
              />
            </a-form-item>
          </a-col>
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
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="期望领料日期" required>
              <a-date-picker
                v-model:value="formData.requiredDate"
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
          <a-tag v-if="selectedBatchCount > 0" color="blue">已选{{ selectedBatchCount }}个批次</a-tag>
        </div>

        <a-empty v-if="!loading && expandedMaterialList.length === 0" description="暂无待发料物料" />

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
            <template v-if="column.key === 'batchNo'">
              <a-tag color="blue" size="small">{{ row.currentBatchNo }}</a-tag>
            </template>

            <template v-else-if="column.key === 'materialType'">
              <a-tag :color="getMaterialTypeColor(row.materialType)">
                {{ getMaterialTypeText(row.materialType) }}
              </a-tag>
            </template>

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

            <template v-else-if="column.key === 'remark'">
              <a-input
                :value="row.remark"
                :placeholder="row.isOverApply ? '超量原因必填' : '备注'"
                size="small"
                style="width: 140px;"
                @change="(e) => handleRemarkChange(index, e.target.value)"
              />
            </template>

            <template v-else-if="column.key === 'materialName'">
              <span>{{ row.materialName }}</span>
              <a-tag v-if="row.isSameMaterial" color="green" size="small" style="margin-left: 4px;">合并</a-tag>
            </template>
          </template>

          <template #summary v-if="expandedMaterialList.length > 0">
            <a-table-summary>
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

        <a-form :model="formData" layout="vertical" v-if="expandedMaterialList.length > 0">
          <a-row :gutter="24">
            <a-col :span="8">
              <a-form-item label="领料仓库" required>
                <a-select
                  v-model:value="warehouseId"
                  placeholder="请选择仓库"
                  style="width: 100%"
                  :options="warehouseOptions.map(w => ({ label: w.name, value: w.id }))"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="领料人" required>
                <a-select
                  v-model:value="formData.requesterUserId"
                  placeholder="请选择领料人"
                  style="width: 100%"
                  :options="userOptions"
                  @change="handleRequesterChange"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="期望领料日期" required>
                <a-date-picker
                  v-model:value="formData.requiredDate"
                  valueFormat="YYYY-MM-DD"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :span="24">
              <a-form-item label="备注">
                <a-input v-model:value="formData.remark" placeholder="备注" />
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
  import { getBatchesByOrder, getMaterialSummary, submitStockOutApply, getWarehouseList, getSysUserList} from '../ProductionMaterial.api';
  import {LoaderOptions} from "cz-git";


  const { createMessage } = useMessage();
  const emit = defineEmits(['success', 'register']);

  // ==================== 物料类型枚举（字符串值）====================
  const MaterialType = {
    SOURCE: 'RAW',        // 源材料
    INNER_PACK: 'INNER_PACK',  // 内包
    OUTER_PACK: 'OUTER_PACK'     // 外包
  };

  // ==================== 基础状态 ====================
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
  const expandedMaterialList = ref<any[]>([]);
  const loading = ref(false);

  // ==================== 仓库选择 ====================
  const warehouseId = ref<string>('');
  const warehouseOptions = ref<any[]>([]);
  const userOptions = ref<any[]>([]);

  // ==================== 表单数据 ====================
  const formData = ref({
    quantity: 0,
    requiredDate: dayjs().add(1, 'day').format('YYYY-MM-DD'),
    remark: '',
    requesterUserId: '',   // 【新增】领料人ID
    requesterName: ''      // 【新增】领料人姓名
  });

  // const commonForm = ref({
  //   requiredDate: dayjs().add(1, 'day').format('YYYY-MM-DD'),
  //   remark: '',
  //   requesterUserId: '',   // 【新增】领料人ID
  //   requesterName: ''      // 【新增】领料人姓名
  // });

  // ==================== 领料人变化处理 ====================
  function handleRequesterChange(userId: string) {
    const selected = userOptions.value.find(u => u.value === userId);
    formData.value.requesterUserId = userId;
    formData.value.requesterName = selected?.realname || '';
  }

  // ==================== 计算属性 ====================
  const remainingQty = computed(() => {
    if (!record.value) return 0;
    return safeNumber(record.value.remainingQty) ||
      safeNumber((record.value.requiredQty || 0) - (record.value.issuedQty || 0));
  });

  const selectedBatchCount = computed(() => {
    if (applyMode.value === 'batch') {
      return records.value.length;
    }
    return selectedBatchIds.value.length;
  });

  // 【修正】使用字符串比较
  const sourceMaterialSummary = computed(() => {
    const items = expandedMaterialList.value.filter(i => i.materialType === MaterialType.SOURCE);
    const remaining = items.reduce((sum, i) => sum + safeNumber(i.batchRemainingQty), 0);
    const apply = items.reduce((sum, i) => sum + safeNumber(i.batchQuantity), 0);
    return { count: items.length, remaining, apply, isOver: apply > remaining };
  });

  const innerPackageSummary = computed(() => {
    const items = expandedMaterialList.value.filter(i => i.materialType === MaterialType.INNER_PACK);
    const remaining = items.reduce((sum, i) => sum + safeNumber(i.batchRemainingQty), 0);
    const apply = items.reduce((sum, i) => sum + safeNumber(i.batchQuantity), 0);
    return { count: items.length, remaining, apply, isOver: apply > remaining };
  });

  const outerPackageSummary = computed(() => {
    const items = expandedMaterialList.value.filter(i => i.materialType === MaterialType.OUTER_PACK);
    const remaining = items.reduce((sum, i) => sum + safeNumber(i.batchRemainingQty), 0);
    const apply = items.reduce((sum, i) => sum + safeNumber(i.batchQuantity), 0);
    return { count: items.length, remaining, apply, isOver: apply > remaining };
  });

  const totalExpandedRemaining = computed(() => {
    return expandedMaterialList.value.reduce((sum, item) => sum + safeNumber(item.batchRemainingQty), 0);
  });

  const totalExpandedApply = computed(() => {
    return expandedMaterialList.value.reduce((sum, item) => sum + safeNumber(item.batchQuantity), 0);
  });

  const totalIsOver = computed(() => totalExpandedApply.value > totalExpandedRemaining.value);

  const modalTitle = computed(() => {
    const titles: Record<string, string> = {
      single: '出库申请',
      batch: '批量出库申请',
      order: '按订单出库申请'
    };
    return titles[applyMode.value] || '出库申请';
  });

  // ==================== 表格列定义 ====================
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

  // ==================== Modal初始化 ====================
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
    warehouseId.value = '';
    warehouseOptions.value = [];
    userOptions.value = [];
    formData.value = {
      requiredDate: dayjs().add(1, 'day').format('YYYY-MM-DD'),
      remark: '',
      requesterUserId: '',
      requesterName: ''
    };
    await nextTick();

    await loadWarehouseList();
    await loadUserOptions();

    if (applyMode.value === 'single' && record.value.warehouseId) {
      warehouseId.value = record.value.warehouseId;
    }

    if (applyMode.value === 'single') {
      formData.value.quantity = safeNumber(remainingQty.value);
      formData.value.requiredDate = dayjs().add(1, 'day').format('YYYY-MM-DD');
      formData.value.remark = '';
    } else if (applyMode.value === 'batch') {
      await loadBatchMaterialsFromRecords(records.value);
    } else if (applyMode.value === 'order') {
      await loadBatchList();
    }
  });

  // ==================== 工具函数 ====================
  function safeNumber(val: any, precision = 6): number {
    if (!val || isNaN(Number(val))) return 0;
    return Number(Number(val).toFixed(precision));
  }

  function formatNumber(val: any, precision = 2) {
    const num = safeNumber(val, precision);
    return num.toFixed(precision);
  }

  // 【修正】字符串参数
  function getMaterialTypeText(type: string) {
    const map: Record<string, string> = {
      [MaterialType.SOURCE]: '源材料',
      [MaterialType.INNER_PACK]: '内包',
      [MaterialType.OUTER_PACK]: '外包'
    };
    return map[type] || type || '未知';
  }

  function getMaterialTypeColor(type: string) {
    const map: Record<string, string> = {
      [MaterialType.SOURCE]: 'blue',
      [MaterialType.INNER_PACK]: 'green',
      [MaterialType.OUTER_PACK]: 'orange'
    };
    return map[type] || 'default';
  }

  // ==================== 事件处理 ====================
  function handleSingleQtyChange(val: number) {
    formData.value.quantity = safeNumber(val);
    // 【关键】动态更新单条模式的备注
    const isOver = formData.value.quantity > remainingQty.value;
    if (isOver && (!formData.value.remark || formData.value.remark.startsWith('超量申请：'))) {
      const overQty = safeNumber(formData.value.quantity - remainingQty.value);
      formData.value.remark = `超量申请：${formatNumber(overQty)}`;
    } else if (!isOver && formData.value.remark?.startsWith('超量申请：')) {
      formData.value.remark = '';
    }
  }

  function handleQtyChange(index: number, val: number) {
    if (index >= 0 && index < expandedMaterialList.value.length) {
      const item = expandedMaterialList.value[index];
      item.batchQuantity = safeNumber(val);
      item.isOverApply = item.batchQuantity > item.batchRemainingQty;
      // 【关键】动态更新默认备注
      if (item.isOverApply) {
        const overQty = safeNumber(item.batchQuantity - item.batchRemainingQty);
        // 如果用户还没填内容，或之前是自动生成的备注，则更新
        if (!item.remark || item.remark.startsWith('超量申请：')) {
          item.remark = `超量申请：${formatNumber(overQty)}`;
        }
      } else {
        // 不超量时，如果备注是自动生成的，则清空
        if (item.remark && item.remark.startsWith('超量申请：')) {
          item.remark = '';
        }
      }
      expandedMaterialList.value = [...expandedMaterialList.value];
    }
  }

  function handleRemarkChange(index: number, val: string) {
    if (index >= 0 && index < expandedMaterialList.value.length) {
      expandedMaterialList.value[index].remark = val;
      expandedMaterialList.value = [...expandedMaterialList.value];
    }
  }

  // ==================== 数据加载 ====================
  async function loadWarehouseList() {
    try {
      const res = await getWarehouseList({ pageSize: 100 });
      warehouseOptions.value = res.records || [];
    } catch (error) {
      console.error('加载仓库列表失败', error);
    }
  }

  async function loadUserOptions(){
    try{
      const res = await getSysUserList({pageSize:100, status:'1'});
      userOptions.value = (res.records || []).map((u: any) => ({
        label: u.realname + (u.username ? `(${u.username})` : ''),
        value: u.id,
        realname: u.realname
      }));
    }catch(error){
      console.error('加载用户列表失败', error);
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
      batchList.value = (res.records || res || []).map((item: any) => ({
        batchId: item.batchId || item.batch_id,
        batchNo: item.batchNo || item.batch_no,
        materialCount: item.materialCount || item.material_count || 0,
        totalRemainingQty: safeNumber(item.totalRemainingQty || item.total_remaining_qty)
      }));
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
      const res = await getMaterialSummary({ materialReqIds });
      processMaterialData(res);
    } catch (error) {
      mergeMaterialsFallback(list);
    } finally {
      loading.value = false;
    }
  }

  function processMaterialData(res: any) {
    let list: any[] = res.records || res.result || res || [];

    const processedList = list.map((item: any) => {
      const batchNosStr = item.batchNos || item.batch_nos || '';
      const batchIdsStr = item.batchIds || item.batch_ids || '';
      const materialReqIdsStr = item.materialReqIds || item.material_req_ids || '';

      // 【修正】保持原始字符串值 RAW/INNER_PACK/OUTER_PACK
      const materialType = item.materialType || item.material_type || MaterialType.SOURCE;

      return {
        materialId: item.materialId || item.material_id,
        materialCode: item.materialCode || item.material_code,
        materialName: item.materialName || item.material_name,
        materialSpec: item.materialSpec || item.material_spec,
        materialType: materialType,  // 字符串值
        unit: item.unit,
        remainingQty: safeNumber(item.remainingQty || item.remaining_qty),
        quantity: safeNumber(item.remainingQty || item.remaining_qty),
        batchNos: batchNosStr ? batchNosStr.split(',') : [],
        batchIds: batchIdsStr ? batchIdsStr.split(',') : [],
        sourceIds: materialReqIdsStr ? materialReqIdsStr.split(',') : [],
        warehouseId: item.warehouseId || item.warehouse_id,
        isOverApply: false,
        remark: ''
      };
    });

    materialList.value = processedList;
    expandMaterialList(processedList);

    if (processedList.length > 0 && processedList[0].warehouseId) {
      warehouseId.value = processedList[0].warehouseId;
    }
  }

  function expandMaterialList1(list: any[]) {
    const expanded: any[] = [];
    let uniqueIndex = 0;

    list.forEach(item => {
      const batchIds = item.batchIds || [];
      const batchNos = item.batchNos || [];
      const sourceIds = item.sourceIds || [];  // 修复 需求id重复

      if (batchIds.length === 0) return;

      const totalQty = safeNumber(item.quantity);

      batchIds.forEach((batchId: string, idx: number) => {
        const batchRemaining = safeNumber(item.remainingQty / batchIds.length);
        const avgQty = safeNumber(totalQty / batchIds.length);
        const isLast = idx === batchIds.length - 1;
        const allocQty = isLast
          ? safeNumber(totalQty - avgQty * (batchIds.length - 1))
          : avgQty;

        expanded.push({
          ...item,
          uniqueKey: `${item.materialId}_${batchId}_${uniqueIndex++}`,
          currentBatchId: batchId,
          currentBatchNo: batchNos[idx] || batchId,
          currentSourceId: sourceIds[idx] || sourceIds[0] || '',  // 【关键】取对应索引的sourceId即 物料需求表id
          batchQuantity: allocQty,
          batchRemainingQty: batchRemaining,
          isSameMaterial: idx > 0,
          isOverApply: false,
          remark: ''
        });
      });
    });

    expandedMaterialList.value = expanded;
  }

  function expandMaterialList(list: any[]) {
    const expanded: any[] = [];
    let uniqueIndex = 0;

    list.forEach(item => {
      const batchIds = item.batchIds || [];
      const batchNos = item.batchNos || [];
      const sourceIds = item.sourceIds || [];

      if (batchIds.length === 0) return;

      const totalQty = safeNumber(item.quantity);
      const avgQty = safeNumber(totalQty / batchIds.length);

      batchIds.forEach((batchId: string, idx: number) => {
        const isLast = idx === batchIds.length - 1;
        const allocQty = isLast
          ? safeNumber(totalQty - avgQty * (batchIds.length - 1))
          : avgQty;

        const batchRemaining = safeNumber(item.remainingQty / batchIds.length);
        const isOverApply = allocQty > batchRemaining;

        // 【关键】初始化时就设置默认备注
        let defaultRemark = '';
        if (isOverApply) {
          const overQty = safeNumber(allocQty - batchRemaining);
          defaultRemark = `超量申请：${formatNumber(overQty)}`;
        }

        expanded.push({
          ...item,
          uniqueKey: `${item.materialId}_${batchId}_${uniqueIndex++}`,
          currentBatchId: batchId,
          currentBatchNo: batchNos[idx] || batchId,
          currentSourceId: sourceIds[idx] || sourceIds[0] || '',
          batchQuantity: allocQty,
          batchRemainingQty: batchRemaining,//safeNumber(item.remainingQty / batchIds.length),
          isSameMaterial: idx > 0,
          isOverApply: isOverApply,//allocQty > safeNumber(item.remainingQty / batchIds.length),
          remark: defaultRemark  // 【关键】设置默认备注
        });
      });
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
          materialType: item.materialType || item.material_type || MaterialType.SOURCE,
          unit: item.unit,
          remainingQty: remaining,
          quantity: remaining,
          sourceIds: [item.id],
          batchIds: [item.batchId || item.batch_id],
          batchNos: [item.batchNo || item.batch_no],
          warehouseId: item.warehouseId || item.warehouse_id,
          isOverApply: false,
          remark: ''
        });
      }
    });

    const mergedList = Array.from(map.values());
    materialList.value = mergedList;
    expandMaterialList(mergedList);

    if (mergedList.length > 0 && mergedList[0].warehouseId) {
      warehouseId.value = mergedList[0].warehouseId;
    }
  }

  function resetBatchSelect() {
    batchConfirmed.value = false;
    selectedBatchIds.value = [];
    materialList.value = [];
    expandedMaterialList.value = [];
  }

  // ==================== 提交处理 ====================
  async function handleSubmit() {
    try {
      if (!warehouseId.value) {
        createMessage.error('请选择领料仓库');
        return;
      }
      if (!formData.value.requesterUserId) {
        createMessage.error('请选择领料人');
        return;
      }
      let submitData: any = {};

      if (applyMode.value === 'single') {
        submitData = buildSingleSubmitData();
      } else {
        const batchCount = selectedBatchCount.value;
        if (batchCount === 1) {
          submitData = buildSingleBatchSubmitData();
        } else {
          submitData = buildMultiBatchSubmitData();
        }
      }

      console.log('提交数据:', submitData);
      await submitStockOutApply(submitData);
      createMessage.success('出库申请提交成功，等待仓库审核');
      emit('success');
      closeModal();
    } catch (error: any) {
      createMessage.error('提交失败：' + error.message);
    }
  }

  // 单条申请
  function buildSingleSubmitData(): any {
    if (formData.value.quantity <= 0) {
      throw new Error('申请数量必须大于0');
    }

    const isOver = formData.value.quantity > remainingQty.value;
    const overQty = isOver ? safeNumber(formData.value.quantity - remainingQty.value) : 0;
    // 自动生成备注兜底：如果用户没填且超量，自动生成
    const remark = formData.value.remark || (isOver ? `超量申请：${formatNumber(overQty)}` : '');
    return {
      stockOutType: 'PRODUCTION',
      status: 'APPLY',
      warehouseId: warehouseId.value,
      sourceOrderId: record.value.orderId,
      sourceOrderCode: record.value.orderNo,
      requiredDate: formData.value.requiredDate,
      remark: formData.value.remark,
      stockOutDetailList: [{
        goodsId: record.value.materialId,
        goodsCode: record.value.materialCode,
        goodsName: record.value.materialName,
        goodsSpec: record.value.materialSpec,
        // 【修正】保持字符串值 RAW/INNER_PACK/OUTER_PACK
        goodsType: record.value.materialType || MaterialType.SOURCE,
        unit: record.value.unit,
        applyQty: safeNumber(formData.value.quantity),
        actualQty: safeNumber(formData.value.quantity),
        overFlag: isOver ? '1' : '0',
        overQty: overQty,
        productionBatchId: record.value.batchId,
        productionBatchNo: record.value.batchNo,
        requirementId: record.value.id,
        remark: remark
      }]
    };
  }

  // 单批次
  function buildSingleBatchSubmitData(): any {
    const validItems = expandedMaterialList.value.filter(
      (i: any) => safeNumber(i.batchQuantity) > 0
    );

    if (validItems.length === 0) {
      throw new Error('请至少申请一个物料');
    }

    const firstItem = validItems[0];
    const batchId = firstItem.currentBatchId;

    return {
      stockOutType: 'PRODUCTION',
      status: 'APPLY',
      warehouseId: warehouseId.value,
      requesterUserId: formData.value.requesterUserId,  // 【新增】
      requesterName: formData.value.requesterName,      // 【新增】
      sourceOrderId: orderId.value || orderIds.value[0],
      sourceOrderCode: orderNo.value,
      requiredDate: formData.value.requiredDate,
      remark: formData.value.remark,
      stockOutDetailList: validItems.map((item: any) => {
        const isOver = item.batchQuantity > item.batchRemainingQty;

        return {
          goodsId: item.materialId,
          goodsCode: item.materialCode,
          goodsName: item.materialName,
          goodsSpec: item.materialSpec,
          goodsType: item.materialType,  // 字符串值
          unit: item.unit,
          applyQty: safeNumber(item.batchQuantity),
          actualQty: safeNumber(item.batchQuantity),
          overFlag: isOver ? '1' : '0',
          overQty: isOver ? safeNumber(item.batchQuantity - item.batchRemainingQty) : 0,
          productionBatchId: batchId,
          productionBatchNo: item.currentBatchNo,
          requirementId: item.sourceIds?.[0],
          remark: item.remark || (isOver ? `超量申请：${formatNumber(safeNumber(item.batchQuantity - item.batchRemainingQty))}` : '')
        };
      })
    };
  }


  function buildMultiBatchSubmitData(): any {
    const validItems = expandedMaterialList.value.filter(
      (i: any) => safeNumber(i.batchQuantity) > 0
    );

    if (validItems.length === 0) {
      throw new Error('请至少申请一个物料');
    }

    return {
      stockOutType: 'PRODUCTION',
      status: 'APPLY',
      warehouseId: warehouseId.value,
      requesterUserId: formData.value.requesterUserId,  // 【新增】
      requesterName: formData.value.requesterName,      // 【新增】
      sourceOrderId: orderId.value || orderIds.value[0],
      sourceOrderCode: orderNo.value,
      requiredDate: formData.value.requiredDate,
      remark: formData.value.remark,
      // 每行独立，不合并
      stockOutDetailList: validItems.map((item: any) => ({

        goodsId: item.materialId,
        goodsCode: item.materialCode,
        goodsName: item.materialName,
        goodsSpec: item.materialSpec,
        goodsType: item.materialType,
        unit: item.unit,
        applyQty: safeNumber(item.batchQuantity),
        actualQty: safeNumber(item.batchQuantity),
        overFlag: item.isOverApply ? '1' : '0',           // 【加】超量标识
        overQty: item.isOverApply                       // 【加】超量数量
          ? safeNumber(item.batchQuantity - item.batchRemainingQty)
          : 0,
        productionBatchId: item.currentBatchId,
        productionBatchNo: item.currentBatchNo,
        requirementId: item.currentSourceId,
        remark: item.remark || (item.isOverApply ? `超量申请：${formatNumber(safeNumber(item.batchQuantity - item.batchRemainingQty))}` : '')
      }))
    };
  }
  // 多批次合并
  function buildMultiBatchSubmitData1(): any {
    const validItems = expandedMaterialList.value.filter(
      (i: any) => safeNumber(i.batchQuantity) > 0
    );

    if (validItems.length === 0) {
      throw new Error('请至少申请一个物料');
    }

    const materialGroup = new Map();
    validItems.forEach((item: any) => {
      if (!materialGroup.has(item.materialId)) {
        materialGroup.set(item.materialId, {
          materialId: item.materialId,
          materialCode: item.materialCode,
          materialName: item.materialName,
          materialSpec: item.materialSpec,
          materialType: item.materialType,  // 字符串值
          unit: item.unit,
          remainingQty: 0,
          batchDetails: []
        });
      }

      const group = materialGroup.get(item.materialId);
      group.remainingQty += item.batchRemainingQty;
      group.batchDetails.push({
        batchId: item.currentBatchId,
        batchNo: item.currentBatchNo,
        quantity: safeNumber(item.batchQuantity),
       // sourceId: item.sourceIds?.[0],
        sourceId: item.currentSourceId,  // 【改】原来是 item.sourceIds?.[0]
        remark: item.remark || ''
      });
    });

    return {
      stockOutType: 'PRODUCTION',
      status: 'APPLY',
      warehouseId: warehouseId.value,
      sourceOrderId: orderId.value || orderIds.value[0],
      sourceOrderCode: orderNo.value,
      requiredDate: formData.value.requiredDate,
      remark: formData.value.remark,
      stockOutDetailList: Array.from(materialGroup.values()).map((item: any) => {
        const totalApply = safeNumber(
          item.batchDetails.reduce((sum: number, bd: any) => sum + bd.quantity, 0)
        );
        const isOver = totalApply > item.remainingQty;

        const remarks: string[] = [];
        if (isOver) {
          remarks.push(`超量申请: ${(totalApply - item.remainingQty).toFixed(2)}`);
        }
        const detailRemarks = item.batchDetails
          .map((bd: any) => bd.remark)
          .filter((r: string) => r && r !== '超量申请');
        if (detailRemarks.length > 0) {
          remarks.push(detailRemarks.join(';'));
        }

        return {
          goodsId: item.materialId,
          goodsCode: item.materialCode,
          goodsName: item.materialName,
          goodsSpec: item.materialSpec,
          goodsType: item.materialType,  // 字符串值
          unit: item.unit,
          applyQty: totalApply,
          actualQty: totalApply,
          productionBatchId: item.batchDetails.map((bd: any) => bd.batchId).join(','),
         // batchNo: item.batchDetails.map((bd: any) => bd.batchNo).join(','),
          requirementId: item.batchDetails.map((bd: any) => bd.sourceId).filter(Boolean).join(','),
          remark: remarks.join(' | ')
        };
      })
    };
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

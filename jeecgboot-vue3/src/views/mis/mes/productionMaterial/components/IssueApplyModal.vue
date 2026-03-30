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
          <span style="color: #f5222d; font-weight: bold;">{{ remainingQty }}</span>
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
              />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="formData.quantity > remainingQty">
            <a-form-item label="超量原因" required>
              <a-input v-model:value="formData.overReason" placeholder="请填写超量原因" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
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

        <!-- 空状态 -->
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

        <!-- 空状态 -->
        <a-empty v-if="!loading && materialList.length === 0" description="暂无待发料物料" />

        <a-table
          v-else
          :dataSource="materialList"
          :columns="materialColumns"
          :pagination="false"
          size="small"
          bordered
          :scroll="{ y: 300 }"
          rowKey="materialId"
        >
          <template #bodyCell="{ column, record: row }">
            <template v-if="column.key === 'quantity'">
              <a-input-number
                v-model:value="row.quantity"
                :min="0"
                :precision="6"
                style="width: 100px"
                @change="(val) => handleQtyChange(row, val)"
              />
              <a-tag v-if="row.isOverApply" color="orange" style="margin-left: 8px;">超量</a-tag>
            </template>

            <template v-else-if="column.key === 'overReason'">
              <a-input
                v-if="row.isOverApply"
                v-model:value="row.overReason"
                placeholder="填写超量原因"
                size="small"
                style="width: 150px;"
              />
              <span v-else>-</span>
            </template>

            <template v-else-if="column.key === 'batchInfo'">
              <div style="max-width: 180px; word-break: break-all;">
                <a-tag v-for="(bn, idx) in row.batchNos" :key="idx" size="small" style="margin: 2px;">
                  {{ bn }}
                </a-tag>
              </div>
            </template>
          </template>

          <template #summary v-if="materialList.length > 0">
            <a-table-summary>
              <a-table-summary-row>
                <a-table-summary-cell :col-span="3">合计</a-table-summary-cell>
                <a-table-summary-cell>{{ totalRemaining }}</a-table-summary-cell>
                <a-table-summary-cell>{{ totalApply }}</a-table-summary-cell>
                <a-table-summary-cell :col-span="2"></a-table-summary-cell>
              </a-table-summary-row>
            </a-table-summary>
          </template>
        </a-table>

        <a-divider v-if="materialList.length > 0" />

        <a-form :model="commonForm" layout="vertical" v-if="materialList.length > 0">
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

        <!-- 重新选择按钮（按订单模式显示） -->
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
    overReason: '',
    expectDate: dayjs().add(1, 'day').format('YYYY-MM-DD'),
    remark: ''
  });

  const commonForm = ref({
    expectDate: dayjs().add(1, 'day').format('YYYY-MM-DD'),
    remark: ''
  });

  const remainingQty = computed(() => {
    if (!record.value) return 0;
    return (record.value.remainingQty) ||
      ((record.value.requiredQty || 0) - (record.value.issuedQty || 0));
  });

  const totalRemaining = computed(() => {
    return materialList.value.reduce((sum, item) => sum + (Number(item.remainingQty) || 0), 0).toFixed(2);
  });

  const totalApply = computed(() => {
    return materialList.value.reduce((sum, item) => sum + (Number(item.quantity) || 0), 0).toFixed(2);
  });

  const modalTitle = computed(() => {
    const titles: Record<string, string> = {
      single: '出库申请',
      batch: '批量出库申请',
      order: '按订单出库申请'
    };
    return titles[applyMode.value] || '出库申请';
  });

  const materialColumns = [
    { title: '物料编码', dataIndex: 'materialCode', key: 'materialCode', width: 100 },
    { title: '物料名称', dataIndex: 'materialName', key: 'materialName', width: 150 },
    { title: '规格型号', dataIndex: 'materialSpec', key: 'materialSpec', width: 120 },
    { title: '剩余待发', dataIndex: 'remainingQty', key: 'remainingQty', width: 100, align: 'right' },
    { title: '本次申请', key: 'quantity', width: 150, align: 'center' },
    { title: '超量原因', key: 'overReason', width: 180 },
    { title: '涉及批次', key: 'batchInfo', width: 200 },
  ];

  const [registerModal, { closeModal, setModalProps }] = useModalInner(async (data) => {
    console.log('Modal opened with data:', data);

    // 先重置所有状态
    applyMode.value = data.mode || 'single';
    record.value = data.record || {};
    records.value = data.records || [];
    orderId.value = data.orderId || '';
    orderNo.value = data.orderNo || '';
    orderIds.value = data.orderIds || (data.orderId ? [data.orderId] : []);

    batchConfirmed.value = false;
    selectedBatchIds.value = [];
    materialList.value = [];
    batchList.value = [];

    // 使用 nextTick 确保弹窗渲染后再加载数据
    await nextTick();

    if (applyMode.value === 'single') {
      formData.value.quantity = remainingQty.value;
      formData.value.expectDate = dayjs().add(1, 'day').format('YYYY-MM-DD');
      formData.value.overReason = '';
      formData.value.remark = '';
    } else if (applyMode.value === 'batch') {
      // 批量模式：直接使用选中的记录生成汇总
      await loadBatchMaterialsFromRecords(records.value);
    } else if (applyMode.value === 'order') {
      // 按订单模式：先加载批次列表
      await loadBatchList();
    }
  });

  // 格式化数字
  function formatNumber(val: any) {
    if (!val) return '0';
    return Number(val).toFixed(2);
  }

  // 根据订单加载批次列表
  async function loadBatchList() {
    if (!orderId.value) {
      createMessage.error('订单ID为空');
      return;
    }

    loading.value = true;
    console.log('Loading batches for order:', orderId.value);

    try {
      const res = await getBatchesByOrder(orderId.value);
      console.log('Batches loaded:', res);

      // 处理返回数据
      if (res && Array.isArray(res)) {
        batchList.value = res.map(item => ({
          batchId: item.batchId || item.batch_id,
          batchNo: item.batchNo || item.batch_no,
          materialCount: item.materialCount || item.material_count || 0,
          totalRemainingQty: item.totalRemainingQty || item.total_remaining_qty || 0
        }));
      } else if (res && res.records) {
        batchList.value = res.records.map(item => ({
          batchId: item.batchId || item.batch_id,
          batchNo: item.batchNo || item.batch_no,
          materialCount: item.materialCount || item.material_count || 0,
          totalRemainingQty: item.totalRemainingQty || item.total_remaining_qty || 0
        }));
      } else {
        batchList.value = [];
      }

      console.log('Processed batchList:', batchList.value);
    } catch (error) {
      console.error('加载批次列表失败:', error);
      createMessage.error('加载批次列表失败: ' + (error.message || '未知错误'));
      batchList.value = [];
    } finally {
      loading.value = false;
    }
  }

  // 确认批次选择，加载物料汇总
  async function confirmBatchSelect() {
    if (selectedBatchIds.value.length === 0) {
      createMessage.warning('请至少选择一个批次');
      return;
    }

    loading.value = true;
    console.log('Loading materials for batches:', selectedBatchIds.value);

    try {
      const res = await getMaterialSummary({
        batchIds: selectedBatchIds.value,
        orderId: orderId.value
      });

      console.log('Materials loaded:', res);
      processMaterialData(res);
      batchConfirmed.value = true;
    } catch (error) {
      console.error('加载物料汇总失败:', error);
      createMessage.error('加载物料汇总失败: ' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }

  // 批量模式：从选中的记录直接生成物料汇总
  async function loadBatchMaterialsFromRecords(list: any[]) {
    if (!list || list.length === 0) {
      createMessage.warning('没有选中的记录');
      return;
    }

    loading.value = true;
    console.log('Loading materials from records:', list.length);

    try {
      // 提取所有选中的记录ID
      const materialReqIds = list.map(r => r.id).filter(id => id);
      console.log('Material req IDs:', materialReqIds);

      if (materialReqIds.length === 0) {
        // 降级：前端合并
        mergeMaterialsFallback(list);
        return;
      }

      // 调用后端接口获取汇总数据
      const res = await getMaterialSummary({ materialReqIds: materialReqIds });
      console.log('Materials from API:', res);

      processMaterialData(res);
    } catch (error) {
      console.error('加载物料汇总失败，使用前端合并:', error);
      // 降级：前端合并
      mergeMaterialsFallback(list);
    } finally {
      loading.value = false;
    }
  }

  // 处理后端返回的物料数据
  function processMaterialData(res: any) {
    let list: any[] = [];

    if (res && Array.isArray(res)) {
      list = res;
    } else if (res && res.records) {
      list = res.records;
    } else if (res && res.result) {
      list = res.result;
    }

    console.log('Processing material list:', list);

    materialList.value = list.map((item: any) => {
      // 处理字段名映射（下划线转驼峰）
      const batchNosStr = item.batchNos || item.batch_nos || '';
      const batchIdsStr = item.batchIds || item.batch_ids || '';
      const materialReqIdsStr = item.materialReqIds || item.material_req_ids || '';

      return {
        materialId: item.materialId || item.material_id,
        materialCode: item.materialCode || item.material_code,
        materialName: item.materialName || item.material_name,
        materialSpec: item.materialSpec || item.material_spec,
        unit: item.unit,
        remainingQty: Number(item.remainingQty || item.remaining_qty || 0),
        quantity: Number(item.remainingQty || item.remaining_qty || 0),
        batchNos: batchNosStr ? batchNosStr.split(',') : [],
        batchIds: batchIdsStr ? batchIdsStr.split(',') : [],
        sourceIds: materialReqIdsStr ? materialReqIdsStr.split(',') : [],
        isOverApply: false,
        overReason: ''
      };
    });

    console.log('Processed materialList:', materialList.value);
  }

  // 前端合并物料的降级方案
  function mergeMaterialsFallback(list: any[]) {
    console.log('Using fallback merge for:', list.length, 'records');
    const map = new Map();

    list.forEach((item: any) => {
      const remaining = Number(item.remainingQty) || ((Number(item.requiredQty) || 0) - (Number(item.issuedQty) || 0));
      if (remaining <= 0) return;

      const key = item.materialId || item.material_code;
      if (map.has(key)) {
        const exist = map.get(key);
        exist.remainingQty += remaining;
        exist.quantity += remaining;
        exist.sourceIds.push(item.id);
        exist.batchIds.push(item.batchId || item.batch_id);
        exist.batchNos.push(item.batchNo || item.batch_no);
      } else {
        map.set(key, {
          materialId: item.materialId || item.material_id || item.material_code,
          materialCode: item.materialCode || item.material_code,
          materialName: item.materialName || item.material_name,
          materialSpec: item.materialSpec || item.material_spec,
          unit: item.unit,
          remainingQty: remaining,
          quantity: remaining,
          sourceIds: [item.id],
          batchIds: [item.batchId || item.batch_id],
          batchNos: [item.batchNo || item.batch_no],
          isOverApply: false,
          overReason: ''
        });
      }
    });

    materialList.value = Array.from(map.values());
    console.log('Fallback merged materials:', materialList.value);
  }

  // 重置批次选择
  function resetBatchSelect() {
    batchConfirmed.value = false;
    selectedBatchIds.value = [];
    materialList.value = [];
  }

  function handleQtyChange(row: any, val: number) {
    row.isOverApply = val > row.remainingQty;
    if (!row.isOverApply) {
      row.overReason = '';
    }
  }
  // 如果同一物料有多个批次，拆分成多行
  function expandMaterialsByBatch(materials: any[]) {
    const expanded: any[] = [];

    materials.forEach(item => {
      if (item.batchIds && item.batchIds.length > 1) {
        // 有多个批次，拆分
        // 需要按批次比例分配数量，或平均分配
        const qtyPerBatch = item.quantity / item.batchIds.length;

        item.batchIds.forEach((batchId: string, index: number) => {
          expanded.push({
            ...item,
            productionBatchId: batchId,
            productionBatchNo: item.batchNos[index],
            quantity: index === item.batchIds.length - 1
              ? item.quantity - qtyPerBatch * (item.batchIds.length - 1)  // 最后一个拿余数
              : qtyPerBatch,
            // 记录这是拆分出来的
            isSplit: true,
            originalMaterialId: item.materialId
          });
        });
      } else {
        // 只有一个批次，保持原样
        expanded.push({
          ...item,
          productionBatchId: item.batchIds?.[0],
          productionBatchNo: item.batchNos?.[0]
        });
      }
    });

    return expanded;
  }

  async function handleSubmit() {
    try {
      let submitData: any = {};

      if (applyMode.value === 'single') {
        if (formData.value.quantity <= 0) {
          createMessage.error('申请数量必须大于0');
          return;
        }
        if (formData.value.quantity > remainingQty.value && !formData.value.overReason) {
          createMessage.error('超量申请需填写原因');
          return;
        }
// 展开成多行
        const detailList = expandMaterialsByBatch(materialList.value).map(item => ({
          productionOrderMaterialId: item.productionOrderMaterialId || item.sourceIds?.[0],
          productionBatchId: item.productionBatchId,
          productionBatchNo: item.productionBatchNo,
          stockItemCode: item.materialCode,
          itemName: item.materialName,
          specification: item.materialSpec,
          unit: item.unit,
          quantity: item.quantity,
          overReason: item.isOverApply ? item.overReason : null
        }));

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
            quantity: formData.value.quantity,
            overReason: formData.value.quantity > remainingQty.value ? formData.value.overReason : null
          }]
        };
      } else {
        const overItems = materialList.value.filter((i: any) => i.isOverApply && !i.overReason);
        if (overItems.length > 0) {
          createMessage.error(`物料 ${overItems[0].materialCode} 超量申请需填写原因`);
          return;
        }

        submitData = {
          billType: 'production_issue',
          businessStatus: 'pending',
          productionOrderId: orderId.value || orderIds.value[0],
          productionOrderCode: orderNo.value,
          expectDate: commonForm.value.expectDate,
          remark: commonForm.value.remark,
          detailList: materialList.value.map((item: any) => ({
            productionBatchId: item.batchIds?.[0],
            stockItemCode: item.materialCode,
            itemName: item.materialName,
            specification: item.materialSpec,
            unit: item.unit,
            quantity: item.quantity,
            overReason: item.isOverApply ? item.overReason : null,
            productionOrderMaterialIds: item.sourceIds?.join(',')
          }))
        };
      }

      console.log('Submitting:', submitData);
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
</style>

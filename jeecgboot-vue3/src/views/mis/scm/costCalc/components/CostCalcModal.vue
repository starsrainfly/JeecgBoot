<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    width="1000px"
    :minHeight="500"
    :showOkBtn="mode === 'calc'"
    okText="保存快照"
    @ok="handleSaveSnapshot"
  >
    <div class="cost-calc-modal">
      <!-- 产品/配方信息卡片 -->
      <a-descriptions :column="3" size="small" bordered class="info-card">
        <a-descriptions-item label="产品编码">{{ detail.productCode }}</a-descriptions-item>
        <a-descriptions-item label="产品名称">{{ detail.productName }}</a-descriptions-item>
        <a-descriptions-item label="规格型号">{{ detail.productSpec || '-' }}</a-descriptions-item>
        <a-descriptions-item label="颜色">{{ detail.productColor || '-' }}</a-descriptions-item>
        <a-descriptions-item label="配方编号">{{ detail.recipeCode }}</a-descriptions-item>
        <a-descriptions-item label="配方版本">{{ detail.recipeVersion }}</a-descriptions-item>
        <a-descriptions-item label="总配比">{{ detail.proportionTotal || '100' }}</a-descriptions-item>
        <a-descriptions-item label="比例类型">
          <a-tag :color="detail.proportionType === '1' ? 'blue' : 'orange'">
            {{ proportionTypeMap[detail.proportionType] || detail.proportionType || '-' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="单位成本">
          <span style="color: #1890ff; font-weight: bold;">{{ formatNum(detail.totalCostAvg, 4) }}</span> 元/kg
        </a-descriptions-item>
      </a-descriptions>

      <!-- 明细表格 -->
      <a-table
        :columns="columns"
        :data-source="detail.materialList"
        :pagination="false"
        size="small"
        bordered
        class="detail-table"
        :scroll="{ x: 900 }"
      >
        <template #bodyCell="{ column, record }">
          <!-- 占比列：配比 ÷ 总配比 × 100 -->
          <template v-if="column.key === 'ratio'">
            <span v-if="record.proportion != null && detail.proportionTotal > 0">
              {{ ((Number(record.proportion) / Number(detail.proportionTotal)) * 100).toFixed(2) }}%
            </span>
            <span v-else>--</span>
          </template>

          <template v-if="column.key === 'priceSource'">
            <a-tag :color="record.priceSource === 'NONE' ? 'red' : 'green'">
              {{ priceSourceMap[record.priceSource] || record.priceSource }}
            </a-tag>
          </template>

          <template v-if="['avgPrice', 'latestPrice', 'calcPrice', 'amount'].includes(column.key)">
            <span v-if="record.priceSource === 'NONE'" style="color: #ff4d4f; font-weight: bold">--</span>
            <span v-else>{{ formatNum(record[column.key], 4) }}</span>
          </template>

          <template v-if="column.key === 'proportion'">
            {{ record.proportion ? Number(record.proportion).toFixed(4) : '-' }}
          </template>
        </template>
      </a-table>

      <!-- 未定价警告 -->
      <a-alert
        v-if="detail.hasUnpriced"
        message="含未定价物料，合计非完整成本"
        type="warning"
        show-icon
        style="margin-bottom: 12px"
      />

      <!-- 合计栏 -->
      <div class="summary-bar">
        <a-space size="large">
          <span>
            <b>最新成本合计：</b>
            <span class="amount">{{ formatNum(detail.totalCostLatest, 4) }}</span> 元/kg
          </span>
          <span>
            <b>平均成本合计：</b>
            <span class="amount">{{ formatNum(detail.totalCostAvg, 4) }}</span> 元/kg
          </span>
        </a-space>
      </div>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { calculateCost, getSnapshotDetail, saveSnapshot } from '../CostCalc.api';

  const { createMessage, createConfirm } = useMessage();

  const mode = ref<'calc' | 'snapshot'>('calc');
  const productId = ref('');
  const calcId = ref('');
  const detail = ref<any>({ materialList: [], proportionTotal: 100 });

  const modalTitle = computed(() => (mode.value === 'calc' ? '材料成本核算' : '快照明细查看'));

  // 价格来源字典
  const priceSourceMap: Record<string, string> = {
    AVG: '库存均价',
    LATEST: '最新入库',
    NONE: '未定价',
  };

  // 配比类型字典：1-标准（强制100%），2-特殊（允许≠100%）
  const proportionTypeMap: Record<string, string> = {
    '1': '标准（强制100%）',
    '2': '特殊（允许≠100%）',
  };

  const columns = [
    { title: '序号', dataIndex: 'serialNo', width: 50, align: 'center', customRender: ({ index }: any) => index + 1 },
    { title: '物料编码', dataIndex: 'materialCode', width: 100 },
    { title: '物料名称', dataIndex: 'materialName', width: 130 },
    { title: '规格型号', dataIndex: 'materialSpec', width: 100 },
    { title: '配比', dataIndex: 'proportion', key: 'proportion', width: 80, align: 'right' },
    { title: '占比(%)', key: 'ratio', width: 80, align: 'right' },
    { title: '单位', dataIndex: 'unit', width: 60, align: 'center' },
    { title: '库存均价', dataIndex: 'avgPrice', key: 'avgPrice', width: 110, align: 'right' },
    { title: '最新入库价', dataIndex: 'latestPrice', key: 'latestPrice', width: 110, align: 'right' },
    { title: '取价来源', dataIndex: 'priceSource', key: 'priceSource', width: 90, align: 'center' },
    { title: '计算单价', dataIndex: 'calcPrice', key: 'calcPrice', width: 110, align: 'right' },
    { title: '金额(元)', dataIndex: 'amount', key: 'amount', width: 110, align: 'right' },
  ];

  function formatNum(val: any, digits = 4) {
    if (val === null || val === undefined) return '-';
    const n = Number(val);
    return isNaN(n) ? '-' : n.toFixed(digits);
  }

  const [registerModal, { closeModal, setModalProps }] = useModalInner(async (data: any) => {
    mode.value = data.mode || 'calc';
    productId.value = data.productId || '';
    calcId.value = data.calcId || '';
    setModalProps({ confirmLoading: true });
    try {
      const res = mode.value === 'calc'
        ? await calculateCost(productId.value)
        : await getSnapshotDetail(calcId.value);
      detail.value = res.result || res;
    } finally {
      setModalProps({ confirmLoading: false });
    }
  });

  async function handleSaveSnapshot() {
    createConfirm({
      iconType: 'warning',
      title: '保存快照',
      content: '确认保存当前成本核算结果？',
      onOk: async () => {
        await saveSnapshot({ productId: productId.value, calcType: 'MANUAL', remark: '' });
        createMessage.success('快照保存成功');
        closeModal();
      },
    });
  }
</script>

<style scoped>
  .cost-calc-modal { padding: 8px 0; }
  .info-card { margin-bottom: 16px; }
  .detail-table { margin-bottom: 12px; }
  .summary-bar {
    background: #f6ffed;
    border: 1px solid #b7eb8f;
    padding: 12px 16px;
    border-radius: 4px;
  }
  .amount { color: #1890ff; font-size: 16px; font-weight: bold; }
</style>

<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="选择计划明细"
    width="1100px"
    :zIndex="2000"
    :okButtonProps="{ disabled: !canConfirm }"
    @ok="handleConfirm"
    @cancel="handleCancel"
  >
    <!-- 提示信息 -->
    <a-alert
      v-if="restrictInfo"
      :message="restrictInfo"
      type="info"
      show-icon
      style="margin-bottom: 10px;"
    />

    <!-- 查询条件 -->
    <div style="display: flex; align-items: center; margin-bottom: 16px; gap: 16px; flex-wrap: wrap;">
      <div style="display: flex; align-items: center;">
        <span style="margin-right: 8px; white-space: nowrap;">计划编号：</span>
        <a-input
          v-model:value="queryParam.planNo"
          placeholder="输入计划编号筛选"code
          allowClear
          style="width: 180px"
        />
      </div>

      <div style="display: flex; align-items: center;">
        <span style="margin-right: 8px; white-space: nowrap;">产品编码：</span>
        <a-input
          v-model:value="queryParam.productCode"
          placeholder="输入产品编码"
          allowClear
          style="width: 180px"
          :disabled="!!restrictProductId"
        />
      </div>

      <div>
        <a-button type="primary" @click="loadData" style="margin-right: 8px;">查询</a-button>
        <a-button @click="resetQuery">重置</a-button>
      </div>
    </div>
    <!-- 明细列表 -->
    <BasicTable
      :columns="columns"
      :dataSource="detailList"
      :loading="loading"
      :rowSelection="{
        type: 'checkbox',
        selectedRowKeys: selectedKeys,
        onChange: onSelectChange,
        getCheckboxProps: getCheckboxProps
      }"
      size="small"
      bordered
      :pagination="{ pageSize: 10 }"
    />

    <!-- 底部提示 -->
    <div style="margin-top: 10px; padding: 10px; background: #f6ffed; border: 1px solid #b7eb8f;">
      <span v-if="selectedRows.length > 0">
        已选 <strong>{{ selectedRows.length }}</strong> 条明细，
        产品：<strong>{{ selectedProduct }}</strong>，
        内包装：<strong>{{ selectedPackage }}</strong>，
        总数量：<strong>{{ selectedTotalQty }} kg</strong>
      </span>
      <span v-else style="color: #999;">请选择同产品、同内包装的计划明细（已发布且未全部分配）</span>
    </div>
  </BasicModal>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable } from '/@/components/Table';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getPlanDetailList } from '../ProductionOrder.api';

  const emit = defineEmits(['success', 'register']);
  const { createMessage, createWarningModal } = useMessage();

  const [registerModal, { closeModal }] = useModalInner(async (data) => {
    selectedKeys.value = [];
    selectedRows.value = [];
    restrictProductId.value = data?.restrictProductId || '';
    restrictPackageId.value = data?.restrictPackageId || '';
// 关键：接收已选择的明细 ID，用于禁用已选
    alreadySelectedIds.value = data?.alreadySelectedIds || [];

    if (restrictProductId.value) {
      queryParam.value.productCode = '已限制产品';
    }

    await loadData();
  });

  // 限制条件
  // 已选择的明细 ID 列表（从父组件传入）
  const alreadySelectedIds = ref([]);

  const restrictProductId = ref('');
  const restrictPackageId = ref('');
  const restrictInfo = computed(() => {
    if (restrictProductId.value && restrictPackageId.value) {
      return `已限制：只能选择产品ID=${restrictProductId.value}，内包装ID=${restrictPackageId.value} 的明细`;
    } else if (restrictProductId.value) {
      return `已限制：只能选择产品ID=${restrictProductId.value} 的明细`;
    }
    return '';
  });


  // 查询参数
  const queryParam = ref({
    planNo:'',
    productCode: ''
  });

  const detailList = ref([]);
  const loading = ref(false);
  const selectedKeys = ref([]);
  const selectedRows = ref([]);

  // 表格列
  const columns = [
    { title: '计划编号', dataIndex: 'planNo', width: 120 },
    { title: '产品编码', dataIndex: 'productCode', width: 100 },
    { title: '产品名称', dataIndex: 'productName', width: 150 },
    { title: '内包装', dataIndex: 'packageName', width: 120 },
    { title: '计划分配量', dataIndex: 'allocatedQty', width: 100 },
    {
      title: '剩余可分配',
      key: 'remainingQty',
      width: 100,
      customRender: ({ record }) => {
        const total = Number(record.allocatedQty) || 0;
        const used = Number(record.completedQty) || 0;
        const remaining = total - used;
        return remaining.toFixed(2);
      }
    },
    { title: '客户', dataIndex: 'customerName', width: 120,
      customRender: ({ text }) => text || '-'
    },
    { title: '交期', dataIndex: 'deliveryDate', width: 100 },
    { title: '计划类型', dataIndex: 'planType_dictText', width: 80 }
  ];

  // 计算属性
  const selectedProduct = computed(() => selectedRows.value[0]?.productName || '');
  const selectedPackage = computed(() => selectedRows.value[0]?.packageName || '');
  const selectedTotalQty = computed(() =>
    selectedRows.value.reduce((sum, r) => {
      const total = Number(r.allocatedQty) || 0;
      const used = Number(r.completedQty) || 0;
      return sum + (total - used);
    }, 0).toFixed(2)
  );
  const canConfirm = computed(() => selectedRows.value.length > 0);

  // 获取复选框属性（限制选择）- 修正：去掉 : any 类型声明
  function getCheckboxProps(record) {
    // 关键：如果已经选过了，禁用并提示
    if (alreadySelectedIds.value.includes(record.id)) {
      return {
        disabled: true,
        // 自定义 title 提示用户
        title: '该明细已在生产订单中'
      };
    }

    // 如果已有选择，只能选同产品同包装的
    if (selectedRows.value.length > 0) {
      const first = selectedRows.value[0];
      const disabled = record.productId !== first.productId ||
        record.packageId !== first.packageId;
      return { disabled };
    }

    // 如果有传入限制条件，也要校验
    if (restrictProductId.value) {
      const disabled = record.productId !== restrictProductId.value;
      if (disabled) return { disabled: true };
    }
    if (restrictPackageId.value) {
      const disabled = record.packageId !== restrictPackageId.value;
      if (disabled) return { disabled: true };
    }

    // 校验：剩余数量必须大于0
    const total = Number(record.allocatedQty) || 0;
    const used = Number(record.completedQty) || 0;
    const remaining = total - used;
    if (remaining <= 0) {
      return { disabled: true };
    }

    return { disabled: false };

  }

  // 选择变化 - 修正：去掉类型声明
  function onSelectChange(keys, rows) {
    selectedKeys.value = keys;

    // 过滤掉不可选的
    const validRows = rows.filter(row => {
      if (selectedRows.value.length === 0) return true;
      const first = selectedRows.value[0];
      return row.productId === first.productId &&
        row.packageId === first.packageId;
    });

    if (validRows.length < rows.length) {
      createMessage.warning('已自动过滤不符合同产品同包装的明细');
      selectedKeys.value = validRows.map(r => r.id);
      selectedRows.value = validRows;
    } else {
      selectedRows.value = rows;
    }
  }

  // 确认选择
  function handleConfirm() {
    if (selectedRows.value.length === 0) {
      createWarningModal({ title: '提示', content: '请至少选择一条明细' });
      return;
    }

    // 处理数据
    const processedRows = selectedRows.value.map(row => {
      const total = Number(row.allocatedQty) || 0;
      const used = Number(row.completedQty) || 0;
      const remaining = total - used;

      return {
        ...row,
        remainingQty: remaining,
        originalAllocatedQty: row.allocatedQty
      };
    });

    const validRows = processedRows.filter(r => r.remainingQty > 0);
    if (validRows.length === 0) {
      createWarningModal({ title: '提示', content: '所选明细已全部分配，无剩余数量' });
      return;
    }

    emit('success', validRows);
    selectedKeys.value = [];
    selectedRows.value = [];
    closeModal();
  }
  function handleCancel() {
    selectedKeys.value = [];
    selectedRows.value = [];
  }
  // 加载数据
  async function loadData() {
    loading.value = true;
    try {
      const res = await getPlanDetailList({
        planNo: queryParam.value.planNo || undefined,
        productCode: queryParam.value.productCode || undefined,
      });
      detailList.value = res.records || [];
    } catch (error) {
      console.error('加载计划明细失败', error);
      createMessage.error('加载失败');
    } finally {
      loading.value = false;
    }
  }

  function resetQuery() {
    queryParam.value = { planNo: '', productCode: '' };
    if (restrictProductId.value) {
      queryParam.value.productCode = '已限制产品';
    }
    selectedKeys.value = [];
    selectedRows.value = [];
    loadData();
  }
</script>

<style scoped>

</style>

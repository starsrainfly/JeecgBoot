<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="选择收款计划"
    :width="1000"
    @ok="handleSubmit"
  >
    <!-- 查询区域 -->
    <div class="search-form mb-4">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-form-item label="计划单号" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <a-input v-model:value="searchForm.planNo" placeholder="请输入" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="计划名称" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <a-input v-model:value="searchForm.planName" placeholder="请输入" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="订单号" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <a-input v-model:value="searchForm.salesOrderNo" placeholder="请输入" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="结算状态" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <a-select v-model:value="searchForm.planStatus" placeholder="请选择" allowClear>
              <a-select-option value="0">未结算</a-select-option>
              <a-select-option value="1">部分结算</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="16" class="mt-2">
        <a-col :span="24">
          <a-form-item>
            <a-button type="primary" @click="handleSearch">查询</a-button>
            <a-button class="ml-2" @click="handleReset">重置</a-button>
          </a-form-item>
        </a-col>
      </a-row>
    </div>

    <!-- 表格区域 -->
    <BasicTable
      @register="registerTable"
      :rowSelection="{
        type: 'radio',
        selectedRowKeys: selectedRowKeys,
        onChange: onSelectChange
      }"
    />
  </BasicModal>
</template>

<script setup lang="ts">
  import { ref, reactive, h } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable } from '/@/components/Table';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getPlanList } from '../ReceiptOrder.api';

  const emit = defineEmits(['register', 'success']);
  const { createMessage } = useMessage();

  const searchForm = reactive({
    planNo: '',
    planName: '',
    salesOrderNo: '',
    planStatus: undefined as string | undefined,
  });

  const customerId = ref('');
  const excludeIds = ref<string[]>([]);
  const selectedRowKeys = ref<string[]>([]);
  const selectedRows = ref<any[]>([]);

  // 关键：用 useModalInner，和 PriceOfferModal 完全一致
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    customerId.value = data.customerId || '';
    excludeIds.value = data.excludePlanIds || [];
    selectedRowKeys.value = [];
    selectedRows.value = [];
    setModalProps({ confirmLoading: false });
    await reloadTable();
  });

  const [registerTable, { reload: reloadTable }] = useTable({
    api: fetchData,
    columns: [
      { title: '计划单号', dataIndex: 'planNo', width: 140, ellipsis: true },
      { title: '计划名称', dataIndex: 'planName', width: 120, ellipsis: true },
      { title: '销售订单号', dataIndex: 'salesOrderNo', width: 130, ellipsis: true },
      { title: '应收金额', dataIndex: 'planAmount', width: 100, align: 'right' },
      { title: '已收金额', dataIndex: 'paidAmount', width: 100, align: 'right' },
      {
        title: '未收金额',
        dataIndex: 'unpaidAmount',
        width: 100,
        align: 'right',
        customRender: ({ record }) => {
          const val = (record.planAmount || 0) - (record.paidAmount || 0);
          return h('span', { style: { color: '#ff4d4f', fontWeight: 'bold' } }, val.toFixed(2));
        },
      },
      { title: '结算状态', dataIndex: 'planStatus_dictText', width: 90 },
      { title: '计划日期', dataIndex: 'planDate', width: 110 },
    ],
    canResize: false,
    pagination: true,
    showIndexColumn: true,
    clickToRowSelect: true,
    rowKey: 'id',
    immediate: false,
  });

  async function fetchData(params: any) {
    if (!customerId.value) {
      return { records: [], total: 0, current: 1, size: params.pageSize };
    }

    const queryParams = {
      pageNo: params.pageNo,
      pageSize: params.pageSize,
      customerId: customerId.value,
      planNo: searchForm.planNo,
      planName: searchForm.planName,
      salesOrderNo: searchForm.salesOrderNo,
      planStatus: searchForm.planStatus || '0,1',
    };

    return getPlanList(queryParams);
  }

  function handleSearch() {
    reloadTable();
  }

  function handleReset() {
    searchForm.planNo = '';
    searchForm.planName = '';
    searchForm.salesOrderNo = '';
    searchForm.planStatus = undefined;
    reloadTable();
  }

  function onSelectChange(keys: string[], rows: any[]) {
    selectedRowKeys.value = keys;
    selectedRows.value = rows;
  }

  async function handleSubmit() {
    if (selectedRows.value.length === 0) {
      createMessage.warning('请选择一条收款计划');
      return;
    }
    emit('success', selectedRows.value[0]);
    closeModal();
  }
</script>

<style lang="less" scoped>
  .search-form {
    padding: 16px;
    background: #f5f5f5;
    border-radius: 4px;

    .mt-2 {
      margin-top: 8px;
    }

    .ml-2 {
      margin-left: 8px;
    }

    :deep(.ant-form-item) {
      margin-bottom: 0;
    }
  }
</style>

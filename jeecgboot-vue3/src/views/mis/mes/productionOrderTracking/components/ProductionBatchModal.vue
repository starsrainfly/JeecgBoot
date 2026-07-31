<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    width="900px"
    :footer="null"
  >
    <BasicTable
      @register="registerBatchTable"
      :dataSource="tableData"
      :loading="loading"
      :canResize="false"
      size="small"
    >
      <template #bodyCell="{ column, record }">
        <!-- 入库状态 -->
        <template v-if="column.dataIndex === 'inStockStatus'">
          <a-tag :color="getInStockColor(record.inStockStatus)">
            {{ getInStockText(record.inStockStatus) }}
          </a-tag>
        </template>
        <!-- 质检状态 -->
        <template v-if="column.dataIndex === 'qcStatus'">
          <a-tag :color="getQcColor(record.qcStatus)">
            {{ record.qcStatus_dictText || record.qcStatus }}
          </a-tag>
        </template>
      </template>
    </BasicTable>
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, nextTick} from 'vue';
  import {BasicModal, useModalInner} from '/@/components/Modal';
  import {BasicTable, useTable} from '/@/components/Table';
  import {defHttp} from '/@/utils/http/axios';

  const orderId = ref('');
  const orderNo = ref('');
  const productName = ref('');
  const tableData = ref([]);
  const loading = ref(false);

  const modalTitle = computed(() => {
    return `${orderNo.value || '订单'} - ${productName.value || ''} - 批次明细`;
  });

  const [registerModal, {setModalProps}] = useModalInner((data) => {
    orderId.value = data.orderId || '';
    orderNo.value = data.orderNo || '';
    productName.value = data.productName || '';
    setModalProps({ confirmLoading: false });
    nextTick(() => {
      if (orderId.value) loadData();
    });
  });

  const [registerBatchTable] = useTable({
    title: '生产批次明细',
    columns: [
      { title: '批次号', dataIndex: 'batchNo', width: 140, align: 'center' },
      { title: '计划量', dataIndex: 'plannedQty', width: 100, align: 'center' },
      { title: '实际产量', dataIndex: 'actualQty', width: 100, align: 'center' },
      { title: '已入库', dataIndex: 'inStockQty', width: 100, align: 'center' },
      { title: '入库状态', dataIndex: 'inStockStatus', width: 100, align: 'center' },
      { title: '质检状态', dataIndex: 'qcStatus', width: 100, align: 'center' },
      { title: '生产日期', dataIndex: 'productionDate', width: 110, align: 'center' },
      { title: '失效日期', dataIndex: 'expiryDate', width: 110, align: 'center' },
    ],
    pagination: false,
    showIndexColumn: true,
    size: 'small',
    canResize: false,
  });

  async function loadData() {
    loading.value = true;
    try {
      const res = await defHttp.get({
        url: '/mes/productionBatch/list',
        params: { orderId: orderId.value, pageSize: 100 },
      });
      tableData.value = res.records || res.result?.records || [];
    } catch (e) {
      console.error(e);
      tableData.value = [];
    } finally {
      loading.value = false;
    }
  }

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
    return '未入库';
  }
  function getQcColor(status) {
    const s = String(status || '').toUpperCase();
    if (s === 'PASS') return 'green';
    if (s === 'FAIL') return 'red';
    return 'orange';
  }
</script>

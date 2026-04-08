<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    width="1000px"
    :footer="null"
  >
    <BasicTable
      @register="registerBatchTable"
      :dataSource="tableData"
      :canResize="false"
      size="small"
      :rowKey="(record) => record.id"
    >
      <template #bodyCell="{ column, record, text }">
        <!-- 效期 -->
        <template v-if="column.dataIndex === 'expiryDate'">
          <a-tag :color="getExpiryColor(text)">{{ formatDate(text) }}</a-tag>
        </template>

        <!-- 库存数量 -->
        <template v-if="column.dataIndex === 'quantity'">
          <span>{{ text }} / 锁:{{ record.lockedQty || 0 }}</span>
        </template>

        <!-- 可用数量 - 关键修复：确保匹配到 -->
        <template v-if="column.dataIndex === 'availableQty'">
          <span :class="getAvailableQtyClass(record)">
            {{ calculateAvailable(record) }}
          </span>
        </template>

        <!-- 质检状态 -->
        <template v-if="column.dataIndex === 'qcStatus'">
          <a-tag :color="getQcStatusColor(record.qcStatus)">
            {{ getQcStatusText(record.qcStatus) }}
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
  import {getDetailByGoods} from '../StockSummary.api';

  const goodsId = ref('');
  const goodsName = ref('');
  const warehouseId = ref('');
  const tableData = ref([]);

  const modalTitle = computed(() => {
    return `${goodsName.value || '物料'} - 批次明细`;
  });

  const [registerModal, {setModalProps}] = useModalInner((data) => {
    goodsId.value = data.goodsId || '';
    goodsName.value = data.goodsName || '';
    warehouseId.value = data.warehouseId || '';

    setModalProps({ confirmLoading: false });

    nextTick(() => {
      if (goodsId.value) {
        loadTableData();
      }
    });
  });

  // 关键修复：移除 slots 配置，完全依赖 bodyCell 插槽
  const [registerBatchTable] = useTable({
    title: '批次明细',
    columns: [
      { title: '批次号', dataIndex: 'batchNo', width: 120, align: 'center' },
      { title: '库存数量', dataIndex: 'quantity', width: 120, align: 'center' },
      { title: '锁定数量', dataIndex: 'lockedQty', width: 100, align: 'center' },
      {
        title: '可用数量',
        dataIndex: 'availableQty',
        width: 90,
        align: 'center',
        // 移除 slots 配置，让 bodyCell 统一处理
      },
      { title: '入库时间', dataIndex: 'stockInTime', width: 150, align: 'center' },
      { title: '生产日期', dataIndex: 'productionDate', width: 110, align: 'center' },
      {
        title: '效期至',
        dataIndex: 'expiryDate',
        width: 110,
        align: 'center',
        // 移除 slots 配置
      },
      { title: '供应商', dataIndex: 'supplierName', width: 150, ellipsis: true },
      {
        title: '货位',
        dataIndex: 'locationId',
        width: 150,
        ellipsis: true,
        customRender: ({record}) => {
          const parts = [];
          if (record.warehouseId_dictText) parts.push(record.warehouseId_dictText);
          if (record.areaId_dictText) parts.push(record.areaId_dictText);
          if (record.shelfId_dictText) parts.push(record.shelfId_dictText);
          if (record.locationId_dictText) parts.push(record.locationId_dictText);
          return parts.join('-') || record.locationId || '-';
        }
      },
      {
        title: '状态',
        dataIndex: 'qcStatus',
        width: 100,
        align: 'center',
        // 移除 slots 配置
      },
    ],
    pagination: {
      pageSize: 10,
      showSizeChanger: true,
      showTotal: (total) => `共 ${total} 条`,
    },
    showIndexColumn: true,
    size: 'small',
    canResize: false,
  });

  async function loadTableData() {
    if (!goodsId.value) return;

    const params = {
      goodsId: goodsId.value,
      warehouseId: warehouseId.value,
      pageNo: 1,
      pageSize: 10,
    };

    try {
      const res = await getDetailByGoods(params);
      let records = [];
      if (res && res.records) {
        records = res.records;
      } else if (res && res.result && res.result.records) {
        records = res.result.records;
      }

      // 调试：检查数据
      console.log('Loaded records:', records);
      if (records.length > 0) {
        console.log('First record fields:', Object.keys(records[0]));
        console.log('Sample record:', records[0]);
      }

      tableData.value = records;
    } catch (error) {
      console.error('Load data error:', error);
      tableData.value = [];
    }
  }

  function calculateAvailable(record) {
    const qty = Number(record.quantity) || 0;
    const locked = Number(record.lockedQty) || 0;
    return (qty - locked).toFixed(2);
  }

  function getAvailableQtyClass(record) {
    const available = Number(calculateAvailable(record));
    if (available <= 0) return 'text-red-500 font-bold';
    if (available < Number(record.quantity) * 0.1) return 'text-orange-500';
    return 'text-green-600';
  }

  function formatDate(date) {
    if (!date) return '-';
    if (typeof date === 'string' && date.length > 10) {
      return date.substring(0, 10);
    }
    return date;
  }

  // 质检状态颜色 - 添加容错
  function getQcStatusColor(status) {
    const s = String(status || '').toUpperCase();
    const colorMap = {
      'WAIT_CHECK': 'orange',
      'CHECKING': 'blue',
      'PASS': 'green',
      'FAIL': 'red',
    };
    return colorMap[s] || 'default';
  }

  // 质检状态文本 - 添加容错
  function getQcStatusText(status) {
    const s = String(status || '').toUpperCase();
    const textMap = {
      'WAIT_CHECK': '待报检',
      'CHECKING': '质检中',
      'PASS': '质检合格',
      'FAIL': '质检不合格',
    };
    return textMap[s] || s || '-';
  }

  // 效期颜色 - 添加容错
  function getExpiryColor(date) {
    if (!date) return 'default';

    let expiry;
    try {
      expiry = new Date(date);
      if (isNaN(expiry.getTime())) {
        return 'default';
      }
    } catch (e) {
      return 'default';
    }

    const now = new Date();
    const diff = Math.ceil((expiry - now) / (1000 * 60 * 60 * 24));

    if (diff < 0) return 'red';
    if (diff <= 7) return 'orange';
    if (diff <= 30) return 'blue';
    return 'green';
  }
</script>

<style lang="less" scoped>
  .text-red-500 { color: #f5222d; }
  .text-orange-500 { color: #fa8c16; }
  .text-green-600 { color: #52c41a; }
  .font-bold { font-weight: bold; }
</style>

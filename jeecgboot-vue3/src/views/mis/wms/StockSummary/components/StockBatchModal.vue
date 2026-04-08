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
  const tableData = ref([]); // 关键：定义数据源

  const modalTitle = computed(() => {
    return `${goodsName.value || '物料'} - 批次明细`;
  });

  const [registerModal, {setModalProps}] = useModalInner((data) => {
    console.log('Modal received data:', data);

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

  // 关键修复：不使用api配置，手动加载数据
  const [registerBatchTable] = useTable({
    title: '批次明细',
    columns: [
      { title: '批次号', dataIndex: 'batchNo', width: 120, align: 'center' },
      { title: '库存数量', dataIndex: 'quantity', width: 120, align: 'center', slots: { customRender: 'quantity' } },
      { title: '锁定数量', dataIndex: 'lockedQty', width: 100, align: 'center' },
      { title: '入库时间', dataIndex: 'stockInTime', width: 150, align: 'center' },
      { title: '生产日期', dataIndex: 'productionDate', width: 110, align: 'center' },
      { title: '效期至', dataIndex: 'expiryDate', width: 110, align: 'center', slots: { customRender: 'expiryDate' } },
      { title: '供应商', dataIndex: 'supplierName', width: 150, ellipsis: true },
      { title: '货位', dataIndex: 'locationId', width: 150, ellipsis: true,
        customRender: ({record}) => {
          const parts = [];
          if (record.warehouseId_dictText) parts.push(record.warehouseId_dictText);
          if (record.areaId_dictText) parts.push(record.areaId_dictText);
          if (record.shelfId_dictText) parts.push(record.shelfId_dictText);
          if (record.locationId_dictText) parts.push(record.locationId_dictText);
          return parts.join('-') || record.locationId || '-';
        }
      },
      { title: '状态', dataIndex: 'qcStatus', width: 100, align: 'center', slots: { customRender: 'qcStatus' } },
    ],
    pagination: {
      pageSize: 10,
      showSizeChanger: true,
      showTotal: (total) => `共 ${total} 条`,
    },
    showIndexColumn: true,
    size: 'small',
    canResize: false,
    // 关键：不使用api属性，改用dataSource绑定
  });

  // 手动加载数据
  async function loadTableData() {
    if (!goodsId.value) return;

    const params = {
      goodsId: goodsId.value,
      warehouseId: warehouseId.value,
      pageNo: 1,
      pageSize: 10,
    };

    console.log('Loading data with params:', params);

    try {
      const res = await getDetailByGoods(params);
      console.log('API response:', res);

      // 解析数据
      let records = [];
      if (res && res.records) {
        records = res.records;
      } else if (res && res.result && res.result.records) {
        records = res.result.records;
      }

      console.log('Parsed records:', records);

      // 关键：赋值给dataSource
      tableData.value = records;

    } catch (error) {
      console.error('Load data error:', error);
      tableData.value = [];
    }
  }

  // 格式化日期
  function formatDate(date) {
    if (!date) return '-';
    if (typeof date === 'string' && date.length > 10) {
      return date.substring(0, 10);
    }
    return date;
  }

  // 质检状态颜色
  function getQcStatusColor(status) {
    const colorMap = {
      'WAIT_CHECK': 'orange',
      'CHECKING': 'blue',
      'PASS': 'green',
      'FAIL': 'red',
    };
    return colorMap[status] || 'default';
  }

  // 质检状态文本
  function getQcStatusText(status) {
    const textMap = {
      'WAIT_CHECK': '待报检',
      'CHECKING': '质检中',
      'PASS': '质检合格',
      'FAIL': '质检不合格',
    };
    return textMap[status] || status;
  }

  // 效期颜色
  function getExpiryColor(date) {
    if (!date) return 'default';
    const expiry = new Date(date);
    const now = new Date();
    const diff = Math.ceil((expiry - now) / (1000 * 60 * 60 * 24));
    if (diff < 0) return 'red';
    if (diff <= 7) return 'orange';
    if (diff <= 30) return 'blue';
    return 'green';
  }
</script>

<template>
  <div>
    <!-- 预警统计栏 -->
    <a-card size="small" class="stats-card" v-if="pageStats.totalCount > 0">
      <div class="stats-row">
        <span class="stats-label">本页统计：</span>
        <a-tag color="default">共 {{ pageStats.totalCount }} 条</a-tag>
        <a-tag color="red">
          <Icon icon="ant-design:warning-outlined" />
          已过期 {{ pageStats.expiredCount }} 条
        </a-tag>
        <a-tag color="orange">
          <Icon icon="ant-design:clock-circle-outlined" />
          近效期 {{ pageStats.nearExpiryCount }} 条
        </a-tag>
        <a-tag color="#ff4d4f">
          <Icon icon="ant-design:fall-outlined" />
          库存紧张 {{ pageStats.lowStockCount }} 条
        </a-tag>
      </div>
    </a-card>

    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" v-auth="'wms:mis_stock:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出汇总</a-button>
        <a-button type="default" @click="handleSwitchToDetail">
          <Icon icon="ant-design:unordered-list-outlined" /> 切换明细视图
        </a-button>
      </template>

      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)"/>
      </template>

      <!--自定义字段渲染-->
      <template v-slot:bodyCell="{ column, record, text }">
        <!-- 剩余天数 -->
        <template v-if="column.dataIndex === 'remainingDays'">
          <span :class="getRemainingDaysClass(record)">{{ getRemainingDays(record) }}</span>
        </template>
        <!-- 效期预警：显示"剩XX天" -->
        <template v-if="column.dataIndex === 'nearestExpiryDate'">
          <a-tag :color="getExpiryTagColor(record)">{{ getExpiryDisplay(record) }}</a-tag>
          <a-tag v-if="record.expiredBatchCount > 0" color="red">已过期{{ record.expiredBatchCount }}批</a-tag>
          <a-tag v-else-if="record.nearExpiryBatchCount > 0" color="orange">近效期{{ record.nearExpiryBatchCount }}批</a-tag>
        </template>

        <!-- 批次数量 -->
        <template v-if="column.dataIndex === 'batchCount'">
          <a-badge :count="text" :number-style="{ backgroundColor: '#108ee9' }" />
        </template>

        <!-- 库存进度 -->
        <template v-if="column.dataIndex === 'totalQty'">
          <a-tooltip :title="'锁定: ' + record.totalLockedQty">
            <a-progress
              :percent="getLockPercent(record)"
              size="small"
              :stroke-color="getProgressColor(record)"
            />
          </a-tooltip>
          <div style="font-size: 12px; color: #666;">
            总:{{ record.totalQty }} / 可用:{{ record.availableQty }}
          </div>
        </template>
      </template>


    </BasicTable>

    <!-- 批次明细弹窗 -->
    <StockBatchModal @register="registerModal" />
  </div>
</template>

<script lang="ts" name="wms-stock-summary" setup>
  import {ref, reactive} from 'vue';
  import {useRouter} from 'vue-router';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import {useModal} from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {Icon} from '/@/components/Icon';
  import StockBatchModal from './components/StockBatchModal.vue'
  import {summaryColumns, summarySearchFormSchema} from './StockSummary.data';
  import {getSummaryList, getExportUrl} from './StockSummary.api';

  const router = useRouter();
  const queryParam = reactive<any>({});

  // 本页统计
  const pageStats = reactive({
    totalCount: 0,
    expiredCount: 0,
    nearExpiryCount: 0,
    lowStockCount: 0,
  });

  //注册modal
  const [registerModal, {openModal}] = useModal();

  //注册table数据
  const { prefixCls,tableContext,onExportXls } = useListPage({
    tableProps:{
      title: '库存汇总表（按物料+仓库）',
      api: getSummaryList,
      columns: summaryColumns,
      canResize:false,
      formConfig: {
        schemas: summarySearchFormSchema,
        autoSubmitOnEnter:true,
        showAdvancedButton:true,
        fieldMapToTime: [],
      },
      actionColumn: {
        width: 150,
        fixed:'right'
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
      afterFetch: (data) => {
        // 统计当前页预警数据
        pageStats.totalCount = data?.length || 0;
        pageStats.expiredCount = data?.filter(r => (r.expiredBatchCount || 0) > 0).length || 0;
        pageStats.nearExpiryCount = data?.filter(r => {
          const hasNear = (r.nearExpiryBatchCount || 0) > 0;
          const hasExpired = (r.expiredBatchCount || 0) > 0;
          return hasNear && !hasExpired;
        }).length || 0;
        pageStats.lowStockCount = data?.filter(r => {
          const avl = Number(r.availableQty) || 0;
          const total = Number(r.totalQty) || 0;
          return total > 0 && avl < total * 0.1;
        }).length || 0;
        return data;
      },
    },
    exportConfig: {
      name:"库存汇总表",
      url: getExportUrl,
      params: queryParam,
    },
  })

  const [registerTable, {reload}] = tableContext

  /**
   * 切换至明细视图
   */
  function handleSwitchToDetail() {
    router.push('/mis/wms/StockDetail/stockList');
  }

  /**
   * 查看批次明细
   */
  function handleViewBatch(record) {
    openModal(true, {
      goodsId: record.goodsId,
      goodsName: record.goodsName,
      warehouseId: record.warehouseId,
    });
  }

  /**
   * 效期显示文字
   */
  function getExpiryDisplay(record) {
    const dateStr = record.nearestExpiryDate;
    if (!dateStr) return '-';
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) {
      return typeof dateStr === 'string' && dateStr.length > 10 ? dateStr.substring(0, 10) : dateStr;
    }
    const now = new Date();
    now.setHours(0,0,0,0);
    const diff = Math.ceil((d.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    if (diff < 0) return `已过期 ${Math.abs(diff)} 天`;
    if (diff === 0) return '今天到期';
    return `剩 ${diff} 天`;
  }

  /**
   * 效期标签颜色
   */
  function getExpiryTagColor(record) {
    const dateStr = record.nearestExpiryDate;
    if (!dateStr) return 'default';
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return 'default';
    const now = new Date();
    now.setHours(0,0,0,0);
    const diff = Math.ceil((d.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    if (diff < 0) return 'red';
    if (diff <= 7) return 'red';
    if (diff <= 30) return 'orange';
    return 'green';
  }

  /**
   * 锁定进度百分比
   */
  function getLockPercent(record) {
    const total = Number(record.totalQty) || 0;
    const locked = Number(record.totalLockedQty) || 0;
    if (total === 0) return 0;
    return Math.round((locked / total) * 100);
  }

  /**
   * 进度条颜色
   */
  function getProgressColor(record) {
    const percent = getLockPercent(record);
    if (percent > 80) return '#ff4d4f';
    if (percent > 50) return '#faad14';
    return '#52c41a';
  }

  /**
   * 操作栏
   */
  function getTableAction(record){
    return [
      {
        label: '查看批次',
        onClick: handleViewBatch.bind(null, record),
      },
      {
        label: '库存明细',
        onClick: () => {
          router.push({
            path: '/mis/wms/StockDetail/stockList',
            query: {
              goodsId: record.goodsId,
              warehouseId: record.warehouseId,
            }
          });
        },
      }
    ]
  }

  /**
   * 计算剩余天数
   */
  function getRemainingDays(record) {
    const dateStr = record.nearestExpiryDate;
    if (!dateStr) return '-';
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return '-';
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    return Math.ceil((d.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
  }

  /**
   * 剩余天数颜色
   */
  function getRemainingDaysClass(record) {
    const days = Number(getRemainingDays(record));
    if (isNaN(days)) return '';
    if (days < 0) return 'text-red font-bold';   // 已过期
    if (days <= 7) return 'text-red';             // 7天内
    if (days <= 30) return 'text-orange';         // 30天内
    return 'text-green';                          // 正常
  }

</script>

<style lang="less" scoped>
  .stats-card {
    margin-bottom: 12px;
    :deep(.ant-card-body) {
      padding: 10px 16px;
    }
  }
  .stats-row {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
  }
  .stats-label {
    font-weight: 500;
    color: #333;
  }
  .text-red { color: #f5222d; }
  .text-orange { color: #fa8c16; }
  .text-green { color: #52c41a; }
  .font-bold { font-weight: bold; }
</style>

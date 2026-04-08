<template>
  <div>
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
        <!-- 可用库存 -->
        <template v-if="column.dataIndex === 'availableQty'">
          <span :class="getAvailableQtyClass(record)">{{ text }}</span>
        </template>

        <!-- 效期预警 -->
        <template v-if="column.dataIndex === 'nearestExpiryDate'">
          <a-tag :color="getExpiryColor(record)">{{ text }}</a-tag>
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
   * 可用库存样式
   */
  function getAvailableQtyClass(record) {
    const available = Number(record.availableQty) || 0;
    const total = Number(record.totalQty) || 0;
    if (available === 0) return 'text-red-500 font-bold';
    if (available < total * 0.1) return 'text-orange-500';
    return 'text-green-600 font-medium';
  }

  /**
   * 效期颜色
   */
  function getExpiryColor(record) {
    if (record.expiredBatchCount > 0) return 'red';
    if (record.nearExpiryBatchCount > 0) return 'orange';
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
          // 跳转明细列表并带查询参数
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
</script>

<style lang="less" scoped>
  .text-red-500 { color: #f5222d; }
  .text-orange-500 { color: #fa8c16; }
  .text-green-600 { color: #52c41a; }
  .font-bold { font-weight: bold; }
  .font-medium { font-weight: 500; }
</style>

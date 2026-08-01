<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="modalTitle" :width="1000" :minHeight="500" :showOkBtn="false" cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <!-- 主表信息 -->
      <a-descriptions bordered :column="3" size="small" class="mb-4">
        <a-descriptions-item label="采购单号">{{ formData.orderNo }}</a-descriptions-item>
        <a-descriptions-item label="供应商">{{ formData.supplierName }}</a-descriptions-item>
        <a-descriptions-item label="采购员">{{ formData.purchaserName }}</a-descriptions-item>
        <a-descriptions-item label="申请日期">{{ formData.orderDate }}</a-descriptions-item>
        <a-descriptions-item label="要求到货日期">{{ formData.expectedDate }}</a-descriptions-item>
        <a-descriptions-item label="币种">{{ formData.currencyCode_dictText || formData.currencyCode }}</a-descriptions-item>
        <a-descriptions-item label="含税总额">{{ formData.orderTotal }}</a-descriptions-item>
        <a-descriptions-item label="业务状态">
          <a-tag :color="getStatusColor(formData.status)">{{ formData.status_dictText || formData.status }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="审核状态">
          <a-tag :color="getApproveColor(formData.approveStatus)">{{ formData.approveStatus_dictText || formData.approveStatus }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>

      <!-- 跟踪数据 -->
      <a-card title="执行进度" size="small" class="mb-4" :bodyStyle="{ padding: '12px' }">
        <a-row :gutter="24">
          <a-col :span="6">
            <a-statistic title="采购数量合计" :value="formData.totalOrderQty || 0" :precision="2" />
          </a-col>
          <a-col :span="6">
            <a-statistic title="已入库合计" :value="formData.totalReceivedQty || 0" :precision="2" valueStyle="color: #52c41a" />
          </a-col>
          <a-col :span="6">
            <a-statistic title="在途申请合计" :value="formData.totalAppliedQty || 0" :precision="2" valueStyle="color: #faad14" />
          </a-col>
          <a-col :span="6">
            <a-statistic title="到货率" :value="formData.arrivalRate || 0" suffix="%" :precision="2" :valueStyle="getArrivalRateStyle(formData)" />
          </a-col>
        </a-row>
        <a-row class="mt-4" v-if="formData.isOverdue === '1'">
          <a-col :span="24">
            <a-alert type="error" :message="`已超期 ${formData.overdueDays} 天，请尽快跟进到货！`" banner />
          </a-col>
        </a-row>
      </a-card>

      <!-- 子表明细 -->
      <a-card title="采购明细" size="small" :bodyStyle="{ padding: '0' }">
        <BasicTable @register="registerDetailTable" :canResize="false" :pagination="false" />
      </a-card>
    </a-spin>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '@/components/Modal';
  import { BasicTable, useTable } from '@/components/Table';
  import { trackingDetail } from '../PurchaseOrderTracking.api';
  import { detailColumns } from '../PurchaseOrderTrackingDetail.data';

  const confirmLoading = ref(false);
  const formData = ref<Recordable>({});

  const modalTitle = computed(() => {
    return `采购执行跟踪 - ${formData.value.orderNo || ''}`;
  });

  const [registerModal] = useModalInner(async (data) => {
    if (data.id) {
      confirmLoading.value = true;
      try {
        const res = await trackingDetail(data.id);

          formData.value = res || {};
          setDetailTableData(formData.value.purchaseOrderDetailList || []);

      } finally {
        confirmLoading.value = false;
      }
    }
  });

  // 子表
  const [registerDetailTable, { setTableData: setDetailTableData }] = useTable({
    columns: detailColumns,
    showIndexColumn: true,
    canResize: false,
    pagination: false,
    bordered: true,
    size: 'small',
  });

  function getStatusColor(status) {
    const map = { '0': 'default', '1': 'processing', '2': 'success', '3': 'warning' };
    return map[status] || 'default';
  }

  function getApproveColor(status) {
    const map = { '0': 'default', '1': 'success', '2': 'error' };
    return map[status] || 'default';
  }

  function getArrivalRateStyle(record) {
    const rate = Number(record.arrivalRate) || 0;
    if (record.isOverdue === '1') return 'color: #ff4d4f; font-weight: bold';
    if (rate >= 100) return 'color: #52c41a; font-weight: bold';
    if (rate >= 80) return 'color: #1890ff';
    return 'color: #faad14';
  }
</script>

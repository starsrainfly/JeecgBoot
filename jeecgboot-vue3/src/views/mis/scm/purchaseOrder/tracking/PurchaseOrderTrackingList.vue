<template>
  <div>
    <!-- 搜索区域 -->
    <BasicForm @register="registerForm" />

    <!-- 表格区域 -->
    <BasicTable @register="registerTable" :rowClassName="getRowClassName">
<!--      <template #tableTitle>-->
<!--        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出 </a-button>-->
<!--      </template>-->
      <template #arrivalRate="{ record }">
        <a-progress
          :percent="Number(record.arrivalRate) || 0"
          :size="['100%', 16]"
          :stroke-color="getProgressColor(record)"
          :show-info="true"
        />
      </template>
      <template #isOverdue="{ text }">
        <a-tag v-if="text === '1'" color="red">已超期</a-tag>
        <a-tag v-else color="green">正常</a-tag>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>

    <!-- 详情弹窗 -->
    <PurchaseOrderTrackingDetailModal @register="registerModal" />
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { BasicForm, useForm } from '@/components/Form';
  import { useModal } from '@/components/Modal';
  import { useListPage } from '@/hooks/system/useListPage';
  import { trackingList } from './PurchaseOrderTracking.api';
  import PurchaseOrderTrackingDetailModal from './components/PurchaseOrderTrackingDetailModal.vue';
  import { columns, searchFormSchema } from './PurchaseOrderTracking.data';

  const [registerModal, { openModal }] = useModal();

  // 搜索表单
  const [registerForm, { resetFields, getFieldsValue }] = useForm({
    schemas: searchFormSchema,
    showAdvancedButton: true,
    labelWidth: 100,
    baseColProps: { span: 6 },
    actionColOptions: { span: 8 },
    transformDateFunc: (date) => date.format('YYYY-MM-DD'),
  });

  // 列表页 Hook
  const { tableContext } = useListPage({
    tableProps: {
      title: '采购执行跟踪',
      api: trackingList,
      columns,
      canResize: true,
      formConfig: { show: false },
      actionColumn: {
        width: 120,
        fixed: 'right',
        slots: { customRender: 'action' },
      },
      beforeFetch: (params) => {
        const formData = getFieldsValue();
        return { ...params, ...formData };
      },
    },
   // exportConfig: { name: '采购执行跟踪', url: getExportUrl },
  });

  const [registerTable, { reload }] = tableContext;

  // 行样式：超期标红
  function getRowClassName(record) {
    return record.isOverdue === '1' ? 'tracking-overdue-row' : '';
  }

  // 进度条颜色
  function getProgressColor(record) {
    if (record.isOverdue === '1') return '#ff4d4f';
    const rate = Number(record.arrivalRate) || 0;
    if (rate >= 100) return '#52c41a';
    if (rate >= 80) return '#1890ff';
    if (rate >= 50) return '#faad14';
    return '#ff4d4f';
  }

  // 操作列
  function getTableAction(record) {
    return [
      {
        label: '查看',
        onClick: () => {
          openModal(true, { id: record.id, isUpdate: false });
        },
      },
    ];
  }

  // 导出
  function onExportXls() {
    const params = getFieldsValue();
    console.log('导出参数', params);
  }
</script>

<style scoped>
  :deep(.tracking-overdue-row) {
    background-color: #fff1f0 !important;
  }
  :deep(.tracking-overdue-row td) {
    color: #cf1322 !important;
  }
</style>

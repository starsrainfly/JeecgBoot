<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleExport">导出</a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'diffRate'">
          <span v-if="record.diffRate == null">-</span>
          <span v-else :style="{ color: Math.abs(record.diffRate) > 5 ? '#ff4d4f' : '#666', fontWeight: Math.abs(record.diffRate) > 5 ? 'bold' : 'normal' }">
            {{ record.diffRate > 0 ? '+' : '' }}{{ record.diffRate }}%
          </span>
        </template>
        <template v-if="column.key === 'diffQty'">
          <span v-if="record.diffQty == null">-</span>
          <span v-else :style="{ color: record.diffQty > 0 ? '#ff4d4f' : '#52c41a' }">
            {{ record.diffQty > 0 ? '+' : '' }}{{ record.diffQty }}
          </span>
        </template>
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '批次明细',
              onClick: handleBatchDetail.bind(null, record),
            },
          ]"
        />
      </template>
    </BasicTable>

    <BasicModal v-bind="$attrs" @register="registerDetailModal" title="批次称重明细" width="900px">
      <a-table
        :columns="detailColumns"
        :data-source="detailData"
        bordered
        size="small"
        :loading="detailLoading"
        :pagination="false"
        rowKey="batchNo"
      >
        <template #bodyCell="{ column, text }">
          <template v-if="['bomPlannedQty', 'actualQty'].includes(column.dataIndex)">
            {{ text != null ? Number(text).toFixed(4) : '-' }}
          </template>
          <template v-if="column.dataIndex === 'diffQty'">
            <span :style="{ color: text > 0 ? '#ff4d4f' : text < 0 ? '#52c41a' : '#666' }">
              {{ text != null ? Number(text).toFixed(6) : '-' }}
            </span>
          </template>
        </template>
      </a-table>
    </BasicModal>
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { BasicModal, useModal } from '/@/components/Modal';
  import { list, batchDetail } from './MaterialReqActualReport.api';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage } = useMessage();
  const [registerDetailModal, { openModal: openDetailModal }] = useModal();
  const detailData = ref<any[]>([]);
  const detailLoading = ref(false);

  const columns = [
    { title: '生产订单号', dataIndex: 'orderNo', width: 150 },
    { title: '产品编码', dataIndex: 'productCode', width: 120 },
    { title: '产品名称', dataIndex: 'productName', width: 150 },
    { title: '物料编码', dataIndex: 'materialCode', width: 120 },
    { title: '物料名称', dataIndex: 'materialName', width: 150 },
    { title: '规格型号', dataIndex: 'materialSpec', width: 120 },
    { title: '物料类型', dataIndex: 'materialTypeText', width: 80 },
    { title: '计划用量(kg)', dataIndex: 'plannedQty', width: 130, align: 'right', customRender: ({ text }: any) => (text ? Number(text).toFixed(4) : '-') },
    { title: '实际称重(kg)', dataIndex: 'actualQty', width: 130, align: 'right', customRender: ({ text }: any) => (text ? Number(text).toFixed(4) : '-') },
    { title: '差异量(kg)', key: 'diffQty', dataIndex: 'diffQty', width: 120, align: 'right' },
    { title: '差异率(%)', key: 'diffRate', dataIndex: 'diffRate', width: 110, align: 'right' },
    // 隐藏字段：必须声明在 columns 中，否则 action 插槽的 record 取不到
    { title: 'orderId', dataIndex: 'orderId', width: 0, defaultHidden: true },
    { title: 'materialId', dataIndex: 'materialId', width: 0, defaultHidden: true },
    {
      title: '操作',
      key: 'action',
      width: 100,
      slots: { customRender: 'action' },
      fixed: 'right',
    },
  ];

  const detailColumns = [
    { title: '批次号', dataIndex: 'batchNo', width: 140 },
    { title: '序号', dataIndex: 'batchSeq', width: 60, align: 'center' },
    { title: '物料编码', dataIndex: 'materialCode', width: 120 },
    { title: '物料名称', dataIndex: 'materialName', width: 150 },
    { title: '计划用量(kg)', dataIndex: 'bomPlannedQty', width: 130, align: 'right' },
    { title: '实际称重(kg)', dataIndex: 'actualQty', width: 130, align: 'right' },
    { title: '差异量(kg)', dataIndex: 'diffQty', width: 120, align: 'right' },
    { title: '操作员', dataIndex: 'operatorName', width: 100 },
    { title: '完成时间', dataIndex: 'completeTime', width: 160 },
  ];

  const [registerTable] = useTable({
    title: '物料需求与领料对比报表',
    api: async (params) => {
      const res = await list(params);
      return res.result || res;
    },
    columns,
    formConfig: {
      labelWidth: 110,
      baseColProps: { span: 6 },
      actionColOptions: { span: 6 },
      schemas: [
        { field: 'orderNo', label: '生产订单号', component: 'Input' },
        { field: 'productCode', label: '产品编码', component: 'Input' },
        { field: 'materialCode', label: '物料编码', component: 'Input' },
        {
          field: 'materialType',
          label: '物料类型',
          component: 'Select',
          componentProps: {
            options: [
              { label: '源材料', value: 'RAW' },
              { label: '内包', value: 'package_inner' },
              { label: '外包', value: 'package_outer' },
            ],
          },
        },
        {
          field: 'startDate',
          label: '计划开工(起)',
          component: 'DatePicker',
          componentProps: { valueFormat: 'YYYY-MM-DD' },
        },
        {
          field: 'endDate',
          label: '计划开工(止)',
          component: 'DatePicker',
          componentProps: { valueFormat: 'YYYY-MM-DD' },
        },
        {
          field: 'diffRateThreshold',
          label: '差异率阈值(%)',
          component: 'InputNumber',
          componentProps: { defaultValue: 5, min: 0 },
        },
      ],
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
  });

  async function handleBatchDetail(record: any) {
    detailData.value = [];
    detailLoading.value = true;
    try {
      const res = await batchDetail({ orderId: record.orderId, materialId: record.materialId });
      detailData.value = res || [];
    } finally {
      detailLoading.value = false;
    }
    openDetailModal(true);
  }

  function handleExport() {
    createMessage.info('导出功能请对接后端 /mes/materialReqActualReport/exportXls');
  }
</script>

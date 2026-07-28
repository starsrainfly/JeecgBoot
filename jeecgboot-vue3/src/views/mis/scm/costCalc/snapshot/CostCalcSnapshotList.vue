<template>
  <div>
    <BasicTable @register="registerTable">
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '查看',
              onClick: handleView.bind(null, record),
            },
            {
              label: '删除',
              color: 'error',
              popConfirm: {
                title: '确认删除？',
                confirm: handleDelete.bind(null, record),
              },
            },
          ]"
        />
      </template>
    </BasicTable>

    <CostCalcModal @register="registerModal" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  // ===== 修正：从父目录引用 API =====
  import { list, deleteSnapshot } from '../CostCalc.api';
  // ===== 修正：从上级 components 目录引用弹窗 =====
  import CostCalcModal from '../components/CostCalcModal.vue';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();

  const columns = [
    { title: '核算单号', dataIndex: 'calcNo', width: 140 },
    { title: '核算类型', dataIndex: 'calcType', width: 100, customRender: ({ text }: any) => (text === 'MONTHLY' ? '月度自动' : '手动保存') },
    { title: '核算日期', dataIndex: 'calcDate', width: 110 },
    { title: '产品编码', dataIndex: 'productCode', width: 120 },
    { title: '产品名称', dataIndex: 'productName', width: 150 },
    { title: '配方版本', dataIndex: 'recipeVersion', width: 90 },
    { title: '最新成本', dataIndex: 'totalCostLatest', width: 120, align: 'right', customRender: ({ text }: any) => (text ? Number(text).toFixed(4) : '-') },
    { title: '平均成本', dataIndex: 'totalCostAvg', width: 120, align: 'right', customRender: ({ text }: any) => (text ? Number(text).toFixed(4) : '-') },
    { title: '备注', dataIndex: 'remark', width: 150, ellipsis: true },
    {
      title: '操作',
      key: 'action',
      width: 140,
      slots: { customRender: 'action' },
      fixed: 'right',
    },
  ];

  const [registerTable, { reload }] = useTable({
    title: '成本核算快照',
    api: async (params) => {
      // 使用生成的 list 方法（返回完整响应体，需要取 result）
      const res = await list(params);
      return res.result || res;
    },
    columns,
    formConfig: {
      labelWidth: 100,
      baseColProps: { span: 4 },
      actionColOptions: { span: 4 },
      schemas: [
        { field: 'calcNo', label: '核算单号', component: 'Input' },
        { field: 'productCode', label: '产品编码', component: 'Input' },
        {
          field: 'calcType',
          label: '核算类型',
          component: 'Select',
          componentProps: {
            options: [
              { label: '手动保存', value: 'MANUAL' },
              { label: '月度自动', value: 'MONTHLY' },
            ],
          },
        },
      ],
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
  });

  function handleView(record: any) {
    openModal(true, { mode: 'snapshot', calcId: record.id });
  }

  async function handleDelete(record: any) {
    // ===== 修正：使用 deleteSnapshot（你 CostCalc.api.ts 里自定义的方法）=====
    await deleteSnapshot(record.id);
    createMessage.success('删除成功');
    reload();
  }
</script>

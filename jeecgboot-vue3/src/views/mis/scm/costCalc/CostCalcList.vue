<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleSnapshotList">快照管理</a-button>
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '成本核算',
              onClick: handleCalc.bind(null, record),
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
  // ===== 修正：同级目录引用 =====
  import { getProductList } from './CostCalc.api';
  import CostCalcModal from './components/CostCalcModal.vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const [registerModal, { openModal }] = useModal();

  const columns = [
    { title: '产品编码', dataIndex: 'productCode', width: 120 },
    { title: '产品名称', dataIndex: 'productName', width: 150 },
    { title: '规格型号', dataIndex: 'productSpec', width: 120 },
    { title: '颜色', dataIndex: 'productColor', width: 80 },
    { title: '配方编号', dataIndex: 'recipeCode', width: 120 },
    { title: '配方名称', dataIndex: 'recipeName', width: 150 },
    { title: '配方版本', dataIndex: 'recipeVersion', width: 90 },
    {
      title: '操作',
      key: 'action',
      width: 100,
      slots: { customRender: 'action' },
      fixed: 'right',
    },
  ];

  const [registerTable] = useTable({
    title: '产品材料成本核算',
    api: async (params) => {
      const res = await getProductList(params);
      return res.result || res;
    },
    columns,
    formConfig: {
      labelWidth: 100,
      baseColProps: { span: 8 },
      actionColOptions: { span: 8 },
      schemas: [
        { field: 'productCode', label: '产品编码', component: 'Input' },
        { field: 'productName', label: '产品名称', component: 'Input' },
      ],
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
  });

  function handleCalc(record: any) {
    openModal(true, { mode: 'calc', productId: record.productId });
  }

  function handleSnapshotList() {
    // 这个路径必须和菜单管理里"快照管理"菜单的 url/path 完全一致
    router.push('/mis/scm/costCalc/snapshot');
  }
</script>

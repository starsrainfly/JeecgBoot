<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" v-auth="'wms:mis_delivery:add'" @click="handleAdd" preIcon="ant-design:plus-outlined">
          新增发货
        </a-button>
        <super-query :config="superQueryConfig" @search="handleSuperQuery" />
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
    </BasicTable>
    <DeliveryJobModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="wms-deliveryJob" setup>
  import { ref, reactive } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import DeliveryJobModal from './components/DeliveryJobModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './DeliveryJob.data';
  import { list, deleteOne } from './DeliveryJob.api';

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);

  const [registerModal, { openModal }] = useModal();
  const { tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '发货作业',
      api: list,
      columns,
      canResize: false,
      formConfig: {
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        fieldMapToTime: [
          ['deliveryTime', ['deliveryTime_begin', 'deliveryTime_end'], 'YYYY-MM-DD HH:mm:ss'],
        ],
      },
      actionColumn: {
        width: 120,
        fixed: 'right',
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;
  const superQueryConfig = reactive(superQuerySchema);

  function handleSuperQuery(params) {
    Object.keys(params).map((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }

  function handleAdd() {
    openModal(true, {});
  }

  function handleDetail(record: Recordable) {
    // 可扩展：查看发货详情
    console.log(record);
  }

  async function handleDelete(record) {
    await deleteOne({ id: record.id }, handleSuccess);
  }

  function handleSuccess() {
    selectedRowKeys.value = [];
    reload();
  }

  function getTableAction(record) {
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
    ];
  }

  function getDropDownAction(record) {
    return [
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
        auth: 'wms:mis_delivery:delete',
      },
    ];
  }
</script>

<style lang="less" scoped>
  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>

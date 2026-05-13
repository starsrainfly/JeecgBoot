<!-- src/views/wms/shelfTask/ShelfTaskList.vue -->
<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" @click="handleBatchShelf" :disabled="selectedRowKeys.length === 0" preIcon="ant-design:upload-outlined">
          批量上架
        </a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>

    <ShelfTaskModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="wms-shelfTask" setup>
  import { ref, reactive } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import ShelfTaskModal from './components/ShelfTaskModal.vue';
  import { columns, searchFormSchema } from './ShelfTask.data';
  import { pendingList } from './ShelfTask.api';

  const queryParam = reactive<any>({});

  // 注册modal
  const [registerModal, { openModal }] = useModal();

  // 注册table数据
  const { tableContext } = useListPage({
    tableProps: {
      title: '待上架库存',
      api: pendingList,
      columns,
      canResize: false,
      formConfig: {
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
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

  const [registerTable, { reload , getSelectRows }, { rowSelection, selectedRowKeys }] = tableContext;

  /**
   * 单条上架
   */
  function handleShelf(record: Recordable) {
    openModal(true, {
      record,
      isBatch: false,
    });
  }

  /**
   * 批量上架
   */
  function handleBatchShelf() {
    const rows = getSelectRows();//selectedRowKeys.value;
    console.log("batchShelf rows:",rows)
    if (rows.length === 0) return;
    openModal(true, {
      records: rows,
      isBatch: true,
    });
  }

  /**
   * 成功回调
   */
  function handleSuccess() {
    selectedRowKeys.value = [];
    reload();
  }

  /**
   * 操作栏
   */
  function getTableAction(record: Recordable) {
    return [
      {
        label: '上架',
        onClick: handleShelf.bind(null, record),
        type: 'primary',
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

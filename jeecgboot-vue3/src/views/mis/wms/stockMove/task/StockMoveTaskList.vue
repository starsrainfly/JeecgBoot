<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" @click="handleBatchMove" :disabled="selectedRowKeys.length === 0" preIcon="ant-design:swap-outlined">
          批量移库
        </a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>

    <StockMoveTaskModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="wms-stockMoveTask" setup>
  import { ref, reactive } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import StockMoveTaskModal from './components/StockMoveTaskModal.vue';
  import { StockMoveTaskColumns, StockMoveTaskSearchForm } from './StockMoveTask.data';
  import { pendingList } from './StockMoveTask.api';

  const queryParam = reactive<any>({});

  // 注册modal
  const [registerModal, { openModal }] = useModal();

  // 注册table数据（改用 useListPage，与上架一致）
  const { tableContext } = useListPage({
    tableProps: {
      title: '可移库库存',
      api: pendingList,
      columns: StockMoveTaskColumns,
      canResize: false,
      formConfig: {
        schemas: StockMoveTaskSearchForm,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
      },
      actionColumn: {
        width: 100,
        fixed: 'right',
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
  });

  const [registerTable, { reload, getSelectRows }, { rowSelection, selectedRowKeys }] = tableContext;

  /**
   * 单条移库
   */
  function handleMove(record: Recordable) {
    openModal(true, {
      record,
      isBatch: false,
    });
  }

  /**
   * 批量移库
   */
  function handleBatchMove() {
    const rows = getSelectRows();
    console.log("batchMove rows:", rows);
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
        label: '移库',
        onClick: handleMove.bind(null, record),
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

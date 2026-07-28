<template>
  <div>
    <BasicTable @register="registerTable">
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)"/>
      </template>
    </BasicTable>
    <!-- 审核弹窗 -->
    <PurchaseOrderApproveModal @register="registerApproveModal" @success="handleSuccess" />
    <!-- 详情复用采购订单的弹窗（只读） -->
    <PurchaseOrderModal @register="registerDetailModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="scm-purchaseOrderApprove" setup>
  import { reactive } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import PurchaseOrderApproveModal from './components/PurchaseOrderApproveModal.vue';
  import PurchaseOrderModal from '../components/PurchaseOrderModal.vue';
  import { columns, searchFormSchema } from './PurchaseOrderApprove.data';
  import { list } from '../PurchaseOrder.api';

  const queryParam = reactive<any>({});
  const [registerApproveModal, { openModal: openApproveModal }] = useModal();
  const [registerDetailModal, { openModal: openDetailModal }] = useModal();

  const { tableContext } = useListPage({
    tableProps: {
      title: '采购审核',
      api: list,
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
      beforeFetch: (params) => Object.assign(params, queryParam),
    },
  });

  const [registerTable, { reload }] = tableContext;

  /** 审核 */
  function handleApprove(record) {
    openApproveModal(true, { record });
  }

  /** 详情（复用采购订单弹窗只读模式） */
  function handleDetail(record) {
    openDetailModal(true, { record, isUpdate: true, showFooter: false });
  }

  function handleSuccess() {
    reload();
  }

  function getTableAction(record) {
    return [
      {
        label: '审核',
        onClick: handleApprove.bind(null, record),
        auth: 'scm:mis_purchase_order:approve',
        ifShow: record.approveStatus === '0',
      },
    ];
  }

  function getDropDownAction(record) {
    return [
      { label: '详情', onClick: handleDetail.bind(null, record) },
    ];
  }
</script>

<style lang="less" scoped>
  :deep(.ant-picker), :deep(.ant-input-number) {
    width: 100%;
  }
</style>

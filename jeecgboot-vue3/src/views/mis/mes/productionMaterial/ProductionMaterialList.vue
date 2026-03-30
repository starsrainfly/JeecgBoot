<!-- ProductionMaterialList.vue - 修正解构 -->
<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" v-auth="'mes:mis_production_material:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-button type="primary" v-auth="'mes:mis_production_material:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
        <j-upload-button type="primary" v-auth="'mes:mis_production_material:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button v-auth="'mes:mis_production_material:deleteBatch'">批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
        <a-button v-if="selectedRowKeys.length > 0" type="primary" @click="handleBatchApply">
          <Icon icon="ant-design:export-outlined" />
          批量申请出库 ({{ selectedRowKeys.length }})
        </a-button>
        <super-query :config="superQueryConfig" @search="handleSuperQuery" />
      </template>

      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>

      <template #bodyCell="{ column, record, text }">
        <template v-if="column.dataIndex === 'orderNo'">
          <a @click.stop="handleOrderApply(record.orderId, record.orderNo)">{{ text }}</a>
        </template>
      </template>
    </BasicTable>

    <ProductionMaterialModal @register="registerModal" @success="handleSuccess"></ProductionMaterialModal>
    <IssueApplyModal @register="registerApplyModal" @success="handleSuccess"></IssueApplyModal>
  </div>
</template>

<script lang="ts" name="mes-productionMaterial" setup>
  import { ref, reactive, toRaw } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import ProductionMaterialModal from './components/ProductionMaterialModal.vue';
  import IssueApplyModal from './components/IssueApplyModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './ProductionMaterial.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './ProductionMaterial.api';
  import { Icon } from '/@/components/Icon';

  const queryParam = reactive<any>({});
  const [registerModal, { openModal }] = useModal();

  // 使用 useListPage
  const { tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '物料需求表',
      api: list,
      columns,
      canResize: false,
      formConfig: {
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
      },
      actionColumn: {
        width: 150,
        fixed: 'right'
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: "物料需求表",
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess
    },
  });

  // 正确解构 - useListPage 返回的 tableContext 是一个数组
  // [registerTable, tableAction, { rowSelection, selectedRowKeys, getSelectRows }]
  const [registerTable, { reload, getSelectRows }, { rowSelection, selectedRowKeys }] = tableContext;

  const superQueryConfig = reactive(superQuerySchema);
  const [registerApplyModal, { openModal: openApplyModal }] = useModal();

  function handleSingleApply(record) {
    openApplyModal(true, {
      mode: 'single',
      record: record
    });
  }

  function handleBatchApply() {
    // 使用 getSelectRows 获取选中的行
    const rows = getSelectRows();
    console.log('选中的行:', rows);

    if (!rows || rows.length === 0) {
      console.warn('没有选中的行');
      return;
    }

    // 转换为原始数据
    const selectedRows = rows.map(r => toRaw(r));

    // 获取所有涉及的订单
    const orderIds = [...new Set(selectedRows.map(r => r.orderId))];
    const orderNos = [...new Set(selectedRows.map(r => r.orderNo))];

    openApplyModal(true, {
      mode: 'batch',
      orderId: orderIds.length === 1 ? orderIds[0] : null,
      orderNo: orderIds.length === 1 ? orderNos[0] : orderNos.join(','),
      orderIds: orderIds,
      records: selectedRows
    });
  }

  function handleOrderApply(orderId, orderNo) {
    if (!orderId) return;
    openApplyModal(true, {
      mode: 'order',
      orderId: orderId,
      orderNo: orderNo
    });
  }

  function handleSuperQuery(params) {
    Object.keys(params).map((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }

  function handleAdd() {
    openModal(true, { isUpdate: false, showFooter: true });
  }

  function handleEdit(record) {
    openModal(true, { record, isUpdate: true, showFooter: true });
  }

  function handleDetail(record) {
    openModal(true, { record, isUpdate: true, showFooter: false });
  }

  async function handleDelete(record) {
    await deleteOne({ id: record.id }, handleSuccess);
  }

  async function batchHandleDelete() {
    await batchDelete({ ids: selectedRowKeys.value }, handleSuccess);
  }

  function handleSuccess() {
    selectedRowKeys.value = [];
    reload();
  }

  function getTableAction(record) {
    return [{
      label: '编辑',
      onClick: handleEdit.bind(null, record),
      auth: 'mes:mis_production_material:edit'
    }];
  }

  function getDropDownAction(record) {
    const remainingQty = (record.requiredQty || 0) - (record.issuedQty || 0);
    if (remainingQty <= 0) {
      return [{ label: '已完成', disabled: true }];
    }
    return [
      {
        label: '申请出库',
        color: 'primary',
        onClick: () => handleSingleApply(record),
        auth: 'mes:mis_production_material:apply_issue'
      },
      { label: '详情', onClick: handleDetail.bind(null, record) },
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
        auth: 'mes:mis_production_material:delete'
      }
    ];
  }
</script>

<style lang="less" scoped>
  :deep(.ant-picker), :deep(.ant-input-number) {
    width: 100%;
  }
</style>

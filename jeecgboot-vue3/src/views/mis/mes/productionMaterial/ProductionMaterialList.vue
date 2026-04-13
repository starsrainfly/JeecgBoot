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
          <a @click.stop="handleOrderApply(record.orderId, record.orderNo,record)">{{ text }}</a>
        </template>
      </template>
    </BasicTable>

    <ProductionMaterialModal @register="registerModal" @success="handleSuccess"></ProductionMaterialModal>
    <IssueApplyModal @register="registerApplyModal" @success="handleSuccess"></IssueApplyModal>
  </div>
</template>

<script lang="ts" name="mes-productionMaterial" setup>
  import { ref, reactive, toRaw, computed} from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import ProductionMaterialModal from './components/ProductionMaterialModal.vue';
  import IssueApplyModal from './components/IssueApplyModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './ProductionMaterial.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './ProductionMaterial.api';
  import { Icon } from '/@/components/Icon';
  import {useMessage} from "@/hooks/web/useMessage";
  import { message } from 'ant-design-vue';

  const queryParam = reactive<any>({});
  const [registerModal, { openModal }] = useModal();
  const { createMessage } = useMessage();
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
      //整行加颜色
      rowClassName: (record) => {
        const available = parseFloat(record.availableStockQty) || 0;
        const locked = parseFloat(record.lockedQty) || 0;
        const realAvailable = available - locked;

        const required = parseFloat(record.requiredQty) || 0;
        const issued = parseFloat(record.issuedQty) || 0;
        const realRemaining = required - issued - locked;

        if (locked > 0 && realAvailable < realRemaining) return 'row-warning';
        if (realAvailable <= 0) return 'row-danger';
        if (realAvailable < realRemaining) return 'row-warning';
        return '';
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
    const check = canApply(record);
    if (!check.can) {
      message.warning(check.reason);
      return;
    }
    openApplyModal(true, {
      mode: 'single',
      record: record
    });
  }
  // 计算属性：判断选中的行是否有不能申请的
  const hasLockedSelectedRows = computed(() => {
    const rows = getSelectRows();
    if (!rows || rows.length === 0) return false;
    return rows.some(r => !canApply(r).can);
  });

  // 判断能否申请出库
  function canApply(record): { can: boolean; reason: string } {
    const status = record.status;
    const required = parseFloat(record.requiredQty) || 0;
    const issued = parseFloat(record.issuedQty) || 0;
    const locked = parseFloat(record.lockedQty) || 0;

    // 状态1：已申请（锁定库存），不能再申请
    if (status === '1') {
      return { can: false, reason: '已申请，不可再申请' };
    }

    // 状态2：已完成，不能再申请
    if (status === '2') {
      return { can: false, reason: '已完成，不可再申请' };
    }

    // 状态3：物料需求已取消，不能再申请
    if (status === '3') {
      return { can: false, reason: '物料需求已取消' };
    }

    // 状态0：待申请，可以申请
    return { can: true, reason: '' };
  }

  // 判断能否删除
  function canDelete(record): { can: boolean; reason: string } {
    // 只有待申请(0)才能删除
    if (record.status === '0') {
      return { can: true, reason: '' };
    }

    const statusMap = {
      '1': '已申请',
      '2': '已完成',
      '3': '已取消'
    };

    return {
      can: false,
      reason: `${statusMap[record.status] || '未知状态'}，不能删除`
    };
  }

  function handleBatchApply() {
    // 使用 getSelectRows 获取选中的行
    const rows = getSelectRows();
    console.log('选中的行:', rows);

    if (!rows || rows.length === 0) {
      createMessage.warn('没有选中的行');
      return;
    }

    // 转换为原始数据
    const selectedRows = rows.map(r => toRaw(r));
    // 检查是否有不能申请的行
    const cannotApplyRows = selectedRows.filter(r => !canApply(r).can);

    if (cannotApplyRows.length > 0) {
      // 显示第一条的原因
      const firstReason = canApply(cannotApplyRows[0]).reason;
      message.warning(`选中的记录中有 ${cannotApplyRows.length} 条${firstReason}`);
      return;
    }
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

  function handleOrderApply(orderId, orderNo,record) {
    if (!orderId) return;
    // 检查当前行状态，给出友好提示
    const check = canApply(record);
    if (!check.can) {
      message.info(`当前物料${check.reason}，订单弹窗将只显示待申请的物料`);
    }
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
    const deleteCheck = canDelete(record);
    if(deleteCheck.can){
      await deleteOne({ id: record.id }, handleSuccess);
    }
    else{
      createMessage.warning(deleteCheck.reason);
    }

  }

  async function batchHandleDelete() {
    const rows = getSelectRows();

    if (!rows || rows.length === 0) {
      message.warning('请先选择记录');
      return;
    }

    // 检查是否有不能删除的行
    const cannotDeleteRows = rows.filter(r => !canDelete(r).can);

    if (cannotDeleteRows.length > 0) {
      // 统计各状态的数量
      const statusCount = {};
      cannotDeleteRows.forEach(r => {
        const status = r.status === '1' ? '已申请' :
          r.status === '2' ? '已完成' :
            r.status === '3' ? '已取消' : '未知';
        statusCount[status] = (statusCount[status] || 0) + 1;
      });

      const details = Object.entries(statusCount)
        .map(([status, count]) => `${status}${count}条`)
        .join('、');

      message.warning(`选中的记录中有 ${cannotDeleteRows.length} 条不能删除（${details}）`);
      return;
    }

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
    const check = canApply(record);
    const deleteCheck = canDelete(record);
    if (remainingQty <= 0) {
      return [
        { label: '已完成', disabled: true },
        { label: '详情', onClick: handleDetail.bind(null, record) },
        {
          label: '删除',
          disabled: true,
          popConfirm: {
            title: '已完成，不能删除',
            placement: 'topLeft',
          },
          auth: 'mes:mis_production_material:delete'
        }
      ];
    }
    // 根据状态显示不同的提示
    let label = '申请出库';
    if (record.status === '1') {
      label = '已申请';
    } else if (record.status === '2') {
      label = '已完成';
    } else if (record.status === '3') {
      label = '已取消';
    }
    return [
      {
        label: label,
        color: check.can ? 'primary' : 'default',
        disabled: !check.can,
        onClick: check.can ? () => handleSingleApply(record) : undefined,
        auth: 'mes:mis_production_material:apply_issue'
      },
      { label: '详情', onClick: handleDetail.bind(null, record) },
      {
        label: '删除',
        color: deleteCheck.can ? 'error' : 'default',
        disabled: !deleteCheck.can,
        popConfirm: deleteCheck.can ? {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        } : {
          title: deleteCheck.reason,
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
  :deep(.row-danger) {
    background-color: #fff1f0 !important;
    td {
      background-color: #fff1f0 !important;
    }
  }
  :deep(.row-warning) {
    background-color: #fffbe6 !important;
    td {
      background-color: #fffbe6 !important;
    }
  }
  :deep(.row-danger:hover), :deep(.row-warning:hover) {
    background-color: inherit;
  }
</style>

<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
        <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>批量操作 <Icon icon="mdi:chevron-down"></Icon></a-button>
        </a-dropdown>
      </template>

      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)"/>
      </template>

      <template v-slot:bodyCell="{ column, record, index, text }">
      </template>
    </BasicTable>

    <WeighingExecuteModal @register="registerWeighingModal" @success="handleSuccess" />
    <ProductionExecuteModal @register="registerProductionModal" @success="handleSuccess" />
    <QcExecuteModal @register="registerQcModal" @success="handleSuccess" />
    <MyTaskModal @register="registerModal" @success="handleSuccess"></MyTaskModal>
  </div>
</template>

<script lang="ts" name="my-task" setup>
  import { ref, reactive } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import MyTaskModal from './components/MyTaskModal.vue'
  import { myTaskList, startTask, completeTask, reportQc, setBatchStatus } from './MyTask.api';
  import { columns, searchFormSchema, ProductionExecuteformSchema } from './MyTask.data';
  import { useMessage } from "@/hooks/web/useMessage";
  import WeighingExecuteModal from './components/WeighingExecuteModal.vue';
  import ProductionExecuteModal from './components/ProductionExecuteModal.vue';
  import QcExecuteModal from './components/QcExecuteModal.vue';

  const { createMessage } = useMessage();
  const [registerWeighingModal, { openModal: openWeighing }] = useModal();
  const [registerProductionModal, { openModal: openProduction }] = useModal();
  const [registerQcModal, { openModal: openQc }] = useModal();

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const [registerModal, { openModal }] = useModal();

  const { tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '我的任务',
      api: myTaskList,
      columns,
      canResize: false,
      formConfig: {
        labelWidth: 120,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
      },
      actionColumn: {
        width: 260,
        fixed: 'right'
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  function handleAdd() {
    openModal(true, { isUpdate: false, showFooter: true });
  }

  async function handleDelete(record) {
    console.log('Delete:', record);
  }

  async function batchHandleDelete() {
    console.log('Batch Delete:', selectedRowKeys.value);
  }

  function handleSuccess() {
    selectedRowKeys.value = [];
    reload();
  }

  function getDropDownAction(record) {
    return [{ label: '详情', onClick: handleDetail.bind(null, record) }];
  }

  function handleDetail(record) {
    openModal(true, { record, isUpdate: true, showFooter: false });
  }

  async function handleStart(record) {
    openExecuteModal(record, 'START');
    reload();
  }

  async function handleStartPackage(record) {
    await startTask(record);
    createMessage.success('任务已开始');
    reload();
  }

  async function handleWeighing(record) {
    openWeighing(true, record);
    reload();
  }

  function handleQc(record) {
    openQc(true, { record });
  }

  /**
   * 打开对应类型的执行弹窗（taskType 数据库里全是小写）
   */
  function openExecuteModal(record, actionMode: 'START' | 'COMPLETE') {
    switch (record.taskType) {
      case 'weighing':
      case 'production': {
        const r = { ...record };
        // 实际设备为空时，默认带出计划设备，操作员确认或改选
        if (!r.actualEquipmentId) {
          r.actualEquipmentId = r.planEquipmentId;
          r.actualEquipmentCode = r.planEquipmentCode;
          r.actualEquipmentName = r.planEquipmentName;
          r.actualModel = r.planModel;
          r.actualEquipmentType = r.planEquipmentType;
        }
        openProduction(true, {
          record: r,
          mode: 'START',
          schemas: ProductionExecuteformSchema,
          title: '生产开工 - 选择设备',
          showFooter: true
        });
        break;
      }
      case 'package':
        handleStartPackage(record);
        break;
      case 'qc':
        handleQc(record);
        break;
    }
  }

  async function handleComplete(record) {
    await completeTask(record);
    if (record.taskType === 'weighing') {
      await setBatchStatus({ id: record.batchId, status: 'PRODUCING' });
    } else if (record.taskType === 'package') {
      await setBatchStatus({ id: record.batchId, status: 'COMPLETED' });
    }
    createMessage.success('任务已完成');
    reload();
  }

  async function handleReportQc(record) {
    await reportQc({ taskId: record.id });
    createMessage.success('报检成功，质检工单已生成');
    reload();
  }

  function getTableAction(record: Recordable) {
    return [
      {
        label: '开工',
        color: 'primary',
        disabled: record.status !== 'ASSIGNED' || record.taskType === 'qc',
        tooltip: record.taskType === 'qc' ? '质检工单请使用质检录入' : (record.status !== 'ASSIGNED' ? '请先等待上一步完成' : ''),
        onClick: () => handleStart(record),
      },
      {
        label: '质检',
        color: 'primary',
        disabled: !(record.taskType === 'qc' && record.status === 'ASSIGNED'),
        onClick: () => handleQc(record),
      },
      {
        label: '配料',
        color: 'warning',
        disabled: !((record.status === 'PROCESSING') && record.taskType === 'weighing'),
        onClick: () => handleWeighing(record),
      },
      {
        label: '报检',
        color: 'error',
        disabled: !(record.status === 'PROCESSING' && record.qcRequired === '1'
          && (!record.qcStatus || record.qcStatus === 'WAIT_CHECK')),
        tooltip: record.qcRequired !== '1' ? '无需报检'
          : (record.qcStatus && record.qcStatus !== 'WAIT_CHECK' ? '已报检' : ''),
        onClick: () => handleReportQc(record),
      },
      {
        label: '完工',
        color: 'success',
        disabled: !(record.status === 'PROCESSING' && record.taskType !== 'qc'),
        onClick: () => handleComplete(record),
      },
    ];
  }
</script>

<style lang="less" scoped>
  :deep(.ant-picker), :deep(.ant-input-number) {
    width: 100%;
  }
</style>

<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
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

        <!-- 高级查询 -->
        <!-- <super-query :config="superQueryConfig" @search="handleSuperQuery" /> -->
      </template>

      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)"/>
        <!-- 自定义操作列 (替换原本的 TableAction) -->

        <a-space>
          <template #bodyCell="{ column, record, text }">
            <template v-if="column.dataIndex === 'status'">
              <a-badge :status="getStatusBadge(text)" :text="getStatusText(text)" />
            </template>
            <template v-if="column.dataIndex === 'taskType'">
              <a-tag :color="getTaskTypeColor(text)">{{ getTaskTypeText(text) }}</a-tag>
            </template>
          </template>
<!--          &lt;!&ndash; 待执行 &ndash;&gt;-->
<!--          <template v-if="record.status === 'ASSIGNED'">-->
<!--            <a-button type="primary" size="small" @click="handleStart(record)">-->
<!--              开始执行-->
<!--            </a-button>-->
<!--          </template>-->

<!--          &lt;!&ndash; 执行中 &ndash;&gt;-->
<!--          <template v-if="record.status === 'PROCESSING'">-->
<!--            <a-button type="primary" size="small" @click="handleContinue(record)">-->
<!--              继续执行-->
<!--            </a-button>-->

<!--            &lt;!&ndash; 根据工单类型判断是报检还是完成 &ndash;&gt;-->
<!--            <template v-if="['weighing', 'production', 'package'].includes(record.taskType)">-->
<!--              <a-button-->
<!--                v-if="record.qcRequired === '1' && record.qcStatus === '0'"-->
<!--                type="primary"-->
<!--                danger-->
<!--                size="small"-->
<!--                @click="handleReportQc(record)"-->
<!--              >-->
<!--                完成并报检-->
<!--              </a-button>-->
<!--              <a-button v-else size="small" @click="handleComplete(record)">-->
<!--                完成任务-->
<!--              </a-button>-->
<!--            </template>-->

<!--            &lt;!&ndash; 质检任务 &ndash;&gt;-->
<!--            <a-button-->
<!--              v-if="record.taskType === 'qc'"-->
<!--              type="primary"-->
<!--              size="small"-->
<!--              @click="handleComplete(record)"-->
<!--            >-->
<!--              完成质检-->
<!--            </a-button>-->
<!--          </template>-->
        </a-space>
      </template>

      <!--字段回显插槽-->
      <template v-slot:bodyCell="{ column, record, index, text }">
      </template>
    </BasicTable>

    <WeighingExecuteModal @register="registerWeighingModal" @success="handleSuccess" />
<!--    &lt;!&ndash; 2. 生产弹窗 (请确保路径正确) &ndash;&gt;-->
    <ProductionExecuteModal @register="registerProductionModal" @success="handleSuccess" />

<!--    &lt;!&ndash; 3. 质检弹窗 (请确保路径正确) &ndash;&gt;-->
<!--    <QcExecuteModal @register="registerQcModal" @success="handleSuccess" />-->
    <!-- 包材执行弹窗 -->
<!--    <PackageExecuteModal  @register="registerPackageModal"  @success="handleSuccess" />-->
    <!-- 表单区域 -->
    <MyTaskModal @register="registerModal" @success="handleSuccess"></MyTaskModal>
  </div>
</template>

<script lang="ts" name="my-task" setup>
  import { ref, reactive } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import MyTaskModal from './components/MyTaskModal.vue'
  import {myTaskList, startTask, completeTask, reportQc, setBatchStatus, startWeighing, completeWeighing} from './MyTask.api'; // 请确保这个文件存在
  import { columns, searchFormSchema,ProductionExecuteformSchema  } from './MyTask.data';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import {useMessage} from "@/hooks/web/useMessage";
  import WeighingExecuteModal from './components/WeighingExecuteModal.vue';
  import ProductionExecuteModal from './components/ProductionExecuteModal.vue';
  import QcExecuteModal from './components/QcExecuteModal.vue';
  import PackageExecuteModal from './components/PackageExecuteModal.vue';

  const { createMessage } = useMessage();
  const [registerWeighingModal, { openModal: openWeighing }] = useModal();
  const [registerProductionModal, { openModal: openProduction }] = useModal();
  const [registerQcModal, { openModal: openQc }] = useModal();

  // const [registerPackageModal, { openModal: openPackage }] = useModal();

  const currentMode = ref<'START' | 'COMPLETE' | 'QC_REPORT'>('START'); // 当前模式
  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);

  // 注册 modal (如果不需要弹窗，可以注释相关逻辑)
  const [registerModal, { openModal }] = useModal();

  // 注册 table 数据 (完全复刻 ProductionTask 的 useListPage 模式)
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
        width: 240,
        fixed: 'right'
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
    // exportConfig: { name: "我的任务", url: getExportUrl, params: queryParam },
    // importConfig: { url: getImportUrl, success: handleSuccess }
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  // 高级查询配置 (如果不需要可以删)
  // const superQueryConfig = reactive({});

  // function handleSuperQuery(params) { ... }

  // 以下是标准的 CRUD 回调，如果没用到 Modal 可以简化
  function handleAdd() {
    openModal(true, {
      isUpdate: false,
      showFooter: true,
    });
  }

  async function handleDelete(record) {
    // await deleteOne({id: record.id}, handleSuccess);
    console.log('Delete:', record);
  }

  async function batchHandleDelete() {
    // await batchDelete({ids: selectedRowKeys.value}, handleSuccess);
    console.log('Batch Delete:', selectedRowKeys.value);
  }

  function handleSuccess() {
    selectedRowKeys.value = [];
    reload();
  }

  // 操作栏
  // function getTableAction(record) {
  //   return [
  //     {
  //       label: '编辑',
  //       onClick: handleEdit.bind(null, record),
  //       // auth: 'my_task:edit'
  //     }
  //   ];
  // }

  function getDropDownAction(record) {
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
      // {
      //   label: '删除',
      //   popConfirm: {
      //     title: '是否确认删除',
      //     confirm: handleDelete.bind(null, record),
      //     placement: 'topLeft',
      //   },
      //   // auth: 'my_task:delete'
      // }
    ];
  }

  function handleEdit(record) {
    openModal(true, { record, isUpdate: true, showFooter: true });
  }

  function handleDetail(record) {
    openModal(true, { record, isUpdate: true, showFooter: false });
  }

  async function handleStart(record) {
    //await startTask({ taskId: record.id });
   // createMessage.success('任务已开始');

    openExecuteModal(record,'START');
    reload();
  }

  async function handleStartPackage(record){
    await startTask(record);
  }
  async function handleWeighing(record) {
    openWeighing(true, record);
    reload();
  }
  // 打开对应类型的执行弹窗
  function openExecuteModal(record, actionMode: 'START' | 'COMPLETE') {


    switch (record.taskType) {
      case 'weighing':
        // openWeighing(true, record);
        // break;
      case 'production':
       // if(actionMode ==='START'){
          openProduction(true, {
            record,
            mode:'START',
            schemas: ProductionExecuteformSchema, // 传入 Schema
            title: '生产开工 - 选择设备',
            showFooter: true
          });
        // }
        // else{
        //  handleComplete(record);
        // }

        break;
      case 'package':
        handleStartPackage(record);//只更新操作人员及开始时间
       // openPackage(true, record);
        break;
      case 'qc':
      //  openQc(true, record);
        break;
    }
  }


  async function handleComplete(record) {
    await completeTask(record);
    if(record.taskType === 'weighing'){
      await setBatchStatus({id:record.batchId,status:'PRODUCING'});
    }
    else if(record.taskType === 'package'){
      await setBatchStatus({id:record.batchId,status:'COMPLETED'});
    }
    createMessage.success('任务已完成');
    reload();
  }

  async function handleReportQc(record) {
    await reportQc({ taskId: record.id });
    createMessage.success('报检成功，质检工单已生成');
    reload();
  }

  // 状态显示
  function getStatusBadge(status: string) {
    const map: Record<string, string> = {
      'PENDING': 'default',
      'PROCESSING': 'processing',
      'COMPLETED': 'success',
      'qc': 'warning',
    };
    return map[status] || 'default';
  }

  function getStatusText(status: string) {
    const map: Record<string, string> = {
      'PENDING': '待执行',
      'ASSIGNED':'执行',
      'PROCESSING': '执行中',
      'COMPLETED': '已完成',
      'qc_reported': '已报检',
    };
    return map[status] || status;
  }

  function getTaskTypeColor(type: string) {
    const map: Record<string, string> = {
      'weighing': 'blue',
      'production': 'green',
      'package': 'orange',
      'qc': 'red',
    };
    return map[type] || 'default';
  }

  function getTaskTypeText(type: string) {
    const map: Record<string, string> = {
      'weighing': '配料',
      'production': '生产',
      'package': '包材',
      'qc': '质检',
    };
    return map[type] || type;
  }

  // 获取操作按钮
  function getTableAction(record: Recordable) {
    return [
      {
        label: '开工',
        color: 'primary',
        disabled: record.status !== 'ASSIGNED', // 只有ASSIGNED能点
        tooltip: record.status !== 'ASSIGNED' ? '请先等待上一步完成' : '', // 可选：鼠标悬停提示
        onClick: () => handleStart(record),
      },
      {
        label: '配料',
        color: 'warning',
        disabled: !((record.status === 'PROCESSING' ) && record.taskType === 'weighing'),
        onClick: () => handleWeighing(record),
      },
      {
        label: '报检',
        color: 'error',
        disabled: !(record.status === 'PROCESSING' && record.qcRequired === '1'),
        // 如果不需要报检，可以加一个 Tooltip 说明原因
        tooltip: record.qcRequired !== '1' ? '无需报检' : '',
        onClick: () => handleReportQc(record),
      },
      {
        label: '完工',
        color: 'success',
        disabled: !(record.status === 'PROCESSING' ),
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

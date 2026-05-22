<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="1300"
    :minHeight="500"
    :showOkBtn="false"
    :showCancelBtn="false">

    <!-- 顶部信息栏 -->
    <a-descriptions :column="4" bordered size="small" class="mb-4">
      <a-descriptions-item label="盘点单号">{{ checkInfo.checkNo }}</a-descriptions-item>
      <a-descriptions-item label="盘点范围">{{ checkInfo.checkScope_dictText }}</a-descriptions-item>
      <a-descriptions-item label="盘点方法">
        <a-tag :color="checkInfo.checkMethod === '2' ? 'red' : 'blue'">
          {{ checkInfo.checkMethod_dictText }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="状态">
        <a-tag :color="getStatusColor(checkInfo.checkStatus)">
          {{ checkInfo.checkStatus_dictText }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="盘点仓库">{{ checkInfo.warehouseId_dictText || '-' }}</a-descriptions-item>
      <a-descriptions-item label="区域">{{ checkInfo.areaId_dictText || '-' }}</a-descriptions-item>
      <a-descriptions-item label="货架">{{ checkInfo.shelfId_dictText || '-' }}</a-descriptions-item>
      <a-descriptions-item label="货位">{{ checkInfo.locationId_dictText || '-' }}</a-descriptions-item>
    </a-descriptions>

    <!-- 未开始：提示 + 开始按钮 -->
    <template v-if="!isStarted">
      <a-alert
        message="当前为库存预览模式，可查看待盘点物料清单及工作量"
        type="info"
        show-icon
        class="mb-4" />

      <a-row :gutter="16" class="mb-4">
        <a-col :span="6">
          <a-card size="small">
            <div class="stat-item">
              <div class="stat-title">待盘项数</div>
              <div class="stat-value">{{ previewList.length }}</div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <a-button
        type="primary"
        block
        size="large"
        :loading="startLoading"
        @click="handleRealStartCheck"
        class="mb-4">
        <Icon icon="ant-design:play-circle-outlined" />
        开始盘点（生成明细并进入编辑）
      </a-button>
    </template>

    <!-- 已开始：扫码区域 + 统计 -->
    <template v-else>
      <a-card size="small" title="扫码作业" class="mb-4">
        <div class="scan-area">
          <Html5ScanInput
            placeholder="扫描库位码或物料码"
            @change="handleScan"
            style="width: 400px;" />
          <a-button type="primary" @click="handleScanSubmit" class="ml-2">定位</a-button>
        </div>
        <div v-if="scanResult" class="mt-2 text-green-600">
          {{ scanResult }}
        </div>
      </a-card>

      <a-row :gutter="16" class="mb-4">
        <a-col :span="6">
          <a-card size="small">
            <div class="stat-item">
              <div class="stat-title">总项数</div>
              <div class="stat-value">{{ totalItems }}</div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card size="small">
            <div class="stat-item">
              <div class="stat-title">已盘</div>
              <div class="stat-value text-green">{{ checkedItems }}</div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card size="small">
            <div class="stat-item">
              <div class="stat-title">待盘</div>
              <div class="stat-value text-orange">{{ pendingItems }}</div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card size="small">
            <div class="stat-item">
              <div class="stat-title">跳过</div>
              <div class="stat-value">{{ skipItems }}</div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </template>

    <!-- ===== 预览模式表格（只读 - BasicTable） ===== -->
    <BasicTable
      v-if="!isStarted"
      @register="registerPreviewTable"
      :dataSource="previewList"
      class="check-table" />

    <!-- ===== 作业模式表格（可编辑 - JVxeTable） ===== -->
    <JVxeTable
      v-else
      ref="jobTable"
      keep-source
      resizable
      :loading="tableLoading"
      :columns="checkJobColumns"
      :dataSource="detailList"
      :height="400"

      :rowNumber="true"
      :rowSelection="true"
      :toolbar="false"
      :disabledRows="{ checkStatus: ['2', '3'] }"
      @valueChange="handleValueChange"
      class="check-table">

      <!-- 系统库存列：盲盘时隐藏 -->
      <template #systemQty="{ row }">
        <span v-if="isBlind && (!row.actualQty && row.actualQty !== 0)">***</span>
        <span v-else>{{ row.systemQty }}</span>
      </template>

      <!-- 差异数量列 -->
      <template #diffQty="{ row }">
<!--        <span v-if="!row.actualQty && row.actualQty !== 0">-</span>-->
<!--        <span v-else :class="getDiffClass(row.diffQty)">-->
<!--          {{ showDiff ? row.diffQty : '***' }}-->
<!--        </span>-->
        <span v-if="row.checkStatus !== '2' && row.checkStatus !== '3' && !row.actualQty && row.actualQty !== 0">-</span>
        <span v-else :class="getDiffClass(row.diffQty)">
    {{ showDiff ? (row.diffQty ?? 0) : '***' }}
  </span>
      </template>

      <!-- 差异金额列（只读，自动计算） -->
      <template #diffAmount="{ row }">
        <span v-if="!row.actualQty && row.actualQty !== 0">-</span>
        <span v-else :class="getDiffClass(row.diffAmount)">{{ row.diffAmount ?? 0 }}</span>
      </template>

      <!-- 操作列 -->
      <template #action="{ row, rowIndex }">
        <template v-if="row.checkStatus === '0' || row.checkStatus === null || row.checkStatus === undefined">
          <a-button type="link" size="small" @click="handleConfirmRow(row, rowIndex)">
            确认
          </a-button>
          <a-button type="link" size="small" @click="handleSkipRow(row, rowIndex)">
            跳过
          </a-button>
        </template>
        <template v-else-if="row.checkStatus === '2'">
          <a-tag color="green">已盘</a-tag>
          <a-button type="link" size="small" @click="handleEditRow(row, rowIndex)">
            修改
          </a-button>
        </template>
        <template v-else-if="row.checkStatus === '3'">
          <a-tag>已跳过</a-tag>
          <a-button type="link" size="small" @click="handleRestoreRow(row, rowIndex)">
            恢复
          </a-button>
        </template>
      </template>
    </JVxeTable>

    <!-- 底部固定操作栏（仅已开始显示） -->
    <template #footer v-if="isStarted">
      <div class="footer-content" >
        <div class="stats">
          <span>进度：{{ checkedItems + skipItems }} / {{ totalItems }}</span>
          <span class="ml-4 text-red" v-if="diffItems > 0">差异：{{ diffItems }} 项</span>
        </div>
        <div class="actions">
          <a-button @click="handleClose">取消</a-button>
          <a-button
            type="primary"
            @click="handleBatchConfirm"
            :disabled="selectedRowCount === 0"
            class="ml-2">
            批量确认({{ selectedRowCount }})
          </a-button>
          <a-button
            type="primary"
            danger
            @click="handleFinishCheck"
            class="ml-2"
            :loading="finishLoading">
            完成盘点
          </a-button>
        </div>
      </div>
    </template>
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, nextTick, unref} from 'vue';
  import {BasicModal, useModalInner} from '/@/components/Modal';
  import {BasicTable, useTable} from '/@/components/Table';
  import {Html5ScanInput} from '/@/components/Scan';
  import {useMessage} from '/@/hooks/web/useMessage';
  import {Icon} from '/@/components/Icon';
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils';
  import {
    inventoryCheckPreview,
    inventoryCheckDetailList,
    startCheck,
    confirmDetail,
    batchConfirm,
    finishCheck
  } from '../InventoryCheck.api';
  import {previewColumns, checkJobColumns} from '../InventoryCheck.data';

  const {createMessage, createConfirm} = useMessage();

  // ===== 标准 JVxeTable 引用配置 =====
  const refKeys = ref(['jobTable']);
  const activeKey = ref('jobTable');
  const jobTable = ref();
  const tableRefs = { jobTable };

  const checkInfo = ref<any>({});
  const isStarted = ref(false);
  const isBlind = ref(false);
  const showDiff = ref(true);
  const previewList = ref<any[]>([]);
  const detailList = ref<any[]>([]);
  const startLoading = ref(false);
  const finishLoading = ref(false);
  const tableLoading = ref(false);
  const scanResult = ref('');

  const DETAIL_STATUS = {
    PENDING: '0',
    CHECKED: '2',
    SKIP: '3'
  };

  // ===== 预览表格（只读） =====
  const [registerPreviewTable] = useTable({
    columns: previewColumns,
    dataSource: [],
    pagination: false,
    canResize: true,
    scroll: { y: 400 }
  });

  // ===== useJvxeMethod 标准方法 =====
  // 这里不需要完整的表单提交流程，所以 requestAddOrEdit 和 classifyIntoFormData 用空实现
  const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] = useJvxeMethod(
    requestAddOrEdit,
    classifyIntoFormData,
    tableRefs,
    activeKey,
    refKeys,

    validateSubForm
  );

  // 空实现：本组件不需要通过 useJvxeMethod 提交表单
  function classifyIntoFormData(allValues: any) {
    return allValues;
  }

  async function requestAddOrEdit(values: any) {
    // 本组件不走这里提交，逐行/批量确认直接调 API
  }

  function validateSubForm() {
    return Promise.resolve();
  }

  // ===== Modal 初始化 =====
  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    checkInfo.value = data.record || {};
    isBlind.value = checkInfo.value.checkMethod === '2';
    showDiff.value = !isBlind.value;

    // 重置状态
    isStarted.value = checkInfo.value.checkStatus !== '0';
    previewList.value = [];
    detailList.value = [];

    await nextTick();

    if (isStarted.value) {
      await loadDetailList(data.record.id);
    } else {
      await loadPreviewData(data.record.id);
    }

    setModalProps({ confirmLoading: false });
  });

  // 加载库存预览（只读）
  async function loadPreviewData(checkId: string) {
    try {
      const res = await inventoryCheckPreview({id: checkId});
      console.log("preview res:", res);
      previewList.value = res.records || [];
    } catch (e) {
      createMessage.error('加载预览数据失败');
    }
  }

  // 加载明细（可编辑）
  async function loadDetailList(checkId: string) {
    tableLoading.value = true;
    try {
      const res = await inventoryCheckDetailList({
        checkId: checkId,
        pageSize: 9999
      });
      console.log("detail res:", res);
     // detailList.value = res.records || [];
      // 处理 null 值，避免 inputNumber 默认显示 0
      detailList.value = (res.records || []).map(row => ({
        ...row,
        actualQty: row.actualQty === 0 ? null : row.actualQty,
        diffQty: row.diffQty === 0 ? null : row.diffQty,
        diffAmount: row.diffAmount,//row.diffAmount === 0 ? null : row.diffAmount,
      }));

    } catch (e) {
      createMessage.error('加载明细数据失败');
      console.error(e);
    } finally {
      tableLoading.value = false;
    }
  }

  // 真正的开始盘点
  async function handleRealStartCheck() {
    startLoading.value = true;
    try {
      await startCheck({id: checkInfo.value.id});
      isStarted.value = true;
      checkInfo.value.checkStatus = '1';
      await loadDetailList(checkInfo.value.id);
      createMessage.success('盘点开始，现在可以编辑实盘数量');
    } catch (e: any) {
      createMessage.error(e.message || '开始盘点失败');
    } finally {
      startLoading.value = false;
    }
  }

  // JVxeTable 值变化事件 - 自动计算差异
  function handleValueChange({ row, column, value }) {
    if (column.key === 'actualQty' || column.key === 'costPrice') {
      const actualQty = row.actualQty || 0;
      const systemQty = row.systemQty || 0;
      const costPrice = row.costPrice || 0;

      row.diffQty = actualQty - systemQty;
      row.diffAmount = row.diffQty * costPrice;

      // 差异为0时清空原因
      if (row.diffQty === 0) {
        row.diffReason = '';
      }
    }
  }

  // 校验单行数据
  function validateRow(row: any): boolean {
    if (row.actualQty === null || row.actualQty === undefined || row.actualQty < 0) {
      createMessage.error(`编码 ${row.goodsCode} 的实盘数量必填且不能为负数`);
      return false;
    }
    if (row.diffQty !== 0 && !row.diffReason) {
      createMessage.error(`编码 ${row.goodsCode} 有差异，必须填写差异原因`);
      return false;
    }
    return true;
  }

  // 逐行确认
  async function handleConfirmRow(row: any, rowIndex: number) {
    if (!validateRow(row)) return;

    try {
      await confirmDetail({
        id: row.id,
        checkId: checkInfo.value.id,
        actualQty: row.actualQty,
        diffQty: row.diffQty,
        diffAmount: row.diffAmount,
        diffReason: row.diffReason,
        costPrice:row.costPrice,
        checkStatus: DETAIL_STATUS.CHECKED
      });

      // row.checkStatus = DETAIL_STATUS.CHECKED;
      // row.checkStatus_dictText = '已盘';
      // 强制替换整行触发响应式更新
      detailList.value[rowIndex] = {
        ...row,
        checkStatus: DETAIL_STATUS.CHECKED,
        checkStatus_dictText: '已盘'
      };
      detailList.value = [...detailList.value]; // 强制刷新数组引用

     // createMessage.success('确认成功');
    } catch (e: any) {
      createMessage.error(e.message || '确认失败');
    }
  }

  // 跳过行
  async function handleSkipRow(row: any, rowIndex: number) {
    createConfirm({
      title: '确认跳过',
      content: `确定跳过 ${row.goodsCode} ${row.goodsName}？`,
      onOk: async () => {
        try {
          await confirmDetail({
            id: row.id,
            checkId: checkInfo.value.id,
            actualQty: 0,
            diffQty: 0,
            diffAmount: 0,
            diffReason: '跳过',
            checkStatus: DETAIL_STATUS.SKIP
          });

          // row.actualQty = 0;
          // row.diffQty = 0;
          // row.diffAmount = 0;
          // row.diffReason = '跳过';
          // row.checkStatus = DETAIL_STATUS.SKIP;
          // row.checkStatus_dictText = '已跳过';
          detailList.value[rowIndex] = {
            ...row,
            actualQty: 0,
            diffQty: 0,
            diffAmount: 0,
            diffReason: '跳过',
            checkStatus: DETAIL_STATUS.SKIP,
            checkStatus_dictText: '已跳过'
          };
          detailList.value = [...detailList.value]; // 强制刷新数组引用

         // createMessage.success('已跳过');
        } catch (e: any) {
          createMessage.error(e.message || '跳过失败');
        }
      }
    });
  }

  // 修改已确认行（重新编辑）
  function handleEditRow(row: any, rowIndex: number) {
    // row.checkStatus = DETAIL_STATUS.PENDING;
    // row.checkStatus_dictText = '待盘点';
    const actualQty = row.actualQty || 0;
    const systemQty = row.systemQty || 0;
    const costPrice = row.costPrice || 0;
    const diffQty = actualQty - systemQty;
    const diffAmount = diffQty * costPrice;

    detailList.value[rowIndex] = {
      ...row,
      checkStatus: DETAIL_STATUS.PENDING,
      checkStatus_dictText: '待盘点',
      diffQty: diffQty,
      diffAmount: diffAmount
    };
    detailList.value = [...detailList.value]; // 强制刷新数组引用
    createMessage.info('已恢复编辑状态，修改后请重新确认');
  }

  // 恢复已跳过行
  async function handleRestoreRow(row: any, rowIndex: number) {
    try {
      await confirmDetail({
        id: row.id,
        checkId: checkInfo.value.id,
        actualQty: 0,
        diffQty: 0,
        diffAmount: 0,
        diffReason: '',
        checkStatus: DETAIL_STATUS.PENDING
      });


      // ... 更新前端状态
      detailList.value[rowIndex] = {
        ...row,
        checkStatus: DETAIL_STATUS.PENDING,
        checkStatus_dictText: '待盘点',
        actualQty: null,
        diffQty: null,
        diffAmount: null,
        diffReason: ''
      };
      detailList.value = [...detailList.value]; // 强制刷新数组引用
    } catch (e: any) {
      createMessage.error(e.message || '恢复失败');
    }

    createMessage.info('已恢复，请重新盘点');
  }

  // 扫码
  function handleScan(result: string) {
    scanResult.value = result;
  }

  function handleScanSubmit() {
    const code = scanResult.value;
    if (!code) return;

    const matched = detailList.value.filter(item =>
      item.goodsCode === code ||
      item.pathCode === code ||
      item.locationId === code
    );

    if (matched.length > 0) {
      createMessage.success(`定位到 ${matched.length} 条记录`);
    } else {
      createMessage.warning('未找到匹配记录');
    }
  }

  // ===== 通过 useJvxeMethod 获取选中行 =====
  const selectedRowCount = computed(() => {
    console.log("jobTable getSelectionData:",tableRefs.jobTable.value?.getSelectionData);
    return tableRefs.jobTable.value?.getSelectionData?.()?.length || 0;
  });

  // 批量确认
  async function handleBatchConfirm() {
    const selected = tableRefs.jobTable.value?.getSelectionData?.() || [];
    if (!selected.length) {
      createMessage.warning('请先选择要确认的行');
      return;
    }

    // 校验
    const invalid = selected.filter(row => !validateRow(row));
    if (invalid.length) {
      createMessage.error(`${invalid.length} 行数据不完整，请检查`);
      return;
    }

    const details = selected.map(row => ({
      id: row.id,
      checkId: checkInfo.value.id,
      actualQty: row.actualQty,
      diffQty: row.diffQty,
      costPrice:row.costPrice,
      diffAmount: row.diffAmount,
      diffReason: row.diffReason,
      checkStatus: DETAIL_STATUS.CHECKED
    }));
    console.log("batchConfirm details:",details)

    try {
      await batchConfirm({
        id: checkInfo.value.id,
        inventoryCheckDetailList: details
      });


      // 更新前端状态 - 强制刷新数组
      selected.forEach(row => {
        const idx = detailList.value.findIndex(item => item.id === row.id);
        if (idx !== -1) {
          detailList.value[idx] = {
            ...detailList.value[idx],
            actualQty: row.actualQty,  // 保留当前值
            checkStatus: DETAIL_STATUS.CHECKED,
            checkStatus_dictText: '已完成'
          };
        }
      });
      detailList.value = [...detailList.value];

     // createMessage.success(`已确认 ${selected.length} 条`);
    } catch (e: any) {
      createMessage.error(e.message || '批量确认失败');
    }
  }

  // 完成盘点
  async function handleFinishCheck() {
    const data = detailList.value;
    const pending = data.filter(item => !item.checkStatus || item.checkStatus === DETAIL_STATUS.PENDING);

    if (pending.length > 0) {
      createConfirm({
        title: '确认完成盘点',
        content: `还有 ${pending.length} 项未盘点，确定完成吗？`,
        onOk: async () => {
          await doFinish();
        }
      });
    } else {
      await doFinish();
    }
  }

  async function doFinish() {
    finishLoading.value = true;
    try {
      await finishCheck({ id: checkInfo.value.id });
      createMessage.success('盘点完成');
      closeModal();
      emit('success');
    } catch (e: any) {
      createMessage.error(e.message || '完成盘点失败');
    } finally {
      finishLoading.value = false;
    }
  }

  // 关闭弹窗
  function handleClose() {
    closeModal();
  }

  // 统计
  const totalItems = computed(() => detailList.value.length);
  const checkedItems = computed(() => detailList.value.filter(i => i.checkStatus === DETAIL_STATUS.CHECKED).length);
  const pendingItems = computed(() => detailList.value.filter(i => !i.checkStatus || i.checkStatus === DETAIL_STATUS.PENDING).length);
  const skipItems = computed(() => detailList.value.filter(i => i.checkStatus === DETAIL_STATUS.SKIP).length);
  const diffItems = computed(() => detailList.value.filter(i => i.diffQty !== 0 && i.diffQty !== null && i.diffQty !== undefined).length);

  function getStatusColor(status: string) {
    const colors: Record<string, string> = {
      '0': 'default',
      '1': 'processing',
      '2': 'warning',
      '3': 'success'
    };
    return colors[status] || 'default';
  }

  function getDiffClass(diff: number) {
    if (!diff || diff === 0) return '';
    return diff > 0 ? 'text-red' : 'text-green';
  }

  const title = computed(() => {
    const prefix = isStarted.value ? '盘点作业' : '盘点预览';
    return `${prefix} - ${checkInfo.value.checkNo || ''}`;
  });

  const emit = defineEmits(['register', 'success']);
</script>

<style lang="less" scoped>
  .scan-area {
    display: flex;
    align-items: center;
  }
  .stat-item {
    text-align: center;
    padding: 8px;
  }
  .stat-title {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #333;
  }
  .text-green { color: #3f8600; }
  .text-orange { color: #fa8c16; }
  .text-red { color: #cf1322; }

  /* footer 样式 */
  .footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

  }
  .footer-content .stats {
    font-size: 14px;
    color: #666;
  }
  .footer-content .actions {
    display: flex;
    gap: 8px;
  }

  :deep(.ant-input-number) {
    width: 100%;
  }
</style>

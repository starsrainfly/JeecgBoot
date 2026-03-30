<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    width="1000px"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <!-- 模式一：单条申请 -->
    <div v-if="applyMode === 'single'">
      <a-descriptions bordered :column="2">
        <a-descriptions-item label="生产订单">{{ record.sourceNo }}</a-descriptions-item>
        <a-descriptions-item label="生产批次">{{ record.batchNo }}</a-descriptions-item>
        <a-descriptions-item label="物料编码">{{ record.materialCode }}</a-descriptions-item>
        <a-descriptions-item label="物料名称">{{ record.materialName }}</a-descriptions-item>
        <a-descriptions-item label="规格型号">{{ record.materialSpec }}</a-descriptions-item>
        <a-descriptions-item label="单位">{{ record.unit }}</a-descriptions-item>
        <a-descriptions-item label="需求数量">{{ record.requiredQty }}</a-descriptions-item>
        <a-descriptions-item label="已发数量">{{ record.issuedQty }}</a-descriptions-item>
        <a-descriptions-item label="待发数量" :span="2">
          <span style="color: red; font-weight: bold;">{{ pendingQty }}</span>
        </a-descriptions-item>
      </a-descriptions>

      <a-divider />

      <BasicForm @register="registerSingleForm" />
    </div>

    <!-- 模式二/三：批量/按订单申请 -->
    <div v-else>
      <!-- 订单信息 -->
      <a-card v-if="orderNo" size="small" title="订单信息" class="mb-4">
        <p>订单号: {{ orderNo }}</p>
        <p v-if="applyMode === 'order'">
          该订单包含 {{ batchList.length }} 个批次，
          <a-button type="link" @click="loadBatchDetail">查看并选择批次</a-button>
        </p>
      </a-card>

      <!-- 批次选择（按订单模式时显示） -->
      <a-card v-if="showBatchSelect" size="small" title="选择批次" class="mb-4">
        <a-checkbox-group v-model:value="selectedBatchIds">
          <a-row>
            <a-col :span="8" v-for="batch in batchList" :key="batch.id">
              <a-checkbox :value="batch.id">
                {{ batch.batchNo }} ({{ batch.materialCount }}种物料)
              </a-checkbox>
            </a-col>
          </a-row>
        </a-checkbox-group>
        <a-button type="primary" size="small" @click="confirmBatchSelect">
          确认选择
        </a-button>
      </a-card>

      <!-- 物料汇总表格 -->
      <BasicTable
        v-if="materialSummaryList.length > 0"
        :dataSource="materialSummaryList"
        :columns="summaryColumns"
        :pagination="false"
        :canResize="false"
      >
        <!-- 申请数量编辑 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'applyQty'">
            <a-input-number
              v-model:value="record.applyQty"
              :min="0"
              :max="record.pendingQty * 2"  // 允许超量但限制倍数
            @change="(val) => handleQtyChange(record, val)"
            style="width: 100px"
            />
            <span v-if="record.isOverApply" style="color: orange; margin-left: 8px;">
              ⚠️ 超量
            </span>
          </template>

          <template v-if="column.dataIndex === 'overReason'">
            <a-input
              v-if="record.isOverApply"
              v-model:value="record.overApplyReason"
              placeholder="请填写超量原因"
              size="small"
            />
            <span v-else>-</span>
          </template>
        </template>
      </BasicTable>

      <!-- 公共信息 -->
      <a-divider />
      <BasicForm @register="registerCommonForm" />
    </div>
  </BasicModal>
</template>

<script setup>
  import { ref, computed, unref } from 'vue';
  import { useModalInner } from '/@/components/Modal';
  import { useForm } from '/@/components/Form';
  import { BasicTable } from '/@/components/Table';

  const emit = defineEmits(['success', 'register']);

  // 弹窗状态
  const applyMode = ref('single');  // single/batch/order
  const record = ref({});
  const orderId = ref(null);
  const orderNo = ref(null);
  const materialReqIds = ref([]);

  // 批量模式数据
  const batchList = ref([]);
  const selectedBatchIds = ref([]);
  const showBatchSelect = ref(false);
  const materialSummaryList = ref([]);

  // 弹窗标题
  const modalTitle = computed(() => {
    const titles = {
      single: '出库申请 - 单条物料',
      batch: '出库申请 - 批量汇总',
      order: '出库申请 - 按订单汇总'
    };
    return titles[applyMode.value] || '出库申请';
  });

  // 单条表单
  const [registerSingleForm, { validate: validateSingle, setFieldsValue: setSingleValues }] = useForm({
    schemas: [
      {
        field: 'applyQty',
        label: '申请数量',
        component: 'InputNumber',
        required: true,
        componentProps: { min: 0, precision: 6 },
        suffix: () => h('span', { style: { color: '#999' } }, `待发: ${pendingQty.value}`)
      },
      {
        field: 'overApplyReason',
        label: '超量原因',
        component: 'Input',
        show: ({ model }) => model.applyQty > pendingQty.value,
        dynamicRules: ({ model }) => model.applyQty > pendingQty.value ?
          [{ required: true, message: '超量申请需填写原因' }] : []
      },
      {
        field: 'expectDate',
        label: '期望领料日期',
        component: 'DatePicker',
        componentProps: { valueFormat: 'YYYY-MM-DD' }
      },
      {
        field: 'remark',
        label: '备注',
        component: 'InputTextArea'
      }
    ],
    showActionButtonGroup: false
  });

  // 公共表单（批量模式）
  const [registerCommonForm, { validate: validateCommon }] = useForm({
    schemas: [
      {
        field: 'expectDate',
        label: '期望领料日期',
        component: 'DatePicker',
        required: true,
        componentProps: { valueFormat: 'YYYY-MM-DD' }
      },
      {
        field: 'urgentLevel',
        label: '紧急程度',
        component: 'Select',
        defaultValue: 'normal',
        componentProps: {
          options: [
            { label: '普通', value: 'normal' },
            { label: '紧急', value: 'urgent' }
          ]
        }
      },
      {
        field: 'remark',
        label: '备注',
        component: 'InputTextArea'
      }
    ],
    showActionButtonGroup: false
  });

  // 汇总表格列
  const summaryColumns = [
    { title: '物料编码', dataIndex: 'materialCode', width: 100 },
    { title: '物料名称', dataIndex: 'materialName', width: 150 },
    { title: '规格型号', dataIndex: 'materialSpec', width: 120 },
    { title: '单位', dataIndex: 'unit', width: 60 },
    {
      title: '待发数量',
      dataIndex: 'pendingQty',
      width: 100,
      customRender: ({ text }) => h('span', { style: { color: 'red' } }, text)
    },
    {
      title: '本次申请',
      dataIndex: 'applyQty',
      width: 150,
      slots: { customRender: 'applyQty' }
    },
    {
      title: '超量原因',
      dataIndex: 'overReason',
      width: 200,
      slots: { customRender: 'overReason' }
    }
  ];

  // 计算待发数量
  const pendingQty = computed(() => {
    if (!record.value) return 0;
    return (record.value.requiredQty || 0) - (record.value.issuedQty || 0);
  });

  // 弹窗打开时的初始化
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    applyMode.value = data.mode || 'single';
    record.value = data.record || {};
    orderId.value = data.orderId || null;
    orderNo.value = data.orderNo || null;
    materialReqIds.value = data.materialReqIds || [];

    if (applyMode.value === 'single') {
      // 单条模式：初始化表单默认值
      setSingleValues({
        applyQty: pendingQty.value,
        expectDate: dayjs().add(1, 'day').format('YYYY-MM-DD')
      });
    } else if (applyMode.value === 'order') {
      // 按订单模式：加载批次列表
      await loadBatchList();
      showBatchSelect.value = true;
    } else if (applyMode.value === 'batch') {
      // 批量模式：直接使用选中的记录生成汇总
      generateSummaryFromRecords(data.records);
    }
  });

  // 加载批次列表
  async function loadBatchList() {
    const res = await getBatchesByOrder({ orderId: orderId.value });
    batchList.value = res.records || [];
  }

  // 确认批次选择，加载物料汇总
  async function confirmBatchSelect() {
    if (selectedBatchIds.value.length === 0) {
      message.warning('请至少选择一个批次');
      return;
    }

    const res = await getMaterialSummaryByBatches({
      batchIds: selectedBatchIds.value,
      orderId: orderId.value
    });

    materialSummaryList.value = res.records.map(item => ({
      ...item,
      applyQty: item.pendingQty,  // 默认申请待发数量
      isOverApply: false,
      overApplyReason: ''
    }));

    showBatchSelect.value = false;
  }

  // 从选中记录生成汇总（批量模式）
  function generateSummaryFromRecords(records) {
    // 按物料合并
    const materialMap = new Map();

    records.forEach(r => {
      const pending = (r.requiredQty || 0) - (r.issuedQty || 0);
      if (pending <= 0) return; // 跳过已完成的

      const key = r.materialId;
      if (materialMap.has(key)) {
        const exist = materialMap.get(key);
        exist.pendingQty += pending;
        exist.sourceRecords.push({ id: r.id, batchId: r.batchId, pending });
      } else {
        materialMap.set(key, {
          materialId: r.materialId,
          materialCode: r.materialCode,
          materialName: r.materialName,
          materialSpec: r.materialSpec,
          unit: r.unit,
          pendingQty: pending,
          applyQty: pending,
          sourceRecords: [{ id: r.id, batchId: r.batchId, pending }],
          isOverApply: false,
          overApplyReason: ''
        });
      }
    });

    materialSummaryList.value = Array.from(materialMap.values());
  }

  // 申请数量变化处理
  function handleQtyChange(record, value) {
    record.isOverApply = value > record.pendingQty;
    if (!record.isOverApply) {
      record.overApplyReason = '';
    }
  }

  // 提交申请
  async function handleSubmit() {
    try {
      let submitData = {
        applyType: 'production',
        expectDate: null,
        urgentLevel: 'normal',
        remark: ''
      };

      if (applyMode.value === 'single') {
        // 单条模式
        const formData = await validateSingle();
        const commonData = await validateCommon();

        submitData = {
          ...submitData,
          ...commonData,
          detailList: [{
            materialReqId: record.value.id,
            materialId: record.value.materialId,
            applyQty: formData.applyQty,
            overApplyReason: formData.overApplyReason,
            sourceRecord: record.value
          }]
        };
      } else {
        // 批量/订单模式
        const commonData = await validateCommon();

        // 检查是否有超量未填原因
        const overItems = materialSummaryList.value.filter(i => i.isOverApply && !i.overApplyReason);
        if (overItems.length > 0) {
          message.error(`物料 ${overItems[0].materialCode} 超量申请需填写原因`);
          return;
        }

        submitData = {
          ...submitData,
          ...commonData,
          orderId: orderId.value,
          batchIds: selectedBatchIds.value,
          detailList: materialSummaryList.value.map(item => ({
            materialId: item.materialId,
            applyQty: item.applyQty,
            overApplyReason: item.overApplyReason,
            // 追溯信息
            sourceRecords: item.sourceRecords || [{
              id: item.materialReqId,  // 单条模式
              batchId: item.batchId
            }]
          }))
        };
      }

      // 提交API
      const res = await submitIssueApply(submitData);
      message.success(`申请单 ${res.applyNo} 提交成功`);
      emit('success');
      closeModal();
    } catch (error) {
      console.error('提交失败:', error);
    }
  }
</script>

<style scoped>

</style>

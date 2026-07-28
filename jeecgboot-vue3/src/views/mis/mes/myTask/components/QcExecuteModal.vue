<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="1000"
    :canFullscreen="true"
    @ok="handleSubmit"
  >
    <a-alert type="info" class="mb-3" show-icon :message="sourceInfo" />

    <BasicForm @register="registerForm" />

    <div class="qc-detail-header">
      <span class="qc-detail-title">检验项目</span>
      <a-button size="small" type="primary" preIcon="ant-design:plus-outlined" @click="addItem">添加项目</a-button>
    </div>
    <a-table
      :columns="itemColumns"
      :dataSource="detailList"
      :pagination="false"
      size="small"
      rowKey="rowKey"
      bordered
    >
      <template #bodyCell="{ column, record, index }">
        <template v-if="column.dataIndex === 'itemName'">
          <a-input v-model:value="record.itemName" placeholder="项目名称" />
        </template>
        <template v-if="column.dataIndex === 'standard'">
          <a-input v-model:value="record.standard" placeholder="标准要求" />
        </template>
        <template v-if="column.dataIndex === 'actualValue'">
          <a-input v-model:value="record.actualValue" placeholder="实测值" />
        </template>
        <template v-if="column.dataIndex === 'itemResult'">
          <a-select v-model:value="record.itemResult" placeholder="判定" style="width: 100%" allowClear>
            <a-select-option value="PASS">合格</a-select-option>
            <a-select-option value="FAIL">不合格</a-select-option>
          </a-select>
        </template>
        <template v-if="column.dataIndex === 'equipmentCode'">
          <div style="min-width: 150px">
            <JPopup
              v-model:value="record.equipmentCode"
              code="mdm_equipment_select"
              :multi="false"
              :fieldConfig="equipmentFieldConfig"
              :setFieldsValue="(values) => Object.assign(record, values)"
            />
          </div>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <a-button size="small" danger type="link" @click="removeItem(index)">删除</a-button>
        </template>
      </template>
    </a-table>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm, FormSchema, JPopup } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { previewQcItems, completeQc } from '../MyTask.api';

  const emit = defineEmits(['register', 'success']);
  const { createMessage } = useMessage();

  const currentTask = ref<any>({});
  const detailList = ref<any[]>([]);
  let rowKeySeq = 0;

  const equipmentFieldConfig = [
    { source: 'id', target: 'equipmentId' },
    { source: 'equipment_code', target: 'equipmentCode' },
    { source: 'equipment_name', target: 'equipmentName' },
  ];

  const itemColumns = [
    { title: '检验项目', dataIndex: 'itemName', width: 150 },
    { title: '标准要求', dataIndex: 'standard', width: 140 },
    { title: '实测值', dataIndex: 'actualValue', width: 120 },
    { title: '判定', dataIndex: 'itemResult', width: 100 },
    { title: '检测设备', dataIndex: 'equipmentCode', width: 170 },
    { title: '操作', dataIndex: 'action', width: 70, align: 'center' },
  ];

  const formSchemas: FormSchema[] = [
    {
      label: '质检结果',
      field: 'qcResult',
      component: 'RadioButtonGroup',
      required: true,
      componentProps: {
        options: [
          { label: '合格', value: 'PASS' },
          { label: '不合格', value: 'FAIL' },
          { label: '返工', value: 'REWORK' },
        ],
      },
      colProps: { span: 24 },
    },
    {
      label: '质检结论',
      field: 'qcConclusion',
      component: 'InputTextArea',
      componentProps: { rows: 3, placeholder: '请输入质检结论' },
      colProps: { span: 24 },
    },
  ];

  const [registerForm, { resetFields, validate }] = useForm({
    schemas: formSchemas,
    showActionButtonGroup: false,
    labelWidth: 90,
    baseColProps: { span: 24 },
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false });
    await resetFields();
    detailList.value = [];
    const record = data?.record || {};
    currentTask.value = record;

    // 预生成检验项目（根据配方技术指标）
    if (record.id) {
      try {
        const items = await previewQcItems({ taskId: record.id });
        if (items && items.length > 0) {
          detailList.value = items.map((it) => ({ ...it, rowKey: ++rowKeySeq }));
        }
      } catch (e) {
        console.warn('预生成检验项目失败，可手工添加', e);
      }
    }
  });

  const title = computed(() => `质检录入 - ${currentTask.value?.batchNo || ''}`);

  const sourceInfo = computed(() => {
    const t = currentTask.value || {};
    return `工单：${t.taskNo || ''}　批次：${t.batchNo || ''}　产品：${t.productCode || ''} ${t.productName || ''}　订单：${t.orderNo || ''}`;
  });

  function addItem() {
    detailList.value.push({
      rowKey: ++rowKeySeq,
      itemName: '',
      standard: '',
      actualValue: '',
      itemResult: undefined,
      equipmentId: '',
      equipmentCode: '',
      equipmentName: '',
    });
  }

  function removeItem(index: number) {
    detailList.value.splice(index, 1);
  }

  async function handleSubmit() {
    try {
      const values = await validate();

      const invalid = detailList.value.filter((d) => !d.itemName || !d.itemName.trim());
      if (invalid.length > 0) {
        createMessage.warning('检验项目名称不能为空，请补充或删除空行');
        return;
      }

      setModalProps({ confirmLoading: true });
      const t = currentTask.value;
      await completeQc({
        qcTaskId: t.id,
        sourceTaskId: t.sourceTaskId,
        batchId: t.batchId,
        batchNo: t.batchNo,
        orderNo: t.orderNo,
        productId: t.productId,
        productCode: t.productCode,
        productName: t.productName,
        qcResult: values.qcResult,
        qcConclusion: values.qcConclusion,
        qcRecordDetailList: detailList.value.map((d, idx) => ({
          itemName: d.itemName,
          standard: d.standard,
          actualValue: d.actualValue,
          itemResult: d.itemResult,
          equipmentId: d.equipmentId,
          equipmentCode: d.equipmentCode,
          equipmentName: d.equipmentName,
          sortNo: idx + 1,
        })),
      });

      createMessage.success('质检完成');
      closeModal();
      emit('success');
    } catch (error) {
      return Promise.reject(error);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
  .qc-detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 8px 0;
  }
  .qc-detail-title {
    font-weight: 600;
  }
</style>

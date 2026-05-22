<<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="1100"
    :minHeight="700"

    @ok="handleSubmit">

    <!-- 盘点单信息 -->
    <a-descriptions :column="3" bordered size="small" class="mb-4">
      <a-descriptions-item label="盘点单号">{{ record.checkNo }}</a-descriptions-item>
      <a-descriptions-item label="盘点范围">{{ record.checkScope_dictText }}</a-descriptions-item>
      <a-descriptions-item label="盘点方法">{{ record.checkMethod_dictText }}</a-descriptions-item>
      <a-descriptions-item label="盘点人">{{ record.checkUserName }}</a-descriptions-item>
      <a-descriptions-item label="开始时间">{{ record.checkStartTime }}</a-descriptions-item>
      <a-descriptions-item label="完成时间">{{ record.checkFinishedTime }}</a-descriptions-item>
    </a-descriptions>

    <!-- 差异汇总 -->
    <a-row :gutter="16" class="mb-4">
      <a-col :span="6">
        <a-card size="small">
          <div class="stat-item">
            <div class="stat-title">总项数</div>
            <div class="stat-value">{{ summary.totalItems }}</div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card size="small">
          <div class="stat-item">
            <div class="stat-title">已盘项数</div>
            <div class="stat-value">{{ summary.checkedItems }}</div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card size="small">
          <div class="stat-item">
            <div class="stat-title">差异项数</div>
            <div class="stat-value" :class="summary.diffItems > 0 ? 'text-red' : 'text-green'">
              {{ summary.diffItems }}
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card size="small">
          <div class="stat-item">
            <div class="stat-title">差异金额</div>
            <div class="stat-value" :class="summary.diffAmount > 0 ? 'text-red' : 'text-green'">
              {{ formatNumber(summary.diffAmount) }}
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 差异明细表格 -->
    <a-divider orientation="left">差异明细（仅显示有差异的记录）</a-divider>
    <BasicTable
      @register="registerDiffTable"
      :dataSource="diffList"
      :pagination="{ pageSize: 10 }" />

    <!-- 审核表单 -->
    <a-divider orientation="left">审核意见</a-divider>
    <BasicForm @register="registerApproveForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed} from 'vue';
  import {BasicModal, useModalInner} from '/@/components/Modal';
  import {BasicTable, useTable} from '/@/components/Table';
  import {BasicForm, useForm} from '/@/components/Form';
  import {useMessage} from '/@/hooks/web/useMessage';
  import {approveCheck, inventoryCheckDetailList} from '../InventoryCheck.api';
  import {approveDiffColumns} from '../InventoryCheck.data';

  const {createMessage, createConfirm} = useMessage();

  const record = ref<any>({});
  const diffList = ref<any[]>([]);
  const summary = ref({
    totalItems: 0,
    checkedItems: 0,
    diffItems: 0,
    diffAmount: 0
  });

  const [registerDiffTable, { setTableData }] = useTable({
    columns: approveDiffColumns,
    //dataSource: [],
    pagination: { pageSize: 10 }
  });

  const approveFormSchema = [
    {
      label: '审核结果',
      field: 'approveStatus',
      component: 'JDictSelectTag',
      componentProps: { dictCode: 'approval_status' },
      //dynamicRules: [{ required: true, message: '请选择审核结果!' }],
      dynamicRules: ({model,schema}) => {
        return [
          { required: true, message: '请选择审核结果!'},
        ];
      },
    },
    {
      label: '审核备注',
      field: 'approveRemark',
      component: 'InputTextArea',
      dynamicRules: ({model}) => {
        return model?.approveStatus === '2'
          ? [{ required: true, message: '不通过时必须填写原因!' }]
          : [];
      }
    }
  ];

  const [registerApproveForm, {validate, resetFields, setFieldsValue}] = useForm({
    schemas: approveFormSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 24}
  });

  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    record.value = data.record || {};
    await resetFields();
    await loadDetailData(data.record.id);
    setModalProps({ confirmLoading: false });
  });

  async function loadDetailData(checkId: string) {
    const res = await inventoryCheckDetailList({checkId});
    const list = res.records || [];
    console.log("res.records:",list)
    // 只显示有差异的
    diffList.value = list.filter(item => item.diffQty !== 0 && item.diffQty !== null);
    console.log("diffList:",diffList.value)
    setTableData(diffList.value);
    summary.value = {
      totalItems: list.length,
      checkedItems: list.filter(i => i.checkStatus === '2').length,
      diffItems: diffList.value.length,
      diffAmount: diffList.value.reduce((sum, item) => sum + (item.diffAmount || 0), 0)
    };
  }

  function formatNumber(val: number): string {
    return val?.toFixed(2) || '0.00';
  }

  async function handleSubmit() {
    try {
      const values = await validate();

      if (values.approveStatus === '2') {
        createConfirm({
          title: '确认退回',
          content: '审核不通过将退回盘点中状态，盘点人需重新盘点，确定吗？',
          onOk: async () => {
            await doApprove(values);
          }
        });
      } else {
        await doApprove(values);
      }
    } catch (e) {
      console.error('审核失败:', e);
    }
  }

  async function doApprove(values: any) {
    setModalProps({ confirmLoading: true });
    try {
      // 审核只传主表字段，不带子表
      await approveCheck({
        id: record.value.id,
        approveStatus: values.approveStatus,
        approveRemark: values.approveRemark
      });
      createMessage.success('审核完成');
      closeModal();
      emit('success');
    } catch (e) {
      createMessage.error(e.message || '审核失败');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }

  const title = computed(() => `审核盘点单 - ${record.value.checkNo || ''}`);
  const emit = defineEmits(['register','success']);
</script>

<style lang="less" scoped>
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
  .text-red { color: #cf1322; }
  .text-green { color: #3f8600; }
</style>

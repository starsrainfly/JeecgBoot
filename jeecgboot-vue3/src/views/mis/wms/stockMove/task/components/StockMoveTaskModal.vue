<<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="800"
    @ok="handleSubmit"
  >
    <!-- 单条：显示原库存信息 -->
    <div v-if="!isBatch && currentRecord" class="mb-4 p-4 bg-gray-50 rounded">
      <h4 class="font-bold mb-2">原位置信息</h4>
      <a-descriptions :column="2" size="small" bordered>
        <a-descriptions-item label="物料编码">{{ currentRecord.goodsCode }}</a-descriptions-item>
        <a-descriptions-item label="物料名称">{{ currentRecord.goodsName }}</a-descriptions-item>
        <a-descriptions-item label="批号">{{ currentRecord.batchNo || '-' }}</a-descriptions-item>
        <a-descriptions-item label="当前库存">{{ currentRecord.quantity }} {{ currentRecord.unit }}</a-descriptions-item>
        <a-descriptions-item label="原仓库">{{ currentRecord.warehouseId_dictText }}</a-descriptions-item>
        <a-descriptions-item label="原区域">{{ currentRecord.areaId_dictText }}</a-descriptions-item>
        <a-descriptions-item label="原货架">{{ currentRecord.shelfId_dictText || '-' }}</a-descriptions-item>
        <a-descriptions-item label="原货位">{{ currentRecord.locationId_dictText || '-' }}</a-descriptions-item>
      </a-descriptions>
    </div>

    <!-- 批量：显示表格 -->
    <div v-else-if="isBatch" class="mb-4">
      <a-alert
        :message="`已选择 ${moveRecords.length} 条库存记录，将按库存数量全部移库`"
        type="info"
        show-icon
        banner
        class="mb-2"
      />
      <BasicTable
        :columns="batchColumns"
        :dataSource="moveRecords"
        :pagination="false"
        :showIndexColumn="true"
        size="small"
        bordered
        rowKey="id"
      />
    </div>

    <!-- 表单 -->
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicTable } from '/@/components/Table';
  import { Descriptions, Alert } from 'ant-design-vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { doMove, batchMove } from '../StockMoveTask.api';
  import { singleMoveFormSchema, batchMoveFormSchema } from '../StockMoveTask.data';

  const ADescriptions = Descriptions;
  const ADescriptionsItem = Descriptions.Item;
  const AAlert = Alert;

  const emit = defineEmits(['register', 'success']);

  const { createMessage } = useMessage();

  const isBatch = ref(false);
  const currentRecord = ref<any>(null);
  const moveRecords = ref<any[]>([]);

  // 批量表格列
  const batchColumns = [
    { title: '物料编码', dataIndex: 'goodsCode', width: 110 },
    { title: '物料名称', dataIndex: 'goodsName', width: 120 },
    { title: '批号', dataIndex: 'batchNo', width: 120 },
    { title: '移库数量', dataIndex: 'quantity', width: 80 },
    { title: '单位', dataIndex: 'unit', width: 60 },
    { title: '原仓库', dataIndex: 'warehouseId_dictText', width: 100 },
    { title: '原区域', dataIndex: 'areaId_dictText', width: 100 },
  ];

  // 设置标题
  const title = computed(() => (unref(isBatch) ? '批量移库' : '移库作业'));

  // 表单配置 —— 默认单条
  const [registerForm, { setProps, resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 120,
    schemas: singleMoveFormSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 12 },
  });

  // 表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    // 重置表单
    await resetFields();
    setModalProps({ confirmLoading: false });

    isBatch.value = !!data?.isBatch;
    moveRecords.value = data?.records || (data?.record ? [data.record] : []);
    currentRecord.value = data?.record || null;

    // 切换表单 schema
    if (unref(isBatch)) {
      await setProps({ schemas: batchMoveFormSchema });
    } else {
      await setProps({ schemas: singleMoveFormSchema });
    }

    // 单条回显
    if (!unref(isBatch) && currentRecord.value) {
      await setFieldsValue({
        fromStockId: currentRecord.value.id,
        goodsName: currentRecord.value.goodsName,
        goodsCode: currentRecord.value.goodsCode,
        goodsSpec: currentRecord.value.goodsSpec,
        batchNo: currentRecord.value.batchNo,
        quantity: currentRecord.value.quantity,
        unit: currentRecord.value.unit,
        moveQty: currentRecord.value.quantity,
      });
    }
  });

  // 表单提交
  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });

      if (unref(isBatch) && moveRecords.value.length > 0) {
        // 批量移库
        const list = moveRecords.value.map((record) => ({
          fromStockId: record.id,
          toWarehouseId: values.toWarehouseId,
          toAreaId: values.toAreaId,
          toShelfId: values.toShelfId,
          toLocationId: values.toLocationId,
          moveQty: record.quantity,
          moveReason: values.moveReason,
          remark: values.remark,
        }));
        await batchMove(list);
        createMessage.success('批量移库成功');
      } else {
        // 单条移库
        await doMove({
          fromStockId: moveRecords.value[0].id,
          toWarehouseId: values.toWarehouseId,
          toAreaId: values.toAreaId,
          toShelfId: values.toShelfId,
          toLocationId: values.toLocationId,
          moveQty: values.moveQty,
          moveReason: values.moveReason,
          remark: values.remark,
        });
        createMessage.success('移库成功');
      }

      closeModal();
      emit('success');
    } catch (error) {
      console.error('移库失败:', error);
      createMessage.error(error.message || '移库失败');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>

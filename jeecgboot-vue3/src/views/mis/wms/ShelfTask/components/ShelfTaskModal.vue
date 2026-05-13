<!-- src/views/wms/shelfTask/components/ShelfTaskModal.vue -->
<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="800" @ok="handleSubmit">
    <!-- 批量时显示提示 -->
    <div v-if="isBatch" class="batch-tip">
      <a-alert type="info" :message="`已选择 ${records.length} 条记录，将统一上架到目标位置`" banner />
    </div>
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref , nextTick} from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { shelfFormSchema,batchShelfFormSchema  } from '../ShelfTask.data';
  import { doShelf, batchShelf } from '../ShelfTask.api';

  // Emits声明
  const emit = defineEmits(['register', 'success']);

  const isBatch = ref(false);
  const records = ref<any[]>([]);

  // 当前表单值缓存
  const formCache = ref({
    toWarehouseId: '',
    toAreaId: '',
    toShelfId: '',
  });
  // 动态选择 schema：单条用 shelfFormSchema，批量用 batchShelfFormSchema
  const currentSchemas = ref(shelfFormSchema);
  // 表单配置 //shelfFormSchema,
  const [registerForm, { setProps,resetFields, setFieldsValue, validate, getFieldsValue }] = useForm({
    labelWidth: 120,
    schemas: shelfFormSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 12 },
  });

  // 设置标题
  const title = computed(() => (unref(isBatch) ? '批量上架' : '上架操作'));

  // ============ 联动事件处理 ============
  async function handleWarehouseChange(warehouseId: string) {

    if (formCache.value.toWarehouseId === warehouseId) return;
    formCache.value.toWarehouseId = warehouseId;

    // 清空下级
    await setFieldsValue({
      toAreaId: undefined,
      toShelfId: undefined,
      toLocationId: undefined,
    });
    formCache.value.toAreaId = '';
    formCache.value.toShelfId = '';
  }

  async function handleAreaChange(areaId: string) {

    if (formCache.value.toAreaId === areaId) return;
    formCache.value.toAreaId = areaId;

    // 清空下级
    await setFieldsValue({
      toShelfId: undefined,
      toLocationId: undefined,
    });
    formCache.value.toShelfId = '';
  }

  async function handleShelfChange(shelfId: string) {

    if (formCache.value.toShelfId === shelfId) return;
    formCache.value.toShelfId = shelfId;

    // 清空货位
    await setFieldsValue({
      toLocationId: undefined,
    });
  }

  // 暴露事件供 data.ts 使用
  const formEvents = {
    handleWarehouseChange,
    handleAreaChange,
    handleShelfChange,
  };
  (window as any).shelfTaskEvents = formEvents;

  // 表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {

    // 重置表单
    await resetFields();
    setModalProps({ confirmLoading: false });

    formCache.value = { toWarehouseId: '', toAreaId: '', toShelfId: '' };

    isBatch.value = !!data?.isBatch;
    records.value = data?.records || (data?.record ? [data.record] : []);
//console.log("dialog records:",records.value)
    // 动态切换 schema
    // 关键：用 setProps 切换 schemas
    if (unref(isBatch)) {
      await setProps({ schemas: batchShelfFormSchema });
    } else {
      await setProps({ schemas: shelfFormSchema });
    }

    if (!unref(isBatch) && records.value.length === 1) {
      // 单条上架，回显库存信息
      const record = records.value[0];
      await setFieldsValue({
        stockId: record.id,
        goodsName: record.goodsName,
        goodsCode: record.goodsCode,
        goodsSpec: record.goodsSpec,
        batchNo: record.batchNo,
        quantity: record.quantity,
        unit: record.unit,
        shelfQty: record.quantity, // 默认全部上架
      });
    }

  });

  // 表单提交事件
  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });

      if (unref(isBatch) && records.value.length > 0) {
        // 批量上架
        const list = records.value.map((record) => ({
          stockId: record.id,
          toWarehouseId: values.toWarehouseId,
          toAreaId: values.toAreaId,
          toShelfId: values.toShelfId,
          toLocationId: values.toLocationId,
          shelfQty: record.quantity, // 批量默认全部上架
          remark: values.remark,
        }));
        await batchShelf(list);
      } else {
        // 单条上架
        await doShelf({
          stockId: records.value[0].id,
          toWarehouseId: values.toWarehouseId,
          toAreaId: values.toAreaId,
          toShelfId: values.toShelfId,
          toLocationId: values.toLocationId,
          shelfQty: values.shelfQty,
          remark: values.remark,
        });
      }

      // 关闭弹窗
      closeModal();
      // 刷新列表
      emit('success');
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

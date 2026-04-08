<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1024" @ok="handleSubmit">
    <BasicForm @register="registerForm" name="WarehouseLocationForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref} from 'vue';
  import {BasicModal, useModalInner} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import {formSchema} from '../WarehouseLocation.data';
  import {saveOrUpdate, getWarehouseById, getAreaById, getShelfById} from '../WarehouseLocation.api';

  const emit = defineEmits(['register','success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);

  // 存储 code 的响应式数据
  const codeMap = ref({
    warehouseCode: '',
    areaCode: '',
    shelfCode: ''
  });

  // 当前表单值缓存（用于对比和生成 pathCode）
  const formCache = ref({
    warehouseId: '',
    areaId: '',
    shelfId: '',
    locationCode: ''
  });

  const [registerForm, {
    setProps,
    resetFields,
    setFieldsValue,
    validate,
    scrollToField,
    getFieldsValue
  }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 8}
  });

  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    await resetFields();

    // 重置状态
    codeMap.value = { warehouseCode: '', areaCode: '', shelfCode: '' };
    formCache.value = { warehouseId: '', areaId: '', shelfId: '', locationCode: '' };

    setModalProps({
      confirmLoading: false,
      showCancelBtn: !!data?.showFooter,
      showOkBtn: !!data?.showFooter
    });

    isUpdate.value = !!data?.isUpdate;
    isDetail.value = !data?.showFooter;

    if (unref(isUpdate)) {
      await setFieldsValue({...data.record});

      // 初始化缓存
      formCache.value = {
        warehouseId: data.record?.warehouseId || '',
        areaId: data.record?.areaId || '',
        shelfId: data.record?.shelfId || '',
        locationCode: data.record?.locationCode || ''
      };

      // 加载已有数据的 code
      if (data.record?.warehouseId) {
        await loadWarehouseCode(data.record.warehouseId);
      }
      if (data.record?.areaId) {
        await loadAreaCode(data.record.areaId);
      }
      if (data.record?.shelfId) {
        await loadShelfCode(data.record.shelfId);
      }

      // 生成 pathCode
      if (data.record?.locationCode) {
        generatePathCode();
      }
    }

    setProps({ disabled: !data?.showFooter });
  });

  const title = computed(() => {
    if (!unref(isUpdate)) return '新增';
    return unref(isDetail) ? '详情' : '编辑';
  });

  // ============ 事件处理函数（由 formSchema 的 onChange 调用） ============

  // 仓库变化处理
  async function handleWarehouseChange(warehouseId: string) {
    if (formCache.value.warehouseId === warehouseId) return;

    formCache.value.warehouseId = warehouseId;

    if (warehouseId) {
      await loadWarehouseCode(warehouseId);
    } else {
      codeMap.value.warehouseCode = '';
    }

    // 清空下级
    setFieldsValue({
      areaId: undefined,
      areaCode: undefined,
      shelfId: undefined,
      shelfCode: undefined,
      locationCode: undefined,
      pathCode: undefined
    });

    formCache.value.areaId = '';
    formCache.value.shelfId = '';
    formCache.value.locationCode = '';
    codeMap.value.areaCode = '';
    codeMap.value.shelfCode = '';
  }

  // 区域变化处理
  async function handleAreaChange(areaId: string) {
    if (formCache.value.areaId === areaId) return;

    formCache.value.areaId = areaId;

    if (areaId) {
      await loadAreaCode(areaId);
    } else {
      codeMap.value.areaCode = '';
    }

    // 清空下级
    setFieldsValue({
      shelfId: undefined,
      shelfCode: undefined,
      locationCode: undefined,
      pathCode: undefined
    });

    formCache.value.shelfId = '';
    formCache.value.locationCode = '';
    codeMap.value.shelfCode = '';
  }

  // 货架变化处理
  async function handleShelfChange(shelfId: string) {
    if (formCache.value.shelfId === shelfId) return;

    formCache.value.shelfId = shelfId;

    if (shelfId) {
      await loadShelfCode(shelfId);
    } else {
      codeMap.value.shelfCode = '';
    }

    generatePathCode();
  }

  // 库位编码变化处理
  function handleLocationCodeChange(locationCode: string) {
    formCache.value.locationCode = locationCode;
    generatePathCode();
  }

  // 暴露给 formSchema 使用
  const formEvents = {
    handleWarehouseChange,
    handleAreaChange,
    handleShelfChange,
    handleLocationCodeChange
  };

  // 将事件函数挂载到全局供 data.ts 使用
  (window as any).warehouseLocationEvents = formEvents;

  // ============ 加载 Code ============
  async function loadWarehouseCode(warehouseId: string) {
    try {
      const res = await getWarehouseById({id: warehouseId});
      codeMap.value.warehouseCode = res.warehouseCode || '';
      setFieldsValue({ warehouseCode: codeMap.value.warehouseCode });
    } catch (error) {
      console.error('加载仓库Code失败', error);
      codeMap.value.warehouseCode = '';
    }
  }

  async function loadAreaCode(areaId: string) {
    try {
      const res = await getAreaById({id: areaId});
      codeMap.value.areaCode = res.areaCode || '';
      setFieldsValue({ areaCode: codeMap.value.areaCode });
    } catch (error) {
      console.error('加载区域Code失败', error);
      codeMap.value.areaCode = '';
    }
  }

  async function loadShelfCode(shelfId: string) {
    try {
      const res = await getShelfById({id: shelfId});
      codeMap.value.shelfCode = res.shelfCode || '';
      setFieldsValue({ shelfCode: codeMap.value.shelfCode });
    } catch (error) {
      console.error('加载货架Code失败', error);
      codeMap.value.shelfCode = '';
    }
  }

  // ============ 生成 PathCode ============
  function generatePathCode() {
    const { warehouseCode, areaCode, shelfCode } = codeMap.value;
    const locationCode = formCache.value.locationCode;

    if (warehouseCode && areaCode && shelfCode && locationCode) {
      const pathCode = `${warehouseCode}-${areaCode}-${shelfCode}-${locationCode}`;
      setFieldsValue({ pathCode });
    }
  }

  // ============ 表单提交 ============
  async function handleSubmit(v) {
    try {
      let values = await validate();
      setModalProps({confirmLoading: true});

      // 确保 pathCode 已生成
      const { warehouseCode, areaCode, shelfCode } = codeMap.value;
      if (warehouseCode && areaCode && shelfCode && values.locationCode) {
        values.pathCode = `${warehouseCode}-${areaCode}-${shelfCode}-${values.locationCode}`;
      }

      await saveOrUpdate(values, isUpdate.value);
      closeModal();
      emit('success');
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    } finally {
      setModalProps({confirmLoading: false});
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-input-number) {
    width: 100%;
  }
  :deep(.ant-calendar-picker) {
    width: 100%;
  }
</style>

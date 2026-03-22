<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="1280"
    @ok="handleSubmit"
  >
    <BasicForm @register="registerForm" ref="formRef" name="RecipeForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="配方明细" key="recipeDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="recipeDetail"
          :loading="recipeDetailTable.loading"
          :columns="recipeDetailTable.columns"
          :dataSource="recipeDetailTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          @valueChange="handleDetailValueChange"
          @deleted="handleDetailDeleted"
        />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive, watch, nextTick} from 'vue';
  import {BasicModal, useModalInner} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import {formSchema,recipeDetailColumns} from '../Recipe.data';
  import {saveOrUpdate,recipeDetailList} from '../Recipe.api';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage } = useMessage();

  // ========== 关键：必须声明 register 和 success 事件 ==========
  const emit = defineEmits(['register', 'success']);

  const isUpdate = ref(true);
  const formDisabled = ref(false);
  const refKeys = ref(['recipeDetail', ]);
  const activeKey = ref('recipeDetail');
  const recipeDetail = ref();
  const tableRefs = {recipeDetail, };

  const recipeDetailTable = reactive({
    loading: false,
    dataSource: [],
    columns: recipeDetailColumns,
  });

  const [registerForm, {
    setProps,
    resetFields,
    setFieldsValue,
    validate,
    getFieldsValue,
    validateFields,
    clearValidate,
    updateSchema
  }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 6},

  });

  // ========== 关键：使用 useModalInner 并传入回调 ==========
  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    // 重置表单
    await reset();
    setModalProps({
      confirmLoading: false,
      showCancelBtn: data?.showFooter,
      showOkBtn: data?.showFooter
    });
    isUpdate.value = !!data?.isUpdate;
    formDisabled.value = !data?.showFooter;

    if (unref(isUpdate)) {
      await setFieldsValue({ ...data.record });

      requestSubTableData(recipeDetailList, {id: data?.record?.id}, recipeDetailTable, () => {
        nextTick(() => {
          calculateProportionTotal();
          const currentType = data.record?.proportionType || '1';
          if (currentType === '1') {
            validateFields(['proportionTotal']).catch(() => {});
          }
        });
      });
    }

    setProps({ disabled: !data?.showFooter });
  });

  const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] = useJvxeMethod(
    requestAddOrEdit,
    classifyIntoFormData,
    tableRefs,
    activeKey,
    refKeys
  );

  let lastProportionType = ref('1');

  watch(
    () => {
      try {
        const values = getFieldsValue();
        return values?.proportionType;
      } catch (e) {
        return '1';
      }
    },
    (newType) => {
      if (!newType) return;

      // 类型变化时处理
      if (newType !== lastProportionType.value) {
        lastProportionType.value = newType;

        // 延迟执行，确保表单已更新
        setTimeout(() => {
          if (newType === '1') {
            // 标准类型：触发校验（显示错误提示）
            validateFields(['proportionTotal']).catch(() => {});
          } else {
            // 特殊类型：清除错误提示
            clearValidate(['proportionTotal']);
          }
        }, 100);
      }
    },
    { immediate: false }
  );

  // 计算配比总和
  function calculateProportionTotal() {
    const tableData = recipeDetailTable.dataSource || [];
    let total = 0;
    tableData.forEach(row => {
      total += parseFloat(row.proportion) || 0;
    });

    total = Math.round(total * 100) / 100;

    try {
      const currentValues = getFieldsValue();
      if (currentValues) {
        setFieldsValue({ proportionTotal: total.toString() });
      }
    } catch (e) {
      console.warn('设置配比总和失败:', e);
    }
  }

  // 明细行值变化
  function handleDetailValueChange({ row, column, value }: any) {
    if (column.key === 'proportion') {
      const index = recipeDetailTable.dataSource.findIndex((item: any) =>
        item.id === row.id || (item._X_ROW_KEY && item._X_ROW_KEY === row._X_ROW_KEY)
      );
      if (index !== -1) {
        recipeDetailTable.dataSource[index].proportion = value;
      }

      calculateProportionTotal();
      nextTick(() => {
        const currentType = getFieldsValue()?.proportionType;
        if (currentType === '1') {
          validateFields(['proportionTotal']).catch(() => {});
        }
        else{
          clearValidate(['proportionTotal']);
        }
      });
    }
  }

  // 明细行删除
  function handleDetailDeleted() {
    nextTick(() => {
      calculateProportionTotal();
      const currentType = getFieldsValue()?.proportionType;
      if (currentType === '1') {
        validateFields(['proportionTotal']).catch(() => {});
      }
      else{
        // 特殊类型：清除错误提示
        clearValidate(['proportionTotal']);
      }
    });
  }

  const title = computed(() => {
    if (!unref(isUpdate)) return '新增';
    return !unref(formDisabled) ? '编辑' : '详情';
  });

  async function reset() {
    await resetFields();
    activeKey.value = 'recipeDetail';
    recipeDetailTable.dataSource = [];
  }

  function classifyIntoFormData(allValues: any) {
    let main = Object.assign({}, allValues.formValue);

    const tableData = allValues.tablesValue[0].tableData || [];
    let total = 0;
    tableData.forEach((row: any) => {
      total += parseFloat(row.proportion) || 0;
    });
    main.proportionTotal = (Math.round(total * 100) / 100).toString();

    return {
      ...main,
      recipeDetailList: tableData,
    };
  }

  async function requestAddOrEdit(values: any) {
    try {
      const proportionType = values.proportionType;
      const proportionTotal = parseFloat(values.proportionTotal) || 0;

      if (proportionType === '1' && proportionTotal !== 100) {
        createMessage.error('标准类型的配比总和必须等于100，当前总和为：' + proportionTotal);
        return;
      }

      setModalProps({ confirmLoading: true });
      await saveOrUpdate(values, isUpdate.value);
      closeModal();
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

  :deep(.ant-calendar-picker) {
    width: 100%;
  }
</style>

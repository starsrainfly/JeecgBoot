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
          @edit-closed="handleEditClosed"
          @removed="handleDetailDeleted"
        />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive, nextTick} from 'vue';
  import {BasicModal, useModalInner} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import {formSchema,recipeDetailColumns} from '../Recipe.data';
  import {saveOrUpdate,recipeDetailList} from '../Recipe.api';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage } = useMessage();
  const emit = defineEmits(['register', 'success']);

  const isUpdate = ref(true);
  const formDisabled = ref(false);
  const refKeys = ref(['recipeDetail']);
  const activeKey = ref('recipeDetail');
  const recipeDetail = ref();
  const tableRefs = {recipeDetail};

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
  }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 6},
  });

  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
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

  // ========== 编辑完成触发计算 ==========
  // function handleEditClosed(event: any) {
  //   const { row, column, value } = event || {};
  //
  //   if (column?.key === 'proportion' || column?.field === 'proportion') {
  //     const index = recipeDetailTable.dataSource.findIndex((item: any) =>
  //       item.id === row.id || (item._X_ROW_KEY && item._X_ROW_KEY === row._X_ROW_KEY)
  //     );
  //     if (index !== -1) {
  //       recipeDetailTable.dataSource[index].proportion = value;
  //     }
  //
  //     calculateProportionTotal();
  //   }
  // }
  // ========== 编辑完成触发计算 ==========
  function handleEditClosed(event: any) {
    const { row, column, value } = event || {};

    // 只处理配比列
    if (column?.key === 'proportion' || column?.field === 'proportion') {
      // 关键：直接从 JVxeTable 实例获取全部数据
      const jvxeTable = recipeDetail.value;
      if (!jvxeTable) return;

      let tableData = [];
      if (jvxeTable.getTableData) {
        tableData = jvxeTable.getTableData();
      } else if (jvxeTable.getData) {
        tableData = jvxeTable.getData();
      } else if (jvxeTable.getRecords) {
        tableData = jvxeTable.getRecords();
      } else if (jvxeTable.getCurrentData) {
        tableData = jvxeTable.getCurrentData();
      }

      console.log('JVxeTable 数据:', tableData);

      // 用实例数据计算，不依赖 dataSource
      let total = 0;
      tableData.forEach((item: any) => {
        total += parseFloat(item.proportion) || 0;
      });
      total = Math.round(total * 100) / 100;

      // 同时同步回 dataSource（保持后续逻辑一致）
      recipeDetailTable.dataSource = tableData;

      try {
        setFieldsValue({ proportionTotal: total.toString() });
      } catch (e) {
        console.warn('设置配比总和失败:', e);
      }

      // 校验
      nextTick(() => {
        const currentType = getFieldsValue()?.proportionType;
        if (currentType === '1') {
          validateFields(['proportionTotal']).catch(() => {});
        } else {
          clearValidate(['proportionTotal']);
        }
      });
    }
  }

  // ========== 删除行触发计算 ==========
  function handleDetailDeleted() {
    console.log("delete detail row");
    nextTick(() => {
      doCalculate();
    });
  }

  // ========== 计算配比总和 ==========
  function calculateProportionTotal() {
    const tableData = recipeDetailTable.dataSource || [];
    let total = 0;
    tableData.forEach(row => {
      total += parseFloat(row.proportion) || 0;
    });
    total = Math.round(total * 100) / 100;

    try {
      setFieldsValue({ proportionTotal: total.toString() });
    } catch (e) {
      console.warn('设置配比总和失败:', e);
    }

    nextTick(() => {
      const currentType = getFieldsValue()?.proportionType;
      if (currentType === '1') {
        validateFields(['proportionTotal']).catch(() => {});
      } else {
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
  let calculateTimer = null;
  function handleDetailValueChange(row, value) {
    if (calculateTimer) clearTimeout(calculateTimer);
    calculateTimer = setTimeout(() => {
      doCalculate();
    }, 800);
  }

  function doCalculate() {
    const jvxeTable = recipeDetail.value;
    console.log("recipeDetail.vlaue",recipeDetail.value)
    if (!jvxeTable) return;

    let tableData = [];
    if (jvxeTable.getTableData) {
      tableData = jvxeTable.getTableData();
    } else if (jvxeTable.getData) {
      tableData = jvxeTable.getData();
    }

    let total = 0;
    tableData.forEach(item => {
      total += parseFloat(item.proportion) || 0;
    });
    total = Math.round(total * 100) / 100;

    recipeDetailTable.dataSource = tableData;

    try {
      setFieldsValue({ proportionTotal: total.toString() });
    } catch (e) {
      console.warn('设置配比总和失败:', e);
    }

    nextTick(() => {
      const currentType = getFieldsValue()?.proportionType;
      if (currentType === '1') {
        validateFields(['proportionTotal']).catch(() => {});
      } else {
        clearValidate(['proportionTotal']);
      }
    });
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

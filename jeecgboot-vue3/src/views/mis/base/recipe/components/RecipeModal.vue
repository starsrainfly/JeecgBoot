<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="1440"
    @ok="handleSubmit"
  >
    <!-- 左右分栏 -->
    <a-row :gutter="20">
      <!-- 左侧：主表（占 9/24 ≈ 37.5%） -->
      <a-col :span="9" class="left-panel">
        <BasicForm
          @register="registerForm"
          ref="formRef"
          name="RecipeForm"
        />
      </a-col>

      <!-- 右侧：子表（占 15/24 ≈ 62.5%） -->
      <a-col :span="15" class="right-panel">
        <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
          <a-tab-pane tab="配方明细" key="recipeDetail" :forceRender="true">
            <JVxeTable
              keep-source
              resizable
              ref="recipeDetail"
              :loading="recipeDetailTable.loading"
              :columns="recipeDetailTable.columns"
              :dataSource="recipeDetailTable.dataSource"
              :height="800"
              :rowNumber="true"
              :rowSelection="true"
              :disabled="formDisabled"
              :toolbar="true"
              @edit-closed="handleEditClosed"
              @removed="handleDetailDeleted"
            >
              <template #materialCode="{ row }">
                <a-input
                  :value="row.materialCode"
                  readonly
                  :disabled="formDisabled"
                  :placeholder="formDisabled ? '' : '点击选择物料'"
                  :style="{ cursor: formDisabled ? 'default' : 'pointer' }"
                  @click="openMaterialSelect(row)"
                >
                  <template #suffix>
                    <SearchOutlined v-if="!formDisabled" style="color: #1890ff" />
                  </template>
                </a-input>
              </template>
            </JVxeTable>
          </a-tab-pane>
        </a-tabs>
      </a-col>
    </a-row>

    <MaterialSelectModal @register="registerMaterialModal" @select="handleMaterialSelect" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive, nextTick} from 'vue';
  import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import {formSchema,recipeDetailColumns} from '../Recipe.data';
  import {saveOrUpdate,recipeDetailList} from '../Recipe.api';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { SearchOutlined } from '@ant-design/icons-vue';
  //import MaterialSelectModal from './MaterialSelectModal.vue';
  import MaterialSelectModal from '/@/components/MaterialSelect';

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
    baseColProps: {span: 24},      // ← 左侧单列，每行一个字段
    labelCol: { span: 6 },        // 标签窄一点
    wrapperCol: { span: 18 },
  });

  const [registerMaterialModal, { openModal: openMaterialModal }] = useModal();
  let currentEditRow: any = null;

  function openMaterialSelect(row: any) {
    if (formDisabled.value) return;
    currentEditRow = row;
    openMaterialModal(true, {});
  }

  function handleMaterialSelect(record: any) {
    if (!currentEditRow) return;
    const rowKey = currentEditRow.id ?? currentEditRow._X_ROW_KEY;
    const values = {
      materialId: record.id,
      materialCode: record.materialCode,
      materialName: record.materialName,
      materialSpec: record.materialSpec,
    };
    recipeDetail.value?.setValues([{ rowKey, values }]);
    Object.assign(currentEditRow, values);
    currentEditRow = null;
  }

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

  function handleEditClosed(event: any) {
    const { row, column, value } = event || {};
    if (column?.key === 'proportion' || column?.field === 'proportion') {
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
      let total = 0;
      tableData.forEach((item: any) => {
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
  }

  function handleDetailDeleted() {
    nextTick(() => {
      doCalculate();
    });
  }

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
    // let main = Object.assign({}, allValues.formValue);
    // const tableData = allValues.tablesValue[0].tableData || [];
    // let total = 0;
    // tableData.forEach((row: any) => {
    //   total += parseFloat(row.proportion) || 0;
    // });
    // main.proportionTotal = (Math.round(total * 100) / 100).toString();
    // return {
    //   ...main,
    //   recipeDetailList: tableData,
    // };

    let main = Object.assign({}, allValues.formValue);
    const tableData = allValues.tablesValue[0].tableData || [];

    // 处理子表 ID：新增时全部清空；编辑时只保留真实数据库 ID
    const cleanTableData = tableData.map((row: any) => {
      const newRow = { ...row };
      const rowId = newRow.id;

      if (!unref(isUpdate)) {
        // 新增模式：所有子表 ID 都清空，由后端生成雪花 ID
        delete newRow.id;
      } else {
        // 编辑模式：只保留真实数据库 ID，清空前端临时 ID（row_xxx）和空值
        if (!rowId || String(rowId).trim() === '' || String(rowId).startsWith('row_')) {
          delete newRow.id;
        }
      }
      return newRow;
    });

    // 计算配比总和
    let total = 0;
    cleanTableData.forEach((row: any) => {
      total += parseFloat(row.proportion) || 0;
    });
    main.proportionTotal = (Math.round(total * 100) / 100).toString();

    return {
      ...main,
      recipeDetailList: cleanTableData,
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

  /* 主表单行间距（上次加的） */
  :deep(.ant-form-item) {
    margin-bottom: 8px !important;
  }
  :deep(.ant-form-item-label) {
    padding-bottom: 2px;
  }
  /*!* 弹窗 body 压缩 *!*/
  /*:deep(.ant-modal-body) {*/
  /*  padding: 12px 16px !important;*/
  /*}*/

  /*!* 左侧主表面板：固定高度 + 内部滚动 *!*/
  /*.left-panel {*/
  /*  max-height: 660px;*/
  /*  overflow-y: auto;*/
  /*  padding-right: 4px;*/

  /*  !* 滚动条美化 *!*/
  /*  &::-webkit-scrollbar {*/
  /*    width: 4px;*/
  /*  }*/
  /*  &::-webkit-scrollbar-thumb {*/
  /*    background: #d9d9d9;*/
  /*    border-radius: 2px;*/
  /*  }*/

  /*  !* 主表单压缩 *!*/
  /*  :deep(.ant-form-item) {*/
  /*    margin-bottom: 8px !important;*/
  /*  }*/
  /*  :deep(.ant-form-item-label) {*/
  /*    line-height: 28px !important;*/
  /*    height: 28px !important;*/
  /*    padding-bottom: 0 !important;*/
  /*  }*/
  /*  :deep(.ant-input),*/
  /*  :deep(.ant-input-number),*/
  /*  :deep(.ant-select .ant-select-selector) {*/
  /*    min-height: 28px !important;*/
  /*    height: 28px !important;*/
  /*  }*/
  /*  :deep(.ant-input-number) {*/
  /*    width: 100%;*/
  /*  }*/
  /*  !* 文本域高度限制 *!*/
  /*  :deep(.ant-input-textarea) textarea {*/
  /*    min-height: unset !important;*/
  /*  }*/
  /*}*/

  /*!* 右侧子表面板 *!*/
  /*.right-panel {*/
  /*  :deep(.ant-tabs) {*/
  /*    margin-top: -8px;   !* 抵消 tabs 顶部空白 *!*/
  /*  }*/
  /*  :deep(.ant-tabs-content) {*/
  /*    padding-top: 4px;*/
  /*  }*/
  /*}*/
</style>

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
          :height="700"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          @edit-closed="handleEditClosed"
          @removed="handleDetailDeleted"
        >
          <!-- 物料编码：自定义选择插槽 -->
          <template #materialCode="{ row }">
            <a-input
              :value="row.materialCode"
              readonly
              :disabled="formDisabled"
              :placeholder="formDisabled ? '' : '点击选择物料'"
              :style="{ cursor: formDisabled ? 'default' : 'pointer', height: '24px' }"
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

    <!-- 物料选择弹窗 -->
    <MaterialSelectModal @register="registerMaterialModal" @select="handleMaterialSelect" />

  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive, nextTick} from 'vue';
  import {BasicModal, useModal,  useModalInner} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import {formSchema,recipeDetailColumns} from '../Recipe.data';
  import {saveOrUpdate,recipeDetailList} from '../Recipe.api';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
  import { useMessage } from '/@/hooks/web/useMessage';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import MaterialSelectModal from './MaterialSelectModal.vue';

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
    baseColProps: {span: 4},   // ← 一行6列
    labelCol: { span: 8 },     // 标签更窄
    wrapperCol: { span: 16 },  // 输入框更宽
  });

  // ===== 物料选择弹窗 =====
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

  /*!* 弹窗内容区 padding 压缩 *!*/
  /*:deep(.ant-modal-body) {*/
  /*  padding: 10px 16px !important;*/
  /*}*/

  /*!* 主表单全面压缩 *!*/
  /*:deep(.ant-form-item) {*/
  /*  margin-bottom: 6px !important;*/
  /*}*/
  /*:deep(.ant-form-item-label) {*/
  /*  padding-bottom: 0 !important;*/
  /*  line-height: 26px !important;*/
  /*  height: 26px !important;*/
  /*}*/
  /*:deep(.ant-form-item-control) {*/
  /*  line-height: 26px !important;*/
  /*}*/
  /*:deep(.ant-input),*/
  /*:deep(.ant-input-number),*/
  /*:deep(.ant-select .ant-select-selector) {*/
  /*  min-height: 26px !important;*/
  /*  height: 26px !important;*/
  /*}*/
  /*:deep(.ant-input-number) {*/
  /*  width: 100%;*/
  /*}*/
  /*:deep(.ant-calendar-picker) {*/
  /*  width: 100%;*/
  /*}*/
  /*!* 文本域在表单里也要压缩 *!*/
  /*:deep(.ant-input-textarea) textarea {*/
  /*  min-height: unset !important;*/
  /*}*/

  /* 子表行高强制压缩 */
  /*:deep(.vxe-table) {*/
  /*  font-size: 13px !important;*/

  /*  .vxe-header--row {*/
  /*    height: 30px !important;*/
  /*  }*/
  /*  .vxe-header--column {*/
  /*    padding: 2px 0 !important;*/
  /*  }*/

  /*  .vxe-body--row {*/
  /*    height: 30px !important;*/
  /*  }*/
  /*  .vxe-cell {*/
  /*    padding: 1px 4px !important;*/
  /*    line-height: 1.3 !important;*/
  /*    height: 30px !important;*/
  /*  }*/

  /*  !* 行内所有 ant 输入组件强制 24px *!*/
  /*  .ant-input,*/
  /*  .ant-input-number,*/
  /*  .ant-input-number-input,*/
  /*  .ant-input-affix-wrapper,*/
  /*  .ant-select .ant-select-selector,*/
  /*  .ant-select-selection-item,*/
  /*  .ant-select-selection-search-input {*/
  /*    min-height: 24px !important;*/
  /*    height: 24px !important;*/
  /*    line-height: 24px !important;*/
  /*    font-size: 13px !important;*/
  /*  }*/

  /*  !* 数字输入框内部 input *!*/
  /*  .ant-input-number-input-wrap {*/
  /*    height: 24px !important;*/
  /*  }*/
  /*}*/
</style>

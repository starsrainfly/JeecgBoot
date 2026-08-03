<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="StockOutForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="出库明细表" key="stockOutDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="stockOutDetail"
          :loading="stockOutDetailTable.loading"
          :columns="stockOutDetailTable.columns"
          :dataSource="stockOutDetailTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
         >
          <!-- 物料选择：输入框 + 搜索图标触发弹窗 -->
          <template #goodsCode="{ row }">
            <a-input
              :value="row.goodsCode"
              readonly
              :disabled="formDisabled"
              :placeholder="formDisabled ? '' : '点击选择物料'"
              :style="{ cursor: formDisabled ? 'default' : 'pointer' }"
              @click="openMaterialSelect(row)"
            >
              <template #suffix>
                <Icon
                  v-if="!formDisabled"
                  icon="ant-design:search-outlined"
                  style="color: #1890ff; font-size: 14px;"
                />
              </template>
            </a-input>
          </template>
        </JVxeTable>
      </a-tab-pane>
    </a-tabs>
    <!-- 物料选择弹窗（与入库/配料/采购模块共用） -->
    <MaterialSelectModal @register="registerMaterialModal" @select="handleMaterialSelect" />
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref,reactive} from 'vue';
    import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import { JVxeTable } from '/@/components/jeecg/JVxeTable'
    import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
    import {formSchema,stockOutDetailColumns} from '../MaterialOutApprove.data';
    import {saveOrUpdate,stockOutDetailList, approveStockOut} from '../MaterialOutApprove.api';
    import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'

    import MaterialSelectModal from '/@/components/MaterialSelect';
    import { Icon } from '/@/components/Icon';

    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const isAuditMode = ref(false); // 新增：是否为审核模式
    const formDisabled = ref(false);
    const refKeys = ref(['stockOutDetail', ]);
    const activeKey = ref('stockOutDetail');
    const stockOutDetail = ref();
    const tableRefs = {stockOutDetail, };
    const stockOutDetailTable = reactive({
          loading: false,
          dataSource: [],
          columns:stockOutDetailColumns
    })

    // ===== 物料选择弹窗注册 =====
    const [registerMaterialModal, { openModal: openMaterialModal }] = useModal();
    const currentSelectRow = ref<any>(null);
    const currentSelectRowIndex = ref<number>(-1);

    //表单配置
    const [registerForm, {setProps,resetFields, setFieldsValue, validate}] = useForm({
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 6}
    });
     //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //重置表单
        await reset();
        // 新增：判断是否为审核模式
        isAuditMode.value = data?.isAudit === true;

        setModalProps({confirmLoading: false,showCancelBtn:data?.showFooter,showOkBtn:data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        formDisabled.value = !data?.showFooter;
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
             requestSubTableData(stockOutDetailList, {id:data?.record?.id}, stockOutDetailTable)
        }
        // 隐藏底部时禁用整个表单
       setProps({ disabled: !data?.showFooter })
    });
    //方法配置
    const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] =
      useJvxeMethod(handleSubmitMethod,classifyIntoFormData,tableRefs,activeKey,refKeys);


    /**
     * 获取弹窗标题
     */
    function getModalTitle() {
      if (isAuditMode.value) return '入库审核';
      if (!unref(isUpdate)) return '新增';
      if (!unref(formDisabled)) return '编辑';
      return '详情';
    }
    //设置标题
    //const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));
    const title = computed(() => {
      return getModalTitle();
    });

    async function reset(){
      await resetFields();
      activeKey.value = 'stockOutDetail';
      stockOutDetailTable.dataSource = [];
      isAuditMode.value = false;
      submitMethodRef.value = requestAddOrEdit; // 重置提交方法
    }
    function classifyIntoFormData(allValues) {
         let main = Object.assign({}, allValues.formValue)
         return {
           ...main, // 展开
           stockOutDetailList: allValues.tablesValue[0].tableData,
         }
       }
    //表单提交事件
    async function requestAddOrEdit(values) {
        try {
            setModalProps({confirmLoading: true});
            //提交表单
            await saveOrUpdate(values, isUpdate.value);
            //关闭弹窗
            closeModal();
            //刷新列表
            emit('success');
        } finally {
            setModalProps({confirmLoading: false});
        }
    }

    async function requestApprove(values) {

      try {
        setModalProps({confirmLoading: true});
        //提交表单
        await approveStockOut(values);
        //关闭弹窗
        closeModal();
        //刷新列表
        emit('success');
      } finally {
        setModalProps({confirmLoading: false});
      }
    }
    const submitMethodRef = ref(requestAddOrEdit);

    async function handleSubmitMethod(values) {
      if (isAuditMode.value) {
        console.log('执行审核提交');
        return requestApprove(values);
      } else {
        console.log('执行编辑/新增提交');
        return requestAddOrEdit(values);
      }
    }

    // ==================== 物料选择逻辑（与入库/配方/采购保持一致） ====================

    /** 打开物料选择弹窗 */
    function openMaterialSelect(row: any) {
      if (formDisabled.value) return;
      currentSelectRow.value = row;
      // 获取行索引，用于 setValues 的 rowKey（新增行 id 为空时最可靠）
      const tableInstance = stockOutDetail.value;
      if (tableInstance) {
        const allData = tableInstance.getTableData ? tableInstance.getTableData() :
          (tableInstance.getData ? tableInstance.getData() : stockOutDetailTable.dataSource);
        currentSelectRowIndex.value = allData.indexOf(row);
      }
      openMaterialModal(true, {});
    }

    /** 物料选择回调 — 回填当前行 */
    function handleMaterialSelect(record: any) {
      const row = currentSelectRow.value;
      if (!row) return;
      console.log('[StockOutModal] handleMaterialSelect record:', record);

      // 1. 直接更新行对象（确保隐藏字段 goodsId 被赋值）
      row.goodsId   = record.id;
      row.goodsCode = record.material_code || record.code || record.materialCode;
      row.goodsName = record.material_name || record.name || record.materialName;
      row.goodsSpec = record.material_spec || record.spec || record.materialSpec;
      row.goodsType = record.material_type || record.type || record.materialType;

      // 2. 通过 setValues 刷新可见列（使用索引作为 rowKey）
      const tableInstance = stockOutDetail.value;
      if (tableInstance && currentSelectRowIndex.value >= 0) {
        tableInstance.setValues([{
          rowKey: currentSelectRowIndex.value,
          values: {
            goodsCode: row.goodsCode,
            goodsName: row.goodsName,
            goodsSpec: row.goodsSpec,
            goodsType: row.goodsType,
          }
        }]);
      }

      // 3. 兜底：强制刷新 dataSource，确保 JVxeTable 内部状态同步
      if (currentSelectRowIndex.value >= 0) {
        stockOutDetailTable.dataSource[currentSelectRowIndex.value] = { ...row };
        stockOutDetailTable.dataSource = [...stockOutDetailTable.dataSource];
      }

      currentSelectRow.value = null;
      currentSelectRowIndex.value = -1;
    }

</script>

<style lang="less" scoped>
	/** 时间和数字输入框样式 */
  :deep(.ant-input-number) {
    width: 100%;
  }

  :deep(.ant-calendar-picker) {
    width: 100%;
  }
</style>

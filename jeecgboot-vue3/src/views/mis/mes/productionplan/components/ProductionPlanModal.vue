<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1024" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="ProductionPlanForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="生产计划明细表" key="productionPlanDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="productionPlanDetail"
          :loading="productionPlanDetailTable.loading"
          :columns="productionPlanDetailTable.columns"
          :dataSource="productionPlanDetailTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          @value-change="handleDetailValueChange"
          />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref,reactive, nextTick} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import { JVxeTable } from '/@/components/jeecg/JVxeTable'
    import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
    import {formSchema,productionPlanDetailColumns} from '../ProductionPlan.data';
    import {saveOrUpdate,productionPlanDetailList} from '../ProductionPlan.api';
    import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
    import {useMessage} from "@/hooks/web/useMessage";

    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const formDisabled = ref(false);
    const refKeys = ref(['productionPlanDetail', ]);
    const activeKey = ref('productionPlanDetail');
    const productionPlanDetail = ref();
    const tableRefs = {productionPlanDetail, };
    const productionPlanDetailTable = reactive({
          loading: false,
          dataSource: [],
          columns:productionPlanDetailColumns
    })
    //表单配置
    const [registerForm, {setProps,resetFields, setFieldsValue,getFieldsValue, validate}] = useForm({
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 8}
    });
     //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //重置表单
        await reset();
        setModalProps({confirmLoading: false,showCancelBtn:data?.showFooter,showOkBtn:data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        formDisabled.value = !data?.showFooter;
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
             requestSubTableData(productionPlanDetailList, {id:data?.record?.id}, productionPlanDetailTable)
        }
        // 隐藏底部时禁用整个表单
       setProps({ disabled: !data?.showFooter })
    });
    //方法配置
    const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] =
      useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys);

    //设置标题
    const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

    const { createMessage, createWarningModal } = useMessage();

    function getCurrentPlanType(): string {
      try {
        const formData = getFieldsValue();
        return formData?.planType || '0';
      } catch (e) {
        return '0';
      }
    }

    async function getCurrentPlannedQty(): Promise<number> {
      const formData = await getFieldsValue();
      const qty = formData?.plannedQty;
      return Number(qty) || 0;
    }

    // ==================== 核心校验逻辑 ====================

    async function validateDetailAllocatedQty(): Promise<{ valid: boolean; message?: string }> {
      const detailTable = productionPlanDetail.value;
      if (!detailTable) return { valid: true };

      const plannedQty = await getCurrentPlannedQty();
      const planType = getCurrentPlanType();
      const rows = detailTable.getTableData() || [];
      let totalAllocated = 0;
      const errors: string[] = [];

      for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        const allocatedQty = Number(row.allocatedQty) || 0;
        // 【修改】demandQty → orderQty
        const orderQty = Number(row.orderQty) || 0;
        const rowNo = i + 1;

        // 规则1：销售订单类型时，分配数量 <= 订单数量
        if (planType === '0' && allocatedQty > orderQty) {
          errors.push(`第${rowNo}行：分配数量(${allocatedQty})不能大于订单数量(${orderQty})`);
        }

        totalAllocated += allocatedQty;
      }

      // 规则2：总和 <= 计划数量
      if (totalAllocated > plannedQty) {
        errors.push(`明细分配数量总和(${totalAllocated})不能大于计划数量(${plannedQty})`);
      }

      if (errors.length > 0) {
        return { valid: false, message: errors.join('\n') };
      }
      return { valid: true };
    }

    function validateDetailAllocatedQtySync(plannedQty: number): { valid: boolean; message?: string } {
      const detailTable = productionPlanDetail.value;
      if (!detailTable) return { valid: true };

      const planType = getCurrentPlanType();
      const rows = detailTable.getTableData() || [];
      let totalAllocated = 0;
      const errors: string[] = [];

      for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        const allocatedQty = Number(row.allocatedQty) || 0;
        // 【修改】demandQty → orderQty
        const orderQty = Number(row.orderQty) || 0;
        const rowNo = i + 1;

        if (planType === '0' && allocatedQty > orderQty) {
          errors.push(`第${rowNo}行：分配数量(${allocatedQty})不能大于订单数量(${orderQty})`);
        }

        totalAllocated += allocatedQty;
      }

      if (totalAllocated > plannedQty) {
        errors.push(`明细分配数量总和(${totalAllocated})不能大于计划数量(${plannedQty})`);
      }

      if (errors.length > 0) {
        return { valid: false, message: errors.join('\n') };
      }
      return { valid: true };
    }

    async function validateSubForm(): Promise<boolean> {
      const result = await validateDetailAllocatedQty();
      if (!result.valid) {
        createWarningModal({
          title: '数据校验失败',
          content: result.message,
        });
        return false;
      }
      return true;
    }

    async function handleDetailValueChange({ row, column }) {
      if (column.property === 'allocatedQty') {
        await nextTick();
        const plannedQty = await getCurrentPlannedQty();
        const result = validateDetailAllocatedQtySync(plannedQty);
        if (!result.valid) {
          createMessage.warning(result.message);
        }
      }
    }

    async function reset(){
      await resetFields();
      activeKey.value = 'productionPlanDetail';
      productionPlanDetailTable.dataSource = [];
    }
    function classifyIntoFormData(allValues) {
         let main = Object.assign({}, allValues.formValue)
         return {
           ...main, // 展开
           productionPlanDetailList: allValues.tablesValue[0].tableData,
         }
       }
    //表单提交事件
    async function requestAddOrEdit(values) {
        try {
          // 提交前最终校验
          const result = await validateDetailAllocatedQty();
          if (!result.valid) {
            createWarningModal({
              title: '提交失败',
              content: result.message,
            });
            return Promise.reject(result.message);
          }
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

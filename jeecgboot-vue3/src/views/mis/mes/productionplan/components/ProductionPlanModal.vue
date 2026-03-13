<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1024" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="ProductionPlanForm"  />
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
    import {ref, computed, unref,reactive, nextTick,onUnmounted} from 'vue';
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
    });

    const { createMessage, createWarningModal } = useMessage();

    //表单配置 - 关键：添加 onValuesChange 实时监听
    const [registerForm, {setProps,resetFields, setFieldsValue, validate, getFieldsValue}] = useForm({
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 8},

    });

    function getCurrentPlanType(): string {
      try {
        const formData = getFieldsValue();
        return formData?.planType || '0';
      } catch (e) {
        return '0';
      }
    }

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
    const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] = useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys,validateSubForm);

    //设置标题
    const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

    async function reset(){
      await resetFields();
      activeKey.value = 'productionPlanDetail';
      productionPlanDetailTable.dataSource = [];
    }
    function classifyIntoFormData(allValues) {
      let main = Object.assign({}, allValues.formValue || {});

      // 1. 尝试从 allValues 获取 (标准流程)
      let subTableData = allValues?.tablesValue?.[0]?.tableData;

      // 2. 【强力修复】如果标准流程拿不到，尝试直接从 ref 实例获取
      if (!subTableData || subTableData.length === 0) {
        console.warn('⚠️ useJvxeMethod 未收集到子表数据，尝试从 Ref 实例直接获取...');

        const tableInstance = unref(productionPlanDetail);
        if (tableInstance) {
          // 尝试多种可能的获取数据方法
          const getData = tableInstance.getTableData || tableInstance.getData || tableInstance.getDataSource;

          if (getData && typeof getData === 'function') {
            const directData = getData.call(tableInstance);
            // VXE Table getTableData 通常返回 { fullData: [...] } 或直接返回数组，视版本而定
            // Jeecg JVxeTable 通常 getTableData() 返回的是包含数据的对象或直接是数组
            if (Array.isArray(directData)) {
              subTableData = directData;
            } else if (directData && Array.isArray(directData.fullData)) {
              subTableData = directData.fullData;
            } else if (directData && Array.isArray(directData.records)) {
              subTableData = directData.records;
            }

            console.log('✅ 从 Ref 实例成功获取数据，长度:', subTableData.length);
          } else {
            console.error('❌ 表格实例没有发现获取数据的方法 (getTableData/getData)');
          }
        } else {
          console.error('❌ 表格实例 productionPlanDetail 仍然为 undefined');
        }
      }

      // 3. 最终兜底
      subTableData = subTableData || [];

      return {
        ...main,
        productionPlanDetailList: subTableData,
      };
    }

    // ==================== 核心校验逻辑 - 关键修正 ====================

    /**
     * 获取当前最新的计划数量（实时从表单获取）
     */
    async function getCurrentPlannedQty(): Promise<number> {
      const formData = await getFieldsValue();
      const qty = formData?.plannedQty;
      return Number(qty) || 0;
    }

    /**
     * 校验明细行的分配数量 - 实时获取最新值
     */
    async function validateDetailAllocatedQty(): Promise<{ valid: boolean; message?: string }> {
      const detailTable = productionPlanDetail.value;
      if (!detailTable) return { valid: true };

      // 关键：实时从表单获取最新的计划数量，而不是用缓存值
      const plannedQty = await getCurrentPlannedQty();
      const planType = getCurrentPlanType();

      const rows = detailTable.getTableData() || [];
      let totalAllocated = 0;
      const errors: string[] = [];

      for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        const allocatedQty = Number(row.allocatedQty) || 0;
        const demandQty = Number(row.demandQty) || 0;
        const rowNo = i + 1;

        // 规则1：销售订单类型时，分配数量 <= 需求数量
        if (planType === '0' && allocatedQty > demandQty) {
          errors.push(`第${rowNo}行：分配数量(${allocatedQty})不能大于需求数量(${demandQty})`);
        }

        totalAllocated += allocatedQty;
      }

      // 规则2：总和 <= 计划数量（使用实时获取的值）
      if (totalAllocated > plannedQty) {
        errors.push(`明细分配数量总和(${totalAllocated})不能大于计划数量(${plannedQty})`);
      }

      if (errors.length > 0) {
        return { valid: false, message: errors.join('\n') };
      }

      return { valid: true };
    }

    /**
     * 同步校验版本（用于非 async 场景）
     */
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
        const demandQty = Number(row.demandQty) || 0;
        const rowNo = i + 1;

        if (planType === '0' && allocatedQty > demandQty) {
          errors.push(`第${rowNo}行：分配数量(${allocatedQty})不能大于需求数量(${demandQty})`);
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

    /**
     * 子表自定义校验（提交前调用）
     */
    async function validateSubForm(): Promise<boolean> {
      const result = await validateDetailAllocatedQty();
      if (!result.valid) {
        createWarningModal({
          title: '数据校验失败',
          content: result.message,
        });
        return Promise.reject(result.message);
      }
      return Promise.resolve(true);
    }

    /**
     * 明细表数值变化监听 - 关键修正：实时获取表单值
     */
    async function handleDetailValueChange({ row, column }) {
      if (column.property === 'allocatedQty') {
        // 关键：等待 DOM 更新后，实时获取表单最新值
        await nextTick();
        const plannedQty = await getCurrentPlannedQty();

        // 使用同步版本进行校验，传入实时获取的计划数量
        const result = validateDetailAllocatedQtySync(plannedQty);
        if (!result.valid) {
          createMessage.warning(result.message);
        }
      }
    }


    //表单提交事件
    async function requestAddOrEdit(values) {
        try {
          // 最终校验 - 实时获取最新值
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

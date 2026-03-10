<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1024" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="ProductionOrderForm"/>
    <!-- 选择计划明细按钮 -->
    <div style="margin: 10px 0;">
      <a-button type="primary" @click="handleSelectPlan" preIcon="ant-design:search-outlined">
        选择计划明细
      </a-button>
<!--      <a-tag v-if="selectedPlanDetails.length > 0" color="blue" style="margin-left: 10px;">-->
<!--        已选择 {{ selectedPlanDetails.length }} 条明细-->
<!--      </a-tag>-->
      <a-tag v-if="productionOrderDetailTable.dataSource.length > 0" color="blue" style="margin-left: 10px;">
        共 {{ productionOrderDetailTable.dataSource.length }} 条明细
        <span v-if="lastAddedCount > 0" style="color: #52c41a;">
        （本次新增 {{ lastAddedCount }} 条）
      </span>
      </a-tag>
    </div>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="生产订单明细" key="productionOrderDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="productionOrderDetail"
          :loading="productionOrderDetailTable.loading"
          :columns="productionOrderDetailTable.columns"
          :dataSource="productionOrderDetailTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          @value-change="handleDetailChange"
        />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
  <!-- 选择计划明细弹窗 -->
  <SelectPlanDetailModal
    @register="registerSelectModal"
    @success="onSelectPlanSuccess"
  />
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive, nextTick, onUnmounted} from 'vue';
  import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable'
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
  import {formSchema,productionOrderDetailColumns} from '../ProductionOrder.data';
  import {saveOrUpdate,productionOrderDetailList, getPackageMapping} from '../ProductionOrder.api';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'

  import { useMessage } from '/@/hooks/web/useMessage';
  import SelectPlanDetailModal from './SelectPlanDetailModal.vue';

  const { createMessage, createWarningModal } = useMessage();

  // 注册选择弹窗
  const [registerSelectModal, { openModal: openSelectModal }] = useModal();

  // 当前选中的计划明细
  const selectedPlanDetails = ref<any[]>([]);

  // Emits声明
  const emit = defineEmits(['register','success']);
  const isUpdate = ref(true);
  const formDisabled = ref(false);
  const refKeys = ref(['productionOrderDetail', ]);
  const activeKey = ref('productionOrderDetail');
  const productionOrderDetail = ref();
  const tableRefs = {productionOrderDetail, };
  const productionOrderDetailTable = reactive({
    loading: false,
    dataSource: [],
    columns:productionOrderDetailColumns
  })

  // ===== 关键：用于监听表单状态的变量 =====
  const lastOuterPackageId = ref<string | null>(null);
  const formWatchTimer = ref<any>(null);
  const isCalculating = ref(false);

  //表单配置
  const [registerForm, {setProps,resetFields, setFieldsValue, validate,getFieldsValue}] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 8},
  });

  // ===== 关键：启动表单监听 =====
  function startFormWatch() {
    // 先停止之前的监听
    stopFormWatch();

    console.log('启动表单字段监听...');

    // 延迟启动，确保表单完全初始化
    setTimeout(() => {
      console.log('开始监听 outerPackageId 变化...');

      formWatchTimer.value = setInterval(() => {
        try {
          const formData = getFieldsValue();
          const currentOuterPackageId = formData?.outerPackageId;

          // 有值且与上次不同，触发计算
          // BUG修复：这里原来是 outerPackageId.value，应该是 lastOuterPackageId.value
          if (currentOuterPackageId && currentOuterPackageId !== lastOuterPackageId.value) {
            console.log('检测到外包装变化:', lastOuterPackageId.value, '->', currentOuterPackageId);
            lastOuterPackageId.value = currentOuterPackageId;

            // 延迟执行，避免频繁变更
            nextTick(() => {
              handleOuterPackageChange(currentOuterPackageId);
            });
          }
        } catch (e) {
          // 表单可能还未初始化，忽略错误
          console.log('监听检查失败:', e);
        }
      }, 500); // 每500ms检查一次
    }, 1000); // 延迟1秒启动，确保表单已渲染
  }

  // ===== 关键：停止表单监听 =====
  function stopFormWatch() {
    if (formWatchTimer.value) {
      clearInterval(formWatchTimer.value);
      formWatchTimer.value = null;
      console.log('停止表单字段监听');
    }
  }

  // 组件卸载时清理
  onUnmounted(() => {
    stopFormWatch();
  });

  // 打开选择计划弹窗
  function handleSelectPlan() {
    // 如果已有选择，传递当前产品作为限制
    const currentProduct = getFieldsValue().productId;
    // 关键：收集已选择的计划明细 ID
    const alreadySelectedIds = productionOrderDetailTable.dataSource
      .map(row => row.planDetailId)
      .filter(Boolean); // 过滤掉空值

    console.log('已选择的计划明细 ID:', alreadySelectedIds);
    openSelectModal(true, {
      restrictProductId: currentProduct,  // 限制同一产品
      restrictPackageId: getFieldsValue().innerPackageId, // 限制同一包装
      alreadySelectedIds: alreadySelectedIds, // 传给弹窗用于禁用
    });
  }
  // 添加：记录上次新增数量
  const lastAddedCount = ref(0);
  // 选择计划明细成功回调
  function onSelectPlanSuccess(selectedRows: any[]) {
    if (!selectedRows || selectedRows.length === 0) return;

    selectedPlanDetails.value = selectedRows;
    console.log('接收到的完整数据:', selectedRows);
    console.log('planId:', selectedRows[0]?.planId);           // ✅ 计划主表 ID
    console.log('planDetailId:', selectedRows[0]?.id); // ✅ 计划明细 ID

    console.log('新选择的行:', selectedRows);

    // 1. 过滤掉已存在的（根据 sourcePlanDetailId 去重）
    const existingIds = new Set(
      productionOrderDetailTable.dataSource.map(row => row.sourcePlanDetailId)
    );

    const newRows = selectedRows.filter(row => {
      const isDuplicate = existingIds.has(row.planDetailId);
      if (isDuplicate) {
        console.log('过滤重复:', row.planNo, row.planDetailId);
      }
      return !isDuplicate;
    });
// 关键修改：更新提示标签为实际新增数量
    const addedCount = newRows.length;
    const duplicateCount = selectedRows.length - newRows.length;

    if (newRows.length === 0) {
      createMessage.warning('所选明细已全部存在，未添加重复数据');
      // 更新提示为实际数量（0条）
      selectedPlanDetails.value = [...productionOrderDetailTable.dataSource]; // 保持原有
      return;
    }

    if (duplicateCount > 0) {
      createMessage.warning(`已过滤 ${duplicateCount} 条重复明细，实际新增 ${addedCount} 条`);
    }
    // if (newRows.length < selectedRows.length) {
    //   createMessage.warning(`已过滤 ${selectedRows.length - newRows.length} 条重复明细，新增 ${newRows.length} 条`);
    // }

    // 2. 追加到现有数据（保留用户已修改的数据）
    const startIndex = productionOrderDetailTable.dataSource.length;

    const formattedRows = newRows.map((row, index) => ({
      id: `${Date.now()}_${startIndex + index}`,  // 临时ID
      planNo: row.planNo,
      planType: row.planType,
      planTypeName: row.planTypeName,

      // 关键：保存关联 ID
      sourcePlanId: row.planId,
      sourcePlanDetailId: row.planDetailId,

      // 销售订单来源
      salesOrderNo: row.salesOrderNo || '-',
      salesOrderId: row.salesOrderId,
      salesOrderLineId: row.salesOrderDetailId,

      // 客户
      customerCode: row.customerCode || '-',
      customerName: row.customerName || '-',
      customerId: row.customerId,

      // 数量（关键：使用剩余可分配数量）
      planAllocatedQty: row.remainingQty,
      allocatedQty: row.remainingQty,

      // 其他
      deliverDate: row.deliveryDate,
      priorityLevel: '3',
      remark: row.planRemark || '',

      sortNo: startIndex + index + 1
    }));

    // 追加到现有数据
    productionOrderDetailTable.dataSource = [
      ...productionOrderDetailTable.dataSource,
      ...formattedRows
    ];
    // 关键修改：更新提示标签为实际总数量
    selectedPlanDetails.value = productionOrderDetailTable.dataSource;
    // 3. 更新主表汇总数据
    updateMainFormValues();

    createMessage.success(`成功添加 ${formattedRows.length} 条明细`);

  }
  // 提取：更新主表汇总数据
  function updateMainFormValues() {
    const allRows = productionOrderDetailTable.dataSource;

    if (allRows.length === 0) {
      // 清空主表
      setFieldsValue({
        productId: undefined,
        productCode: undefined,
        productName: undefined,
        innerPackageId: undefined,
        innerPackageName: undefined,
        plannedQty: 0,
        deliveryDate: undefined,
      });
      selectedPlanDetails.value = [];
      return;
    }

    // 重新计算（以第一行为基准，理论上应该一致）
    const firstRow = allRows[0];
    const totalQty = allRows.reduce((sum, row) => sum + Number(row.allocatedQty || 0), 0);

    // 取最早的交期
    const earliestDate = allRows
      .map(r => r.deliverDate)
      .filter(Boolean)
      .sort()[0];

    setFieldsValue({
      productId: firstRow.productId, // 注意：这里需要从某处获取，可能需要在 formattedRows 中保留
      plannedQty: totalQty,
      deliveryDate: earliestDate,
    });

    createMessage.success(`订单计划数量已更新：${totalQty} kg，共 ${allRows.length} 条明细`);
  }
  // 监听明细变化，同步更新主表数量
  function handleDetailChange({ row, column }) {
    if (column.key === 'allocatedQty') {
      // 校验：本次执行 ≤ 原始数量
      const original = Number(row.planAllocatedQty) || 0;
      const current = Number(row.allocatedQty) || 0;

      if (current > original) {
        createMessage.warning(`本次执行数量(${current})不能大于计划数量(${original})`);
        // 重置为原始数量
        row.allocatedQty = original;
        return;
      }

      // 关键：自动重新计算主表总数量
      nextTick(() => {
        recalculateTotalQty();
      });
    }
  }

  // 重新计算主表总数量
  function recalculateTotalQty() {
    const rows = productionOrderDetailTable.dataSource;
    const total = rows.reduce((sum, row) => sum + (Number(row.allocatedQty) || 0), 0);

    setFieldsValue({
      plannedQty: total
    });

    createMessage.success(`订单计划数量已更新：${total} kg`);
  }

  // 外包装变化，自动计算包装数量
  async function handleOuterPackageChange(outerPackageId: string) {
    if (!outerPackageId || isCalculating.value) return;

    isCalculating.value = true;
    console.log('开始处理外包装变化:', outerPackageId);

    try {
      const formData = getFieldsValue();
      const innerPackageId = formData.innerPackageId;
      const totalQty = formData.plannedQty;
      const innerCapacity = formData.innerPackageCapacity || 10; // 默认10kg

      console.log('当前表单数据:', { innerPackageId, totalQty, innerCapacity });

      if (!innerPackageId) {
        createMessage.warning('请先选择内包装（通过"选择计划明细"按钮选择）');
        return;
      }

      if (!totalQty || totalQty <= 0) {
        createMessage.warning('计划数量必须大于0');
        return;
      }

      console.log('调用 getPackageMapping API:', { innerPackageId, outerPackageId });

      // 查询包装映射关系
      const mapping = await getPackageMapping({
        innerPackageId,
        outerPackageId
      });

      console.log('API 返回结果:', mapping);

      if (!mapping) {
        createMessage.warning('未找到该内外包装的映射关系，请先维护包装映射');
        return;
      }

      const innerPerOuter = mapping?.innerPerOuter || 1;  // 一箱几个内包

      // 计算
      const innerPackageQty = Math.ceil(totalQty / innerCapacity);
      const outerPackageQty = Math.ceil(innerPackageQty / innerPerOuter);

      console.log('计算结果:', { innerPackageQty, outerPackageQty, innerPerOuter });

      // 自动填充表单
      setFieldsValue({
        innerPackageQty: innerPackageQty,
        outerPackageQty: outerPackageQty,
        outerInnerPerOuter: innerPerOuter
      });

      createMessage.success(`包装数量已自动计算：内包${innerPackageQty}个，外包${outerPackageQty}个（每箱${innerPerOuter}个内包）`);
    } catch (error: any) {
      console.error('查询包装映射失败', error);
      createMessage.error('计算包装数量失败：' + (error.message || '网络错误'));
    } finally {
      isCalculating.value = false;
    }
  }

  // 提交前校验
  async function validateBeforeSubmit() {
    const rows = productionOrderDetailTable.dataSource;

    // 校验1：必须有明细
    if (rows.length === 0) {
      createWarningModal({ title: '校验失败', content: '请选择计划明细' });
      return false;
    }

    // 校验2：本次执行数量必须大于0且不超过原始
    for (let i = 0; i < rows.length; i++) {
      const row = rows[i];
      const original = Number(row.planAllocatedQty) || 0;
      const current = Number(row.allocatedQty) || 0;

      if (current <= 0) {
        createWarningModal({
          title: '校验失败',
          content: `第${i + 1}行：本次执行数量必须大于0`
        });
        return false;
      }
      if (current > original) {
        createWarningModal({
          title: '校验失败',
          content: `第${i + 1}行：本次执行数量(${current})不能大于计划数量(${original})`
        });
        return false;
      }
    }

    // 校验3：主表数量必须等于明细汇总
    const formData = getFieldsValue();
    const totalPlanned = Number(formData.plannedQty) || 0;
    const totalDetail = rows.reduce((sum, r) => sum + (Number(r.allocatedQty) || 0), 0);

    if (Math.abs(totalPlanned - totalDetail) > 0.0001) {
      createWarningModal({
        title: '校验失败',
        content: `订单计划数量(${totalPlanned})与明细汇总(${totalDetail})不一致，请检查`
      });
      return false;
    }

    return true;
  }

  //表单赋值
  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    //重置表单
    await reset();
    setModalProps({confirmLoading: false,showCancelBtn:data?.showFooter,showOkBtn:data?.showFooter});
    isUpdate.value = !!data?.isUpdate;
    formDisabled.value = !data?.showFooter;

    // 重置监听状态
    lastOuterPackageId.value = null;

    if (unref(isUpdate)) {
      //表单赋值
      await setFieldsValue({
        ...data.record,
      });

      // 编辑模式：如果有外包装，记录当前值避免重复触发
      if (data.record?.outerPackageId) {
        lastOuterPackageId.value = data.record.outerPackageId;
      }

      requestSubTableData(productionOrderDetailList, {id:data?.record?.id}, productionOrderDetailTable)
    }

    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });

    // 关键：只有在启用编辑模式（showFooter=true）时才启动监听
    if (data?.showFooter) {
      console.log('准备启动表单监听，data.showFooter=', data.showFooter);
      nextTick(() => {
        startFormWatch();
      });
    } else {
      console.log('未启动监听，data.showFooter=', data?.showFooter);
    }
  });

  //方法配置
  const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] = useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys);

  //设置标题
  const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

  async function reset(){
    await resetFields();
    activeKey.value = 'productionOrderDetail';
    productionOrderDetailTable.dataSource = [];
    // 重置监听状态
    lastOuterPackageId.value = null;
    // 停止监听
    stopFormWatch();
  }

  function classifyIntoFormData(allValues) {
    let main = Object.assign({}, allValues.formValue)
    return {
      ...main, // 展开
      productionOrderDetailList: allValues.tablesValue[0].tableData,
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

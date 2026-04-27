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
        >
        </JVxeTable>
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

  // ===== 关键：产量计算相关变量 =====
  // 用于防止循环触发的标志
  const isUpdatingQty = ref(false);
  // 记录最后修改的字段，用于决定计算策略
  const triggerSource  = ref<'plannedQty' | 'batchSize' | 'batchCount' | null>(null);


  //表单配置
  const [registerForm, {setProps,resetFields, setFieldsValue, validate,getFieldsValue}] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 8},
  });

  // ===== 关键：启动表单监听 =====
  // function startFormWatch() {
  //   // 先停止之前的监听
  //   stopFormWatch();
  //
  //   console.log('启动表单字段监听...');
  //
  //   // 延迟启动，确保表单完全初始化
  //   setTimeout(() => {
  //     console.log('开始监听 outerPackageId 变化...');
  //
  //     formWatchTimer.value = setInterval(() => {
  //       try {
  //         const formData = getFieldsValue();
  //         const currentOuterPackageId = formData?.outerPackageId;
  //
  //         // 1、监听外包装变化 有值且与上次不同，触发计算
  //
  //         if (currentOuterPackageId && currentOuterPackageId !== lastOuterPackageId.value) {
  //           console.log('检测到外包装变化:', lastOuterPackageId.value, '->', currentOuterPackageId);
  //           lastOuterPackageId.value = currentOuterPackageId;
  //
  //           // 延迟执行，避免频繁变更
  //           nextTick(() => {
  //             handleOuterPackageChange(currentOuterPackageId);
  //           });
  //         }
  //
  //         // 2. 监听产量相关字段变化（新增）
  //         handleQtyFieldsChange(formData);
  //
  //       } catch (e) {
  //         // 表单可能还未初始化，忽略错误
  //         console.log('监听检查失败:', e);
  //       }
  //     }, 500); // 每500ms检查一次
  //   }, 1000); // 延迟1秒启动，确保表单已渲染
  // }

  // ===== 新增：处理产量字段变化 =====
  function handleQtyFieldsChange(formData: any) {
    if (isUpdatingQty.value) return; // 防止循环

    const plannedQty = formData?.plannedQty;
    const batchSize = formData?.batchSize;
    const batchCount = formData?.batchCount;

    // 检测哪个字段发生了变化
    // 使用一个简单的方式：比较当前值和上次记录的值
    // 这里我们用另一种方式：通过 onValuesChange 来捕获变化

    // 这个函数只处理非空值的计算逻辑
    // 实际的变化检测在下面的 watch 中处理
  }

  // ===== 关键：使用 watch 监听表单值变化（更可靠）=====
  const programSettingFields = ref<Set<string>>(new Set());
  // 存储上次的表单值用于比较
  const prevFormValues = ref({
    plannedQty: null as number | null,
    batchSize: null as number | null,
    batchCount: null as number | null,
  });
  // 在 registerModal 中初始化 watch
  let stopQtyWatch: (() => void) | null = null;

  function startQtyWatch() {
    // 停止之前的监听
    if (stopQtyWatch) {
      stopQtyWatch();
    }

    // 使用定时轮询方式监听（因为 useForm 不提供直接的 watch）
    const timer = setInterval(() => {
      try {
        // 如果正在更新中，跳过检测
        if (isUpdatingQty.value) return;

        const formData = getFieldsValue();
        const current = {
          plannedQty: formData?.plannedQty ?? null,
          batchSize: formData?.batchSize ?? null,
          batchCount: formData?.batchCount ?? null,
        };

        // 检测变化
        // 检测哪个字段发生了变化（用户主动修改的）
        let changedField: 'plannedQty' | 'batchSize' | 'batchCount' | null = null;


        // 检查变化，且不是程序设置的
        if (
          current.plannedQty !== prevFormValues.value.plannedQty &&
          !programSettingFields.value.has('plannedQty')
        ) {
          changedField = 'plannedQty';
        } else if (
          current.batchSize !== prevFormValues.value.batchSize &&
          !programSettingFields.value.has('batchSize')
        ) {
          changedField = 'batchSize';
        } else if (
          current.batchCount !== prevFormValues.value.batchCount &&
          !programSettingFields.value.has('batchCount')
        ) {
          changedField = 'batchCount';
        }

        // 如果有变化，执行对应的计算逻辑
        if (changedField) {
          console.log('检测到变化:', changedField, '值从', prevFormValues.value, '变为', current);

          // 记录触发源和当前值
          triggerSource.value = changedField;
          prevFormValues.value = { ...current };

          // 根据触发源执行对应的计算
          executeCalculation(changedField, current);
        }
        else {
          // 同步 prev 值（避免程序设置后残留差异）
          prevFormValues.value = { ...current };
        }

      } catch (e) {
        // 忽略错误
      }
    }, 200);

    stopQtyWatch = () => clearInterval(timer);
  }

  // === MODIFIED === 替换 executeCalculation
  function executeCalculation(
    source: 'plannedQty' | 'batchSize' | 'batchCount',
    values: { plannedQty: number | null; batchSize: number | null; batchCount: number | null }
  ) {
    const { plannedQty, batchSize, batchCount } = values;
    if (!plannedQty || plannedQty <= 0) return;

    // 清空上一次标记
    programSettingFields.value.clear();

    try {
      switch (source) {
        case 'plannedQty':
          if (batchSize && batchSize > 0) {
            const newBatchCount = Math.ceil(plannedQty / batchSize);
            if (newBatchCount !== batchCount) {
              programSettingFields.value.add('batchCount');
              setFieldsValue({ batchCount: newBatchCount });
            }
          }
          break;

        case 'batchSize':
          if (batchSize && batchSize > 0) {
            const newBatchCount = Math.ceil(plannedQty / batchSize);
            if (newBatchCount !== batchCount) {
              programSettingFields.value.add('batchCount'); // 关键：标记是程序设置
              setFieldsValue({ batchCount: newBatchCount });
            }
          }
          break;

        case 'batchCount':
          if (batchCount && batchCount > 0) {
            const newBatchSize = Math.ceil(plannedQty / batchCount);
            if (newBatchSize !== batchSize) {
              programSettingFields.value.add('batchSize'); // 关键：标记是程序设置
              setFieldsValue({ batchSize: newBatchSize });
            }
          }
          break;
      }
    } finally {
      // 延迟清除标记
      setTimeout(() => {
        programSettingFields.value.clear();
      }, 300);
    }
  }

  // ===== 关键：停止表单监听 =====
  function stopFormWatch() {
    if (formWatchTimer.value) {
      clearInterval(formWatchTimer.value);
      formWatchTimer.value = null;
      console.log('停止表单字段监听');
    }
    if (stopQtyWatch) {
      stopQtyWatch();
      stopQtyWatch = null;
    }
  }

  // 新增：重新计算包装数量
  function recalculatePackageQty(row) {
    const allocatedQty = Number(row.allocatedQty) || 0;
    const innerPackageCapacity = Number(row.innerPackageCapacity) || 1;
    const innerPerOuter = Number(row.innerPerOuter) || 1;

    if (allocatedQty <= 0 || innerPackageCapacity <= 0) {
      console.warn('参数不足，无法计算:', { allocatedQty, innerPackageCapacity });
      return;
    }

    // 计算内包数量 向上取整
    const innerPackageQty = Math.ceil(allocatedQty / innerPackageCapacity);
    // 计算外包数量
    const outerPackageQty = Math.ceil(innerPackageQty / innerPerOuter);

    // 使用 setValues 更新当前行 [^20^]
    productionOrderDetail.value.setValues([{
      rowKey: row.id,
      values: {
        innerPackageQty: innerPackageQty,
        outerPackageQty: outerPackageQty
      }
    }]);
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
      // restrictPackageId: getFieldsValue().innerPackageId, // 限制同一包装
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
    console.log('当前 dataSource:', productionOrderDetailTable.dataSource.map(r => ({ planDetailId: r.planDetailId, planNo: r.planNo })));
    // 1. 过滤掉已存在的（根据 planDetailId 去重）
    const existingIds = new Set(
      productionOrderDetailTable.dataSource
        .filter(row => row && row.planDetailId)  // 过滤空值
        .map(row => row.planDetailId)
    );

    console.log('已存在的 planDetailIds:', Array.from(existingIds));

    // 关键修复：selectedRows 中的 id 字段对应 planDetailId
    const newRows = selectedRows.filter(row => {
      const rowId = String(row.id);  // ← 关键：用 row.id，不是 row.planDetailId
      const isDuplicate = existingIds.has(rowId);
      if (isDuplicate) {
        console.log('过滤重复:', row.planNo, rowId);
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
      id: null,  // 临时ID
      planNo: row.planNo,
      planType: row.planType,

      // 关键：保存关联 ID
      planId: row.planId,
      planDetailId: row.id,

      // 销售订单来源
      salesOrderNo: row.salesOrderNo || '-',
      salesOrderId: row.salesOrderId,
      salesOrderLineId: row.salesOrderDetailId,

      productId: row.productId,
      productCode: row.productCode,
      productName: row.productName,
      productColor: row.productColor,
      // 客户
      customerCode: row.customerCode || '-',
      customerName: row.customerName || '-',
      customerId: row.customerId,

      //包装
      innerPackageId: row.packageId,
      innerPackageCapacity: row.packageCapacity,
      innerPackageSpec: row.packageSpec,

      // 数量（关键：使用剩余可分配数量）
      planAllocatedQty: row.remainingQty,
      allocatedQty: row.remainingQty,
      innerPackageQty: Math.ceil(row.remainingQty / row.packageCapacity),

      innerPackageUnit:'个',
      outerPackageUnit:'个',
      // 其他
      deliverDate: row.deliveryDate,
      priorityLevel: '3',
      remark: row.planRemark || '',
      plannedStartDate: row.plannedStartDate,
      plannedEndDate: row.plannedEndDate,

      sortNo: startIndex + index + 1
    }));
    console.log('formattedRows:', formattedRows);
// 添加完数据后，如果有 innerPackageCapacity，立即计算内包数量
    nextTick(() => {
      productionOrderDetailTable.dataSource.forEach(row => {
        if (row.packageCapacity && row.remainingQty) {
          recalculatePackageQty(row);
        }
      });
    });
    // 追加到现有数据
    const newDataSource = [...productionOrderDetailTable.dataSource, ...formattedRows];
    productionOrderDetailTable.dataSource = newDataSource;

    console.log('选择完数据源:', productionOrderDetailTable.dataSource);

    // 更新提示
    selectedPlanDetails.value = newDataSource;
    lastAddedCount.value = addedCount;  // 记录本次新增数量
    // 3. 更新主表汇总数据
    updateMainFormValues();

    createMessage.success(`成功添加 ${formattedRows.length} 条明细`);

  }
  // 提取：更新主表汇总数据
  function updateMainFormValues() {
    const allRows = productionOrderDetailTable.dataSource;
    const firstOriginalRow = selectedPlanDetails.value[0];
    if (allRows.length === 0) {
      // 清空主表
      setFieldsValue({
        productId: undefined,
        productCode: undefined,
        productName: undefined,
        productColor: undefined,
        innerPackageId: undefined,
        innerPackageCapacity:undefined,
        innerPackageName: undefined,
        plannedQty: 0,
        deliveryDate: undefined,
      });
      selectedPlanDetails.value = [];
      return;
    }

    // 重新计算（以第一行为基准，理论上应该一致）
    const firstRow = allRows[0];
    console.log('updateMainFormValues firstRow:', allRows[0]);
    console.log('updateMainFormValues firstOriginalRow:', firstOriginalRow);
    const totalQty = allRows.reduce((sum, row) => sum + Number(row.allocatedQty || 0), 0);

    // 取最早的交期
    const earliestDate = allRows
      .map(r => r.deliverDate)
      .filter(Boolean)
      .sort()[0];

    const earliestPlannedStartDate = allRows
      .map(r => r.plannedStartDate)
      .filter(Boolean)
      .sort()[0];
    const earliestPlannedEndDate = allRows
      .map(r => r.plannedEndDate)
      .filter(Boolean)
      .sort()[0];
    // 关键：设置计划产量时，检查是否需要计算批次数量
    const currentBatchSize = getFieldsValue().batchSize || 50; // 默认50
    const newBatchCount = Math.ceil(totalQty / currentBatchSize);

    const innerPackageQty = Math.ceil(totalQty / firstOriginalRow.innerPackageCapacity);
    // setFieldsValue({
    //   productId: firstRow.productId, // 注意：这里需要从某处获取，可能需要在 formattedRows 中保留
    //   plannedQty: totalQty,
    //   deliveryDate: earliestDate,
    // });
    setFieldsValue({
      productId: firstOriginalRow.productId,
      productCode:firstOriginalRow.productCode,
      productName:firstOriginalRow.productName,
      productColor:firstOriginalRow.productColor,
      innerPackageId:firstOriginalRow.innerPackageId,
      innerPackageCapacity:firstOriginalRow.innerPackageCapacity,
      innerPackageQty:innerPackageQty,
      plannedQty: totalQty,
      batchSize: currentBatchSize,
      batchCount: newBatchCount, // 自动计算
      deliveryDate: earliestDate,
      plannedStartDate: earliestPlannedStartDate,
      plannedEndDate: earliestPlannedEndDate,
    });
    console.log('已设置主表值:', {
      productId: firstOriginalRow.productId,
      productCode: firstOriginalRow.productCode,
      productName: firstOriginalRow.productName,
      productColor: firstOriginalRow.productColor,
      innerPackageId:firstOriginalRow.innerPackageId,
      innerPackageCapacity:firstOriginalRow.innerPackageCapacity,
      innerPackageQty:innerPackageQty,
    });

    createMessage.success(`订单计划数量已更新：${totalQty} kg，共 ${allRows.length} 条明细`);
  }
  // 监听明细变化，同步更新主表数量
  function handleDetailChange(event) {
    const { row, column, value, type } = event;
    if (column.key === 'allocatedQty') {
      // 校验：本次执行 ≤ 原始数量
      const original = Number(row.planAllocatedQty) || 0;
      const current = Number(row.allocatedQty) || 0;
      console.log("handleDetailChange original:", original)
      if (current > original) {
        createMessage.warning(`本次执行数量(${current})不能大于计划数量(${original})`);
        // 重置为原始数量
        row.allocatedQty = original;
        return;
      }

      // 关键：自动重新计算主表总数量
      nextTick(() => {
        recalculateTotalQty();
        //包装数量
        recalculatePackageQty(row);
      });
    }
    if (column.key === 'innerPackageSpec') {
      // 弹窗选择后会自动映射 innerPerOuter，这里触发计算
      nextTick(() => {
        recalculatePackageQty(row);
      });
    }
    // 2. 处理外包装选择变化
    if (column.key === 'outerPackageSpec') {
      // 弹窗选择后会自动映射 innerPerOuter，这里触发计算
      nextTick(() => {
        recalculatePackageQty(row);
      });
    }

    // 3. 处理每箱数量变化（用户手动修改）
    if (column.key === 'innerPerOuter') {
      nextTick(() => {
        recalculatePackageQty(row);
      });
    }

    // 4. 处理内包数量变化（用户手动修改）
    if (column.key === 'innerPackageQty') {
      nextTick(() => {
        // 根据内包数量重新计算外包数量
        const innerPackageQty = Number(value) || 0;
        const innerPerOuter = Number(row.innerPerOuter) || 1;
        const outerPackageQty = Math.ceil(innerPackageQty / innerPerOuter);

        // 使用 setValues 更新当前行
        productionOrderDetail.value.setValues([{
          rowKey: row.id,
          values: {
            outerPackageQty: outerPackageQty
          }
        }]);
      });
    }
  }

  // 重新计算主表总数量
  function recalculateTotalQty() {
    const rows = productionOrderDetailTable.dataSource;
    const total = rows.reduce((sum, row) => sum + (Number(row.allocatedQty) || 0), 0);

    // 关键：更新计划产量时，同步更新批次数量
    const currentBatchSize = getFieldsValue().batchSize || 50;
    const newBatchCount = Math.ceil(total / currentBatchSize);

    setFieldsValue({
      plannedQty: total,
      batchCount: newBatchCount
    });

    createMessage.success(`订单计划数量已更新：${total} kg，${newBatchCount} 批`);
  }

  // 外包装变化，自动计算包装数量
  // async function handleOuterPackageChange(outerPackageId: string) {
  //   if (!outerPackageId || isCalculating.value) return;
  //
  //   isCalculating.value = true;
  //   console.log('开始处理外包装变化:', outerPackageId);
  //
  //   try {
  //     const formData = getFieldsValue();
  //     const innerPackageId = formData.innerPackageId;
  //     const totalQty = formData.plannedQty;
  //     const innerCapacity = formData.innerPackageCapacity || 10; // 默认10kg
  //
  //     console.log('当前表单数据:', { innerPackageId, totalQty, innerCapacity });
  //
  //     if (!innerPackageId) {
  //       createMessage.warning('请先选择内包装（通过"选择计划明细"按钮选择）');
  //       return;
  //     }
  //
  //     if (!totalQty || totalQty <= 0) {
  //       createMessage.warning('计划数量必须大于0');
  //       return;
  //     }
  //
  //     console.log('调用 getPackageMapping API:', { innerPackageId, outerPackageId });
  //
  //     // 查询包装映射关系
  //     const mapping = await getPackageMapping({
  //       innerPackageId,
  //       outerPackageId
  //     });
  //
  //     console.log('API 返回结果:', mapping);
  //
  //     if (!mapping) {
  //       createMessage.warning('未找到该内外包装的映射关系，请先维护包装映射');
  //       return;
  //     }
  //
  //     const innerPerOuter = mapping?.innerPerOuter || 1;  // 一箱几个内包
  //
  //     // 计算
  //     const innerPackageQty = Math.ceil(totalQty / innerCapacity);
  //     const outerPackageQty = Math.ceil(innerPackageQty / innerPerOuter);
  //
  //     console.log('计算结果:', { innerPackageQty, outerPackageQty, innerPerOuter });
  //
  //     // 自动填充表单
  //     setFieldsValue({
  //       innerPackageQty: innerPackageQty,
  //       outerPackageQty: outerPackageQty,
  //       outerInnerPerOuter: innerPerOuter
  //     });
  //
  //     createMessage.success(`包装数量已自动计算：内包${innerPackageQty}个，外包${outerPackageQty}个（每箱${innerPerOuter}个内包）`);
  //   } catch (error: any) {
  //     console.error('查询包装映射失败', error);
  //     createMessage.error('计算包装数量失败：' + (error.message || '网络错误'));
  //   } finally {
  //     isCalculating.value = false;
  //   }
  // }

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
      // 关键修复：检查 row 是否存在
      if (!row) {
        console.warn(`第${i + 1}行数据为空，跳过校验`);
        continue;
      }
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



    if (unref(isUpdate)) {
      //表单赋值
      await setFieldsValue({
        ...data.record,
      });



      requestSubTableData(productionOrderDetailList, {id:data?.record?.id}, productionOrderDetailTable)
    }

    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });

    // 关键：只有在启用编辑模式（showFooter=true）时才启动监听
    if (data?.showFooter) {
      console.log('准备启动表单监听，data.showFooter=', data.showFooter);
      nextTick(() => {
        startQtyWatch(); // 启动产量字段监听
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

    // 停止监听
    stopFormWatch();
    stopFormWatch();
    // 重置产量监听
    prevFormValues.value = { plannedQty: null, batchSize: null, batchCount: null };
  }

  function classifyIntoFormData(allValues) {
    let main = Object.assign({}, allValues.formValue)

    let list = allValues?.tablesValue?.[0]?.tableData || [];

    // 清理 JVxeTable 生成的临时 ID，强制设为 null
    list = list.map(row => {
      const id = row.id;
      // 如果是临时 ID（row_ 开头）或空字符串，设为 null
      if (!id || id === '' || (typeof id === 'string' && id.startsWith('row_'))) {
        return { ...row, id: null };
      }
      return row;
    });
    return {
      ...main, // 展开
      productionOrderDetailList: list,//allValues.tablesValue[0].tableData,
    }
  }

  //表单提交事件
  async function requestAddOrEdit(values) {
    try {
      console.log('提交前数据源:', productionOrderDetailTable.dataSource);
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

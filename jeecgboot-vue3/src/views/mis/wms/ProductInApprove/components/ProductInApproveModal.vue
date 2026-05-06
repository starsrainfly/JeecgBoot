<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="StockInForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="入库明细表" key="stockInDetail" :forceRender="true"><!--  :columns="stockInDetailTable.columns"-->
        <JVxeTable
          keep-source
          resizable
          ref="stockInDetail"
          :loading="stockInDetailTable.loading"

          :columns="auditColumns"
          :dataSource="stockInDetailTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
          @edit-closed="handleValueChange"
          />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref,reactive,nextTick} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import { JVxeTable } from '/@/components/jeecg/JVxeTable'
    import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
    import {formSchema,stockInDetailColumns} from '../ProductInApprove.data';
    import {saveOrUpdate,stockInDetailList,approve} from '../ProductInApprove.api';
    import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const formDisabled = ref(false);
    const isAuditMode = ref(false); // 新增：是否为审核模式
    const refKeys = ref(['stockInDetail', ]);
    const activeKey = ref('stockInDetail');
    const stockInDetail = ref();
    const tableRefs = {stockInDetail, };
    const stockInDetailTable = reactive({
          loading: false,
          dataSource: [],
          columns:stockInDetailColumns
    })

    // ==================== 新增：审核模式下的列配置 ====================
    /**
     * 审核模式下的列配置
     * 只有特定字段可编辑：实收数量(actualQty)、质检状态(qcStatus)
     * 其他字段设置为 disabled
     */
    const auditColumns = computed(() => {
      if (!isAuditMode.value) {
        // 非审核模式，返回原始列配置
        return stockInDetailColumns;
      }

      // 审核模式：设置各列的 disabled 状态
      return stockInDetailColumns.map(col => {
        const editableFields = ['actualQty', 'productionDate','shelfLife','expiryDate', 'qcStatus','batchNo','serialNo']; // 审核时可编辑的字段
        const isEditable = editableFields.includes(col.key);

        return {
          ...col,
          disabled: !isEditable, // 不可编辑的字段设置为 true
          // 对于 popup 类型，在审核模式下禁用选择
          readonly: !isEditable && col.type === 'popup' ? true : false
        };
      });
    });


    // 使用 ref 来存储当前提交方法，以便动态切换
    const submitMethodRef = ref(requestAddOrEdit);
    // 在 useModalInner 中切换
   // submitMethodRef.value = isAuditMode.value ? requestAudit : requestAddOrEdit;
    // 不使用 ref，而是使用一个函数来动态判断
    async function handleSubmitMethod(values) {
      if (isAuditMode.value) {
        console.log('执行审核提交');
        return requestAudit(values);
      } else {
        console.log('执行编辑/新增提交');
        return requestAddOrEdit(values);
      }
    }
    //表单配置
    const [registerForm, {setProps,resetFields, setFieldsValue, validate,updateSchema}] = useForm({
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 6},
        labelWidth:100,
    });

     //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //重置表单
        await reset();
      // 新增：判断是否为审核模式
        isAuditMode.value = data?.isAudit === true;

        setModalProps({confirmLoading: false,showCancelBtn:data?.showFooter,
          showOkBtn:data?.showFooter,
          title:data?.title || getModalTitle()});

        isUpdate.value = !!data?.isUpdate;
        formDisabled.value = !data?.showFooter;

      // // 审核模式下更新表单 schema
      // if (isAuditMode.value) {
      //   // 过滤出审核时可编辑的字段
      //   const editableFields = ['approveStatus', 'approveRemark', 'id'];
      //   const filteredSchema = formSchema.filter(schema => editableFields.includes(schema.field));
      //   await updateSchema(filteredSchema);
      // }
      // ==================== 关键修复：审核模式下设置字段禁用 ====================
      if (isAuditMode.value) {
        // 审核模式下，只有 approveStatus 和 approveRemark 可编辑，其他都禁用
        const editableFields = ['approveStatus', 'approveRemark','id'];

        // 构建新的 schema，使用 dynamicDisabled 函数控制每个字段
        const auditSchema = formSchema.map(schema => {
          const canEdit = editableFields.includes(schema.field);
          return {
            ...schema,
            // 使用 dynamicDisabled 函数返回布尔值
            dynamicDisabled: () => !canEdit,
            // 同时设置 disabled 属性作为备用
            disabled: !canEdit
          };
        });

        // 更新 schema
        await updateSchema(auditSchema);

        // 强制刷新表单
        await nextTick();
      }

        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
             requestSubTableData(stockInDetailList, {id:data?.record?.id}, stockInDetailTable)
        }
        // 隐藏底部时禁用整个表单
      // 控制表单禁用状态
      if (!data?.showFooter) {
        // 详情模式：全部禁用
        setProps({ disabled: true });
      } else if (isAuditMode.value) {
        // 审核模式：不禁用（通过 schema 控制字段级别禁用）
        setProps({ disabled: false });
      } else {
        // 新增/编辑模式：不禁用
        setProps({ disabled: false });
      }
    });
    //方法配置
    const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] =
      useJvxeMethod(
        handleSubmitMethod,
        classifyIntoFormData,tableRefs,activeKey,refKeys);

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
      // if (isAuditMode.value) return '入库审核';
      // return !unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情';
    });
    async function reset(){
      await resetFields();
      activeKey.value = 'stockInDetail';
      stockInDetailTable.dataSource = [];
      isAuditMode.value = false;
      submitMethodRef.value = requestAddOrEdit; // 重置提交方法
      // ==================== 关键修复：重置时恢复原始 schema ====================
      // 恢复所有字段的 disabled 状态
      const originalSchema = formSchema.map(schema => ({
        ...schema,
        dynamicDisabled: false,
        disabled: false
      }));
      await updateSchema(originalSchema);

      await nextTick();
    }

    // ==================== 核心：实时计算逻辑 ====================


    /**
     * 处理编辑完成事件
     */
     function handleValueChange({ row, column, cellValue }) {
      const field = column.key || column.field;
      const tableInstance = stockInDetail.value;
      if (!tableInstance) return;

      // 获取行标识（用于 setValues）
      const rowIndex = tableInstance.getRowIndex ? tableInstance.getRowIndex(row) : stockInDetailTable.dataSource.indexOf(row);
      const rowKey = row.id !== undefined ? row.id : rowIndex;

      const valuesToSet: any = {};

      // 1. 申请数量变化时，自动填充实收数量（始终同步，不管是否为空）
      if (field === 'applyQty') {
        const applyQty = parseFloat(row.applyQty); //parseFloat(cellValue || 0);
        // 始终将申请数量同步到实收数量（如果需要用户手动修改，可以注释掉这行）
        valuesToSet.actualQty = applyQty;
        row.actualQty = applyQty;
      }

      // 2. 币种变化时，自动获取汇率
      if (field === 'currency') {
        const exchangeRate = getExchangeRateByCurrency(cellValue);
        valuesToSet.exchangeRate = exchangeRate;
        row.exchangeRate = exchangeRate;
      }

      if (['productionDate', 'shelfLife'].includes(field)) {
        const expiryDate = calculateExpiryDate(row);
        if (expiryDate) {
          valuesToSet.expiryDate = expiryDate;
          row.expiryDate = expiryDate;
        }
      }

      // 3. 重新计算金额（当 申请数量、实收数量、单价、汇率 任一变化时）
      if (['applyQty', 'actualQty', 'unitPrice', 'exchangeRate', 'currency'].includes(field)) {
        const totalAmount = calculateTotalAmount(row);
        valuesToSet.totalAmount = totalAmount;
        row.totalAmount = totalAmount;
      }

      // 4. 使用 setValues 批量更新单元格（关键！必须调用才能刷新视图）
      if (Object.keys(valuesToSet).length > 0) {
        console.log('设置值到行', rowKey, ':', valuesToSet);

        tableInstance.setValues([{
          rowKey: rowKey,
          values: valuesToSet
        }]);
      }
    }
    /**
     * 计算并设置行金额
     */
    function calculateTotalAmount(row) {
      // 实收数量优先，如果没有则使用申请数量
      const qty = row.actualQty !== undefined && row.actualQty !== null && row.actualQty !== ''
        ? parseFloat(row.actualQty)
        : parseFloat(row.applyQty || 0);

      const unitPrice = parseFloat(row.unitPrice || 0);
      const exchangeRate = parseFloat(row.exchangeRate || 1);
      console.log("qty:",qty)
      console.log("unitPrice:",unitPrice)
      console.log("exchangeRate:",exchangeRate)

      // 计算：数量 * 单价 * 汇率
      const totalAmount = qty * unitPrice * exchangeRate;
      console.log("totalAmount:",totalAmount)
      // 设置金额（保留2位小数）
      return isNaN(totalAmount) ? 0 : Math.round(totalAmount * 100) / 100;
    }
    // 自动填充实收数量
     function autoFillActualQty(row) {
      if (row.applyQty !== undefined && row.applyQty !== null && row.applyQty !== '' &&
        (row.actualQty === undefined || row.actualQty === null || row.actualQty === '')) {
        row.actualQty = parseFloat(row.applyQty);
      }
    }
    /**
     * 计算失效日期
     * 公式：生产日期 + 保质天数 = 失效日期
     */
    function calculateExpiryDate(row): string | null {
      // 获取生产日期
      const productionDate = row.productionDate;
      // 获取保质天数
      const shelfLife = parseInt(row.shelfLife);

      // 校验数据有效性
      if (!productionDate || isNaN(shelfLife) || shelfLife < 0) {
        return null;
      }

      // 解析生产日期
      const date = new Date(productionDate);
      if (isNaN(date.getTime())) {
        return null;
      }

      // 计算失效日期（生产日期 + 保质天数）
      date.setDate(date.getDate() + shelfLife);

      // 格式化为 YYYY-MM-DD 格式（与 JVxeTypes.date 格式一致）
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');

      return `${year}-${month}-${day}`;
    }
    // 根据币种获取汇率
    function getExchangeRateByCurrency(currencyCode) {
      // 这里可以扩展为调用后端API获取实时汇率
      const rates = {
        'CNY': 1,      // 人民币
        'USD': 7.2,    // 美元
        'EUR': 7.8,    // 欧元
        'JPY': 0.06,   // 日元
        'GBP': 9.2,    // 英镑
        'KRW': 0.0055, // 韩元
      };
      return rates[currencyCode] || 1;
    }

    function classifyIntoFormData(allValues) {
         let main = Object.assign({}, allValues.formValue)
        // 处理子表数据，确保金额已正确计算
        const detailList = allValues.tablesValue[0].tableData.map(item => {
          // 重新计算每一行的金额
          item.totalAmount = calculateTotalAmount(item);
          return item;
        });
         return {
           ...main, // 展开
           stockInDetailList: detailList,//allValues.tablesValue[0].tableData,
         }
       }

    /**
     * 审核提交事件
     */
    async function requestAudit(values) {
      try {
        setModalProps({confirmLoading: true});
        // 使用 approve 接口提交
        await approve(values);
        //关闭弹窗
        closeModal();
        //刷新列表
        emit('success');
      } finally {
        setModalProps({confirmLoading: false});
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

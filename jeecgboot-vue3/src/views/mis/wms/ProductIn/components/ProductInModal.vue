<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1280" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="StockInForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="入库明细表" key="stockInDetail" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="stockInDetail"
          :loading="stockInDetailTable.loading"
          :columns="stockInDetailTable.columns"
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
    import {ref, computed, unref,reactive} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import { JVxeTable } from '/@/components/jeecg/JVxeTable'
    import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
    import {formSchema,stockInDetailColumns} from '../ProductIn.data';
    import {saveOrUpdate,stockInDetailList} from '../ProductIn.api';
    import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils'
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const formDisabled = ref(false);
    const refKeys = ref(['stockInDetail', ]);
    const activeKey = ref('stockInDetail');
    const stockInDetail = ref();
    const tableRefs = {stockInDetail, };
    const stockInDetailTable = reactive({
          loading: false,
          dataSource: [],
          columns:stockInDetailColumns
    })
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
        setModalProps({confirmLoading: false,showCancelBtn:data?.showFooter,showOkBtn:data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        formDisabled.value = !data?.showFooter;
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
             requestSubTableData(stockInDetailList, {id:data?.record?.id}, stockInDetailTable)
        }
        // 隐藏底部时禁用整个表单
       setProps({ disabled: !data?.showFooter })
    });
    //方法配置
    const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] = useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys);

    //设置标题
    const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

    async function reset(){
      await resetFields();
      activeKey.value = 'stockInDetail';
      stockInDetailTable.dataSource = [];
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

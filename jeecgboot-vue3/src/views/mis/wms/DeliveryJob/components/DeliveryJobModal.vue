<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="1400"
    :defaultFullscreen="false"
    @ok="handleSubmit"
  >
    <div class="delivery-modal-body">
      <!-- 顶部：订单选择表单 -->
      <a-row :gutter="16" class="mb-4">
        <a-col :span="24">
          <BasicForm @register="registerOrderForm" />
        </a-col>
      </a-row>

      <!-- 扫码 -->
      <a-row :gutter="16" class="mb-4">
        <a-col :span="8">
          <a-form-item label="产品扫码" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
            <a-input-search
              ref="productScanInput"
              v-model:value="scanCodeVal"
              placeholder="请扫描产品二维码（支持扫码枪/摄像头）"
              enter-button="扫码"
              @search="handleScan"
              @pressEnter="handleScan"
              @keydown="onProductScanKeydown"
            />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-alert v-if="scanMsg" :message="scanMsg" :type="scanType" show-icon />
        </a-col>
      </a-row>

      <!-- 中部：未发货明细 + FIFO库存 -->
      <a-row :gutter="16" class="mb-4">
        <a-col :span="14">
          <a-card title="未发货订单明细" :bordered="false" :bodyStyle="{ padding: '8px' }">
            <BasicTable @register="registerOrderTable" />
          </a-card>
        </a-col>
        <a-col :span="10">
          <a-card title="FIFO库存建议（点击行添加）" :bordered="false" :bodyStyle="{ padding: '8px' }">
            <BasicTable @register="registerStockTable" />
          </a-card>
        </a-col>
      </a-row>

      <!-- 本次发货明细 -->
      <a-card title="本次发货明细" :bordered="false" :bodyStyle="{ padding: '8px' }" class="mb-4">
        <template #extra>
          <a-button type="primary" danger size="small" @click="clearItems">清空</a-button>
        </template>
        <BasicTable @register="registerDeliveryTable">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'actualQty'">
              <a-input-number v-model:value="record.actualQty" :min="0.01" :step="1" style="width: 100%" />
            </template>
            <template v-if="column.key === 'action'">
              <a-button type="link" danger size="small" @click="handleDeleteItem(record)">删除</a-button>
            </template>
          </template>
        </BasicTable>
      </a-card>

      <!-- 物流信息 -->
      <a-card title="物流信息" :bordered="false" :bodyStyle="{ padding: '8px' }">
        <BasicForm @register="registerLogisticsForm" />
      </a-card>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, reactive, computed, watch, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable } from '/@/components/Table';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { add } from '/@/components/Form/src/componentMap';
  import {
    orderLineColumns,
    stockColumns,
    deliveryItemColumns,
    logisticsFormSchema,
    orderSelectFormSchema,
  } from '../DeliveryJob.data';
  import { getPendingOrderLines, scanCode, scanDeliver } from '../DeliveryJob.api';
  import ScanInput from './ScanInput.vue';
  import JPopup from '/@/components/Form/src/jeecg/components/JPopup.vue';

  add('ScanInput', ScanInput);

  const emit = defineEmits(['register', 'success']);
  const { createMessage, createConfirm } = useMessage();

  const title = computed(() => '扫码发货');

  // ========== 订单选择表单 ==========
  const [registerOrderForm, { getFieldsValue: getOrderFields, setFieldsValue: setOrderFields, resetFields: resetOrderFields }] = useForm({
    schemas: orderSelectFormSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 8 },
    labelWidth: 100,
  });

  // 当前选中的订单信息（从表单同步）
  const selectedOrder = reactive<any>({
    id: '',
    orderNo: '',
    customerId: '',
    customerName: '',
    consignee: '',
    consigneePhone: '',
    consigneeAddress: '',
  });

  // ========== 扫码 ==========
  const scanCodeVal = ref('');
  const scanMsg = ref('');
  const scanType = ref<'success' | 'error' | 'warning' | 'info'>('info');
  const matchedStocks = ref<any[]>([]);
  const matchedOrderLines = ref<any[]>([]);

  let productScanBuffer = '';
  let productScanLastTime = 0;
  const PRODUCT_SCAN_THRESHOLD_MS = 80;

  function onProductScanKeydown(e: KeyboardEvent) {
    const now = Date.now();
    const diff = now - productScanLastTime;
    productScanLastTime = now;
    if (e.key === 'Enter') {
      if (productScanBuffer.length > 0 && diff < PRODUCT_SCAN_THRESHOLD_MS) {
        e.preventDefault();
        scanCodeVal.value = productScanBuffer;
        handleScan();
        productScanBuffer = '';
        return;
      }
      productScanBuffer = '';
      return;
    }
    if (diff > PRODUCT_SCAN_THRESHOLD_MS) productScanBuffer = '';
    productScanBuffer += e.key;
  }

  // ========== 表格数据 ==========
  const orderLineData = ref<any[]>([]);
  const deliveryItems = ref<any[]>([]);

  const [registerOrderTable] = useTable({
    dataSource: orderLineData,
    columns: orderLineColumns,
    canResize: false,
    pagination: false,
    showIndexColumn: false,
  });

  const [registerStockTable, { setTableData: setStockData }] = useTable({
    columns: stockColumns,
    canResize: false,
    pagination: false,
    showIndexColumn: false,
    rowSelection: { type: 'radio' },
    clickToRowSelect: true,
    onRowClick: (record) => addStockToDelivery(record),
  });

  const [registerDeliveryTable, { setTableData: setDeliveryData }] = useTable({
    dataSource: deliveryItems,
    columns: [
      ...deliveryItemColumns,
      { title: '操作', key: 'action', width: 80, fixed: 'right' },
    ],
    canResize: false,
    pagination: false,
    showIndexColumn: true,
  });

  // 物流表单
  const [registerLogisticsForm, { setFieldsValue: setLogisticsFields, validate: validateLogistics, resetFields: resetLogisticsFields }] = useForm({
    schemas: logisticsFormSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 8 },
    labelWidth: 100,
  });

  // ========== 核心：监听订单表单变化 ==========
  let lastOrderId = '';

  watch(
    () => getOrderFields(),
    async (vals) => {
      if (!vals) return;
      const newOrderId = vals.sourceOrderId;

      // 防止重复触发
      if (!newOrderId || newOrderId === lastOrderId) return;
      lastOrderId = newOrderId;

      // 同步到 selectedOrder
      Object.assign(selectedOrder, {
        id: newOrderId,
        orderNo: vals.orderNo || '',
        customerId: vals.customerId || '',
        customerName: vals.customerName || '',
        consignee: vals.consignee || '',
        consigneePhone: vals.consigneePhone || '',
        consigneeAddress: vals.consigneeAddress || '',
      });

      // 加载未发货明细
      const res = await getPendingOrderLines({ orderId: newOrderId });

      orderLineData.value = res?.lines || res || [];

      // 设置物流表单默认值
      const now = new Date();
      const fmt = (n: number) => String(n).padStart(2, '0');
      const defaultTime = `${now.getFullYear()}-${fmt(now.getMonth() + 1)}-${fmt(now.getDate())} ${fmt(now.getHours())}:${fmt(now.getMinutes())}:${fmt(now.getSeconds())}`;

      await setLogisticsFields({
        sourceOrderId: newOrderId,
        sourceOrderNo: vals.orderNo,
        customerId: vals.customerId,
        customerName: vals.customerName,
        consignee: vals.consignee,
        consigneePhone: vals.consigneePhone,
        consigneeAddress: vals.consigneeAddress,
        deliveryTime: defaultTime,
      });

      setStockData([]);
      scanMsg.value = '';
    },
    { deep: true, immediate: false }
  );

  // ========== Modal 生命周期 ==========
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async () => {
    await resetAll();
    setModalProps({ confirmLoading: false, showCancelBtn: true, showOkBtn: true });
  });

  async function resetAll() {
    lastOrderId = '';
    await resetOrderFields();
    Object.assign(selectedOrder, {
      id: '', orderNo: '', customerId: '', customerName: '',
      consignee: '', consigneePhone: '', consigneeAddress: '',
    });
    orderLineData.value = [];
    setStockData([]);
    clearItems();
    scanMsg.value = '';
    scanCodeVal.value = '';
    await resetLogisticsFields();
  }

  // ========== 扫码方法 ==========
  async function handleScan() {
    if (!selectedOrder.id) {
      createMessage.warning('请先选择销售订单');
      return;
    }
    const code = scanCodeVal.value?.trim();
    if (!code) {
      createMessage.warning('请输入扫码内容');
      return;
    }
    try {
      const res = await scanCode({ scanCode: code, orderId: selectedOrder.id });
      if (res && res.matched) {
        scanMsg.value = res.msg || '匹配成功';
        scanType.value = 'success';
        matchedStocks.value = res.stocks || [];
        matchedOrderLines.value = res.orderLines || [];
        setStockData(matchedStocks.value);
        if (matchedStocks.value.length > 0) addStockToDelivery(matchedStocks.value[0]);
      } else {
        scanMsg.value = res?.msg || '未匹配';
        scanType.value = 'error';
        matchedStocks.value = [];
        matchedOrderLines.value = [];
        setStockData([]);
      }
    } catch (err: any) {
      scanMsg.value = err?.message || '扫码解析失败';
      scanType.value = 'error';
    }
    scanCodeVal.value = '';
  }

  function addStockToDelivery(stock: any) {
    if (!stock || !matchedOrderLines.value?.length) return;
    const orderLine = matchedOrderLines.value[0];
    const currentSum = deliveryItems.value
      .filter((i) => i.sourceDetailId === orderLine.id)
      .reduce((sum, i) => sum + Number(i.actualQty || 0), 0);

    const remaining = Number(orderLine.remainingQty || 0);
    const available = Number(stock.quantity || 0) - Number(stock.lockedQty || 0);
    if (available < 1) {
      createMessage.warning('该库存可用数量不足');
      return;
    }
    const addQty = Math.min(1, available);

    if (currentSum + addQty > remaining) {
      createConfirm({
        iconType: 'warning',
        title: '超发提醒',
        content: `该产品剩余可发数量为 ${remaining}，本次累计将发 ${currentSum + addQty}，是否继续？`,
        onOk: () => doAddItem(stock, orderLine, addQty),
      });
    } else {
      doAddItem(stock, orderLine, addQty);
    }
  }

  function doAddItem(stock: any, orderLine: any, qty: number) {
    const available = Number(stock.quantity || 0) - Number(stock.lockedQty || 0);
    if (qty > available) {
      createMessage.warning(`库存可用数量仅 ${available}`);
      return;
    }
    deliveryItems.value.push({
      sourceDetailId: orderLine.id,
      stockId: stock.id,
      goodsId: stock.goodsId,
      goodsCode: stock.goodsCode,
      goodsName: stock.goodsName,
      goodsSpec: stock.goodsSpec,
      unit: stock.unit,
      productionBatchId: stock.productionBatchId,
      productionBatchNo: stock.batchNo,
      productionDate: stock.productionDate,
      expiryDate: stock.expiryDate,
      warehouseId: stock.warehouseId,
      warehouseName: stock.warehouseId_dictText || stock.warehouseId,
      actualQty: qty,
      unitPrice: orderLine.unitPrice,
      scanCode: scanCodeVal.value || stock.goodsCode,
      remark: '',
    });
    setDeliveryData([...deliveryItems.value]);
    createMessage.success('已添加到发货明细');
  }

  function handleDeleteItem(record: any) {
    const idx = deliveryItems.value.findIndex(
      (i) => i.stockId === record.stockId && i.sourceDetailId === record.sourceDetailId
    );
    if (idx > -1) {
      deliveryItems.value.splice(idx, 1);
      setDeliveryData([...deliveryItems.value]);
    }
  }

  function clearItems() {
    deliveryItems.value = [];
    setDeliveryData([]);
  }

  async function handleSubmit() {
    if (!selectedOrder.id) {
      createMessage.warning('请先选择销售订单');
      return;
    }
    if (deliveryItems.value.length === 0) {
      createMessage.warning('请至少添加一条发货明细');
      return;
    }
    const values = await validateLogistics();
    const validItems = deliveryItems.value.filter((i) => Number(i.actualQty) > 0);
    if (validItems.length === 0) {
      createMessage.warning('请填写有效的发货数量');
      return;
    }
    const params = {
      ...values,
      scanItems: validItems.map((i) => ({ ...i, actualQty: Number(i.actualQty) })),
    };
    try {
      setModalProps({ confirmLoading: true });
      const res = await scanDeliver(params);
      createMessage.success(res || '发货成功');
      closeModal();
      emit('success');
    } catch (err: any) {
      createMessage.error(err?.message || '发货失败');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

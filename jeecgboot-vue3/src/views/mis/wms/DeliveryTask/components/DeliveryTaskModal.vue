<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    title="扫码发货"
    :width="1400"
    @ok="handleSubmit"
  >
    <div class="delivery-modal-body">
      <!-- 顶部：订单信息（只读）+ 物流信息 -->
      <a-card title="订单信息" :bordered="false" :bodyStyle="{ padding: '12px' }" class="mb-4">
        <a-descriptions :column="4">
          <a-descriptions-item label="订单号">{{ orderInfo.orderNo }}</a-descriptions-item>
          <a-descriptions-item label="客户名称">{{ orderInfo.customerName }}</a-descriptions-item>
          <a-descriptions-item label="收货人">{{ orderInfo.consignee }}</a-descriptions-item>
          <a-descriptions-item label="联系电话">{{ orderInfo.consigneePhone }}</a-descriptions-item>
          <a-descriptions-item label="收货地址" :span="2">{{ orderInfo.consigneeAddress }}</a-descriptions-item>
        </a-descriptions>
      </a-card>

      <!-- 扫码区域 -->
      <a-row :gutter="16" class="mb-4">
        <a-col :span="12">
          <a-form-item label="产品扫码" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
            <a-input-search
              v-model:value="scanCodeVal"
              placeholder="请扫描产品二维码（支持扫码枪/摄像头）"
              enter-button="扫码"
              @search="handleScan"
              @pressEnter="handleScan"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12" style="display: flex; align-items: center;">
          <a-alert v-if="scanMsg" :message="scanMsg" :type="scanType" show-icon style="width: 100%;" />
        </a-col>
      </a-row>

      <!-- 中部：未发货明细 + FIFO库存建议 -->
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

      <!-- 本次扫码明细 -->
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

      <!-- 物流信息表单 -->
      <a-card title="物流信息" :bordered="false" :bodyStyle="{ padding: '8px' }">
        <BasicForm @register="registerLogisticsForm" />
      </a-card>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, reactive, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable } from '/@/components/Table';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';
  import {
    pendingLineColumns,
    stockColumns,
    deliveryItemColumns,
    logisticsFormSchema,
  } from '../DeliveryTask.data';
  import { getPendingLines, scanCode, scanDeliver } from '../DeliveryTask.api';

  const emit = defineEmits(['register', 'success']);
  const { createMessage, createConfirm } = useMessage();

  // 订单信息（从列表传入）
  const orderInfo = reactive({
    orderId: '',
    orderNo: '',
    customerId: '',
    customerName: '',
    consignee: '',
    consigneePhone: '',
    consigneeAddress: '',
  });

  // 扫码
  const scanCodeVal = ref('');
  const scanMsg = ref('');
  const scanType = ref<'success' | 'error'>('success');
  const matchedStocks = ref<any[]>([]);
  const matchedOrderLines = ref<any[]>([]);

  // 表格数据
  const orderLineData = ref<any[]>([]);
  const deliveryItems = ref<any[]>([]);

  // 未发货明细表格
  const [registerOrderTable] = useTable({
    dataSource: orderLineData,
    columns: pendingLineColumns,
    canResize: false,
    pagination: false,
  });

  // FIFO库存表格
  const [registerStockTable, { setTableData: setStockData }] = useTable({
    columns: stockColumns,
    canResize: false,
    pagination: false,
    rowSelection: { type: 'radio' },
    onRowClick: (record) => addStockToDelivery(record),
  });

  // 本次发货明细
  const [registerDeliveryTable, { setTableData: setDeliveryData }] = useTable({
    dataSource: deliveryItems,
    columns: [
      ...deliveryItemColumns,
      { title: '操作', key: 'action', width: 80, fixed: 'right' },
    ],
    canResize: false,
    pagination: false,
  });

  // 物流表单
  const [registerLogisticsForm, { setFieldsValue, validate, resetFields }] = useForm({
    schemas: logisticsFormSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 8 },
  });

  // Modal生命周期
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetAll();
    // 自动带入订单信息
    orderInfo.orderId = data.orderId;
    orderInfo.orderNo = data.orderNo;

    // 加载订单详情和未发货明细
    await loadOrderInfo(data.orderId);
    setModalProps({ confirmLoading: false });
  });

  async function loadOrderInfo(orderId: string) {
    // 加载订单主表信息
    const res = await getPendingLines({ orderId });
    if (res.order) {
      Object.assign(orderInfo, res.order);
      // 设置物流表单默认值
      await setFieldsValue({
        sourceOrderId: orderInfo.orderId,
        sourceOrderNo: orderInfo.orderNo,
        customerId: orderInfo.customerId,
        customerName: orderInfo.customerName,
        consignee: orderInfo.consignee,
        consigneePhone: orderInfo.consigneePhone,
        consigneeAddress: orderInfo.consigneeAddress,
        deliveryTime: formatNow(),
      });
    }
    // 加载未发货明细
    orderLineData.value = res.lines || [];
  }

  async function handleScan() {
    const code = scanCodeVal.value?.trim();
    if (!code) return;

    try {
      const res = await scanCode({
        scanCode: code,
        orderId: orderInfo.orderId,
      });

      if (res.matched) {
        scanMsg.value = res.msg || '匹配成功';
        scanType.value = 'success';
        matchedStocks.value = res.stocks || [];
        matchedOrderLines.value = res.orderLines || [];
        setStockData(matchedStocks.value);

        if (matchedStocks.value.length > 0) {
          addStockToDelivery(matchedStocks.value[0]);
        }
      } else {
        scanMsg.value = res?.msg || '未匹配';
        scanType.value = 'error';
      }
    } catch (err: any) {
      scanMsg.value = err?.message || '扫码失败';
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
      createMessage.warning('库存可用数量不足');
      return;
    }

    const addQty = Math.min(1, available, remaining);

    if (currentSum + addQty > remaining) {
      createConfirm({
        iconType: 'warning',
        title: '超发提醒',
        content: `剩余可发 ${remaining}，本次累计将发 ${currentSum + addQty}，是否继续？`,
        onOk: () => doAddItem(stock, orderLine, addQty),
      });
    } else {
      doAddItem(stock, orderLine, addQty);
    }
  }

  function doAddItem(stock: any, orderLine: any, qty: number) {
    deliveryItems.value.push({
      sourceDetailId: orderLine.id,
      stockId: stock.id,
      goodsId: stock.goodsId,
      goodsCode: stock.goodsCode,
      goodsName: stock.goodsName,
      productionBatchId: stock.productionBatchId,
      productionBatchNo: stock.batchNo,
      productionDate: stock.productionDate,
      expiryDate: stock.expiryDate,
      warehouseId: stock.warehouseId,
      warehouseName: stock.warehouseId_dictText,
      actualQty: qty,
      unitPrice: orderLine.unitPrice,
      scanCode: scanCodeVal.value || stock.goodsCode,
    });
    setDeliveryData([...deliveryItems.value]);
    createMessage.success('已添加');
  }

  // ... 其他方法（clearItems, handleDeleteItem, handleSubmit 类似 DeliveryJobModal）

  function formatNow(): string {
    const now = new Date();
    const fmt = (n: number) => String(n).padStart(2, '0');
    return `${now.getFullYear()}-${fmt(now.getMonth() + 1)}-${fmt(now.getDate())} ${fmt(now.getHours())}:${fmt(now.getMinutes())}:${fmt(now.getSeconds())}`;
  }

  async function resetAll() {
    deliveryItems.value = [];
    setDeliveryData([]);
    setStockData([]);
    scanMsg.value = '';
    scanCodeVal.value = '';
    await resetFields();
  }
</script>

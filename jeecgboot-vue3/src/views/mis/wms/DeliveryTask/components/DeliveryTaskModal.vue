<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    title="扫码发货"
    :width="1200"
    @ok="handleSubmit"
  >
    <div class="delivery-modal-body">
      <!-- 订单信息：精简 -->
      <a-card :bordered="false" :bodyStyle="{ padding: '8px 12px' }" class="mb-2">
        <a-descriptions :column="4" size="small">
          <a-descriptions-item label="订单号">{{ orderInfo.orderNo }}</a-descriptions-item>
          <a-descriptions-item label="客户">{{ orderInfo.customerName }}</a-descriptions-item>
          <a-descriptions-item label="收货人">{{ orderInfo.consignee }} {{ orderInfo.consigneePhone }}</a-descriptions-item>
          <a-descriptions-item label="地址" :span="2">{{ orderInfo.consigneeAddress }}</a-descriptions-item>
        </a-descriptions>
      </a-card>

      <!-- 产品扫码 + 提示 -->
      <a-row :gutter="16" class="mb-2">
        <a-col :xs="24" :sm="16">
          <a-form-item label="产品扫码" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
            <div class="scan-input flex items-center">
              <a-input
                ref="productInputRef"
                v-model:value="scanCodeVal"
                @keydown="onProductKeydown"
                placeholder="请扫描产品二维码"
                allowClear
              />
              <a-button class="ml-2" type="primary" size="small" @click="openProductScan">
                <Icon icon="ant-design:camera-outlined" />
              </a-button>
            </div>
          </a-form-item>
        </a-col>
        <a-col :xs="24" :sm="8">
          <a-alert v-if="productScanMsg" :message="productScanMsg" :type="productScanType" show-icon />
        </a-col>
      </a-row>

      <!-- 中部：未发货明细 + FIFO库存 -->
      <a-row :gutter="12" class="mb-2">
        <a-col :xs="24" :sm="14">
          <a-card title="未发货明细" :bordered="false" :bodyStyle="{ padding: '4px' }" size="small">
            <BasicTable @register="registerOrderTable" />
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="10">
          <a-card title="库存建议" :bordered="false" :bodyStyle="{ padding: '4px' }" size="small">
            <BasicTable @register="registerStockTable" >
              <template #stockAction="{ record }">
                <a-button type="link" size="small" @click="addStockToDelivery(record)">
                  添加
                </a-button>
              </template>
            </BasicTable>
          </a-card>
        </a-col>
      </a-row>

      <!-- 本次发货明细 -->
      <a-card title="本次发货" :bordered="false" :bodyStyle="{ padding: '4px' }" class="mb-2" size="small">
       <!--汇总信息-->

        <template #extra>
          <a-button type="primary" danger size="small" @click="clearItems">清空</a-button>
        </template>
        <BasicTable @register="registerDeliveryTable">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'actualQty'">
              <a-input-number
                v-model:value="record.actualQty"
                :min="0.01"
                :step="1" style="width: 100%"
                size="small"
                @change="(val) => onQtyChange(record, val)"
              />
            </template>
            <template v-if="column.key === 'action'">
              <a-button type="link" danger size="small" @click="handleDeleteItem(record)">删除</a-button>
            </template>
          </template>
        </BasicTable>
      </a-card>

      <!-- 物流信息（紧凑布局） -->
      <a-card title="物流信息" :bordered="false" :bodyStyle="{ padding: '8px' }" >
        <a-row :gutter="12" align="middle" class="mb-2">
          <!-- 物流单号：label + input + button 一行 -->
          <a-col :xs="24" :sm="12" :lg="8">
            <div class="logistics-input-row ">
              <span class="logistics-label">物流单号</span>
              <a-input
                ref="logisticsInputRef"
                v-model:value="logisticsFormTrackingNo"
                @keydown="onLogisticsKeydown"
                placeholder="扫描或输入"
                allowClear

              />
              <a-button class="ml-2" type="primary" size="small" @click="openLogisticsScan">
                <Icon icon="ant-design:camera-outlined" />
              </a-button>
            </div>
          </a-col>
          <!-- 提示信息 -->
          <a-col :xs="24" :sm="12" :md="16" >
            <a-alert
              v-if="logisticsScanMsg"
              :message="logisticsScanMsg"
              :type="logisticsScanType"
              show-icon
              style="padding: 4px 8px;"
            />
          </a-col>
        </a-row>

        <BasicForm
          @register="registerLogisticsForm"
          :baseColProps="{ xs: 24, sm: 12, md: 8 }"
          :labelWidth="80"
        />
      </a-card>
    </div>

    <!-- 产品摄像头扫码弹窗 -->
    <a-modal
      v-model:visible="productScanVisible"
      title="摄像头扫码-产品"
      :footer="null"
      :width="420"
      :destroyOnClose="true"
      @cancel="stopProductScan"
    >
      <div class="scan-box">
        <div id="product-scan-reader" class="scan-reader" />
        <p v-if="productScanTip" class="scan-tip">{{ productScanTip }}</p>
        <a-button type="primary" block class="mt-2" @click="stopProductScan">停止扫描</a-button>
      </div>
    </a-modal>

    <!-- 物流摄像头扫码弹窗 -->
    <a-modal
      v-model:visible="logisticsScanVisible"
      title="摄像头扫码-物流单号"
      :footer="null"
      :width="420"
      :destroyOnClose="true"
      @cancel="stopLogisticsScan"
    >
      <div class="scan-box">
        <div id="logistics-scan-reader" class="scan-reader" />
        <p v-if="logisticsScanTip" class="scan-tip">{{ logisticsScanTip }}</p>
        <a-button type="primary" block class="mt-2" @click="stopLogisticsScan">停止扫描</a-button>
      </div>
    </a-modal>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, reactive, watch, nextTick, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable } from '/@/components/Table';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { Icon } from '/@/components/Icon';

  import {
    pendingLineColumns,
    stockColumns,
    deliveryItemColumns,
    logisticsFormSchema,
  } from '../DeliveryTask.data';
  import { getPendingLines, scanCode, scanDeliver, getOrderStocks, scanLogisticsCode } from '../DeliveryTask.api';

  const emit = defineEmits(['register', 'success']);
  const { createMessage, createConfirm } = useMessage();

  // ==================== 订单信息 ====================
  const orderInfo = reactive({
    orderId: '',
    orderNo: '',
    customerId: '',
    customerName: '',
    consignee: '',
    consigneePhone: '',
    consigneeAddress: '',
  });

  // ==================== 产品扫码 ====================
  const scanCodeVal = ref('');
  const productScanMsg = ref('');
  const productScanType = ref<'success' | 'error' | 'warning'>('success');
  const matchedStocks = ref<any[]>([]);
  const matchedOrderLines = ref<any[]>([]);
  const productInputRef = ref<HTMLInputElement | null>(null);
  const productScanVisible = ref(false);
  const productScanTip = ref('');
  let productHtml5QrCode: any = null;

  // ==================== 物流单号扫码 ====================
  const logisticsFormTrackingNo = ref('');
  const logisticsScanMsg = ref('');
  const logisticsScanType = ref<'success' | 'error' | 'warning'>('success');
  const logisticsInputRef = ref<HTMLInputElement | null>(null);
  const logisticsScanVisible = ref(false);
  const logisticsScanTip = ref('');
  let logisticsHtml5QrCode: any = null;

  // ==================== 表格数据 ====================
  const orderLineData = ref<any[]>([]);
  const deliveryItems = ref<any[]>([]);

  // 本次发货总数量
  const deliveryTotalQty = computed(() => {
    return deliveryItems.value.reduce((sum, item) => sum + Number(item.actualQty || 0), 0);
  });

  // 单位（取第一个产品的单位，如果没有则空）
  const deliveryUnit = computed(() => {
    return deliveryItems.value[0]?.unit || '';
  });

  const [registerOrderTable] = useTable({
    dataSource: orderLineData,
    columns: pendingLineColumns,
    canResize: false,
    pagination: false,
  });

  const [registerStockTable, { setTableData: setStockData }] = useTable({
    columns: stockColumns,
    canResize: false,
    pagination: false,

  });

  const [registerDeliveryTable, { setTableData: setDeliveryData }] = useTable({
    dataSource: deliveryItems,
    columns: [
      ...deliveryItemColumns,
      { title: '操作', key: 'action', width: 80, fixed: 'right' },
    ],
    canResize: false,
    pagination: false,

  });

  // 物流表单（不含trackingNo）
  const [registerLogisticsForm, { setFieldsValue, validate, resetFields, getFieldsValue, updateSchema }] = useForm({
    schemas: logisticsFormSchema.filter(s => s.field !== 'trackingNo'),
    showActionButtonGroup: false,
    baseColProps: { xs: 24, sm: 12, md: 8 },
    labelWidth: 80,
  });



  // ==================== 关键：监听物流单号变化 ====================
  watch(logisticsFormTrackingNo, async (val) => {
    console.log('=== watch 物流单号 ===', val);
    if (!val || val.trim() === '') return;

    const code = val.trim();
    await setFieldsValue({ trackingNo: code });
    await handleLogisticsScan(code);
  });

  // ==================== Modal 初始化 ====================
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetAll();
    orderInfo.orderId = data.orderId;
    orderInfo.orderNo = data.orderNo;
    await loadOrderInfo(data.orderId);
    await loadOrderStocks(data.orderId);
    setModalProps({ confirmLoading: false });
  });

  async function loadOrderInfo(orderId: string) {
    const res = await getPendingLines({ orderId });
    if (!res) {
      createMessage.warning('未获取到订单数据');
      return;
    }
    if (res.order) {
      Object.assign(orderInfo, {
        customerId: res.order.customerId,
        customerName: res.order.customerName,
        consignee: res.order.deliveryConsignee,
        consigneePhone: res.order.deliveryPhone,
        consigneeAddress: res.order.deliveryAddress,
      });
    }
    orderLineData.value = res.lines || [];

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

  function onQtyChange(record: any, val: number | string) {
    const newVal = Number(val);
    console.log('数量手动修改:', record.productionBatchNo, '新值:', newVal);

    // 找到 deliveryItems 中对应行并更新
    const idx = deliveryItems.value.findIndex(
      (i) => i.stockId === record.stockId && i.productionBatchNo === record.productionBatchNo
    );

    if (idx > -1) {
      deliveryItems.value[idx].actualQty = newVal;
      console.log('已同步到 deliveryItems:', deliveryItems.value[idx]);
    }
  }

  async function loadOrderStocks(orderId: string) {
    try {
      const res = await getOrderStocks({ orderId });
      setStockData(res || []);
    } catch (err) {
      console.error('加载库存失败', err);
    }
  }

  // ==================== 产品扫码处理 ====================
  let productKeyBuffer = '';
  let productLastKeyTime = 0;
  const SCAN_THRESHOLD_MS = 80;

  function onProductKeydown(e: KeyboardEvent) {
    const now = Date.now();
    const timeDiff = now - productLastKeyTime;
    productLastKeyTime = now;

    if (e.key === 'Enter') {
      if (productKeyBuffer.length > 0 && timeDiff < SCAN_THRESHOLD_MS) {
        e.preventDefault();
        handleProductScan(productKeyBuffer);
        productKeyBuffer = '';
        return;
      }
      productKeyBuffer = '';
      return;
    }

    if (timeDiff > SCAN_THRESHOLD_MS) {
      productKeyBuffer = '';
    }
    productKeyBuffer += e.key;
  }

  async function handleProductScan(val: string) {
    if (!val?.trim()) return;
    try {
      const res = await scanCode({
        scanCode: val.trim(),
        orderId: orderInfo.orderId,
      });
      if (res.matched) {
        productScanMsg.value = res.msg || '匹配成功';
        productScanType.value = 'success';
        matchedStocks.value = res.stocks || [];
        matchedOrderLines.value = res.orderLines || [];
        setStockData(matchedStocks.value);

        // 自动添加第一条库存（扫码快捷方式）
        if (matchedStocks.value.length > 0 && matchedOrderLines.value.length > 0) {
          addStockToDelivery(matchedStocks.value[0]);
        }
      } else {
        productScanMsg.value = res?.msg || '未匹配';
        productScanType.value = 'error';
        matchedOrderLines.value = [];  // 清空
      }
    } catch (err: any) {
      productScanMsg.value = err?.message || '扫码失败';
      productScanType.value = 'error';
      matchedOrderLines.value = [];
    }
    scanCodeVal.value = '';
  }

  async function openProductScan() {
    if (typeof window === 'undefined' || !navigator.mediaDevices) {
      createMessage.error('当前环境不支持摄像头');
      return;
    }
    productScanVisible.value = true;
    await nextTick();

    productScanTip.value = '正在启动摄像头…';
    try {
      const { Html5Qrcode } = await import('html5-qrcode');
      productHtml5QrCode = new Html5Qrcode('product-scan-reader');
      await productHtml5QrCode.start(
        { facingMode: 'environment' },
        { fps: 10, qrbox: { width: 250, height: 250 } },
        (decodedText: string) => {
          console.log('产品扫码结果:', decodedText);
          scanCodeVal.value = decodedText;
          handleProductScan(decodedText);
          productScanTip.value = '识别成功：' + decodedText;
          setTimeout(() => stopProductScan(), 300);
        },
        () => {}
      );
      productScanTip.value = '请将条码对准框内';
    } catch (err: any) {
      productScanTip.value = '摄像头启动失败';
      createMessage.error('摄像头启动失败：' + (err?.message || String(err)));
    }
  }

  async function stopProductScan() {
    productScanVisible.value = false;
    if (productHtml5QrCode) {
      try { await productHtml5QrCode.stop(); productHtml5QrCode.clear(); } catch (e) {}
      productHtml5QrCode = null;
    }
  }

  // ==================== 物流单号扫码处理 ====================
  let logisticsKeyBuffer = '';
  let logisticsLastKeyTime = 0;

  function onLogisticsKeydown(e: KeyboardEvent) {
    const now = Date.now();
    const timeDiff = now - logisticsLastKeyTime;
    logisticsLastKeyTime = now;

    if (e.key === 'Enter') {
      if (logisticsKeyBuffer.length > 0 && timeDiff < SCAN_THRESHOLD_MS) {
        e.preventDefault();
        logisticsFormTrackingNo.value = logisticsKeyBuffer;
        logisticsKeyBuffer = '';
        return;
      }
      logisticsKeyBuffer = '';
      return;
    }

    if (timeDiff > SCAN_THRESHOLD_MS) {
      logisticsKeyBuffer = '';
    }
    logisticsKeyBuffer += e.key;
  }

  // 核心：物流单号识别
  async function handleLogisticsScan(code: string) {
    console.log('=== handleLogisticsScan 执行 ===', code);

    try {
      const res = await scanLogisticsCode({ trackingNo: code });
      console.log('接口返回:', res);

      if (res && res.id) {
        logisticsScanMsg.value = `识别成功：${res.companyName || '未知快递'} ${code}`;
        logisticsScanType.value = 'success';
        console.log("识别成功：", res.companyName + " " + res.companyCode + " " + res.companyType);

        // 先回填单号和公司信息
        await setFieldsValue({
          trackingNo: code,
          logisticsCompanyId: res.id,
          logisticsCompany: res.companyName,
          logisticsCompanyCode: res.companyCode,
        });

        // 延迟给字典组件赋值
        setTimeout(async () => {
          await setFieldsValue({
            logisticsType: res.companyType,
          });
          console.log('字典赋值完成:', res.companyType);
        }, 300);

      } else {
        logisticsScanMsg.value = '单号已录入，未识别快递公司，请手动选择';
        logisticsScanType.value = 'warning';
      }
    } catch (err: any) {
      logisticsScanMsg.value = '物流识别失败：' + (err?.message || '未知错误');
      logisticsScanType.value = 'error';
    }
  }

  async function openLogisticsScan() {
    if (typeof window === 'undefined' || !navigator.mediaDevices) {
      createMessage.error('当前环境不支持摄像头');
      return;
    }
    logisticsScanVisible.value = true;
    await nextTick();

    logisticsScanTip.value = '正在启动摄像头…';
    try {
      const { Html5Qrcode } = await import('html5-qrcode');
      logisticsHtml5QrCode = new Html5Qrcode('logistics-scan-reader');
      await logisticsHtml5QrCode.start(
        { facingMode: 'environment' },
        { fps: 10, qrbox: { width: 250, height: 250 } },
        (decodedText: string) => {
          console.log('物流扫码结果:', decodedText);
          logisticsFormTrackingNo.value = decodedText;
          logisticsScanTip.value = '识别成功：' + decodedText;
          setTimeout(() => stopLogisticsScan(), 300);
        },
        () => {}
      );
      logisticsScanTip.value = '请将条码对准框内';
    } catch (err: any) {
      logisticsScanTip.value = '摄像头启动失败';
      createMessage.error('摄像头启动失败：' + (err?.message || String(err)));
    }
  }

  async function stopLogisticsScan() {
    logisticsScanVisible.value = false;
    if (logisticsHtml5QrCode) {
      try { await logisticsHtml5QrCode.stop(); logisticsHtml5QrCode.clear(); } catch (e) {}
      logisticsHtml5QrCode = null;
    }
  }

  // ==================== 发货明细操作 ====================
  function addStockToDelivery(stock: any) {
    console.log('=== addStockToDelivery 被调用 ===', stock);

    if (!stock) return;

    // 查找订单明细
    let orderLine = matchedOrderLines.value?.[0];
    if (!orderLine) {
      orderLine = orderLineData.value.find(
        (line) => line.productCode === stock.goodsCode || line.productId === stock.goodsId
      );
    }

    if (!orderLine) {
      createMessage.warning('该产品不在当前订单中');
      return;
    }

    // ========== 所有变量提前定义 ==========
    const orderRemaining = Number(orderLine.orderQty || 0) - Number(orderLine.deliveredQty || 0);
    const available = Number(stock.quantity || 0) - Number(stock.lockedQty || 0);

    const currentSum = deliveryItems.value
      .filter((i) => i.sourceDetailId === orderLine.id)
      .reduce((sum, i) => sum + Number(i.actualQty || 0), 0);

    const stillNeed = orderRemaining - currentSum;

    console.log('订单剩余:', orderRemaining, '库存可用:', available, '本次已发:', currentSum, '还缺:', stillNeed);

    // ========== 校验 ==========
    if (orderRemaining <= 0) {
      createMessage.warning('该产品订单数量已发完');
      return;
    }
    if (available < 1) {
      createMessage.warning('库存可用数量不足');
      return;
    }
    if (stillNeed <= 0) {
      createMessage.warning('本次发货明细中该产品数量已达订单剩余数量');
      return;
    }

    // ========== 计算本次应发 ==========
    const addQty = Math.min(stillNeed, available);

    // 查找是否已有相同批次
    const existingIndex = deliveryItems.value.findIndex(
      (item) => item.sourceDetailId === orderLine.id
        && item.productionBatchNo === stock.batchNo
    );

    console.log('本次应发:', addQty, '是否累加:', existingIndex > -1);

    // ========== 执行添加 ==========
    if (existingIndex > -1) {
      // 同批次累加
      const afterAdd = deliveryItems.value[existingIndex].actualQty + addQty;
      if (afterAdd > orderRemaining) {
        // 保险：理论上不会触发，因为 addQty <= stillNeed
        deliveryItems.value[existingIndex].actualQty = orderRemaining - (currentSum - deliveryItems.value[existingIndex].actualQty);
        createMessage.warning('累加后超过订单数量，已自动调整');
      } else {
        deliveryItems.value[existingIndex].actualQty = afterAdd;
      }
      setDeliveryData([...deliveryItems.value]);
      createMessage.success('数量已累加');
    } else {
      // 不同批次，新增行
      if (addQty < stillNeed) {
        // 库存不够发满，提示
        createConfirm({
          iconType: 'warning',
          title: '库存不足',
          content: `订单还需 ${stillNeed}，但库存仅 ${available}，是否发 ${addQty}？`,
          onOk: () => {
            doAddNewItem(stock, orderLine, addQty);
          },
        });
      } else {
        doAddNewItem(stock, orderLine, addQty);
      }
    }
  }
  // 新增行（抽离出来复用）
  function doAddNewItem(stock: any, orderLine: any, qty: number) {
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
      unitPrice: orderLine.unitPrice || orderLine.price || 0,
      scanCode: scanCodeVal.value || stock.goodsCode,
    });
    setDeliveryData([...deliveryItems.value]);
    createMessage.success('已添加');
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

  function clearItems() {
    deliveryItems.value = [];
    setDeliveryData([]);
  }

  function handleDeleteItem(record: any) {
    const idx = deliveryItems.value.findIndex(
      (i) => i.stockId === record.stockId && i.productionBatchId === record.productionBatchId
    );
    if (idx > -1) {
      deliveryItems.value.splice(idx, 1);
      setDeliveryData([...deliveryItems.value]);
    }
  }

  // ==================== 提交 ====================
  async function handleSubmit() {
    if (deliveryItems.value.length === 0) {
      createMessage.warning('请先扫码添加发货产品');
      return;
    }
    try {
      setModalProps({ confirmLoading: true });
      const logistics = await validate();
      const params = {
        ...logistics,
        orderId: orderInfo.orderId,
        items: deliveryItems.value.map((item) => ({
          sourceDetailId: item.sourceDetailId,
          stockId: item.stockId,
          actualQty: item.actualQty,
          scanCode: item.scanCode,
        })),
      };
      await scanDeliver(params);
      createMessage.success('发货成功');
      emit('success');
      closeModal();
    } catch (err: any) {
      createMessage.error(err?.message || '提交失败');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }

  function formatNow(): string {
    const now = new Date();
    const fmt = (n: number) => String(n).padStart(2, '0');
    return `${now.getFullYear()}-${fmt(now.getMonth() + 1)}-${fmt(now.getDate())} ${fmt(now.getHours())}:${fmt(now.getMinutes())}:${fmt(now.getSeconds())}`;
  }

  async function resetAll() {
    deliveryItems.value = [];
    setDeliveryData([]);
    setStockData([]);
    productScanMsg.value = '';
    logisticsScanMsg.value = '';
    scanCodeVal.value = '';
    logisticsFormTrackingNo.value = '';
    matchedStocks.value = [];
    matchedOrderLines.value = [];
    await resetFields();
  }
</script>

<style lang="less" scoped>
  .delivery-modal-body {
    max-height: calc(90vh - 80px);
    overflow-y: auto;
    padding-right: 4px;
  }

  .scan-input {
    display: flex;
    width: 100%;
    align-items: center;

    .ant-input {
      flex: 1;
    }

    .ant-btn {
      flex-shrink: 0;
    }
  }

  .compact-form-item {
    margin-bottom: 0 !important;

    :deep(.ant-form-item-label) {
      padding-bottom: 0;
      line-height: 28px;
    }
    :deep(.ant-form-item-control) {
      line-height: 1.5;
    }
  }

  .scan-box {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .scan-reader {
    width: 100%;
    max-width: 380px;
    min-height: 240px;
    background: #000;
    border-radius: 4px;
    overflow: hidden;
  }

  .scan-tip {
    margin-top: 8px;
    color: #666;
    font-size: 13px;
    text-align: center;
  }

  // 手机端适配
  @media screen and (max-width: 768px) {
    .delivery-modal-body {
      :deep(.ant-descriptions) {
        .ant-descriptions-row {
          display: flex;
          flex-wrap: wrap;
        }
        .ant-descriptions-item {
          flex: 1 1 50%;
          min-width: 140px;
          padding-bottom: 4px;
        }
      }

      :deep(.ant-form-item) {
        margin-bottom: 8px;
      }

      :deep(.ant-card) {
        margin-bottom: 8px;
      }

      :deep(.ant-table-wrapper) {
        overflow-x: auto;
      }

      :deep(.ant-card-head) {
        padding: 8px 12px;
        min-height: 36px;
      }

      :deep(.ant-card-head-title) {
        font-size: 14px;
        padding: 4px 0;
      }
    }

    .scan-input {
      .ant-input {
        min-width: 0;
      }
    }
  }

  .logistics-input-row {
    display: flex;
    align-items: center;  // 垂直居中
    min-height: 32px;

    .logistics-label {
      flex-shrink: 0;
      width: 80px;
      text-align: right;
      padding-right: 8px;
      color: rgba(0, 0, 0, 0.85);
      font-size: 14px;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      height: 32px;
    }

    .ant-input {
      height: 32px;
    }

    .ant-btn {
      height: 32px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
    }
  }
</style>

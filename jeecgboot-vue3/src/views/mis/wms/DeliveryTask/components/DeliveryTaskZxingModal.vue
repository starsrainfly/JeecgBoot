<<template>
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
            <ZxingScanInput
              v-model:value="scanCodeVal"
              @change="handleProductScan"
              placeholder="请扫描产品二维码"
            />
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
              <ZxingScanInput
                v-model:value="logisticsNo"
                @change="handleLogisticsScan"
                @blur ="handleLogisticsScan"
                placeholder="扫描或输入"
                style="flex: 1;"
              />
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
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, reactive, watch, nextTick, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable } from '/@/components/Table';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { Icon } from '/@/components/Icon';
  import { ZxingScanInput } from '/@/components/Scan';

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
    companyId:'',
    companyCode:'',
    companyName:'',
  });

  // ==================== 产品扫码 ====================
  const scanCodeVal = ref('');
  const productScanMsg = ref('');
  const productScanType = ref<'success' | 'error' | 'warning'>('success');
  const matchedStocks = ref<any[]>([]);
  const matchedOrderLines = ref<any[]>([]);

  // ==================== 物流单号扫码 ====================
  const logisticsNo = ref('');
  const logisticsScanMsg = ref('');
  const logisticsScanType = ref<'success' | 'error' | 'warning'>('success');
  const identifying = ref(false);

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
        companyId: res.order.companyId,
        companyCode: res.order.companyCode,
        companyName: res.order.companyName,
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
      companyId: orderInfo.companyId,
      companyCode: orderInfo.companyCode,
      companyName: orderInfo.companyName,
      deliveryTime: formatNow(),
    });
  }

  function onQtyChange(record: any, val: number | string) {
    const newVal = Number(val);
    console.log('数量手动修改:', record.productionBatchNo, '新值:', newVal);

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

        if (matchedStocks.value.length > 0 && matchedOrderLines.value.length > 0) {
          addStockToDelivery(matchedStocks.value[0]);
        }
      } else {
        productScanMsg.value = res?.msg || '未匹配';
        productScanType.value = 'error';
        matchedOrderLines.value = [];
      }
    } catch (err: any) {
      productScanMsg.value = err?.message || '扫码失败';
      productScanType.value = 'error';
      matchedOrderLines.value = [];
    }
    scanCodeVal.value = '';
  }

  // ==================== 物流单号扫码处理 ====================
  async function handleLogisticsScan(code: string) {
    console.log('=== handleLogisticsScan 执行 ===', code);
    if (identifying.value) return;
    if (!code || code.trim().length < 5) return;   // 物流单号至少5位才识别
    identifying.value = true;

    try {
      const res = await scanLogisticsCode({ trackingNo: code });
      console.log('接口返回:', res);

      if (res && res.id) {
        logisticsScanMsg.value = `识别成功：${res.companyName || '未知快递'} ${code}`;
        logisticsScanType.value = 'success';
        console.log("识别成功：", res.companyName + " " + res.companyCode + " " + res.companyType);

        await setFieldsValue({
          trackingNo: code,
          logisticsCompanyId: res.id,
          logisticsCompany: res.companyName,
          logisticsCompanyCode: res.companyCode,
        });

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
    } finally {
      identifying.value = false;
    }
  }

  // ==================== 发货明细操作 ====================
  function addStockToDelivery(stock: any) {
    console.log('=== addStockToDelivery 被调用 ===', stock);

    if (!stock) return;

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

    const orderRemaining = Number(orderLine.orderQty || 0) - Number(orderLine.deliveredQty || 0);
    const available = Number(stock.quantity || 0) - Number(stock.lockedQty || 0);

    const currentSum = deliveryItems.value
      .filter((i) => i.sourceDetailId === orderLine.id)
      .reduce((sum, i) => sum + Number(i.actualQty || 0), 0);

    const stillNeed = orderRemaining - currentSum;

    console.log('订单剩余:', orderRemaining, '库存可用:', available, '本次已发:', currentSum, '还缺:', stillNeed);

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

    const addQty = Math.min(stillNeed, available);

    const existingIndex = deliveryItems.value.findIndex(
      (item) => item.sourceDetailId === orderLine.id
        && item.productionBatchNo === stock.batchNo
    );

    console.log('本次应发:', addQty, '是否累加:', existingIndex > -1);

    if (existingIndex > -1) {
      const afterAdd = deliveryItems.value[existingIndex].actualQty + addQty;
      if (afterAdd > orderRemaining) {
        deliveryItems.value[existingIndex].actualQty = orderRemaining - (currentSum - deliveryItems.value[existingIndex].actualQty);
        createMessage.warning('累加后超过订单数量，已自动调整');
      } else {
        deliveryItems.value[existingIndex].actualQty = afterAdd;
      }
      setDeliveryData([...deliveryItems.value]);
      createMessage.success('数量已累加');
    } else {
      if (addQty < stillNeed) {
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
      unitPrice: orderLine.unitPrice  || 0,
      scanCode: scanCodeVal.value || stock.goodsCode,
      unit:stock.unit || 'kg',
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
        sourceOrderId: orderInfo.orderId,
        sourceOrderNo: orderInfo.orderNo,
        customerId: orderInfo.customerId,
        customerName: orderInfo.customerName,
        consignee: orderInfo.consignee,
        consigneePhone: orderInfo.consigneePhone,
        consigneeAddress: orderInfo.consigneeAddress,
        companyId: orderInfo.companyId,
        companyCode: orderInfo.companyCode,
        companyName: orderInfo.companyName,
        logisticsNo: logisticsNo.value,
        deliveryItems: deliveryItems.value.map((item) => ({
          sourceDetailId: item.sourceDetailId,
          stockId: item.stockId,
          goodsId: item.goodsId,
          goodsCode: item.goodsCode,
          goodsName: item.goodsName,
          goodsSpec: item.goodsSpec,
          unit: item.unit,
          actualQty: item.actualQty,
          unitPrice: item.unitPrice,
          productionBatchId: item.productionBatchId,
          productionBatchNo: item.productionBatchNo,
          productionDate: item.productionDate ? formatDate(item.productionDate) : null,
          expiryDate: item.expiryDate ? formatDate(item.expiryDate) : null,
          warehouseId: item.warehouseId,
          warehouseName: item.warehouseName,
          scanCode: item.scanCode,
          remark: item.remark,
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

  function formatDate(date: any): string {
    if (!date) return '';
    if (typeof date === 'string') return date;
    if (date instanceof Date) {
      return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,'0')}-${String(date.getDate()).padStart(2,'0')}`;
    }
    return String(date);
  }

  async function resetAll() {
    deliveryItems.value = [];
    setDeliveryData([]);
    setStockData([]);
    productScanMsg.value = '';
    logisticsScanMsg.value = '';
    scanCodeVal.value = '';
    logisticsNo.value = '';
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
    align-items: center;
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

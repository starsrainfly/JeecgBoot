<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="`打印送货单 - ${deliveryData?.deliveryNo || ''}`"
    :width="900"
    :footer="null"
    :centered="true"
    class="print-preview-modal"
  >
    <!-- 工具栏 -->
    <div class="print-toolbar">
      <div class="toolbar-left">
        <a-switch
          v-model:checked="showPrice"
          checked-children="显示单价"
          un-checked-children="隐藏单价"
          @change="handleTogglePrice"
        />
        <span class="hint">（部分客户要求不显示单价）</span>
      </div>
      <div class="toolbar-right">
        <a-button type="primary" @click="handleDirectPrint" preIcon="ant-design:printer-outlined">
          直接打印
        </a-button>
        <a-button @click="handleClose" style="margin-left: 8px">
          关闭
        </a-button>
      </div>
    </div>

    <!-- 打印区域：A4/自定义纸张 -->
    <div class="print-page" id="printArea" :class="{ 'hide-price': !showPrice }">
      <!-- 公司抬头 -->
      <div class="company-header">
        <div class="company-logo">
          <img src="/logo.png" alt="LOGO" v-if="false" />
          <div class="logo-text">QIFU</div>
        </div>
        <div class="company-info">
          <h2 class="company-name">{{ deliveryData?.companyName || '惠州骐富新材料技术有限公司' }}</h2>
          <p class="company-address">
            地址：{{ deliveryData?.companyAddress || '惠州市博罗县石湾镇源头工业区A栋' }}
            &nbsp;&nbsp;Tel：{{ deliveryData?.companyPhone || '0752-6919972' }}
            &nbsp;&nbsp;Fax：{{ deliveryData?.companyFax || '0752-6919940' }}
          </p>
        </div>
      </div>

      <!-- 单据标题 -->
      <div class="doc-title">送 货 单</div>

      <!-- 客户信息 -->
      <div class="customer-info">
        <!-- 第一行 -->
        <div class="info-row">
          <div class="info-item left wide">
            <span class="label">客户名称：</span>
            <span class="value">{{ deliveryData?.customerName }}</span>
          </div>
          <div class="info-item left">
            <span class="label">订 单 号：</span>
            <span class="value">{{ deliveryData?.sourceOrderNo }}</span>
          </div>
        </div>
        <!-- 第二行 -->
        <div class="info-row">
          <div class="info-item left wide">
            <span class="label">收货地址：</span>
            <span class="value">{{ deliveryData?.consigneeAddress }}</span>
          </div>
          <div class="info-item left">
            <span class="label">送货单号：</span>
            <span class="value">{{ deliveryData?.deliveryNo }}</span>
          </div>
        </div>
        <!-- 第三行 -->
        <div class="info-row">
          <div class="info-item left wide">
            <span class="label">联系电话：</span>
            <span class="value">{{ deliveryData?.consigneePhone }}</span>
          </div>
          <div class="info-item left">
            <span class="label">日　　期：</span>
            <span class="value">{{ formatDate(deliveryData?.deliveryTime) }}</span>
          </div>
        </div>
      </div>

      <!-- 明细表格 -->
      <table class="detail-table">
        <thead>
        <tr>
          <th class="col-seq">序号</th>
          <th class="col-pn">客户料号(PN)</th>
          <th class="col-code">产品编号</th>
          <th class="col-name">产品名称</th>
          <th class="col-unit">单位</th>
          <th class="col-qty">数量</th>
          <th class="col-price" v-if="showPrice">单价</th>
          <th class="col-remark">备注</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(item, index) in detailList" :key="index">
          <td class="center">{{ index + 1 }}</td>
          <td>{{ item.customerPn || item.goodsCode }}</td>
          <td>{{ item.goodsCode }}</td>
          <td>{{ item.goodsName }}</td>
          <td class="center">{{ item.unit }}</td>
          <td class="center">{{ item.actualQty }}</td>
          <td class="right col-price" v-if="showPrice">{{ formatPrice(item.unitPrice) }}</td>
          <td>1{{ item.remark || '&nbsp;'}}</td>
        </tr>
        <!-- 空行补齐（最少显示5行） -->
        <tr v-for="n in Math.max(0, 5 - detailList.length)" :key="'empty-' + n" class="empty-row">
          <td class="center">&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td class="col-price" v-if="showPrice">&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
        </tbody>
        <tfoot>
        <tr class="total-row">
          <td>&nbsp;</td>
          <td class="center">合计：</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td class="center">{{ totalQty }}</td>
          <td class="right col-price" v-if="showPrice">{{ totalAmount }}</td>
          <td>&nbsp;</td>
        </tr>
        </tfoot>
      </table>

      <!-- 声明 -->
      <div class="declaration">
        <p>声明：1.收到货物，如有疑问，请于三天内检查提出，否则视同上述所载事项；</p>
        <p>　　　2.在业务执行过程中，如对货品质量有疑问，在原包装没有拆损的基础上供方在5天内可以接受换货或退货；</p>
        <p>　　　3.请保存此单，作为退换货物凭证，无单恕不受理；</p>
      </div>

      <!-- 签名区 -->
      <div class="signature-area">
        <div class="sign-box">
          <p>送货单位（签名）：</p>
          <p>（盖章）：</p>
        </div>
        <div class="sign-box">
          <p>收货单位（签名）：</p>
          <p>（盖章）：</p>
        </div>
      </div>
    </div>
  </BasicModal>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';

  const emit = defineEmits(['register', 'close']);

  const deliveryData = ref(null);
  const detailList = ref([]);
  const showPrice = ref(false);

  // 计算合计
  const totalQty = computed(() => {
    return detailList.value.reduce((sum, item) => sum + Number(item.actualQty || 0), 0);
  });

  const totalAmount = computed(() => {
    return detailList.value.reduce((sum, item) => sum + Number(item.detailAmount || (item.unitPrice * item.actualQty) || 0), 0).toFixed(2);
  });

  const [registerModal, { closeModal }] = useModalInner((data) => {
    deliveryData.value = data.record || {};
    detailList.value = data.detailList || [];
    showPrice.value = data.showPrice || false;
  });

  // 切换单价显示
  function handleTogglePrice() {
    // 纯前端切换，无需请求
  }

  // 直接打印
  function handleDirectPrint() {
    const printContent = document.getElementById('printArea');
    if (!printContent) return;

    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>送货单 - ${deliveryData.value?.deliveryNo}</title>
        <style>
          @page { size: A4 landscape; margin: 10mm; }
          body { margin: 0; padding: 0; font-family: 'SimSun', '宋体', serif; font-size: 12pt; }
          ${getPrintStyles()}
        </style>
      </head>
      <body>
        ${printContent.outerHTML}
        <script>
          window.onload = function() {
            window.print();
            // 打印后关闭（可选）
            // window.close();
          };
        <\/script>
      </body>
      </html>
    `);
    printWindow.document.close();
  }

  // 关闭弹窗
  function handleClose() {
    closeModal();
  }

  // 格式化日期
  function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  }

  // 格式化金额
  function formatPrice(price) {
    if (!price || price === 0) return '';
    return Number(price).toFixed(4);
  }

  // 打印样式（与屏幕显示一致）
  function getPrintStyles() {
    return `
      .print-page { width: 100%; padding: 0; }
      .company-header { display: flex; align-items: center; border-bottom: 2px solid #333; padding-bottom: 8px; margin-bottom: 12px; }
      .logo-text { font-size: 24pt; font-weight: bold; color: #0066cc; margin-right: 20px; }
      .company-name { font-size: 18pt; font-weight: bold; margin: 0 0 4px 0; text-align: center; }
      .company-address { font-size: 10pt; margin: 0; text-align: center; }
      .doc-title { font-size: 20pt; font-weight: bold; text-align: center; margin: 12px 0; letter-spacing: 8px; }
      .customer-info { margin-bottom: 12px; }
      .info-row { display: flex; margin-bottom: 6px; }
      .info-item { flex: 1; display: flex; align-items: center; }
      .info-item.wide { flex: 2; }
      .label { font-size: 11pt; white-space: nowrap; }
      .value { flex: 1; border-bottom: 1px solid #333; padding: 0 4px; margin: 0 8px; min-height: 18px; }
      .detail-table { width: 100%; border-collapse: collapse; margin-bottom: 12px; }
      .detail-table th, .detail-table td { border: 1px solid #333; padding: 6px 4px; font-size: 10.5pt; }
      .detail-table th { background: #f5f5f5; font-weight: bold; text-align: center; }
      .center { text-align: center; }
      .right { text-align: right; }
      .col-seq { width: 40px; }
      .col-pn { width: 100px; }
      .col-code { width: 90px; }
      .col-name { width: auto; }
      .col-unit { width: 50px; }
      .col-qty { width: 60px; }
      .col-price { width: 80px; }
      .col-remark { width: 100px; }
      .total-row td { font-weight: bold; border-top: 2px solid #333; }
      .empty-row td { height: 28px; }
      .declaration { font-size: 9pt; margin-bottom: 20px; }
      .declaration p { margin: 2px 0; }
      .signature-area { display: flex; justify-content: space-between; margin-top: 30px; }
      .sign-box { width: 45%; }
      .sign-box p { margin: 4px 0; font-size: 11pt; }

     .hide-price .col-price {
      display: none !important;
    }
    `;
  }
</script>

<style lang="less" scoped>
  .print-preview-modal {
    :deep(.ant-modal-body) {
      background: #e8e8e8;
      padding: 16px;
      max-height: 80vh;
      overflow-y: auto;
    }
  }

  .print-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding: 12px 16px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 1px 2px rgba(0,0,0,0.1);

    .hint {
      color: #999;
      font-size: 12px;
      margin-left: 8px;
    }
  }

  /* 打印页面样式 */
  .print-page {
    width: 210mm;  /* A4 宽度 */
    min-height: 148mm; /* 半张A4高度，或根据内容 */
    margin: 0 auto;
    background: #fff;
    padding: 15mm;
    box-shadow: 0 2px 8px rgba(0,0,0,0.15);

    &.hide-price {
      .col-price { display: none; }
    }
  }

  .company-header {
    display: flex;
    align-items: center;
    border-bottom: 2px solid #333;
    padding-bottom: 8px;
    margin-bottom: 12px;

    .logo-text {
      font-size: 28px;
      font-weight: bold;
      color: #0066cc;
      margin-right: 20px;
    }

    .company-name {
      font-size: 20px;
      font-weight: bold;
      margin: 0 0 4px 0;
      text-align: center;
    }

    .company-address {
      font-size: 11px;
      margin: 0;
      text-align: center;
      color: #666;
    }
  }

  .doc-title {
    font-size: 22px;
    font-weight: bold;
    text-align: center;
    margin: 12px 0;
    letter-spacing: 10px;
  }

  .customer-info {
    margin-bottom: 12px;

    .info-row {
      display: flex;
      margin-bottom: 6px;
    }

    .info-item {
      flex: 1;
      display: flex;
      align-items: center;

      &.wide { flex: 2; }
    }

    .label {
      font-size: 12px;
      white-space: nowrap;
      font-weight: 500;
    }

    .value {
      flex: 1;
      border-bottom: 1px solid #333;
      padding: 0 4px;
      margin: 0 8px;
      min-height: 18px;
      font-size: 12px;
    }
  }

  .detail-table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 12px;

    th, td {
      border: 1px solid #333;
      padding: 6px 4px;
      font-size: 11px;
    }

    th {
      background: #f5f5f5;
      font-weight: bold;
      text-align: center;
    }

    .center { text-align: center; }
    .right { text-align: right; }

    .col-seq { width: 40px; }
    .col-pn { width: 100px; }
    .col-code { width: 90px; }
    .col-name { min-width: 150px; }
    .col-unit { width: 50px; }
    .col-qty { width: 60px; }
    .col-price { width: 80px; }
    .col-remark { width: 100px; }

    .total-row td {
      font-weight: bold;
      border-top: 2px solid #333;
    }

    .empty-row td { height: 28px; }
  }

  .declaration {
    font-size: 10px;
    margin-bottom: 20px;
    color: #333;

    p { margin: 2px 0; }
  }

  .signature-area {
    display: flex;
    justify-content: space-between;
    margin-top: 30px;

    .sign-box {
      width: 45%;

      p {
        margin: 4px 0;
        font-size: 12px;
      }
    }
  }

  /* 隐藏单价时的样式 */
  .hide-price {
    .col-price { display: none; }
  }
</style>

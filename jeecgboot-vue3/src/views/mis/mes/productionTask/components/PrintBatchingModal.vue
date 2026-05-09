<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="配料工单打印"
    width="1100"
    wrapClassName="batching-print-modal"
    :showOkBtn="false"
    :cancelText="'关闭'"
    :zIndex="2000"
  >
    <div class="print-container" ref="printRef" id="batchingPrintContent">
      <!-- 公司抬头 -->
      <div class="company-header">
        <h2>{{ printData.companyName || '惠州骐富新材料技术有限公司' }}</h2>
      </div>

      <!-- 基本信息 -->
      <div class="info-section">
        <table class="info-table">
          <tbody>
          <tr>
            <td class="label" style="width: 10%;">工作单号：</td>
            <td style="width: 15%; white-space: nowrap; text-overflow: clip;">{{ printData.taskNo }}</td>
            <td class="label" style="width: 10%;">生产批号：</td>
            <td style="width: 15%; white-space: nowrap; text-overflow: clip;">{{ printData.batchNo }}</td>
            <td class="label" style="width: 10%;">客户编号：</td>
            <td style="width: 15%; white-space: nowrap; text-overflow: clip;">{{ printData.customerCode || '-' }}</td>
            <td class="label" style="width: 10%;">生产数量：</td>
            <td style="width: 15%; white-space: nowrap; text-overflow: clip;">{{ printData.plannedQty }} Kg</td>
          </tr>
          <tr>
            <td class="label" style="width: 10%;">生产日期：</td>
            <td style="width: 15%; white-space: nowrap; text-overflow: clip;">{{ formatDate(printData.productionDate) }}</td>
            <td class="label" style="width: 10%;">产品名称：</td>
            <td style="width: 15%; white-space: nowrap; text-overflow: clip;">{{ printData.productName }}</td>
            <td class="label" style="width: 10%;">单釜数量：</td>
            <td style="width: 15%; white-space: nowrap; text-overflow: clip;">{{ printData.batchSize }} Kg</td>
            <td class="label" style="width: 10%;">釜数：</td>
            <td style="width: 15%; white-space: nowrap; text-overflow: clip;">{{ printData.batchCount }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 技术要求 -->
      <div class="section" v-if="printData.technics">
        <div class="section-title">技术要求：</div>
        <div class="section-content pre-wrap">{{ printData.technics }}</div>
      </div>

      <!-- 生产工艺 -->
      <div class="section">
        <div class="section-title">生产工艺：</div>
        <div class="process-steps">
          <div v-for="(step, index) in printData.processSteps" :key="index" class="step-item">
            {{ step.stepSeq }}、{{ step.stepDesc }}
          </div>
        </div>
        <!-- 工单补充说明 -->
        <div class="task-desc" v-if="printData.taskDesc">
          <div class="sub-title">【本工单补充说明】</div>
          <div class="pre-wrap">{{ printData.taskDesc }}</div>
        </div>
      </div>

      <!-- 配料明细表 -->
      <div class="section">
        <div class="section-title">配料明细：</div>
        <table class="material-table">
          <thead>
          <tr>
            <th style="width: 40px;">序号</th>
            <th style="width: 100px;">物料编码</th>
            <th style="width: 140px;">物料名称</th>
            <th style="width: 80px;">型号规格</th>
            <th style="width: 90px;">总配置(Kg)</th>
            <th style="width: 90px;">实际重量(Kg)</th>
            <th style="width: 110px;">物料批号</th>
            <th style="width: 70px;">配料人</th>
            <th style="width: 70px;">确认人</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(item, index) in printData.materialList" :key="index">
            <td>{{ item.serialNo }}</td>
            <td>{{ item.materialCode }}</td>
            <td>{{ item.materialName }}</td>
            <td>{{ item.materialSpec || '--' }}</td>
            <td>{{ formatNumber(item.plannedQty) }}</td>
            <td></td>
            <td>{{ item.materialBatchNo || '' }}</td>
            <td></td>
            <td></td>
          </tr>
          <tr class="total-row">
            <td colspan="4" style="text-align: center; font-weight: bold;">合计：</td>
            <td style="font-weight: bold;">{{ formatNumber(printData.totalPlannedQty) }}</td>
            <td colspan="4"></td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 注意事项 -->
      <div class="section" v-if="printData.notes">
        <div class="section-title">注意事项：</div>
        <div class="notes-content pre-wrap">{{ printData.notes }}</div>
      </div>

      <!-- 工艺记录 -->
      <div class="section">
        <div class="section-title">工艺记录：</div>
        <div class="record-lines">
          <div class="record-line"></div>
          <div class="record-line"></div>
        </div>
      </div>

      <!-- 签名区域 -->
      <div class="sign-section">
        <div class="sign-item">
          <span>生产签名：</span>
          <span class="sign-line"></span>
        </div>
        <div class="sign-item">
          <span>主管签名：</span>
          <span class="sign-line"></span>
        </div>
        <div class="sign-item">
          <span>质检签名：</span>
          <span class="sign-line"></span>
        </div>
        <div class="sign-item">
          <span>制单人：{{ printData.createBy }}</span>
        </div>
      </div>
    </div>

    <!-- 打印按钮 -->
    <template #footer>
      <a-button type="primary" @click="handlePrint" preIcon="ant-design:printer-outlined">
        打印
      </a-button>
      <a-button @click="closeModal">关闭</a-button>
    </template>
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, reactive} from 'vue';
  import {BasicModal, useModalInner} from '/@/components/Modal';
  import {getBatchingPrintData} from '../ProductionTask.api';
  import {useMessage} from '/@/hooks/web/useMessage';
  import dayjs from 'dayjs';
  import { printJS } from '/@/hooks/web/usePrintJS';

  const emit = defineEmits(['register', 'success']);
  const {createMessage} = useMessage();

  const printRef = ref();
  const printData = reactive({
    companyName: '',
    taskNo: '',
    batchNo: '',
    orderNo: '',
    customerCode: '',
    customerName: '',
    productCode: '',
    productName: '',
    productColor: '',
    plannedQty: 0,
    batchSize: 0,
    batchCount: 1,
    productionDate: null,
    technics: '',
    taskDesc: '',
    processSteps: [],
    materialList: [],
    totalPlannedQty: 0,
    notes: '',
    createBy: ''
  });

  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
   // setModalProps({confirmLoading: true});
    setModalProps({
      width: '1100px',
      wrapClassName: 'batching-print-modal'  // 双重保险
    });
    try {
      const res = await getBatchingPrintData({taskId: data.taskId});
      Object.assign(printData, res);
    } finally {
      setModalProps({confirmLoading: false});
    }
  });

  function formatDate(date) {
    return date ? dayjs(date).format('YYYY-MM-DD') : '';
  }

  function formatNumber(num) {
    return num ? Number(num).toFixed(4) : '0.0000';
  }

  function handlePrint() {
    printJS({
      printable: 'batchingPrintContent',  // DOM id
      type: 'html',
      // 打印样式（覆盖页面样式）
      style: `
      @page { size: A4; margin: 10mm; }
      body { font-family: 'SimSun', '宋体', serif; font-size: 10.5pt; width: 190mm; margin: 0 auto; padding: 5mm; }
      .company-header { text-align: center; margin-bottom: 8mm; }
      .company-header h2 { font-size: 16pt; font-weight: bold; letter-spacing: 2px; }
      .info-table { width: 100%; border-collapse: collapse; table-layout: fixed; margin-bottom: 5mm; font-size: 8.5pt; }
      .info-table td { padding: 2mm 1.5mm; border: 0.5pt solid #333; vertical-align: middle; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
      .info-table .label { font-weight: bold; background: #f5f5f5; text-align: right; text-overflow: clip; }
      .section { margin-bottom: 4mm; }
      .section-title { font-weight: bold; font-size: 11pt; margin-bottom: 2mm; }
      .section-content { font-size: 10pt; line-height: 1.6; }
      .pre-wrap { white-space: pre-wrap; word-wrap: break-word; }
      .process-steps { font-size: 10pt; line-height: 1.6; }
      .step-item { margin-bottom: 1mm; }
      .task-desc { margin-top: 3mm; padding: 2mm; background: #fafafa; border: 0.5pt dashed #999; }
      .sub-title { font-weight: bold; font-size: 10pt; color: #333; margin-bottom: 1mm; }
      .material-table { width: 100%; border-collapse: collapse; font-size: 9pt; margin-top: 2mm; }
      .material-table th, .material-table td { border: 0.5pt solid #333; padding: 2mm; text-align: center; vertical-align: middle; }
      .material-table th { background: #f0f0f0; font-weight: bold; font-size: 9pt; }
      .material-table td { height: 8mm; }
      .total-row td { font-weight: bold; background: #f5f5f5; }
      .notes-content { font-size: 9pt; font-style: italic; color: #333; line-height: 1.5; padding: 2mm; background: #fafafa; border-left: 2pt solid #1890ff; }
      .record-lines { margin-top: 2mm; }
      .record-line { border-bottom: 0.5pt solid #333; height: 10mm; margin-bottom: 2mm; }
      .sign-section { display: flex; justify-content: space-between; margin-top: 8mm; padding-top: 3mm; border-top: 0.5pt solid #ccc; font-size: 9pt; }
      .sign-item { display: flex; align-items: center; }
      .sign-line { display: inline-block; width: 30mm; border-bottom: 0.5pt solid #333; margin-left: 2mm; }
    `,
      scanStyles: false,  // 不扫描页面样式，只用上面传入的 style
    });
  }

  // function handlePrint() {
  //   const printContent = printRef.value.innerHTML;
  //   const printWindow = window.open('', '_blank');
  //   printWindow.document.write(`
  //     <!DOCTYPE html>
  //     <html>
  //       <head>
  //         <title>配料工单 - ${printData.batchNo}</title>
  //         <style>
  //           @page {
  //             size: A4;
  //             margin: 10mm;
  //           }
  //           * {
  //             margin: 0;
  //             padding: 0;
  //             box-sizing: border-box;
  //           }
  //           body {
  //             font-family: 'SimSun', '宋体', serif;
  //             font-size: 10.5pt;
  //             line-height: 1.4;
  //             color: #000;
  //             width: 190mm;
  //             margin: 0 auto;
  //             padding: 5mm;
  //           }
  //           .company-header {
  //             text-align: center;
  //             margin-bottom: 8mm;
  //           }
  //           .company-header h2 {
  //             font-size: 16pt;
  //             font-weight: bold;
  //             letter-spacing: 2px;
  //           }
  //           .info-table {
  //             width: 100%;
  //             border-collapse: collapse;
  //             table-layout: fixed;
  //             margin-bottom: 5mm;
  //             font-size: 8.5pt;
  //           }
  //           .info-table td {
  //             padding: 2mm 1.5mm;
  //             border: 0.5pt solid #333;
  //             vertical-align: middle;
  //             white-space: nowrap;
  //             overflow: hidden;
  //             text-overflow: ellipsis;
  //           }
  //           .info-table .label {
  //             font-weight: bold;
  //             background: #f5f5f5;
  //             text-align: right;
  //              /* 标签列不要省略号，确保显示完整 */
  //             text-overflow: clip;
  //           }
  //           .section {
  //             margin-bottom: 4mm;
  //           }
  //           .section-title {
  //             font-weight: bold;
  //             font-size: 11pt;
  //             margin-bottom: 2mm;
  //           }
  //           .section-content {
  //             font-size: 10pt;
  //             line-height: 1.6;
  //           }
  //           .pre-wrap {
  //             white-space: pre-wrap;
  //             word-wrap: break-word;
  //           }
  //           .process-steps {
  //             font-size: 10pt;
  //             line-height: 1.6;
  //           }
  //           .step-item {
  //             margin-bottom: 1mm;
  //           }
  //           .task-desc {
  //             margin-top: 3mm;
  //             padding: 2mm;
  //             background: #fafafa;
  //             border: 0.5pt dashed #999;
  //           }
  //           .sub-title {
  //             font-weight: bold;
  //             font-size: 10pt;
  //             color: #333;
  //             margin-bottom: 1mm;
  //           }
  //           .material-table {
  //             width: 100%;
  //             border-collapse: collapse;
  //             font-size: 9pt;
  //             margin-top: 2mm;
  //           }
  //           .material-table th, .material-table td {
  //             border: 0.5pt solid #333;
  //             padding: 2mm;
  //             text-align: center;
  //             vertical-align: middle;
  //           }
  //           .material-table th {
  //             background: #f0f0f0;
  //             font-weight: bold;
  //             font-size: 9pt;
  //           }
  //           .material-table td {
  //             height: 8mm;
  //           }
  //           .total-row td {
  //             font-weight: bold;
  //             background: #f5f5f5;
  //           }
  //           .notes-content {
  //             font-size: 9pt;
  //             font-style: italic;
  //             color: #333;
  //             line-height: 1.5;
  //             padding: 2mm;
  //             background: #fafafa;
  //             border-left: 2pt solid #1890ff;
  //           }
  //           .record-lines {
  //             margin-top: 2mm;
  //           }
  //           .record-line {
  //             border-bottom: 0.5pt solid #333;
  //             height: 10mm;
  //             margin-bottom: 2mm;
  //           }
  //           .sign-section {
  //             display: flex;
  //             justify-content: space-between;
  //             margin-top: 8mm;
  //             padding-top: 3mm;
  //             border-top: 0.5pt solid #ccc;
  //             font-size: 9pt;
  //           }
  //           .sign-item {
  //             display: flex;
  //             align-items: center;
  //           }
  //           .sign-line {
  //             display: inline-block;
  //             width: 30mm;
  //             border-bottom: 0.5pt solid #333;
  //             margin-left: 2mm;
  //           }
  //           @media print {
  //             body {
  //               margin: 0;
  //               padding: 0;
  //             }
  //             .no-print {
  //               display: none !important;
  //             }
  //           }
  //         </style>
  //       </head>
  //       <body>
  //         ${printContent}
  //       </body>
  //     </html>
  //   `);
  //   printWindow.document.close();
  //   printWindow.focus();
  //   setTimeout(() => {
  //     printWindow.print();
  //     printWindow.close();
  //   }, 300);
  // }
</script>

<style lang="less" scoped>
  :deep(.ant-modal) {
    width: 1100px !important;
    max-width: 95vw;
  }

  .print-container {
    padding: 15px;
    background: #fff;
    font-family: 'SimSun', '宋体', serif;
  }

  .company-header {
    text-align: center;
    margin-bottom: 15px;
    h2 {
      font-size: 20px;
      font-weight: bold;
      margin: 0;
      letter-spacing: 3px;
    }
  }

  .info-section {
    margin-bottom: 10px;
  }

  .info-table {
    width: 100%;
    border-collapse: collapse;
    table-layout: fixed;
    font-size: 12px;
  }

  .info-table td {
   // padding: 6px 4px;
    padding: 5px 3px;  /* 左右padding减小，给内容更多空间 */
    border: 1px solid #333;
    vertical-align: middle;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .info-table .label {
    font-weight: bold;
    background: #f0f0f0;
    text-align: right;
    font-size: 11px;
    /* 标签列不要省略号，确保显示完整 */
    text-overflow: clip;
  }

  .section {
    margin-bottom: 10px;

    .section-title {
      font-weight: bold;
      font-size: 14px;
      margin-bottom: 5px;
    }

    .section-content {
      font-size: 12px;
      line-height: 1.6;
    }
  }

  .pre-wrap {
    white-space: pre-wrap;
    word-wrap: break-word;
  }

  .process-steps {
    font-size: 12px;
    line-height: 1.6;

    .step-item {
      margin-bottom: 3px;
    }
  }

  .task-desc {
    margin-top: 8px;
    padding: 8px;
    background: #fafafa;
    border: 1px dashed #d9d9d9;
    border-radius: 4px;

    .sub-title {
      font-weight: bold;
      color: #666;
      margin-bottom: 4px;
      font-size: 12px;
    }
  }

  .material-table {
    width: 100%;
    border-collapse: collapse;
    font-size: 11px;

    th, td {
      border: 1px solid #333;
      padding: 6px 4px;
      text-align: center;
      vertical-align: middle;
    }

    th {
      background: #f0f0f0;
      font-weight: bold;
      font-size: 11px;
    }

    td {
      height: 28px;
    }

    .total-row {
      font-weight: bold;
      background: #f5f5f5;

      td {
        border-top: 2px solid #333;
      }
    }
  }

  .notes-content {
    font-style: italic;
    color: #666;
    line-height: 1.5;
    padding: 8px;
    background: #fafafa;
    border-left: 3px solid #1890ff;
    font-size: 11px;
  }

  .record-lines {
    .record-line {
      border-bottom: 1px solid #333;
      height: 30px;
      margin-bottom: 5px;
    }
  }

  .sign-section {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
    padding-top: 10px;
    border-top: 1px solid #eee;
    font-size: 12px;

    .sign-item {
      display: flex;
      align-items: center;

      .sign-line {
        display: inline-block;
        width: 100px;
        border-bottom: 1px solid #333;
        margin-left: 5px;
      }
    }
  }
</style>

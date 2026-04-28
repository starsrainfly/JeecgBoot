<template>
  <a-modal
    v-model:visible="visible"
    :title="title"
    @ok="handleConfirm"
    :confirmLoading="loading"
    width="900px"
    :destroyOnClose="true"
  >
    <a-row :gutter="24">
      <!-- 左侧：用现有的 LabelPreview 组件 -->
      <a-col :span="14">
        <LabelPreview
          :labelWidth="data.labelWidth"
          :labelHeight="data.labelHeight"
          :productCode="data.productCode"
          :productName="data.productName"
          :printProductName="data.printProductName"
          :productColor="data.productColor"
          :batchNo="data.batchNo"
          :companyName="data.companyName"
          :qrContent="data.qrContent"
          :barcodeContent="data.batchNo"
          :templateJson="data.templateJson"
          :copies="data.copies"
          :status="data.status"
          :produceDate="data.produceDate"
          :qcStatus="data.qcStatus"
        />
      </a-col>

      <!-- 右侧：打印信息 -->
      <a-col :span="10">
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item label="作业编号">{{ data.taskNo }}</a-descriptions-item>
          <a-descriptions-item label="产品编码">{{ data.productCode }}</a-descriptions-item>
          <a-descriptions-item label="产品名称">{{ data.productName }}</a-descriptions-item>
          <a-descriptions-item label="产品颜色">{{ data.productColor }}</a-descriptions-item>
          <a-descriptions-item label="批次号">{{ data.batchNo }}</a-descriptions-item>
          <a-descriptions-item label="标签尺寸">{{ data.labelWidth }}mm × {{ data.labelHeight }}mm</a-descriptions-item>
          <a-descriptions-item label="模板编码">{{ data.templateCode }}</a-descriptions-item>
          <a-descriptions-item label="公司名称">{{ data.companyName || '-' }}</a-descriptions-item>
          <a-descriptions-item label="打印产品名称">{{ data.printProductName || '默认' }}</a-descriptions-item>
        </a-descriptions>

        <a-divider />

        <a-form :model="data" layout="vertical">
          <a-form-item label="打印份数" required>
            <a-input-number
              v-model:value="data.copies"
              :min="1"
              :max="1000"
              style="width:100%"
            />
          </a-form-item>
        </a-form>
      </a-col>
    </a-row>

    <!-- 隐藏的真实打印区域 -->
    <div v-show="false">
      <div id="real-print-area">
        <div
          v-for="i in data.copies"
          :key="i"
          class="print-label-page"
          :style="{
            width: data.labelWidth + 'mm',
            height: data.labelHeight + 'mm',
          }"
        >
          <div
            v-for="(el, idx) in parsedElements"
            :key="idx"
            class="print-element"
            :style="getPrintElementStyle(el)"
          >
            <div v-if="el.type === 'text'" class="print-text" :style="getPrintTextStyle(el)">
              {{ getElementValue(el) }}
            </div>
            <svg v-else-if="el.type === 'barcode'" :id="'print-bc-' + i + '-' + idx" class="print-barcode-svg"></svg>
            <img v-else-if="el.type === 'qrcode' && data.qrImage"
                 :src="data.qrImage"
                 class="print-qrcode"
                 :style="{width: (el.size || 22) + 'mm', height: (el.size || 22) + 'mm'}" />
          </div>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, nextTick, watch } from 'vue';
  import JsBarcode from 'jsbarcode';
  import LabelPreview from './LabelPreview.vue';
  import { getTemplateInfo } from '../LabelPrintTask.api';

  const props = defineProps({
    title: { type: String, default: '标签打印确认' },
  });

  const emit = defineEmits(['confirm', 'cancel']);

  const visible = ref(false);
  const loading = ref(false);

  const data = reactive({
    id: '',
    taskNo: '',
    productName: '',
    productCode: '',
    productColor:'',
    batchNo: '',
    copies: 1,
    labelWidth: 60,
    labelHeight: 35,
    templateCode: '',
    templateJson: '',
    companyName: '',
    printProductName: '',
    qrImage: '',
    qrContent: '',
    produceDate: '',
    qcStatus: '',
    status: 'PENDING',
  });

  // 解析模板元素（用于真实打印）
  const parsedTemplate = computed(() => {
    try {
      if (data.templateJson) return JSON.parse(data.templateJson);
    } catch (e) {
      console.error('模板解析失败', e);
    }
    return { elements: [] };
  });

  const parsedElements = computed(() => parsedTemplate.value.elements || []);

  // 打印元素位置样式 - 直接用 mm
  function getPrintElementStyle(el: any) {
    return {
      position: 'absolute',
      left: el.x + 'mm',
      top: el.y + 'mm',
      width: el.width ? el.width + 'mm' : 'auto',
      height: el.height ? el.height + 'mm' : 'auto',
    };
  }

  // 打印文本样式 - 用 pt 单位
  function getPrintTextStyle(el: any) {
    return {
      fontSize: (el.fontSize || 9) + 'pt',
      fontWeight: el.bold ? 'bold' : 'normal',
      textAlign: el.align || 'left',
      width: el.width ? el.width + 'mm' : 'auto',
      whiteSpace: 'nowrap',
      overflow: 'hidden',
      textOverflow: 'ellipsis',
      lineHeight: 1.2,
    };
  }

  // 获取字段值
  function getElementValue(el: any): string {
    if (el.value) return el.value;
    const map: Record<string, string> = {
      companyName: data.companyName,
      productName: data.productName,
      productCode: data.productCode,
      productColor: data.productColor,
      printProductName:data.printProductName,
      batchNo: data.batchNo,
      produceDate: data.produceDate,
      qcStatus: data.qcStatus,
    };
    return map[el.field] || '';
  }

  // 渲染打印条码 - 关键修复
  async function renderPrintBarcodes() {
    await nextTick();

    for (let i = 1; i <= data.copies; i++) {
      parsedElements.value.forEach((el, idx) => {
        if (el.type === 'barcode') {
          const svg = document.getElementById('print-bc-' + i + '-' + idx);
          const value = getElementValue(el);

          if (svg && value) {
            try {
              // 条码高度：模板配置的是 mm
              const heightMm = el.height || 16;
              // JsBarcode 需要 px，浏览器打印默认 96dpi，1mm ≈ 3.78px
              const heightPx = heightMm * 3.78;
              // 条码宽度
              const widthMm = el.width || 26;

              JsBarcode(svg, value, {
                format: el.format || 'CODE128',
                width: 2,
                height: heightPx * 0.7,
                displayValue: true,
                fontSize: 8,
                margin: 0,
                textMargin: 2,
              });

              // 设置 SVG 容器尺寸
              svg.style.width = widthMm + 'mm';
              svg.style.height = heightMm + 'mm';
              svg.style.display = 'block';

            } catch (e) {
              console.error('条码渲染失败', e);
            }
          }
        }
      });
    }
  }

  // 执行打印
  async function doPrint() {
    await renderPrintBarcodes();

    return new Promise<void>((resolve) => {
      setTimeout(() => {
        const content = document.getElementById('real-print-area')?.innerHTML;
        if (!content) { resolve(); return; }

        const w = window.open('', '_blank');
        w!.document.write(`
        <!DOCTYPE html>
        <html>
          <head>
            <title>标签打印</title>
            <style>
              @page {
                size: ${data.labelWidth}mm ${data.labelHeight}mm;
                margin: 0;
              }
              * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
              }
              body {
                font-family: 'Microsoft YaHei', 'SimSun', sans-serif;
                width: ${data.labelWidth}mm;
                height: ${data.labelHeight}mm;
                overflow: hidden;
              }
              .print-label-page {
                width: ${data.labelWidth}mm;
                height: ${data.labelHeight}mm;
                position: relative;
                overflow: hidden;
                background: #fff;
                page-break-after: always;
              }
              .print-label-page:last-child {
                page-break-after: auto;
              }
              .print-element {
                position: absolute;
              }
              .print-text {
                line-height: 1.2;
              }
              .print-barcode-svg {
                width: 100%;
                height: 100%;
                display: block;
              }
              .print-qrcode {
                display: block;
                width: 100%;
                height: 100%;
              }
            </style>
          </head>
          <body>${content}</body>
        </html>
      `);

        w!.document.close();
        w!.focus();

        setTimeout(() => {
          w!.print();
          setTimeout(() => w!.close(), 1000);
          resolve();
        }, 500);
      }, 300);
    });
  }

  // 打开弹窗
  async function open(record: any) {
    let templateJson = '';
    let labelWidth = record.labelWidth || 60;
    let labelHeight = record.labelHeight || 35;
    let templateCode = record.templateCode || '';

    if (record.templateId) {
      try {
        const res = await getTemplateInfo({id:record.templateId});
        let templateData: any = null;

        if (res && typeof res === 'object') {
          if ('success' in res && 'result' in res) {
            if (res.success) templateData = res.result;
          } else if ('contentJson' in res || 'content_json' in res) {
            templateData = res;
          }
        }

        if (templateData) {
          templateJson = templateData.contentJson || templateData.content_json || '';
          labelWidth = templateData.labelWidth || templateData.label_width || labelWidth;
          labelHeight = templateData.labelHeight || templateData.label_height || labelHeight;
          templateCode = templateData.templateCode || templateData.template_code || templateCode;
        }
      } catch (e) {
        console.error('加载模板失败', e);
      }
    }

    Object.assign(data, {
      id: record.id || '',
      taskNo: record.taskNo || '',
      productName: record.productName || '',
      productCode: record.productCode || '',
      productColor: record.productColor ||'',
      batchNo: record.batchNo || '',
      labelWidth: labelWidth,
      labelHeight: labelHeight,
      templateCode: templateCode,
      templateJson: templateJson,
      companyName: record.companyName || record.companyId_dictText || '',
      printProductName: record.printProductName || '',
      qrImage: record.qrImage || '',
      qrContent: record.qrContent || '',
      produceDate: record.produceDate || new Date().toLocaleDateString('zh-CN'),
      qcStatus: record.qcStatus || '合格',
      copies: record.copies || 1,
      status: record.status || 'PENDING',
    });

    visible.value = true;
  }

  // 确认打印
  async function handleConfirm() {
    loading.value = true;
    try {
      await doPrint();
      emit('confirm', { id: data.id, copies: data.copies });
    } catch (e) {
      console.error('打印失败', e);
      throw e;
    } finally {
      loading.value = false;
    }
  }

  watch(visible, (val) => {
    if (!val) emit('cancel');
  });

  defineExpose({ open });
</script>

<style lang="less" scoped>
  .preview-title {
    font-weight: 500; font-size: 14px;
    margin-bottom: 12px; color: #333;
  }
  .label-preview-wrapper {
    display: flex; justify-content: center; align-items: center;
    background: #f5f5f5; padding: 16px;
    border-radius: 6px; margin-bottom: 8px;
  }
  .label-paper {
    background: #fff; border: 1px solid #d9d9d9;
    position: relative; box-shadow: 0 1px 4px rgba(0,0,0,0.1);
    overflow: hidden;
  }
  .preview-element { position: absolute; }
  .preview-text {
    font-family: 'Microsoft YaHei', 'SimSun', sans-serif;
    line-height: 1.2;
  }
  .preview-barcode { display: flex; flex-direction: column; align-items: center; }
  .preview-qrcode { display: block; }
  .preview-size { text-align: center; font-size: 12px; color: #999; }
</style>

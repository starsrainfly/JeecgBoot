<template>
  <div class="label-preview-container">
    <div class="preview-header">
      <span class="preview-title">标签预览</span>
      <a-tag :color="statusColor">{{ statusText }}</a-tag>
    </div>

    <div class="preview-body">
      <div class="label-paper" :style="labelPaperStyle">
        <template v-for="(el, idx) in templateElements" :key="idx">
          <div
            v-if="el.type === 'text'"
            class="tpl-text"
            :style="getTextStyle(el)"
          >
            {{ getElementValue(el) }}
          </div>

          <div
            v-else-if="el.type === 'barcode'"
            class="tpl-barcode"
            :style="getBarcodeWrapperStyle(el)"
          >
            <svg :id="'barcode-' + idx" ></svg>
          </div>

          <div
            v-else-if="el.type === 'qrcode'"
            class="tpl-qrcode"
            :style="getQrcodeStyle(el)"
          >
            <qrcode-vue
              v-if="getElementValue(el)"
              :value="getElementValue(el)"
              :size="getQrSize(el)"
              level="M"
            />
          </div>
        </template>
      </div>
    </div>

    <div class="preview-footer">
      <span>{{ labelWidth }}mm × {{ labelHeight }}mm</span>
      <span v-if="copies">打印 {{ copies }} 份</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { computed, onUpdated, nextTick, watch } from 'vue';
  import QrcodeVue from 'qrcode.vue';
  import JsBarcode from 'jsbarcode';

  const props = defineProps({
    // 标签尺寸
    labelWidth: { type: Number, default: 60 },
    labelHeight: { type: Number, default: 40 },
    // 统一数据入口（新增，用于库位标签等动态数据）
    labelDataJson: { type: String, default: '{}' },
    // 产品信息（保留原有）
    productCode: { type: String, default: '' },
    productName: { type: String, default: '' },
    printProductName: { type: String, default: '' },
    productColor: { type: String, default: '' },
    batchNo: { type: String, default: '' },
    // 公司信息（保留原有）
    companyName: { type: String, default: '' },
    // 二维码/条码内容（保留原有）
    qrContent: { type: String, default: '' },
    barcodeContent: { type: String, default: '' },
    // 模板配置（保留原有）
    templateJson: { type: String, default: '' },
    // 打印份数（保留原有）
    copies: { type: Number, default: 1 },
    // 状态（保留原有）
    status: { type: String, default: 'PENDING' },
    // 其他数据（保留原有）
    produceDate: { type: String, default: '' },
    expiryDate: { type: String, default: '' },
    spec: { type: String, default: '' },
    qcStatus: { type: String, default: '' },
    // 库位标签字段（新增）
    locationCode: { type: String, default: '' },
    locationName: { type: String, default: '' },
    pathCode: { type: String, default: '' },
    warehouseName: { type: String, default: '' },
    areaName: { type: String, default: '' },
    shelfName: { type: String, default: '' },
  });

  // 每毫米像素数（预览缩放）
  const pxPerMm = computed(() => {
    const maxWidth = 340;
    const basePx = 5;
    const actualWidth = props.labelWidth * basePx;
    return actualWidth > maxWidth ? maxWidth / props.labelWidth : basePx;
  });

  // 解析 labelDataJson（新增）
  const labelData = computed(() => {
    try {
      if (props.labelDataJson && props.labelDataJson !== '{}') {
        return JSON.parse(props.labelDataJson);
      }
    } catch (e) {
      console.error('labelDataJson解析失败', e);
    }
    return {};
  });

  // 解析模板JSON（完全保留原有逻辑）
  const templateConfig = computed(() => {
    try {
      if (props.templateJson) {
        return JSON.parse(props.templateJson);
      }
    } catch (e) {
      console.error('模板JSON解析失败', e);
    }
    // 默认产品标签模板
    return {
      page: { width: props.labelWidth, height: props.labelHeight },
      elements: [
        { type: 'text', field: 'companyName', x: 2, y: 1, fontSize: 7, align: 'center', width: 56 },
        { type: 'text', field: 'productName', x: 2, y: 6, fontSize: 11, bold: true, width: 28 },
        { type: 'text', field: 'color', x: 2, y: 12, fontSize: 8, width: 28 },
        { type: 'barcode', field: 'batchNo', x: 2, y: 16, width: 26, height: 8, format: 'CODE128' },
        { type: 'text', field: 'batchNo', x: 2, y: 24, fontSize: 5, width: 26, align: 'center' },
        { type: 'text', field: 'datePrefix', x: 2, y: 28, fontSize: 6, value: '日期:', width: 28 },
        { type: 'text', field: 'produceDate', x: 8, y: 28, fontSize: 6, width: 28 },
        { type: 'qrcode', field: 'qrCode', x: 32, y: 4, size: 22 },
        { type: 'text', field: 'qcStatus', x: 34, y: 27, fontSize: 10, bold: true, width: 20 },
      ],
      dataMapping: {
        qcStatus: 'batch.qcStatus'
      }
    };
  });

  const templateElements = computed(() => {
    return templateConfig.value.elements || [];
  });

  // 获取模板默认值（完全保留原有）
  function getTemplateDefaultValue(field: string): string {
    const tpl = templateConfig.value;
    if (!tpl.dataMapping) return '';

    const mapping = tpl.dataMapping[field];
    if (!mapping) return '';

    if (typeof mapping === 'string') {
      if (mapping.includes(':')) {
        const parts = mapping.split(':');
        return parts[1] || '';
      }
      if (!mapping.includes('.')) {
        return mapping;
      }
    }
    return '';
  }

  // 标签纸样式（完全保留原有）
  const labelPaperStyle = computed(() => {
    const px = pxPerMm.value;
    return {
      width: `${props.labelWidth * px}px`,
      height: `${props.labelHeight * px}px`,
      border: '1px solid #d9d9d9',
      backgroundColor: '#fff',
      position: 'relative',
      boxSizing: 'border-box',
      overflow: 'hidden',
    };
  });

  // 状态显示（完全保留原有）
  const statusText = computed(() => {
    const map = { PENDING: '待打印', PRINTING: '打印中', COMPLETED: '已完成', FAILED: '失败' };
    return map[props.status] || props.status;
  });

  const statusColor = computed(() => {
    const map = { PENDING: 'orange', PRINTING: 'blue', COMPLETED: 'green', FAILED: 'red' };
    return map[props.status] || 'default';
  });

  // 获取元素值 —— 核心修改：增加 labelDataJson 优先级 + 库位字段
  function getElementValue(el: any): string {
    const field = el.field;
    const data = labelData.value;

    // 【新增】优先从 labelDataJson 中取值（最高优先级）
    if (data[field] !== undefined && data[field] !== null && data[field] !== '') {
      return String(data[field]);
    }

    let value = '';
    switch (field) {
      // === 产品标签字段（完全保留原有）===
      case 'companyName':
        value = props.companyName || '';
        break;
      case 'productCode':
        value = props.productCode || '';
        break;
      case 'productName':
        value = props.productName || props.printProductName || '';
        break;
      case 'color':
        value = props.productColor || '';
        break;
      case 'batchNo':
        value = props.barcodeContent || props.batchNo || '';
        break;
      case 'produceDate':
        value = props.produceDate || new Date().toLocaleDateString('zh-CN');
        break;
      case 'datePrefix':
        value = el.value || '日期:';
        break;
      case 'qcStatus':
        value = props.qcStatus || '';
        if (!value) {
          value = getTemplateDefaultValue('qcStatus') || '';
        }
        if (!value) {
          value = '合格';
        }
        break;
      case 'qrCode':
        value = props.qrContent || '';
        break;

      // === 库位标签字段（新增）===
      case 'pathCode':
        value = props.pathCode || '';
        break;
      case 'locationCode':
        value = props.locationCode || '';
        break;
      case 'locationName':
        value = props.locationName || '';
        break;
      case 'warehouseName':
        value = props.warehouseName || '';
        break;
      case 'areaName':
        value = props.areaName || '';
        break;
      case 'shelfName':
        value = props.shelfName || '';
        break;

      default:
        value = el.value || '';
    }

    return value;
  }

  // 文本样式（完全保留原有）
  function getTextStyle(el: any) {
    const px = pxPerMm.value;
    let color = el.color || '#000';
    if (el.field === 'qcStatus') {
      color = '#000';
    }
    return {
      position: 'absolute',
      left: `${el.x * px}px`,
      top: `${el.y * px}px`,
      fontSize: `${(el.fontSize || 8) * px / 4}px`,
      fontWeight: el.bold ? 'bold' : 'normal',
      color: color,
      textAlign: el.align || 'left',
      width: el.width ? `${el.width * px}px` : 'auto',
      lineHeight: 1.2,
      whiteSpace: 'nowrap',
      overflow: 'hidden',
      textOverflow: 'ellipsis',
    };
  }

  // 条码容器样式（完全保留原有）
  function getBarcodeWrapperStyle(el: any) {
    const px = pxPerMm.value;
    return {
      position: 'absolute',
      left: `${el.x * px}px`,
      top: `${el.y * px}px`,
    };
  }

  // 二维码样式（完全保留原有）
  function getQrcodeStyle(el: any) {
    const px = pxPerMm.value;
    return {
      position: 'absolute',
      left: `${el.x * px}px`,
      top: `${el.y * px}px`,
      width: `${el.size * px}px`,
      height: `${el.size * px}px`,
    };
  }

  // 【新增】二维码尺寸计算（兼容 size/width 属性）
  function getQrSize(el: any): number {
    const px = pxPerMm.value;
    const sizeMm = el.size || el.width || 20;
    return Math.floor(sizeMm * px);
  }

  // 渲染条码（完全保留原有逻辑，仅增加 labelDataJson 监听）
  function renderBarcodes() {
    nextTick(() => {
      templateElements.value.forEach((el, idx) => {
        if (el.type === 'barcode') {
          const svg = document.getElementById('barcode-' + idx) as HTMLElement;
          const value = getElementValue(el);

          if (svg && value) {
            try {
              const px = pxPerMm.value;
              const configHeight = el.height || 12;
              const configWidth = el.width || 32;
              const targetWidth = configWidth * px;
              const targetHeight = configHeight * px;
              const textHeight = Math.round(targetHeight * 0.25);
              const barHeight = targetHeight - textHeight - 4;

              svg.innerHTML = '';
              svg.removeAttribute('width');
              svg.removeAttribute('height');
              svg.removeAttribute('style');

              JsBarcode(svg, value, {
                format: el.format || 'CODE128',
                width: 2,
                height: barHeight,
                displayValue: true,
                fontSize: Math.max(10, textHeight - 2),
                margin: 0,
                textMargin: 2,
              });

              svg.setAttribute('width', targetWidth + 'px');
              svg.setAttribute('height', targetHeight + 'px');
              svg.style.width = targetWidth + 'px';
              svg.style.height = targetHeight + 'px';

            } catch (e) {
              console.error('条码渲染失败', e);
            }
          }
        }
      });
    });
  }

  // 监听数据变化重新渲染条码（增加 labelDataJson 监听）
  watch(() => [props.barcodeContent, props.batchNo, props.templateJson, props.labelDataJson], renderBarcodes, { deep: true, immediate: true });
  onUpdated(renderBarcodes);
</script>

<style lang="less" scoped>
  .label-preview-container {
    border: 1px solid #e8e8e8;
    border-radius: 6px;
    overflow: hidden;

    .preview-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 12px;
      background-color: #fafafa;
      border-bottom: 1px solid #e8e8e8;

      .preview-title {
        font-weight: 500;
        font-size: 14px;
      }
    }

    .preview-body {
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 16px;
      background-color: #f5f5f5;
      border-radius: 6px;
      min-height: 200px;
      overflow: auto;

      .label-paper {
        box-shadow: 0 1px 4px rgba(0,0,0,0.1);

        .tpl-text {
          font-family: 'Microsoft YaHei', 'SimHei', sans-serif;
        }

        .tpl-barcode {
          svg {
            display:block;
          }
        }

        .tpl-qrcode {
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
    }

    .preview-footer {
      display: flex;
      justify-content: space-between;
      padding: 8px 12px;
      background-color: #fafafa;
      border-top: 1px solid #e8e8e8;
      font-size: 12px;
      color: #666;
    }
  }
</style>

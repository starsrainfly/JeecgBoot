<template>
  <div class="label-preview-container">
    <div class="preview-header">
      <span class="preview-title">标签预览</span>
      <a-tag :color="statusColor">{{ statusText }}</a-tag>
    </div>

    <div class="preview-body">
      <!-- 标签纸 - 按模板尺寸 -->
      <div class="label-paper" :style="labelPaperStyle">
        <!-- 按模板元素渲染 -->
        <template v-for="(el, idx) in templateElements" :key="idx">
          <!-- 文本元素 -->
          <div
            v-if="el.type === 'text'"
            class="tpl-text"
            :style="getTextStyle(el)"
          >
            {{ getElementValue(el) }}
          </div>

          <!-- 条码元素 -->
          <div
            v-else-if="el.type === 'barcode'"
            class="tpl-barcode"
            :style="getBarcodeWrapperStyle(el)"
          >
            <svg :id="'barcode-' + idx" ></svg>
          </div>
<!--          style="max-width:100%;"-->
          <!-- 二维码元素 -->
          <div
            v-else-if="el.type === 'qrcode'"
            class="tpl-qrcode"
            :style="getQrcodeStyle(el)"
          >
            <qrcode-vue
              v-if="getElementValue(el)"
              :value="getElementValue(el)"
              :size="el.size * pxPerMm"
              level="M"
            />
          </div>
        </template>
      </div>
    </div>

    <!-- 尺寸信息 -->
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
    // 产品信息
    productCode: { type: String, default: '' },
    productName: { type: String, default: '' },
    printProductName: { type: String, default: '' },
    productColor: { type: String, default: '' },
    batchNo: { type: String, default: '' },
    // 公司信息
    companyName: { type: String, default: '' },
    // 二维码/条码内容
    qrContent: { type: String, default: '' },
    barcodeContent: { type: String, default: '' },
    // 模板配置
    templateJson: { type: String, default: '' },
    // 打印份数
    copies: { type: Number, default: 1 },
    // 状态
    status: { type: String, default: 'PENDING' },
    // 其他数据
    produceDate: { type: String, default: '' },
    expiryDate: { type: String, default: '' },
    spec: { type: String, default: '' },
    qcStatus: { type: String, default: '' },
  });

  // 每毫米像素数（预览缩放）
  const pxPerMm = computed(() => {
    const maxWidth = 340;
    const basePx = 5;
    const actualWidth = props.labelWidth * basePx;
    return actualWidth > maxWidth ? maxWidth / props.labelWidth : basePx;
  });

  // 解析模板JSON
  const templateConfig = computed(() => {
    try {
      if (props.templateJson) {
        return JSON.parse(props.templateJson);
      }
    } catch (e) {
      console.error('模板JSON解析失败', e);
    }
    // 默认模板
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

  // 获取模板默认值
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

  // 标签纸样式
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

  // 状态显示
  const statusText = computed(() => {
    const map = { PENDING: '待打印', PRINTING: '打印中', COMPLETED: '已完成', FAILED: '失败' };
    return map[props.status] || props.status;
  });

  const statusColor = computed(() => {
    const map = { PENDING: 'orange', PRINTING: 'blue', COMPLETED: 'green', FAILED: 'red' };
    return map[props.status] || 'default';
  });

  // 获取元素值
  function getElementValue(el: any): string {
    const field = el.field;

    let value = '';
    switch (field) {
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
      default:
        value = el.value || '';
    }

    return value;
  }

  // 文本样式 - 关键修复：严格按模板配置
  function getTextStyle(el: any) {
    const px = pxPerMm.value;

    // qcStatus 强制黑色
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

  // 条码样式 - 关键修复：严格按模板 height 配置
  function getBarcodeStyle(el: any) {
    const px = pxPerMm.value;
    // 严格使用模板配置的 height，不覆盖
    const height = el.height || 8; // 只有模板没配时才默认8
    return {
      position: 'absolute',
      left: `${el.x * px}px`,
      top: `${el.y * px}px`,
      width: `${el.width * px}px`,
      height: `${height * px}px`,
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
    };
  }
  // 条码容器样式 - 只控制位置，不控制尺寸
  function getBarcodeWrapperStyle(el: any) {
    const px = pxPerMm.value;
    return {
      position: 'absolute',
      left: `${el.x * px}px`,
      top: `${el.y * px}px`,
      // 不设置 width/height，让内部 SVG 自然展开
    };
  }
  // 二维码样式
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

  // 渲染条码 - 关键修复：严格按模板 height 配置
  function renderBarcodes() {
    nextTick(() => {
      templateElements.value.forEach((el, idx) => {
        if (el.type === 'barcode') {
          const svg = document.getElementById('barcode-' + idx) as HTMLElement;
          const value = getElementValue(el);

          if (svg && value) {
            try {
              const px = pxPerMm.value;
              const configHeight = el.height || 12; // 模板配置的高度(mm)
              const configWidth = el.width || 32;    // 模板配置的宽度(mm)

              // 目标尺寸（px）
              const targetWidth = configWidth * px;
              const targetHeight = configHeight * px;

              // 整个元素尺寸（px）
              const totalWidth = configWidth * px;
              const totalHeight = configHeight * px;

              // // 文字区域高度：约 3mm 或按比例
              // const textHeight = Math.min(14, totalHeight * 0.25); // 文字占25%，最多14px
              // // 条码条高度 = 总高度 - 文字高度 - 间距
              // const barHeight = Math.max(totalHeight - textHeight - 4, totalHeight * 0.6);
              // 文字区域：占 25% 高度
              const textHeight = Math.round(targetHeight * 0.25);
              const barHeight = targetHeight - textHeight - 4; // 条码条高度

              // // 设置 SVG 容器尺寸
              // svg.style.width = totalWidth + 'px';
              // svg.style.height = totalHeight + 'px';
// 关键：先清空 SVG，避免重复渲染叠加
              svg.innerHTML = '';
              svg.removeAttribute('width');
              svg.removeAttribute('height');
              svg.removeAttribute('style');

              JsBarcode(svg, value, {
                format: el.format || 'CODE128',
                width: 2,                    // 单条宽度 2px
                height: barHeight,           // 条码条高度
                displayValue: true,          // 显示文字
                fontSize: Math.max(10, textHeight - 2), // 文字大小
                margin: 0,
                textMargin: 2,               // 文字与条码间距
              });

              // 关键：JsBarcode 生成后，强制设置 SVG 尺寸为模板配置值
              // 这样条码条 + 文字总高 = 配置高度
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

  // 监听数据变化重新渲染条码
  watch(() => [props.barcodeContent, props.batchNo, props.templateJson], renderBarcodes, { deep: true, immediate: true});
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
            /*max-width: 100%;*/
            /*height: auto;*/
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

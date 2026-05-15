<template>
  <div class="label-preview-container">
    <div class="preview-header">
      <span class="preview-title">标签预览</span>
      <a-tag :color="statusColor">{{ statusText }}</a-tag>
    </div>

    <div class="preview-body">
      <div class="label-paper" :style="labelPaperStyle">
        <template v-for="(el, idx) in templateElements" :key="idx">
          <div v-if="el.type === 'text'" class="tpl-text" :style="getTextStyle(el)">
            {{ getElementValue(el) }}
          </div>
          <div v-else-if="el.type === 'qrcode'" class="tpl-qrcode" :style="getQrcodeStyle(el)">
            <qrcode-vue v-if="getElementValue(el)" :value="getElementValue(el)" :size="getQrSize(el)" level="M" />
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
  import { computed } from 'vue';
  import QrcodeVue from 'qrcode.vue';

  const props = defineProps({
    labelWidth: { type: Number, default: 60 },
    labelHeight: { type: Number, default: 40 },
    labelDataJson: { type: String, default: '{}' },
    // 产品标签字段（保持兼容）
    productCode: { type: String, default: '' },
    productName: { type: String, default: '' },
    printProductName: { type: String, default: '' },
    productColor: { type: String, default: '' },
    batchNo: { type: String, default: '' },
    companyName: { type: String, default: '' },
    qrContent: { type: String, default: '' },
    barcodeContent: { type: String, default: '' },
    templateJson: { type: String, default: '' },
    copies: { type: Number, default: 1 },
    status: { type: String, default: 'PENDING' },
    produceDate: { type: String, default: '' },
    expiryDate: { type: String, default: '' },
    spec: { type: String, default: '' },
    qcStatus: { type: String, default: '' },
    // 库位标签字段
    locationCode: { type: String, default: '' },
    locationName: { type: String, default: '' },
    pathCode: { type: String, default: '' },
    warehouseName: { type: String, default: '' },
    areaName: { type: String, default: '' },
    shelfName: { type: String, default: '' },
  });

  const pxPerMm = computed(() => {
    const maxWidth = 340;
    const basePx = 5;
    const actualWidth = props.labelWidth * basePx;
    return actualWidth > maxWidth ? maxWidth / props.labelWidth : basePx;
  });

  const labelData = computed(() => {
    try {
      if (props.labelDataJson && props.labelDataJson !== '{}') {
        return JSON.parse(props.labelDataJson);
      }
    } catch (e) {
      console.error('labelDataJson解析失败', e);
    }
    // 合并所有可能的字段，优先级：labelDataJson > 独立props
    return {
      // 产品标签
      productCode: props.productCode,
      productName: props.productName,
      printProductName: props.printProductName,
      productColor: props.productColor,
      batchNo: props.batchNo,
      companyName: props.companyName,
      qrContent: props.qrContent,
      barcodeContent: props.barcodeContent,
      produceDate: props.produceDate,
      expiryDate: props.expiryDate,
      spec: props.spec,
      qcStatus: props.qcStatus,
      // 库位标签
      locationCode: props.locationCode,
      locationName: props.locationName,
      pathCode: props.pathCode,
      warehouseName: props.warehouseName,
      areaName: props.areaName,
      shelfName: props.shelfName,
    };
  });

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
        { type: 'text', field: 'batchNo', x: 2, y: 16, fontSize: 5, width: 26, align: 'center' },
        { type: 'text', field: 'datePrefix', x: 2, y: 28, fontSize: 6, value: '日期:', width: 28 },
        { type: 'text', field: 'produceDate', x: 8, y: 28, fontSize: 6, width: 28 },
        { type: 'qrcode', field: 'qrCode', x: 32, y: 4, size: 22 },
        { type: 'text', field: 'qcStatus', x: 34, y: 27, fontSize: 10, bold: true, width: 20 },
      ]
    };
  });

  const templateElements = computed(() => templateConfig.value.elements || []);

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

  const statusText = computed(() => {
    const map = { PENDING: '待打印', PRINTING: '打印中', COMPLETED: '已完成', FAILED: '失败' };
    return map[props.status] || props.status;
  });

  const statusColor = computed(() => {
    const map = { PENDING: 'orange', PRINTING: 'blue', COMPLETED: 'green', FAILED: 'red' };
    return map[props.status] || 'default';
  });

  /**
   * 获取元素显示值 - 核心修复：增加库位字段映射
   */
  function getElementValue(el: any): string {
    const field = el.field;
    const data = labelData.value;

    // 优先从 labelDataJson 解析的数据中取
    if (data[field] !== undefined && data[field] !== null && data[field] !== '') {
      return String(data[field]);
    }

    // 兜底：从 props 或固定值中获取
    switch (field) {
      // === 库位标签字段 ===
      case 'pathCode': return props.pathCode || '';
      case 'locationCode': return props.locationCode || '';
      case 'locationName': return props.locationName || '';
      case 'warehouseName': return props.warehouseName || '';
      case 'areaName': return props.areaName || '';
      case 'shelfName': return props.shelfName || '';
      case 'qrCode': return data.qrContent || props.qrContent || ''; // 库位二维码内容

      // === 产品标签字段 ===
      case 'companyName': return props.companyName || '';
      case 'productCode': return props.productCode || '';
      case 'productName': return props.productName || props.printProductName || '';
      case 'color': return props.productColor || '';
      case 'batchNo': return props.barcodeContent || props.batchNo || '';
      case 'produceDate': return props.produceDate || new Date().toLocaleDateString('zh-CN');
      case 'datePrefix': return el.value || '日期:';
      case 'qcStatus': return props.qcStatus || '合格';

      // 兼容旧模板的 qrCode 字段（产品标签用 qrContent）
      case 'qrContent': return props.qrContent || '';

      default: return el.value || '';
    }
  }

  function getTextStyle(el: any) {
    const px = pxPerMm.value;
    return {
      position: 'absolute',
      left: `${el.x * px}px`,
      top: `${el.y * px}px`,
      fontSize: `${(el.fontSize || 8) * px / 4}px`,
      fontWeight: el.bold ? 'bold' : 'normal',
      color: el.color || '#000',
      textAlign: el.align || 'left',
      width: el.width ? `${el.width * px}px` : 'auto',
      lineHeight: 1.2,
      whiteSpace: 'nowrap',
      overflow: 'hidden',
      textOverflow: 'ellipsis',
    };
  }

  function getQrcodeStyle(el: any) {
    const px = pxPerMm.value;
    // 支持 width/height 或 size 属性
    const size = el.size || el.width || 20;
    return {
      position: 'absolute',
      left: `${el.x * px}px`,
      top: `${el.y * px}px`,
      width: `${size * px}px`,
      height: `${size * px}px`,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
    };
  }

  // 二维码尺寸计算（兼容 size/width 属性）
  function getQrSize(el: any): number {
    const px = pxPerMm.value;
    const sizeMm = el.size || el.width || 20;
    return Math.floor(sizeMm * px);
  }
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
      min-height: 200px;
      overflow: auto;

      .label-paper {
        box-shadow: 0 1px 4px rgba(0,0,0,0.1);

        .tpl-text {
          font-family: 'Microsoft YaHei', 'SimHei', sans-serif;
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

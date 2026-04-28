<template>
  <div class="label-preview-container">
    <div class="preview-header">
      <span class="preview-title">标签预览</span>
      <a-tag :color="statusColor">{{ statusText }}</a-tag>
    </div>

    <div class="preview-body" :style="previewBodyStyle">
      <!-- 标签纸 -->
      <div class="label-paper" :style="labelPaperStyle">
        <!-- 二维码区域 -->
        <div class="qr-section" v-if="showQrCode">
          <div class="qr-placeholder" :style="qrStyle">
            <qrcode-vue
              v-if="qrContent"
              :value="qrContent"
              :size="qrSize"
              level="M"
            />
            <div v-else class="qr-empty">二维码</div>
          </div>
        </div>

        <!-- 文本信息区域 -->
        <div class="info-section" :style="infoSectionStyle">
          <div class="info-row" v-if="printProductName">
            <span class="label-text">品名:</span>
            <span class="value-text">{{ printProductName }}</span>
          </div>
          <div class="info-row" v-if="productCode">
            <span class="label-text">编码:</span>
            <span class="value-text">{{ productCode }}</span>
          </div>
          <div class="info-row" v-if="batchNo">
            <span class="label-text">批次:</span>
            <span class="value-text">{{ batchNo }}</span>
          </div>
          <div class="info-row" v-if="productColor">
            <span class="label-text">颜色:</span>
            <span class="value-text">{{ productColor }}</span>
          </div>
          <div class="info-row" v-if="companyName">
            <span class="label-text">公司:</span>
            <span class="value-text">{{ companyName }}</span>
          </div>
          <div class="info-row" v-if="printDate">
            <span class="label-text">日期:</span>
            <span class="value-text">{{ printDate }}</span>
          </div>
        </div>
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
  import { computed } from 'vue';
  import QrcodeVue from 'qrcode.vue';

  const props = defineProps({
    // 标签尺寸
    labelWidth: { type: Number, default: 60 },
    labelHeight: { type: Number, default: 40 },
    // 产品信息
    productCode: { type: String, default: '' },
    productName: { type: String, default: '' },
    printProductName: { type: String, default: '' },
    productColor: { type: String, default: '' },
    printColor: { type: String, default: '' },
    batchNo: { type: String, default: '' },
    // 公司信息
    companyName: { type: String, default: '' },
    // 二维码
    qrContent: { type: String, default: '' },
    showQrCode: { type: Boolean, default: true },
    // 打印份数
    copies: { type: Number, default: 1 },
    // 状态
    status: { type: String, default: 'PENDING' },
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

  // 打印日期
  const printDate = computed(() => {
    return new Date().toLocaleDateString('zh-CN');
  });

  // 预览缩放比例（根据容器自适应）
  const scale = computed(() => {
    const maxWidth = 280;
    const mmToPx = 3.78; // 1mm ≈ 3.78px at 96dpi
    const actualWidth = props.labelWidth * mmToPx;
    return actualWidth > maxWidth ? maxWidth / actualWidth : 1;
  });

  // 标签纸样式
  const labelPaperStyle = computed(() => {
    const mmToPx = 3.78 * scale.value;
    return {
      width: `${props.labelWidth * mmToPx}px`,
      height: `${props.labelHeight * mmToPx}px`,
      border: '1px solid #d9d9d9',
      backgroundColor: '#fff',
      display: 'flex',
      flexDirection: props.labelWidth > props.labelHeight ? 'row' : 'column',
      padding: `${4 * scale.value}px`,
      boxSizing: 'border-box',
      gap: `${4 * scale.value}px`,
    };
  });

  // 预览区域样式
  const previewBodyStyle = computed(() => ({
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: '16px',
    backgroundColor: '#f5f5f5',
    borderRadius: '6px',
    minHeight: '200px',
  }));

  // 二维码尺寸
  const qrSize = computed(() => {
    const mmToPx = 3.78 * scale.value;
    const minDim = Math.min(props.labelWidth, props.labelHeight);
    return Math.floor((minDim * 0.4) * mmToPx);
  });

  const qrStyle = computed(() => ({
    width: `${qrSize.value}px`,
    height: `${qrSize.value}px`,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    border: '1px dashed #d9d9d9',
  }));

  // 信息区域样式
  const infoSectionStyle = computed(() => ({
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    fontSize: `${12 * scale.value}px`,
    gap: `${2 * scale.value}px`,
  }));
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
      .label-paper {
        box-shadow: 0 1px 4px rgba(0,0,0,0.1);

        .qr-section {
          display: flex;
          align-items: center;
          justify-content: center;

          .qr-placeholder {
            .qr-empty {
              color: #999;
              font-size: 12px;
            }
          }
        }

        .info-section {
          .info-row {
            display: flex;
            gap: 4px;
            line-height: 1.4;

            .label-text {
              color: #666;
              white-space: nowrap;
            }

            .value-text {
              font-weight: 500;
              word-break: break-all;
            }
          }
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

<template>
  <div class="scan-input flex items-center">
    <a-input
      ref="inputRef"
      v-bind="$attrs"
      :value="props.value"
      @update:value="onInputChange"
      @keydown="onKeydown"
      placeholder="请输入或扫描（支持扫码枪）"
    />
    <a-button class="ml-2" type="primary" @click="openScan" preIcon="ant-design:camera-outlined">

    </a-button>

    <a-modal
      v-model:visible="scanVisible"
      title="摄像头扫码"
      :footer="null"
      :width="460"
      :destroyOnClose="true"
      @cancel="stopScan"
    >
      <div class="scan-box">
        <!-- 扫描区域容器 -->
        <div class="scan-wrapper">
          <div id="scan-reader" ref="readerRef" class="scan-reader" />

          <!-- 扫描框叠加层：角标 + 动画线 -->
          <div class="scan-overlay">
            <!-- 四角定位标 -->
            <div class="corner corner-tl"></div>
            <div class="corner corner-tr"></div>
            <div class="corner corner-bl"></div>
            <div class="corner corner-br"></div>

            <!-- 扫描动画线 -->
            <div class="scan-line-box">
              <div class="scan-line"></div>
            </div>

            <!-- 扫描文字提示 -->
            <div class="scan-text">将二维码/条码放入框内，即可自动扫描</div>
          </div>
        </div>

        <p v-if="scanTip" class="scan-tip">{{ scanTip }}</p>
        <a-button type="primary" block class="mt-2" @click="stopScan">停止扫描</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, nextTick } from 'vue';
  import { Icon } from '/@/components/Icon';
  import { useMessage } from '/@/hooks/web/useMessage';

  const props = defineProps<{ value?: string }>();
  const emit = defineEmits(['update:value', 'change']);
  const { createMessage } = useMessage();

  const inputRef = ref<HTMLInputElement | null>(null);
  const scanVisible = ref(false);
  const readerRef = ref<HTMLDivElement | null>(null);
  const scanTip = ref('');
  let html5QrCode: any = null;

  // ===== 扫码枪支持 =====
  let keyBuffer = '';
  let lastKeyTime = 0;
  const SCAN_THRESHOLD_MS = 80; // 按键间隔小于80ms视为扫码枪

  function onInputChange(val: string) {
    emit('update:value', val);
    // 关键：同时发出 change 事件，确保父组件能捕获
    emit('change', val);
  }


  function onKeydown(e: KeyboardEvent) {
    const now = Date.now();
    const timeDiff = now - lastKeyTime;
    lastKeyTime = now;

    if (e.key === 'Enter') {
      if (keyBuffer.length > 0 && timeDiff < SCAN_THRESHOLD_MS) {
        // 扫码枪输入：拦截默认Enter，直接回填结果
        e.preventDefault();
        emit('update:value', keyBuffer);
        emit('change', keyBuffer);
        keyBuffer = '';
        return;
      }
      keyBuffer = '';
      return;
    }

    if (timeDiff > SCAN_THRESHOLD_MS) {
      keyBuffer = '';
    }
    keyBuffer += e.key;
  }

  async function openScan() {
    if (typeof window === 'undefined' || !navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      createMessage.error('当前浏览器或环境不支持摄像头调用');
      return;
    }
    scanVisible.value = true;
    await nextTick();
    startScan();
  }

  async function startScan() {
    scanTip.value = '正在启动摄像头…';
    try {
      const { Html5Qrcode } = await import('html5-qrcode');
      if (!readerRef.value) {
        scanTip.value = '初始化失败';
        return;
      }
      html5QrCode = new Html5Qrcode('scan-reader');
      await html5QrCode.start(
        { facingMode: 'environment' },
        { fps: 10, qrbox: { width: 320, height: 320 } },
        (decodedText: string) => {
          onInputChange(decodedText);
          scanTip.value = '识别成功：' + decodedText;
          setTimeout(() => stopScan(), 300);
        },
        () => {
          // 帧级别扫码失败，静默忽略
        }
      );
      scanTip.value = '';
    } catch (err: any) {
      scanTip.value = '摄像头启动失败，请检查权限或换用扫码枪';
      createMessage.error('摄像头启动失败：' + (err?.message || String(err)));
    }
  }

  async function stopScan() {
    scanVisible.value = false;
    if (html5QrCode) {
      try {
        await html5QrCode.stop();
        html5QrCode.clear();
      } catch (e) {
        // ignore
      }
      html5QrCode = null;
    }
  }
</script>

<style lang="less" scoped>
  .scan-input {
    display: flex;
    width: 100%;
  }
  .scan-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px;
  }

  // 扫描区域容器：相对定位，用于叠加层定位
  .scan-wrapper {
    position: relative;
    width: 100%;
    max-width: 400px;
    height: 400px;
    border-radius: 8px;
    overflow: hidden;
    background: #000;
  }

  .scan-reader {
    width: 100%;
    height: 100%;
    border-radius: 8px;
    overflow: hidden;

    // 深度选择器：覆盖 html5-qrcode 内部样式，扩大扫描框
    :deep(video) {
      object-fit: cover;
    }

    :deep(#qr-shaded-region) {
      border-width: 40px !important;
    }
  }

  // 扫描框叠加层
  .scan-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  // 四角定位标（类似微信扫码框的四个角）
  .corner {
    position: absolute;
    width: 28px;
    height: 28px;
    border-color: #00ff00;
    border-style: solid;
    z-index: 10;
  }

  .corner-tl {
    top: 36px;
    left: 36px;
    border-width: 4px 0 0 4px;
    border-top-left-radius: 4px;
  }

  .corner-tr {
    top: 36px;
    right: 36px;
    border-width: 4px 4px 0 0;
    border-top-right-radius: 4px;
  }

  .corner-bl {
    bottom: 36px;
    left: 36px;
    border-width: 0 0 4px 4px;
    border-bottom-left-radius: 4px;
  }

  .corner-br {
    bottom: 36px;
    right: 36px;
    border-width: 0 4px 4px 0;
    border-bottom-right-radius: 4px;
  }

  // 扫描线容器：限定在扫描框区域内
  .scan-line-box {
    position: absolute;
    top: 36px;
    left: 36px;
    right: 36px;
    bottom: 36px;
    overflow: hidden;
    z-index: 11;
  }

  // 扫描动画线（类似微信的从上到下移动的横线）
  .scan-line {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 3px;
    background: linear-gradient(
      90deg,
      transparent 0%,
      rgba(0, 255, 0, 0.1) 20%,
      rgba(0, 255, 0, 0.9) 50%,
      rgba(0, 255, 0, 0.1) 80%,
      transparent 100%
    );
    box-shadow: 0 0 10px rgba(0, 255, 0, 0.8), 0 0 20px rgba(0, 255, 0, 0.4);
    border-radius: 2px;
    animation: scanMove 2s linear infinite;
  }

  // 扫描线动画：从上到下循环移动
  // 使用 :global 包裹 keyframes，避免 scoped 样式导致动画失效
  @keyframes scanMove {
    0% {
      transform: translateY(0);
      opacity: 0.6;
    }
    10% {
      opacity: 1;
    }
    90% {
      opacity: 1;
    }
    100% {
      transform: translateY(328px);
      opacity: 0.6;
    }
  }

  // 扫描提示文字
  .scan-text {
    position: absolute;
    bottom: 48px;
    color: rgba(255, 255, 255, 0.9);
    font-size: 13px;
    text-align: center;
    text-shadow: 0 1px 3px rgba(0, 0, 0, 0.8);
    z-index: 12;
    padding: 0 20px;
  }

  .scan-tip {
    margin-top: 12px;
    color: #666;
    font-size: 13px;
    text-align: center;
  }
</style>

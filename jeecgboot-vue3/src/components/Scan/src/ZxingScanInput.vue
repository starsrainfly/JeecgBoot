<<template>
  <div class="zxing-scan-input flex items-center">
    <a-input
      ref="inputRef"
      v-bind="$attrs"
      :value="props.value"
      @update:value="onInputChange"
      @keydown="onKeydown"
      placeholder="请输入或扫描（支持扫码枪）"
    />
<!--    <a-button class="ml-2" type="default" @click="openScan">-->
<!--      <template #icon>-->
<!--        <Icon icon="ant-design:camera-outlined" />-->
<!--      </template>-->
<!--    </a-button>-->
    <a-button class="ml-2" @click="openScan" preIcon="ant-design:camera-outlined" />
    <a-modal
      v-model:visible="scanVisible"
      title="摄像头扫码"
      :footer="null"
      :width="420"
      :destroyOnClose="true"
      @cancel="stopScan"
    >
      <div class="scan-box">
        <div class="scan-wrapper">
          <!-- 视频层：用固定 id，不用 ref -->
          <video id="zxing-video" class="scan-video" />

          <!-- 自定义覆盖层：微信风格 -->
          <div class="scan-overlay">
            <div class="corner corner-tl" />
            <div class="corner corner-tr" />
            <div class="corner corner-bl" />
            <div class="corner corner-br" />
            <div class="scan-line" />
            <div class="scan-mask-top" />
            <div class="scan-mask-bottom" />
            <div class="scan-mask-left" />
            <div class="scan-mask-right" />
          </div>
        </div>

        <p v-if="scanTip" class="scan-tip">{{ scanTip }}</p>
        <a-button type="primary" block class="mt-2" @click="stopScan">停止扫描</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { Icon } from '/@/components/Icon';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { BrowserQRCodeReader } from '@zxing/browser';

  const props = defineProps<{ value?: string }>();
  const emit = defineEmits(['update:value', 'change']);
  const { createMessage } = useMessage();

  const inputRef = ref<<HTMLInputElement | null>(null);
  const scanVisible = ref(false);
  const scanTip = ref('');
  let codeReader: BrowserQRCodeReader | null = null;
  let controls: any = null;

  // ===== 扫码枪支持 =====
  let keyBuffer = '';
  let lastKeyTime = 0;
  const SCAN_THRESHOLD_MS = 80;

  function onInputChange(val: string) {
    emit('update:value', val);
    emit('change', val);
  }

  function onKeydown(e: KeyboardEvent) {
    const now = Date.now();
    const timeDiff = now - lastKeyTime;
    lastKeyTime = now;

    if (e.key === 'Enter') {
      if (keyBuffer.length > 0 && timeDiff < SCAN_THRESHOLD_MS) {
        e.preventDefault();
        onInputChange(keyBuffer);
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

  // ===== ZXing 摄像头扫码 =====
  async function openScan() {
    if (!navigator.mediaDevices?.getUserMedia) {
      createMessage.error('当前环境不支持摄像头');
      return;
    }
    scanVisible.value = true;
    // 等 modal 打开动画完成，DOM 稳定
    setTimeout(() => startScan(), 400);
  }

  async function startScan() {
    scanTip.value = '正在启动摄像头…';

    try {
      // 用固定 id 获取 DOM
      const videoElement = document.getElementById('zxing-video') as HTMLVideoElement;
      if (!videoElement) {
        scanTip.value = '视频元素未找到 (DOM未就绪)';
        console.error('video element not found');
        return;
      }

      // 检查摄像头
      const devices = await navigator.mediaDevices.enumerateDevices();
      const cameras = devices.filter(d => d.kind === 'videoinput');
      console.log('可用摄像头:', cameras);

      if (cameras.length === 0) {
        scanTip.value = '未检测到摄像头';
        createMessage.error('未检测到摄像头设备');
        return;
      }

      // 创建 ZXing 读取器
      codeReader = new BrowserQRCodeReader();

      // 从摄像头解码，绑定到 video 元素
      controls = await codeReader.decodeFromConstraints(
        { video: { facingMode: 'environment' } },
        videoElement,
        (result, error) => {
          if (result) {
            const text = result.getText();
            scanTip.value = '识别成功：' + text;
            onInputChange(text);
            setTimeout(() => stopScan(), 300);
          }
          // error 每帧未识别，静默忽略
        }
      );

      scanTip.value = '请将条码/二维码对准框内';
    } catch (err: any) {
      console.error('摄像头启动详细错误:', err);
      scanTip.value = '初始化失败: ' + (err?.name || '') + ' ' + (err?.message || '');
      createMessage.error('摄像头启动失败：' + (err?.message || String(err)));
    }
  }

  async function stopScan() {
    scanVisible.value = false;
    if (controls) {
      try {
        await controls.stop();
      } catch (e) {
        // ignore
      }
      controls = null;
    }
    codeReader = null;
  }
</script>

<style lang="less" scoped>
  .zxing-scan-input {
    display: flex;
    width: 100%;
  }

  .scan-box {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .scan-wrapper {
    position: relative;
    width: 100%;
    max-width: 380px;
    height: 240px;
    background: #000;
    border-radius: 4px;
    overflow: hidden;
  }

  .scan-video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  // ===== 微信风格覆盖层 =====
  .scan-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 10;
  }

  @scan-box-size: 220px;
  @scan-box-top: 10px;

  .scan-mask-top,
  .scan-mask-bottom,
  .scan-mask-left,
  .scan-mask-right {
    position: absolute;
    background: rgba(0, 0, 0, 0.65);
  }

  .scan-mask-top {
    top: 0;
    left: 0;
    right: 0;
    height: calc(50% - @scan-box-size / 2 + @scan-box-top);
  }

  .scan-mask-bottom {
    bottom: 0;
    left: 0;
    right: 0;
    height: calc(50% - @scan-box-size / 2 - @scan-box-top);
  }

  .scan-mask-left {
    top: calc(50% - @scan-box-size / 2 + @scan-box-top);
    left: 0;
    width: calc(50% - @scan-box-size / 2);
    height: @scan-box-size;
  }

  .scan-mask-right {
    top: calc(50% - @scan-box-size / 2 + @scan-box-top);
    right: 0;
    width: calc(50% - @scan-box-size / 2);
    height: @scan-box-size;
  }

  // 四角角标
  .corner {
    position: absolute;
    width: 20px;
    height: 20px;
    border-color: #00ff00;
    border-style: solid;
    z-index: 11;
  }

  .corner-tl {
    top: calc(50% - @scan-box-size / 2 + @scan-box-top);
    left: calc(50% - @scan-box-size / 2);
    border-width: 3px 0 0 3px;
  }

  .corner-tr {
    top: calc(50% - @scan-box-size / 2 + @scan-box-top);
    right: calc(50% - @scan-box-size / 2);
    border-width: 3px 3px 0 0;
  }

  .corner-bl {
    bottom: calc(50% - @scan-box-size / 2 - @scan-box-top);
    left: calc(50% - @scan-box-size / 2);
    border-width: 0 0 3px 3px;
  }

  .corner-br {
    bottom: calc(50% - @scan-box-size / 2 - @scan-box-top);
    right: calc(50% - @scan-box-size / 2);
    border-width: 0 3px 3px 0;
  }

  // 移动扫描线
  .scan-line {
    position: absolute;
    left: calc(50% - @scan-box-size / 2);
    width: @scan-box-size;
    height: 2px;
    background: linear-gradient(
      to right,
      transparent 0%,
      #00ff00 20%,
      #00ff00 80%,
      transparent 100%
    );
    box-shadow: 0 0 4px #00ff00;
    animation: scanMove 2.5s linear infinite;
    z-index: 12;
  }

  @keyframes scanMove {
    0% {
      top: calc(50% - @scan-box-size / 2 + @scan-box-top);
      opacity: 0;
    }
    10% {
      opacity: 1;
    }
    90% {
      opacity: 1;
    }
    100% {
      top: calc(50% - @scan-box-size / 2 + @scan-box-top + @scan-box-size);
      opacity: 0;
    }
  }

  .scan-tip {
    margin-top: 8px;
    color: #666;
    font-size: 13px;
    text-align: center;
  }
</style>

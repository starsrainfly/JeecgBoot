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
    <a-button class="ml-2" type="default" @click="openScan">
      <template #icon>
        <Icon icon="ant-design:camera-outlined" />
      </template>
    </a-button>

    <a-modal
      v-model:visible="scanVisible"
      title="摄像头扫码"
      :footer="null"
      :width="420"
      :destroyOnClose="true"
      @cancel="stopScan"
    >
      <div class="scan-box">
        <div id="scan-reader" ref="readerRef" class="scan-reader" />
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
        { fps: 10, qrbox: { width: 250, height: 250 } },
        (decodedText: string) => {
          console.error('===== 扫码回调进入 =====');
          console.error('decodedText:', decodedText);
          console.error('emit 函数存在:', typeof emit === 'function');

          onInputChange(decodedText);
          console.log("emit change");
          scanTip.value = '识别成功：' + decodedText;
          emit('update:value', decodedText);
          emit('change', decodedText);
          setTimeout(() => stopScan(), 300);
        },
        () => {
          // 帧级别扫码失败，静默忽略
        }
      );
      scanTip.value = '请将条码/二维码对准框内';
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
  }
  .scan-reader {
    width: 100%;
    max-width: 380px;
    min-height: 240px;
    background: #000;
    border-radius: 4px;
    overflow: hidden;
  }
  .scan-tip {
    margin-top: 8px;
    color: #666;
    font-size: 13px;
    text-align: center;
  }
</style>

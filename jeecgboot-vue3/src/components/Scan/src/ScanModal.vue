<<template>
  <a-modal
    :visible="visible"
    :title="title"
    :footer="null"
    :width="width"
    :destroyOnClose="true"
    @cancel="handleCancel"
  >
    <div class="scan-modal-body">
      <p v-if="tipText" class="scan-tip">{{ tipText }}</p>

      <component
        :is="scanComponent"
        v-model:value="scanValue"
        @change="handleScanChange"
        style="width: 100%;"
      />

      <div v-if="showResult && scanValue" class="scan-result">
        <a-tag color="green">{{ scanValue }}</a-tag>
      </div>
    </div>

    <div class="scan-modal-footer">
      <a-button @click="handleCancel">取消</a-button>
      <a-button type="primary" :disabled="!scanValue" @click="handleConfirm">确认</a-button>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import ZxingScanInput from './ZxingScanInput.vue';
  import Html5ScanInput from './Html5ScanInput.vue';

  const props = defineProps({
    visible: { type: Boolean, required: true },
    title: { type: String, default: '扫码' },
    width: { type: Number, default: 420 },
    tipText: { type: String, default: '请扫描二维码/条码' },
    showResult: { type: Boolean, default: true },
    useZxing: { type: Boolean, default: true },
  });

  const emit = defineEmits(['update:visible', 'scan', 'confirm', 'cancel']);

  const scanValue = ref('');

  const scanComponent = computed(() => {
    return props.useZxing ? ZxingScanInput : Html5ScanInput;
  });

  function handleScanChange(val: string) {
    emit('scan', val);
  }

  function handleConfirm() {
    if (!scanValue.value) return;
    emit('confirm', scanValue.value);
    emit('update:visible', false);
    scanValue.value = '';
  }

  function handleCancel() {
    emit('cancel');
    emit('update:visible', false);
    scanValue.value = '';
  }
</script>

<style lang="less" scoped>
  .scan-modal-body {
    padding: 16px 0;
    text-align: center;
  }
  .scan-tip {
    color: #666;
    margin-bottom: 12px;
  }
  .scan-result {
    margin-top: 12px;
    padding: 8px;
    background: #f6ffed;
    border-radius: 4px;
  }
  .scan-modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
  }
</style>

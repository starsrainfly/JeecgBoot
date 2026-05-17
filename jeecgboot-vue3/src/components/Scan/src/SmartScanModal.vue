<<template>
  <a-modal
    :visible="visible"
    :title="modalTitle"
    :footer="null"
    :width="600"
    :body-style="{ padding: '24px' }"
    :destroyOnClose="true"
    @cancel="handleCancel"
  >
    <div class="scan-modal-body">
      <p class="scan-tip">{{ tipText }}</p>

      <component
        :is="scanComponent"
        v-model:value="scanValue"
        @change="handleScanChange"
        style="width: 100%;"
      />

      <!-- 解析结果预览 -->
      <div v-if="parsedResult" class="scan-result">
        <a-descriptions bordered size="small" :column="1">
          <a-descriptions-item label="类型">
            <a-tag :color="typeColor">{{ typeText }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-for="(val, key) in displayFields" :key="key" :label="key">
            {{ val || '-' }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </div>

    <div class="scan-modal-footer">
<!--      <a-button @click="handleCancel">取消</a-button>-->
<!--      <a-button type="primary" :disabled="!parsedResult" @click="handleConfirm">应用查询</a-button>-->
      <a-button @click="handleCancel">
        <Icon icon="ant-design:close-outlined" /> 取消
      </a-button>
      <a-button type="primary" :disabled="!parsedResult" @click="handleConfirm">
        <Icon icon="ant-design:check-outlined" /> 应用查询
      </a-button>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import  ZxingScanInput  from './ZxingScanInput.vue';

  const props = defineProps({
    visible: { type: Boolean, required: true },
  });

  const emit = defineEmits(['update:visible', 'scan', 'query']);

  const scanValue = ref('');
  const parsedResult = ref<any>(null);

  const scanComponent = ZxingScanInput;

  const modalTitle = computed(() => {
    if (!parsedResult.value) return '智能扫码';
    const map: Record<string, string> = {
      LOCATION: '库位扫码',
      MATERIAL: '物料扫码',
      PRODUCT: '产品扫码',
    };
    return map[parsedResult.value.type] || '扫码结果';
  });

  const tipText = computed(() => {
    if (!parsedResult.value) return '请扫描库位/物料/产品二维码';
    return '识别成功，点击"应用查询"';
  });

  const typeText = computed(() => {
    const map: Record<string, string> = {
      LOCATION: '库位',
      MATERIAL: '物料',
      PRODUCT: '产品',
    };
    return map[parsedResult.value?.type] || '未知';
  });

  const typeColor = computed(() => {
    const map: Record<string, string> = {
      LOCATION: 'blue',
      MATERIAL: 'green',
      PRODUCT: 'orange',
    };
    return map[parsedResult.value?.type] || 'default';
  });

  const displayFields = computed(() => {
    if (!parsedResult.value) return {};
    const { type, ...fields } = parsedResult.value;
    return fields;
  });

  // ===== 核心：统一解析 =====
  function parseQr(qrContent: string): any {
    try {
      const data = JSON.parse(qrContent);
      const t = data.t;

      // 库位码
      if (t === 'LOCATION' || data.wId || data.w) {
        return {
          type: 'LOCATION',
          warehouseId: data.wId || data.w,
          areaId: data.aId || data.a,
          shelfId: data.shId || data.sh,
          locationId: data.lId || data.l,
          pathCode: data.pathCode || data.p,
        };
      }

      // 物料码
      if (t === 'MATERIAL' || data.materialId || data.materialCode) {
        return {
          type: 'MATERIAL',
          goodsId: data.materialId || data.goodsId || data.id,
          goodsCode: data.materialCode || data.goodsCode || data.code,
          goodsName: data.materialName || data.goodsName || data.name,
        };
      }

      // 产品码（带批次）
      if (t === 'PRODUCT' || data.productCode || data.p) {
        return {
          type: 'PRODUCT',
          goodsCode: data.productCode || data.p || data.goodsCode || data.code,
          batchNo: data.batchNo || data.batch || data.b,
          // 产品码可能也带ID
          goodsId: data.productId || data.goodsId || data.id ,
        };
      }

      return null;
    } catch (e) {
      // 非JSON，尝试当作文本匹配
      return {
        type: 'PRODUCT',
        goodsCode: qrContent,
      };
    }
  }

  function handleScanChange(val: string) {
    scanValue.value = val;
    parsedResult.value = parseQr(val);
    emit('scan', { raw: val, parsed: parsedResult.value });
  }

  function handleConfirm() {
    if (!parsedResult.value) return;

    // 统一转成 Stock 查询参数格式
    const result = parsedResult.value;
    const queryParams: any = {};

    if (result.type === 'LOCATION') {
      if (result.warehouseId) queryParams.warehouseId = result.warehouseId;
      if (result.areaId) queryParams.areaId = result.areaId;
      if (result.shelfId) queryParams.shelfId = result.shelfId;
      if (result.locationId) queryParams.locationId = result.locationId;
    } else if (result.type === 'MATERIAL' || result.type === 'PRODUCT') {
      if (result.goodsId) queryParams.goodsId = result.goodsId;
      if (result.goodsCode) queryParams.goodsCode = result.goodsCode;
      if (result.batchNo) queryParams.batchNo = result.batchNo;
    }

    emit('query', queryParams);
    emit('update:visible', false);
    reset();
  }

  function handleCancel() {
    emit('update:visible', false);
    reset();
  }

  function reset() {
    scanValue.value = '';
    parsedResult.value = null;
  }
</script>

<style lang="less" scoped>
  .scan-modal-body {
    padding: 16px 0;
  }
  .scan-tip {
    text-align: center;
    color: #666;
    margin-bottom: 12px;
  }
  .scan-result {
    margin-top: 16px;
  }
  .scan-modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
  }
</style>

<template>
  <a-modal
    v-model:visible="visible"
    :title="modalTitle"
    @ok="handleConfirm"
    :confirmLoading="loading"
    width="900px"
    :destroyOnClose="true"
    :maskClosable="false"
  >
    <a-row :gutter="24">
      <!-- 左侧：后端生成的 PNG 预览 -->
      <a-col :span="14">
        <div class="preview-box">
          <div v-if="generating" class="generating">
            <a-spin /> 生成预览中...
          </div>
          <div v-else-if="!imageBase64" class="no-image">
            点击"刷新预览"生成图片
          </div>
          <img
            v-else
            :src="imageBase64"
            style="max-width: 100%; height: auto; display: block;"
            class="label-image"
          />
        </div>
        <div class="size-info">
          <span>{{ data.labelWidth }}mm × {{ data.labelHeight }}mm</span>
          <a-space>
            <a-button type="link" size="small" @click="downloadPng" :disabled="!imageBase64">
              <Icon icon="ant-design:download-outlined" /> 下载图片
            </a-button>
            <a-button type="link" size="small" @click="downloadPdf" :disabled="!imageBase64">
              <Icon icon="ant-design:file-pdf-outlined" /> 下载PDF
            </a-button>
            <a-button type="link" size="small" @click="refreshPreview" :loading="generating">
              <Icon icon="ant-design:reload-outlined" /> 刷新预览
            </a-button>
          </a-space>
        </div>
      </a-col>

      <!-- 右侧：打印信息 -->
      <a-col :span="10">
        <a-descriptions :column="1" bordered size="small">
          <!-- 公共字段 -->
          <a-descriptions-item label="作业编号">{{ data.taskNo }}</a-descriptions-item>
          <a-descriptions-item label="标签尺寸">{{ data.labelWidth }}mm × {{ data.labelHeight }}mm</a-descriptions-item>
          <a-descriptions-item label="模板编码">{{ data.templateCode }}</a-descriptions-item>
          <a-descriptions-item label="公司名称">{{ data.companyName || '-' }}</a-descriptions-item>

          <!-- 产品标签字段 -->
          <template v-if="data.templateType === 'PRODUCT'">
            <a-descriptions-item label="产品名称">{{ data.productName }}</a-descriptions-item>
            <a-descriptions-item label="产品编码">{{ data.productCode }}</a-descriptions-item>
            <a-descriptions-item label="产品颜色">{{ data.productColor }}</a-descriptions-item>
            <a-descriptions-item label="批次号">{{ data.batchNo }}</a-descriptions-item>
            <a-descriptions-item label="打印产品名称">{{ data.printProductName || '-' }}</a-descriptions-item>
          </template>

          <!-- 库位标签字段 -->
          <template v-if="data.templateType === 'LOCATION'">
            <a-descriptions-item label="库位编码">{{ data.locationCode }}</a-descriptions-item>
            <a-descriptions-item label="库位名称">{{ data.locationName }}</a-descriptions-item>
            <a-descriptions-item label="组合码">{{ data.pathCode }}</a-descriptions-item>
            <a-descriptions-item label="所属仓库">{{ data.warehouseName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="所属区域">{{ data.areaName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="所属货架">{{ data.shelfName || '-' }}</a-descriptions-item>
          </template>
        </a-descriptions>

        <a-divider />

        <a-form :model="data" layout="vertical">
          <a-form-item label="打印份数" required>
            <template #default>
              <a-input-number
                v-model:value="data.copies"
                :min="1"
                :max="1000"
                style="width:100%"
              />
            </template>
          </a-form-item>
        </a-form>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script setup lang="ts">
  import { ref, reactive, watch, computed } from 'vue';
  import { generateLabelImage, confirmPrint } from '../LabelPrintTask.api';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { Icon } from '/@/components/Icon';
  import { jsPDF } from 'jspdf';

  const props = defineProps({
    title: { type: String, default: '' }, // 废弃，改用 modalTitle computed
  });

  const emit = defineEmits(['confirm', 'cancel']);

  const { createMessage } = useMessage();
  const visible = ref(false);
  const loading = ref(false);
  const generating = ref(false);
  const imageBase64 = ref('');

  const data = reactive({
    id: '',
    taskNo: '',
    templateType: 'PRODUCT',  // PRODUCT / LOCATION / BATCH
    productName: '',
    productCode: '',
    productColor: '',
    batchNo: '',
    printProductName: '',
    locationCode: '',
    locationName: '',
    pathCode: '',
    warehouseName: '',
    areaName: '',
    shelfName: '',
    copies: 1,
    labelWidth: 60,
    labelHeight: 35,
    templateCode: '',
    templateJson: '',
    companyName: '',
    qrContent: '',
    produceDate: '',
    qcStatus: '',
  });

  // 动态标题
  const modalTitle = computed(() => {
    const typeMap = {
      PRODUCT: '产品标签打印确认',
      LOCATION: '库位标签打印确认',
      BATCH: '批次标签打印确认',
    };
    return typeMap[data.templateType] || '标签打印确认';
  });

  // 生成预览图
  async function refreshPreview() {
    if (!data.id) return;
    generating.value = true;
    try {
      const res = await generateLabelImage({ id: data.id, dpi: 300 });
      let base64 = '';
      if (typeof res === 'string' && res.startsWith('data:image')) {
        base64 = res;
      } else if (res.result) {
        base64 = res.result;
      } else if (res.data) {
        base64 = res.data;
      }
      if (base64) {
        imageBase64.value = base64;
      } else {
        console.error('无法获取 Base64，响应结构:', res);
      }
    } catch (e) {
      console.error('生成预览失败', e);
    } finally {
      generating.value = false;
    }
  }

  // 下载 PNG
  function downloadPng() {
    if (!imageBase64.value) {
      createMessage.warning('请先生成预览');
      return;
    }
    const link = document.createElement('a');
    link.download = `标签_${data.taskNo}_${Date.now()}.png`;
    link.href = imageBase64.value;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    createMessage.success('图片下载成功');
  }

  // 下载 PDF
  function downloadPdf() {
    if (!imageBase64.value) {
      createMessage.warning('请先生成预览');
      return;
    }
    const base64Data = imageBase64.value.replace('data:image/png;base64,', '');
    const copies = data.copies;
    try {
      const pdf = new jsPDF({
        orientation: data.labelWidth > data.labelHeight ? 'l' : 'p',
        unit: 'mm',
        format: [data.labelWidth, data.labelHeight],
      });
      for (let i = 0; i < copies; i++) {
        if (i > 0) pdf.addPage([data.labelWidth, data.labelHeight]);
        pdf.addImage(base64Data, 'PNG', 0, 0, data.labelWidth, data.labelHeight);
      }
      pdf.save(`标签_${data.taskNo}_${Date.now()}.pdf`);
      createMessage.success(`PDF下载成功，共 ${copies} 页`);
    } catch (e) {
      console.error('PDF生成失败', e);
      createMessage.error('PDF生成失败');
    }
  }

  // 打印确认
  async function handleConfirm() {
    if (!imageBase64.value) {
      await refreshPreview();
      if (!imageBase64.value) return;
    }
    doPrint();
    try {
      await confirmPrint({ id: data.id, copies: data.copies });
    } catch (e) {
      console.error('打印记录失败', e);
    }
    emit('confirm', { id: data.id });
    visible.value = false;
  }

  function doPrint() {
    const copies = data.copies;
    const imgSrc = imageBase64.value;
    const w = window.open('', '_blank');
    if (!w) {
      alert('弹窗被拦截，请允许浏览器弹窗');
      return;
    }
    let imagesHtml = '';
    for (let i = 0; i < copies; i++) {
      imagesHtml += `<img src="${imgSrc}" class="label-img" />`;
    }
    w.document.write(`
      <!DOCTYPE html>
      <html>
        <head>
          <title>标签打印 - ${data.taskNo}</title>
          <style>
            @page { size: ${data.labelWidth}mm ${data.labelHeight}mm; margin: 0; }
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { display: flex; flex-direction: column; align-items: center; }
            .label-img { width: ${data.labelWidth}mm; height: ${data.labelHeight}mm; display: block; page-break-after: always; }
            .label-img:last-child { page-break-after: auto; }
          </style>
        </head>
        <body>
          ${imagesHtml}
          <script>
            window.onload = function() {
              setTimeout(function() { window.print(); setTimeout(function() { window.close(); }, 500); }, 200);
            };
          <\/script>
        </body>
      </html>
    `);
    w.document.close();
    w.focus();
  }

  // 打开弹窗 —— 核心改造：根据 templateType 自动加载对应字段
  async function open(record: any) {
    console.log("open record",record)
    // 重置所有字段
    Object.assign(data, {
      id: record.id || '',
      taskNo: record.taskNo || '',
      templateType: record.templateType || 'PRODUCT',
      templateCode: record.templateCode || '',
      templateJson: record.templateJson || '',
      labelWidth: record.labelWidth || 60,
      labelHeight: record.labelHeight || 35,
      companyName: record.companyName || record.companyId_dictText || '',
      copies: record.copies || 1,
      qrContent: record.qrContent || '',

      // 产品字段
      productName: record.productName || '',
      productCode: record.productCode || '',
      productColor: record.productColor || '',
      batchNo: record.batchNo || '',
      printProductName: record.printProductName || '',
      produceDate: record.produceDate || new Date().toLocaleDateString('zh-CN'),
      qcStatus: record.qcStatus || '合格',

      // 库位字段
      locationCode: record.locationCode || '',
      locationName: record.locationName || '',
      pathCode: record.pathCode || '',
      warehouseName: record.warehouseName || '',
      areaName: record.areaName || '',
      shelfName: record.shelfName || '',
    });

    imageBase64.value = '';
    visible.value = true;
    await refreshPreview();
  }

  watch(visible, (val) => {
    if (!val) {
      imageBase64.value = '';
      emit('cancel');
    }
  });

  defineExpose({ open });
</script>

<style lang="less" scoped>
  .preview-box {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 200px;
    background: #f5f5f5;
    border-radius: 6px;
    padding: 16px;
    .generating, .no-image { color: #999; font-size: 14px; }
    .label-image { display: block; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
  }
  .size-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    font-size: 12px;
    color: #666;
  }
</style>

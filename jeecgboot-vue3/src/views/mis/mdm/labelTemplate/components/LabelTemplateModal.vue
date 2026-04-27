<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="900"
    @ok="handleSubmit"
  >
    <!-- 上方：基本信息 - 三列布局 -->
    <BasicForm @register="registerBaseForm" />

    <!-- 中间：标签预览 -->
    <a-card title="标签预览" :bordered="false" :bodyStyle="{ padding: '12px' }" class="mt-2" size="small">
      <div class="preview-wrapper">
        <div class="preview-box" :style="previewStyle">
          <div
            v-for="(item, idx) in elements"
            :key="idx"
            class="preview-item"
            :style="getPreviewItemStyle(item)"
            :class="{ active: selectedField === item.field }"
            @click="selectElement(item)"
          >
            <template v-if="item.type === 'text'">
              {{ item.label || `[${item.field}]` }}
            </template>
            <div v-if="item.type === 'barcode'" class="fake-barcode">
              <div class="barcode-lines">▌▌▌▌▌▌▌▌▌▌</div>
              <div class="barcode-text">{{ getBarcodeText(item) }}</div>
            </div>
            <div v-if="item.type === 'qrcode'" class="fake-qrcode">
              <div class="qr-grid">▣</div>
              <div class="qr-label">{{ item.label || 'QR' }}</div>
            </div>
          </div>
        </div>
        <div class="preview-info">
          {{ labelWidth }}×{{ labelHeight }}mm @ {{ dpi }}DPI
          <span v-if="selectedField" class="selected-info">选中: {{ selectedField }}</span>
        </div>
      </div>
    </a-card>

    <!-- 下方：元素配置 -->
    <a-card title="元素配置" :bordered="false" :bodyStyle="{ padding: '8px' }" class="mt-2" size="small">
      <a-table
        :columns="elementColumns"
        :dataSource="elements"
        :pagination="false"
        size="small"
        bordered
        :scroll="{ y: 180 }"
      >
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.key === 'type'">
            <a-tag :color="record.type === 'text' ? 'blue' : record.type === 'barcode' ? 'green' : 'purple'">
              {{ record.type === 'text' ? '文本' : record.type === 'barcode' ? '条码' : '二维码' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'position'">
            X:{{ record.x }}, Y:{{ record.y }}
          </template>
          <template v-if="column.key === 'size'">
            <span v-if="record.type === 'text'">{{ record.fontSize || 10 }}pt</span>
            <span v-else-if="record.type === 'barcode'">{{ record.width || 30 }}×{{ record.height || 10 }}mm</span>
            <span v-else-if="record.type === 'qrcode'">{{ record.size || 18 }}mm
              <a-tag v-if="record.qrFormat" color="cyan" class="ml-1">
              {{ record.qrFormat === 'standard_json' ? '标准' : record.qrFormat === 'batch_only' ? '精简' : '完整' }}
              </a-tag>
            </span>
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'action'">
            <a-button type="link" size="small" @click="editElement(record, index)">编辑</a-button>
            <a-button type="link" danger size="small" @click="removeElement(index)">删除</a-button>
          </template>
        </template>
      </a-table>
      <a-button type="dashed" block class="mt-2" @click="addElement">
        <Icon icon="ant-design:plus-outlined" /> 添加元素
      </a-button>
    </a-card>

    <!-- 元素编辑弹窗 -->
    <ElementEditModal @register="registerElementModal" @save="saveElement" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import type { CSSProperties } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { Icon } from '/@/components/Icon';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useModal } from '/@/components/Modal';
  import ElementEditModal from './ElementEditModal.vue';
  import { baseFormSchema } from '../LabelTemplate.data';
  import { saveOrUpdate } from '../LabelTemplate.api';

  const emit = defineEmits(['register', 'success']);
  const { createMessage } = useMessage();

  const isUpdate = ref(false);
  const isDetail = ref(false);
  const isCopy = ref(false);
  const elements = ref<any[]>([]);
  const selectedField = ref('');
  const editingIndex = ref(-1);

  const [registerElementModal, { openModal: openElementModal }] = useModal();

  // 三列布局
  const [registerBaseForm, { getFieldsValue, setFieldsValue, validate, resetFields }] = useForm({
    schemas: baseFormSchema,
    showActionButtonGroup: false,
    labelWidth: 85,
    baseColProps: { span: 8 },  // 三列：24/8=3列
  });

  const title = computed(() => {
    if (unref(isCopy)) return '复制模板';
    if (!unref(isUpdate)) return '新增模板';
    return unref(isDetail) ? '模板详情' : '编辑模板';
  });

  const labelWidth = computed(() => getFieldsValue()?.labelWidth || 60);
  const labelHeight = computed(() => getFieldsValue()?.labelHeight || 35);
  const dpi = computed(() => getFieldsValue()?.dpi || 300);

  const previewScale = 2.8;
  const previewStyle = computed((): CSSProperties => ({
    width: `${unref(labelWidth) * previewScale}px`,
    height: `${unref(labelHeight) * previewScale}px`,
    position: 'relative',
    border: '1px solid #d9d9d9',
    background: '#fff',
    margin: '0 auto',
    overflow: 'hidden',
  }));

  const elementColumns = [
    { title: '字段', dataIndex: 'field', width: 100 },
    { title: '类型', key: 'type', width: 70 },
    { title: '标签', dataIndex: 'label', width: 90 },
    { title: '位置', key: 'position', width: 80 },
    { title: '大小', key: 'size', width: 80 },
    { title: '操作', key: 'action', width: 100 },
  ];

  function getPreviewItemStyle(item: any): CSSProperties {
    const scale = previewScale;
    const style: CSSProperties = {
      position: 'absolute',
      left: `${(item.x || 0) * scale}px`,
      top: `${(item.y || 0) * scale}px`,
      fontSize: `${(item.fontSize || 10) * (scale / 3.5)}px`,
      fontWeight: item.bold ? 'bold' : 'normal',
      color: '#000',
      border: selectedField.value === item.field ? '2px solid #1890ff' : '1px dashed #ccc',
      background: selectedField.value === item.field ? 'rgba(24,144,255,0.1)' : 'rgba(0,0,0,0.02)',
      padding: '1px 3px',
      cursor: 'pointer',
      whiteSpace: 'nowrap',
      overflow: 'hidden',
      zIndex: selectedField.value === item.field ? 10 : 1,
    };

    if (item.type === 'barcode') {
      style.width = `${(item.width || 26) * scale}px`;
      style.height = `${(item.height || 8) * scale}px`;
    }
    if (item.type === 'qrcode') {
      style.width = `${(item.size || 16) * scale}px`;
      style.height = `${(item.size || 16) * scale}px`;
    }

    return style;
  }

  // 条码显示模拟文本
  function getBarcodeText(item: any): string {
    if (item.field === 'batchNo') return 'WO202604240001';
    if (item.field === 'productName') return '300AWL-3X';
    return 'BARCODE';
  }

  function selectElement(item: any) {
    selectedField.value = item.field;
  }

  function addElement() {
    editingIndex.value = -1;
    openElementModal(true, {
      isUpdate: false,
      record: {
        type: 'text',
        field: '',
        label: '',
        x: 2,
        y: 2,
        fontSize: 10,
        bold: false,
        width: 26,
        height: 8,
        size: 16,
      },
    });
  }

  function editElement(record: any, index: number) {
    editingIndex.value = index;
    openElementModal(true, {
      isUpdate: true,
      record: { ...record },
    });
    selectedField.value = record.field;
  }

  function removeElement(index: number) {
    const removedField = elements.value[index]?.field;
    elements.value.splice(index, 1);
    if (selectedField.value === removedField) {
      selectedField.value = '';
    }
  }

  function saveElement(data: any) {
    if (editingIndex.value >= 0) {
      elements.value[editingIndex.value] = data;
    } else {
      elements.value.push(data);
    }
    selectedField.value = data.field;
    console.log('保存元素:', data);  // 加日志看 qrFormat 是否存在
  }

  function generateDataMapping() {
    const mapping: Record<string, string> = {};
    elements.value.forEach((item: any) => {
      switch (item.field) {
        case 'companyName': mapping[item.field] = 'sys.tenantName'; break;
        case 'productName': mapping[item.field] = 'product.productName'; break;
        case 'productCode': mapping[item.field] = 'product.productCode'; break;
        case 'color': mapping[item.field] = 'product.color'; break;
        case 'batchNo': mapping[item.field] = 'batch.batchNo'; break;
        case 'batchNoText': mapping[item.field] = 'batch.batchNo'; break;
        case 'produceDate': mapping[item.field] = 'batch.produceDate'; break;
        case 'expiryDate': mapping[item.field] = 'batch.expiryDate'; break;
        case 'spec': mapping[item.field] = 'product.spec'; break;
        case 'qrCode':
            // standard_json
            mapping[item.field] = '{"p":"${product.productCode}","b":"${batch.batchNo}","d":"${batch.produceDate}","e":"${batch.expiryDate}","s":"${product.spec}"}';

          break;
        case 'qcStatus': mapping[item.field] = 'batch.qcStatus'; break;
      }
    });
    return mapping;
  }

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data: any) => {
    await resetFields();
    elements.value = [];
    selectedField.value = '';

    isUpdate.value = !!data?.isUpdate;
    isDetail.value = !data?.showFooter;
    isCopy.value = !!data?.isCopy;

    setModalProps({
      confirmLoading: false,
      showCancelBtn: !!data?.showFooter,
      showOkBtn: !!data?.showFooter,
    });

    if ((unref(isUpdate) || unref(isCopy)) && data?.record) {
      const recordData = { ...data.record };

      if (unref(isCopy)) {
        recordData.id = undefined;
        recordData.templateCode = '';
        recordData.templateName = (recordData.templateName || '') + '-副本';
        recordData.isDefault = '0';
      }

      await setFieldsValue(recordData);

      if (data.record.contentJson) {
        try {
          const content = JSON.parse(data.record.contentJson);
          elements.value = content.elements || [];
        } catch (e) {
          console.error('解析模板内容失败', e);
        }
      }
    } else {
      await setFieldsValue({
        labelWidth: 60,
        labelHeight: 35,
        dpi: 300,
        templateType: 'PRODUCT',
        status: '1',
        isDefault: '0',
      });
    }
  });

  async function handleSubmit() {
    try {
      setModalProps({ confirmLoading: true });

      const baseValues = await validate();

      if (elements.value.length === 0) {
        createMessage.error('请至少添加一个元素');
        return;
      }

      const contentJson = JSON.stringify({
        version: '1.0',
        page: {
          width: baseValues.labelWidth,
          height: baseValues.labelHeight,
          dpi: baseValues.dpi,
        },
        elements: elements.value,
        dataMapping: generateDataMapping(),
      });

      const params = {
        ...baseValues,
        contentJson,
      };

      await saveOrUpdate(params, unref(isUpdate) && !unref(isCopy));
      createMessage.success('保存成功');
      emit('success');
      closeModal();
    } catch (error: any) {
      if (error?.errorFields) {
        return Promise.reject(error);
      }
      createMessage.error(error?.message || '保存失败');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-form) {
    .ant-form-item {
      margin-bottom: 6px !important;
    }
    .ant-form-item-label {
      padding-bottom: 2px;
      line-height: 22px;
    }
    .ant-form-item-control {
      line-height: 1;
    }
  }

  .mt-2 {
    margin-top: 8px;
  }

  .preview-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .preview-box {
    background-image:
      linear-gradient(#f0f0f0 1px, transparent 1px),
      linear-gradient(90deg, #f0f0f0 1px, transparent 1px);
    background-size: 14px 14px;
  }

  .preview-item {
    transition: all 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      border-color: #1890ff !important;
      z-index: 100;
    }
  }

  .fake-barcode {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    height: 100%;

    .barcode-lines {
      font-size: 5px;
      letter-spacing: 0.5px;
      line-height: 1;
      flex: 1;
      display: flex;
      align-items: center;
    }

    .barcode-text {
      font-size: 7px;
      line-height: 1.2;
    }
  }

  .fake-qrcode {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;

    .qr-grid {
      font-size: 16px;
      line-height: 1;
    }

    .qr-label {
      font-size: 7px;
      margin-top: 1px;
    }
  }

  .preview-info {
    text-align: center;
    color: #999;
    font-size: 11px;
    margin-top: 4px;

    .selected-info {
      color: #1890ff;
      margin-left: 8px;
    }
  }
</style>

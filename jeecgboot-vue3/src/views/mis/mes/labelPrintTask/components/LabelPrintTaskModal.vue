<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="1200"
    @ok="handleSubmit"
  >
    <a-row :gutter="24">
      <!-- 左侧：表单区域 -->
      <a-col :span="14">
        <BasicForm @register="registerForm" name="LabelPrintTaskForm" />
      </a-col>

      <!-- 右侧：预览区域 -->
      <a-col :span="10">
        <div class="preview-panel">
          <LabelPreview
            :labelWidth="previewData.labelWidth"
            :labelHeight="previewData.labelHeight"
            :productCode="previewData.productCode"
            :productName="previewData.productName"
            :printProductName="previewData.printProductName"
            :productColor="previewData.productColor"
            :batchNo="previewData.batchNo"
            :companyName="previewData.companyName"
            :qrContent="previewData.qrContent"
            :barcodeContent="previewData.barcodeContent"
            :templateJson="previewData.templateJson"
            :copies="previewData.copies"
            :status="previewData.status"
            :produceDate="previewData.produceDate"
            :qcStatus="previewData.qcStatus"
          />

          <!-- 打印设置快捷区（仅编辑/新增时显示） -->
          <div class="print-settings" v-if="!isDetail">
            <a-divider orientation="left">打印设置</a-divider>
            <a-form :model="printSettings" layout="vertical">
              <a-form-item label="打印份数">
                <a-input-number
                  v-model:value="printSettings.copies"
                  :min="1"
                  :max="100"
                  style="width: 100%"
                  @change="handleCopiesChange"
                />
              </a-form-item>
            </a-form>
          </div>

          <!-- 详情模式显示打印信息 -->
          <div class="print-info" v-if="isDetail && previewData.printTime">
            <a-divider orientation="left">打印信息</a-divider>
            <a-descriptions :column="1" size="small" bordered>
              <a-descriptions-item label="实际打印时间">{{ previewData.printTime }}</a-descriptions-item>
              <a-descriptions-item label="打印份数">{{ previewData.copies }} 份</a-descriptions-item>
            </a-descriptions>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- 打印结果弹窗 -->
    <a-modal
      v-model:visible="printResultVisible"
      title="打印结果"
      :footer="null"
      width="400px"
    >
      <a-result
        :status="printResultStatus"
        :title="printResultTitle"
        :sub-title="printResultSubTitle"
      >
        <template #extra>
          <a-button type="primary" @click="printResultVisible = false">知道了</a-button>
        </template>
      </a-result>
    </a-modal>
  </BasicModal>
</template>

<script lang="ts" setup>
  import {ref, computed, unref, reactive, onUnmounted} from 'vue';
  import {BasicModal, useModalInner} from '/@/components/Modal';
  import {BasicForm, useForm} from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import {formSchema} from '../LabelPrintTask.data';
  import {saveOrUpdate, printLabel, getCompanyInfo} from '../LabelPrintTask.api';
  import LabelPreview from './LabelPreview.vue';

  const emit = defineEmits(['register','success']);
  const { createMessage } = useMessage();

  const isUpdate = ref(true);
  const isDetail = ref(false);

  // 打印结果弹窗
  const printResultVisible = ref(false);
  const printResultStatus = ref('success');
  const printResultTitle = ref('');
  const printResultSubTitle = ref('');

  // 打印设置
  const printSettings = reactive({
    copies: 1,
  });

  // 预览数据
  const previewData = reactive({
    labelWidth: 60,
    labelHeight: 40,
    productCode: '',
    productName: '',
    printProductName: '',
    productColor: '',
    batchNo: '',
    companyName: '',
    qrContent: '',
    barcodeContent: '',
    copies: 1,
    status: 'PENDING',
    templateId: '',
    templateJson: '',
    printTime: '',
    produceDate: '',
    qcStatus: '合格',
  });

  // 上一次表单值（用于检测变化）
  let lastFormValues = {};
  let checkTimer = null;
  // 防重入：记录正在加载的公司ID
  let loadingCompanyId = null;

  // 表单配置
  const [registerForm, {
    setProps,
    resetFields,
    setFieldsValue,
    validate,
    scrollToField,
    getFieldsValue,
  }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    onValuesChange: (changedValues, allValues) => {
      syncPreviewData(allValues);
    },
  });

  // 核心：同步表单数据到预览区
  async function syncPreviewData(formValues: any) {
    if (!formValues) return;

    // 标签尺寸
    if (formValues.labelWidth !== undefined) {
      previewData.labelWidth = formValues.labelWidth || 60;
    }
    if (formValues.labelHeight !== undefined) {
      previewData.labelHeight = formValues.labelHeight || 40;
    }

    // 产品信息
    previewData.productCode = formValues.productCode || '';
    previewData.productName = formValues.productName || '';
    previewData.printProductName = formValues.printProductName || '';
    previewData.productColor = formValues.productColor || '';
    previewData.batchNo = formValues.batchNo || '';
    previewData.templateId = formValues.templateId || '';
    previewData.copies = formValues.copies || 1;
    previewData.status = formValues.status || 'PENDING';
    previewData.templateJson = formValues.templateJson || '';
    previewData.produceDate = formValues.produceDate || new Date().toLocaleDateString('zh-CN');
    previewData.qcStatus = formValues.qcStatus || '合格';

    // 条码内容 = 批次号
    previewData.barcodeContent = formValues.batchNo || '';

    // 生成二维码内容（按模板 dataMapping 格式）
    const qrValue = generateQrContent(formValues, previewData.templateJson);
    if (qrValue) {
      previewData.qrContent = qrValue;
      // 回填到表单（如果不同）
      if (qrValue !== formValues.qrContent) {
        setFieldsValue({ qrContent: qrValue });
      }
    }

    console.log('【syncPreviewData】companyName=', previewData.companyName, 'batchNo=', previewData.batchNo, 'qcStatus=', previewData.qcStatus);
  }

  // 生成二维码内容 - 按模板 dataMapping.qrCode 格式
  function generateQrContent(values: any, templateJsonStr: string): string {
    if (!values.productCode && !values.batchNo) return '';

    try {
      // 尝试从模板JSON中解析dataMapping.qrCode格式
      if (templateJsonStr) {
        const tpl = JSON.parse(templateJsonStr);
        if (tpl.dataMapping && tpl.dataMapping.qrCode) {
          // 模板定义了二维码格式，按模板变量替换
          let qrTemplate = tpl.dataMapping.qrCode;
          // 替换变量 - 使用字符串替换避免正则问题
          qrTemplate = qrTemplate.split('${product.productCode}').join(values.productCode || '');
          qrTemplate = qrTemplate.split('${batch.batchNo}').join(values.batchNo || '');
          qrTemplate = qrTemplate.split('${batch.produceDate}').join(values.produceDate || new Date().toISOString().split('T')[0]);
          qrTemplate = qrTemplate.split('${batch.expiryDate}').join(values.expiryDate || '');
          qrTemplate = qrTemplate.split('${product.spec}').join(values.spec || '');

          // 如果替换后是有效的JSON字符串，解析后返回
          try {
            const qrObj = JSON.parse(qrTemplate);
            return JSON.stringify(qrObj);
          } catch (e) {
            // 不是JSON，直接返回替换后的字符串
            return qrTemplate;
          }
        }
      }
    } catch (e) {
      console.error('解析模板二维码格式失败', e);
    }

    // 默认格式（模板未定义时）
    const content = {
      p: values.productCode || '',
      b: values.batchNo || '',
      d: values.produceDate || new Date().toISOString().split('T')[0],
      e: values.expiryDate || '',
      s: values.spec || '',
    };
    return JSON.stringify(content);
  }

  // 加载公司信息 - 关键修复：防重入+失败后保留已有值
  async function loadCompanyInfo(companyId: string) {
    if (!companyId) {
      previewData.companyName = '';
      return;
    }

    // 防重入：如果正在加载同一个ID，直接返回
    if (loadingCompanyId === companyId) {
      console.log('【公司信息】正在加载中，跳过重复请求:', companyId);
      return;
    }

    loadingCompanyId = companyId;

    try {
      console.log('【加载公司信息】companyId=', companyId);
      const res = await getCompanyInfo(companyId);
      console.log('【公司信息返回】', res);
// 关键修复：兼容两种返回格式
      // 格式1: {success: true, result: {departName: '...'}}
      // 格式2: {id: '...', departName: '...'} (defHttp直接返回result)
      let result = null;

      if (res && typeof res === 'object') {
        if ('success' in res && 'result' in res) {
          // 格式1：标准Result对象
          if (res.success && res.result) {
            result = res.result;
          } else {
            console.warn('【公司信息】接口返回失败', res.message);
          }
        } else if ('departName' in res || 'orgName' in res || 'name' in res) {
          // 格式2：直接返回了result对象
          result = res;
          console.log('【公司信息】检测到直接返回result对象格式');
        }
      }
      if ( result) {//res.success && res.result
        // 兼容多种字段名
       // const result = res.result;
        const name = result.departName
          || result.orgName
          || result.name
          || result.depart_name
          || result.tenantName
          || '';

        // 关键：直接赋值预览数据
        previewData.companyName = name;
        console.log('【公司名称已设置】', name);

        // 回填到表单（不触发onValuesChange）
        setFieldsValue({ companyName: name });

        // 关键：强制重新获取表单值并同步预览（因为setFieldsValue不触发onValuesChange）
        const values = await getFieldsValue();
        await syncPreviewData(values);

        console.log('【预览已刷新】companyName=', previewData.companyName);
      } else {
        console.warn('【公司信息】接口返回失败', res.message);
        // 失败后不清空已有值（如果之前有值）
      }
    } catch (e) {
      console.error('【加载公司信息失败】', e);
      // 失败后不清空已有值
    } finally {
      loadingCompanyId = null;
    }
  }

  // 份数变化（右侧设置区）
  function handleCopiesChange() {
    previewData.copies = printSettings.copies;
    setFieldsValue({ copies: printSettings.copies });
  }

  // 定时检测表单变化（解决JPopup回填不触发onValuesChange）
  function startCheckTimer() {
    if (checkTimer) clearInterval(checkTimer);

    checkTimer = setInterval(async () => {
      try {
        const values = await getFieldsValue();
        const keyFields = ['labelWidth', 'labelHeight', 'batchNo', 'productCode', 'templateId', 'companyId', 'templateJson', 'productName'];
        let hasChanged = false;

        for (const key of keyFields) {
          if (values[key] !== lastFormValues[key]) {
            hasChanged = true;
            console.log(`【定时检测】${key} 变化:`, lastFormValues[key], '->', values[key]);

            // 公司ID变化时加载公司名称
            if (key === 'companyId') {
              if (values[key]) {
                loadCompanyInfo(values[key]);
              } else {
                previewData.companyName = '';
              }
            }
          }
        }

        if (hasChanged) {
          lastFormValues = { ...values };
          syncPreviewData(values);
        }
      } catch (e) {
        // 忽略
      }
    }, 300);
  }

  // 表单赋值
  const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
    if (checkTimer) clearInterval(checkTimer);
    loadingCompanyId = null; // 重置防重入

    await resetFields();

    setModalProps({
      confirmLoading: false,
      showCancelBtn: !!data?.showFooter,
      showOkBtn: !!data?.showFooter,
    });

    isUpdate.value = !!data?.isUpdate;
    isDetail.value = !data?.showFooter;

    if (unref(isUpdate) && data.record) {
      await setFieldsValue({ ...data.record });

      const r = data.record;
      previewData.labelWidth = r.labelWidth || 60;
      previewData.labelHeight = r.labelHeight || 40;
      previewData.productCode = r.productCode || '';
      previewData.productName = r.productName || '';
      previewData.printProductName = r.printProductName || '';
      previewData.productColor = r.productColor || '';
      previewData.batchNo = r.batchNo || '';
      previewData.copies = r.copies || 1;
      previewData.status = r.status || 'PENDING';
      previewData.templateId = r.templateId || '';
      previewData.templateJson = r.templateJson || '';
      previewData.printTime = r.printTime || '';
      previewData.produceDate = r.produceDate || '';
      previewData.qcStatus = r.qcStatus || '合格';
      printSettings.copies = r.copies || 1;

      // 加载公司信息
      if (r.companyId) {
        await loadCompanyInfo(r.companyId);
      } else {
        previewData.companyName = r.companyName || '';
      }

      lastFormValues = { ...r };
      syncPreviewData(r);
    } else {
      // 新增时默认值
      previewData.labelWidth = 60;
      previewData.labelHeight = 40;
      previewData.status = 'PENDING';
      previewData.copies = 1;
      printSettings.copies = 1;
      previewData.companyName = '';
      previewData.qrContent = '';
      previewData.barcodeContent = '';
      previewData.productCode = '';
      previewData.productName = '';
      previewData.printProductName = '';
      previewData.productColor = '';
      previewData.batchNo = '';
      previewData.templateId = '';
      previewData.templateJson = '';
      previewData.produceDate = '';
      previewData.qcStatus = '合格';
      lastFormValues = {};
    }

    setProps({ disabled: !data?.showFooter });
    startCheckTimer();
  });

  onUnmounted(() => {
    if (checkTimer) clearInterval(checkTimer);
  });

  // 标题
  const title = computed(() => {
    if (!unref(isUpdate)) return '新增标签打印任务';
    return !unref(isDetail) ? '编辑标签打印任务' : '标签打印任务详情';
  });

  // 表单提交事件（@ok 触发）
  async function handleSubmit() {
    if (isDetail.value) {
      closeModal();
      return;
    }

    try {
      let values = await validate();
      setModalProps({ confirmLoading: true });
      values.copies = printSettings.copies;

      await saveOrUpdate(values, isUpdate.value);
      createMessage.success('保存成功');
      closeModal();
      emit('success');
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }

  // 直接打印
  async function handlePrint() {
    try {
      const values = await getFieldsValue();

      if (!values.id) {
        createMessage.warning('请先保存任务，再执行打印');
        return;
      }

      const res = await printLabel({
        id: values.id,
        copies: printSettings.copies,
      });

      printResultVisible.value = true;
      if (res.success) {
        printResultStatus.value = 'success';
        printResultTitle.value = '打印成功';
        printResultSubTitle.value = `任务编号：${res.result?.taskNo || ''}，已打印 ${printSettings.copies} 份`;
        previewData.status = 'COMPLETED';
        previewData.printTime = res.result?.printTime || new Date().toLocaleString();
        createMessage.success('打印成功');
        emit('success');
      } else {
        printResultStatus.value = 'error';
        printResultTitle.value = '打印失败';
        printResultSubTitle.value = res.message || '请检查打印机状态';
        createMessage.error(res.message || '打印失败');
      }
    } catch (e) {
      printResultVisible.value = true;
      printResultStatus.value = 'error';
      printResultTitle.value = '打印异常';
      printResultSubTitle.value = e.message || '网络异常';
      createMessage.error('打印异常：' + e.message);
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-input-number) {
    width: 100%;
  }

  :deep(.ant-calendar-picker) {
    width: 100%;
  }

  .preview-panel {
    position: sticky;
    top: 24px;

    .print-settings {
      margin-top: 16px;
      padding: 12px;
      background: #fafafa;
      border-radius: 6px;
      border: 1px solid #e8e8e8;
    }

    .print-info {
      margin-top: 16px;
    }
  }
</style>

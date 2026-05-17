<<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    destroyOnClose
    :title="title"
    :width="1200"
    @ok="handleSubmit"
  >
    <a-row :gutter="24">
      <a-col :span="14">
        <BasicForm @register="registerForm" />
      </a-col>
      <a-col :span="10">
        <div class="preview-panel">
          <LabelPreviewUniversal
            :labelWidth="previewData.labelWidth"
            :labelHeight="previewData.labelHeight"
            :labelDataJson="previewData.labelDataJson"
            :templateJson="previewData.templateJson"
            :copies="previewData.copies"
            :status="previewData.status"
            :companyName="previewData.companyName"
            :qrContent="previewData.qrContent"
          />
          <div class="print-settings" v-if="!isDetail">
            <a-divider orientation="left">打印设置</a-divider>
            <a-form :model="printSettings" layout="vertical">
              <a-form-item label="打印份数">
                <a-input-number v-model:value="printSettings.copies" :min="1" :max="100" style="width: 100%" />
              </a-form-item>
            </a-form>
          </div>
        </div>
      </a-col>
    </a-row>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref, reactive, onUnmounted, watch } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { locationFormSchema } from '../LabelPrintTask.data';
  import { saveOrUpdate, getCompanyInfo, getTemplateInfo } from '../LabelPrintTask.api';
  import { defHttp } from '/@/utils/http/axios';
  import LabelPreviewUniversal from './LabelPreviewUniversal.vue';

  const emit = defineEmits(['register', 'success']);
  const { createMessage } = useMessage();

  const isUpdate = ref(true);
  const isDetail = ref(false);
  const printSettings = reactive({ copies: 1 });
  const isInitializing = ref(false);

  const previewData = reactive({
    labelWidth: 60,
    labelHeight: 40,
    labelDataJson: '{}',
    templateJson: '',
    templateName: '',
    copies: 1,
    status: 'PENDING',
    companyName: '',
    qrContent: '',
    qrImage: '',
  });

  // 库位详情缓存（含各层级ID）
  const locationInfo = reactive({
    locationId: '',
    locationCode: '',
    locationName: '',
    pathCode: '',
    warehouseId: '',
    warehouseName: '',
    areaId: '',
    areaName: '',
    shelfId: '',
    shelfName: '',
  });

  let checkTimer = null;
  let loadingCompanyId = null;

  const [registerForm, { setProps, resetFields, setFieldsValue, validate, getFieldsValue, updateSchema }] = useForm({
    schemas: locationFormSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    // onValuesChange: (changedValues, allValues) => {
    // syncPreviewData(allValues);
    // // 任何层级变化都触发加载和清空下级
    // if (changedValues.warehouseId) {
    //   loadWarehouseDetail(changedValues.warehouseId);
    //  setFieldsValue({ areaId: undefined, shelfId: undefined, locationId: undefined });
    //  clearLocationInfo();
    // }
    // if (changedValues.areaId) {
    //   loadAreaDetail(changedValues.areaId);
    //  setFieldsValue({ shelfId: undefined, locationId: undefined });
    //  clearLocationInfo('area');
    // }
    // if (changedValues.shelfId) {
    //   loadShelfDetail(changedValues.shelfId);
    //  setFieldsValue({ locationId: undefined });
    //  clearLocationInfo('shelf');
    // }
    // // 库位变化时加载详情
    // if (changedValues.locationId) {
    //   loadLocationDetail(changedValues.locationId);
    // }
    // // 模板变化时更新预览
    // if (changedValues.templateId) {
    //   loadTemplateDetail(changedValues.templateId);
    // }
    // },
  });

  // 加载库位详情（含各层级ID）
  async function loadLocationDetail(locationId: string) {
    if (!locationId) return;
    try {
      const res = await defHttp.get({
        url: '/wms/warehouseLocation/queryById',
        params: { id: locationId }
      }, { isTransformResponse: false });

      const result = res?.result || res;
      if (result) {
        // 保留仓库、区域、货架
        const keepWarehouseId = locationInfo.warehouseId;
        const keepWarehouseName = locationInfo.warehouseName;
        //  const keepWarehouseCode = locationInfo.warehouseCode;
        const keepAreaId = locationInfo.areaId;
        const keepAreaName = locationInfo.areaName;
        // const keepAreaCode = locationInfo.areaCode;
        const keepShelfId = locationInfo.shelfId;
        const keepShelfName = locationInfo.shelfName;
        //  const keepShelfCode = locationInfo.shelfCode;

        // 只清库位
        locationInfo.locationId = '';
        locationInfo.locationCode = '';
        locationInfo.locationName = '';
        locationInfo.pathCode = '';

        locationInfo.warehouseId = keepWarehouseId;
        locationInfo.warehouseName = keepWarehouseName;
        // locationInfo.warehouseCode = keepWarehouseCode;
        locationInfo.areaId = keepAreaId;
        locationInfo.areaName = keepAreaName;
        //  locationInfo.areaCode = keepAreaCode;
        locationInfo.shelfId = keepShelfId;
        locationInfo.shelfName = keepShelfName;
        // locationInfo.shelfCode = keepShelfCode;
        // 缓存所有ID和编码
        locationInfo.locationId = result.id || '';
        locationInfo.locationCode = result.locationCode || '';
        locationInfo.locationName = result.name || '';
        locationInfo.pathCode = result.pathCode || '';
        locationInfo.warehouseId = result.warehouseId || '';
        locationInfo.areaId = result.areaId || '';
        locationInfo.shelfId = result.shelfId || '';

        await loadRelatedNames(result);

        // 回填到表单
        await setFieldsValue({
          locationCode: locationInfo.locationCode,
          locationName: locationInfo.locationName,
          pathCode: locationInfo.pathCode,
          warehouseName: locationInfo.warehouseName,
          areaName: locationInfo.areaName,
          shelfName: locationInfo.shelfName,
        });

        const values = await getFieldsValue();
        await syncPreviewData(values);
      }
    } catch (e) {
      console.error('加载库位详情失败', e);
    }
  }

  // 加载仓库/区域/货架名称
  async function loadRelatedNames(location: any) {
    try {
      const [warehouseRes, areaRes, shelfRes] = await Promise.all([
        location.warehouseId ? defHttp.get({ url: '/wms/warehouse/queryById', params: { id: location.warehouseId } }, { isTransformResponse: false }) : Promise.resolve(null),
        location.areaId ? defHttp.get({ url: '/wms/warehouseArea/queryById', params: { id: location.areaId } }, { isTransformResponse: false }) : Promise.resolve(null),
        location.shelfId ? defHttp.get({ url: '/wms/warehouseShelf/queryById', params: { id: location.shelfId } }, { isTransformResponse: false }) : Promise.resolve(null),
      ]);

      locationInfo.warehouseName = warehouseRes?.result?.name || '';
      locationInfo.areaName = areaRes?.result?.name || '';
      locationInfo.shelfName = shelfRes?.result?.name || '';

      await setFieldsValue({
        warehouseName: locationInfo.warehouseName,
        areaName: locationInfo.areaName,
        shelfName: locationInfo.shelfName,
      });
    } catch (e) {
      console.error('加载关联信息失败', e);
    }
  }

  // 加载模板详情（获取templateJson和templateName）
  async function loadTemplateDetail(templateId: string) {
    if (!templateId) return;
    try {
      // const res = await defHttp.get({
      //   url: '/mes/labelTemplate/queryById',
      //   params: { id: templateId }
      // }, { isTransformResponse: false });
      const res = await getTemplateInfo({id:templateId});
      const result = res?.result || res;
      if (result) {
        previewData.templateJson = result.contentJson || '';
        previewData.templateName = result.templateName || result.name || '';
        previewData.labelWidth = result.labelWidth || 60;
        previewData.labelHeight = result.labelHeight || 40;

        await setFieldsValue({
          templateJson: previewData.templateJson,
          templateName: previewData.templateName,
          labelWidth: previewData.labelWidth,
          labelHeight: previewData.labelHeight,
        });

        const values = await getFieldsValue();
        await syncPreviewData(values);
      }
    } catch (e) {
      console.error('加载模板详情失败', e);
    }
  }

  // 同步预览数据（核心方法）
  async function syncPreviewData(formValues: any) {
    if (!formValues) return;

    previewData.labelWidth = formValues.labelWidth || 60;
    previewData.labelHeight = formValues.labelHeight || 40;

    previewData.templateJson = formValues.templateJson || previewData.templateJson || '';

    previewData.templateName = formValues.templateName || previewData.templateName || '';
    previewData.copies = formValues.copies || 1;
    previewData.status = formValues.status || 'PENDING';

    const data: any = {
      // locationCode:  formValues.locationCode || '',
      // locationName:  formValues.locationName || '',
      // pathCode:  formValues.pathCode || '',
      // warehouseName:  formValues.warehouseName || '',
      // areaName:  formValues.areaName || '',
      // shelfName:  formValues.shelfName || '',
      locationCode: locationInfo.locationCode || '',
      locationName: locationInfo.locationName || '',
      pathCode: locationInfo.pathCode || '',
      warehouseName: locationInfo.warehouseName || '',
      areaName: locationInfo.areaName || '',
      shelfName: locationInfo.shelfName || '',
    };

    // 公司信息
    if (formValues.companyId) {
      await loadCompanyInfo(formValues.companyId);
    }
    data.companyName = previewData.companyName;

    // === 生成二维码内容（含各层级ID，类型LOCATION）===
    data.qrContent = JSON.stringify({
      t: 'LOCATION',                                    // 类型
      w: locationInfo.warehouseId || '',                // 仓库ID
      a: locationInfo.areaId || '',                     // 区域ID
      sh: locationInfo.shelfId || '',                   // 货架ID
      l: locationInfo.locationId || formValues.locationId || '',  // 库位ID
      p: data.pathCode,                                   // 组合码（人工可读）
    });

    previewData.qrContent = data.qrContent;

    // 合并仓库/区域/货架信息为一行显示
    data.warehouseInfo = [data.warehouseName, data.areaName, data.shelfName]
      .filter(Boolean)
      .join(' / ');

    previewData.labelDataJson = JSON.stringify(data);

    // === 调试用 ===
    console.log('=== syncPreviewData ===');
    console.log('templateJson:', previewData.templateJson ? '有值' : '空');
    console.log('labelDataJson:', previewData.labelDataJson);
    console.log('warehouseName:', data.warehouseName);
    console.log('qrContent:', previewData.qrContent);
  }

  async function loadCompanyInfo(companyId: string) {
    if (!companyId) { previewData.companyName = ''; return; }
    if (loadingCompanyId === companyId) return;
    loadingCompanyId = companyId;
    try {
      const res = await getCompanyInfo(companyId);
      const result = res?.result || res;
      previewData.companyName = result?.departName || result?.orgName || result?.name || '';
    } catch (e) {
      console.error('加载公司信息失败', e);
    } finally {
      loadingCompanyId = null;
    }
  }

  function startCheckTimer(initialValues = {}) {
    if (checkTimer) clearInterval(checkTimer);
    let lastValues = { ...initialValues }; // 用传入的值初始化，而不是空对象
    checkTimer = setInterval(async () => {
      try {
        const values = await getFieldsValue();
        const keyFields = ['warehouseId','areaId','shelfId','locationId', 'templateId', 'companyId', 'templateJson'];
        let hasChanged = false;
        let needSync = false;

        for (const key of keyFields) {
          if (values[key] !== lastValues[key]) {
            hasChanged = true;

            // 处理清空的情况（从有值变无值）
            if (!values[key] && lastValues[key]) {
              if (key === 'locationId') {
                clearLocationInfo('location');
                needSync = true;
              } else if (key === 'shelfId') {
                clearLocationInfo('shelf');
                needSync = true;
              } else if (key === 'areaId') {
                clearLocationInfo('area');
                needSync = true;
              } else if (key === 'warehouseId') {
                clearLocationInfo();
                needSync = true;
              }
            }

            if (key === 'companyId' && values[key]) {
              await loadCompanyInfo(values[key]);
              needSync = true
            }
            if (key === 'locationId' && values[key]) {
              await loadLocationDetail(values[key]);
              needSync = true
            }
            if (key === 'templateId' && values[key]) {
              await loadTemplateDetail(values[key]);
              needSync = true
            }
            if(key === 'warehouseId' && values[key]){
              await loadWarehouseDetail(values[key],true);
              needSync = true
            }
            if(key === 'areaId' && values[key]){
              await loadAreaDetail(values[key],true);
              needSync = true
            }
            if(key === 'shelfId' && values[key]){
              await loadShelfDetail(values[key],true);
              needSync = true
            }
          }
        }
        if (hasChanged) {
          lastValues = { ...values };
          if (needSync) {
            await syncPreviewData(values);
          }
        }
      } catch (e) {}
    }, 300);
  }

  //新增三个方法 加载仓库 区域 货架
  async function loadWarehouseDetail(warehouseId: string, fromUserSelect = true) {
    if (!warehouseId) return;
    try {
      const res = await defHttp.get({
        url: '/wms/warehouse/queryById',
        params: { id: warehouseId }
      }, { isTransformResponse: false });
      const result = res?.result || res;
      if (result) {
        // 只有用户选择时才清空下级
        if (fromUserSelect) {
          clearLocationInfo();
          await setFieldsValue({
            areaId: undefined,
            shelfId: undefined,
            locationId: undefined,
            areaName: '',
            shelfName: '',
            locationName: '',
            locationCode: '',
            pathCode: '',
          });
        }

        locationInfo.warehouseId = result.id || '';
        locationInfo.warehouseName = result.name || '';
        locationInfo.warehouseCode = result.code || '';

        await setFieldsValue({ warehouseName: locationInfo.warehouseName });
      }
    } catch (e) {
      console.error('加载仓库详情失败', e);
    }
  }

  async function loadAreaDetail(areaId: string, fromUserSelect = true) {
    if (!areaId) return;
    try {
      const res = await defHttp.get({
        url: '/wms/warehouseArea/queryById',
        params: { id: areaId }
      }, { isTransformResponse: false });
      const result = res?.result || res;
      if (result) {
        if (fromUserSelect) {
          // 保留仓库，清空货架及以下
          const keepWarehouseId = locationInfo.warehouseId;
          const keepWarehouseName = locationInfo.warehouseName;
          const keepWarehouseCode = locationInfo.warehouseCode;

          clearLocationInfo('area');

          locationInfo.warehouseId = keepWarehouseId;
          locationInfo.warehouseName = keepWarehouseName;
          locationInfo.warehouseCode = keepWarehouseCode;

          await setFieldsValue({
            shelfId: undefined,
            locationId: undefined,
            shelfName: '',
            locationName: '',
            locationCode: '',
            pathCode: '',
          });
        }

        locationInfo.areaId = result.id || '';
        locationInfo.areaName = result.name || '';
        locationInfo.areaCode = result.code || '';

        // 自动回填仓库（如果区域带了warehouseId且当前没选仓库）
        if (result.warehouseId && !locationInfo.warehouseId) {
          await setFieldsValue({ warehouseId: result.warehouseId });
          await loadWarehouseDetail(result.warehouseId, fromUserSelect);
        }

        await setFieldsValue({ areaName: locationInfo.areaName });
      }
    } catch (e) {
      console.error('加载区域详情失败', e);
    }
  }

  async function loadShelfDetail(shelfId: string, fromUserSelect = true) {
    if (!shelfId) return;
    try {
      const res = await defHttp.get({
        url: '/wms/warehouseShelf/queryById',
        params: { id: shelfId }
      }, { isTransformResponse: false });
      const result = res?.result || res;
      if (result) {
        if (fromUserSelect) {
          // 保留仓库和区域
          const keepWarehouseId = locationInfo.warehouseId;
          const keepWarehouseName = locationInfo.warehouseName;
          const keepWarehouseCode = locationInfo.warehouseCode;
          const keepAreaId = locationInfo.areaId;
          const keepAreaName = locationInfo.areaName;
          const keepAreaCode = locationInfo.areaCode;

          clearLocationInfo('shelf');

          locationInfo.warehouseId = keepWarehouseId;
          locationInfo.warehouseName = keepWarehouseName;
          locationInfo.warehouseCode = keepWarehouseCode;
          locationInfo.areaId = keepAreaId;
          locationInfo.areaName = keepAreaName;
          locationInfo.areaCode = keepAreaCode;

          await setFieldsValue({
            locationId: undefined,
            locationName: '',
            locationCode: '',
            pathCode: '',
          });
        }

        locationInfo.shelfId = result.id || '';
        locationInfo.shelfName = result.name || '';
        locationInfo.shelfCode = result.code || '';

        // 自动回填区域
        if (result.areaId && !locationInfo.areaId) {
          await setFieldsValue({ areaId: result.areaId });
          await loadAreaDetail(result.areaId, fromUserSelect);
        }

        await setFieldsValue({ shelfName: locationInfo.shelfName });
      }
    } catch (e) {
      console.error('加载货架详情失败', e);
    }
  }

  function clearLocationInfo(level?:string) {
    if (!level) {
      Object.assign(locationInfo, {
        warehouseId: '', warehouseName: '', warehouseCode: '',
        areaId: '', areaName: '', areaCode: '',
        shelfId: '', shelfName: '', shelfCode: '',
        locationId: '', locationCode: '', locationName: '', pathCode: ''
      });
    } else if (level === 'area') {
      Object.assign(locationInfo, {
        areaId: '', areaName: '', areaCode: '',
        shelfId: '', shelfName: '', shelfCode: '',
        locationId: '', locationCode: '', locationName: '', pathCode: ''
      });
    } else if (level === 'shelf') {
      Object.assign(locationInfo, {
        shelfId: '', shelfName: '', shelfCode: '',
        locationId: '', locationCode: '', locationName: '', pathCode: ''
      });
    }
  }

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    if (checkTimer) clearInterval(checkTimer);
    loadingCompanyId = null;

    // 重置库位信息
    Object.assign(locationInfo, {
      locationId: '', locationCode: '', locationName: '', pathCode: '',
      warehouseId: '', warehouseName: '', areaId: '', areaName: '', shelfId: '', shelfName: ''
    });

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
     // const templateRes = await getTemplateInfo({id:r.templateId});
      await loadTemplateDetail(r.templateId);
      previewData.labelWidth = r.labelWidth || 60;
      previewData.labelHeight = r.labelHeight || 35;
      previewData.copies = r.copies || 1;
      previewData.status = r.status || 'PENDING';
    //  previewData.templateJson = r.templateJson ||'';
    //  previewData.templateName = r.templateName || '';
      printSettings.copies = r.copies || 1;

      if(r.warehouseId){
        await loadWarehouseDetail(r.warehouseId, false);
      }
      if(r.areaId){
        await loadAreaDetail(r.areaId, false);
      }
      // 如果有locationId，加载详情
      if (r.locationId) {
        await loadLocationDetail(r.locationId);
      }

      if (r.companyId) {
        await loadCompanyInfo(r.companyId);
      } else {
        previewData.companyName = r.companyName || '';
      }

      if (r.labelDataJson) {
        previewData.labelDataJson = r.labelDataJson;
      }
      if (r.qrContent) {
        previewData.qrContent = r.qrContent;
      }
      // 最后同步一次预览
      const values = await getFieldsValue();
      await syncPreviewData(values);
    } else {
      // 新增状态重置
      previewData.labelWidth = 60;
      previewData.labelHeight = 40;
      previewData.status = 'PENDING';
      previewData.copies = 1;
      printSettings.copies = 1;
      previewData.companyName = '';
      previewData.qrContent = '';
      previewData.labelDataJson = '{}';
      previewData.templateJson = '';
      previewData.templateName = '';
    }

    setProps({ disabled: !data?.showFooter });
    const initValues = await getFieldsValue();
    startCheckTimer(initValues);
  });

  onUnmounted(() => {
    if (checkTimer) clearInterval(checkTimer);
  });

  const title = computed(() => {
    if (!unref(isUpdate)) return '新增库位标签打印任务';
    return !unref(isDetail) ? '编辑库位标签打印任务' : '库位标签打印任务详情';
  });

  // === 提交方法（关键修复：确保所有字段提交）===
  async function handleSubmit() {
    if (isDetail.value) {
      closeModal();
      return;
    }

    try {
      let values = await validate();
      setModalProps({ confirmLoading: true });

      // === 打印设置 ===
      values.copies = printSettings.copies;

      // === 模板信息 ===
      values.templateType = 'LOCATION';
      values.templateName = values.templateName || previewData.templateName || '';
      values.templateJson = previewData.templateJson || values.templateJson || '';
      values.labelWidth = previewData.labelWidth || values.labelWidth || 60;
      values.labelHeight = previewData.labelHeight || values.labelHeight || 35;

      // === 库位信息（关键：从locationInfo回填）===
      values.locationId = values.locationId || locationInfo.locationId || '';
      values.locationCode = locationInfo.locationCode || values.locationCode || '';
      values.locationName = locationInfo.locationName || values.locationName || '';
      values.pathCode = locationInfo.pathCode || values.pathCode || '';

      values.warehouseName = locationInfo.warehouseName || '';
      values.areaName = locationInfo.areaName || '';
      values.shelfName = locationInfo.shelfName || '';

      // === 二维码内容（从previewData取已生成的）===
      values.qrContent = previewData.qrContent || '';
      // qrImage由后端根据qrContent生成，前端不传
      values.qrImage = '';

      // === 标签数据 ===
      values.labelDataJson = previewData.labelDataJson;
      values.targetCode = locationInfo.locationCode || values.locationCode || '';
      values.targetName = locationInfo.pathCode || locationInfo.locationName || '';

      // === 公司信息 ===
      values.companyName = previewData.companyName || values.companyName || '';

      await saveOrUpdate(values, isUpdate.value);
      createMessage.success('保存成功');
      closeModal();
      emit('success');
    } catch ({ errorFields }) {
      return Promise.reject(errorFields);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-input-number), :deep(.ant-calendar-picker) { width: 100%; }
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
  }
</style>

<template>
  <div>
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-alert type="info" show-icon class="mb-2">
          <template #message>
            <span>交期预警：开启中（提前 <a-tag color="orange">{{ alertDays }}</a-tag> 天标红）</span>
            <a-button type="link" size="small" @click="showSetting = true">设置</a-button>
          </template>
        </a-alert>
      </template>

      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>

      <!-- 交期列：带预警颜色 -->
      <template #deliveryDate="{ record }">
        <span :style="{ color: getAlertColor(record.deliveryDate) }">
          {{ record.deliveryDate }}
          <a-tag v-if="isUrgent(record.deliveryDate)" color="red">即将到期</a-tag>
        </span>
      </template>
    </BasicTable>

    <!-- 设置弹窗 -->
    <a-modal v-model:visible="showSetting" title="预警设置" @ok="saveSetting" :centered="true"
             width="400px">
      <a-form :model="settingForm"  :labelCol="{ span: 7 }"
              :wrapperCol="{ span: 17 }"
              style="padding: 10px 0;">
        <a-form-item label="开启交期预警">
          <a-switch v-model:checked="settingForm.alertEnabled" />
        </a-form-item>
        <a-form-item label="预警天数" v-if="settingForm.alertEnabled">
          <a-input-number v-model:value="settingForm.alertDays" :min="1" :max="30" /> 天
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 扫码发货弹窗 -->
    <DeliveryTaskModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="wms-deliveryTask" setup>
  import { ref, reactive, computed } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import DeliveryTaskModal from './components/DeliveryTaskModal.vue';
  import { taskColumns, taskSearchFormSchema } from './DeliveryTask.data';
  import { getTaskList } from './DeliveryTask.api';

  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();

  // 预警设置（可存 localStorage 或用户配置表）
  const showSetting = ref(false);
  const settingForm = reactive({
    alertEnabled: true,
    alertDays: 3,
  });
  const alertDays = computed(() => settingForm.alertDays);

  // 保存设置
  function saveSetting() {
    localStorage.setItem('deliveryAlertSetting', JSON.stringify(settingForm));
    showSetting.value = false;
    reload();
  }

  // 初始化读取设置
  const saved = localStorage.getItem('deliveryAlertSetting');
  if (saved) {
    Object.assign(settingForm, JSON.parse(saved));
  }

  // 判断预警
  function isUrgent(deliveryDate: string): boolean {
    if (!settingForm.alertEnabled || !deliveryDate) return false;
    const days = Math.ceil((new Date(deliveryDate).getTime() - Date.now()) / (1000 * 60 * 60 * 24));
    return days >= 0 && days <= settingForm.alertDays;
  }

  function getAlertColor(deliveryDate: string): string {
    return isUrgent(deliveryDate) ? '#ff4d4f' : '';
  }

  const queryParam = reactive<any>({});

  const { tableContext } = useListPage({
    tableProps: {
      title: '待发货任务',
      api: getTaskList,
      columns: taskColumns,
      canResize: false,
      formConfig: {
        schemas: taskSearchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
      },
      actionColumn: { width: 120, fixed: 'right' },
      beforeFetch: (params) => {
        // 交期预警参数
        params.alertEnabled = settingForm.alertEnabled;
        params.alertDays = settingForm.alertDays;
        return Object.assign(params, queryParam);
      },
    },
  });

  const [registerTable, { reload }] = tableContext;

  function getTableAction(record) {
    if (record.deliveryStatus === '2') { // 已完成
      return [{ label: '查看', onClick: () => handleView(record) }];
    }
    return [{
      label: '扫码发货',
      type: 'primary',
      onClick: () => handleDeliver(record),
    }];
  }

  function handleDeliver(record) {
    openModal(true, { orderId: record.id, orderNo: record.orderNo });
  }

  function handleView(record) {
    // 查看已完成的发货记录
    console.log('查看发货记录', record);
  }

  function handleSuccess() {
    reload();
  }
</script>

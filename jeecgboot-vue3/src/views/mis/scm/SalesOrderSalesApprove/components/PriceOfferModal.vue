<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="报价选择"
    :width="1200"
    :defaultFullscreen="false"
    @ok="handleSubmit"
  >
    <!-- 查询区域 -->
    <div class="search-form mb-4">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-form-item label="客户编号" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <a-input v-model:value="searchForm.customerCode" disabled class="readonly-input" />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="客户名称" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <a-input v-model:value="searchForm.customerName" disabled class="readonly-input" />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="产品编号" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <a-input v-model:value="searchForm.productCode" placeholder="请输入" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="产品名称" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <a-input v-model:value="searchForm.productName" placeholder="请输入" allowClear />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16" class="mt-2">
        <a-col :span="6">
          <a-form-item label="定制产品名称" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <a-input v-model:value="searchForm.customProductName" placeholder="请输入" allowClear />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="业务员" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
            <JDictSelectTag
              v-model:value="searchForm.salesmanId"
              dictCode="sys_user,realname,id"
              :disabled="!isAdmin"
              @change="handleSalesmanChange"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item>
            <a-button type="primary" @click="handleSearch">查询</a-button>
            <a-button class="ml-2" @click="handleReset">重置</a-button>
          </a-form-item>
        </a-col>
      </a-row>
    </div>

    <!-- 表格区域 -->
    <BasicTable
      @register="registerTable"
      :rowSelection="{
        type: 'radio',
        selectedRowKeys: selectedRowKeys,
        onChange: onSelectChange
      }"
    />
  </BasicModal>
</template>

<script setup lang="ts">
  import { ref, reactive, computed,onMounted } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicTable, useTable } from '/@/components/Table';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStore } from '/@/store/modules/user';
  import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';
  import { getPriceOfferPage } from '../SalesOrderSalesApprove.api';

  const emit = defineEmits(['register', 'success']);
  const { createMessage } = useMessage();
  const userStore = useUserStore();


  // 当前登录用户
  const currentUser = computed(() => ({
    id: userStore.getUserInfo?.id || '',
    realname: userStore.getUserInfo?.realname || userStore.getUserInfo?.username || '',
  }));

  //let isAdmin1 = userStore.getUserInfo.roles?.includes('admin') || userStore.getUserInfo.username === 'admin';
  // 判断是否管理员 - 用 computed 确保响应式
  // const isAdmin = computed(() => {
  //   const userInfo = userStore.getUserInfo;
  //   const roles = userInfo?.roles || [];
  //   // 支持多种角色判断方式
  //   return roles.includes('admin') ||
  //     roles.some(r => r === 'admin' || r.roleCode === 'admin') ||
  //     userInfo?.username === 'admin';
  // });

  const isAdmin = ref(false);

  // 查询表单
  const searchForm = reactive({
    customerId: '',
    customerCode: '',
    customerName: '',
    productCode: '',
    productName: '',
    customProductName: '',
    salesmanId: '',
  });

  // 选中行
  const selectedRowKeys = ref<string[]>([]);
  const selectedRows = ref<any[]>([]);

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    // 打开弹窗时计算 isAdmin
    const userInfo = userStore.getUserInfo;
    const roles = userInfo?.roles || [];

    isAdmin.value = roles.includes('admin') ||
      roles.some(r => r === 'admin' || r?.roleCode === 'admin') ||
      userInfo?.username === 'admin';

    console.log('=== 弹窗打开 ===');
    console.log('userInfo:', userInfo);
    console.log('roles:', roles);
    console.log('isAdmin:', isAdmin.value);

    searchForm.customerId = data.customerId || '';
    searchForm.customerCode = data.customerCode || '';
    searchForm.customerName = data.customerName || '';
    // 业务员默认当前登录人
    searchForm.salesmanId = currentUser.value.id;

    selectedRowKeys.value = [];
    selectedRows.value = [];
    setModalProps({ confirmLoading: false });
    await reloadTable();
  });

  // 业务员变更
  function handleSalesmanChange(val: string) {
    searchForm.salesmanId = val;
    reloadTable();
  }

  // 表格配置
  const [registerTable, { reload: reloadTable }] = useTable({
    api: fetchData,
    columns: [
      { title: '报价单号', dataIndex: 'offerNo', width: 120 },
      { title: '客户名称', dataIndex: 'customerName', width: 150 },
      { title: '产品编码', dataIndex: 'productCode', width: 120 },
      { title: '产品名称', dataIndex: 'productName', width: 180 },
      { title: '产品规格', dataIndex: 'productSpec', width: 120 },
      { title: '产品颜色', dataIndex: 'productColor', width: 120 },
      { title: '定制编码', dataIndex: 'customProductCode', width: 100 },
      { title: '定制名称', dataIndex: 'customProductName', width: 150 },
      { title: '定制规格', dataIndex: 'customProductSpec', width: 120 },
      { title: '单位', dataIndex: 'unit_dictText', width: 80 },
      { title: '包装编码', dataIndex: 'packageCode', width: 100 },
      { title: '包装名称', dataIndex: 'packageName', width: 100 },
      {title:'包装规格',dataIndex:'packageSpec',width:100},
      {title:'包装容量', dataIndex:'packageCapacity', width:100},
      { title: '价格类型', dataIndex: 'priceType_dictText', width: 100 },
      { title: '数量区间', dataIndex: 'qtyRange', width: 120, customRender: ({ record }) => {
          return `${record.qtyMin || 0} ~ ${record.qtyMax || '∞'}`;
        }},
      { title: '单价', dataIndex: 'unitPrice', width: 100 },
      { title: '税率(%)', dataIndex: 'taxRate', width: 80 },
      { title: '最小起订量', dataIndex: 'minOrderQty', width: 100 },
      { title: '数量步长', dataIndex: 'qtyStep', width: 90 },
      { title: '生效日期', dataIndex: 'effectiveDate', width: 120 },
      { title: '失效日期', dataIndex: 'expiryDate', width: 120 },
      { title: '业务员', dataIndex: 'salesmanId_dictText', width: 100 },
      { title: '状态', dataIndex: 'status_dictText', width: 80 },
    ],
    canResize: false,
    pagination: true,
    showIndexColumn: true,
    clickToRowSelect: true,
    rowKey: 'offerDetailId',
    immediate: false,
  });

  // 获取数据
  async function fetchData(params: any) {
    if (!searchForm.customerId) {
      return { records: [], total: 0, current: 1, size: params.pageSize };
    }

    const queryParams = {
      pageNo: params.pageNo,
      pageSize: params.pageSize,
      customerId: searchForm.customerId,
      salesmanId: searchForm.salesmanId,
      productCode: searchForm.productCode,
      productName: searchForm.productName,
      customProductName: searchForm.customProductName,
    };

    return getPriceOfferPage(queryParams);
  }

  // 查询
  function handleSearch() {
    reloadTable();
  }

  // 重置
  function handleReset() {
    searchForm.productCode = '';
    searchForm.productName = '';
    searchForm.customProductName = '';
    searchForm.salesmanId = currentUser.value.id;
    reloadTable();
  }

  // 选择变化
  function onSelectChange(keys: string[], rows: any[]) {
    selectedRowKeys.value = keys;
    selectedRows.value = rows;
  }

  // 确定
  async function handleSubmit() {
    if (selectedRows.value.length === 0) {
      createMessage.warning('请选择一条报价记录');
      return;
    }
    emit('success', selectedRows.value[0]);
    closeModal();
  }
</script>

<style lang="less" scoped>
  .search-form {
    padding: 16px;
    background: #f5f5f5;
    border-radius: 4px;

    .readonly-input {
      background-color: #e6f7ff !important;
      color: #333 !important;
    }

    .mt-2 {
      margin-top: 8px;
    }

    .ml-2 {
      margin-left: 8px;
    }

    :deep(.ant-form-item) {
      margin-bottom: 0;
    }
  }
</style>

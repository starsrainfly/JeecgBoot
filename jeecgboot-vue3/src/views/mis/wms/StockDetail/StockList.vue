<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button
          type="primary"
          @click="scanVisible = true"
          preIcon="ant-design:scan-outlined"
        >
          扫码查库库
        </a-button>
        <a-button type="primary" v-auth="'wms:mis_stock:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-button  type="primary" v-auth="'wms:mis_stock:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
        <j-upload-button type="primary" v-auth="'wms:mis_stock:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button v-auth="'wms:mis_stock:deleteBatch'">批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
        <!-- 高级查询 -->
        <super-query :config="superQueryConfig" @search="handleSuperQuery" />
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)"/>
      </template>
      <!--字段回显插槽-->
      <template v-slot:bodyCell="{ column, record, index, text }">
        <!-- 库存数量颜色预警 -->
        <template v-if="column.dataIndex === 'quantity'">
          <span :class="getQtyClass(text)">{{ text }}</span>
        </template>
        <!-- 已分配未出库量 -->
        <template v-if="column.dataIndex === 'lockedQty'">
          <span v-if="text && text != '0' && text != '0.00'" class="text-orange">{{ text }}</span>
          <span v-else class="text-grey">0</span>
        </template>
        <!-- 可用库存颜色 -->
        <template v-if="column.dataIndex === 'availableQty'">
          <span :class="getAvailableClass(record)">{{ getAvailableVal(record) }}</span>
        </template>
        <!-- 过期日颜色标签 -->
        <template v-if="column.dataIndex === 'expiryDate'">
          <a-tag :color="getExpiryTagColor(text)">{{ formatDate(text) }}</a-tag>
        </template>
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <StockModal @register="registerModal" @success="handleSuccess"></StockModal>
    <!--智能扫码-->
    <SmartScanModal
      v-model:visible="scanVisible"
      @query="handleScanQuery"
    />
  </div>

</template>

<script lang="ts" name="wms-stock" setup>
  import {ref, reactive, computed, unref} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import {useModal} from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage'
  import StockModal from './components/StockModal.vue'
  import {columns, searchFormSchema, superQuerySchema} from './Stock.data';
  import {list, deleteOne, batchDelete, getImportUrl,getExportUrl} from './Stock.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import { useUserStore } from '/@/store/modules/user';

  import { SmartScanModal } from '/@/components/Scan';

  const scanVisible = ref(false);

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, {openModal}] = useModal();
  //注册table数据
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
    tableProps:{
      title: '库存记录表',
      api: list,
      columns,
      canResize:false,
      formConfig: {
        schemas: searchFormSchema,
        autoSubmitOnEnter:true,
        showAdvancedButton:true,
        fieldMapToNumber: [
        ],
        fieldMapToTime: [
          ['productionDate', ['productionDate_begin', 'productionDate_end'], 'YYYY-MM-DD'],
          ['expiryDate', ['expiryDate_begin', 'expiryDate_end'], 'YYYY-MM-DD'],
        ],
      },
      actionColumn: {
        width: 120,
        fixed:'right'
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name:"库存记录表",
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess
    },
  })

  const [registerTable, {reload},{ rowSelection, selectedRowKeys }] = tableContext

  // 高级查询配置
  const superQueryConfig = reactive(superQuerySchema);

  /**
   * 高级查询事件
   */
  function handleSuperQuery(params) {
    Object.keys(params).map((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }
  /**
   * 新增事件
   */
  function handleAdd() {
    openModal(true, {
      isUpdate: false,
      showFooter: true,
    });
  }
  /**
   * 编辑事件
   */
  function handleEdit(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
      showFooter: true,
    });
  }
  /**
   * 详情
   */
  function handleDetail(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
      showFooter: false,
    });
  }
  /**
   * 删除事件
   */
  async function handleDelete(record) {
    await deleteOne({id: record.id}, handleSuccess);
  }
  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await batchDelete({ids: selectedRowKeys.value}, handleSuccess);
  }
  /**
   * 成功回调
   */
  function handleSuccess() {
    (selectedRowKeys.value = []) && reload();
  }
  /**
   * 操作栏
   */
  function getTableAction(record){
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
        auth: 'wms:mis_stock:edit'
      }
    ]
  }
  /**
   * 下拉操作栏
   */
  function getDropDownAction(record){
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      }, {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
        auth: 'wms:mis_stock:delete'
      }
    ]
  }

  function handleScanQuery(params) {
    Object.assign(queryParam, params);
    reload();
  }

  // ===== 颜色预警辅助方法 =====
  function getQtyClass(val) {
    const n = Number(val) || 0;
    if (n === 0) return 'text-red font-bold';
    if (n < 10) return 'text-orange';
    return '';
  }
  function getAvailableVal(record) {
    const qty = Number(record.quantity) || 0;
    const locked = Number(record.lockedQty) || 0;
    return (qty - locked).toFixed(2);
  }
  function getAvailableClass(record) {
    const qty = Number(record.quantity) || 0;
    const locked = Number(record.lockedQty) || 0;
    const avl = qty - locked;
    if (avl <= 0) return 'text-red font-bold';
    if (avl < 10) return 'text-orange';
    return 'text-green';
  }
  function getExpiryTagColor(dateStr) {
    if (!dateStr) return 'default';
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return 'default';
    const now = new Date();
    now.setHours(0,0,0,0);
    const diff = Math.ceil((d.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    if (diff < 0) return 'red';
    if (diff <= 7) return 'red';
    if (diff <= 30) return 'orange';
    return 'green';
  }
  function formatDate(dateStr) {
    if (!dateStr) return '-';
    if (typeof dateStr === 'string' && dateStr.length > 10) return dateStr.substring(0, 10);
    return dateStr;
  }

</script>

<style lang="less" scoped>
  :deep(.ant-picker),:deep(.ant-input-number){
    width: 100%;
  }
  .text-red { color: #f5222d; }
  .text-orange { color: #fa8c16; }
  .text-green { color: #52c41a; }
  .text-grey { color: #999; }
  .font-bold { font-weight: bold; }
</style>

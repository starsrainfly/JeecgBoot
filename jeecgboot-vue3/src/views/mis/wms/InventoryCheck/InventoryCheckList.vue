<template>
  <div class="p-2 cgformErpList">
    <div class="content">
      <!--引用表格-->
      <BasicTable @register="registerTable" :rowSelection="rowSelection">
        <!--插槽:table标题-->
        <template #tableTitle>
          <a-button type="primary" v-auth="'wms:wms_inventory_check:add'"  @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
          <a-button type="primary" v-auth="'wms:wms_inventory_check:exportXls'"  preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
          <j-upload-button type="primary" v-auth="'wms:wms_inventory_check:importExcel'"  preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
          <a-dropdown v-if="selectedRowKeys.length > 0">
            <template #overlay>
              <a-menu>
                <a-menu-item key="1" @click="batchHandleDelete">
                  <Icon icon="ant-design:delete-outlined"></Icon>
                  删除
                </a-menu-item>
              </a-menu>
            </template>
            <a-button  v-auth="'wms:wms_inventory_check:deleteBatch'">批量操作
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
        </template>
      </BasicTable>
      <!--子表表格tab-->
      <a-tabs defaultActiveKey="1" style="margin: 10px">
        <a-tab-pane tab="盘库明细表" key="1" >
          <InventoryCheckDetailList />
        </a-tab-pane>
      </a-tabs>
    </div>
    <!-- 表单区域 -->
    <InventoryCheckModal @register="registerModal" @success="handleSuccess"></InventoryCheckModal>

    <InventoryCheckJobModal @register="registerCheckJobModal" @success="handleSuccess"/>
    <InventoryCheckApproveModal @register="registerApproveModal" @success="handleSuccess"/>
  </div>
</template>

<script lang="ts" name="wms-inventoryCheck" setup>
  import {ref, reactive, computed, unref,provide} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {useModal} from '/@/components/Modal';
  import InventoryCheckModal from './components/InventoryCheckModal.vue'
  import { useUserStore } from '/@/store/modules/user';
  import InventoryCheckDetailList from './InventoryCheckDetailList.vue'
  import {columns, searchFormSchema, superQuerySchema} from './InventoryCheck.data';
  import {list, deleteOne, batchDelete, getImportUrl,getExportUrl, startCheck} from './InventoryCheck.api';
  import {downloadFile} from '/@/utils/common/renderUtils';

  import InventoryCheckJobModal from './components/InventoryCheckJobModal.vue';
  import InventoryCheckApproveModal from './components/InventoryCheckApproveModal.vue';

  const queryParam = reactive<any>({});
  //注册model
  const [registerModal, {openModal}] = useModal();
  const [registerCheckJobModal, {openModal: openCheckJobModal}] = useModal();
  const [registerApproveModal, {openModal: openApproveModal}] = useModal();

   //注册table数据
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
      tableProps:{
           title: '盘库主表',
           api: list,
           columns,
           canResize: false,
           clickToRowSelect: true,
           rowSelection: {type: 'radio'},
           formConfig: {
                schemas: searchFormSchema,
                fieldMapToNumber: [
                ],
                fieldMapToTime: [
                   ['checkStartTime', ['checkStartTime_begin', 'checkStartTime_end'], 'YYYY-MM-DD HH:mm:ss'],
                   ['checkFinishedTime', ['checkFinishedTime_begin', 'checkFinishedTime_end'], 'YYYY-MM-DD HH:mm:ss'],
                ],
            },
           actionColumn: {
               width: 180,
               fixed:'right'
           },
           beforeFetch: (params) => {
             return Object.assign(params, queryParam);
           },
           pagination:{
               current: 1,
               pageSize: 5,
               pageSizeOptions: ['5', '10', '20'],
           }
        },
        exportConfig: {
            name:"盘库主表",
            url: getExportUrl,
            params: queryParam,
        },
        importConfig: {
            url: getImportUrl,
            success: handleSuccess
        },
    })
  const userStore = useUserStore();
  const [registerTable, {reload},{ rowSelection, selectedRowKeys }] = tableContext

  const mainId = computed(() => (unref(selectedRowKeys).length > 0 ? unref(selectedRowKeys)[0] : ''));
  //下发 mainId,子组件接收
  provide('mainId', mainId);

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
     await batchDelete({ids: selectedRowKeys.value},handleSuccess);
   }

  // 开始盘点
  async function handleStartCheck(record) {
    openCheckJobModal(true, { record });
  }

  // 继续盘点（打开作业页）
  function handleContinueCheck(record) {
    openCheckJobModal(true, { record });
  }

  // 审核
  function handleApprove(record) {
    openApproveModal(true, { record });
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
     const actions = [];
     // 待盘点：开始盘点（主按钮）+ 编辑
     if (record.checkStatus === '0') {
       actions.push({
         label: '开始盘点',
         onClick: handleStartCheck.bind(null, record)
       });
       actions.push({
         label: '编辑',
         onClick: handleEdit.bind(null, record),
         auth: 'wms:wms_inventory_check:edit'
       });
     }

     // 盘点中：继续盘点
     if (record.checkStatus === '1') {
       actions.push({
         label: '继续盘点',
         onClick: handleContinueCheck.bind(null, record)

       });
     }

     // 待审核：审核
     if (record.checkStatus === '2') {
       actions.push({
         label: '审核',
         onClick: handleApprove.bind(null, record)
       });
     }

     return actions;
     //   return [
     //     {
     //       label: '编辑',
     //       onClick: handleEdit.bind(null, record),
     //       auth: 'wms:wms_inventory_check:edit'
     //     }
     //   ]
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
          placement: 'topLeft'
        },
        auth: 'wms:wms_inventory_check:delete'
      }
    ]
  }


</script>

<style lang="less" scoped>
  html[data-theme='light'] {
    .cgformErpList {
      height: 100%;
      .content {
        background-color: #fff;
        height: 100%;
      }
    }
  }
  
  :deep(.ant-picker),:deep(.ant-input-number){
    width: 100%;
  }
</style>

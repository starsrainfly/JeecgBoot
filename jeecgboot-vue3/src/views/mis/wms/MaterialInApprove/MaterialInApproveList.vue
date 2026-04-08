<template>
  <div>
    <!--引用表格-->
   <BasicTable @register="registerTable" :rowSelection="rowSelection">
     <!--插槽:table标题-->
      <template #tableTitle>
          <a-button type="primary" v-auth="'wms:mis_stock_in:add'"  @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
          <a-button  type="primary" v-auth="'wms:mis_stock_in:exportXls'"  preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
          <j-upload-button  type="primary" v-auth="'wms:mis_stock_in:importExcel'"  preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
          <a-dropdown v-if="selectedRowKeys.length > 0">
              <template #overlay>
                <a-menu>
                  <a-menu-item key="1" @click="batchHandleDelete">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    删除
                  </a-menu-item>
                </a-menu>
              </template>
              <a-button v-auth="'wms:mis_stock_in:deleteBatch'">批量操作
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
    <!-- 表单区域 -->
    <StockInModal @register="registerModal" @success="handleSuccess"></StockInModal>
  </div>
</template>

<script lang="ts" name="wms-stockIn" setup>
  import {ref, reactive, computed, unref} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {useModal} from '/@/components/Modal';
  import StockInModal from './components/MaterialInApproveModal.vue'
  import {columns, searchFormSchema, superQuerySchema} from './MaterialInApprove.data';
  import {list, deleteOne, batchDelete, getImportUrl,getExportUrl} from './MaterialInApprove.api';
  import {downloadFile} from '/@/utils/common/renderUtils';
  import { useUserStore } from '/@/store/modules/user';
  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, {openModal}] = useModal();
   //注册table数据
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
      tableProps:{
           title: '入库表',
           api: list,
           columns,
           canResize:false,
           formConfig: {
                //labelWidth: 120,
                schemas: searchFormSchema,
                autoSubmitOnEnter:true,
                showAdvancedButton:true,
                fieldMapToNumber: [
                ],
                fieldMapToTime: [
                ],
            },
           actionColumn: {
               width: 140,
               fixed:'right'
           },
           beforeFetch: (params) => {
             return Object.assign(params, queryParam,{isProduct:'0'});
           },
        },
        exportConfig: {
            name:"入库表",
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
     await batchDelete({ids: selectedRowKeys.value},handleSuccess);
   }
   /**
    * 成功回调
    */
  function handleSuccess() {
      (selectedRowKeys.value = []) && reload();
   }

  // 新增：审核事件
  function handleAudit(record) {
    openModal(true, {
      record,
      isUpdate: true,
      showFooter: true,
      isAudit: true, // 传给 Modal 的关键参数
      title: '入库审核'
    });
  }

   /**
      * 操作栏
      */
  function getTableAction(record){
     //  record.approveStatus === '1' 代表已审核，此时不能再审核，也不能删除
     const isAudited = record.approveStatus === '1';
     return [
         {
           label: '编辑',
           onClick: handleEdit.bind(null, record),
           auth: 'wms:mis_stock_in:edit',
           disabled:record.approveStatus === '1'
         },
         {
           label: '审核',
           popConfirm: {
             title: '确认执行审核操作吗？',
             confirm: handleAudit.bind(null, record),
             placement: 'topLeft'
           },
           // 只有未审核的记录才显示审核按钮
           disabled: isAudited
         },
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
          placement: 'topLeft'
        },
        auth: 'wms:mis_stock_in:delete',
        disabled:record.approveStatus === '1'

      }
    ]
  }


</script>

<style lang="less" scoped>
  :deep(.ant-picker),:deep(.ant-input-number){
    width: 100%;
  }
</style>

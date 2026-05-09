<template>
  <div>
    <!--引用表格-->
   <BasicTable @register="registerTable" :rowSelection="rowSelection">
     <!--插槽:table标题-->
      <template #tableTitle>
          <a-button type="primary" v-auth="'mes:mis_production_order:add'"  @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
          <a-button  type="primary" v-auth="'mes:mis_production_order:exportXls'"  preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
          <j-upload-button  type="primary" v-auth="'mes:mis_production_order:importExcel'"  preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
          <a-dropdown v-if="selectedRowKeys.length > 0">
              <template #overlay>
                <a-menu>
                  <a-menu-item key="1" @click="batchHandleDelete">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    删除
                  </a-menu-item>
                  <a-menu-item key="2" @click="batchHandleRelease">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    下达任务
                  </a-menu-item>
                </a-menu>
              </template>
              <a-button v-auth="'mes:mis_production_order:deleteBatch'">批量操作
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
    <ProductionOrderModal @register="registerModal" @success="handleSuccess"></ProductionOrderModal>

    <!-- 打印弹窗 -->
    <PrintBatchingModal @register="registerPrintModal" />
  </div>
</template>

<script lang="ts" name="mes-productionOrder" setup>
  import {ref, reactive, computed, unref} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {useModal} from '/@/components/Modal';
  import ProductionOrderModal from './components/ProductionOrderModal.vue'
  import {columns, searchFormSchema, superQuerySchema} from './ProductionOrder.data';
  import {
    list,
    deleteOne,
    batchDelete,
    getImportUrl,
    getExportUrl,
    batchRelease, releaseOne
  } from './ProductionOrder.api';
  import {downloadFile} from '/@/utils/common/renderUtils';
  import { useUserStore } from '/@/store/modules/user';
  import PrintBatchingModal from '../productionTask/components/PrintBatchingModal.vue';

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, {openModal}] = useModal();
  // 注册打印弹窗
  const [registerPrintModal, {openModal: openPrintModal}] = useModal();

   //注册table数据
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
      tableProps:{
           title: '生产订单',
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
               width: 180,
               fixed:'right'
           },
           beforeFetch: (params) => {
             return Object.assign(params, queryParam);
           },
        },
        exportConfig: {
            name:"生产订单",
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

  /**下达一个任务*/
  async function HandleRelease(record){
    await releaseOne({id:record.id}, handleSuccess);
  }

   /**批量下达任务*/
   async function batchHandleRelease(){
     await batchRelease({ids: selectedRowKeys.value},handleSuccess);
   }

   /**
    * 成功回调
    */
  function handleSuccess() {
      (selectedRowKeys.value = []) && reload();
   }

  function handlePrintOrderBatching(record) {
    openPrintModal(true, {
      mode: 'order',
      orderId: record.id
    });
  }

   /**
      * 操作栏
      */
  function getTableAction(record){
       return [
         {
           label: '编辑',
           onClick: handleEdit.bind(null, record),
           auth: 'mes:mis_production_order:edit'
         },
         {
           label: '打印汇总配料单',
           onClick: handlePrintOrderBatching.bind(null, record),
           color: 'warning',
          // disabled: !(record.status === '1' || record.status === '2' || record.status === '3'),
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
          placement: 'topLeft'
        },
        auth: 'mes:mis_production_order:delete'
      },
      {
        label: '下达任务',
        popConfirm: {
          title: '是否确认下达任务',
          confirm: HandleRelease.bind(null, record),
          placement: 'topLeft'
        },
        auth: 'mes:mis_production_order:release'
      },
    ]
  }


</script>

<style lang="less" scoped>
  :deep(.ant-picker),:deep(.ant-input-number){
    width: 100%;
  }
</style>

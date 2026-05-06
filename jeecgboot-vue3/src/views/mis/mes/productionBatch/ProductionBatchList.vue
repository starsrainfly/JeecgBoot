<template>
  <div>
    <!--引用表格-->
   <BasicTable @register="registerTable" :rowSelection="rowSelection">
     <!--插槽:table标题-->
      <template #tableTitle>
          <a-button type="primary" v-auth="'mes:mis_production_batch:add'"  @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
          <a-button  type="primary" v-auth="'mes:mis_production_batch:exportXls'"  preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
          <j-upload-button  type="primary" v-auth="'mes:mis_production_batch:importExcel'"  preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
          <a-dropdown v-if="selectedRowKeys.length > 0">
              <template #overlay>
                <a-menu>
                  <a-menu-item key="1" @click="batchHandleDelete">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    删除
                  </a-menu-item>
                </a-menu>
              </template>
              <a-button v-auth="'mes:mis_production_batch:deleteBatch'">批量操作
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
    <ProductionBatchModal @register="registerModal" @success="handleSuccess"></ProductionBatchModal>

    <!-- 配料详情弹窗 -->
    <WeighingDetailModal @register="registerWeighingModal"></WeighingDetailModal>

    <!-- 产品入库申请（直接复用原有弹窗） -->
    <ProductInModal @register="registerProductInModal" @success="handleSuccess"></ProductInModal>

  </div>
</template>

<script lang="ts" name="mes-productionBatch" setup>
  import {ref, reactive, computed, unref} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {useModal} from '/@/components/Modal';
  import ProductionBatchModal from './components/ProductionBatchModal.vue'
  import {columns, searchFormSchema, superQuerySchema} from './ProductionBatch.data';
  import {list, deleteOne, batchDelete, getImportUrl,getExportUrl} from './ProductionBatch.api';
  import {downloadFile} from '/@/utils/common/renderUtils';
  import { useUserStore } from '/@/store/modules/user';
  import WeighingDetailModal from '/@/views/mis/mes/productionbatching/components/WeighingDetailModal.vue'
  import ProductInModal from '/@/views/mis/wms/ProductIn/components/ProductInModal.vue'  // 引入产品入库弹窗

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, {openModal}] = useModal();
  //注册配料详情弹窗
  const [registerWeighingModal, {openModal: openWeighingModal}] = useModal();
  //注册产品入库弹窗
  const [registerProductInModal, {openModal: openProductInModal}] = useModal();
   //注册table数据
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
      tableProps:{
           title: '生产批次',
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
            name:"生产批次",
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
   /**
      * 操作栏
      */
  function getTableAction(record){
       return [
         {
           label: '编辑',
           onClick: handleEdit.bind(null, record),
           disabled: !(record.status === 'PENDING'),
           auth: 'mes:mis_production_batch:edit'
         },
         {
           label:'入库申请',
           onClick: handleStockIn.bind(null, record),
           color:'success'
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
      },
      {
        label: '配料详情',
        onClick: handleWeighingDetail.bind(null, record),
        // 配料完成后才能查看详情
        disabled: record.status === 'PENDING' || record.status === 'CREATED',
      },
      {
        label: '删除',
        disabled: !(record.status === 'PENDING'),
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft'
        },
        auth: 'mes:mis_production_batch:delete'
      }
    ]
  }

  /**
   * 查看配料详情
   */
  function handleWeighingDetail(record) {
    openWeighingModal(true, {
      id: record.id,
      batchNo: record.batchNo,
      productName: record.productName,
      plannedQty: record.plannedQty,
      actualQty: record.actualQty,
    });
  }

  /**
   * 入库申请 - 直接弹窗
   */
  function handleStockIn(record) {
    openProductInModal(true, {
      isUpdate: false,
      showFooter: true,
      // 标记从批次触发
      fromBatch: true,
      // 传入批次信息
      record: {
        stockInType: 'PRODUCTION',
        sourceOrderType: 'PRODUCT',
        sourceOrderId: record.id,
        sourceOrderNo: record.batchNo,
        productId: record.productId,
        productCode: record.productCode,
        productName: record.productName,
        productSpec: record.productSpec,
        productColor: record.productColor,
        batchActualQty: record.actualQty,
        batchRemainQty: record.remainQty,
        batchInstockQty: record.inStockQty,
        unit: 'kg',  // 默认或从BOM获取
        isProduct: '1',
      }
    });
  }

</script>

<style lang="less" scoped>
  :deep(.ant-picker),:deep(.ant-input-number){
    width: 100%;
  }
</style>

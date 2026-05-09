<template>
  <div>
    <!--引用表格-->
   <BasicTable @register="registerTable" :rowSelection="rowSelection">
     <!--插槽:table标题-->
      <template #tableTitle>
          <a-button type="primary" v-auth="'wms:mis_delivery:add'"  @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
          <a-button  type="primary" v-auth="'wms:mis_delivery:exportXls'"  preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
          <j-upload-button  type="primary" v-auth="'wms:mis_delivery:importExcel'"  preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
          <a-dropdown v-if="selectedRowKeys.length > 0">
              <template #overlay>
                <a-menu>
                  <a-menu-item key="1" @click="batchHandleDelete">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    删除
                  </a-menu-item>
                </a-menu>
              </template>
              <a-button v-auth="'wms:mis_delivery:deleteBatch'">批量操作
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
    <DeliveryModal @register="registerModal" @success="handleSuccess"></DeliveryModal>

    <!-- 打印预览弹窗 -->
    <PrintDeliveryModal @register="registerPrintModal" />

  </div>
</template>

<script lang="ts" name="wms-delivery" setup>
  import {ref, reactive, computed, unref} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {useModal} from '/@/components/Modal';
  import DeliveryModal from './components/DeliveryModal.vue'
  import {columns, searchFormSchema, superQuerySchema} from './Delivery.data';
  import {list, deleteOne, batchDelete, getImportUrl,getExportUrl,deliveryDetailList} from './Delivery.api';
  import {downloadFile} from '/@/utils/common/renderUtils';
  import PrintDeliveryModal from './components/PrintDeliveryModal.vue';  // ← 新增
  import { useUserStore } from '/@/store/modules/user';
  import {defHttp} from '/@/utils/http/axios';

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, {openModal}] = useModal();
  const [registerPrintModal, { openModal: openPrintModal }] = useModal();  // ← 新增
   //注册table数据
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
      tableProps:{
           title: '发货表',
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
                   ['deliveryTime', ['deliveryTime_begin', 'deliveryTime_end'], 'YYYY-MM-DD HH:mm:ss'],
                ],
            },
           actionColumn: {
               width: 160,
               fixed:'right'
           },
           beforeFetch: (params) => {
             return Object.assign(params, queryParam);
           },
        },
        exportConfig: {
            name:"发货表",
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

  // ==================== 新增：打印相关 ====================

  /**
   * 打开打印预览弹窗
   */
  async function handlePrintPreview(record) {
    // 获取明细数据（复用已有接口）
    let details = [];
    try {
      const res = await defHttp.get({
        url: deliveryDetailList,
        params: { id: record.id }
      });
      //console.log("preview res:",res)
      if (res) {
        details = res || [];
      }
    } catch (e) {
      console.warn('获取明细失败', e);
    }

    openPrintModal(true, {
      record: record,           // 主表数据
      detailList: details,      // 子表明细
      showPrice: false          // 默认不显示单价
    });
  }

  /**
   * 直接打印（不预览）
   */
  async function handleDirectPrint(record) {
    // 同样先获取明细
    let details = [];
    try {
      const res = await defHttp.get({
        url: deliveryDetailList,
        params: { id: record.id }
      });
      console.log("res:",res)
      if (res) {
        details = res || [];
      }
    } catch (e) {
      console.warn('获取明细失败', e);
    }

    // 直接打开打印窗口（复用预览组件的打印逻辑，或单独实现）
    // 这里简化处理：打开预览弹窗后立即触发打印
    openPrintModal(true, {
      record: record,
      detailList: details,
      showPrice: false,
      autoPrint: true  // 标记自动打印
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
           auth: 'wms:mis_delivery:edit'
         },
         {
           label: '预览打印',           // ← 新增
           onClick: () => handlePrintPreview(record),

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
      },
      {
        label: '直接打印',            // ← 新增
        onClick: () => handleDirectPrint(record),

      },
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft'
        },
        auth: 'wms:mis_delivery:delete'
      }
    ]
  }


</script>

<style lang="less" scoped>
  :deep(.ant-picker),:deep(.ant-input-number){
    width: 100%;
  }
</style>

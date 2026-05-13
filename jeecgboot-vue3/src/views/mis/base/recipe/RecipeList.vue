<template>
  <div>
    <!--引用表格-->
   <BasicTable @register="registerTable" :rowSelection="rowSelection">
     <!--插槽:table标题-->
      <template #tableTitle>
          <a-button type="primary"   @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
          <a-button  type="primary"  preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
          <j-upload-button  type="primary"   preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
          <a-dropdown v-if="selectedRowKeys.length > 0">
              <template #overlay>
                <a-menu>
                  <a-menu-item key="1" @click="batchHandleDelete">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    删除
                  </a-menu-item>
                  <a-menu-item key="2" @click="handleCreateSpec">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    新增属性
                  </a-menu-item>
                  <a-menu-item key="3" @click="handleEditSpec">
                    <Icon icon="ant-design:edit-outlined"></Icon>
                    编辑属性
                  </a-menu-item>
                  <a-menu-item key="4" @click="handleSpecDetail">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    属性详情
                  </a-menu-item>
                  <a-menu-item key="5" @click="handlePublish">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    发布配方
                  </a-menu-item>
                </a-menu>
              </template>
              <a-button v-auth="'Recipe:mis_recipe:deleteBatch'">批量操作
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
    <!--属性抽屉-->
    <RecipeSpecDrawer @register="registerDrawer" @success="handleSuccess" />
    <!-- 表单区域 -->
    <RecipeModal @register="registerModal" @success="handleSuccess"></RecipeModal>
  </div>
</template>

<script lang="ts" name="Recipe-recipe" setup>
  import {ref, reactive, computed, unref} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {useModal} from '/@/components/Modal';
  import RecipeModal from './components/RecipeModal.vue'
  import RecipeSpecDrawer from './components/RecipeSpecDrawer.vue'
  import {columns, searchFormSchema, superQuerySchema} from './Recipe.data';
  import {list, deleteOne, batchDelete, getImportUrl, getExportUrl, publishOne} from './Recipe.api';
  import {downloadFile} from '/@/utils/common/renderUtils';

  import { useDrawer } from '/@/components/Drawer';
  import { useUserStore } from '/@/store/modules/user';
  import { getRecipeSpecByRecipeId} from "@/views/mis/base/recipe/RecipeSpec.api";
  import {useMessage} from "@/hooks/web/useMessage";

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, {openModal}] = useModal();
  //注册recipeSpecDrawer
  const [registerDrawer, {openDrawer}] = useDrawer();

  const { createMessage } = useMessage();
   //注册table数据
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
      tableProps:{
           title: '配方表',
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
               width: 120,
               fixed:'right'
           },
           beforeFetch: (params) => {
             return Object.assign(params, queryParam);
           },
        },
        exportConfig: {
            name:"配方表",
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
     // if(record.publishStatus ==="1"){
     //   createMessage.error('已经发布不可编辑');
     //   return ;
     // }
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
    if(record.publishStatus ==="1"){
        createMessage.error('已经发布不可删除');
        return ;
      }

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
          // disabled: record.publishStatus === '1' || record.isActive !== '1',
           onClick: handleEdit.bind(null, record),
           auth: 'Recipe:mis_recipe:edit',
           disabled:record.publishStatus === '1'
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
        label: '删除',

        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft'
        },
        auth: 'Recipe:mis_recipe:delete',
        disabled:record.publishStatus === '1'
      },
      {
        label:'新增属性',
        onClick: handleCreateSpec.bind(null, record),
      },
      {
        label:'编辑属性',
        onClick: handleEditSpec.bind(null, record)
      },
      {
        label:'属性详情',
        onClick: handleSpecDetail.bind(null, record)
      },
      {
        label:'发布配方',
        popConfirm: {
          title: '是否确认发布',
          confirm: handlePublish.bind(null, record),
          placement: 'topLeft'
        },

      },
    ]
  }
  /**
   * 新增对应配方的属性
   * */
  async function handleCreateSpec(record) {
    const recipeId = record.id;
    try {
      const existing = await getRecipeSpecByRecipeId(recipeId);
      if (existing && existing.id) {
        createMessage.warning( '该配方已存在属性配置，不可重复创建！');
        return;
      }
      openDrawer(true, {
        record:{ recipeId: recipeId},
        isUpdate: false,
        showFooter: true,
        tenantSaas: false,
      });
    }catch (error) {
      console.error('检查属性是否存在失败', error);
      createMessage.error('无法验证属性是否已存在' );
    }
  }

  /**
   * 编辑事件
   */
  async function handleEditSpec(record: Recordable) {
    const recipeId = record.id;
    try {
      const existing = await getRecipeSpecByRecipeId(recipeId);

      if (!existing || !existing.id) {
        createMessage.warning('该配方尚未创建属性，请先新增属性！');
        return;
      }
      openDrawer(true, {
        record: existing,
        isUpdate: true,
        showFooter: true,
        tenantSaas: false,
      });
    }catch (error) {
      console.error('加载属性数据失败', error);
      createMessage.error('加载属性信息失败' );
    }
  }

  async function handlePublish(record:Recordable){
    const publishFlag = record.publishStatus;
    if(publishFlag == "1"){
      createMessage.error('已经发布不可再次发布');
      return ;
    }
    await publishOne({id: record.id}, handleSuccess);
  }
  /**
   * 详情
   */
  async function handleSpecDetail(record: Recordable) {
    const recipeId = record.id;
    try {
      const existing = await getRecipeSpecByRecipeId(recipeId);

      if (!existing || !existing.id) {
        createMessage.warning('该配方尚未创建属性，请先新增属性！');
        return;
      }
      openDrawer(true, {
        record: existing,
        isUpdate: true,
        showFooter: false,
        tenantSaas: false,
      });
    }catch (error) {
      console.error('加载属性数据失败', error);
      createMessage.error('加载属性信息失败' );
    }
  }

</script>

<style lang="less" scoped>
  :deep(.ant-picker),:deep(.ant-input-number){
    width: 100%;
  }
</style>

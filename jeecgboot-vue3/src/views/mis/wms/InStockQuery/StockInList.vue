<template>
  <div class="p-2 cgformErpList">
    <div class="content">
      <BasicTable @register="registerTable" :rowSelection="rowSelection">
        <template #tableTitle>
          <a-button type="primary" v-auth="'wms:mis_stock_in:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
<!--          <j-upload-button type="primary" v-auth="'wms:mis_stock_in:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>-->
          <a-dropdown v-if="selectedRowKeys.length > 0">
            <template #overlay>
              <a-menu></a-menu>
            </template>
          </a-dropdown>
          <super-query :config="superQueryConfig" @search="handleSuperQuery" />
        </template>
<!--        <template #action="{ record }">-->
<!--          <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)"/>-->
<!--        </template>-->
<!--        <template v-slot:bodyCell="{ column, record, index, text }">-->
<!--        </template>-->
      </BasicTable>

      <a-tabs defaultActiveKey="1" style="margin: 10px">
        <a-tab-pane tab="入库明细表" key="1">
          <StockInDetailList />
        </a-tab-pane>
      </a-tabs>
    </div>

    <StockInModal @register="registerModal" @success="handleSuccess"></StockInModal>
  </div>
</template>

<script lang="ts" name="wms-stockIn" setup>
  import {ref, reactive, computed, unref, provide} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import {useModal} from '/@/components/Modal';
  import StockInModal from './components/StockInModal.vue'
  import { useUserStore } from '/@/store/modules/user';
  import StockInDetailList from './StockInDetailList.vue'
  import {columns, searchFormSchema, superQuerySchema} from './StockIn.data';
  import {list, deleteOne, batchDelete, getImportUrl, getExportUrl} from './StockIn.api';
  import {downloadFile} from '/@/utils/common/renderUtils';
  // 引入合计工具方法
  import { mapTableTotalSummary } from '/@/utils/common/compUtils';

  const queryParam = reactive<any>({});
  const [registerModal, {openModal}] = useModal();
  const userStore = useUserStore();

  // ========== 汇总数据响应式变量（用于 summaryData 方式）==========
  const summaryData = ref<any[]>([]);

  // ========== 主表汇总函数 - 必须在 useListPage 之前定义 ==========
  function handleMainSummary(tableData: Recordable[]) {
    // 使用工具方法自动计算合计（传入需要合计的字段数组）
    const totals = mapTableTotalSummary(tableData, ['totalAmount']);

    // 返回数组，每个元素是一行合计
    return [
      {
        ...totals,
        stockInNo: '合计',
        _row: '合计',
      }
    ];
  }

  // ========== useListPage 调用 ==========
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '入库表',
      api: list,
      columns,
      canResize: false,
      clickToRowSelect: true,
      rowSelection: {type: 'radio'},
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToNumber: [],
        fieldMapToTime: [],
      },
      // actionColumn: {
      //   width: 120,
      //   fixed: 'right'
      // },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
      pagination: {
        current: 1,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '20'],
      },
      // 合计行配置 - 使用新版原生总结栏（showSummary不设或false）
      // 如果要用旧版表尾合计行，设 showSummary: true
      summaryFunc: handleMainSummary,
      // 或者用 summaryData 方式（推荐，可动态更新）
      // summaryData: unref(summaryData),
    },
    exportConfig: {
      name: "入库表",
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess
    },
  });

  const [registerTable, {reload}, { rowSelection, selectedRowKeys }] = tableContext;

  const mainId = computed(() => (unref(selectedRowKeys).length > 0 ? unref(selectedRowKeys)[0] : ''));
  provide('mainId', mainId);

  const superQueryConfig = reactive(superQuerySchema);

  function handleSuperQuery(params) {
    Object.keys(params).map((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }

  function handleAdd() {
    openModal(true, {
      isUpdate: false,
      showFooter: true,
    });
  }

  function handleEdit(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
      showFooter: true,
    });
  }

  function handleDetail(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
      showFooter: false,
    });
  }

  async function handleDelete(record) {
    await deleteOne({id: record.id}, handleSuccess);
  }

  async function batchHandleDelete() {
    await batchDelete({ids: selectedRowKeys.value}, handleSuccess);
  }

  function handleSuccess() {
    (selectedRowKeys.value = []) && reload();
  }

  function getTableAction(record) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
        auth: 'wms:mis_stock_in:edit'
      }
    ]
  }

  function getDropDownAction(record) {
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
        auth: 'wms:mis_stock_in:delete'
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

  :deep(.ant-picker), :deep(.ant-input-number) {
    width: 100%;
  }
</style>

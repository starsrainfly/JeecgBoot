<template>
  <div class="p-2">
    <a-row :gutter="8">
      <!-- 左侧：产品分类树 -->
      <a-col :xs="24" :sm="24" :md="7" :lg="6" :xl="5">
        <a-card size="small" :bordered="false" class="product-tree-card" :bodyStyle="{ padding: '8px' }">
          <template #title>产品分类</template>
          <template #extra>
            <a-button type="link" size="small" v-auth="'product:mis_product:add'" preIcon="ant-design:plus-outlined" @click="handleAddRoot">新增分类</a-button>
          </template>
          <a-input v-model:value="treeSearchText" placeholder="搜索分类编码/名称" allowClear class="mb-2" />
          <a-spin :spinning="treeLoading">
            <a-tree
              blockNode
              :treeData="displayTreeData"
              :loadData="onLoadTreeData"
              :expandedKeys="displayExpandedKeys"
              :selectedKeys="selectedKeys"
              :fieldNames="{ key: 'id', title: 'title', children: 'children' }"
              @expand="onTreeExpand"
              @select="onTreeSelect"
            >
              <template #title="{ title, hasChild }">
                <Icon v-if="hasChild === '1'" icon="ant-design:folder-outlined" style="color:#faad14;margin-right:2px" />
                <Icon v-else icon="ant-design:file-outlined" style="color:#bbb;margin-right:2px" />
                <span v-if="treeSearchText && title.toLowerCase().includes(treeSearchText.trim().toLowerCase())" v-html="getHighlightTitle(title)"></span>
                <template v-else>{{ title }}</template>
              </template>
            </a-tree>
            <a-empty v-if="!treeLoading && treeData.length === 0" description="暂无分类，点右上角新增" :image-style="{ height: '60px' }" />
          </a-spin>
        </a-card>
      </a-col>

      <!-- 右侧：表格 -->
      <a-col :xs="24" :sm="24" :md="17" :lg="18" :xl="19">
        <BasicTable @register="registerTable" :rowSelection="rowSelection">
          <template #tableTitle>
            <a-button type="primary" v-auth="'product:mis_product:add'" preIcon="ant-design:plus-outlined" :disabled="!currentNode" @click="handleAddLeaf">新增产品</a-button>
            <a-button v-auth="'product:mis_product:add'" preIcon="ant-design:apartment-outlined" :disabled="!currentNode" @click="handleAddSubCategory">新增子分类</a-button>
            <a-button type="primary" v-auth="'product:mis_product:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls">导出</a-button>
            <j-upload-button type="primary" v-auth="'product:mis_product:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
            <a-dropdown v-if="selectedRowKeys.length > 0">
              <template #overlay>
                <a-menu>
                  <a-menu-item key="1" @click="batchHandleDelete">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    删除
                  </a-menu-item>
                </a-menu>
              </template>
              <a-button v-auth="'product:mis_product:deleteBatch'">批量操作
                <Icon icon="ant-design:down-outlined"></Icon>
              </a-button>
            </a-dropdown>

            <!-- 查询范围提示 -->
            <span v-if="isGlobalSearch" class="current-node-tip global">全局搜索模式</span>
            <span v-else-if="currentNode" class="current-node-tip">当前分类：{{ currentNode.title }}</span>
            <span v-else class="current-node-tip empty">请先选择左侧分类</span>
          </template>

          <template #action="{ record }">
            <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
          </template>

          <template v-slot:bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'productCode'">
              <a-tag v-if="record.hasChild === '1'" color="processing" style="margin-right:4px">分类</a-tag>{{ record.productCode }}
            </template>
          </template>

          <template #empty>
            <a-empty description="暂无数据" />
          </template>
        </BasicTable>
        <ProductModal @register="registerModal" @success="handleSuccess" />
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" name="product-product" setup>
  import { ref, reactive, computed, unref } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Icon } from '/@/components/Icon';
  import ProductModal from './components/ProductModal.vue';
  import { columns } from './Product.data';
  import { list, deleteProduct, batchDeleteProduct, getExportUrl, getImportUrl, getChildList } from './Product.api';

  const queryParam = reactive<any>({});
  const [registerModal, { openModal }] = useModal();

  // ================= 左侧分类树 =================
  const treeData = ref<any[]>([]);
  const expandedKeys = ref<string[]>([]);
  const selectedKeys = ref<string[]>([]);
  const currentNode = ref<any>(null);
  const treeLoading = ref(false);
  const treeSearchText = ref('');
  const isGlobalSearch = ref(false);

  // 右侧表格搜索表单
  const rightSearchSchema = [
    { label: '产品编码', field: 'productCode', component: 'Input' },
    { label: '产品名称', field: 'productName', component: 'Input' },
    { label: '型号规格', field: 'productSpec', component: 'Input' },
    {
      label: '查询范围',
      field: 'globalSearch',
      component: 'Switch',
      defaultValue: false,
      componentProps: {
        checkedChildren: '全局',
        unCheckedChildren: '当前分类',
      },
    },
  ];

  // 注册table
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      api: loadChildList,
      title: '产品列表',
      columns,
      canResize: false,
      immediate: false,
      formConfig: {
        schemas: rightSearchSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: false,
        labelWidth: 80,
      },
      actionColumn: {
        width: 280,
        fixed: 'right',
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: '产品信息',
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: importSuccess,
    },
  });

  // ← 关键：从 tableContext 解构出 getForm
  const [registerTable, { reload, getForm }, { rowSelection, selectedRowKeys }] = tableContext;

  /**
   * 右侧表格数据源
   */
  async function loadChildList(params) {
    const node = currentNode.value;
    const global = params.globalSearch === true;
    isGlobalSearch.value = global;

    // ===== 全局搜索模式 =====
    if (global) {
      const res = await list({
        pageNo: params.pageNo,
        pageSize: params.pageSize,
        productCode: params.productCode,
        productName: params.productName,
        productSpec: params.productSpec,
        globalSearch: true,
      });
      return {
        records: res?.records || [],
        total: res?.total || 0,
      };
    }

    // ===== 分类搜索模式 =====
    if (!node) {
      return { records: [], total: 0 };
    }

    let records;
    if (node.hasChild === '1') {
      const res = await getChildList({ pid: node.id });
      records = res?.records ? res.records : res || [];
    } else {
      records = [node];
    }

    // 前端过滤
    const { productCode, productName, productSpec } = params || {};
    if (productCode) {
      records = records.filter((r) => (r.productCode || '').toLowerCase().includes(productCode.toLowerCase()));
    }
    if (productName) {
      records = records.filter((r) => (r.productName || '').includes(productName));
    }
    if (productSpec) {
      records = records.filter((r) => (r.productSpec || '').includes(productSpec));
    }

    const pageNo = params.pageNo || 1;
    const pageSize = params.pageSize || 10;
    return {
      records: records.slice((pageNo - 1) * pageSize, pageNo * pageSize),
      total: records.length,
    };
  }

  // ================= 树相关方法 =================

  function buildTreeNodes(records) {
    return (records || [])
      .filter((item) => !item.pid || item.pid === '0' || item.hasChild === '1')
      .map((item) => ({
        ...item,
        title: `${item.productCode || ''} ${item.productName || ''}`.trim(),
        isLeaf: item.hasChild !== '1',
      }));
  }

  async function initTree() {
    treeLoading.value = true;
    try {
      const res = await list({ pageNo: 1, pageSize: 500 });
      const records = res?.records ? res.records : res || [];
      treeData.value = buildTreeNodes(records);
      if (treeData.value.length > 0) {
        const first = treeData.value[0];
        selectedKeys.value = [first.id];
        currentNode.value = first;
        reload();
      }
    } catch (e) {
      console.error('产品分类树加载失败', e);
      treeData.value = [];
    } finally {
      treeLoading.value = false;
    }
  }
  initTree();

  function onLoadTreeData(treeNode) {
    return new Promise<void>((resolve) => {
      if (treeNode.dataRef.children) {
        resolve();
        return;
      }
      getChildList({ pid: treeNode.dataRef.id }).then((res) => {
        const records = res?.records ? res.records : res || [];
        treeNode.dataRef.children = buildTreeNodes(records);
        treeData.value = [...treeData.value];
        resolve();
      });
    });
  }

  function onTreeExpand(keys) {
    expandedKeys.value = keys;
  }

  /**
   * 选中树节点 -> 同步表单开关为"当前分类"，再加载
   */
  function onTreeSelect(keys, info) {
    if (!keys || keys.length === 0) return;
    selectedKeys.value = keys;
    currentNode.value = info.node.dataRef;
    selectedRowKeys.value = [];
    isGlobalSearch.value = false;

    // ← 关键：把表单里的全局开关切回"当前分类"
    getForm?.().setFieldsValue({ globalSearch: false });

    reload();
  }

  const displayTreeData = computed(() => {
    const kw = treeSearchText.value.trim().toLowerCase();
    if (!kw) return treeData.value;
    const filterNodes = (nodes) => {
      const result = [] as any[];
      for (const node of nodes || []) {
        const matchedChildren = filterNodes(node.children);
        if ((node.title || '').toLowerCase().includes(kw)) {
          result.push(node);
        } else if (matchedChildren.length > 0) {
          result.push({ ...node, children: matchedChildren });
        }
      }
      return result;
    };
    return filterNodes(treeData.value);
  });

  const displayExpandedKeys = computed(() => {
    if (treeSearchText.value.trim()) {
      return collectAllKeys(displayTreeData.value);
    }
    return expandedKeys.value;
  });

  function collectAllKeys(nodes, keys = [] as string[]) {
    for (const n of nodes || []) {
      keys.push(n.id);
      collectAllKeys(n.children, keys);
    }
    return keys;
  }

  function getHighlightTitle(title: string) {
    const kw = treeSearchText.value.trim();
    return title.split(kw).join(`<span style="color:#ff5500">${kw}</span>`);
  }

  function findTreeNode(nodes, id) {
    for (const n of nodes || []) {
      if (n.id === id) return n;
      const found = findTreeNode(n.children, id);
      if (found) return found;
    }
    return null;
  }

  async function reloadTreeKeepExpand() {
    const res = await list({ pageNo: 1, pageSize: 500 });
    const records = res?.records ? res.records : res || [];
    const roots = buildTreeNodes(records);
    for (const key of unref(expandedKeys)) {
      await fillChildren(roots, key);
    }
    treeData.value = roots;
  }

  async function fillChildren(nodes, key): Promise<boolean> {
    for (const node of nodes) {
      if (node.id === key) {
        const res = await getChildList({ pid: key });
        const records = res?.records ? res.records : res || [];
        node.children = buildTreeNodes(records);
        return true;
      }
      if (node.children && (await fillChildren(node.children, key))) {
        return true;
      }
    }
    return false;
  }

  // ================= 新增/编辑/删除 =================

  function handleAddRoot() {
    openModal(true, { isUpdate: false, title: '新增分类' });
  }

  function handleAddLeaf() {
    openModal(true, {
      isUpdate: false,
      record: { pid: currentNode.value.id },
      lockPid: true,
      title: `新增产品（${currentNode.value.productName}）`,
    });
  }

  function handleAddSubCategory() {
    openModal(true, {
      isUpdate: false,
      record: { pid: currentNode.value.id },
      lockPid: true,
      title: `新增子分类（${currentNode.value.productName}）`,
    });
  }

  function handleEdit(record) {
    openModal(true, { record, isUpdate: true });
  }

  function handleDetail(record) {
    openModal(true, { record, isUpdate: true, hideFooter: true });
  }

  function handleAddSub(record) {
    openModal(true, {
      isUpdate: false,
      record: { pid: record.id },
      lockPid: true,
      title: `添加下级（${record.productName}）`,
    });
  }

  /**
   * 进入下级：同样切回分类模式
   */
  async function handleEnterCategory(record) {
    const pid = record.pid;
    const parentNode = findTreeNode(treeData.value, pid);
    if (parentNode) {
      if (!parentNode.children) {
        const res = await getChildList({ pid });
        const records = res?.records ? res.records : res || [];
        parentNode.children = buildTreeNodes(records);
        treeData.value = [...treeData.value];
      }
      if (!expandedKeys.value.includes(pid)) {
        expandedKeys.value = [...expandedKeys.value, pid];
      }
    }
    const node = findTreeNode(treeData.value, record.id);
    if (node) {
      selectedKeys.value = [record.id];
      currentNode.value = node;
      selectedRowKeys.value = [];
      isGlobalSearch.value = false;

      // ← 同样切回分类模式
      getForm?.().setFieldsValue({ globalSearch: false });

      reload();
    }
  }

  async function handleDelete(record) {
    await deleteProduct({ id: record.id }, afterDelete);
  }

  async function batchHandleDelete() {
    await batchDeleteProduct({ id: selectedRowKeys.value }, afterDelete);
  }

  function afterDelete() {
    selectedRowKeys.value = [];
    reload();
    reloadTreeKeepExpand();
  }

  function importSuccess() {
    selectedRowKeys.value = [];
    reload();
    reloadTreeKeepExpand();
  }

  async function handleSuccess({ isUpdate, values, changeParent }) {
    if (isUpdate && !changeParent) {
      reload();
      const node = findTreeNode(treeData.value, values.id);
      if (node) {
        Object.assign(node, values, {
          title: `${values.productCode || ''} ${values.productName || ''}`.trim(),
        });
        treeData.value = [...treeData.value];
      }
    } else {
      reload();
      await reloadTreeKeepExpand();
    }
  }

  function getTableAction(record) {
    const actions: any[] = [
      { label: '编辑', onClick: handleEdit.bind(null, record), auth: 'product:mis_product:edit' },
      { label: '添加下级', onClick: handleAddSub.bind(null, record), auth: 'product:mis_product:add' },
    ];
    if (record.hasChild === '1' && !isGlobalSearch.value) {
      actions.unshift({ label: '进入下级', onClick: handleEnterCategory.bind(null, record) });
    }
    return actions;
  }

  function getDropDownAction(record) {
    return [
      { label: '详情', onClick: handleDetail.bind(null, record) },
      {
        label: '删除',
        popConfirm: { title: '确定删除吗?', confirm: handleDelete.bind(null, record), placement: 'topLeft' },
        auth: 'product:mis_product:delete',
      },
    ];
  }
</script>

<style lang="less" scoped>
  .product-tree-card {
    height: calc(100vh - 130px);
    overflow: auto;
  }

  .current-node-tip {
    margin-left: 8px;
    color: #1890ff;
    font-size: 13px;

    &.empty {
      color: #999;
    }

    &.global {
      color: #52c41a;
      font-weight: 500;
    }
  }

  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>

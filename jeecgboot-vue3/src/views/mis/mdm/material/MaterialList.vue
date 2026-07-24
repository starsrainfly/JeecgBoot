<template>
  <div class="p-2">
    <a-row :gutter="8">
      <!-- 左侧：物料分类树（pid='0' 的顶级分类 + 有子节点的分类） -->
      <a-col :xs="24" :sm="24" :md="7" :lg="6" :xl="5">
        <a-card size="small" :bordered="false" class="material-tree-card" :bodyStyle="{ padding: '8px' }">
          <template #title>物料分类</template>
          <template #extra>
            <a-button type="link" size="small" v-auth="'mdm:mis_material:add'" preIcon="ant-design:plus-outlined" @click="handleAddRoot">新增分类</a-button>
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
<!--              <template #title="{ title }">-->
<!--                <span v-if="treeSearchText && title.toLowerCase().includes(treeSearchText.trim().toLowerCase())" v-html="getHighlightTitle(title)"></span>-->
<!--                <template v-else>{{ title }}</template>-->
<!--              </template>-->
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

      <!-- 右侧：选中节点的子节点列表（不区分分类/物料） -->
      <a-col :xs="24" :sm="24" :md="17" :lg="18" :xl="19">
        <BasicTable @register="registerTable" :rowSelection="rowSelection">
          <!--插槽:table标题-->
          <template #tableTitle>
            <a-button type="primary" v-auth="'mdm:mis_material:add'" preIcon="ant-design:plus-outlined" :disabled="!currentNode" @click="handleAddLeaf">新增物料</a-button>
            <a-button v-auth="'mdm:mis_material:add'" preIcon="ant-design:apartment-outlined" :disabled="!currentNode" @click="handleAddSubCategory">新增子分类</a-button>
            <a-button type="primary" v-auth="'mdm:mis_material:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls">导出</a-button>
            <j-upload-button type="primary" v-auth="'mdm:mis_material:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
            <a-dropdown v-if="selectedRowKeys.length > 0">
              <template #overlay>
                <a-menu>
                  <a-menu-item key="1" @click="batchHandleDelete">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    删除
                  </a-menu-item>
                </a-menu>
              </template>
              <a-button v-auth="'mdm:mis_material:deleteBatch'">批量操作
                <Icon icon="ant-design:down-outlined"></Icon>
              </a-button>
            </a-dropdown>
            <span v-if="currentNode" class="current-node-tip">当前分类：{{ currentNode.title }}</span>
            <span v-else class="current-node-tip empty">请先选择左侧分类</span>
          </template>
          <!--操作栏-->
          <template #action="{ record }">
            <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
          </template>
          <!--字段回显插槽：分类行加标识-->
          <template v-slot:bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'materialCode'">
              <a-tag v-if="record.hasChild === '1'" color="processing" style="margin-right:4px">分类</a-tag>{{ record.materialCode }}
            </template>
          </template>
          <!--空数据提示-->
          <template #empty>
            <a-empty description="该分类下暂无内容" />
          </template>
        </BasicTable>
        <!--表单弹窗-->
        <MaterialModal @register="registerModal" @success="handleSuccess" />
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" name="mdm-material" setup>
  import { ref, reactive, computed, unref } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Icon } from '/@/components/Icon';
  import MaterialModal from './components/MaterialModal.vue';
  import { columns } from './Material.data';
  import { list, deleteMaterial, batchDeleteMaterial, getExportUrl, getImportUrl, getChildList } from './Material.api';

  const queryParam = reactive<any>({});
  const [registerModal, { openModal }] = useModal();

  // ================= 左侧分类树 =================
  const treeData = ref<any[]>([]);
  const expandedKeys = ref<string[]>([]);
  const selectedKeys = ref<string[]>([]);
  const currentNode = ref<any>(null);
  const treeLoading = ref(false);
  const treeSearchText = ref('');

  // 右侧表格的简单查询条件（选中节点范围内前端过滤）
  const rightSearchSchema = [
    { label: '物料编码', field: 'materialCode', component: 'Input' },
    { label: '物料名称', field: 'materialName', component: 'Input' },
    { label: '规格型号', field: 'materialSpec', component: 'Input' },
  ];

  //注册table数据
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      api: loadChildList,
      title: '物料列表',
      columns,
      canResize: false,
      // 不立即加载，等选中分类后再加载
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
      name: '物料表',
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: importSuccess,
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  /**
   * 右侧表格数据源：显示当前选中节点的所有直接子节点（不区分分类/物料）
   * childList 不分页，这里做前端分页 + 简单过滤
   */
  // async function loadChildList(params) {
  //   if (!currentNode.value) {
  //     return { records: [], total: 0 };
  //   }
  //   const res = await getChildList({ pid: currentNode.value.id });
  //   let records = res?.records ? res.records : res || [];
  //   const { materialCode, materialName, materialSpec } = params || {};
  //   if (materialCode) {
  //     records = records.filter((r) => (r.materialCode || '').toLowerCase().includes(materialCode.toLowerCase()));
  //   }
  //   if (materialName) {
  //     records = records.filter((r) => (r.materialName || '').includes(materialName));
  //   }
  //   if (materialSpec) {
  //     records = records.filter((r) => (r.materialSpec || '').includes(materialSpec));
  //   }
  //   const pageNo = params.pageNo || 1;
  //   const pageSize = params.pageSize || 10;
  //   return {
  //     records: records.slice((pageNo - 1) * pageSize, pageNo * pageSize),
  //     total: records.length,
  //   };
  // }


  /**
   * 右侧表格数据源：
   * 选中分类 -> 显示其所有直接子节点；选中叶子 -> 显示该叶子自身
   */
  async function loadChildList(params) {
    const node = currentNode.value;
    if (!node) {
      return { records: [], total: 0 };
    }
    let records;
    if (node.hasChild === '1') {
      const res = await getChildList({ pid: node.id });
      records = res?.records ? res.records : res || [];
    } else {
      // 叶子节点：表格显示自身，便于操作
      records = [node];
    }
    const { materialCode, materialName, materialSpec } = params || {};
    if (materialCode) {
      records = records.filter((r) => (r.materialCode || '').toLowerCase().includes(materialCode.toLowerCase()));
    }
    if (materialName) {
      records = records.filter((r) => (r.materialName || '').includes(materialName));
    }
    if (materialSpec) {
      records = records.filter((r) => (r.materialSpec || '').includes(materialSpec));
    }
    const pageNo = params.pageNo || 1;
    const pageSize = params.pageSize || 10;
    return {
      records: records.slice((pageNo - 1) * pageSize, pageNo * pageSize),
      total: records.length,
    };
  }
  // ================= 树相关方法 =================

  /**
   * 判断是否树节点：顶级分类（pid为空或'0'） 或 有子节点的分类
   */
  function isTreeNode(item) {
    return !item.pid || item.pid === '0' || item.hasChild === '1';
  }

  /**
   * 把接口记录转成树节点
   * 规则：pid='0' 的顶级父节点 或 有子节点的分类，显示在左侧树；物料叶子不进树
   */
  function buildTreeNodes(records) {
    // return (records || [])
    //   .filter((item) => isTreeNode(item))
    //   .map((item) => ({
    //     ...item,
    //     title: `${item.materialCode || ''} ${item.materialName || ''}`.trim(),
    //     // children 不设置，配合 loadData 懒加载
    //   }));
    return (records || []).map((item) => ({
      ...item,
      title: `${item.materialCode || ''} ${item.materialName || ''}`.trim(),
      isLeaf: item.hasChild !== '1',
    }));
  }

  // /**
  //  * 初始化树（加载顶级分类，并默认选中第一个）
  //  */
  // async function initTree() {
  //   treeLoading.value = true;
  //   try {
  //     const res = await list({ pageNo: 1, pageSize: 500, hasQuery: 'true' });
  //     const records = res?.records ? res.records : res || [];
  //     treeData.value = buildTreeNodes(records);
  //     if (treeData.value.length > 0) {
  //       const first = treeData.value[0];
  //       selectedKeys.value = [first.id];
  //       currentNode.value = first;
  //       reload();
  //     }
  //   } finally {
  //     treeLoading.value = false;
  //   }
  // }
  /**
   * 初始化树（加载顶级分类，并默认选中第一个）
   */
  async function initTree() {
    treeLoading.value = true;
    try {
      // 不带 hasQuery，走 rootList 的普通分页分支（where pid='0'）
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
      console.error('物料分类树加载失败', e);
      treeData.value = [];
    } finally {
      treeLoading.value = false;
    }
  }
  initTree();

  /**
   * 树节点懒加载子分类
   */
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
   * 选中树节点 -> 加载右侧子节点表格
   */
  function onTreeSelect(keys, info) {
    if (!keys || keys.length === 0) {
      return;
    }
    selectedKeys.value = keys;
    currentNode.value = info.node.dataRef;
    selectedRowKeys.value = [];
    reload();
  }

  /**
   * 树搜索：过滤已加载的节点（未展开加载的节点不参与搜索）
   */
  const displayTreeData = computed(() => {
    const kw = treeSearchText.value.trim().toLowerCase();
    if (!kw) {
      return treeData.value;
    }
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

  /**
   * 搜索时自动展开所有已加载节点
   */
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

  /**
   * 在树中查找节点
   */
  function findTreeNode(nodes, id) {
    for (const n of nodes || []) {
      if (n.id === id) {
        return n;
      }
      const found = findTreeNode(n.children, id);
      if (found) {
        return found;
      }
    }
    return null;
  }

  /**
   * 整体刷新树并保持展开状态（新增/删除/变更父级后调用）
   */
  async function reloadTreeKeepExpand() {
    const res = await list({ pageNo: 1, pageSize: 500 }); //, hasQuery: 'true'
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

  /**
   * 新增顶级分类（父节点）
   */
  function handleAddRoot() {
    openModal(true, {
      isUpdate: false,
      title: '新增分类',
    });
  }

  /**
   * 在选中分类下新增物料（叶子）
   */
  function handleAddLeaf() {
    openModal(true, {
      isUpdate: false,
      record: { pid: currentNode.value.id },
      lockPid: true,
      title: `新增物料（${currentNode.value.materialName}）`,
    });
  }

  /**
   * 在选中分类下新增子分类
   */
  function handleAddSubCategory() {
    openModal(true, {
      isUpdate: false,
      record: { pid: currentNode.value.id },
      lockPid: true,
      title: `新增子分类（${currentNode.value.materialName}）`,
    });
  }

  /**
   * 编辑
   */
  function handleEdit(record) {
    openModal(true, {
      record,
      isUpdate: true,
    });
  }

  /**
   * 详情
   */
  function handleDetail(record) {
    openModal(true, {
      record,
      isUpdate: true,
      hideFooter: true,
    });
  }

  /**
   * 添加下级（把当前行作为父级）
   */
  function handleAddSub(record) {
    openModal(true, {
      isUpdate: false,
      record: { pid: record.id },
      lockPid: true,
      title: `添加下级（${record.materialName}）`,
    });
  }

  /**
   * 进入下级：在树中选中该分类，右表显示其子节点
   */
  async function handleEnterCategory(record) {
    const pid = record.pid;
    // 确保父节点在树中已展开且子分类已加载
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
      reload();
    }
  }

  /**
   * 删除
   */
  async function handleDelete(record) {
    await deleteMaterial({ id: record.id }, afterDelete);
  }

  /**
   * 批量删除
   */
  async function batchHandleDelete() {
    await batchDeleteMaterial({ id: selectedRowKeys.value }, afterDelete);
  }

  function afterDelete() {
    selectedRowKeys.value = [];
    reload();
    // 删除后父节点 has_child 可能变化，整体刷新树保持展开
    reloadTreeKeepExpand();
  }

  /**
   * 导入成功
   */
  function importSuccess() {
    selectedRowKeys.value = [];
    reload();
    reloadTreeKeepExpand();
  }

  /**
   * 表单保存成功回调
   */
  async function handleSuccess({ isUpdate, values, changeParent }) {
    if (isUpdate && !changeParent) {
      // 普通编辑：刷新右侧表格；若编辑的是树上的分类节点，同步树标题
      reload();
      const node = findTreeNode(treeData.value, values.id);
      if (node) {
        Object.assign(node, values, {
          title: `${values.materialCode || ''} ${values.materialName || ''}`.trim(),
        });
        treeData.value = [...treeData.value];
      }
    } else {
      // 新增 或 编辑时变更了父级：刷新表格 + 整体刷新树
      reload();
      await reloadTreeKeepExpand();
    }
  }

  /**
   * 操作栏
   */
  function getTableAction(record) {
    const actions: any[] = [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
        auth: 'mdm:mis_material:edit',
      },
      {
        label: '添加下级',
        onClick: handleAddSub.bind(null, record),
        auth: 'mdm:mis_material:add',
      },
    ];
    // 分类行（有子节点）增加"进入下级"
    if (record.hasChild === '1') {
      actions.unshift({
        label: '进入下级',
        onClick: handleEnterCategory.bind(null, record),
      });
    }
    return actions;
  }

  /**
   * 下拉操作栏
   */
  function getDropDownAction(record) {
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
      {
        label: '删除',
        popConfirm: {
          title: '确定删除吗?',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
        auth: 'mdm:mis_material:delete',
      },
    ];
  }
</script>

<style lang="less" scoped>
  .material-tree-card {
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
  }

  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>

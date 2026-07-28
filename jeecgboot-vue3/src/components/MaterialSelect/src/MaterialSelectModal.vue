<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="选择物料"
    :width="1000"
    destroyOnClose
    okText="确定"
    @ok="handleOk"
  >
    <div class="material-select">
      <!-- 顶部查询条件 -->
      <div class="search-bar">
        <a-input
          v-model:value="queryParam.materialCode"
          placeholder="物料编码"
          allowClear
          class="search-input"
          @pressEnter="handleSearch"
        />
        <a-input
          v-model:value="queryParam.materialName"
          placeholder="物料名称"
          allowClear
          class="search-input"
          @pressEnter="handleSearch"
        />
        <a-button type="primary" @click="handleSearch">查询</a-button>
        <a-button @click="handleReset">重置</a-button>
        <span class="search-tip">双击行可直接选中；输入条件后为全局查询（不受左侧分类限制）</span>
      </div>

      <div class="select-body">
        <!-- 左侧：物料分类树（与物料管理页一致：rootList 顶级 + childList 懒加载） -->
        <div class="left-tree">
          <a-spin :spinning="treeLoading">
            <a-tree
              blockNode
              :treeData="treeData"
              :loadData="onLoadTreeData"
              :expandedKeys="expandedKeys"
              :selectedKeys="selectedTreeKeys"
              :fieldNames="{ key: 'id', title: 'title', children: 'children' }"
              @expand="onTreeExpand"
              @select="onTreeSelect"
            >
              <template #title="{ title, hasChild }">
                <Icon v-if="hasChild === '1'" icon="ant-design:folder-outlined" style="color:#faad14;margin-right:2px" />
                <Icon v-else icon="ant-design:file-outlined" style="color:#bbb;margin-right:2px" />
                {{ title }}
              </template>
            </a-tree>
          </a-spin>
        </div>

        <!-- 右侧：物料列表（叶子） -->
        <div class="right-list">
          <a-table
            rowKey="id"
            size="small"
            :columns="tableColumns"
            :data-source="dataSource"
            :loading="loading"
            :pagination="pagination"
            :row-selection="{ type: 'radio', selectedRowKeys, onChange: onSelectChange }"
            :custom-row="customRow"
            :scroll="{ y: 480 }"
            @change="handleTableChange"
          />
        </div>
      </div>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { reactive, ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { Icon } from '/@/components/Icon';
  import { useMessage } from '/@/hooks/web/useMessage';
  // 复用物料管理页的接口：/mdm/material/rootList、/mdm/material/childList
  import { list, getChildList } from '/@/views/mis/mdm/material/Material.api';

  const { createMessage } = useMessage();
  const emit = defineEmits(['register', 'select']);

  // ===== 顶部查询 =====
  const queryParam = reactive({
    materialCode: '',
    materialName: '',
  });

  // ===== 左侧树 =====
  const treeData = ref<any[]>([]);
  const treeLoading = ref(false);
  const selectedTreeKeys = ref<any[]>([]);
  const expandedKeys = ref<any[]>([]);
  const currentNode = ref<any>(null);

  // ===== 右侧表格 =====
  const loading = ref(false);
  const dataSource = ref<any[]>([]);
  const selectedRowKeys = ref<any[]>([]);
  const selectedRow = ref<any>(null);

  const tableColumns = [
    { title: '物料编码', dataIndex: 'materialCode', width: 140 },
    { title: '物料名称', dataIndex: 'materialName', width: 180 },
    { title: '型号规格', dataIndex: 'materialSpec', width: 150 },
    { title: '描述', dataIndex: 'description', ellipsis: true },
  ];

  const pagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0,
    showTotal: (total: number) => `共 ${total} 条`,
  });

  // 是否为全局查询模式（输入了查询条件）
  const isSearchMode = computed(() => !!(queryParam.materialCode || queryParam.materialName));

  // ===== 弹窗打开时初始化 =====
  const [registerModal, { closeModal }] = useModalInner(async () => {
    resetState();
    await Promise.all([loadTree(), loadTable(1)]);
  });

  function resetState() {
    queryParam.materialCode = '';
    queryParam.materialName = '';
    selectedTreeKeys.value = [];
    expandedKeys.value = [];
    currentNode.value = null;
    clearSelection();
  }

  function clearSelection() {
    selectedRowKeys.value = [];
    selectedRow.value = null;
  }

  // ===== 左侧树（逻辑与物料管理页一致） =====
  function buildTreeNodes(records) {
    return (records || []).map((item) => ({
      ...item,
      title: `${item.materialCode || ''} ${item.materialName || ''}`.trim(),
      isLeaf: item.hasChild !== '1',
    }));
  }

  async function loadTree() {
    treeLoading.value = true;
    try {
      // rootList 普通分页分支（where pid='0'）
      const res = await list({ pageNo: 1, pageSize: 500 });
      const records = res?.records ? res.records : res || [];
      treeData.value = buildTreeNodes(records);
    } finally {
      treeLoading.value = false;
    }
  }

  // 懒加载子节点
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

  function onTreeSelect(keys, info) {
    selectedTreeKeys.value = keys;
    currentNode.value = keys && keys.length ? info.node.dataRef : null;
    clearSelection();
    loadTable(1);
  }

  // ===== 右侧物料列表 =====
  async function loadTable(pageNo = 1) {
    loading.value = true;
    try {
      if (isSearchMode.value || !currentNode.value) {
        // 全局查询（未选分类时默认列出全部叶子物料）：rootList 的 hasQuery 分支
        const params: any = {
          pageNo,
          pageSize: pagination.pageSize,
          hasQuery: 'true',
          hasChild: '0', // 只查叶子物料
          status: '1', // 与原 online 报表口径一致
        };
        if (queryParam.materialCode) params.materialCode = queryParam.materialCode;
        if (queryParam.materialName) params.materialName = queryParam.materialName;
        const res = await list(params);
        dataSource.value = res?.records || [];
        pagination.total = res?.total || 0;
        pagination.current = pageNo;
      } else {
        // 选中树节点：叶子显示自身；分类显示其直接子级中的叶子物料（childList 不分页，前端分页）
        const node = currentNode.value;
        let records: any[];
        if (node.hasChild === '1') {
          const res = await getChildList({ pid: node.id });
          records = (res?.records ? res.records : res || []).filter((r) => r.hasChild !== '1');
        } else {
          records = [node];
        }
        // 状态过滤（与报表口径一致，无状态字段的放行）
        records = records.filter((r) => !r.status || r.status === '1');
        pagination.total = records.length;
        pagination.current = pageNo;
        dataSource.value = records.slice((pageNo - 1) * pagination.pageSize, pageNo * pagination.pageSize);
      }
    } finally {
      loading.value = false;
    }
  }

  // ===== 事件 =====
  function handleSearch() {
    clearSelection();
    loadTable(1);
  }

  function handleReset() {
    queryParam.materialCode = '';
    queryParam.materialName = '';
    selectedTreeKeys.value = [];
    currentNode.value = null;
    clearSelection();
    loadTable(1);
  }

  function handleTableChange(pag: any) {
    pagination.pageSize = pag.pageSize;
    loadTable(pag.current);
  }

  function onSelectChange(keys: any[]) {
    selectedRowKeys.value = keys;
    selectedRow.value = dataSource.value.find((item) => item.id === keys[0]) || null;
  }

  // 行点击 = 选中；行双击 = 直接选中并回填
  function customRow(record: any) {
    return {
      onClick: () => {
        selectedRowKeys.value = [record.id];
        selectedRow.value = record;
      },
      onDblclick: () => doSelect(record),
    };
  }

  function handleOk() {
    if (!selectedRow.value) {
      createMessage.warning('请先选择一条物料（双击行可直接选择）');
      return;
    }
    doSelect(selectedRow.value);
  }

  function doSelect(record: any) {
    emit('select', record);
    closeModal();
  }
</script>

<style lang="less" scoped>
  .material-select {
    .search-bar {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;

      .search-input {
        width: 200px;
      }

      .search-tip {
        color: #999;
        font-size: 12px;
      }
    }

    .select-body {
      display: flex;
      border: 1px solid #f0f0f0;
      border-radius: 4px;
      height: 560px; // 撑高弹窗

      .left-tree {
        width: 260px;
        border-right: 1px solid #f0f0f0;
        padding: 8px;
        overflow: auto;
        height: 100%;
      }

      .right-list {
        flex: 1;
        padding: 8px 0 0 8px;
        overflow: hidden;
      }
    }
  }
</style>

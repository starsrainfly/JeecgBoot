<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="选择产品"
    :width="1000"
    destroyOnClose
    okText="确定"
    @ok="handleOk"
  >
    <div class="product-select">
      <!-- 顶部查询条件 -->
      <div class="search-bar">
        <a-input
          v-model:value="queryParam.productCode"
          placeholder="产品编码"
          allowClear
          class="search-input"
          @pressEnter="handleSearch"
        />
        <a-input
          v-model:value="queryParam.productName"
          placeholder="产品名称"
          allowClear
          class="search-input"
          @pressEnter="handleSearch"
        />
        <a-input
          v-model:value="queryParam.productSpec"
          placeholder="型号规格"
          allowClear
          class="search-input"
          @pressEnter="handleSearch"
        />
        <a-button type="primary" @click="handleSearch">查询</a-button>
        <a-button @click="handleReset">重置</a-button>
        <span class="search-tip">双击行可直接选中；输入条件后为全局查询（不受左侧分类限制）</span>
      </div>

      <div class="select-body">
        <!-- 左侧：产品分类树 -->
        <div class="left-tree">
          <a-spin :spinning="treeLoading">
            <a-tree
              v-if="treeData.length > 0"
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
            <a-empty v-else-if="!treeLoading" description="暂无分类数据" />
          </a-spin>
        </div>

        <!-- 右侧：产品列表（叶子） -->
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
  import { reactive, ref, computed, nextTick } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { Icon } from '/@/components/Icon';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { defHttp } from '/@/utils/http/axios';

  const { createMessage } = useMessage();
  const emit = defineEmits(['register', 'select']);

  // ===== 直接定义接口（避免外部 API 文件路径问题） =====
  const API = {
    rootList: '/product/product/rootList',
    childList: '/product/product/childList',
  };

  async function fetchRootList(params: any) {
    return defHttp.get({ url: API.rootList, params });
  }
  async function fetchChildList(params: any) {
    return defHttp.get({ url: API.childList, params });
  }

  // ===== 顶部查询 =====
  const queryParam = reactive({
    productCode: '',
    productName: '',
    productSpec: '',
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
    { title: '产品编码', dataIndex: 'productCode', width: 140 },
    { title: '产品名称', dataIndex: 'productName', width: 180 },
    { title: '型号规格', dataIndex: 'productSpec', width: 150 },
    { title: '颜色', dataIndex: 'productColor', width: 100 },
    { title: '配方编码', dataIndex: 'recipeCode', width: 120 },
    { title: '配方版本', dataIndex: 'recipeVersion', width: 120 },
    { title: '描述', dataIndex: 'description', ellipsis: true },
  ];

  const pagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0,
    showTotal: (total: number) => `共 ${total} 条`,
  });

  const isSearchMode = computed(() => !!(queryParam.productCode || queryParam.productName || queryParam.productSpec));

  // ===== 弹窗打开时初始化 =====
  const [registerModal, { closeModal }] = useModalInner(async () => {
    resetState();
    await loadTree();
    await loadTable(1);
  });

  function resetState() {
    queryParam.productCode = '';
    queryParam.productName = '';
    queryParam.productSpec = '';
    selectedTreeKeys.value = [];
    expandedKeys.value = [];
    currentNode.value = null;
    clearSelection();
  }

  function clearSelection() {
    selectedRowKeys.value = [];
    selectedRow.value = null;
  }

  // ===== 左侧树 =====
  function buildTreeNodes(records: any[]) {
    return (records || [])
      .filter((item) => !item.pid || item.pid === '0' || item.hasChild === '1')
      .map((item) => ({
        ...item,
        title: `${item.productCode || ''} ${item.productName || ''}`.trim(),
        isLeaf: item.hasChild !== '1',
      }));
  }

  async function loadTree() {
    treeLoading.value = true;
    try {
      const res = await fetchRootList({ pageNo: 1, pageSize: 500 });
      let records = res?.records ? res.records : res || [];
      if (!records || records.length === 0) {
        try {
          const res2 = await fetchChildList({ pid: '0', pageNo: 1, pageSize: 500 });
          records = res2?.records ? res2.records : res2 || [];
        } catch (e2) {
          console.warn('[ProductSelectModal] childList fallback failed:', e2);
        }
      }
      treeData.value = buildTreeNodes(records);
      await nextTick();
    } catch (error) {
      console.error('[ProductSelectModal] loadTree error:', error);
      createMessage.error('加载产品分类失败：' + (error.message || '未知错误'));
    } finally {
      treeLoading.value = false;
    }
  }

  function onLoadTreeData(treeNode: any) {
    return new Promise<void>((resolve) => {
      if (treeNode.dataRef.children && treeNode.dataRef.children.length > 0) {
        resolve();
        return;
      }
      fetchChildList({ pid: treeNode.dataRef.id }).then((res) => {
        const records = res?.records ? res.records : res || [];
        treeNode.dataRef.children = buildTreeNodes(records);
        treeData.value = [...treeData.value];
        resolve();
      }).catch((err) => {
        console.error('[ProductSelectModal] onLoadTreeData error:', err);
        resolve();
      });
    });
  }

  function onTreeExpand(keys: any[]) {
    expandedKeys.value = keys;
  }

  function onTreeSelect(keys: any[], info: any) {
    selectedTreeKeys.value = keys;
    currentNode.value = keys && keys.length ? info.node.dataRef : null;
    clearSelection();
    loadTable(1);
  }

  // ===== 关键修复：递归加载分类下的所有后代叶子产品 =====
  async function loadAllLeafProducts(pid: string): Promise<any[]> {
    const res = await fetchChildList({ pid });
    const records = (res?.records ? res.records : res || []) as any[];

    const leaves = records.filter((r) => r.hasChild !== '1');
    const categories = records.filter((r) => r.hasChild === '1');

    if (categories.length === 0) {
      return leaves;
    }

    // 并行递归加载所有子分类下的叶子（同级分类并行，提升效率）
    const childResults = await Promise.all(
      categories.map((c) => loadAllLeafProducts(c.id))
    );

    return leaves.concat(...childResults);
  }

  // ===== 右侧产品列表 =====
  async function loadTable(pageNo = 1) {
    if (isSearchMode.value) {
      return searchProduct(pageNo);
    }

    loading.value = true;
    try {
      const node = currentNode.value;
      let records: any[] = [];

      if (!node) {
        // 未选中节点：加载所有叶子产品（全局浏览）
        const res = await fetchChildList({ hasChild: '0' });
        records = (res?.records ? res.records : res || []).filter((r: any) => r.hasChild !== '1');
      } else if (node.hasChild === '1') {
        // 关键修复：分类节点递归加载所有后代叶子，而非仅直接子级
        records = await loadAllLeafProducts(node.id);
      } else {
        // 叶子节点：仅显示自身
        records = [node];
      }

      // 状态过滤（启用的）
      records = records.filter((r: any) => !r.status || r.status === '1');
      pagination.total = records.length;
      pagination.current = pageNo;
      dataSource.value = records.slice((pageNo - 1) * pagination.pageSize, pageNo * pagination.pageSize);
    } catch (error) {
      console.error('[ProductSelectModal] loadTable error:', error);
      createMessage.error('加载产品列表失败：' + (error.message || '未知错误'));
    } finally {
      loading.value = false;
    }
  }

  async function searchProduct(pageNo = 1) {
    loading.value = true;
    try {
      const params: any = {
        hasChild: '0',
        status: '1',
      };
      if (queryParam.productCode) params.productCode = queryParam.productCode;
      if (queryParam.productName) params.productName = queryParam.productName;
      if (queryParam.productSpec) params.productSpec = queryParam.productSpec;

      const res = await fetchChildList(params);
      let records = res?.records ? res.records : res || [];
      records = records.filter((r: any) => r.hasChild !== '1' && (!r.status || r.status === '1'));

      pagination.total = records.length;
      pagination.current = pageNo;
      dataSource.value = records.slice((pageNo - 1) * pagination.pageSize, pageNo * pagination.pageSize);
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
    queryParam.productCode = '';
    queryParam.productName = '';
    queryParam.productSpec = '';
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
      createMessage.warning('请先选择一条产品（双击行可直接选择）');
      return;
    }
    doSelect(selectedRow.value);
  }

  function doSelect(record: any) {
    console.log('[ProductSelectModal] doSelect record:', record);
    emit('select', record);
    closeModal();
  }
</script>

<style lang="less" scoped>
  .product-select {
    .search-bar {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;
      .search-input {
        width: 180px;
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
      height: 560px;
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

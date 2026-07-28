<template>
  <div>
    <BasicTable @register="registerTable">
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
      <!-- 自定义列：到货进度 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'arrivalProgress'">
          <a-progress
            :percent="calcProgress(record)"
            size="small"
            :status="calcProgress(record) >= 100 ? 'success' : 'active'"
          />
        </template>
      </template>
    </BasicTable>

    <!-- 复用现有的到货弹窗 -->
    <PurchaseArrivalModal @register="registerArrivalModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="scm-purchaseArrival" setup>
  import { reactive } from 'vue';
  import { BasicTable, TableAction } from '@/components/Table';
  import { useListPage } from '@/hooks/system/useListPage';
  import { useModal } from '@/components/Modal';
  import PurchaseArrivalModal from '../components/PurchaseArrivalModal.vue';
  import { columns, searchFormSchema } from '../PurchaseOrder.data';
  import { list } from '../PurchaseOrder.api';

  const queryParam = reactive<any>({
    // 固定只查已审核通过的采购单
    approveStatus: '1',
  });

  const [registerArrivalModal, { openModal: openArrivalModal }] = useModal();

  const { tableContext } = useListPage({
    tableProps: {
      title: '采购到货',
      api: list,
      columns: [
        ...columns,
        {
          title: '到货进度',
          align: 'center',
          key: 'arrivalProgress',
          width: 160,
          customRender: ({ record }) => {
            const p = calcProgress(record);
            return `${p}%`;
          },
        },
      ],
      canResize: false,
      formConfig: {
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
      },
      actionColumn: {
        width: 100,
        fixed: 'right',
      },
      beforeFetch: (params) => {
        // 强制过滤：已审核 + 非全部到货状态
        return Object.assign(params, queryParam, {
          status: 'PURCHASING,APPROVED', // 按你实际状态字典调整
        });
      },
    },
  });

  const [registerTable, { reload }] = tableContext;

  /** 计算到货进度（已入库总额 / 采购总额），这里简化用主表字段，如有明细数据可更精确 */
  function calcProgress(record: any) {
    const total = Number(record.orderTotal) || 1;
    // 如果主表没有已到货金额字段，先写死 0，建议后端接口返回 arrivedTotal
    const arrived = Number(record.arrivedTotal) || 0;
    return Math.min(100, Math.round((arrived / total) * 100));
  }

  function handleArrival(record: Recordable) {
    openArrivalModal(true, { record });
  }

  function handleSuccess() {
    reload();
  }

  function getTableAction(record: Recordable) {
    return [
      {
        label: '到货',
        type: 'primary',
        onClick: handleArrival.bind(null, record),
       // auth: 'scm:mis_purchase_order:arrival',
        // 如果已全部到货，不显示按钮（需要后端返回 arrivedTotal 或 status 判断）
        ifShow: record.status !== 'COMPLETED',
      },
    ];
  }
</script>

<style lang="less" scoped>
  :deep(.ant-progress) {
    width: 100px;
  }
</style>

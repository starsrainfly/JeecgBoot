import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '生产单号',
    align: "center",
    dataIndex: 'orderNo',
    width: 150,
    fixed: 'left',
  },
  {
    title: '产品编码',
    align: "center",
    dataIndex: 'productCode',
    width: 120,
    fixed: 'left',
  },
  {
    title: '产品名称',
    align: "center",
    dataIndex: 'productName',
    width: 150,
    ellipsis: true,
  },
  {
    title: '颜色',
    align: "center",
    dataIndex: 'productColor',
    width: 120,
  },
  {
    title: '配方编码',
    align: "center",
    dataIndex: 'recipeCode',
    width: 100,
  },
  {
    title: '计划产量',
    align: "center",
    dataIndex: 'plannedQty',
    width: 100,
  },
  {
    title: '批次进度',
    align: "center",
    dataIndex: 'batchProgress',
    width: 160,
   // slots: { customRender: 'batchProgress' },
  },
  {
    title: '实际产量',
    align: "center",
    dataIndex: 'totalActualQty',
    width: 100,
  },
  {
    title: '已入库',
    align: "center",
    dataIndex: 'totalInStockQty',
    width: 100,
  },
  {
    title: '订单状态',
    align: "center",
    dataIndex: 'status_dictText',
    width: 100,
  },
  {
    title: '计划开工',
    align: "center",
    dataIndex: 'plannedStartDate',
    width: 110,
  },
  {
    title: '计划完工',
    align: "center",
    dataIndex: 'plannedEndDate',
    width: 110,
   // slots: { customRender: 'plannedEndDate' },
  },
  {
    title: '实际开工',
    align: "center",
    dataIndex: 'actualStartTime',
    width: 140,
  },
  {
    title: '实际完工',
    align: "center",
    dataIndex: 'actualEndTime',
    width: 140,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: "生产单号",
    field: 'orderNo',
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "产品编码",
    field: 'productCode',
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "产品名称",
    field: 'productName',
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "订单状态",
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'mes_production_status'  // 请确认字典：0草稿 1已下达 2部分完成 3已完成
    },
    colProps: {span: 6},
  },
  {
    label: "计划日期",
    field: 'planDate',
    component: 'RangePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    colProps: {span: 8},
  },
];

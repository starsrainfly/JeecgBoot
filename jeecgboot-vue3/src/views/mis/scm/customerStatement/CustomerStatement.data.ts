import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '客户名称',
    align: "center",
    dataIndex: 'customerName',
    width: 180,
    fixed: 'left',
  },
  {
    title: '订单号',
    align: "center",
    dataIndex: 'orderNo',
    width: 160,
  },
  {
    title: '订单日期',
    align: "center",
    dataIndex: 'orderDate',
    width: 110,
  },
  {
    title: '应收日期',
    align: "center",
    dataIndex: 'receivableDate',
    width: 110,
  },
  {
    title: '订单总额',
    align: "center",
    dataIndex: 'orderTotal',
    width: 120,
  },
  {
    title: '发货金额',
    align: "center",
    dataIndex: 'deliveredAmount',
    width: 120,
  },
  {
    title: '已收款',
    align: "center",
    dataIndex: 'receivedAmount',
    width: 120,
  },
  {
    title: '欠款',
    align: "center",
    dataIndex: 'outstanding',
    width: 120,
  },
  {
    title: '逾期天数',
    align: "center",
    dataIndex: 'overdueDays',
    width: 100,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: "客户名称",
    field: 'customerName',
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "订单号",
    field: 'orderNo',
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "订单日期",
    field: 'orderDate',
    component: 'RangePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    colProps: {span: 8},
  },
];

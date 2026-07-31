import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '客户名称',
    align: "center",
    dataIndex: 'customerName',
    width: 200,
    fixed: 'left',
  },
  {
    title: '总欠款',
    align: "center",
    dataIndex: 'totalOutstanding',
    width: 140,
  },
  {
    title: '1个月内',
    align: "center",
    dataIndex: 'current',
    width: 140,
  },
  {
    title: '1-3个月',
    align: "center",
    dataIndex: 'aging1to3',
    width: 140,
  },
  {
    title: '3-6个月',
    align: "center",
    dataIndex: 'aging3to6',
    width: 140,
  },
  {
    title: '6个月以上',
    align: "center",
    dataIndex: 'aging6plus',
    width: 140,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: "客户名称",
    field: 'customerName',
    component: 'Input',
    colProps: {span: 8},
  },
];

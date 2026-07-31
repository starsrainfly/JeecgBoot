import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '订单号',
    align: "center",
    dataIndex: 'orderNo',
    width: 150,
    fixed: 'left',
  },
  {
    title: '订单日期',
    align: "center",
    dataIndex: 'orderDate',
    width: 110,
  },
  {
    title: '客户名称',
    align: "center",
    dataIndex: 'customerName',
    width: 180,
    ellipsis: true,
  },
  {
    title: '业务员',
    align: "center",
    dataIndex: 'salesmanName',
    width: 100,
  },
  {
    title: '订单总额',
    align: "center",
    dataIndex: 'orderTotal',
    width: 120,
  },
  {
    title: '已收款',
    align: "center",
    dataIndex: 'receivedAmount',
    width: 120,
  },
  {
    title: '未收款',
    align: "center",
    dataIndex: 'unreceivedAmount',
    width: 120,
  },
  {
    title: '交货日期',
    align: "center",
    dataIndex: 'deliveryDate',
    width: 110,
    slots: { customRender: 'deliveryDate' },
  },
  {
    title: '应收日期',
    align: "center",
    dataIndex: 'receivableDate',
    width: 110,
    slots: { customRender: 'receivableDate' },
  },
  {
    title: '订单状态',
    align: "center",
    dataIndex: 'orderStatus_dictText',
    width: 100,
  },
  {
    title: '发货状态',
    align: "center",
    dataIndex: 'deliveryStatus_dictText',
    width: 100,
  },
  {
    title: '结算状态',
    align: "center",
    dataIndex: 'settleStatus_dictText',
    width: 100,
  },
  {
    title: '审核状态',
    align: "center",
    dataIndex: 'salesApproveStatus_dictText',
    width: 100,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: "订单号",
    field: 'orderNo',
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "客户名称",
    field: 'customerName',
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "业务员",
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "sys_user where del_flag='0' and status='1',realname,id"
    },
    colProps: {span: 6},
  },
  {
    label: "订单日期",
    field: 'orderDate',
    component: 'RangePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    colProps: {span: 6},
  },
  {
    label: "订单状态",
    field: 'orderStatus',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'scm_order_status'
    },
    colProps: {span: 6},
  },
  {
    label: "发货状态",
    field: 'deliveryStatus',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'wms_delivery_status'
    },
    colProps: {span: 6},
  },
  {
    label: "结算状态",
    field: 'settleStatus',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'fms_settlement_status'
    },
    colProps: {span: 6},
  },
  {
    label: "审核状态",
    field: 'salesApproveStatus',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'approval_status'
    },
    colProps: {span: 6},
  },
];

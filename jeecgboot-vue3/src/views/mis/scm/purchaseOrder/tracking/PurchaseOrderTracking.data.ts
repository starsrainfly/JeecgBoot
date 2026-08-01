import { BasicColumn, FormSchema } from '@/components/Table';


// ==================== 表格列定义 ====================
export const columns: BasicColumn[] = [
  {
    title: '采购单号',
    dataIndex: 'orderNo',
    width: 160,
    fixed: 'left',
  },
  {
    title: '供应商',
    dataIndex: 'supplierName',
    width: 180,
  },
  {
    title: '采购员',
    dataIndex: 'purchaserName',
    width: 100,
  },
  {
    title: '要求到货日期',
    dataIndex: 'expectedDate',
    width: 120,
  },
  {
    title: '采购数量合计',
    dataIndex: 'totalOrderQty',
    width: 120,
    align: 'right',
  },
  {
    title: '已入库合计',
    dataIndex: 'totalReceivedQty',
    width: 120,
    align: 'right',
  },
  {
    title: '在途申请合计',
    dataIndex: 'totalAppliedQty',
    width: 120,
    align: 'right',
  },
  {
    title: '到货率',
    dataIndex: 'arrivalRate',
    width: 160,
    slots: { customRender: 'arrivalRate' },
  },
  {
    title: '是否超期',
    dataIndex: 'isOverdue',
    width: 100,
    align: 'center',
    slots: { customRender: 'isOverdue' },
  },
  {
    title: '超期天数',
    dataIndex: 'overdueDays',
    width: 100,
    align: 'right',
    customRender: ({ text }) => {
      if (!text || text <= 0) return '-';
      return `${text} 天`;
    },
  },
  {
    title: '业务状态',
    dataIndex: 'status_dictText',
    width: 100,
   // dictCode: 'scm_purchase_status',
  },
  {
    title: '审核状态',
    dataIndex: 'approveStatus_dictText',
    width: 100,
   // dictCode: 'approval_status',
  },
  {
    title: '币种',
    dataIndex: 'currencyCode',
    width: 80,
   // dictCode: 'mis_currency,currency_name,currency_code',
  },
  {
    title: '含税总额',
    dataIndex: 'orderTotal',
    width: 120,
    align: 'right',
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 180,
    ellipsis: true,
  },
];

// ==================== 搜索表单定义 ====================
export const searchFormSchema: FormSchema[] = [
  {
    label: '采购单号',
    field: 'orderNo',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '供应商',
    field: 'supplierName',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '采购员',
    field: 'purchaserId',
    component: 'JSelectUserByDept',
    componentProps: {
      rowKey: 'id',
      labelKey: 'realname',
    },
    colProps: { span: 6 },
  },
  {
    label: '要求到货日期',
    field: 'expectedDate',
    component: 'RangePicker',
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
    },
    colProps: { span: 6 },
  },
  {
    label: '业务状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'scm_purchase_status',
    },
    colProps: { span: 6 },
  },
  {
    label: '审核状态',
    field: 'approveStatus',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'approval_status',
    },
    colProps: { span: 6 },
  },
  {
    label: '是否超期',
    field: 'isOverdue',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'yn',
    },
    colProps: { span: 6 },
  },
];

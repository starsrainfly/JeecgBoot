import { BasicColumn, FormSchema } from '@/components/Table';
import dayjs from 'dayjs';

export const columns: BasicColumn[] = [
  { title: '客户', dataIndex: 'customerName', width: 200 },
  { title: '期间', dataIndex: 'period', width: 100, align: 'center' },
  { title: '出库笔数', dataIndex: 'outCount', width: 100, align: 'right' },
  { title: '出库数量合计', dataIndex: 'totalQty', width: 140, align: 'right' },
  { title: '销售金额合计', dataIndex: 'totalSalesAmount', width: 140, align: 'right' },
  { title: '成本金额合计', dataIndex: 'totalCostAmount', width: 140, align: 'right' },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '期间起',
    field: 'startPeriod',
    component: 'DatePicker',
    componentProps: {
      picker: 'month',
      format: 'YYYY-MM',
      valueFormat: 'YYYY-MM',
      placeholder: '请选择月份',
    },
    defaultValue: dayjs().subtract(2, 'month').format('YYYY-MM'),
    colProps: { span: 6 },
  },
  {
    label: '期间止',
    field: 'endPeriod',
    component: 'DatePicker',
    componentProps: {
      picker: 'month',
      format: 'YYYY-MM',
      valueFormat: 'YYYY-MM',
      placeholder: '请选择月份',
    },
    defaultValue: dayjs().format('YYYY-MM'),
    colProps: { span: 6 },
  },
  {
    label: '客户名称',
    field: 'customerName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const { setFieldsValue } = formActionType;
      return {
        setFieldsValue: setFieldsValue,
        code: 'scm_customer_no_param',
        fieldConfig: [
          { source: 'id', target: 'customerId' },
          { source: 'customer_code', target: 'customerCode' },
          { source: 'customer_name', target: 'customerName' },
        ],
        multi: false,
      };
    },
    colProps: { span: 6 },
  },
  {
    label: '客户id',
    field: 'customerId',
    component: 'Input',
    show: false,
  },
  {
    label: '出库类型',
    field: 'stockOutType',
    component: 'JDictSelectTag',
    componentProps: { dictCode: 'wms_stock_out_type', placeholder: '请选择出库类型' },
    colProps: { span: 6 },
  },
];

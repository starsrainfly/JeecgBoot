import { BasicColumn, FormSchema } from '@/components/Table';
import dayjs from 'dayjs';

export const columns: BasicColumn[] = [
  { title: '供应商', dataIndex: 'supplierName', width: 200 },
  { title: '期间', dataIndex: 'period', width: 100, align: 'center' },
  { title: '入库笔数', dataIndex: 'inCount', width: 100, align: 'right' },
  { title: '入库数量合计', dataIndex: 'totalQty', width: 140, align: 'right' },
  { title: '入库金额合计', dataIndex: 'totalAmount', width: 140, align: 'right' },
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
    label: '供应商',
    field: 'supplierName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const { setFieldsValue } = formActionType;
      return {
        setFieldsValue: setFieldsValue,
        code: 'scm_supplier',
        fieldConfig: [
          { source: 'id', target: 'supplierId' },
          { source: 'supplier_name', target: 'supplierName' },
        ],
        multi: true,
      };
    },
    colProps: { span: 6 },
  },
  {
    label: '供应商id',
    field: 'supplierId',
    component: 'Input',
    show: false,
  },
  {
    label: '入库类型',
    field: 'stockInType',
    component: 'JDictSelectTag',
    componentProps: { dictCode: 'wms_stock_in_type', placeholder: '请选择入库类型' },
    colProps: { span: 6 },
  },
];

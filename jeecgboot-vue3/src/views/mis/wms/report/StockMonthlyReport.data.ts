import { BasicColumn, FormSchema } from '@/components/Table';
import dayjs from 'dayjs';

export const columns: BasicColumn[] = [
  { title: '物料编码', dataIndex: 'goodsCode', width: 120, fixed: 'left' },
  { title: '物料名称', dataIndex: 'goodsName', width: 180 },
  { title: '单位', dataIndex: 'unit', width: 80, align: 'center' },
  { title: '期间', dataIndex: 'period', width: 100, align: 'center' },
  { title: '期初数量', dataIndex: 'openingQty', width: 120, align: 'right' },
  { title: '期初金额', dataIndex: 'openingAmount', width: 120, align: 'right' },
  { title: '入库数量', dataIndex: 'inQty', width: 120, align: 'right' },
  { title: '入库金额', dataIndex: 'inAmount', width: 120, align: 'right' },
  { title: '出库数量', dataIndex: 'outQty', width: 120, align: 'right' },
  { title: '出库金额', dataIndex: 'outAmount', width: 120, align: 'right' },
  { title: '期末数量', dataIndex: 'closingQty', width: 120, align: 'right' },
  { title: '期末金额', dataIndex: 'closingAmount', width: 120, align: 'right' },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '期间',
    field: 'period',
    component: 'DatePicker',
    componentProps: {
      picker: 'month',
      format: 'YYYY-MM',
      valueFormat: 'YYYY-MM',
      placeholder: '请选择月份',
    },
    defaultValue: dayjs().format('YYYY-MM'),
    required: true,
    colProps: { span: 6 },
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'mis_warehouse,name,id',
      placeholder: '请选择仓库',
    },
    colProps: { span: 6 },
  },
  {
    label: '物料编码',
    field: 'goodsCode',
    component: 'Input',
    componentProps: { placeholder: '请输入物料编码' },
    colProps: { span: 6 },
  },
];

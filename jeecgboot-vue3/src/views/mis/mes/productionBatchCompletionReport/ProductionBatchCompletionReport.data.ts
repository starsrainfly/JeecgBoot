// views/mes/productionBatchCompletionReport/ProductionBatchCompletionReport.data.ts
import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  { title: '订单编号', dataIndex: 'orderNo', width: 150, align: 'center', fixed: 'left' },
  { title: '批次号', dataIndex: 'batchNo', width: 150, align: 'center', fixed: 'left' },
  { title: '序号', dataIndex: 'batchSeq', width: 60, align: 'center' },
  { title: '产品编码', dataIndex: 'productCode', width: 120, align: 'center' },
  { title: '产品名称', dataIndex: 'productName', width: 150, align: 'center', ellipsis: true },
  { title: '产品颜色', dataIndex: 'productColor', width: 90, align: 'center' },
  { title: '配方编码', dataIndex: 'recipeCode', width: 100, align: 'center' },
  { title: '计划生产量(Kg)', dataIndex: 'plannedQty', width: 125, align: 'center' },
  { title: '实际生产量(Kg)', dataIndex: 'actualQty', width: 125, align: 'center' },
  { title: '已入库量(Kg)', dataIndex: 'inStockQty', width: 115, align: 'center' },
  { title: '入库状态', dataIndex: 'inStockStatus', width: 95, align: 'center' },
  { title: '质检状态', dataIndex: 'qcStatus', width: 95, align: 'center' },
  { title: '状态', dataIndex: 'status', width: 95, align: 'center' },
  { title: '生产日期', dataIndex: 'productionDate', width: 110, align: 'center' },
  { title: '失效日期', dataIndex: 'expiryDate', width: 110, align: 'center' },
  { title: '配料状态', dataIndex: 'weighingStatus', width: 95, align: 'center' },
  { title: '实际总投料(Kg)', dataIndex: 'totalActualWeight', width: 130, align: 'center' },
];

export const searchFormSchema: FormSchema[] = [
  { label: '订单编号', field: 'orderNo', component: 'Input', colProps: { span: 6 } },
  { label: '批次号', field: 'batchNo', component: 'Input', colProps: { span: 6 } },
  { label: '产品编码', field: 'productCode', component: 'Input', colProps: { span: 6 } },
  { label: '产品名称', field: 'productName', component: 'Input', colProps: { span: 6 } },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: { dictCode: 'mes_batch_status' },
    colProps: { span: 6 },
  },
  {
    label: '入库状态',
    field: 'inStockStatus',
    component: 'JDictSelectTag',
    componentProps: { dictCode: 'mes_in_stock_status' },
    colProps: { span: 6 },
  },
  {
    label: '质检状态',
    field: 'qcStatus',
    component: 'JDictSelectTag',
    componentProps: { dictCode: 'mes_qc_status' },
    colProps: { span: 6 },
  },
  {
    label: '生产日期',
    field: 'productionDate',
    component: 'RangePicker',
    componentProps: { valueFormat: 'YYYY-MM-DD' },
    colProps: { span: 8 },
  },
];

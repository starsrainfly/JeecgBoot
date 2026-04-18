import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

// ==================== 列表页列 ====================
export const columns: BasicColumn[] = [
  {
    title: '发货单号',
    align: 'center',
    dataIndex: 'deliveryNo',
  },
  {
    title: '来源类型',
    align: 'center',
    dataIndex: 'sourceType_dictText',
  },
  {
    title: '来源订单号',
    align: 'center',
    dataIndex: 'sourceOrderNo',
  },
  {
    title: '客户名称',
    align: 'center',
    dataIndex: 'customerName',
  },
  {
    title: '收货人',
    align: 'center',
    dataIndex: 'consignee',
  },
  {
    title: '收货人电话',
    align: 'center',
    dataIndex: 'consigneePhone',
  },
  {
    title: '物流类型',
    align: 'center',
    dataIndex: 'logisticsType_dictText',
  },
  {
    title: '物流公司',
    align: 'center',
    dataIndex: 'logisticsCompany',
  },
  {
    title: '物流单号',
    align: 'center',
    dataIndex: 'logisticsNo',
  },
  {
    title: '发货时间',
    align: 'center',
    dataIndex: 'deliveryTime',
  },
  {
    title: '发货数量',
    align: 'center',
    dataIndex: 'deliveryQty',
  },
  {
    title: '发货金额',
    align: 'center',
    dataIndex: 'deliveryAmount',
  },
  {
    title: '状态',
    align: 'center',
    dataIndex: 'status_dictText',
  },
  {
    title: '出库单号',
    align: 'center',
    dataIndex: 'stockOutNo',
  },
  {
    title: '发货人',
    align: 'center',
    dataIndex: 'deliverBy',
  },
];

// ==================== 列表页搜索表单 ====================
export const searchFormSchema: FormSchema[] = [
  {
    label: '来源订单号',
    field: 'sourceOrderNo',
    component: 'Input',
  },
  {
    label: '客户名称',
    field: 'customerName',
    component: 'Input',
  },
  {
    label: '物流单号',
    field: 'logisticsNo',
    component: 'Input',
  },
  {
    label: '发货时间',
    field: 'deliveryTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
      showTime: true,
    },
  },
  {
    label: '状态',
    field: 'status',
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: 'wms_delivery_status',
    },
  },
];

// ==================== 弹窗内：未发货订单明细列 ====================
export const orderLineColumns: BasicColumn[] = [
  {
    title: '产品编码',
    align: 'center',
    dataIndex: 'itemCode',
  },
  {
    title: '产品名称',
    align: 'center',
    dataIndex: 'itemName',
  },
  {
    title: '规格型号',
    align: 'center',
    dataIndex: 'itemSpec',
  },
  {
    title: '单位',
    align: 'center',
    dataIndex: 'unit',
  },
  {
    title: '订单数量',
    align: 'center',
    dataIndex: 'quantity',
  },
  {
    title: '已发数量',
    align: 'center',
    dataIndex: 'deliveredQty',
  },
  {
    title: '剩余数量',
    align: 'center',
    dataIndex: 'remainingQty',
  },
  {
    title: '行号',
    align: 'center',
    dataIndex: 'sortIndex',
  },
];

// ==================== 弹窗内：FIFO库存列 ====================
export const stockColumns: BasicColumn[] = [
  {
    title: '产品编码',
    align: 'center',
    dataIndex: 'goodsCode',
  },
  {
    title: '产品名称',
    align: 'center',
    dataIndex: 'goodsName',
  },
  {
    title: '批次号',
    align: 'center',
    dataIndex: 'batchNo',
  },
  {
    title: '生产日期',
    align: 'center',
    dataIndex: 'productionDate',
  },
  {
    title: '有效期至',
    align: 'center',
    dataIndex: 'expiryDate',
  },
  {
    title: '库存数量',
    align: 'center',
    dataIndex: 'quantity',
  },
  {
    title: '锁定数量',
    align: 'center',
    dataIndex: 'lockedQty',
  },
  {
    title: '可用数量',
    align: 'center',
    dataIndex: 'availableQty',
    customRender: ({ record }) => {
      const qty = Number(record.quantity || 0);
      const locked = Number(record.lockedQty || 0);
      return (qty - locked).toFixed(2);
    },
  },
  {
    title: '仓库',
    align: 'center',
    dataIndex: 'warehouseId_dictText',
  },
  {
    title: '库位',
    align: 'center',
    dataIndex: 'locationId_dictText',
  },
];

// ==================== 弹窗内：本次发货明细列 ====================
export const deliveryItemColumns: BasicColumn[] = [
  {
    title: '产品编码',
    align: 'center',
    dataIndex: 'goodsCode',
  },
  {
    title: '产品名称',
    align: 'center',
    dataIndex: 'goodsName',
  },
  {
    title: '批次号',
    align: 'center',
    dataIndex: 'productionBatchNo',
  },
  {
    title: '生产日期',
    align: 'center',
    dataIndex: 'productionDate',
  },
  {
    title: '仓库',
    align: 'center',
    dataIndex: 'warehouseName',
  },
  {
    title: '发货数量',
    align: 'center',
    dataIndex: 'actualQty',
    edit: true,
    editComponent: 'InputNumber',
  },
  {
    title: '单价',
    align: 'center',
    dataIndex: 'unitPrice',
  },
  {
    title: '扫码内容',
    align: 'center',
    dataIndex: 'scanCode',
  },
];

// ==================== 弹窗内：物流信息表单 ====================
export const logisticsFormSchema: FormSchema[] = [
  {
    label: '物流类型',
    field: 'logisticsType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'wms_logistics_type',
    },
    colProps: { span: 8 },
  },
  {
    label: '物流公司编码',
    field: 'logisticsCompanyCode',
    component:'Input',
    show:false
  },
  {
    label: '物流公司',
    field: 'logisticsCompany',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return{
        setFieldsValue:setFieldsValue,
        code:"wms_logistics_company",
        fieldConfig: [
              { source: 'id', target: 'logisticsCompanyId' },
              { source: 'company_code', target: 'logisticsCompanyCode' },
             { source: 'company_name', target: 'logisticsCompany' },
              { source:'company_type', target: 'logisticsType'},
        ],
        multi:false
      }
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入物流公司!'},
      ];
    },

    colProps: { span: 8 },
  },
  {
    label: '物流单号',
    field: 'logisticsNo',
    component: 'ScanInput',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入物流单号!'},
      ];
    },
    colProps: { span: 8 },
  },
  {
    label: '物流费用',
    field: 'logisticsCost',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入物流费用!'},
      ];
    },
    colProps: { span: 8 },
  },
  {
    label: '司机电话',
    field: 'driverPhone',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    label: '发货时间',
    field: 'deliveryTime',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
    colProps: { span: 8 },
  },
  {
    label: '收货人',
    field: 'consignee',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    label: '联系电话',
    field: 'consigneePhone',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    label: '收货地址',
    field: 'consigneeAddress',
    component: 'Input',
    colProps: { span: 16 },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
    colProps: { span: 24 },
  },
  {
    label: '客户id',
    field: 'customerId',
    component: 'Input',
    show: false,
  },
  {
    label: '客户名称',
    field: 'customerName',
    component: 'Input',
    show: false,
  },
  {
    label: '物流公司id',
    field: 'logisticsCompanyId',
    component: 'Input',
    show: false,
  },
  {
    label: '来源订单id',
    field: 'sourceOrderId',
    component: 'Input',
    show: false,
  },
  {
    label: '来源订单号',
    field: 'sourceOrderNo',
    component: 'Input',
    show: false,
  },
];

// ==================== 高级查询 ====================
export const superQuerySchema = {
  deliveryNo: { title: '发货单号', order: 0, view: 'text', type: 'string' },
  sourceOrderNo: { title: '来源订单号', order: 1, view: 'text', type: 'string' },
  customerName: { title: '客户名称', order: 2, view: 'text', type: 'string' },
  logisticsNo: { title: '物流单号', order: 3, view: 'text', type: 'string' },
  logisticsCompany: { title: '物流公司', order: 4, view: 'text', type: 'string' },
  deliveryTime: { title: '发货时间', order: 5, view: 'datetime', type: 'string' },
  status: { title: '状态', order: 6, view: 'list', type: 'string', dictCode: 'wms_delivery_status' },
  stockOutNo: { title: '出库单号', order: 7, view: 'text', type: 'string' },
  deliverBy: { title: '发货人', order: 8, view: 'text', type: 'string' },
};

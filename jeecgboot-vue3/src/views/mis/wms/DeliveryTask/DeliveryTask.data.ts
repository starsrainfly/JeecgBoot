import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { h } from 'vue';

// ==================== 待发货任务列 ====================
export const taskColumns: BasicColumn[] = [
  {
    title: '订单号',
    align: 'center',
    dataIndex: 'orderNo',
    width: 140,
  },
  {
    title: '客户名称',
    align: 'center',
    dataIndex: 'customerName',
    width: 150,
  },
  {
    title: '业务员',
    align: 'center',
    dataIndex: 'salesmanName',
    width: 100,
  },
  {
    title: '下单日期',
    align: 'center',
    dataIndex: 'orderDate',
    width: 110,
  },
  {
    title: '要求交期',
    align: 'center',
    dataIndex: 'deliveryDate',
    width: 110,
    slots: { customRender: 'deliveryDate' },
  },
  {
    title: '订单总数量',
    align: 'center',
    dataIndex: 'totalQty',
    width: 100,
  },
  {
    title: '已发数量',
    align: 'center',
    dataIndex: 'deliveredQty',
    width: 100,
  },
  {
    title: '剩余数量',
    align: 'center',
    dataIndex: 'remainingQty',
    width: 100,
    customRender: ({ record }) => {
      const remain = Number(record.totalQty || 0) - Number(record.deliveredQty || 0);
      return remain;
    },
  },
  {
    title: '发货状态',
    align: 'center',
    dataIndex: 'deliveryStatus_dictText',
    width: 100,
    customRender: ({ record }) => {
      const status = record.deliveryStatus;
      const colors = { '0': 'default', '1': 'processing', '2': 'success' };
      const texts = { '0': '未开始', '1': '部分发货', '2': '已完成' };
      return h('a-tag', { color: colors[status] || 'default' }, texts[status] || status);
    },
  },
  {
    title:'公司',
    align:'center',
    dataIndex:'companyName',
    width:120
  },
];

// ==================== 搜索表单 ====================
export const taskSearchFormSchema: FormSchema[] = [
  {
    label: '订单号',
    field: 'orderNo',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '客户名称',
    field: 'customerName',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '业务员',
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps: {
      dict: "sys_user where del_flag='0' and status='1',realname,id",
    },
    colProps: { span: 6 },
  },
  {
    label: '下单日期',
    field: 'orderDate',
    component: 'RangePicker',
    colProps: { span: 6 },
  },
  {
    label: '要求交期',
    field: 'deliveryDate',
    component: 'RangePicker',
    colProps: { span: 6 },
  },
  {
    label: '发货状态',
    field: 'deliveryStatus',
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: 'delivery_task_status',
    },
    colProps: { span: 6 },
  },
  {
    label:'公司',
    field:'companyId',
    component:'JSelectMultiple',
    componentProps: {
      dictCode: "sys_depart where del_flag='0' and org_category='1' and org_type='1',depart_name,id",
    },
  },
  {
    label: '隐藏已完成',
    field: 'hideCompleted',
    component: 'JSwitch',
    componentProps: { options: ['1', '0'] },
    colProps: { span: 6 },
  },
  {
    label: '仅看预警',
    field: 'onlyUrgent',
    component: 'JSwitch',
    componentProps: { options: ['1', '0'] },
    colProps: { span: 6 },
  },
];

// ==================== 弹窗内：未发货订单明细列 ====================
export const pendingLineColumns: BasicColumn[] = [
  {
    title: '产品编码',
    align: 'center',
    dataIndex: 'productCode',
    width: 120,
  },
  {
    title: '产品名称',
    align: 'center',
    dataIndex: 'productName',
    width: 150,
  },
  {
    title: '规格型号',
    align: 'center',
    dataIndex: 'productSpec',
    width: 120,
  },
  {
    title: '单位',
    align: 'center',
    dataIndex: 'unit',
    width: 80,
  },
  {
    title: '订单数量',
    align: 'center',
    dataIndex: 'orderQty',
    width: 100,
  },
  {
    title: '已发数量',
    align: 'center',
    dataIndex: 'deliveredQty',
    width: 100,
  },
  {
    title: '剩余数量',
    align: 'center',
    dataIndex: 'remainingQty',
    width: 100,
    customRender: ({ record }) => {
      return Number(record.orderQty || 0) - Number(record.deliveredQty || 0);
    },
  },
  {
    title: '本次扫码',
    align: 'center',
    dataIndex: 'currentScanQty',
    width: 100,
    customRender: ({ record }) => {
      // 从deliveryItems计算本次该明细的扫码数量
      return record.currentScanQty || 0;
    },
  },
];

// ==================== 弹窗内：FIFO库存列 ====================
export const stockColumns: BasicColumn[] = [
  {
    title: '产品编码',
    align: 'center',
    dataIndex: 'goodsCode',
    width: 120,
  },
  {
    title: '产品名称',
    align: 'center',
    dataIndex: 'goodsName',
    width: 150,
  },
  {
    title: '批次号',
    align: 'center',
    dataIndex: 'batchNo',
    width: 140,
  },
  {
    title: '生产日期',
    align: 'center',
    dataIndex: 'productionDate',
    width: 110,
  },
  {
    title: '有效期至',
    align: 'center',
    dataIndex: 'expiryDate',
    width: 110,
  },
  {
    title: '库存数量',
    align: 'center',
    dataIndex: 'quantity',
    width: 100,
  },
  {
    title: '锁定数量',
    align: 'center',
    dataIndex: 'lockedQty',
    width: 100,
  },
  {
    title: '可用数量',
    align: 'center',
    dataIndex: 'availableQty',
    width: 100,
    customRender: ({ record }) => {
      return Number(record.quantity || 0) - Number(record.lockedQty || 0);
    },
  },
  {
    title: '仓库',
    align: 'center',
    dataIndex: 'warehouseId_dictText',
    width: 120,
  },
  {
    title: '库位',
    align: 'center',
    dataIndex: 'locationId_dictText',
    width: 120,
  },
  {
    title:'单位',
    align:'center',
    dataIndex:'unit',
    width:80
  },
  {
    title: '操作',
    align: 'center',
    key: 'action',
    width: 80,
    fixed: 'right',
    slots: { customRender: 'stockAction' },
  },
];

// ==================== 弹窗内：本次发货明细列 ====================
export const deliveryItemColumns: BasicColumn[] = [
  {
    title: '产品编码',
    align: 'center',
    dataIndex: 'goodsCode',
    width: 120,
  },
  {
    title: '产品名称',
    align: 'center',
    dataIndex: 'goodsName',
    width: 150,
  },
  {
    title: '批次号',
    align: 'center',
    dataIndex: 'productionBatchNo',
    width: 140,
  },
  {
    title: '生产日期',
    align: 'center',
    dataIndex: 'productionDate',
    width: 110,
  },
  {
    title: '有效期至',
    align: 'center',
    dataIndex: 'expiryDate',
    width: 110,
  },
  {
    title: '仓库',
    align: 'center',
    dataIndex: 'warehouseName',
    width: 120,
  },
  {
    title: '发货数量',
    align: 'center',
    dataIndex: 'actualQty',
    width: 100,
    edit: true,
    editComponent: 'InputNumber',
  },
  {
    title: '单价',
    align: 'center',
    dataIndex: 'unitPrice',
    width: 100,
  },
  {
    title: '扫码内容',
    align: 'center',
    dataIndex: 'scanCode',
    width: 150,
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
    dynamicRules: () => [{ required: true, message: '请选择物流类型' }],
    colProps: { xs: 24, sm: 12, md: 8 },
  },
  {
    label: '物流公司',
    field: 'logisticsCompany',
    component: 'JPopup',
    componentProps: ({ formActionType }) => ({
      code: 'wms_logistics_company',
      fieldConfig: [
        { source: 'id', target: 'logisticsCompanyId' },
        { source: 'company_code', target: 'logisticsCompanyCode' },
        { source: 'company_name', target: 'logisticsCompany' },
        { source: 'company_type', target: 'logisticsType' },
      ],
      multi: false,
    }),
    dynamicRules: () => [{ required: true, message: '请选择物流公司' }],
    colProps: { xs: 24, sm: 12, md: 8 },
  },
  {
    label: '物流费用',
    field: 'logisticsCost',
    component: 'InputNumber',
    dynamicRules: () => [{ required: true, message: '请输入物流费用' }],
    colProps: { xs: 24, sm: 12, md: 8 },
  },
  {
    label: '司机电话',
    field: 'driverPhone',
    component: 'Input',
    colProps: { xs: 24, sm: 12, md: 8 },
  },
  {
    label: '发货时间',
    field: 'deliveryTime',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
    dynamicRules: () => [{ required: true, message: '请选择发货时间' }],
    colProps: { xs: 24, sm: 12, md: 8 },
  },
  {
    label: '收货人',
    field: 'consignee',
    component: 'Input',
    dynamicRules: () => [{ required: true, message: '请输入收货人' }],
    colProps: { xs: 24, sm: 12, md: 8 },
  },
  {
    label: '联系电话',
    field: 'consigneePhone',
    component: 'Input',
    dynamicRules: () => [{ required: true, message: '请输入联系电话' }],
    colProps: { xs: 24, sm: 12, md: 8 },
  },
  {
    label: '收货地址',
    field: 'consigneeAddress',
    component: 'Input',
    dynamicRules: () => [{ required: true, message: '请输入收货地址' }],
    colProps: { xs: 24, sm: 12, md: 8 },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    colProps: { xs: 24, sm: 24, md: 24 },
  },
  // 隐藏字段
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
    label: '物流公司编码',
    field: 'logisticsCompanyCode',
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
  orderNo: { title: '订单号', order: 0, view: 'text', type: 'string' },
  customerName: { title: '客户名称', order: 1, view: 'text', type: 'string' },
  salesmanName: { title: '业务员', order: 2, view: 'text', type: 'string' },
  orderDate: { title: '下单日期', order: 3, view: 'datetime', type: 'string' },
  deliveryDate: { title: '要求交期', order: 4, view: 'datetime', type: 'string' },
  deliveryStatus: { title: '发货状态', order: 5, view: 'list', type: 'string', dictCode: 'delivery_task_status' },
};

import { BasicColumn, FormSchema } from '/@/components/Table';

// 表格列
export const stockOutDetailQueryColumns: BasicColumn[] = [
  {
    title: '出库单号',
    dataIndex: 'stockOutNo',
    width: 160,
    fixed: 'left',
  },
  {
    title: '出库类型',
    dataIndex: 'stockOutType_dictText',
    width: 100,
  },
  {
    title: '客户名称',
    dataIndex: 'customerName',
    width: 150,
  },
  {
    title: '仓库',
    dataIndex: 'warehouseName',
    width: 120,
  },
  {
    title: '领用人',
    dataIndex: 'requesterName',
    width: 100,
  },
  {
    title: '物品编码',
    dataIndex: 'goodsCode',
    width: 120,
  },
  {
    title: '物品名称',
    dataIndex: 'goodsName',
    width: 180,
  },
  {
    title: '规格型号',
    dataIndex: 'goodsSpec',
    width: 120,
  },
  {
    title: '单位',
    dataIndex: 'unit',
    width: 60,
  },
  {
    title: '批次号',
    dataIndex: 'batchNo',
    width: 140,
  },
  {
    title: '生产批次号',
    dataIndex: 'productionBatchNo',
    width: 140,
  },
  {
    title: '申请数量',
    dataIndex: 'applyQty',
    width: 100,
    align: 'right',
  },
  {
    title: '实发数量',
    dataIndex: 'actualQty',
    width: 100,
    align: 'right',
  },
  {
    title: '成本单价',
    dataIndex: 'costPrice',
    width: 100,
    align: 'right',
  },
  {
    title: '成本金额',
    dataIndex: 'costTotal',
    width: 100,
    align: 'right',
  },
  {
    title: '销售单价',
    dataIndex: 'salesPrice',
    width: 100,
    align: 'right',
  },
  {
    title: '销售金额',
    dataIndex: 'salesTotal',
    width: 100,
    align: 'right',
  },
  {
    title: '有效期至',
    dataIndex: 'expiryDate',
    width: 110,
  },
  {
    title: '生产日期',
    dataIndex: 'productionDate',
    width: 110,
  },
  {
    title: '是否超量',
    dataIndex: 'overFlag',
    width: 90,
    customRender: ({ text }) => text === '1' ? '是' : text === '0' ? '否' : text,
  },
  {
    title: '超量数量',
    dataIndex: 'overQty',
    width: 100,
    align: 'right',
  },
  {
    title: '业务状态',
    dataIndex: 'status_dictText',
    width: 100,
  },
  {
    title: '审核状态',
    dataIndex: 'approveStatus',
    width: 100,
  },
  {
    title: '出库时间',
    dataIndex: 'stockOutTime',
    width: 160,
  },
  {
    title: '需求日期',
    dataIndex: 'requiredDate',
    width: 110,
  },
  {
    title: '收货人',
    dataIndex: 'consignee',
    width: 100,
  },
  {
    title: '收货电话',
    dataIndex: 'consigneePhone',
    width: 120,
  },
  {
    title: '收货地址',
    dataIndex: 'deliverAddress',
    width: 200,
    ellipsis: true,
  },
  {
    title: '来源单据',
    dataIndex: 'sourceOrderCode',
    width: 150,
  },
  {
    title: '源类型',
    dataIndex: 'sourceType',
    width: 100,
  },
  {
    title: '主表备注',
    dataIndex: 'masterRemark',
    width: 200,
    ellipsis: true,
  },
  {
    title: '明细备注',
    dataIndex: 'remark',
    width: 200,
    ellipsis: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
  },
];

// 查询表单
export const detailQuerySearchSchema: FormSchema[] = [
  {
    field: 'stockOutNo',
    label: '出库单号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'goodsName',
    label: '物品名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'goodsCode',
    label: '物品编码',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'batchNo',
    label: '批次号',
    component: 'Input',
    colProps: { span: 6 },
  },

  {
    label: "出库类型",
    field: "stockOutType",
    component: 'JSelectMultiple',
    componentProps:{
      dictCode:"wms_stock_out_type"
    },
    //colProps: {span: 6},
  },
  {
    label: "客户名称",
    field: "customerName",
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return{
        setFieldsValue:setFieldsValue,
        code:"scm_customer_no_param",
        fieldConfig: [
          { source: 'id', target: 'customerId' },
          { source: 'customer_name', target: 'customerName' },
        ],
        multi:true
      }
    },

    //colProps: {span: 6},
  },
  {
    label: "领用人",
    field: "requesterUserId",
    component: 'JSelectMultiple',
    componentProps:{
      dictCode:"sys_user where del_flag='0' and status='1',realname,id"
    },
    //colProps: {span: 6},
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
    },

  },
  {
    label: "审核状态",
    field: "approveStatus",
    component: 'JSelectMultiple',
    componentProps:{
      dictCode:"approval_status"
    },
    //colProps: {span: 6},
  },
  {
    field: 'status',
    label: '业务状态',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'wms_stock_out_status',
      placeholder: '请选择',
    },
    colProps: { span: 6 },
  },

  {
    field: 'goodsType',
    label: '物品类型',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'wms_item_type',
      placeholder: '请选择',
    },
    colProps: { span: 6 },
  },
  {
    field: 'overFlag',
    label: '是否超量',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'yn',
      placeholder: '请选择',
    },
    colProps: { span: 6 },
  },
  {
    field: 'stockOutTime',
    label: '出库时间',
    component: 'RangePicker',
    componentProps: {
      showTime: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
    colProps: { span: 8 },
  },
  {
    field: 'requiredDate',
    label: '需求日期',
    component: 'RangePicker',
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
    },
    colProps: { span: 8 },
  },
];

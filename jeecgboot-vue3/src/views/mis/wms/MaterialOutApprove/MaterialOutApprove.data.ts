import {BasicColumn, FormSchema} from '/@/components/Table';
import {JVxeColumn, JVxeTypes} from '/@/components/jeecg/JVxeTable/types'
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '出库单号',
    align:"center",
    dataIndex: 'stockOutNo'
   },
   {
    title: '出库类型',
    align:"center",
    dataIndex: 'stockOutType_dictText'
   },
   // {
   //  title: '销售订单',
   //  align:"center",
   //  dataIndex: 'sourceOrderId'
   // },
   // {
   //  title: '客户名称',
   //  align:"center",
   //  dataIndex: 'customerName'
   // },
   // {
   //  title: '领用人id',
   //  align:"center",
   //  dataIndex: 'requesterUserId_dictText'
   // },
   {
    title: '领用人',
    align:"center",
    dataIndex: 'requesterName'
   },
   {
    title: '仓库',
    align:"center",
    dataIndex: 'warehouseName'
   },
   // {
   //  title: '操作人id',
   //  align:"center",
   //  dataIndex: 'operatorUserId'
   // },
   {
    title: '操作人',
    align:"center",
    dataIndex: 'operatorName'
   },
   {
    title: '业务状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '是否产品',
    align:"center",
    dataIndex: 'isProduct_dictText'
   },
   // {
   //  title: '审批人id',
   //  align:"center",
   //  dataIndex: 'approveId'
   // },
   {
    title: '审批人',
    align:"center",
    dataIndex: 'approveName' //_dictText
   },
   {
    title: '审批时间',
    align:"center",
    dataIndex: 'approveTime'
   },
   {
    title: '审核备注',
    align:"center",
    dataIndex: 'approveRemark'
   },
   {
    title: '审核状态',
    align:"center",
    dataIndex: 'approveStatus_dictText'
   },
   {
    title: '申请时间',
    align:"center",
    dataIndex: 'applyTime'
   },
   {
    title: '出库时间',
    align:"center",
    dataIndex: 'stockOutTime'
   },
   // {
   //  title: '收货地址',
   //  align:"center",
   //  dataIndex: 'deliverAddress'
   // },
   // {
   //  title: '收货人',
   //  align:"center",
   //  dataIndex: 'consignee'
   // },
   // {
   //  title: '收货人电话',
   //  align:"center",
   //  dataIndex: 'consigneePhone'
   // },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "出库单号",
      field: "stockOutNo",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "出库类型",
      field: "stockOutType",
      component: 'JSelectMultiple',
      defaultValue:'PRODUCTION',
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
            code:"scm_customer",
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
    defaultValue:'0'
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '出库单号',
    field: 'stockOutNo',
    component: 'Input',
    show:false
  },
  {
    label: '出库类型',
    field: 'stockOutType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_stock_out_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入出库类型!'},
          ];
     },
  },
  {
    label: '来源单据id',
    field: 'sourceOrderId',
    component: 'Input',
    show:false
  },
  {
    label: '来源单据编号',
    field: 'sourceOrderCode',
    component: 'Input',
    show:false
  },
  // {
  //   label: '客户id',
  //   field: 'customerId',
  //   component: 'Input',
  //   dynamicRules: ({model,schema}) => {
  //         return [
  //                { required: true, message: '请输入客户id!'},
  //         ];
  //    },
  // },
  // {
  //   label: '客户名称',
  //   field: 'customerName',
  //   component: 'JPopup',
  //   componentProps: ({ formActionType }) => {
  //       const {setFieldsValue} = formActionType;
  //       return{
  //           setFieldsValue:setFieldsValue,
  //           code:"scm_customer",
  //           fieldConfig: [
  //               { source: 'id', target: 'customerId' },
  //               { source: 'customer_name', target: 'customerName' },
  //           ],
  //           multi:true
  //       }
  //   },
  //
  //   dynamicRules: ({model,schema}) => {
  //         return [
  //                { required: true, message: '请输入客户名称!'},
  //         ];
  //    },
  // },
  {
    label: '领用人',
    field: 'requesterUserId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_user where del_flag='0' and status='1',realname,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入领用人id!'},
          ];
     },
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入仓库id!'},
          ];
     },
  },
  {
    label: '仓库',
    field: 'warehouseName',
    component: 'Input',
    show:false
  },
  // {
  //   label: '是否产品',
  //   field: 'isProduct',
  //   component: 'JDictSelectTag',
  //   componentProps:{
  //       dictCode:"yn"
  //    },
  // },
  // {
  //   label: '审批人id',
  //   field: 'approveId',
  //   component: 'Input',
  //   show:false
  // },
  {
    label: '审核备注',
    field: 'approveRemark',
    component: 'Input',
    show:true
  },
  {
    label: '审核状态',
    field: 'approveStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"approval_status"
     },
    defaultValue:'',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入审核状态!'},
      ];
    },
  },
  // {
  //   label: '收货地址',
  //   field: 'deliverAddress',
  //   component: 'Input',
  // },
  // {
  //   label: '收货人',
  //   field: 'consignee',
  //   component: 'Input',
  // },
  // {
  //   label: '收货人电话',
  //   field: 'consigneePhone',
  //   component: 'Input',
  // },
  {
    label:'是否产品',
    field:'isProduct',
    component:'JDictSelectTag',
    componentProps:{
      dictCode: "yn",
      disabled:true,
    },
    defaultValue:'0'
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];
//子表单数据
//子表表格配置
export const stockOutDetailColumns: JVxeColumn[] = [
    // {
    //   title: '出库单号',
    //   key: 'stockOutNo',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '出库主表id',
    //   key: 'stockOutId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    {
      title: '物品id',
      key: 'goodsId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      visible:false
    },
    {
      title: '类型',
      key: 'goodsType',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mdm_material_type",
      width:"100px",
      placeholder: '请输入${title}',
      defaultValue:'',

    },
    {
      title: '编码',
      key: 'goodsCode',
      type: JVxeTypes.popup,
      popupCode:"mdm_material_select",
      fieldConfig: [
        { source: 'id', target: 'goodsId' },
        { source: 'material_code', target: 'goodsCode' },
        { source: 'material_name', target: 'goodsName' },
        { source: 'material_spec', target: 'goodsSpec' },
        { source: 'material_type', target: 'goodsType' },
      ],

      width:"150px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '名称',
      key: 'goodsName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '规格型号',
      key: 'goodsSpec',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '单位',
      key: 'unit',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mis_unit where del_flag='0',unit,unit",
      width:"80px",
      placeholder: '请输入${title}',
      defaultValue:"kg",
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '申请数量',
      key: 'applyQty',
      type: JVxeTypes.inputNumber,
      width:"150px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '实发数量',
      key: 'actualQty',
      type: JVxeTypes.inputNumber,
      width:"150px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title:'是否超量',
      key:'overFlag',
      type:JVxeTypes.select,
      width:"100px",
      options:[],
      dictCode:"yn",
      placeholder: '请输入${title}',
      defaultValue:"0",
    },
    {
      title:'超量数量',
      key:'overQty',
      type:JVxeTypes.inputNumber,
      width:"100px",
      defaultValue: 0,
    },
    {
      title: '批次号',
      key: 'batchNo',
      type: JVxeTypes.input,
      width:"100px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '序列号',
      key: 'serialNo',
      type: JVxeTypes.input,
      width:"100px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    // {
    //   title: '有效期至',
    //   key: 'expiryDate',
    //   type: JVxeTypes.date,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '成本单价',
    //   key: 'costPrice',
    //   type: JVxeTypes.inputNumber,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '成本金额',
    //   key: 'costTotal',
    //   type: JVxeTypes.inputNumber,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    {
      title:'销售单价',
      key:'salesPrice',
      type: JVxeTypes.inputNumber,
      width:"100px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title:'销售金额',
      key:'salesTotal',
      type: JVxeTypes.inputNumber,
      width:"100px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    // {
    //   title: '来源单据明细id',
    //   key: 'sourceDetailId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '需求表id',
    //   key: 'requirementId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '生产批次id',
    //   key: 'productionBatchId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '入库明细id',
    //   key: 'inDetailId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '库存记录id',
    //   key: 'stockId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
  ]


// 高级查询数据
export const superQuerySchema = {
  stockOutNo: {title: '出库单号',order: 0,view: 'text', type: 'string',},
  stockOutType: {title: '出库类型',order: 1,view: 'list', type: 'string',dictCode: 'wms_stock_out_type',},
  sourceOrderId: {title: '销售订单',order: 2,view: 'text', type: 'string',},
  customerName: {title: '客户名称',order: 5,view: 'popup', type: 'string',code: 'scm_customer', orgFields: 'customer_name', destFields: 'customerName', popupMulti: false,},
  requesterUserId: {title: '领用人id',order: 6,view: 'list', type: 'string',dictTable: "sys_user where del_flag='0' and status='1'", dictCode: 'id', dictText: 'realname',},
  requesterName: {title: '领用人',order: 7,view: 'text', type: 'string',},
  warehouseName: {title: '仓库',order: 9,view: 'text', type: 'string',},
  operatorUserId: {title: '操作人id',order: 10,view: 'text', type: 'string',},
  operatorName: {title: '操作人',order: 11,view: 'text', type: 'string',},
  status: {title: '业务状态',order: 12,view: 'list', type: 'string',dictCode: 'wms_stock_out_status',},
  isProduct: {title: '是否产品',order: 13,view: 'list', type: 'string',dictCode: 'yn',},
  approveId: {title: '审批人id',order: 14,view: 'text', type: 'string',},
  approveName: {title: '审批人',order: 15,view: 'list', type: 'string',dictCode: '',},
  approveTime: {title: '审批时间',order: 16,view: 'datetime', type: 'string',},
  approveRemark: {title: '审核备注',order: 17,view: 'text', type: 'string',},
  approveStatus: {title: '审核状态',order: 18,view: 'list', type: 'string',dictCode: 'approval_status',},
  applyTime: {title: '申请时间',order: 19,view: 'datetime', type: 'string',},
  stockOutTime: {title: '出库时间',order: 20,view: 'datetime', type: 'string',},
  deliverAddress: {title: '收货地址',order: 21,view: 'text', type: 'string',},
  consignee: {title: '收货人',order: 22,view: 'text', type: 'string',},
  consigneePhone: {title: '收货人电话',order: 23,view: 'text', type: 'string',},
  remark: {title: '备注',order: 24,view: 'text', type: 'string',},
  //子表高级查询
  stockOutDetail: {
    title: '出库明细表',
    view: 'table',
    fields: {
        stockOutNo: {title: '出库单号',order: 0,view: 'text', type: 'string',},
        stockOutId: {title: '出库主表id',order: 1,view: 'text', type: 'string',},
        goodsId: {title: '物品id',order: 2,view: 'text', type: 'string',},
        goodsType: {title: '类型',order: 3,view: 'list', type: 'string',dictCode: 'mdm_material_type',},
        goodsCode: {title: '编码',order: 4,view: 'popup', type: 'string',code: 'mdm_material_select', orgFields: 'material_code', destFields: 'goodsCode', popupMulti: false,},
        goodsName: {title: '名称',order: 5,view: 'text', type: 'string',},
        goodsSpec: {title: '规格型号',order: 6,view: 'text', type: 'string',},
        unit: {title: '单位',order: 7,view: 'text', type: 'string',},
        applyQty: {title: '申请数量',order: 8,view: 'number', type: 'number',},
        actualQty: {title: '实发数量',order: 9,view: 'number', type: 'number',},
        batchNo: {title: '批次号',order: 10,view: 'text', type: 'string',},
        serialNo: {title: '序列号',order: 11,view: 'text', type: 'string',},
        expiryDate: {title: '有效期至',order: 12,view: 'date', type: 'string',},
        costPrice: {title: '成本单价',order: 13,view: 'number', type: 'number',},
        costTotal: {title: '成本金额',order: 14,view: 'number', type: 'number',},
        salesPrice:{title: '销售单价',order: 13,view: 'number', type: 'number',},
        salesTotal:{title: '销售金额',order: 13,view: 'number', type: 'number',},
        sourceDetailId: {title: '来源单据明细id',order: 15,view: 'text', type: 'string',},
        requirementId: {title: '需求表id',order: 17,view: 'text', type: 'string',},
        productionBatchId: {title: '生产批次id',order: 18,view: 'text', type: 'string',},
        inDetailId: {title: '入库明细id',order: 19,view: 'text', type: 'string',},
        stockId: {title: '库存记录id',order: 20,view: 'text', type: 'string',},
    }
  },
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
// 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

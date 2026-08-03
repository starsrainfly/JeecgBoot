import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
import {useForm} from "@/components/Form";
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '入库单号',
    align:"center",
    dataIndex: 'stockInNo'
   },
   {
    title: '入库类型',
    align:"center",
    dataIndex: 'stockInType_dictText'
   },

   {
     title: '仓库名称',
     align:"center",
     dataIndex: 'warehouseName'
   },
   // {
   //  title: '供应商',
   //  align:"center",
   //  dataIndex: 'supplierName'
   // },
   // {
   //  title: '采购员',
   //  align:"center",
   //  dataIndex: 'purchaserName'
   // },
   {
    title: '制单人',
    align:"center",
    dataIndex: 'operatorName'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   // {
   //  title: '审批人id',
   //  align:"center",
   //  dataIndex: 'auditId'
   // },
  {
    title: '审批人',
    align:"center",
    dataIndex: 'approveName'
  },
  {
    title: '审批时间',
    align:"center",
    dataIndex: 'approveTime'
  },
   {
    title: '申请时间',
    align:"center",
    dataIndex: 'applyTime'
   },
   {
    title: '审核状态',
    align:"center",
    dataIndex: 'approveStatus_dictText',
   },
   {
    title: '审核备注',
    align:"center",
    dataIndex: 'approveRemark'
   },
   {
    title: '入库时间',
    align:"center",
    dataIndex: 'stockInTime'
   },
   // {
   //  title: '来源订单类型',
   //  align:"center",
   //  dataIndex: 'sourceOrderType'
   // },
   // {
   //  title: '来源单据id',
   //  align:"center",
   //  dataIndex: 'sourceOrderId'
   // },
   // {
   //  title: '来源单据号',
   //  align:"center",
   //  dataIndex: 'sourceOrderNo'
   // },
   // {
   //   title: '客户',
   //   align:"center",
   //   dataIndex: 'customerName'
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
      label: "入库类型",
      field: "stockInType",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"wms_stock_in_type"
      },
    defaultValue:'INVENTORY'
      //colProps: {span: 6},
 	},
	// {
  //     label: "供应商",
  //     field: "supplierName",
  //   component: 'JPopup',
  //   componentProps: ({ formActionType }) => {
  //       const {setFieldsValue} = formActionType;
  //       return{
  //           setFieldsValue:setFieldsValue,
  //           code:"scm_supplier",
  //           fieldConfig: [
  //               { source: 'id', target: 'supplierId' },
  //               { source: 'supplier_name', target: 'supplierName' },
  //           ],
  //           multi:true
  //       }
  //   },
  //
  //     //colProps: {span: 6},
 	// },
	// {
  //     label: "客户",
  //     field: "customerName",
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
  //     //colProps: {span: 6},
 	// },
	{
      label: "仓库",
      field: "warehouseId",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mis_warehouse,name,id"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '入库单号',
    field: 'stockInNo',
    component: 'Input',
    dynamicDisabled:true //禁用
  },
  {
    label: '入库类型',
    field: 'stockInType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_stock_in_type"
     },
    defaultValue:'INVENTORY',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请选择入库类型!'},
      ];
    },
  },
  {
    label: '供应商id',
    field: 'supplierId',
    component: 'Input',
    show:false //隐藏
  },
  {
    label: '供应商',
    field: 'supplierName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_supplier",
            fieldConfig: [
                { source: 'id', target: 'supplierId' },
                { source: 'supplier_name', target: 'supplierName' },
            ],
            multi:false
        }
    },
    // dynamicRules: ({model,schema}) => {
    //   return [
    //     { required: true, message: '请选择供应商!'},
    //   ];
    // },
    show:false
  },
  // {
  //   label: '客户id',
  //   field: 'customerId',
  //   component: 'Input',
  //   show:false //隐藏
  // },
  // {
  //   label: '客户',
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
  //           multi:false
  //       }
  //   },
  // },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse,name,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入仓库!'},
          ];
     },
  },
  // {
  //   label: '采购员',
  //   field: 'purchaserId',
  //   component: 'JDictSelectTag',
  //   componentProps:{
  //       dictCode:"sys_user,realname,id",
  //    },
  // },
  {
    label: '来源订单类型',
    field: 'sourceOrderType',
    component: 'Input',
    show:false
  },
  {
    label: '来源单据id',
    field: 'sourceOrderId',
    component: 'Input',
    show:false
  },
  {
    label: '生产批次号',
    field: 'sourceOrderNo',
    component: 'JPopup',
    show: ({ model }) => model.stockInType === 'PRODUCTION',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return {
        setFieldsValue: setFieldsValue,
        code: "mes_production_batch_select",
        fieldConfig: [
          {source: 'id', target: 'sourceOrderId'},
          {source: 'PRODUCT', target: 'sourceOrderType'},
          { source: 'id', target: 'sourceOrderId' },
          { source: 'batch_no', target: 'sourceOrderNo' }, // 批次号显示用
          { source: 'product_id', target: 'productId' },   // 隐藏字段
          { source: 'product_code', target: 'productCode' }, // 隐藏字段
          { source: 'product_name', target: 'productName' }, // 隐藏字段
          { source: 'product_spec', target: 'productSpec' }, // 隐藏字段
          {source: 'product_color', target:'productColor'},
          { source: 'actual_qty', target: 'batchActualQty' }, // 实际产量（校验用）
          { source: 'remain_qty', target: 'batchRemainQty' }, // 剩余可入量
          { source: 'planned_qty', target: 'plannedQty' },
          { source: 'recipe_id', target: 'recipeId' },
          {source: 'in_stock_qty', target: 'batchInStockQty'},    // 已入库数量（新增）
          //{ source: 'unit', target: 'unit' },
        ],
        multi: false,

      }
    },
  },
  // 隐藏字段：产品信息（用于子表自动填充）
  {
    label: 'productId',
    field: 'productId',
    component: 'Input',
    show: false
  },
  {
    label: 'productCode',
    field: 'productCode',
    component: 'Input',
    show: false
  },
  {
    label: 'productName',
    field: 'productName',
    component: 'Input',
    show: false
  },
  {
    label: 'productSpec',
    field: 'productSpec',
    component: 'Input',
    show: false
  },
  {
    label: 'productColor',
    field: 'productColor',
    component: 'Input',
    show: false
  },
  {
    label: 'batchActualQty',
    field: 'batchActualQty',
    component: 'InputNumber',
    show: false
  },
  {
    label: 'batchRemainQty',
    field: 'batchRemainQty',
    component: 'InputNumber',
    show: false
  },
  {
    label: 'batchInStockQty',
    field: 'batchInStockQty',
    component: 'InputNumber',
    show: false
  },
  {
    label: 'plannedQty',
    field: 'plannedQty',
    component: 'InputNumber',
    show: false
  },
  {
    label: 'unit',
    field: 'unit',
    component: 'Input',
    defaultValue:'kg',
    show: false
  },
  {
    label: 'recipeId',
    field: 'recipeId',
    component: 'Input',
    show: false
  },
  {
    label:'是否产品',
    field:'isProduct',
    component:'JDictSelectTag',
    componentProps:{
      dictCode: "yn",
      disabled:true,
    },
    defaultValue:'1',
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
export const stockInDetailColumns: JVxeColumn[] = [
    // {
    //   title: '入库单号',
    //   key: 'stockInNo',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    {
      title: '类型（物料、产品）',
      key: 'goodsType',
      type: JVxeTypes.select,
      options:[],
      dictCode:"wms_item_type",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:"PRODUCT",
      visible:false
    },
    {
      title: '物料id',
      key: 'goodsId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
      visible:false
    },
    {
      title: '编码',
      key: 'goodsCode',
      type: JVxeTypes.slot,
      slotName: 'goodsCode',
      // type: JVxeTypes.popup,
      // popupCode:"mdm_product_select",
      // fieldConfig: [
      //   { source: 'id', target: 'goodsId' },
      //   { source: 'product_code', target: 'goodsCode' },
      //   { source: 'product_name', target: 'goodsName' },
      //   { source: 'product_spec', target: 'goodsSpec' },
      //   { source: 'product_color', target: 'goodsColor'},
      //  // { source: 'material_type', target: 'goodsType' },
      // ],

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '产品名称',
      key: 'goodsName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
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
    title: '产品颜色',
    key: 'goodsColor',
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
      dictCode:"mis_unit where del_flag='0' and status='1',unit,unit",
      width:"200px",
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
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '实收数量',
      key: 'actualQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      disabled: true,
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    // {
    //   title: '币种',
    //   key: 'currency',
    //   type: JVxeTypes.select,
    //   options:[],
    //   dictCode:"mis_currency where del_flag='0' and status='1',currency_name,currency_code",
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'CNY',
    //     validateRules: [
    //       { required: true, message: '${title}不能为空' },
    //     ],
    //   visible:false,
    // },
    // {
    //   title: '汇率',
    //   key: 'exchangeRate',
    //   type: JVxeTypes.inputNumber,
    //   options:[],
    //   dictCode:"",
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue: 1,
    //     validateRules: [
    //       { required: true, message: '${title}不能为空' },
    //     ],
    //   visible:false,
    // },
    {
      title: '成本单价',
      key: 'unitPrice',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
     // visible:false,
    },
    {
      title: '成本金额',
      key: 'totalAmount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
      //visible:false,
    },
    {
      title:'生产日期',
      key:'productionDate',
      type:JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title:'保质天数',
      key:'shelfLife',
      type:JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title:'失效日期',
      key:'expiryDate',
      type:JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '批次号',
      key: 'batchNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      // validateRules: [
      //   { required: true, message: '${title}不能为空' },
      // ],
    },
    {
      title: '序列号',
      key: 'serialNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '备注',
      key: 'remark',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '质检状态',
      key: 'qcStatus',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mes_qc_status",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  // 隐藏字段：关联生产批次
  {
    title: '生产批次ID',
    key: 'productionBatchId',
    type: JVxeTypes.input,
    width:"200px",
    visible: false,
  },
  // 隐藏字段：用于校验
  {
    title: '批次已入库量',
    key: '_batchInStockQty',
    type: JVxeTypes.inputNumber,
    width: "120px",
    disabled: true,  // 只读
    //visible: false,  // 隐藏，或设为 true 显示
  },
  {
    title: '批次剩余量',
    key: '_batchRemainQty',
    type: JVxeTypes.inputNumber,
    width: "120px",
    disabled: true,
   // visible: false,
  },
  ]


// 高级查询数据
export const superQuerySchema = {
  stockInNo: {title: '入库单号',order: 0,view: 'text', type: 'string',},
  stockInType: {title: '入库类型',order: 1,view: 'list', type: 'string',dictCode: 'wms_stock_in_type',},
  supplierName: {title: '供应商',order: 2,view: 'popup', type: 'string',code: 'scm_supplier', orgFields: 'supplier_name', destFields: 'supplierName', popupMulti: false,},
  customerName: {title: '客户',order: 3,view: 'popup', type: 'string',code: 'scm_customer', orgFields: 'customer_name', destFields: 'customerName', popupMulti: false,},
  warehouseName: {title: '仓库名称',order: 5,view: 'list', type: 'string',dictCode: '',},
  purchaserName: {title: '采购员',order: 7,view: 'list', type: 'string',dictCode: '',},
  operatorName: {title: '制单人',order: 8,view: 'text', type: 'string',},
  status: {title: '状态',order: 9,view: 'list', type: 'string',dictCode: 'wms_stock_in_status',},
  //auditId: {title: '审批人id',order: 10,view: 'text', type: 'string',},
  auditName: {title: '审批人',order: 11,view: 'text', type: 'string',},
  auditTime: {title: '审批时间',order: 12,view: 'datetime', type: 'string',},
  applyTime: {title: '申请时间',order: 13,view: 'datetime', type: 'string',},
  auditStatus: {title: '审核状态',order: 14,view: 'list', type: 'string',dictCode:'approval_status'},
  auditRemark: {title: '审核备注',order: 15,view: 'text', type: 'string',},
  stockInTime: {title: '入库时间',order: 16,view: 'datetime', type: 'string',},
  // sourceOrderType: {title: '来源订单类型',order: 17,view: 'text', type: 'string',},
  // sourceOrderId: {title: '来源单据id',order: 18,view: 'text', type: 'string',},
  // sourceOrderNo: {title: '来源单据号',order: 19,view: 'text', type: 'string',},
  remark: {title: '备注',order: 20,view: 'text', type: 'string',},
  //子表高级查询
  stockInDetail: {
    title: '入库明细表',
    view: 'table',
    fields: {
        stockInNo: {title: '入库单号',order: 0,view: 'text', type: 'string',},
        goodsType: {title: '类型（物料、产品）',order: 1,view: 'list', type: 'string',dictCode: 'wms_item_type',},
        goodsCode: {title: '编码',order: 3,view: 'popup', type: 'string',code: 'mdm_material_select', orgFields: 'material_code', destFields: 'goodsCode', popupMulti: false,},
        goodsName: {title: '项目名称',order: 4,view: 'text', type: 'string',},
        goodsSpec: {title: '规格型号',order: 5,view: 'text', type: 'string',},
        unit: {title: '单位',order: 6,view: 'list', type: 'string',dictTable: "mis_unit where del_flag='0'", dictCode: 'unit', dictText: 'unit',},
        applyQty: {title: '申请数量',order: 7,view: 'number', type: 'number',},
        actualQty: {title: '实收数量',order: 8,view: 'number', type: 'number',},
        currency: {title: '原始交易币种',order: 9,view: 'list', type: 'string',dictTable: "mis_currency where del_flag='0' and status='1'", dictCode: 'currency_code', dictText: 'currency_name',},
        exchangeRate: {title: '汇率',order: 10,view: 'number', type: 'number',dictCode: '',},
        unitPrice: {title: '单价（本币）',order: 11,view: 'number', type: 'number',},
        totalAmount: {title: '金额',order: 12,view: 'number', type: 'number',},
        qcStatus: {title: '质检状态',order: 13,view: 'list', type: 'string',dictCode: 'mes_qc_status',},
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

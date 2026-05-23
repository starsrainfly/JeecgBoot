import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
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
    title: '供应商',
    align:"center",
    dataIndex: 'supplierName'
   },
   {
    title: '客户',
    align:"center",
    dataIndex: 'customerName'
   },
   {
    title: '仓库名称',
    align:"center",
    dataIndex: 'warehouseId_dictText'
   },
  {
    title: '总金额',
    align:"center",
    dataIndex: 'totalAmount',
    customRender:({text}) =>{
      return text ? parseFloat(text).toFixed(2) : '0.00';
    },
  },
   {
    title: '采购员',
    align:"center",
    dataIndex: 'purchaserName_dictText'
   },
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
   //  dataIndex: 'approveId'
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
    dataIndex: 'approveStatus'
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
   {
    title: '来源单据号',
    align:"center",
    dataIndex: 'sourceOrderNo'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
   {
    title: '是否产品',
    align:"center",
    dataIndex: 'isProduct_dictText'
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
      //colProps: {span: 6},
 	},
	{
      label: "供应商",
      field: "supplierName",
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
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "客户",
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
    dynamicDisabled:true,
  },
  {
    label: '入库类型',
    field: 'stockInType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_stock_in_type"
     },
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
            multi:true
        }
    },

  },
  {
    label: '客户',
    field: 'customerName',
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

  },
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
  {
    label: '采购员',
    field: 'purchaserId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_user,realname,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入采购员!'},
          ];
     },
  },
  {
    label: '审批人id',
    field: 'approveId',
    component: 'Input',
  },
  {
    label: '审核状态',
    field: 'approveStatus',
    component: 'Input',
  },
  {
    label: '审核备注',
    field: 'approveRemark',
    component: 'Input',
  },
  {
    label: '来源订单类型',
    field: 'sourceOrderType',
    component: 'Input',
  },
  {
    label: '来源单据id',
    field: 'sourceOrderId',
    component: 'Input',
  },
  {
    label: '来源单据号',
    field: 'sourceOrderNo',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
  {
    label: '是否产品',
    field: 'isProduct',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];

//子表列表数据
export const stockInDetailColumns: BasicColumn[] = [
   {
    title: '入库单号',
    align:"center",
    dataIndex: 'stockInNo'
   },
   {
    title: '类型（物料、产品）',
    align:"center",
    dataIndex: 'goodsType_dictText'
   },
   {
    title: '编码',
    align:"center",
    dataIndex: 'goodsCode'
   },
   {
    title: '项目名称',
    align:"center",
    dataIndex: 'goodsName'
   },
   {
    title: '规格型号',
    align:"center",
    dataIndex: 'goodsSpec'
   },
   {
    title: '单位',
    align:"center",
    dataIndex: 'unit_dictText'
   },
   {
    title: '申请数量',
    align:"center",
    dataIndex: 'applyQty'
   },
   {
    title: '实收数量',
    align:"center",
    dataIndex: 'actualQty'
   },
   {
    title: '币种',
    align:"center",
    dataIndex: 'currency'
   },
   {
    title: '汇率',
    align:"center",
    dataIndex: 'exchangeRate'
   },
   {
    title: '单价',
    align:"center",
    dataIndex: 'unitPrice'
   },
   {
    title: '金额',
    align:"center",
    dataIndex: 'totalAmount'
   },
   {
    title: '批号',
    align:"center",
    dataIndex: 'batchNo'
   },
   {
    title: '序列号',
    align:"center",
    dataIndex: 'serialNo'
   },
   {
    title: '质检状态',
    align:"center",
    dataIndex: 'qcStatus_dictText'
   },
   {
    title: '生产日期',
    align:"center",
    dataIndex: 'productionDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '质保天数',
    align:"center",
    dataIndex: 'shelfLife'
   },
   {
    title: '过期日期',
    align:"center",
    dataIndex: 'expiryDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
  {
    title:'备注',
    align:'center',
    dataIndex:'remark'
  },

];
//子表表单数据
export const stockInDetailFormSchema: FormSchema[] = [
  // TODO 子表隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false
  },
  {
    label: '入库单号',
    field: 'stockInNo',
    component: 'Input',
  },
  {
    label: '类型（物料、产品）',
    field: 'goodsType',
    defaultValue: "RAW",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_item_type"
     },
  },
  {
    label: '物料id',
    field: 'goodsId',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入物料id!'},
          ];
     },
  },
  {
    label: '编码',
    field: 'goodsCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_material_select",
            fieldConfig: [
                { source: 'id', target: 'goodsId' },
                { source: 'material_code', target: 'goodsCode' },
                { source: 'material_name', target: 'goodsName' },
                { source: 'material_spec', target: 'goodsSpec' },
                { source: 'material_type', target: 'goodsType' },
            ],
            multi:true
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入编码!'},
          ];
     },
  },
  {
    label: '项目名称',
    field: 'goodsName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入项目名称!'},
          ];
     },
  },
  {
    label: '规格型号',
    field: 'goodsSpec',
    component: 'Input',
  },
  {
    label: '单位',
    field: 'unit',
    defaultValue: "kg",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_unit where del_flag='0',unit,unit"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入单位!'},
          ];
     },
  },
  {
    label: '申请数量',
    field: 'applyQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入申请数量!'},
          ];
     },
  },
  {
    label: '实收数量',
    field: 'actualQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入实收数量!'},
          ];
     },
  },
  {
    label: '币种',
    field: 'currency',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_currency where del_flag='0' and status='1',currency_name,currency_code"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入币种!'},
          ];
     },
  },
  {
    label: '汇率',
    field: 'exchangeRate',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:""
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入汇率!'},
          ];
     },
  },
  {
    label: '单价',
    field: 'unitPrice',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入单价!'},
          ];
     },
  },
  {
    label: '金额',
    field: 'totalAmount',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入金额!'},
          ];
     },
  },
  {
    label: '批号',
    field: 'batchNo',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入批号!'},
          ];
     },
  },
  {
    label: '序列号',
    field: 'serialNo',
    component: 'Input',
  },
  {
    label: '质检状态',
    field: 'qcStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mes_qc_status"
     },
  },
  {
    label: '生产日期',
    field: 'productionDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入生产日期!'},
          ];
     },
  },
  {
    label: '质保天数',
    field: 'shelfLife',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入质保天数!'},
          ];
     },
  },
  {
    label: '过期日期',
    field: 'expiryDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入过期日期!'},
          ];
     },
  },
  {
    label:'备注',
    field:'remark',
    component:'Input',
  },
];

// ========== 纯明细查询专用列（包含主表关联字段）==========
export const stockInDetailQueryColumns: BasicColumn[] = [
  // 主表信息列
  {
    title: '入库单号',
    align: "center",
    dataIndex: 'stockInNo',
    sorter: true,
  },
  {
    title: '入库类型',
    align: "center",
    dataIndex: 'stockInType_dictText'
  },
  {
    title: '供应商',
    align: "center",
    dataIndex: 'supplierName'
  },
  {
    title: '客户',
    align: "center",
    dataIndex: 'customerName'
  },
  {
    title: '仓库',
    align: "center",
    dataIndex: 'warehouseId_dictText'
  },
  {
    title: '采购员',
    align: "center",
    dataIndex: 'purchaserId_dictText'
  },
  {
    title: '制单人',
    align: "center",
    dataIndex: 'operatorName'
  },
  {
    title: '状态',
    align: "center",
    dataIndex: 'status_dictText'
  },
  {
    title: '审核状态',
    align: "center",
    dataIndex: 'approveStatus_dictText'
  },
  {
    title: '入库时间',
    align: "center",
    dataIndex: 'stockInTime'
  },
  // 明细信息列
  {
    title: '物料类型',
    align: "center",
    dataIndex: 'goodsType_dictText'
  },
  {
    title: '物料编码',
    align: "center",
    dataIndex: 'goodsCode',
    sorter: true,
  },
  {
    title: '物料名称',
    align: "center",
    dataIndex: 'goodsName',
    sorter: true,
  },
  {
    title: '规格型号',
    align: "center",
    dataIndex: 'goodsSpec'
  },
  {
    title: '单位',
    align: "center",
    dataIndex: 'unit'
  },
  {
    title: '申请数量',
    align: "center",
    dataIndex: 'applyQty'
  },
  {
    title: '实收数量',
    align: "center",
    dataIndex: 'actualQty'
  },
  {
    title: '单价',
    align: "center",
    dataIndex: 'unitPrice'
  },
  {
    title: '金额',
    align: "center",
    dataIndex: 'totalAmount'
  },
  {
    title: '批号',
    align: "center",
    dataIndex: 'batchNo'
  },
  {
    title: '备注',
    align: "center",
    dataIndex: 'remark'
  },
  {
    title: '质检状态',
    align: "center",
    dataIndex: 'qcStatus_dictText'
  },
  {
    title: '生产日期',
    align: "center",
    dataIndex: 'productionDate',
    customRender:({text}) => {
      return !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
    },
  },
  {
    title: '过期日期',
    align: "center",
    dataIndex: 'expiryDate',
    customRender:({text}) => {
      return !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
    },
  },
];

// ========== 纯明细查询专用查询条件 ==========
export const detailQuerySearchSchema: FormSchema[] = [
  {
    label: "入库单号",
    field: "stockInNo",
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "物料编码",
    field: "goodsCode",
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "物料名称",
    field: "goodsName",
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "批号",
    field: "batchNo",
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: "入库类型",
    field: "stockInType",
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: "wms_stock_in_type"
    },
    colProps: {span: 6},
  },
  {
    label: "供应商",
    field: "supplierName",
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return {
        setFieldsValue: setFieldsValue,
        code: "scm_supplier",
        fieldConfig: [
          { source: 'supplier_name', target: 'supplierName' },
        ],
        multi: false
      }
    },
    colProps: {span: 6},
  },
  {
    label: "仓库",
    field: "warehouseId",
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: "mis_warehouse,name,id"
    },
    colProps: {span: 6},
  },
  {
    label: "质检状态",
    field: "qcStatus",
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: "mes_qc_status"
    },
    colProps: {span: 6},
  },
  {
    label: "入库时间",
    field: "stockInTime",
    component: 'RangePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    colProps: {span: 6},
  },
];

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
  approveId: {title: '审批人id',order: 10,view: 'text', type: 'string',},
  approveName: {title: '审批人',order: 11,view: 'text', type: 'string',},
  approveTime: {title: '审批时间',order: 12,view: 'datetime', type: 'string',},
  applyTime: {title: '申请时间',order: 13,view: 'datetime', type: 'string',},
  approveStatus: {title: '审核状态',order: 14,view: 'text', type: 'string',},
  approveRemark: {title: '审核备注',order: 15,view: 'text', type: 'string',},
  stockInTime: {title: '入库时间',order: 16,view: 'datetime', type: 'string',},
  sourceOrderType: {title: '来源订单类型',order: 17,view: 'text', type: 'string',},
  sourceOrderId: {title: '来源单据id',order: 18,view: 'text', type: 'string',},
  sourceOrderNo: {title: '来源单据号',order: 19,view: 'text', type: 'string',},
  remark: {title: '备注',order: 20,view: 'text', type: 'string',},
  isProduct: {title: '是否产品',order: 21,view: 'list', type: 'string',dictCode: 'yn',},
};

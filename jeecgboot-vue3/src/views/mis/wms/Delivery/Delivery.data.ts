import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '发货单号',
    align:"center",
    dataIndex: 'deliveryNo'
   },
   {
    title: '来源类型',
    align:"center",
    dataIndex: 'sourceType_dictText'
   },
   {
    title: '来源订单号',
    align:"center",
    dataIndex: 'sourceOrderNo'
   },
   {
    title: '客户名称',
    align:"center",
    dataIndex: 'customerName'
   },
   {
    title: '收货人',
    align:"center",
    dataIndex: 'consignee'
   },
   {
    title: '收货人电话',
    align:"center",
    dataIndex: 'consigneePhone'
   },
   {
    title: '收货人地址',
    align:"center",
    dataIndex: 'consigneeAddress'
   },
   {
    title: '物流类型',
    align:"center",
    dataIndex: 'logisticsType_dictText'
   },
   {
    title: '物流公司编码',
    align:"center",
    dataIndex: 'logisticsCompanyCode'
   },
   {
    title: '物流公司',
    align:"center",
    dataIndex: 'logisticsCompany'
   },
   {
    title: '物流单号/车牌号',
    align:"center",
    dataIndex: 'logisticsNo'
   },
   {
    title: '物流费用',
    align:"center",
    dataIndex: 'logisticsCost'
   },
   {
    title: '司机电话',
    align:"center",
    dataIndex: 'driverPhone'
   },
   {
    title: '发货时间',
    align:"center",
    dataIndex: 'deliveryTime'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '出库单号',
    align:"center",
    dataIndex: 'stockOutNo'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
   {
    title: '发货人',
    align:"center",
    dataIndex: 'deliverBy'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "来源类型",
      field: "sourceType",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"wms_delivery_source"
      },
      //colProps: {span: 6},
 	},
	{
      label: "来源订单号",
      field: "sourceOrderNo",
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_sales_order_for_plan",
            fieldConfig: [
                { source: 'id', target: 'sourceOrderId' },
                { source: 'order_no', target: 'sourceOrderNo' },
            ],
            multi:true
        }
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
                { source: 'name', target: 'consignee' },
                { source: 'address', target: 'consigneeAddress' },
                { source: 'phone_number', target: 'consigneePhone' },
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "物流类型",
      field: "logisticsType",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"wms_logistics_type"
      },
      //colProps: {span: 6},
 	},
	{
      label: "物流公司编码",
      field: "logisticsCompanyCode",
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
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "物流公司",
      field: "logisticsCompany",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "物流单号/车牌号",
      field: "logisticsNo",
      component: 'Input',
      //colProps: {span: 6},
 	},
     {
      label: "发货时间",
      field: "deliveryTime",
      component: 'RangePicker',
      componentProps: {
          valueType: 'Date',
          showTime:true
      },
      //colProps: {span: 6},
	},
	{
      label: "状态",
      field: "status",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"wms_delivery_status"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '发货单号',
    field: 'deliveryNo',
    component: 'Input',
  },
  {
    label:'公司',
    field:'companyName',
    component:'Input'
  },
  {
    label: '来源类型',
    field: 'sourceType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_delivery_source"
     },
  },
  {
    label: '来源订单id',
    field: 'sourceOrderId',
    component: 'Input',
    show:false,
  },
  {
    label: '来源订单号',
    field: 'sourceOrderNo',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_sales_order_for_plan",
            fieldConfig: [
                { source: 'id', target: 'sourceOrderId' },
                { source: 'order_no', target: 'sourceOrderNo' },
            ],
            multi:false
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入来源订单号!'},
          ];
     },
  },
  {
    label: '客户id',
    field: 'customerId',
    component: 'Input',
    show:false,
  },
  {
    label: '客户名称',
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
                { source: 'name', target: 'consignee' },
                { source: 'address', target: 'consigneeAddress' },
                { source: 'phone_number', target: 'consigneePhone' },
            ],
            multi:false
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入客户名称!'},
          ];
     },
  },
  {
    label: '收货人',
    field: 'consignee',
    component: 'Input',
  },
  {
    label: '收货人电话',
    field: 'consigneePhone',
    component: 'Input',
  },
  {
    label: '收货人地址',
    field: 'consigneeAddress',
    component: 'Input',
  },
  {
    label: '物流类型',
    field: 'logisticsType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_logistics_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入物流类型!'},
          ];
     },
  },
  {
    label: '物流公司id',
    field: 'logisticsCompanyId',
    component: 'Input',
    show:false
  },
  {
    label: '物流公司编码',
    field: 'logisticsCompanyCode',
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
            ],
            multi:false
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入物流公司编码!'},
          ];
     },
  },
  {
    label: '物流公司',
    field: 'logisticsCompany',
    component: 'Input',
  },
  {
    label: '物流单号/车牌号',
    field: 'logisticsNo',
    component: 'Input',
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
  },
  {
    label: '司机电话',
    field: 'driverPhone',
    component: 'Input',
  },
  {
    label: '发货时间',
    field: 'deliveryTime',
    component: 'DatePicker',
    componentProps: {
       showTime:true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入发货时间!'},
          ];
     },
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_delivery_status"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入状态!'},
          ];
     },
  },
  {
    label: '出库单id',
    field: 'stockOutId',
    component: 'Input',
    show:false
  },
  {
    label: '出库单号',
    field: 'stockOutNo',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
  {
    label: '发货人',
    field: 'deliverBy',
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
export const deliveryDetailColumns: JVxeColumn[] = [
    // {
    //   title: '发货单ID',
    //   key: 'deliveryId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //     validateRules: [
    //       { required: true, message: '${title}不能为空' },
    //     ],
    // },
    // {
    //   title: '销售订单明细ID',
    //   key: 'sourceDetailId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //     validateRules: [
    //       { required: true, message: '${title}不能为空' },
    //     ],
    // },
    {
      title: '产品ID',
      key: 'goodsId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '产品编码',
      key: 'goodsCode',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
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
      title: '单位',
      key: 'unit',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    // {
    //   title: '生产批次ID',
    //   key: 'productionBatchId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //     validateRules: [
    //       { required: true, message: '${title}不能为空' },
    //     ],
    // },
    {
      title: '批次号',
      key: 'productionBatchNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '生产日期',
      key: 'productionDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '有效期至',
      key: 'expiryDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    // {
    //   title: '库存记录ID',
    //   key: 'stockId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //     validateRules: [
    //       { required: true, message: '${title}不能为空' },
    //     ],
    // },
    // {
    //   title: '仓库ID',
    //   key: 'warehouseId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    {
      title: '仓库名称',
      key: 'warehouseName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '实际发货数量',
      key: 'actualQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '单价',
      key: 'unitPrice',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '金额',
      key: 'detailAmount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '扫描的二维码内容',
      key: 'scanCode',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '扫码时间',
      key: 'scanTime',
      type: JVxeTypes.datetime,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    // {
    //   title: '出库明细ID',
    //   key: 'stockOutDetailId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    {
      title: '备注',
      key: 'remark',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  deliveryNo: {title: '发货单号',order: 0,view: 'text', type: 'string',},
  sourceType: {title: '来源类型',order: 1,view: 'list', type: 'string',dictCode: 'wms_delivery_source',},
  sourceOrderNo: {title: '来源订单号',order: 3,view: 'popup', type: 'string',code: 'scm_sales_order_for_plan', orgFields: 'order_no', destFields: 'sourceOrderNo', popupMulti: false,},
  customerName: {title: '客户名称',order: 5,view: 'popup', type: 'string',code: 'scm_customer', orgFields: 'customer_name', destFields: 'customerName', popupMulti: false,},
  consignee: {title: '收货人',order: 6,view: 'text', type: 'string',},
  consigneePhone: {title: '收货人电话',order: 7,view: 'text', type: 'string',},
  consigneeAddress: {title: '收货人地址',order: 8,view: 'text', type: 'string',},
  logisticsType: {title: '物流类型',order: 9,view: 'list', type: 'string',dictCode: 'wms_logistics_type',},
  logisticsCompanyCode: {title: '物流公司编码',order: 11,view: 'popup', type: 'string',code: 'wms_logistics_company', orgFields: 'company_code', destFields: 'logisticsCompanyCode', popupMulti: false,},
  logisticsCompany: {title: '物流公司',order: 12,view: 'text', type: 'string',},
  logisticsNo: {title: '物流单号/车牌号',order: 13,view: 'text', type: 'string',},
  logisticsCost: {title: '物流费用',order: 14,view: 'number', type: 'number',},
  driverPhone: {title: '司机电话',order: 15,view: 'text', type: 'string',},
  deliveryTime: {title: '发货时间',order: 16,view: 'datetime', type: 'string',},
  status: {title: '状态',order: 17,view: 'list', type: 'string',dictCode: 'wms_delivery_status',},
  stockOutNo: {title: '出库单号',order: 19,view: 'text', type: 'string',},
  remark: {title: '备注',order: 20,view: 'text', type: 'string',},
  deliverBy: {title: '发货人',order: 21,view: 'text', type: 'string',},
  //子表高级查询
  deliveryDetail: {
    title: '发货明细',
    view: 'table',
    fields: {
        deliveryId: {title: '发货单ID',order: 0,view: 'text', type: 'string',},
        sourceDetailId: {title: '销售订单明细ID',order: 1,view: 'text', type: 'string',},
        goodsId: {title: '产品ID',order: 2,view: 'text', type: 'string',},
        goodsCode: {title: '产品编码',order: 3,view: 'text', type: 'string',},
        goodsName: {title: '产品名称',order: 4,view: 'text', type: 'string',},
        goodsSpec: {title: '规格型号',order: 5,view: 'text', type: 'string',},
        unit: {title: '单位',order: 6,view: 'text', type: 'string',},
        productionBatchId: {title: '生产批次ID',order: 7,view: 'text', type: 'string',},
        productionBatchNo: {title: '批次号',order: 8,view: 'text', type: 'string',},
        productionDate: {title: '生产日期',order: 9,view: 'date', type: 'string',},
        expiryDate: {title: '有效期至',order: 10,view: 'date', type: 'string',},
        stockId: {title: '库存记录ID',order: 11,view: 'text', type: 'string',},
        warehouseId: {title: '仓库ID',order: 12,view: 'text', type: 'string',},
        warehouseName: {title: '仓库名称',order: 13,view: 'text', type: 'string',},
        actualQty: {title: '实际发货数量',order: 14,view: 'number', type: 'number',},
        unitPrice: {title: '单价',order: 15,view: 'number', type: 'number',},
        detailAmount: {title: '金额',order: 16,view: 'number', type: 'number',},
        scanCode: {title: '扫描的二维码内容',order: 17,view: 'text', type: 'string',},
        scanTime: {title: '扫码时间',order: 18,view: 'datetime', type: 'string',},
        remark: {title: '备注',order: 20,view: 'text', type: 'string',},
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

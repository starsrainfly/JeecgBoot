import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '计划编号',
    align:"center",
    dataIndex: 'planNo'
  },
   {
    title: '产品编码',
    align:"center",
    dataIndex: 'productCode'
   },
   {
    title: '产品名称',
    align:"center",
    dataIndex: 'productName'
   },
   {
    title: '计划数量（kg）',
    align:"center",
    dataIndex: 'plannedQty'
   },
  {
    title: '完工数量（kg）',
    align:"center",
    dataIndex: 'completedQty'
  },
   {
    title: '计划类型',
    align:"center",
    dataIndex: 'planType_dictText'
   },
   {
    title: '计划开工日期',
    align:"center",
    dataIndex: 'plannedStartDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '计划完工日期',
    align:"center",
    dataIndex: 'plannedEndDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '实际开工时间',
    align:"center",
    dataIndex: 'actualStartTime'
   },
   {
    title: '实际完工时间',
    align:"center",
    dataIndex: 'actualEndTime'
   },
   {
    title: '计划状态',
    align:"center",
    dataIndex: 'planStatus_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: "计划编号",
    field: "planNo",
    component: 'Input',
    //colProps: {span: 6},
  },
	{
      label: "产品编码",
      field: "productCode",
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_product_select",
            fieldConfig: [
                { source: 'id', target: 'productId' },
                { source: 'product_code', target: 'productCode' },
                { source: 'product_name', target: 'productName' },
            ],
            multi:false
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "产品名称",
      field: "productName",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "计划类型",
      field: "planType",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mes_plan_type"
      },
      //colProps: {span: 6},
 	},
	{
      label: "计划开工日期",
      field: "plannedStartDate",
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD'
      },      
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '计划编号',
    field: 'planNo',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '产品编码',
    field: 'productCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_product_select",
            fieldConfig: [
                { source: 'id', target: 'productId' },
                { source: 'product_code', target: 'productCode' },
                { source: 'product_name', target: 'productName' },
            ],
            multi:false
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入产品编码!'},
          ];
     },
  },
  {
    label: '产品名称',
    field: 'productName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入产品名称!'},
          ];
     },
  },
  {
    label: '计划生产数量（kg）',
    field: 'plannedQty',
    component: 'InputNumber',
    componentProps: {
      // 监听变化，触发子表校验
      onChange: (e) => {
        // 这个在组件内部处理，这里只是标记
      }
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入计划生产数量(kg)!'},
        {
          // 基础数值校验，复杂业务校验在Modal中处理
          pattern: /^[0-9]+(\.[0-9]{1,4})?$/,
          message: '请输入有效数字'
        },
        {
          validator: (_, value) => {
            // 这里只做基础校验，复杂校验在提交时处理
            return Promise.resolve();
          }
        }
      ];
    },
  },
  {
    label: '计划类型',
    field: 'planType',
    defaultValue: "0",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mes_plan_type"
     },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请选择计划类型!'},
      ];
    },
  },
  {
    label: '计划开工日期',
    field: 'plannedStartDate',
    component: 'DatePicker',
    componentProps:{
      valueFormat: 'YYYY-MM-DD'
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入计划开工日期!'},
      ];
    },
  },
  {
    label: '计划完工日期',
    field: 'plannedEndDate',
    component: 'DatePicker',
    componentProps:{
      valueFormat: 'YYYY-MM-DD'
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入计划完工日期!'},
      ];
    },
  },
  {
    label: '计划状态',
    field: 'planStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mes_plan_status"
     },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请选择计划状态!'},
      ];
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
//子表单数据
//子表表格配置
export const productionPlanDetailColumns: JVxeColumn[] = [
    // {
    //   title: '计划id',
    //   key: 'planId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //   show: false
    // },
    // {
    //   title: '销售行id',
    //   key: 'salesOrderDetailId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //   show: false
    // },
    // {
    //   title: '销售订单id',
    //   key: 'salesOrderId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //   show: false
    // },
    {
      title: '销售单号',
      key: 'salesOrderCode',
      type: JVxeTypes.popup,
      popupCode:"scm_sales_order_for_plan",
      fieldConfig: [
        { source: 'id', target: 'salesOrderId' },
        { source: 'order_no', target: 'salesOrderCode' },
        { source: 'delivery_date', target: 'deliveryDate' },
        { source: 'order_line_id', target: 'salesOrderLineId' },
        { source: 'item_id', target: 'productId' },
        { source: 'item_code', target: 'productCode' },
        { source: 'item_name', target: 'productName' },
        { source: 'demand_qty', target: 'demandQty' },
        { source: 'package_item_id', target: 'packageId' },
        { source: 'package_capacity', target: ' packageCapacity' },
        { source: 'package_capacity_unit', target: 'packageCapacityUnit' },
        { source: 'package_name', target: 'packageName' },
        { source: 'customer_id', target: 'customerId' },
        { source: 'customer_name', target: 'customerName' },
        { source: 'salesman_id', target: 'salesmanId' },
        { source: 'salesman', target: 'salesman' },
        { source: 'remark', target: 'salesRemark' },
      ],

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',

    },
    // {
    //   title: '产品编码',
    //   key: 'productId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //   show:false
    // },
    {
      title: '产品编码',
      key: 'productCode',
      type: JVxeTypes.popup,
      popupCode:"mdm_product_select",
      fieldConfig: [
        { source: 'id', target: 'productId' },
        { source: 'product_code', target: 'productCode' },
        { source: 'product_name', target: 'productName' },
        { source: 'product_spec', target: 'productSpec' },
      ],

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '产品名称',
      key: 'productName',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '产品规格',
      key: 'productSpec',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '订单数量',
      key: 'demandQty',
      type: JVxeTypes.inputNumber,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '分配数量',
      key: 'allocatedQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
        {
          // 基础数值校验，复杂业务校验在Modal中处理
          pattern: /^[0-9]+(\.[0-9]{1,4})?$/,
          message: '请输入有效数字'
        }
      ],
    },
    {
      title: '已完成数量',
      key: 'completedQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      disabled:true,
    },
    {
      title: '包装名称',
      key: 'packageName',
      type: JVxeTypes.popup,
      popupCode:"mdm_package_select",
      param: {
        packageType: "'0'",
      },
      fieldConfig: [
        { source: 'id', target: 'packageId' },
        { source: 'material_name', target: 'packageName'},
        { source: 'description', target: 'packageSpec'},
        { source: 'package_capacity', target: 'packageCapacity'},
        { source: 'package_capacity_unit', target: 'packageCapacityUnit'},
      ],

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '包装规格',
      key: 'packageSpec',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '包装容量',
      key: 'packageCapacity',
      type: JVxeTypes.inputNumber,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '包装单位',
      key: 'packageCapacityUnit',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    // {
    //   title: '客户',
    //   key: 'customerId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //   show:false
    // },
    {
      title: '客户编码',
      key: 'customerCode',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '客户名称',
      key: 'customerName',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    // {
    //   title: '业务',
    //   key: 'salesmanId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //   show:false
    // },
    {
      title: '业务员',
      key: 'salesmanName',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '交货日期',
      key: 'deliveryDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    // {
    //   title: '包装id',
    //   key: 'packageId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //   show:false
    // },
    {
      title: '销售备注',
      key: 'salesRemark',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '计划备注',
      key: 'planRemark',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '状态',
      key: 'detailStatus',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mes_plan_status",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      disabled:true,
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  productCode: {title: '产品编码',order: 0,view: 'popup', type: 'string',code: 'mdm_product_select', orgFields: 'product_code', destFields: 'productCode', popupMulti: false,},
  productName: {title: '产品名称',order: 1,view: 'text', type: 'string',},
  plannedQty: {title: '计划生产数量（kg）',order: 2,view: 'number', type: 'number',},
  planType: {title: '计划类型',order: 3,view: 'list', type: 'string',dictCode: 'mes_plan_type',},
  plannedStartDate: {title: '计划开工日期',order: 4,view: 'date', type: 'string',},
  plannedEndDate: {title: '计划完工日期',order: 5,view: 'date', type: 'string',},
  actualStartTime: {title: '实际开工时间',order: 6,view: 'datetime', type: 'string',},
  actualEndTime: {title: '实际完工时间',order: 7,view: 'datetime', type: 'string',},
  planStatus: {title: '计划状态',order: 8,view: 'list', type: 'string',dictCode: 'mes_plan_status',},
  //子表高级查询
  productionPlanDetail: {
    title: '生产计划明细表',
    view: 'table',
    fields: {
        salesOrderDetailId: {title: '销售行id',order: 1,view: 'text', type: 'string',},
        salesOrderId: {title: '销售订单id',order: 2,view: 'text', type: 'string',},
        salesOrderCode: {title: '订单编号',order: 3,view: 'popup', type: 'string',code: 'scm_sales_order_for_plan', orgFields: 'order_no', destFields: 'salesOrderCode', popupMulti: false,},
        productId: {title: '产品编码',order: 4,view: 'text', type: 'string',},
        productCode: {title: '产品编码',order: 5,view: 'popup', type: 'string',code: 'mdm_product_select', orgFields: 'product_code', destFields: 'productCode', popupMulti: false,},
        productName: {title: '产品名称',order: 6,view: 'text', type: 'string',},
        productSpec: {title: '产品规格',order: 7,view: 'text', type: 'string',},
        demandQty: {title: '订单数量',order: 8,view: 'number', type: 'number',},
        allocatedQty: {title: '分配数量',order: 9,view: 'number', type: 'number',},
        completedQty: {title: '已完成数量',order: 10,view: 'number', type: 'number',},
        packageName: {title: '包装名称',order: 11,view: 'popup', type: 'string',code: '', orgFields: '', destFields: 'packageName', popupMulti: false,},
        packageSpec: {title: '包装规格',order: 12,view: 'text', type: 'string',},
        packageCapacity: {title: '包装容量',order: 13,view: 'number', type: 'number',},
        packageCapacityUnit: {title: '包装单位',order: 14,view: 'text', type: 'string',},
        customerId: {title: '客户',order: 15,view: 'text', type: 'string',},
        customerCode: {title: '客户编码',order: 16,view: 'text', type: 'string',},
        customerName: {title: '客户名称',order: 17,view: 'text', type: 'string',},
        salesmanId: {title: '业务',order: 18,view: 'text', type: 'string',},
        salesmanName: {title: '业务员',order: 19,view: 'text', type: 'string',},
        deliveryDate: {title: '交货日期',order: 20,view: 'date', type: 'string',},
        packageId: {title: '包装id',order: 21,view: 'text', type: 'string',},
        salesRemark: {title: '销售备注',order: 22,view: 'text', type: 'string',},
        planRemark: {title: '计划备注',order: 23,view: 'text', type: 'string',},
        detailStatus: {title: '状态',order: 24,view: 'list', type: 'string',dictCode: 'mes_plan_status',},
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

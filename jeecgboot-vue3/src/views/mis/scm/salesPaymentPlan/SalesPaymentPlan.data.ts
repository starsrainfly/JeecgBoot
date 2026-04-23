import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '计划单号',
    align:"center",
    dataIndex: 'planNo'
   },
   {
    title: '计划名称',
    align:"center",
    dataIndex: 'planName',
     width:240
   },
   // {
   //  title: '关联订单id',
   //  align:"center",
   //  dataIndex: 'salesOrderId'
   // },
   {
    title: '销售订单号',
    align:"center",
    dataIndex: 'salesOrderNo'
   },
  {
    title: '业务员',
    align:"center",
    dataIndex: 'salesmanId_dictText',
    width:90
  },
   {
    title: '客户名称',
    align:"center",
    dataIndex: 'customerId_dictText'
   },

   {
    title: '期数',
    align:"center",
    dataIndex: 'planStage',
     width:80,
   },
   {
    title: '应收金额',
    align:"center",
    dataIndex: 'planAmount',
     width:100
   },
   {
    title: '应收日期',
    align:"center",
    dataIndex: 'planDate',
     width:100,
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
  {
    title: '已付金额',
    align:"center",
    dataIndex: 'paidAmount',
    width:100,
  },
  {
    title: '未付金额',
    align:"center",
    dataIndex: 'unpaidAmount',
    width:100,
  },
   {
    title: '结算方式',
    align:"center",
    dataIndex: 'paymentMethod_dictText',
     width:100,
   },
   {
    title: '账期天数',
    align:"center",
    dataIndex: 'paymentDays',
     width:100,
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'planStatus_dictText',
     width:100,
   },

];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "计划单号",
      field: 'planNo',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "销售订单号",
      field: 'salesOrderNo',
      component: 'Input',
      //colProps: {span: 6},
 	},
  {
    label: '业务员',
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"sys_user where del_flag='0' and status='1',realname,id",
    },

  },
  {
    label: "客户id",
    field: 'customerId',
    component:'Input',
    show:false,
  },
	{
      label: "客户",
      field: 'customerName',
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
      label: "计划名称",
      field: 'planName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "计划收款日期",
      field: 'planDate',
      component: 'RangePicker',
      componentProps: {
        valueType: 'Date',
      },
      //colProps: {span: 6},
 	},
	{
      label: "结算方式",
      field: 'paymentMethod',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"payment_method"
      },
      //colProps: {span: 6},
 	},
	{
      label: "账期天数",
      field: 'paymentDays',
      component: 'InputNumber',
      //colProps: {span: 6},
 	},
	{
      label: "状态",
      field: 'planStatus',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"fms_settlement_status"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '计划单号',
    field: 'planNo',
    component: 'Input',
    componentProps:{
      readonly: true,
    }
  },
  {
    label: '销售订单id',
    field: 'salesOrderId',
    component: 'Input',
    show:false,
  },
  {
    label: '销售订单号',
    field: 'salesOrderNo',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return{
        setFieldsValue:setFieldsValue,
        code:"scm_sales_order_for_plan",
        fieldConfig: [
          { source: 'id', target: 'salesOrderId' },
          { source: 'order_no', target: 'salesOrderNo' },
          { source: 'salesman_id', target: 'salesmanId' },
          { source: 'customer_id', target: 'customerId' },
          { source: 'customer_name', target: 'customerName' },
        ],
        multi:false
      }
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入销售订单号!'},
          ];
     },
  },
  {
    label:'客户id',
    field: 'customerId',
    component:'Input',
    show:false
  },
  {
    label: '客户',
    field: 'customerName',
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
            multi:false
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入客户!'},
          ];
     },
  },
  {
    label: '业务员',
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"sys_user where del_flag='0' and status='1',realname,id",
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入业务员!'},
      ];
    },
  },
  {
    label: '计划名称',
    field: 'planName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划名称!'},
          ];
     },
  },
  {
    label: '期数',
    field: 'planStage',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入期数!'},
          ];
     },
  },
  {
    label: '计划收款金额',
    field: 'planAmount',
    component: 'InputNumber',
    componentProps: ({ formModel }) => ({
      onChange: (val) => {
        // 计划金额变化时，自动重算未付
        const plan = Number(val) || 0;
        const paid = Number(formModel.paidAmount) || 0;
        formModel.unpaidAmount = (plan - paid).toFixed(2);
      },
    }),
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划收款金额!'},
          ];
     },
  },
  {
    label: '计划收款日期',
    field: 'planDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划收款日期!'},
          ];
     },
  },
  {
    label: '结算方式',
    field: 'paymentMethod',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"payment_method"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入结算方式!'},
          ];
     },
  },
  {
    label: '账期天数',
    field: 'paymentDays',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入账期天数!'},
          ];
     },
  },
  {
    label: '已付金额',
    field: 'paidAmount',
    component: 'InputNumber',
    componentProps: ({ formModel }) => ({
      onChange: (val) => {
        // 已付变化时，自动算未付
        const plan = Number(formModel.planAmount) || 0;
        const paid = Number(val) || 0;
        formModel.unpaidAmount = (plan - paid).toFixed(2);
      },
    }),
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入已付金额!'},
      ];
    },
  },
  {
    label: '未付金额',
    field: 'unpaidAmount',
    component: 'InputNumber',
    componentProps:{
      readonly:true,
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入未付金额!'},
      ];
    },
  },
  {
    label: '状态',
    field: 'planStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"fms_settlement_status"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入状态!'},
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

// 高级查询数据
export const superQuerySchema = {
  planNo: {title: '计划单号',order: 0,view: 'text', type: 'string',},
  salesOrderId: {title: '关联订单id',order: 1,view: 'text', type: 'string',},
  salesOrderNo: {title: '销售订单号',order: 2,view: 'text', type: 'string',},
  customerId: {title: '客户id',order: 3,view: 'popup', type: 'string',code: 'scm_customer_no_param', orgFields: 'id', destFields: 'customerId', popupMulti: false,},
  planName: {title: '计划名称',order: 4,view: 'text', type: 'string',},
  planStage: {title: '期数',order: 5,view: 'number', type: 'number',},
  planAmount: {title: '计划收款金额',order: 6,view: 'number', type: 'number',},
  planDate: {title: '计划收款日期',order: 7,view: 'date', type: 'string',},
  paymentMethod: {title: '结算方式',order: 8,view: 'list', type: 'string',dictCode: 'payment_method',},
  paymentDays: {title: '账期天数',order: 9,view: 'number', type: 'number',},
  planStatus: {title: '状态',order: 10,view: 'list', type: 'string',dictCode: 'fms_settlement_status',},
  paidAmount: {title: '已付金额',order: 11,view: 'number', type: 'number',},
  unpaidAmount: {title: '未付金额',order: 12,view: 'number', type: 'number',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

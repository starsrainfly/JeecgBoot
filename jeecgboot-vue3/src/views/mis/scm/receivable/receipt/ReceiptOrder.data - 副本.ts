import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '收款单号',
    align:"center",
    dataIndex: 'receiptNo'
   },
   // {
   //  title: '客户id',
   //  align:"center",
   //  dataIndex: 'customerId'
   // },
   {
    title: '客户编号',
    align:"center",
    dataIndex: 'customerCode'
   },
   {
    title: '客户名称',
    align:"center",
    dataIndex: 'customerName'
   },
   {
    title: '业务员',
    align:"center",
    dataIndex: 'salesmanId_dictText'
   },
   {
    title: '收款日期',
    align:"center",
    dataIndex: 'receiptDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '收款总额',
    align:"center",
    dataIndex: 'receiptAmount'
   },
   {
    title: '币种',
    align:"center",
    dataIndex: 'currencyCode_dictText'
   },
   {
    title: '汇率',
    align:"center",
    dataIndex: 'exchangeRate'
   },
   {
    title: '收款方式',
    align:"center",
    dataIndex: 'paymentMethod_dictText'
   },
  {
    title: '收款银行',
    align:"center",
    dataIndex: 'receiptBankName'
  },
   {
    title: '收款银行账户',
    align:"center",
    dataIndex: 'bankAccount'
   },
   {
    title: '银行流水号/支票号',
    align:"center",
    dataIndex: 'referenceNo'
   },
  {
    title: '付款银行',
    dataIndex: 'payerBankName',
    align:"center",

  },
  {
    title: '付款账号',
    dataIndex: 'payerAccount',
    align:"center",
  },
  {
    title: '付款人/公司',
    dataIndex: 'payerName',
    align:"center",
  },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "收款单号",
      field: "receiptNo",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "客户编号",
      field: "customerCode",
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_customer_no_param",
            fieldConfig: [
                { source: 'id', target: 'customerId' },
                { source: 'customer_code', target: 'customerCode' },
                { source: 'customer_name', target: 'customerName' },
                { source: 'payment_method', target: 'paymentMethod' },
                { source: 'paymnet_days', target: 'paymentDays' },
            ],
            multi:false
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "客户名称",
      field: "customerName",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "业务员",
      field: "salesmanId",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"sys_user where del_flag='0' and status='1',realname,id"
      },
      //colProps: {span: 6},
 	},
     {
      label: "收款日期",
      field: "receiptDate",
      component: 'RangePicker',
      componentProps: {
        valueType: 'Date',
      },
      //colProps: {span: 6},
	},
];
//表单数据
export const formSchema: FormSchema[] = [
  // ==================== 基本信息分组 ====================
  {
    label: '收款单号',
    field: 'receiptNo',
    component: 'Input',
  },
  {
    label: '客户id',
    field: 'customerId',
    component: 'Input',
    show:false
  },
  {
    label: '客户编号',
    field: 'customerCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_customer_no_param",
            fieldConfig: [
                { source: 'id', target: 'customerId' },
                { source: 'customer_code', target: 'customerCode' },
                { source: 'customer_name', target: 'customerName' },
                { source: 'payment_method', target: 'paymentMethod' },
                { source: 'paymnet_days', target: 'paymentDays' },
                { source: 'salesman_id', target: 'salesmanId' },
            ],
            multi:false
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入客户编号!'},
          ];
     },
  },
  {
    label: '客户名称',
    field: 'customerName',
    component: 'Input',
  },
  {
    label: '业务员',
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_user where del_flag='0' and status='1',realname,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入业务员!'},
          ];
     },
  },
  {
    label: '收款日期',
    field: 'receiptDate',
    component: 'DatePicker',
    componentProps:{
      valueFormat: 'YYYY-MM-DD'
    },    
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入收款日期!'},
          ];
     },
  },
  {
    label: '收款总额',
    field: 'receiptAmount',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入收款总额!'},
          ];
     },
  },
  {
    label: '币种',
    field: 'currencyCode',
    defaultValue: "CNY",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_currency where del_flag='0' and status='1',currency_code,currency_code"
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
    defaultValue: 1,
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入汇率!'},
          ];
     },
  },
  {
    label: '收款方式',
    field: 'paymentMethod',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"payment_method"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入收款方式!'},
          ];
     },
  },
  {
    label: '收款银行',
    field: 'receiptBankName',
    component: 'Input',
  },
  {
    label: '收款银行账户',
    field: 'bankAccount',
    component: 'Input',
  },
  {
    label: '银行流水号/支票号',
    field: 'referenceNo',
    component: 'Input',
  },
  {
    label: '付款银行',
    field: 'payerBankName',
    component: 'Input',
    //componentProps: { dictCode: 'bank_name' },
  },
  {
    label: '付款账号',
    field: 'payerAccount',
    component: 'Input',
  },
  {
    label: '付款人/公司',
    field: 'payerName',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'status',
    defaultValue: "1",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"status"
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
//子表单数据
//子表表格配置
export const receiptOrderDetailColumns: JVxeColumn[] = [
    // {
    //   title: '收款单id',
    //   key: 'receiptId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    {
      title: '收款计划id',
      key: 'planId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      visible:false
    },
    {
      title: '收款计划单号',
      key: 'planNo',
      type: JVxeTypes.slot,  // 改为 slot 类型
      slotName: 'planSelectSlot', // 自定义插槽名

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '计划名称',
      key: 'planName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '订单id',
      key: 'salesOrderId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      visible:false,
    },
    {
      title: '销售订单号',
      key: 'salesOrderNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '应收金额',
      key: 'planAmount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '已收金额',
      key: 'alreadyReceipt',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '本次收款金额',
      key: 'receiptAmount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
          { pattern: /^[0-9]+(\.[0-9]{1,2})?$/, message: '请输入有效金额' },
        ],
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  receiptNo: {title: '收款单号',order: 0,view: 'text', type: 'string',},
  customerId: {title: '客户id',order: 1,view: 'text', type: 'string',},
  customerCode: {title: '客户编号',order: 2,view: 'popup', type: 'string',code: 'scm_customer_no_param', orgFields: 'customer_code', destFields: 'customerCode', popupMulti: false,},
  customerName: {title: '客户名称',order: 3,view: 'text', type: 'string',},
  salesmanId: {title: '业务员',order: 4,view: 'list', type: 'string',dictTable: "sys_user where del_flag='0' and status='1'", dictCode: 'id', dictText: 'realname',},
  receiptDate: {title: '收款日期',order: 5,view: 'date', type: 'string',},
  receiptAmount: {title: '收款总额',order: 6,view: 'number', type: 'number',},
  currencyCode: {title: '币种',order: 7,view: 'list', type: 'string',dictTable: "mis_currency where del_flag='0' and status='1'", dictCode: 'currency_code', dictText: 'currency_code',},
  exchangeRate: {title: '汇率',order: 8,view: 'number', type: 'number',},
  paymentMethod: {title: '收款方式',order: 9,view: 'list', type: 'string',dictCode: 'payment_method',},
  bankAccount: {title: '收款银行账户',order: 10,view: 'text', type: 'string',},
  referenceNo: {title: '银行流水号/支票号',order: 11,view: 'text', type: 'string',},
  remark: {title: '备注',order: 12,view: 'text', type: 'string',},
  status: {title: '状态',order: 13,view: 'list', type: 'string',dictCode: 'status',},
  //子表高级查询
  receiptOrderDetail: {
    title: '收款明细',
    view: 'table',
    fields: {
        receiptId: {title: '收款单id',order: 0,view: 'text', type: 'string',},
        planId: {title: '收款计划id',order: 1,view: 'text', type: 'string',},
        planNo: {title: '收款计划单号',order: 2,view: 'popup', type: 'string',code: 'scm_sales_payment_plan', orgFields: 'plan_no', destFields: 'planNo', popupMulti: false,},
        planName: {title: '计划名称',order: 3,view: 'text', type: 'string',},
        salesOrderId: {title: '订单id',order: 4,view: 'text', type: 'string',},
        salesOrderNo: {title: '订单号',order: 5,view: 'text', type: 'string',},
        planAmount: {title: '应收金额',order: 6,view: 'number', type: 'number',},
        alreadyReceipt: {title: '已收金额',order: 7,view: 'number', type: 'number',},
        receiptAmount: {title: '核销金额',order: 8,view: 'number', type: 'number',},
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

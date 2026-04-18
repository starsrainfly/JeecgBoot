import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '策略名称',
    align:"center",
    dataIndex: 'strategyName'
   },
   {
    title: '产品',
    align:"center",
    dataIndex: 'itemId'
   },
   {
    title: '产品编码',
    align:"center",
    dataIndex: 'itemCode'
   },
   {
    title: '产品名称',
    align:"center",
    dataIndex: 'itemName'
   },
   {
    title: '币种代码',
    align:"center",
    dataIndex: 'currencyCode_dictText'
   },
   {
    title: '客户名称',
    align:"center",
    dataIndex: 'customerName'
   },
   {
    title: '业务员',
    align:"center",
    dataIndex: 'salesmanName'
   },
   {
    title: '包装名称',
    align:"center",
    dataIndex: 'packageName'
   },
  {
    title: '包装规格',
    align:"center",
    dataIndex: 'packageSpec'
  },
   {
    title: '价格',
    align:"center",
    dataIndex: 'agreedPrice'
   },
   {
    title: '最小起订量',
    align:"center",
    dataIndex: 'minQuantity'
   },
   {
    title: '生效日期',
    align:"center",
    dataIndex: 'effectiveFrom',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '失效日期',
    align:"center",
    dataIndex: 'effectiveTo',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '定价类型',
    align:"center",
    dataIndex: 'pricingType_dictText'
   },
   {
    title: '是否启用',
    align:"center",
    dataIndex: 'isActive_dictText'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "策略名称",
      field: 'strategyName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "产品编码",
      field: 'itemCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "产品名称",
      field: 'itemName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "客户名称",
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

      //colProps: {span: 6},
 	},
	{
      label: "业务员id",
      field: 'salesmanId',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"sys_user where del_flag='0' and status='1',realname,id"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '策略名称',
    field: 'strategyName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入策略名称!'},
          ];
     },
  },
  {
    label: '产品',
    field: 'itemId',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_product_popup",
            fieldConfig: [
                { source: 'id', target: 'itemId' },
                { source: 'product_code', target: 'itemCode' },
                { source: 'product_name', target: 'itemName' },
            ],
            multi:true
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入产品!'},
          ];
     },
  },
  {
    label: '产品编码',
    field: 'itemCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入产品编码!'},
          ];
     },
  },
  {
    label: '产品名称',
    field: 'itemName',
    component: 'Input',
  },
  {
    label: '币种代码',
    field: 'currencyCode',
    defaultValue: "CNY",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_currency,currency_name,currency_code"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入币种代码!'},
          ];
     },
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
            ],
            multi:true
        }
    },

  },
  {
    label: '业务员',
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_user where del_flag='0' and status='1',realname,id"
     },
  },
  {
    label: '包装id',
    field: 'packageItemId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_material where is_package='1' and del_flag='0',material_spec,id"
     },
  },
  {
    label: '价格',
    field: 'agreedPrice',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入价格!'},
          ];
     },
  },
  {
    label: '最小起订量',
    field: 'minQuantity',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入最小起订量!'},
          ];
     },
  },
  {
    label: '生效日期',
    field: 'effectiveFrom',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入生效日期!'},
          ];
     },
  },
  {
    label: '失效日期',
    field: 'effectiveTo',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入失效日期!'},
          ];
     },
  },
  {
    label: '定价类型',
    field: 'pricingType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"scm_pricing_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入定价类型!'},
          ];
     },
  },
  {
    label: '是否启用',
    field: 'isActive',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入是否启用!'},
          ];
     },
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

// 高级查询数据
export const superQuerySchema = {
  strategyName: {title: '策略名称',order: 0,view: 'text', type: 'string',},
  itemId: {title: '产品',order: 1,view: 'popup', type: 'string',code: 'mdm_product_popup', orgFields: 'id', destFields: 'itemId', popupMulti: false,},
  itemCode: {title: '产品编码',order: 2,view: 'text', type: 'string',},
  itemName: {title: '产品名称',order: 3,view: 'text', type: 'string',},
  currencyCode: {title: '币种代码',order: 4,view: 'list', type: 'string',dictTable: "mis_currency", dictCode: 'currency_code', dictText: 'currency_name',},
  customerName: {title: '客户名称',order: 5,view: 'popup', type: 'string',code: 'scm_customer', orgFields: 'customer_name', destFields: 'customerName', popupMulti: false,},
  salesmanName: {title: '业务员',order: 7,view: 'text', type: 'string',},
  packageItemId: {title: '包装id',order: 8,view: 'list', type: 'string',dictTable: "mis_material where is_package='1' and del_flag='0'", dictCode: 'id', dictText: 'material_spec',},
  agreedPrice: {title: '价格',order: 9,view: 'number', type: 'number',},
  minQuantity: {title: '最小起订量',order: 10,view: 'number', type: 'number',},
  effectiveFrom: {title: '生效日期',order: 11,view: 'date', type: 'string',},
  effectiveTo: {title: '失效日期',order: 12,view: 'date', type: 'string',},
  pricingType: {title: '定价类型',order: 13,view: 'list', type: 'string',dictCode: 'scm_pricing_type',},
  isActive: {title: '是否启用',order: 14,view: 'list', type: 'string',dictCode: 'yn',},
  remark: {title: '备注',order: 15,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

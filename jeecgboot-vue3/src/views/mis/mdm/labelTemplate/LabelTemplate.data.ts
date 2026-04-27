import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '模板编码',
    align:"center",
    dataIndex: 'templateCode'
   },
   {
    title: '模板名称',
    align:"center",
    dataIndex: 'templateName'
   },
   {
    title: '模板类型',
    align:"center",
    dataIndex: 'templateType_dictText'
   },
   {
    title: '指定产品',
    align:"center",
    dataIndex: 'productId_dictText'
   },
   {
    title: '标签宽度(mm)',
    align:"center",
    dataIndex: 'labelWidth'
   },
   {
    title: '标签高度(mm)',
    align:"center",
    dataIndex: 'labelHeight'
   },
   {
    title: '打印DPI',
    align:"center",
    dataIndex: 'dpi'
   },
   {
    title: '模板元素配置JSON',
    align:"center",
    dataIndex: 'contentJson'
   },
   {
    title: '是否默认模板',
    align:"center",
    dataIndex: 'isDefault_dictText'
   },
   {
    title: '是否系统模板',
    align:"center",
    dataIndex: 'isSystem_dictText'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
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
      label: "模板编码",
      field: 'templateCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "模板名称",
      field: 'templateName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "是否默认模板",
      field: 'isDefault',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"yn"
      },
      //colProps: {span: 6},
 	},
	{
      label: "是否系统模板",
      field: 'isSystem',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"yn"
      },
      //colProps: {span: 6},
 	},
	{
      label: "状态",
      field: 'status',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"status"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '模板编码',
    field: 'templateCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入模板编码!'},
          ];
     },
  },
  {
    label: '模板名称',
    field: 'templateName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入模板名称!'},
          ];
     },
  },
  {
    label: '模板类型',
    field: 'templateType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mdm_label_template_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入模板类型!'},
          ];
     },
  },
  {
    label: '指定产品',
    field: 'productId',
    component: 'Input',
    show:false,
  },
  {
    label: '指定产品',
    field: 'productName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_product_select",
            fieldConfig: [
                { source: 'id', target: 'productId' },
              { source: 'product_name', target: 'productName' },
            ],
            multi:false
        }
    },

  },
  {
    label: '标签宽度(mm)',
    field: 'labelWidth',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入标签宽度(mm)!'},
      ];
    },
  },
  {
    label: '标签高度(mm)',
    field: 'labelHeight',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入标签高度(mm)!'},
      ];
    },
  },
  {
    label: '打印DPI',
    field: 'dpi',
    component: 'InputNumber',
  },
  {
    label: '模板元素配置JSON',
    field: 'contentJson',
    component: 'InputTextArea',
  },
  {
    label: '是否默认模板',
    field: 'isDefault',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入是否默认模板!'},
          ];
     },
  },
  {
    label: '是否系统模板',
    field: 'isSystem',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入是否系统模板!'},
          ];
     },
  },
  {
    label: '状态',
    field: 'status',
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

// 表单数据 - 基础信息部分（Modal左侧用）
// LabelTemplate.data.ts
export const baseFormSchema: FormSchema[] = [
  {
    label: '模板编码',
    field: 'templateCode',
    component: 'Input',
    dynamicRules: () => [{ required: true, message: '请输入模板编码!' }],
    colProps: { span: 8 },
  },
  {
    label: '模板名称',
    field: 'templateName',
    component: 'Input',
    dynamicRules: () => [{ required: true, message: '请输入模板名称!' }],
    colProps: { span: 8 },
  },
  {
    label: '模板类型',
    field: 'templateType',
    component: 'ASelect',
    componentProps: {
      options: [
        { label: '产品标签', value: 'PRODUCT' },
        { label: '库位标签', value: 'LOCATION' },
      ],
    },
    dynamicRules: () => [{ required: true, message: '请选择模板类型!' }],
    colProps: { span: 8 },
  },
  {
    label: '指定产品',
    field: 'productId',
    component: 'Input',
    show: false,
  },
  {
    label: '指定产品',
    field: 'productName',
    component: 'JPopup',
    componentProps: ({ formActionType }: any) => {
      const { setFieldsValue } = formActionType;
      return {
        setFieldsValue,
        code: 'mdm_product_select',
        fieldConfig: [
          { source: 'id', target: 'productId' },
          { source: 'product_name', target: 'productName' },
        ],
        multi: false,
      };
    },
    colProps: { span: 8 },
  },
  {
    label: '宽度(mm)',
    field: 'labelWidth',
    component: 'InputNumber',
    defaultValue: 60,
    dynamicRules: () => [{ required: true, message: '请输入宽度!' }],
    colProps: { span: 8 },
  },
  {
    label: '高度(mm)',
    field: 'labelHeight',
    component: 'InputNumber',
    defaultValue: 35,
    dynamicRules: () => [{ required: true, message: '请输入高度!' }],
    colProps: { span: 8 },
  },
  {
    label: 'DPI',
    field: 'dpi',
    component: 'InputNumber',
    defaultValue: 300,
    colProps: { span: 8 },
  },
  {
    label: '默认',
    field: 'isDefault',
    component: 'JDictSelectTag',
    componentProps: { dictCode: 'yn' },
    defaultValue: '0',
    colProps: { span: 8 },
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: { dictCode: 'status' },
    defaultValue: 'ACTIVE',
    colProps: { span: 8 },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: { rows: 2 },
    colProps: { span: 24 },
  },
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '',
    field: 'contentJson',
    component: 'Input',
    show: false,
  },
];

// 高级查询数据
export const superQuerySchema = {
  templateCode: {title: '模板编码',order: 0,view: 'text', type: 'string',},
  templateName: {title: '模板名称',order: 1,view: 'text', type: 'string',},
  templateType: {title: '模板类型',order: 2,view: 'list', type: 'string',dictCode: 'mdm_label_template_type',},
  productId: {title: '指定产品',order: 3,view: 'popup', type: 'string',code: 'mdm_product_select', orgFields: 'id', destFields: 'productId', popupMulti: false,},
  labelWidth: {title: '标签宽度(mm)',order: 4,view: 'number', type: 'number',},
  labelHeight: {title: '标签高度(mm)',order: 5,view: 'number', type: 'number',},
  dpi: {title: '打印DPI',order: 6,view: 'number', type: 'number',},
  contentJson: {title: '模板元素配置JSON',order: 7,view: 'textarea', type: 'string',},
  isDefault: {title: '是否默认模板',order: 8,view: 'list', type: 'string',dictCode: 'yn',},
  isSystem: {title: '是否系统模板',order: 9,view: 'list', type: 'string',dictCode: 'yn',},
  status: {title: '状态',order: 10,view: 'list', type: 'string',dictCode: 'status',},
  remark: {title: '备注',order: 11,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

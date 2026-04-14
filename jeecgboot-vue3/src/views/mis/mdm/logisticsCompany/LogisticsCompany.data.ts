import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '公司编码',
    align:"center",
    sorter: true,
    dataIndex: 'companyCode'
   },
   {
    title: '公司名称',
    align:"center",
    sorter: true,
    dataIndex: 'companyName'
   },
   {
    title: '公司类型',
    align:"center",
    sorter: true,
    dataIndex: 'companyType_dictText'
   },
   {
    title: '官方全称',
    align:"center",
    dataIndex: 'officialName'
   },
   {
    title: '官网',
    align:"center",
    dataIndex: 'websit'
   },
   {
    title: '查询连接',
    align:"center",
    dataIndex: 'queryUrl'
   },
   {
    title: '客服电话',
    align:"center",
    dataIndex: 'contactPhone'
   },
   {
    title: '排序',
    align:"center",
    sorter: true,
    dataIndex: 'sortOrder'
   },
   {
    title: '状态',
    align:"center",
    sorter: true,
    dataIndex: 'status_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "公司编码",
      field: 'companyCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "公司名称",
      field: 'companyName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "公司类型",
      field: 'companyType',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"wms_logistics_type"
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
    label: '公司编码',
    field: 'companyCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入公司编码!'},
          ];
     },
  },
  {
    label: '公司名称',
    field: 'companyName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入公司名称!'},
          ];
     },
  },
  {
    label: '公司类型',
    field: 'companyType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_logistics_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入公司类型!'},
          ];
     },
  },
  {
    label: '官方全称',
    field: 'officialName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入官方全称!'},
          ];
     },
  },
  {
    label: '官网',
    field: 'websit',
    component: 'Input',
  },
  {
    label: '查询连接',
    field: 'queryUrl',
    component: 'Input',
  },
  {
    label: '客服电话',
    field: 'contactPhone',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入客服电话!'},
          ];
     },
  },
  {
    label: '排序',
    field: 'sortOrder',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入排序!'},
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
  companyCode: {title: '公司编码',order: 0,view: 'text', type: 'string',},
  companyName: {title: '公司名称',order: 1,view: 'text', type: 'string',},
  companyType: {title: '公司类型',order: 2,view: 'list', type: 'string',dictCode: 'wms_logistics_type',},
  officialName: {title: '官方全称',order: 3,view: 'text', type: 'string',},
  websit: {title: '官网',order: 4,view: 'text', type: 'string',},
  queryUrl: {title: '查询连接',order: 5,view: 'text', type: 'string',},
  contactPhone: {title: '客服电话',order: 6,view: 'text', type: 'string',},
  sortOrder: {title: '排序',order: 7,view: 'number', type: 'number',},
  status: {title: '状态',order: 8,view: 'list', type: 'string',dictCode: 'status',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
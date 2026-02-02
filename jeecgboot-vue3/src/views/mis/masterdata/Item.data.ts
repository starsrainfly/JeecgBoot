import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '项目类型',
    align:"center",
    dataIndex: 'itemType_dictText'
   },
   {
    title: '编码',
    align:"center",
    dataIndex: 'code'
   },
   {
    title: '名称',
    align:"center",
    dataIndex: 'name'
   },
   {
    title: '型号规格',
    align:"center",
    dataIndex: 'spec'
   },
   {
    title: '单位',
    align:"center",
    dataIndex: 'unit'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'isActive'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "项目类型",
      field: 'itemType',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"item_type"
      },
      //colProps: {span: 6},
 	},
	{
      label: "编码",
      field: 'code',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "名称",
      field: 'name',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "型号规格",
      field: 'spec',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "状态",
      field: 'isActive',
      component: 'Input',
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '项目类型',
    field: 'itemType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"item_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入项目类型!'},
          ];
     },
  },
  {
    label: '编码',
    field: 'code',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入编码!'},
          ];
     },
  },
  {
    label: '名称',
    field: 'name',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入名称!'},
          ];
     },
  },
  {
    label: '型号规格',
    field: 'spec',
    component: 'Input',
  },
  {
    label: '单位',
    field: 'unit',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'isActive',
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
  itemType: {title: '项目类型',order: 0,view: 'list', type: 'string',dictCode: 'item_type',},
  code: {title: '编码',order: 1,view: 'text', type: 'string',},
  name: {title: '名称',order: 2,view: 'text', type: 'string',},
  spec: {title: '型号规格',order: 3,view: 'text', type: 'string',},
  unit: {title: '单位',order: 4,view: 'text', type: 'string',},
  isActive: {title: '状态',order: 5,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
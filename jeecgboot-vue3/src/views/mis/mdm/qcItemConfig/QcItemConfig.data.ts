import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: 'recipe_spec字段名(驼峰)',
    align:"center",
    sorter: true,
    dataIndex: 'fieldName'
   },
   {
    title: '检验项目名称',
    align:"center",
    sorter: true,
    dataIndex: 'itemName'
   },
   {
    title: '单位',
    align:"center",
    sorter: true,
    dataIndex: 'unit'
   },
   {
    title: '是否默认带出(1是/0否)',
    align:"center",
    sorter: true,
    dataIndex: 'enabled_dictText'
   },
   {
    title: '排序号',
    align:"center",
    sorter: true,
    dataIndex: 'sortNo'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: 'recipe_spec字段名(驼峰)',
    field: 'fieldName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入recipe_spec字段名(驼峰)!'},
          ];
     },
  },
  {
    label: '检验项目名称',
    field: 'itemName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入检验项目名称!'},
          ];
     },
  },
  {
    label: '单位',
    field: 'unit',
    component: 'Input',
  },
  {
    label: '是否默认带出(1是/0否)',
    field: 'enabled',
    defaultValue: "1",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入是否默认带出(1是/0否)!'},
          ];
     },
  },
  {
    label: '排序号',
    field: 'sortNo',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入排序号!'},
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
  fieldName: {title: 'recipe_spec字段名(驼峰)',order: 0,view: 'text', type: 'string',},
  itemName: {title: '检验项目名称',order: 1,view: 'text', type: 'string',},
  unit: {title: '单位',order: 2,view: 'text', type: 'string',},
  enabled: {title: '是否默认带出(1是/0否)',order: 3,view: 'list', type: 'string',dictCode: 'yn',},
  sortNo: {title: '排序号',order: 4,view: 'number', type: 'number',},
  remark: {title: '备注',order: 5,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
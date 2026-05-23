import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '所属模块',
    align:"center",
    dataIndex: 'configModule_dictText'
   },
   {
    title: '配置编码',
    align:"center",
    dataIndex: 'configCode'
   },
   {
    title: '配置名称',
    align:"center",
    dataIndex: 'configName'
   },
   {
    title: '配置值',
    align:"center",
    dataIndex: 'configValue'
   },
   {
    title: '值类型',
    align:"center",
    dataIndex: 'configType_dictText'
   },
   {
    title: '说明',
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
    label: '所属模块',
    field: 'configModule',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_module"
     },
  },
  {
    label: '配置编码',
    field: 'configCode',
    component: 'Input',
  },
  {
    label: '配置名称',
    field: 'configName',
    component: 'Input',
  },
  {
    label: '配置值',
    field: 'configValue',
    component: 'Input',
  },
  {
    label: '值类型',
    field: 'configType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_config_type"
     },
  },
  {
    label: '说明',
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
  configModule: {title: '所属模块',order: 0,view: 'list', type: 'string',dictCode: 'sys_module',},
  configCode: {title: '配置编码',order: 1,view: 'text', type: 'string',},
  configName: {title: '配置名称',order: 2,view: 'text', type: 'string',},
  configValue: {title: '配置值',order: 3,view: 'text', type: 'string',},
  configType: {title: '值类型',order: 4,view: 'list', type: 'string',dictCode: 'sys_config_type',},
  remark: {title: '说明',order: 5,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
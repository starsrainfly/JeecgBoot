import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
// import { rules} from '/@/utils/helper/validator';
// import { render } from '/@/utils/common/renderUtils';
// import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '计量单位',
    align:"center",
    dataIndex: 'unit'
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
      label: "计量单位",
      field: 'unit',
      component: 'Input',
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
    label: '计量单位',
    field: 'unit',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
      //dictCode: "sys_dict_item,item_text,item_value,dict_id='1280401766745718786'"
      dictCode: 'status',
      numberToString: true,
     // placeholder: '请选择状态',
     //  options: [
     //    { label: '正常', value: 1 },
     //    { label: '冻结', value: 2 },
     //  ],
      // type:'radioButton'
     },
    // defaultValue: '1',
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
  unit: {title: '计量单位',order: 0,view: 'text', type: 'string',},
  status: {title: '状态',order: 1,view: 'number', type: 'number',dictCode: 'status',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

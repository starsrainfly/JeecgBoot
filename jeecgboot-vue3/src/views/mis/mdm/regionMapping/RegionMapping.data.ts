import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '类型N国内，W外',
    align:"center",
    sorter: true,
    dataIndex: 'regionType_dictText'
   },
   {
    title: '完整编码',
    align:"center",
    sorter: true,
    dataIndex: 'fullCode'
   },
   {
    title: '简写',
    align:"center",
    sorter: true,
    dataIndex: 'shortCode'
   },
   {
    title: '名称',
    align:"center",
    sorter: true,
    dataIndex: 'regionName'
   },
   {
    title: '排序',
    align:"center",
    sorter: true,
    dataIndex: 'sortNo'
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
      label: "类型N国内，W外",
      field: 'regionType',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"scm_trade_type"
      },
      //colProps: {span: 6},
 	},
	{
      label: "完整编码",
      field: 'fullCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "简写",
      field: 'shortCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "名称",
      field: 'regionName',
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
    label: '类型N国内，W外',
    field: 'regionType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"scm_trade_type"
     },
  },
  {
    label: '完整编码',
    field: 'fullCode',
    component: 'Input',
  },
  {
    label: '简写',
    field: 'shortCode',
    component: 'Input',
  },
  {
    label: '名称',
    field: 'regionName',
    component: 'Input',
  },
  {
    label: '排序',
    field: 'sortNo',
    component: 'InputNumber',
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
  regionType: {title: '类型N国内，W外',order: 0,view: 'list', type: 'string',dictCode: 'scm_trade_type',},
  fullCode: {title: '完整编码',order: 1,view: 'text', type: 'string',},
  shortCode: {title: '简写',order: 2,view: 'text', type: 'string',},
  regionName: {title: '名称',order: 3,view: 'text', type: 'string',},
  sortNo: {title: '排序',order: 4,view: 'number', type: 'number',},
  status: {title: '状态',order: 5,view: 'list', type: 'string',dictCode: 'status',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
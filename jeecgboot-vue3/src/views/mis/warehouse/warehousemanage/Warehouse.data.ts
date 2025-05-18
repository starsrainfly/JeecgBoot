import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '仓库编码',
    align:"center",
    sorter: true,
    dataIndex: 'warehouseCode'
   },
   {
    title: '仓库名称',
    align:"center",
    sorter: true,
    dataIndex: 'name'
   },
   {
    title: '仓库类型',
    align:"center",
    sorter: true,
    dataIndex: 'warehouseType_dictText'
   },
   {
    title: '仓库容量',
    align:"center",
    sorter: true,
    dataIndex: 'capacity'
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
    label: "仓库编码",
    field: "warehouseCode",
    component: 'JInput',
  },
  {
    label: "仓库名称",
    field: "name",
    component: 'JInput',
  },
	{
      label: "仓库类型",
      field: 'warehouseType',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"warehouse_type"
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
    label: '仓库编码',
    field: 'warehouseCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入仓库编码!'},
          ];
     },
  },
  {
    label: '仓库名称',
    field: 'name',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入仓库名称!'},
          ];
     },
  },
  {
    label: '仓库类型',
    field: 'warehouseType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"warehouse_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入仓库类型!'},
          ];
     },
  },
  {
    label: '仓库容量',
    field: 'capacity',
    component: 'InputNumber',
  },
  {
    label: '状态',
    field: 'status',
    defaultValue: "1",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"status"
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
  warehouseCode: {title: '仓库编码',order: 0,view: 'text', type: 'string',},
  name: {title: '仓库名称',order: 1,view: 'text', type: 'string',},
  warehouseType: {title: '仓库类型',order: 2,view: 'list', type: 'string',dictCode: 'warehouse_type',},
  capacity: {title: '仓库容量',order: 3,view: 'number', type: 'number',},
  status: {title: '状态',order: 4,view: 'list', type: 'string',dictCode: 'status',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
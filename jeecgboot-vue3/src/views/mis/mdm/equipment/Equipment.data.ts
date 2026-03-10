import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '设备编号',
    align:"center",
    dataIndex: 'equipmentCode'
   },
   {
    title: '设备名称',
    align:"center",
    dataIndex: 'equipmentName'
   },
   {
    title: '型号',
    align:"center",
    dataIndex: 'model'
   },
   {
    title: '设备类型',
    align:"center",
    dataIndex: 'equipmentType_dictText'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '位置',
    align:"center",
    dataIndex: 'location'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "设备编号",
      field: 'equipmentCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "设备名称",
      field: 'equipmentName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "型号",
      field: 'model',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "设备类型",
      field: 'equipmentType',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mdm_equipment_type"
      },
      //colProps: {span: 6},
 	},
	{
      label: "状态",
      field: 'status',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mdm_equipment_status"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '设备编号',
    field: 'equipmentCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入设备编号!'},
          ];
     },
  },
  {
    label: '设备名称',
    field: 'equipmentName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入设备名称!'},
          ];
     },
  },
  {
    label: '型号',
    field: 'model',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入型号!'},
          ];
     },
  },
  {
    label: '设备类型',
    field: 'equipmentType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mdm_equipment_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入设备类型!'},
          ];
     },
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mdm_equipment_status"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入状态!'},
          ];
     },
  },
  {
    label: '位置',
    field: 'location',
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
  equipmentCode: {title: '设备编号',order: 0,view: 'text', type: 'string',},
  equipmentName: {title: '设备名称',order: 1,view: 'text', type: 'string',},
  model: {title: '型号',order: 2,view: 'text', type: 'string',},
  equipmentType: {title: '设备类型',order: 3,view: 'list', type: 'string',dictCode: 'mdm_equipment_type',},
  status: {title: '状态',order: 4,view: 'list', type: 'string',dictCode: 'mdm_equipment_status',},
  location: {title: '位置',order: 5,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
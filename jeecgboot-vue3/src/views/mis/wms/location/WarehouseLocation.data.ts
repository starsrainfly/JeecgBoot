import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '仓库',
    align:"center",
    dataIndex: 'warehouseId_dictText'
   },
   {
    title: '区域',
    align:"center",
    dataIndex: 'areaId_dictText'
   },
   {
    title: '货架',
    align:"center",
    dataIndex: 'shelfId_dictText'
   },
   {
    title: '库位编码',
    align:"center",
    dataIndex: 'locationCode'
   },
   {
    title: '库位名称',
    align:"center",
    dataIndex: 'name'
   },
   {
    title: '货位类型',
    align:"center",
    dataIndex: 'locationType_dictText'
   },
   {
    title: '长(m)',
    align:"center",
    dataIndex: 'length'
   },
   {
    title: '宽(m)',
    align:"center",
    dataIndex: 'width'
   },
   {
    title: '高(m)',
    align:"center",
    dataIndex: 'height'
   },
   {
    title: '体积(m³)',
    align:"center",
    dataIndex: 'volume'
   },
   {
    title: '描述',
    align:"center",
    dataIndex: 'description'
   },
   {
    title: '是否默认',
    align:"center",
    dataIndex: 'isDefault_dictText'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '组合码',
    align:"center",
    dataIndex: 'pathCode'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "仓库",
      field: 'warehouseId',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mis_warehouse,name,id"
      },
      //colProps: {span: 6},
 	},
	{
      label: "区域",
      field: 'areaId',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mis_warehouse_area,name,id"
      },
      //colProps: {span: 6},
 	},
	{
      label: "货架",
      field: 'shelfId',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mis_warehouse_shelf,name,id"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse,name,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入仓库!'},
          ];
     },
  },
  {
    label: '区域',
    field: 'areaId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_area,name,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入区域!'},
          ];
     },
  },
  {
    label: '货架',
    field: 'shelfId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_shelf,name,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入货架!'},
          ];
     },
  },
  {
    label: '库位编码',
    field: 'locationCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入库位编码!'},
          ];
     },
  },
  {
    label: '库位名称',
    field: 'name',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入库位名称!'},
          ];
     },
  },
  {
    label: '货位类型',
    field: 'locationType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"location_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入货位类型!'},
          ];
     },
  },
  {
    label: '长(m)',
    field: 'length',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入长(m)!'},
          ];
     },
  },
  {
    label: '宽(m)',
    field: 'width',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入宽(m)!'},
          ];
     },
  },
  {
    label: '高(m)',
    field: 'height',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入高(m)!'},
          ];
     },
  },
  {
    label: '体积(m³)',
    field: 'volume',
    component: 'InputNumber',
  },
  {
    label: '描述',
    field: 'description',
    component: 'Input',
  },
  {
    label: '是否默认',
    field: 'isDefault',
    defaultValue: "0",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
  },
  {
    label: '状态',
    field: 'status',
    defaultValue: "1",
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
    label: '组合码',
    field: 'pathCode',
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
  warehouseId: {title: '仓库',order: 0,view: 'list', type: 'string',dictTable: "mis_warehouse", dictCode: 'id', dictText: 'name',},
  areaId: {title: '区域',order: 1,view: 'list', type: 'string',dictTable: "mis_warehouse_area", dictCode: 'id', dictText: 'name',},
  shelfId: {title: '货架',order: 2,view: 'list', type: 'string',dictTable: "mis_warehouse_shelf", dictCode: 'id', dictText: 'name',},
  locationCode: {title: '库位编码',order: 3,view: 'text', type: 'string',},
  name: {title: '库位名称',order: 4,view: 'text', type: 'string',},
  locationType: {title: '货位类型',order: 5,view: 'list', type: 'string',dictCode: 'location_type',},
  length: {title: '长(m)',order: 6,view: 'number', type: 'number',},
  width: {title: '宽(m)',order: 7,view: 'number', type: 'number',},
  height: {title: '高(m)',order: 8,view: 'number', type: 'number',},
  volume: {title: '体积(m³)',order: 9,view: 'number', type: 'number',},
  description: {title: '描述',order: 10,view: 'text', type: 'string',},
  isDefault: {title: '是否默认',order: 11,view: 'list', type: 'string',dictCode: 'yn',},
  status: {title: '状态',order: 12,view: 'list', type: 'string',dictCode: 'status',},
  pathCode: {title: '组合码',order: 13,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
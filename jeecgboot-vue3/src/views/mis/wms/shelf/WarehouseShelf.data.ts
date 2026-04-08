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
    title: '货架编码',
    align:"center",
    dataIndex: 'shelfCode'
   },
   {
    title: '货架名称',
    align:"center",
    dataIndex: 'name'
   },
   {
    title: '描述',
    align:"center",
    dataIndex: 'description'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "仓库",
      field: 'warehouseId',
      component: 'JDictSelectTag',
      componentProps:{
          dictCode:"mis_warehouse,name,id"
      },
      //colProps: {span: 6},
 	},
	{
      label: "区域",
      field: 'areaId',
      component: 'JDictSelectTag',
      // componentProps:{
      //     dictCode:"mis_warehouse_area,name,id"
      // },
      //colProps: {span: 6},
    componentProps: ({ formModel }) => {
      // 根据仓库ID动态加载对应区域
      const warehouseId = formModel?.warehouseId;
      return {
        // 使用 warehouseId 作为 key，变化时强制重新渲染
        key: warehouseId || 'empty',

        // 根据是否有 warehouseId 决定 dictCode
        dictCode: warehouseId
          ? `mis_warehouse_area,name,id,warehouse_id='${warehouseId}'`
          : '',  // 空字符串表示不加载任何数据

        // 未选择仓库时的占位提示
        placeholder: warehouseId ? "请选择区域" : "请先选择仓库",
      };

    },
 	},
	{
      label: "货架编码",
      field: 'shelfCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "货架名称",
      field: 'name',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "描述",
      field: 'description',
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
    componentProps: ({ formModel }) => {
      // 根据仓库ID动态加载对应区域
      const warehouseId = formModel?.warehouseId;
      return {
        // 使用 warehouseId 作为 key，变化时强制重新渲染
        key: warehouseId || 'empty',

        // 根据是否有 warehouseId 决定 dictCode
        dictCode: warehouseId
          ? `mis_warehouse_area,name,id,warehouse_id='${warehouseId}'`
          : '',  // 空字符串表示不加载任何数据

        // 未选择仓库时的占位提示
        placeholder: warehouseId ? "请选择区域" : "请先选择仓库",
      };

    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入区域!'},
      ];
    },
  },
  {
    label: '货架编码',
    field: 'shelfCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入货架编码!'},
          ];
     },
  },
  {
    label: '货架名称',
    field: 'name',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入货架名称!'},
          ];
     },
  },
  {
    label: '描述',
    field: 'description',
    component: 'Input',
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
  shelfCode: {title: '货架编码',order: 2,view: 'text', type: 'string',},
  name: {title: '货架名称',order: 3,view: 'text', type: 'string',},
  description: {title: '描述',order: 4,view: 'text', type: 'string',},
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

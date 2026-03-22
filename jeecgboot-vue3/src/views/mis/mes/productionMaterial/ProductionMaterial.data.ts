import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   // {
   //  title: '订单id',
   //  align:"center",
   //  sorter: true,
   //  dataIndex: 'orderId',
   // },
   {
    title: '生产单号',
    align:"center",
    sorter: true,
    dataIndex: 'orderNo'
   },
  {
    title:'生产批号',
    align:"center",
    sorter:true,
    dataIndex:'batchNo',
    width:160
  },
   {
    title: '物料编码',
    align:"center",
    sorter: true,
    dataIndex: 'materialCode'
   },
   {
    title: '物料名称',
    align:"center",
    dataIndex: 'materialName'
   },
   {
    title: '规格型号',
    align:"center",
    dataIndex: 'materialSpec'
   },
   {
    title: '单位',
    align:"center",
    dataIndex: 'unit'
   },
   {
    title: '需求数量',
    align:"center",
    sorter: true,
    dataIndex: 'requiredQty'
   },
   {
    title: '已发数量',
    align:"center",
    dataIndex: 'issuedQty'
   },
   {
    title: '剩余待发',
    align:"center",
    dataIndex: 'remainingQty'
   },
   {
    title: '需求日期',
    align:"center",
    sorter: true,
    dataIndex: 'requiredDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   // {
   //  title: '优先级',
   //  align:"center",
   //  dataIndex: 'priority'
   // },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '目标仓库',
    align:"center",
    sorter: true,
    dataIndex: 'warehouseId'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "物料编码",
      field: 'materialCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "物料名称",
      field: 'materialName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "规格型号",
      field: 'materialSpec',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "需求日期",
      field: 'requiredDate',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD'
      },
      //colProps: {span: 6},
 	},
	{
      label: "状态",
      field: 'status',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mes_production_material_status"
      },
      //colProps: {span: 6},
 	},
	{
      label: "目标仓库",
      field: 'warehouseId',
      component: 'Input',
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '订单id',
    field: 'orderId',
    component: 'Input',
    show:false
  },
  {
    label: '生产单号',
    field: 'orderNo',
    component: 'Input',
  },
  {
    label: '物料id',
    field: 'materialId',
    component: 'Input',
    show:false
  },
  {
    label: '物料编码',
    field: 'materialCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入物料编码!'},
          ];
     },
    dynamicDisabled:true
  },
  {
    label: '物料名称',
    field: 'materialName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入物料名称!'},
          ];
     },
    dynamicDisabled:true
  },
  {
    label: '规格型号',
    field: 'materialSpec',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入规格型号!'},
          ];
     },
    dynamicDisabled:true
  },
  {
    label: '单位',
    field: 'unit',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入单位!'},
          ];
     },
  },
  {
    label: '需求数量',
    field: 'requiredQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入需求数量!'},
          ];
     },
  },
  {
    label: '已发数量',
    field: 'issuedQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入已发数量!'},
          ];
     },
  },
  {
    label: '剩余待发',
    field: 'remainingQty',
    component: 'InputNumber',
  },
  {
    label: '需求日期',
    field: 'requiredDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  // {
  //   label: '优先级',
  //   field: 'priority',
  //   component: 'InputNumber',
  // },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mes_production_material_status"
     },
  },
  {
    label: '目标仓库',
    field: 'warehouseId',
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
  orderId: {title: '订单id',order: 0,view: 'text', type: 'string',},
  orderNo: {title: '生产单号',order: 1,view: 'text', type: 'string',},
  materialCode: {title: '物料编码',order: 3,view: 'text', type: 'string',},
  materialName: {title: '物料名称',order: 4,view: 'text', type: 'string',},
  materialSpec: {title: '规格型号',order: 5,view: 'text', type: 'string',},
  unit: {title: '单位',order: 6,view: 'text', type: 'string',},
  requiredQty: {title: '需求数量',order: 7,view: 'number', type: 'number',},
  issuedQty: {title: '已发数量',order: 8,view: 'number', type: 'number',},
  remainingQty: {title: '剩余待发',order: 9,view: 'number', type: 'number',},
  requiredDate: {title: '需求日期',order: 10,view: 'date', type: 'string',},
  priority: {title: '优先级',order: 11,view: 'number', type: 'number',},
  status: {title: '状态',order: 12,view: 'list', type: 'string',dictCode: 'mes_production_material_status',},
  warehouseId: {title: '目标仓库',order: 13,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

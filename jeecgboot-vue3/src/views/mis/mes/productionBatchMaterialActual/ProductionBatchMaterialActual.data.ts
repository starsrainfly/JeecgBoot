import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '生产订单号',
    align:"center",
    dataIndex: 'orderNo'
   },
   {
    title: '批次编号',
    align:"center",
    dataIndex: 'batchNo'
   },
   {
    title: '产品编码',
    align:"center",
    dataIndex: 'productCode'
   },
   {
    title: '产品名称',
    align:"center",
    dataIndex: 'productName'
   },
   {
    title: 'bom顺序号',
    align:"center",
    dataIndex: 'bomSerialNo'
   },
   {
    title: '物料编码',
    align:"center",
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
    title: '计划用量(kg)',
    align:"center",
    dataIndex: 'plannedQty'
   },
   {
    title: '实际称重(kg)',
    align:"center",
    dataIndex: 'actualQty'
   },
   {
    title: '操作员',
    align:"center",
    dataIndex: 'operatorName'
   },
   {
    title: '完成时间',
    align:"center",
    dataIndex: 'completeTime'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "生产订单号",
      field: 'orderNo',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "批次编号",
      field: 'batchNo',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "物料编码",
      field: 'materialCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: 'bomid',
    field: 'batchBomId',
    component: 'Input',
  },
  {
    label: '生产订单号',
    field: 'orderNo',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '批次编号',
    field: 'batchNo',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '产品编码',
    field: 'productCode',
    component: 'Input',
  },
  {
    label: '产品名称',
    field: 'productName',
    component: 'Input',
  },
  {
    label: 'bom顺序号',
    field: 'bomSerialNo',
    component: 'InputNumber',
    dynamicDisabled:true
  },
  {
    label: '物料编码',
    field: 'materialCode',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '物料名称',
    field: 'materialName',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '规格型号',
    field: 'materialSpec',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '计划用量(kg)',
    field: 'plannedQty',
    component: 'InputNumber',
    dynamicDisabled:true
  },
  {
    label: '实际称重(kg)',
    field: 'actualQty',
    component: 'InputNumber',
    dynamicDisabled:true
  },
  {
    label: '操作员id',
    field: 'operatorId',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '操作员',
    field: 'operatorName',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '完成时间',
    field: 'completeTime',
    component: 'DatePicker',
    componentProps: {
       showTime: true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
    dynamicDisabled:true
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
  orderNo: {title: '生产订单号',order: 1,view: 'text', type: 'string',},
  batchNo: {title: '批次编号',order: 2,view: 'text', type: 'string',},
  productCode: {title: '产品编码',order: 3,view: 'text', type: 'string',},
  productName: {title: '产品名称',order: 4,view: 'text', type: 'string',},
  bomSerialNo: {title: 'bom顺序号',order: 5,view: 'number', type: 'number',},
  materialCode: {title: '物料编码',order: 6,view: 'text', type: 'string',},
  materialName: {title: '物料名称',order: 7,view: 'text', type: 'string',},
  materialSpec: {title: '规格型号',order: 8,view: 'text', type: 'string',},
  plannedQty: {title: '计划用量(kg)',order: 9,view: 'number', type: 'number',},
  actualQty: {title: '实际称重(kg)',order: 10,view: 'number', type: 'number',},
  operatorName: {title: '操作员',order: 12,view: 'text', type: 'string',},
  completeTime: {title: '完成时间',order: 13,view: 'datetime', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
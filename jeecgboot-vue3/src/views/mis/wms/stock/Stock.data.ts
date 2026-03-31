import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '项目id',
    align:"center",
    dataIndex: 'itemId'
   },
   {
    title: '项目编码',
    align:"center",
    dataIndex: 'itemCode'
   },
   {
    title: '项目名称',
    align:"center",
    dataIndex: 'itemName'
   },
   {
    title: '规格型号',
    align:"center",
    dataIndex: 'specification'
   },
   {
    title: '单位',
    align:"center",
    dataIndex: 'unit'
   },
   {
    title: '项目类型',
    align:"center",
    dataIndex: 'itemType'
   },
   {
    title: '仓库id',
    align:"center",
    dataIndex: 'warehouseId'
   },
   {
    title: '区域id',
    align:"center",
    dataIndex: 'areaId'
   },
   {
    title: '货架id',
    align:"center",
    dataIndex: 'shelfId'
   },
   {
    title: '位置id',
    align:"center",
    dataIndex: 'locationId'
   },
   {
    title: '批次号',
    align:"center",
    dataIndex: 'batchNo'
   },
   {
    title: '过期日（根据shelf_life自动计算）',
    align:"center",
    dataIndex: 'expiryDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '库存数量',
    align:"center",
    dataIndex: 'quantity'
   },
   {
    title: '已分配未出库量',
    align:"center",
    dataIndex: 'lockedQty'
   },
   {
    title: '入库明细id',
    align:"center",
    dataIndex: 'inDetailId'
   },
   {
    title: '辅助单位',
    align:"center",
    dataIndex: 'auxUnit'
   },
   {
    title: '辅助单位数量',
    align:"center",
    dataIndex: 'auxQuantity'
   },
   {
    title: '单位换算率',
    align:"center",
    dataIndex: 'conversionRate'
   },
   {
    title: '供应商id',
    align:"center",
    dataIndex: 'supplierId'
   },
   {
    title: '供应商名称',
    align:"center",
    dataIndex: 'supplierName'
   },
   {
    title: '入库时间',
    align:"center",
    dataIndex: 'stockInTime'
   },
   {
    title: '原始入库数量',
    align:"center",
    dataIndex: 'originalQty'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '项目id',
    field: 'itemId',
    component: 'Input',
  },
  {
    label: '项目编码',
    field: 'itemCode',
    component: 'Input',
  },
  {
    label: '项目名称',
    field: 'itemName',
    component: 'Input',
  },
  {
    label: '规格型号',
    field: 'specification',
    component: 'Input',
  },
  {
    label: '单位',
    field: 'unit',
    component: 'Input',
  },
  {
    label: '项目类型',
    field: 'itemType',
    component: 'Input',
  },
  {
    label: '仓库id',
    field: 'warehouseId',
    component: 'Input',
  },
  {
    label: '区域id',
    field: 'areaId',
    component: 'Input',
  },
  {
    label: '货架id',
    field: 'shelfId',
    component: 'Input',
  },
  {
    label: '位置id',
    field: 'locationId',
    component: 'Input',
  },
  {
    label: '批次号',
    field: 'batchNo',
    component: 'Input',
  },
  {
    label: '过期日（根据shelf_life自动计算）',
    field: 'expiryDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  {
    label: '库存数量',
    field: 'quantity',
    component: 'InputNumber',
  },
  {
    label: '已分配未出库量',
    field: 'lockedQty',
    component: 'InputNumber',
  },
  {
    label: '入库明细id',
    field: 'inDetailId',
    component: 'Input',
  },
  {
    label: '辅助单位',
    field: 'auxUnit',
    component: 'Input',
  },
  {
    label: '辅助单位数量',
    field: 'auxQuantity',
    component: 'InputNumber',
  },
  {
    label: '单位换算率',
    field: 'conversionRate',
    component: 'InputNumber',
  },
  {
    label: '供应商id',
    field: 'supplierId',
    component: 'Input',
  },
  {
    label: '供应商名称',
    field: 'supplierName',
    component: 'Input',
  },
  {
    label: '入库时间',
    field: 'stockInTime',
    component: 'DatePicker',
    componentProps: {
       showTime: true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
  },
  {
    label: '原始入库数量',
    field: 'originalQty',
    component: 'InputNumber',
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
  itemId: {title: '项目id',order: 0,view: 'text', type: 'string',},
  itemCode: {title: '项目编码',order: 1,view: 'text', type: 'string',},
  itemName: {title: '项目名称',order: 2,view: 'text', type: 'string',},
  specification: {title: '规格型号',order: 3,view: 'text', type: 'string',},
  unit: {title: '单位',order: 4,view: 'text', type: 'string',},
  itemType: {title: '项目类型',order: 5,view: 'text', type: 'string',},
  warehouseId: {title: '仓库id',order: 6,view: 'text', type: 'string',},
  areaId: {title: '区域id',order: 7,view: 'text', type: 'string',},
  shelfId: {title: '货架id',order: 8,view: 'text', type: 'string',},
  locationId: {title: '位置id',order: 9,view: 'text', type: 'string',},
  batchNo: {title: '批次号',order: 10,view: 'text', type: 'string',},
  expiryDate: {title: '过期日（根据shelf_life自动计算）',order: 11,view: 'date', type: 'string',},
  quantity: {title: '库存数量',order: 12,view: 'number', type: 'number',},
  lockedQty: {title: '已分配未出库量',order: 13,view: 'number', type: 'number',},
  inDetailId: {title: '入库明细id',order: 14,view: 'text', type: 'string',},
  auxUnit: {title: '辅助单位',order: 15,view: 'text', type: 'string',},
  auxQuantity: {title: '辅助单位数量',order: 16,view: 'number', type: 'number',},
  conversionRate: {title: '单位换算率',order: 17,view: 'number', type: 'number',},
  supplierId: {title: '供应商id',order: 18,view: 'text', type: 'string',},
  supplierName: {title: '供应商名称',order: 19,view: 'text', type: 'string',},
  stockInTime: {title: '入库时间',order: 20,view: 'datetime', type: 'string',},
  originalQty: {title: '原始入库数量',order: 21,view: 'number', type: 'number',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
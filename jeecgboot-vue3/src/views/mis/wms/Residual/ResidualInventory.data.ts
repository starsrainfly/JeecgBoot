import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
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
    title: '仓库',
    align:"center",
    sorter: true,
    dataIndex: 'warehouseId_dictText'
   },
   {
    title: '余料数量',
    align:"center",
    sorter: true,
    dataIndex: 'qty'
   },
   {
    title: '生产订单号',
    align:"center",
    dataIndex: 'productionOrderNo'
   },
   {
    title: '生产批次号',
    align:"center",
    dataIndex: 'productionBatchNo'
   },
   {
    title: '出库单id',
    align:"center",
    dataIndex: 'stockOutId'
   },
   {
    title: '出库明细id',
    align:"center",
    dataIndex: 'stockOutDetailId'
   },
   {
    title: '物料批次号',
    align:"center",
    dataIndex: 'materialBatchNo'
   },
   {
    title: '状态 1可用，2锁定，0耗尽',
    align:"center",
    sorter: true,
    dataIndex: 'status_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '物料id',
    field: 'materialId',
    component: 'Input',
  },
  {
    label: '物料编码',
    field: 'materialCode',
    component: 'Input',
  },
  {
    label: '物料名称',
    field: 'materialName',
    component: 'Input',
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
     },
  },
  {
    label: '余料数量',
    field: 'qty',
    component: 'InputNumber',
  },
  {
    label: '生产订单id',
    field: 'productionOrderId',
    component: 'Input',
  },
  {
    label: '生产订单号',
    field: 'productionOrderNo',
    component: 'Input',
  },
  {
    label: '生产批次id',
    field: 'productionBatchId',
    component: 'Input',
  },
  {
    label: '生产批次号',
    field: 'productionBatchNo',
    component: 'Input',
  },
  {
    label: '出库单id',
    field: 'stockOutId',
    component: 'Input',
  },
  {
    label: '出库明细id',
    field: 'stockOutDetailId',
    component: 'Input',
  },
  {
    label: '物料批次号',
    field: 'materialBatchNo',
    component: 'Input',
  },
  {
    label: '状态 1可用，2锁定，0耗尽',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_residual_status"
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
  materialCode: {title: '物料编码',order: 1,view: 'text', type: 'string',},
  materialName: {title: '物料名称',order: 2,view: 'text', type: 'string',},
  warehouseId: {title: '仓库',order: 3,view: 'list', type: 'string',dictTable: "mis_warehouse where del_flag='0' and status='1'", dictCode: 'id', dictText: 'name',},
  qty: {title: '余料数量',order: 4,view: 'number', type: 'number',},
  productionOrderNo: {title: '生产订单号',order: 6,view: 'text', type: 'string',},
  productionBatchNo: {title: '生产批次号',order: 8,view: 'text', type: 'string',},
  stockOutId: {title: '出库单id',order: 9,view: 'text', type: 'string',},
  stockOutDetailId: {title: '出库明细id',order: 10,view: 'text', type: 'string',},
  materialBatchNo: {title: '物料批次号',order: 11,view: 'text', type: 'string',},
  status: {title: '状态 1可用，2锁定，0耗尽',order: 12,view: 'list', type: 'string',dictCode: 'wms_residual_status',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
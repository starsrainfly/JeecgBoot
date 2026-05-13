import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '上架记录号',
    align:"center",
    dataIndex: 'recordNo'
   },
   // {
   //  title: '原库存ID',
   //  align:"center",
   //  dataIndex: 'stockId'
   // },
   // {
   //  title: '新库存ID',
   //  align:"center",
   //  dataIndex: 'newStockId'
   // },
   {
    title: '来源类型',
    align:"center",
    dataIndex: 'sourceType_dictText'
   },
   {
    title: '来源单号',
    align:"center",
    dataIndex: 'sourceNo'
   },
   // {
   //  title: '物料id',
   //  align:"center",
   //  dataIndex: 'goodsId'
   // },
   {
    title: '物料编码',
    align:"center",
    dataIndex: 'goodsCode'
   },
   {
    title: '名称',
    align:"center",
    dataIndex: 'goodsName'
   },
   {
    title: '批号',
    align:"center",
    dataIndex: 'batchNo'
   },
   {
    title: '原仓库',
    align:"center",
    dataIndex: 'fromWarehouseId_dictText'
   },
   {
    title: '原区域',
    align:"center",
    dataIndex: 'fromAreaId_dictText'
   },
   {
    title: '原货架',
    align:"center",
    dataIndex: 'fromShelfId_dictText'
   },
   {
    title: '原货位',
    align:"center",
    dataIndex: 'fromLocationId_dictText'
   },
   {
    title: '目标仓库',
    align:"center",
    dataIndex: 'toWarehouseId_dictText'
   },
   {
    title: '目标区域',
    align:"center",
    dataIndex: 'toAreaId_dictText'
   },
   {
    title: '目标货架',
    align:"center",
    dataIndex: 'toShelfId_dictText'
   },
   {
    title: '目标货位',
    align:"center",
    dataIndex: 'toLocationId_dictText'
   },
   {
    title: '上架数量',
    align:"center",
    dataIndex: 'shelfQty'
   },
   {
    title: '操作员',
    align:"center",
    dataIndex: 'operatorId_dictText'
   },
   // {
   //  title: '操作人姓名',
   //  align:"center",
   //  dataIndex: 'operatorName'
   // },
   {
    title: '上架时间',
    align:"center",
    dataIndex: 'shelfTime'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
   {
    title: '类型',
    align:"center",
    dataIndex: 'goodsType_dictText'
   },
   {
    title: '规格型号',
    align:"center",
    dataIndex: 'goodsSpec'
   },
   {
    title: '颜色',
    align:"center",
    dataIndex: 'goodsColor'
   },
   {
    title: '是否产品',
    align:"center",
    dataIndex: 'isProduct_dictText'
   },
   {
    title: '单位',
    align:"center",
    dataIndex: 'unit_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '上架记录号',
    field: 'recordNo',
    component: 'Input',
  },
  {
    label: '原库存ID',
    field: 'stockId',
    component: 'Input',
    show:false
  },
  {
    label: '新库存ID',
    field: 'newStockId',
    component: 'Input',
    show:false
  },
  {
    label: '来源类型',
    field: 'sourceType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_stock_in_type"
     },
  },
  {
    label: '来源单号',
    field: 'sourceNo',
    component: 'Input',
  },
  {
    label: '物料id',
    field: 'goodsId',
    component: 'Input',
    show:false
  },
  {
    label: '物料编码',
    field: 'goodsCode',
    component: 'Input',
  },
  {
    label: '名称',
    field: 'goodsName',
    component: 'Input',
  },
  {
    label: '批号',
    field: 'batchNo',
    component: 'Input',
  },
  {
    label: '原仓库',
    field: 'fromWarehouseId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
     },
  },
  {
    label: '原区域',
    field: 'fromAreaId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id"
     },
  },
  {
    label: '原货架',
    field: 'fromShelfId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_shelf where del_flag ='0' and status='1',shelf_code,id"
     },
  },
  {
    label: '原货位',
    field: 'fromLocationId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_location where del_flag='0' and status='1',path_code,id"
     },
  },
  {
    label: '目标仓库',
    field: 'toWarehouseId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
     },
  },
  {
    label: '目标区域',
    field: 'toAreaId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id"
     },
  },
  {
    label: '目标货架',
    field: 'toShelfId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_shelf where del_flag ='0' and status='1',shelf_code,id"
     },
  },
  {
    label: '目标货位',
    field: 'toLocationId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_location where del_flag='0' and status='1',path_code,id"
     },
  },
  {
    label: '上架数量',
    field: 'shelfQty',
    component: 'InputNumber',
  },
  {
    label: '操作员',
    field: 'operatorId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_user where del_flag = '0' and status='1',realname,id"
     },
  },
  // {
  //   label: '操作人姓名',
  //   field: 'operatorName',
  //   component: 'Input',
  // },
  {
    label: '上架时间',
    field: 'shelfTime',
    component: 'DatePicker',
    componentProps: {
       showTime: true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
  {
    label: '类型',
    field: 'goodsType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_item_type"
     },
  },
  {
    label: '规格型号',
    field: 'goodsSpec',
    component: 'Input',
  },
  {
    label: '颜色',
    field: 'goodsColor',
    component: 'Input',
  },
  {
    label: '是否产品',
    field: 'isProduct',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
  },
  {
    label: '单位',
    field: 'unit',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_unit where del_flag='0' and status='1',unit,unit"
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
  recordNo: {title: '上架记录号',order: 0,view: 'text', type: 'string',},
  stockId: {title: '原库存ID',order: 1,view: 'text', type: 'string',},
  newStockId: {title: '新库存ID',order: 2,view: 'text', type: 'string',},
  sourceType: {title: '来源类型',order: 3,view: 'list', type: 'string',dictCode: 'wms_stock_in_type',},
  sourceNo: {title: '来源单号',order: 4,view: 'text', type: 'string',},
  goodsId: {title: '物料id',order: 5,view: 'text', type: 'string',},
  goodsCode: {title: '物料编码',order: 6,view: 'text', type: 'string',},
  goodsName: {title: '名称',order: 7,view: 'text', type: 'string',},
  batchNo: {title: '批号',order: 8,view: 'text', type: 'string',},
  fromWarehouseId: {title: '原仓库',order: 9,view: 'list', type: 'string',dictTable: "mis_warehouse where del_flag='0' and status='1'", dictCode: 'id', dictText: 'name',},
  fromAreaId: {title: '原区域',order: 10,view: 'list', type: 'string',dictTable: "mis_warehouse_area where del_flag='0' and status='1'", dictCode: 'id', dictText: 'area_code',},
  fromShelfId: {title: '原货架',order: 11,view: 'list', type: 'string',dictTable: "mis_warehouse_shelf where del_flag ='0' and status='1'", dictCode: 'id', dictText: 'shelf_code',},
  fromLocationId: {title: '原货位',order: 12,view: 'list', type: 'string',dictTable: "mis_warehouse_location where del_flag='0' and status='1'", dictCode: 'id', dictText: 'path_code',},
  toWarehouseId: {title: '目标仓库',order: 13,view: 'list', type: 'string',dictTable: "mis_warehouse where del_flag='0' and status='1'", dictCode: 'id', dictText: 'name',},
  toAreaId: {title: '目标区域',order: 14,view: 'list', type: 'string',dictTable: "mis_warehouse_area where del_flag='0' and status='1'", dictCode: 'id', dictText: 'area_code',},
  toShelfId: {title: '目标货架',order: 15,view: 'list', type: 'string',dictTable: "mis_warehouse_shelf where del_flag ='0' and status='1'", dictCode: 'id', dictText: 'shelf_code',},
  toLocationId: {title: '目标货位',order: 16,view: 'list', type: 'string',dictTable: "mis_warehouse_location where del_flag='0' and status='1'", dictCode: 'id', dictText: 'path_code',},
  quantity: {title: '上架数量',order: 17,view: 'number', type: 'number',},
  operatorId: {title: 'operatorId',order: 18,view: 'list', type: 'string',dictTable: "sys_user where del_flag = '0' and status='1'", dictCode: 'id', dictText: 'realname',},
  operatorName: {title: '操作人姓名',order: 19,view: 'text', type: 'string',},
  shelfTime: {title: '上架时间',order: 20,view: 'datetime', type: 'string',},
  remark: {title: '备注',order: 21,view: 'text', type: 'string',},
  goodsType: {title: '类型',order: 23,view: 'list', type: 'string',dictCode: 'wms_item_type',},
  goodsSpec: {title: '规格型号',order: 24,view: 'text', type: 'string',},
  goodsColor: {title: '颜色',order: 25,view: 'text', type: 'string',},
  isProduct: {title: '是否产品',order: 26,view: 'list', type: 'string',dictCode: 'yn',},
  unit: {title: '单位',order: 27,view: 'list', type: 'string',dictTable: "mis_unit where del_flag='0' and status='1'", dictCode: 'unit', dictText: 'unit',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '移库记录号',
    align:"center",
    dataIndex: 'moveNo'
   },
   // {
   //  title: '原库存ID',
   //  align:"center",
   //  dataIndex: 'fromStockId'
   // },
   // {
   //  title: '新库存ID',
   //  align:"center",
   //  dataIndex: 'toStockId'
   // },
   // {
   //  title: '物品id',
   //  align:"center",
   //  dataIndex: 'goodsId'
   // },
   {
    title: '物品编码',
    align:"center",
    dataIndex: 'goodsCode'
   },
   {
    title: '物品名称',
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
    title: '数量',
    align:"center",
    dataIndex: 'moveQty'
   },
   {
    title: '移库原因',
    align:"center",
    dataIndex: 'moveReason'
   },
   // {
   //  title: '操作人',
   //  align:"center",
   //  dataIndex: 'operatorId_dictText'
   // },
   {
    title: '操作人姓名',
    align:"center",
    dataIndex: 'operatorName'
   },
   {
    title: '移库时间',
    align:"center",
    dataIndex: 'moveTime'
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
    title: '是否产品',
    align:"center",
    dataIndex: 'isProduct_dictText'
   },
   {
    title: '颜色',
    align:"center",
    dataIndex: 'goodsColor'
   },
   {
    title: '单位',
    align:"center",
    dataIndex: 'unit_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    field: 'fromWarehouseId',
    label: '源仓库',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
    },
    colProps: { span: 6 },
  },
  {
    field: 'fromAreaId',
    label: '源区域',
    component: 'JDictSelectTag',
    // componentProps:{
    //   dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id"
    // },
    componentProps: ({ formModel }) => ({
      key: formModel?.fromWarehouseId || 'empty',
      dictCode: formModel?.fromWarehouseId
        ? `mis_warehouse_area,name,id,warehouse_id='${formModel.fromWarehouseId}'`
        : '',
      placeholder: formModel?.fromWarehouseId ? "请选择区域" : "请先选择仓库",
    }),
    colProps: { span: 6 },
  },
  {
    field: 'goodsCode',
    label: '编码',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'goodsName',
    label: '名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'batchNo',
    label: '批次号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'goodsType',
    label: '物料类型',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'wms_item_type',
    },
    colProps: { span: 6 },
  },
  {
    field: 'isProduct',
    label: '是否产品',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'yn',
    },
    colProps: { span: 6 },
  },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '移库记录号',
    field: 'moveNo',
    component: 'Input',
  },
  {
    label: '原库存ID',
    field: 'fromStockId',
    component: 'Input',
    show:false
  },
  {
    label: '新库存ID',
    field: 'toStockId',
    component: 'Input',
    show:false
  },
  {
    label: '物品id',
    field: 'goodsId',
    component: 'Input',
    show:false
  },
  {
    label: '物品编码',
    field: 'goodsCode',
    component: 'Input',
  },
  {
    label: '物品名称',
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
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入原仓库!'},
          ];
     },
  },
  {
    label: '原区域',
    field: 'fromAreaId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入原区域!'},
          ];
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
    label: '数量',
    field: 'moveQty',
    component: 'InputNumber',
  },
  {
    label: '移库原因',
    field: 'moveReason',
    component: 'Input',
  },
  {
    label: '操作人',
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
    label: '移库时间',
    field: 'moveTime',
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
    label: '是否产品',
    field: 'isProduct',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
  },
  {
    label: '颜色',
    field: 'goodsColor',
    component: 'Input',
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
  moveNo: {title: '移库记录号',order: 0,view: 'text', type: 'string',},
  fromStockId: {title: '原库存ID',order: 1,view: 'text', type: 'string',},
  toStockId: {title: '新库存ID',order: 2,view: 'text', type: 'string',},
  goodsId: {title: '物品id',order: 3,view: 'text', type: 'string',},
  goodsCode: {title: '物品编码',order: 4,view: 'text', type: 'string',},
  goodsName: {title: '物品名称',order: 5,view: 'text', type: 'string',},
  batchNo: {title: '批号',order: 6,view: 'text', type: 'string',},
  fromWarehouseId: {title: '原仓库',order: 7,view: 'list', type: 'string',dictTable: "mis_warehouse where del_flag='0' and status='1'", dictCode: 'id', dictText: 'name',},
  fromAreaId: {title: '原区域',order: 8,view: 'list', type: 'string',dictTable: "mis_warehouse_area where del_flag='0' and status='1'", dictCode: 'id', dictText: 'area_code',},
  fromShelfId: {title: '原货架（AREA级别为NULL）',order: 9,view: 'list', type: 'string',dictTable: "mis_warehouse_shelf where del_flag ='0' and status='1'", dictCode: 'id', dictText: 'shelf_code',},
  fromLocationId: {title: '原货位（AREA/SHELF级别为NULL）',order: 10,view: 'list', type: 'string',dictTable: "mis_warehouse_location where del_flag='0' and status='1'", dictCode: 'id', dictText: 'path_code',},
  toWarehouseId: {title: '目标仓库',order: 11,view: 'list', type: 'string',dictTable: "mis_warehouse where del_flag='0' and status='1'", dictCode: 'id', dictText: 'name',},
  toAreaId: {title: '目标区域',order: 12,view: 'list', type: 'string',dictTable: "mis_warehouse_area where del_flag='0' and status='1'", dictCode: 'id', dictText: 'area_code',},
  toShelfId: {title: '目标货架（AREA级别为NULL）',order: 13,view: 'list', type: 'string',dictTable: "mis_warehouse_shelf where del_flag ='0' and status='1'", dictCode: 'id', dictText: 'shelf_code',},
  toLocationId: {title: '目标货位（AREA/SHELF级别为NULL）',order: 14,view: 'list', type: 'string',dictTable: "mis_warehouse_location where del_flag='0' and status='1'", dictCode: 'id', dictText: 'path_code',},
  quantity: {title: '数量',order: 15,view: 'number', type: 'number',},
  moveReason: {title: '移库原因',order: 16,view: 'text', type: 'string',},
  operatorId: {title: '操作人',order: 17,view: 'list', type: 'string',dictTable: "sys_user where del_flag = '0' and status='1'", dictCode: 'id', dictText: 'realname',},
  operatorName: {title: '操作人姓名',order: 18,view: 'text', type: 'string',},
  moveTime: {title: '移库时间',order: 19,view: 'datetime', type: 'string',},
  remark: {title: '备注',order: 20,view: 'text', type: 'string',},
  goodsType: {title: '类型',order: 22,view: 'list', type: 'string',dictCode: 'wms_item_type',},
  goodsSpec: {title: '规格型号',order: 23,view: 'text', type: 'string',},
  isProduct: {title: '是否产品',order: 24,view: 'list', type: 'string',dictCode: 'yn',},
  goodsColor: {title: '颜色',order: 25,view: 'text', type: 'string',},
  unit: {title: '单位',order: 26,view: 'list', type: 'string',dictTable: "mis_unit where del_flag='0' and status='1'", dictCode: 'unit', dictText: 'unit',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

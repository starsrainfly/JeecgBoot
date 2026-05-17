import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   // {
   //  title: '物料id',
   //  align:"center",
   //  dataIndex: 'goodsId',
   //
   // },
   {
    title: '编码',
    align:"center",
    dataIndex: 'goodsCode'
   },
   {
    title: '名称',
    align:"center",
    dataIndex: 'goodsName'
   },
   {
    title: '规格型号',
    align:"center",
    dataIndex: 'goodsSpec'
   },
   {
    title: '单位',
    align:"center",
    dataIndex: 'unit'
   },
   {
    title: '类型',
    align:"center",
    dataIndex: 'goodsType_dictText'
   },
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
    title: '位置',
    align:"center",
    dataIndex: 'locationId_dictText'
   },
   {
    title: '批次号',
    align:"center",
    dataIndex: 'batchNo'
   },
   {
    title: '生产日期',
    align:"center",
    dataIndex: 'productionDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '质保天数',
    align:"center",
    dataIndex: 'shelfLife'
   },
   {
    title: '过期日',
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
   // {
   //  title: '供应商id',
   //  align:"center",
   //  dataIndex: 'supplierId'
   // },
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
   {
    title: '状态',
    align:"center",
    dataIndex: 'qcStatus_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "编码",
      field: 'goodsCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "名称",
      field: 'goodsName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "规格型号",
      field: 'goodsSpec',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "类型",
      field: 'goodsType',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"wms_item_type"
      },
      //colProps: {span: 6},
 	},
	{
      label: "仓库",
      field: 'warehouseId',
      component: 'JDictSelectTag',
    componentProps: ({ formModel, formActionType }) => ({
      dictCode: "mis_warehouse where del_flag='0' and status='1',name,id",
      // 关键：仓库变更时清空所有下级
      onChange: (value: string) => {
        // 清空区域、货架、货位
        formActionType?.setFieldsValue({
          areaId: undefined,
          shelfId: undefined,
          locationId: undefined,
        });
      },
    }),
      // componentProps:{
      //     dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
      // },
      //colProps: {span: 6},
 	},
	{
      label: "区域",
      field: 'areaId',
      component: 'JDictSelectTag',
      componentProps: ({ formModel, formActionType }) => ({
      key: formModel?.warehouseId || 'empty',
      dictCode: formModel?.warehouseId
        ? `mis_warehouse_area,name,id,del_flag='0' and status='1' and warehouse_id='${formModel.warehouseId}'`
        : '',
      placeholder: formModel?.warehouseId ? "请选择区域" : "请先选择仓库",
        onChange: (value: string) => {
          formActionType?.setFieldsValue({
            shelfId: undefined,
            locationId: undefined,
          });
        },
    }),
      // componentProps:{
      //     dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id"
      // },
      //colProps: {span: 6},
 	},
	{
      label: "货架",
      field: 'shelfId',
      component: 'JDictSelectTag',
     componentProps: ({ formModel, formActionType }) => ({
      key: formModel?.areaId || 'empty',
      dictCode: formModel?.areaId
        ? `mis_warehouse_shelf,name,id,del_flag='0' and status='1' and area_id='${formModel.areaId}'`
        : '',
       placeholder: formModel?.areaId ? "请选择区域" : "请先选择仓库",
       onChange: (value: string) => {
         formActionType?.setFieldsValue({
           locationId: undefined,
         });
       },
     }),
      // componentProps:{
      //     dictCode:"mis_warehouse_shelf where del_flag ='0' and status='1',shelf_code,id"
      // },
      //colProps: {span: 6},
 	},
	{
      label: "位置",
      field: 'locationId',
      component: 'JSelectMultiple',
      componentProps: ({ formModel }) => ({
      key: formModel?.shelfId || 'empty',
      dictCode: formModel?.shelfId
        ? `mis_warehouse_location,name,id,del_flag='0' and status='1' and shelf_id='${formModel.shelfId}'`
        : '',
      placeholder: formModel?.shelfId ? "请选择目标货位" : "请先选择目标货架",
    }),
      // componentProps:{
      //     dictCode:"mis_warehouse_location where del_flag='0' and status='1',path_code,id"
      // },
      //colProps: {span: 6},
 	},
	{
      label: "批次号",
      field: 'batchNo',
      component: 'Input',
      //colProps: {span: 6},
 	},
     {
      label: "生产日期",
      field: "productionDate",
      component: 'RangePicker',
      componentProps: {
        valueType: 'Date',
      },
      //colProps: {span: 6},
	},
     {
      label: "过期日",
      field: "expiryDate",
      component: 'RangePicker',
      componentProps: {
        valueType: 'Date',
      },
      //colProps: {span: 6},
	},
	// {
  //     label: "生产批号id",
  //     field: 'productionBatchId',
  //     component: 'Input',
  //     //colProps: {span: 6},
 	// },
	{
      label: "供应商名称",
      field: 'supplierName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_supplier",
            fieldConfig: [
                { source: 'id', target: 'supplierId' },
                { source: 'supplier_name', target: 'supplierName' },
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '物品id',
    field: 'goodsId',
    component: 'Input',
    show:false,
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入项目id!'},
          ];
     },
  },
  {
    label: '编码',
    field: 'goodsCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入项目编码!'},
          ];
     },
  },
  {
    label: '名称',
    field: 'goodsName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入项目名称!'},
          ];
     },
  },
  {
    label: '规格型号',
    field: 'goodsSpec',
    component: 'Input',
  },
  {
    label: '单位',
    field: 'unit',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:""
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入单位!'},
          ];
     },
  },
  {
    label: '类型',
    field: 'goodsType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_item_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入项目类型!'},
          ];
     },
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入仓库id!'},
          ];
     },
  },
  {
    label: '区域',
    field: 'areaId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id"
     },
  },
  {
    label: '货架',
    field: 'shelfId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_shelf where del_flag ='0' and status='1',shelf_code,id"
     },
  },
  {
    label: '位置',
    field: 'locationId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_location where del_flag='0' and status='1',path_code,id"
     },
  },
  {
    label: '批次号',
    field: 'batchNo',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入批次号!'},
          ];
     },
  },
  {
    label: '生产日期',
    field: 'productionDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入生产日期!'},
          ];
     },
  },
  {
    label: '质保天数',
    field: 'shelfLife',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入质保天数!'},
          ];
     },
  },
  {
    label: '过期日',
    field: 'expiryDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入过期日（根据shelf_life自动计算）!'},
          ];
     },
  },
  {
    label: '库存数量',
    field: 'quantity',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入库存数量!'},
          ];
     },
  },
  {
    label: '已分配未出库量',
    field: 'lockedQty',
    component: 'InputNumber',
  },
  {
    label: '供应商id',
    field: 'supplierId',
    component: 'Input',
    show:false
  },
  {
    label: '供应商名称',
    field: 'supplierName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_supplier",
            fieldConfig: [
                { source: 'id', target: 'supplierId' },
                { source: 'supplier_name', target: 'supplierName' },
            ],
            multi:true
        }
    },

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
  {
    label: '状态',
    field: 'qcStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_stock_status"
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
  goodsId: {title: '项目id',order: 0,view: 'text', type: 'string',},
  goodsCode: {title: '项目编码',order: 1,view: 'text', type: 'string',},
  goodsName: {title: '项目名称',order: 2,view: 'text', type: 'string',},
  goodsSpec: {title: '规格型号',order: 3,view: 'text', type: 'string',},
  unit: {title: '单位',order: 4,view: 'list', type: 'string',dictCode: '',},
  goodsType: {title: '项目类型',order: 5,view: 'list', type: 'string',dictCode: 'wms_item_type',},
  warehouseId: {title: '仓库id',order: 6,view: 'list', type: 'string',dictTable: "mis_warehouse where del_flag='0' and status='1'", dictCode: 'id', dictText: 'name',},
  areaId: {title: '区域id',order: 7,view: 'list', type: 'string',dictTable: "mis_warehouse_area where del_flag='0' and status='1'", dictCode: 'id', dictText: 'area_code',},
  shelfId: {title: '货架id',order: 8,view: 'list', type: 'string',dictTable: "mis_warehouse_shelf where del_flag ='0' and status='1'", dictCode: 'id', dictText: 'shelf_code',},
  locationId: {title: '位置id',order: 9,view: 'list', type: 'string',dictTable: "mis_warehouse_location where del_flag='0' and status='1'", dictCode: 'id', dictText: 'path_code',},
  batchNo: {title: '批次号',order: 10,view: 'text', type: 'string',},
  productionDate: {title: '生产日期',order: 11,view: 'date', type: 'string',},
  shelfLife: {title: '质保天数',order: 12,view: 'number', type: 'number',},
  expiryDate: {title: '过期日（根据shelf_life自动计算）',order: 13,view: 'date', type: 'string',},
  quantity: {title: '库存数量',order: 14,view: 'number', type: 'number',},
  lockedQty: {title: '已分配未出库量',order: 15,view: 'number', type: 'number',},
  supplierId: {title: '供应商id',order: 17,view: 'text', type: 'string',},
  supplierName: {title: '供应商名称',order: 18,view: 'popup', type: 'string',code: 'scm_supplier', orgFields: 'supplier_name', destFields: 'supplierName', popupMulti: false,},
  stockInTime: {title: '入库时间',order: 19,view: 'datetime', type: 'string',},
  originalQty: {title: '原始入库数量',order: 20,view: 'number', type: 'number',},
  qcStatus: {title: '状态',order: 21,view: 'list', type: 'string',dictCode: 'wms_stock_status',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

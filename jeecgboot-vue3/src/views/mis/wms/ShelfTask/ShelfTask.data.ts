// src/views/wms/shelfTask/ShelfTask.data.ts
import { BasicColumn, FormSchema } from '/@/components/Table';

// 获取事件处理函数
const getEvents = () => (window as any).shelfTaskEvents || {};

// 列表数据
export const columns: BasicColumn[] = [
  {
    title: '物料名称',
    align: 'center',
    dataIndex: 'goodsName',
    width: 150,
  },
  {
    title: '物料编码',
    align: 'center',
    dataIndex: 'goodsCode',
    width: 120,
  },
  {
    title: '规格型号',
    align: 'center',
    dataIndex: 'goodsSpec',
    width: 120,
  },
  {
    title: '颜色',
    align: 'center',
    dataIndex: 'goodsColor',
    width: 80,
  },
  {
    title: '批次号',
    align: 'center',
    dataIndex: 'batchNo',
    width: 130,
  },
  {
    title: '库存数量',
    align: 'center',
    dataIndex: 'quantity',
    width: 100,
  },
  {
    title:'锁定库存',
    align:'center',
    dataIndex:'lockedQty',
    width:100
  },
  {
    title: '单位',
    align: 'center',
    dataIndex: 'unit',
    width: 60,
  },
  {
    title: '当前仓库',
    align: 'center',
    dataIndex: 'warehouseId_dictText',
    width: 120,
  },
  {
    title: '当前区域',
    align: 'center',
    dataIndex: 'areaId_dictText',
    width: 120,
  },
  {
    title: '当前货架',
    align: 'center',
    dataIndex: 'shelfId_Text',
    width: 100,
    customRender: ({ text }) => text || '-',
  },
  {
    title: '当前货位',
    align: 'center',
    dataIndex: 'locationId_Text',
    width: 100,
    customRender: ({ text }) => text || '-',
  },
  {
    title: '物料类型',
    align: 'center',
    dataIndex: 'goodsType_dictText',
    width: 100,
  },
  {
    title: '是否产品',
    align: 'center',
    dataIndex: 'isProduct_dictText',
    width: 80,
  },
];

// 查询数据
export const searchFormSchema: FormSchema[] = [
  {
    field: 'warehouseId',
    label: '仓库',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
    },
    colProps: { span: 6 },
  },
  {
    field: 'areaId',
    label: '区域',
    component: 'JDictSelectTag',
    // componentProps:{
    //   dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id"
    // },
    componentProps: ({ formModel }) => ({
      key: formModel?.warehouseId || 'empty',
      dictCode: formModel?.warehouseId
        ? `mis_warehouse_area,name,id,warehouse_id='${formModel.warehouseId}'`
        : '',
      placeholder: formModel?.warehouseId ? "请选择区域" : "请先选择仓库",
    }),
    colProps: { span: 6 },
  },
  {
    field: 'goodsName',
    label: '物料名称',
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

// 上架弹窗表单数据
export const shelfFormSchema: FormSchema[] = [
  {
    label: '库存ID',
    field: 'stockId',
    component: 'Input',
    show: false,
  },
  {
    label: '物料名称',
    field: 'goodsName',
    component: 'Input',
    dynamicDisabled: true,
    colProps: { span: 12 },
  },
  {
    label: '物料编码',
    field: 'goodsCode',
    component: 'Input',
    dynamicDisabled: true,
    colProps: { span: 12 },
  },
  {
    label: '规格型号',
    field: 'goodsSpec',
    component: 'Input',
    dynamicDisabled: true,
    colProps: { span: 12 },
  },
  {
    label: '批次号',
    field: 'batchNo',
    component: 'Input',
    dynamicDisabled: true,
    colProps: { span: 12 },
  },
  {
    label: '当前库存数量',
    field: 'quantity',
    component: 'InputNumber',
    dynamicDisabled: true,
    colProps: { span: 12 },
  },
  {
    label: '单位',
    field: 'unit',
    component: 'Input',
    dynamicDisabled: true,
    colProps: { span: 12 },
  },
  {
    label: '目标仓库',
    field: 'toWarehouseId',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"mis_warehouse where del_flag='0' and status='1',name,id",
      onChange: (val: string) => {
        getEvents().handleWarehouseChange?.(val);
      }
    },
    required: true,
    colProps: { span: 12 },
  },
  {
    label: '目标区域',
    field: 'toAreaId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toWarehouseId || 'empty',
      dictCode: formModel?.toWarehouseId
        ? `mis_warehouse_area,name,id,del_flag='0' and status='1' and warehouse_id='${formModel.toWarehouseId}'`
        : '',
      placeholder: formModel?.toWarehouseId ? "请选择区域" : "请先选择仓库",
      onChange: (val: string) => {
        getEvents().handleAreaChange?.(val);
      }
    }),
    required: true,
    colProps: { span: 12 },
  },
  {
    label: '目标货架',
    field: 'toShelfId',
    component: 'JDictSelectTag',
    // componentProps:{
    //   dictCode:"mis_warehouse_shelf where del_flag ='0' and status='1',shelf_code,id",
    //   onChange: (val: string) => {
    //     getEvents().handleShelfChange?.(val);
    //   }
    // },
    componentProps: ({ formModel }) => ({
      key: formModel?.toAreaId || 'empty',
      dictCode: formModel?.toAreaId
        ? `mis_warehouse_shelf,name,id,del_flag='0' and status='1' and area_id='${formModel.toAreaId}'`
        : '',
      placeholder: formModel?.toAreaId ? "请选择货架" : "请先选择区域",
      onChange: (val: string) => {
        getEvents().handleShelfChange?.(val);
      }
    }),
    required: true,
    colProps: { span: 12 },
  },
  {
    label: '目标货位',
    field: 'toLocationId',
    component: 'JDictSelectTag',
    // componentProps:{
    //   dictCode:"mis_warehouse_location where del_flag='0' and status='1',path_code,id"
    // },
    componentProps: ({ formModel }) => ({
      key: formModel?.toShelfId || 'empty',
      dictCode: formModel?.toShelfId
        ? `mis_warehouse_location,name,id,del_flag='0' and status='1' and shelf_id='${formModel.toShelfId}'`
        : '',
      placeholder: formModel?.toShelfId ? "请选择目标货位" : "请先选择目标货架",
    }),
    colProps: { span: 12 },
  },
  {
    label: '上架数量',
    field: 'shelfQty',
    component: 'InputNumber',
    required: true,
    componentProps: {
      min: 0.001,
      precision: 3,
    },
    colProps: { span: 12 },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    colProps: { span: 24 },
  },
];

// 批量上架弹窗表单（去掉上架数量，增加提示信息）
export const batchShelfFormSchema: FormSchema[] = [
  {
    label: '库存ID',
    field: 'stockId',
    component: 'Input',
    show: false,
  },
  // 批量时不显示库存详情，只显示目标位置
  {
    label: '目标仓库',
    field: 'toWarehouseId',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "mis_warehouse where del_flag='0' and status='1',name,id",
      onChange: (val: string) => {
        getEvents().handleWarehouseChange?.(val);
      }
    },
    required: true,
    colProps: { span: 12 },
  },
  {
    label: '目标区域',
    field: 'toAreaId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toWarehouseId || 'empty',
      dictCode: formModel?.toWarehouseId
        ? `mis_warehouse_area,name,id,warehouse_id='${formModel.toWarehouseId}'`
        : '',
      placeholder: formModel?.toWarehouseId ? "请选择区域" : "请先选择仓库",
      onChange: (val: string) => {
        getEvents().handleAreaChange?.(val);
      }
    }),
    required: true,
    colProps: { span: 12 },
  },
  {
    label: '目标货架',
    field: 'toShelfId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toAreaId || 'empty',
      dictCode: formModel?.toAreaId
        ? `mis_warehouse_shelf,name,id,area_id='${formModel.toAreaId}'`
        : '',
      placeholder: formModel?.toAreaId ? "请选择货架" : "请先选择区域",
      onChange: (val: string) => {
        getEvents().handleShelfChange?.(val);
      }
    }),
    required: true,
    colProps: { span: 12 },
  },
  {
    label: '目标货位',
    field: 'toLocationId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toShelfId || 'empty',
      dictCode: formModel?.toShelfId
        ? `mis_warehouse_location,name,id,shelf_id='${formModel.toShelfId}'`
        : '',
      placeholder: formModel?.toShelfId ? "请选择目标货位" : "请先选择目标货架",
    }),
    colProps: { span: 12 },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    colProps: { span: 24 },
  },
];

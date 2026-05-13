import { FormSchema } from '/@/components/Form';
import { BasicColumn } from '/@/components/Table';

// ==================== 可移库库存表格列 ====================

export const StockMoveTaskColumns: BasicColumn[] = [
  {
    title: '物料编码',
    dataIndex: 'goodsCode',
    width: 120,
  },
  {
    title: '物料名称',
    dataIndex: 'goodsName',
    width: 150,
  },
  {
    title: '规格型号',
    dataIndex: 'goodsSpec',
    width: 120,
  },
  {
    title: '批号',
    dataIndex: 'batchNo',
    width: 120,
  },
  {
    title: '类型',
    dataIndex: 'goodsType_dictText',
    width: 100,

  },
  {
    title: '是否产品',
    dataIndex: 'isProduct_dictText',
    width: 90,

  },
  {
    title: '当前库存',
    dataIndex: 'quantity',
    width: 120,
    customRender: ({ record }) => {
      return `${record.quantity} ${record.unit || ''}`;
    },
  },
  {
    title: '锁定库存',
    dataIndex: 'lockedQty',
    width: 120,
    customRender: ({ record }) => {
      return `${record.lockedQty} ${record.unit || ''}`;
    },
  },
  {
    title: '仓库',
    dataIndex: 'warehouseId_dictText',
    width: 120,

  },
  {
    title: '区域',
    dataIndex: 'areaId_dictText',
    width: 120,

  },
  {
    title: '货架',
    dataIndex: 'shelfId_dictText',
    width: 120,

  },
  {
    title: '货位',
    dataIndex: 'locationId_dictText',
    width: 140,

  },
  {
    title: '颜色',
    dataIndex: 'goodsColor',
    width: 80,
  },
];

// ==================== 查询表单 ====================

export const StockMoveTaskSearchForm: FormSchema[] = [
  {
    field: 'warehouseId',
    label: '仓库',
    component: 'JDictSelectTag',
    componentProps: {
      dict: 'mis_warehouse,name,id,del_flag=0 and status=1',
    },
    colProps: { span: 6 },
  },
  {
    field: 'areaId',
    label: '区域',

    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.warehouseId || 'empty',
      dictCode: formModel?.warehouseId
        ? `mis_warehouse_area,name,id,del_flag='0' and status='1' and warehouse_id='${formModel.warehouseId}'`
        : '',
      placeholder: formModel?.warehouseId ? "请选择区域" : "请先选择仓库",
    }),
    // componentProps: {
    //   dict: 'mis_warehouse_area,area_code,id,del_flag=0 and status=1',
    // },
    colProps: { span: 6 },
  },
  {
    field: 'shelfId',
    label: '货架',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.areaId || 'empty',
      dictCode: formModel?.areaId
        ? `mis_warehouse_shelf,name,id,del_flag='0' and status='1' and area_id='${formModel.areaId}'`
        : '',
      placeholder: formModel?.areaId ? "请选择区域" : "请先选择仓库",
    }),
    // component: 'JSearchSelect',
    // componentProps: {
    //   dict: 'mis_warehouse_shelf,shelf_code,id,del_flag=0 and status=1',
    // },
    colProps: { span: 6 },
  },
  {
    label: "位置",
    field: 'locationId',
    component: 'JSelectMultiple',
    componentProps: ({ formModel }) => ({
      key: formModel?.ShelfId || 'empty',
      dictCode: formModel?.ShelfId
        ? `mis_warehouse_location,name,id,del_flag='0' and status='1' and shelf_id='${formModel.ShelfId}'`
        : '',
      placeholder: formModel?.ShelfId ? "请选择目标货位" : "请先选择目标货架",
    }),
    // componentProps:{
    //     dictCode:"mis_warehouse_location where del_flag='0' and status='1',path_code,id"
    // },
    //colProps: {span: 6},
  },
  {
    field: 'goodsName',
    label: '物料名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'batchNo',
    label: '批号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'goodsType',
    label: '类型',
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

// ==================== 移库表单（弹窗内用） ====================

export const singleMoveFormSchema: FormSchema[] = [
  {
    field: 'fromStockId',
    label: '原库存ID',
    component: 'Input',
    show: false,
  },
  {
    field: 'toWarehouseId',
    label: '目标仓库',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: `mis_warehouse,name,id,del_flag='0' and status='1'`,
    },
    required: true,
    colProps: { span: 12 },
  },
  {
    field: 'toAreaId',
    label: '目标区域',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toWarehouseId || 'empty',
      dictCode: formModel?.toWarehouseId
        ? `mis_warehouse_area,name,id,del_flag='0' and status='1' and warehouse_id='${formModel.toWarehouseId}'`
        : '',
      placeholder: formModel?.toWarehouseId ? "请选择区域" : "请先选择仓库",
    }),
    // componentProps: {
    //   dict: 'mis_warehouse_area,area_code,id,del_flag=0 and status=1',
    // },
    required: true,
    colProps: { span: 12 },
  },
  {
    field: 'toShelfId',
    label: '目标货架',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toAreaId || 'empty',
      dictCode: formModel?.toAreaId
        ? `mis_warehouse_shelf,name,id,del_flag='0' and status='1' and area_id='${formModel.toAreaId}'`
        : '',
      placeholder: formModel?.toAreaId ? "请选择区域" : "请先选择仓库",
    }),
    // componentProps: {
    //   dict: 'mis_warehouse_shelf,shelf_code,id,del_flag=0 and status=1',
    // },
    colProps: { span: 12 },
  },
  {
    field: 'toLocationId',
    label: '目标货位',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toShelfId || 'empty',
      dictCode: formModel?.toShelfId
        ? `mis_warehouse_location,name,id,del_flag='0' and status='1' and shelf_id='${formModel.toShelfId}'`
        : '',
      placeholder: formModel?.toShelfId ? "请选择目标货位" : "请先选择目标货架",
    }),
    // componentProps: {
    //   dict: 'mis_warehouse_location,path_code,id,del_flag=0 and status=1',
    // },
    colProps: { span: 12 },
  },
  {
    field: 'moveQty',
    label: '移库数量',
    component: 'InputNumber',
    componentProps: {
     // min: 0.000001,
      precision: 6,
      style: { width: '100%' },
    },
    required: true,
    colProps: { span: 12 },
  },
  {
    field: 'moveReason',
    label: '移库原因',
    component: 'Input',
    // componentProps: {
    //   dictCode: 'wms_move_reason',
    // },
    colProps: { span: 12 },
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    componentProps: {
      rows: 2,
    },
    colProps: { span: 24 },
  },
];

export const batchMoveFormSchema: FormSchema[] = [
  {
    field: 'fromStockId',
    label: '原库存ID',
    component: 'Input',
    show: false,
  },
  {
    field: 'toWarehouseId',
    label: '目标仓库',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: `mis_warehouse,name,id,del_flag='0' and status='1'`,
    },
    required: true,
    colProps: { span: 12 },
  },
  {
    field: 'toAreaId',
    label: '目标区域',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toWarehouseId || 'empty',
      dictCode: formModel?.toWarehouseId
        ? `mis_warehouse_area,name,id,del_flag='0' and status='1' and warehouse_id='${formModel.toWarehouseId}'`
        : '',
      placeholder: formModel?.toWarehouseId ? "请选择区域" : "请先选择仓库",
    }),
    // componentProps: {
    //   dict: 'mis_warehouse_area,area_code,id,del_flag=0 and status=1',
    // },
    required: true,
    colProps: { span: 12 },
  },
  {
    field: 'toShelfId',
    label: '目标货架',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toAreaId || 'empty',
      dictCode: formModel?.toAreaId
        ? `mis_warehouse_shelf,name,id,del_flag='0' and status='1' and area_id='${formModel.toAreaId}'`
        : '',
      placeholder: formModel?.toAreaId ? "请选择区域" : "请先选择仓库",
    }),
    // componentProps: {
    //   dict: 'mis_warehouse_shelf,shelf_code,id,del_flag=0 and status=1',
    // },
    colProps: { span: 12 },
  },
  {
    field: 'toLocationId',
    label: '目标货位',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.toShelfId || 'empty',
      dictCode: formModel?.toShelfId
        ? `mis_warehouse_location,name,id,del_flag='0' and status='1' and shelf_id='${formModel.toShelfId}'`
        : '',
      placeholder: formModel?.toShelfId ? "请选择目标货位" : "请先选择目标货架",
    }),
    // componentProps: {
    //   dict: 'mis_warehouse_location,path_code,id,del_flag=0 and status=1',
    // },
    colProps: { span: 12 },
  },
  // {
  //   field: 'moveQty',
  //   label: '移库数量',
  //   component: 'InputNumber',
  //   componentProps: {
  //     // min: 0.000001,
  //     precision: 6,
  //     style: { width: '100%' },
  //   },
  //   required: true,
  //   colProps: { span: 12 },
  // },
  {
    field: 'moveReason',
    label: '移库原因',
    component: 'Input',
    // componentProps: {
    //   dictCode: 'wms_move_reason',
    // },
    colProps: { span: 12 },
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    componentProps: {
      rows: 2,
    },
    colProps: { span: 24 },
  },
];

import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';

// 获取事件处理函数
const getEvents = () => (window as any).warehouseLocationEvents || {};

//列表数据
export const columns: BasicColumn[] = [
  { title: '仓库', align:"center", dataIndex: 'warehouseId_dictText' },
  { title: '区域', align:"center", dataIndex: 'areaId_dictText' },
  { title: '货架', align:"center", dataIndex: 'shelfId_dictText' },
  { title: '库位编码', align:"center", dataIndex: 'locationCode' },
  { title: '库位名称', align:"center", dataIndex: 'name' },
  { title: '货位类型', align:"center", dataIndex: 'locationType_dictText' },
  { title: '长(m)', align:"center", dataIndex: 'length' },
  { title: '宽(m)', align:"center", dataIndex: 'width' },
  { title: '高(m)', align:"center", dataIndex: 'height' },
  { title: '体积(m³)', align:"center", dataIndex: 'volume' },
  { title: '描述', align:"center", dataIndex: 'description' },
  { title: '是否默认', align:"center", dataIndex: 'isDefault_dictText' },
  { title: '状态', align:"center", dataIndex: 'status_dictText' },
  { title: '组合码', align:"center", dataIndex: 'pathCode' },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: "仓库",
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps: { dictCode: "mis_warehouse,name,id" },
  },
  {
    label: "区域",
    field: 'areaId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.warehouseId || 'empty',
      dictCode: formModel?.warehouseId
        ? `mis_warehouse_area,name,id,warehouse_id='${formModel.warehouseId}'`
        : '',
      placeholder: formModel?.warehouseId ? "请选择区域" : "请先选择仓库",
    }),
  },
  {
    label: "货架",
    field: 'shelfId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.areaId || 'empty',
      dictCode: formModel?.areaId
        ? `mis_warehouse_shelf,name,id,area_id='${formModel.areaId}'`
        : '',
      placeholder: formModel?.areaId ? "请选择货架" : "请先选择区域",
    }),
  },
];

//表单数据
export const formSchema: FormSchema[] = [
  // 仓库
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "mis_warehouse,name,id",
      onChange: (val: string) => {
        getEvents().handleWarehouseChange?.(val);
      }
    },
    dynamicRules: () => [{ required: true, message: '请输入仓库!' }],
  },

  // 区域
  {
    label: '区域',
    field: 'areaId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.warehouseId || 'empty',
      dictCode: formModel?.warehouseId
        ? `mis_warehouse_area,name,id,warehouse_id='${formModel.warehouseId}'`
        : '',
      placeholder: formModel?.warehouseId ? "请选择区域" : "请先选择仓库",
      onChange: (val: string) => {
        getEvents().handleAreaChange?.(val);
      }
    }),
    dynamicRules: () => [{ required: true, message: '请输入区域!' }],
  },

  // 货架
  {
    label: '货架',
    field: 'shelfId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.areaId || 'empty',
      dictCode: formModel?.areaId
        ? `mis_warehouse_shelf,name,id,area_id='${formModel.areaId}'`
        : '',
      placeholder: formModel?.areaId ? "请选择货架" : "请先选择区域",
      onChange: (val: string) => {
        getEvents().handleShelfChange?.(val);
      }
    }),
    dynamicRules: () => [{ required: true, message: '请输入货架!' }],
  },

  // 库位编码
  {
    label: '库位编码',
    field: 'locationCode',
    component: 'Input',
    componentProps: {
      onChange: (e: any) => {
        getEvents().handleLocationCodeChange?.(e?.target?.value);
      }
    },
    dynamicRules: () => [{ required: true, message: '请输入库位编码!' }],
  },

  // 库位名称
  {
    label: '库位名称',
    field: 'name',
    component: 'Input',
    dynamicRules: () => [{ required: true, message: '请输入库位名称!' }],
  },

  // 货位类型
  {
    label: '货位类型',
    field: 'locationType',
    component: 'JDictSelectTag',
    componentProps: { dictCode: "location_type" },
    dynamicRules: () => [{ required: true, message: '请输入货位类型!' }],
  },

  // 长宽高体积
  {
    label: '长(m)',
    field: 'length',
    component: 'InputNumber',
    dynamicRules: () => [{ required: true, message: '请输入长(m)!' }],
  },
  {
    label: '宽(m)',
    field: 'width',
    component: 'InputNumber',
    dynamicRules: () => [{ required: true, message: '请输入宽(m)!' }],
  },
  {
    label: '高(m)',
    field: 'height',
    component: 'InputNumber',
    dynamicRules: () => [{ required: true, message: '请输入高(m)!' }],
  },
  {
    label: '体积(m³)',
    field: 'volume',
    component: 'InputNumber',
  },

  // 描述
  {
    label: '描述',
    field: 'description',
    component: 'Input',
  },

  // 是否默认
  {
    label: '是否默认',
    field: 'isDefault',
    defaultValue: "0",
    component: 'JDictSelectTag',
    componentProps: { dictCode: "yn" },
  },

  // 状态
  {
    label: '状态',
    field: 'status',
    defaultValue: "1",
    component: 'JDictSelectTag',
    componentProps: { dictCode: "status" },
    dynamicRules: () => [{ required: true, message: '请输入状态!' }],
  },

  // 组合码（只读）
  {
    label: '组合码',
    field: 'pathCode',
    component: 'Input',
    componentProps: {
      readonly: true,
      placeholder: "自动生成，如：A-01-02-001"
    },
  },

  // 隐藏字段
  { label: '', field: 'warehouseCode', component: 'Input', show: false },
  { label: '', field: 'areaCode', component: 'Input', show: false },
  { label: '', field: 'shelfCode', component: 'Input', show: false },
  { label: '', field: 'id', component: 'Input', show: false },
];

// 高级查询数据
export const superQuerySchema = {
  warehouseId: {title: '仓库', order: 0, view: 'list', type: 'string', dictTable: "mis_warehouse", dictCode: 'id', dictText: 'name'},
  areaId: {title: '区域', order: 1, view: 'list', type: 'string', dictTable: "mis_warehouse_area", dictCode: 'id', dictText: 'name'},
  shelfId: {title: '货架', order: 2, view: 'list', type: 'string', dictTable: "mis_warehouse_shelf", dictCode: 'id', dictText: 'name'},
  locationCode: {title: '库位编码', order: 3, view: 'text', type: 'string'},
  name: {title: '库位名称', order: 4, view: 'text', type: 'string'},
  locationType: {title: '货位类型', order: 5, view: 'list', type: 'string', dictCode: 'location_type'},
  length: {title: '长(m)', order: 6, view: 'number', type: 'number'},
  width: {title: '宽(m)', order: 7, view: 'number', type: 'number'},
  height: {title: '高(m)', order: 8, view: 'number', type: 'number'},
  volume: {title: '体积(m³)', order: 9, view: 'number', type: 'number'},
  description: {title: '描述', order: 10, view: 'text', type: 'string'},
  isDefault: {title: '是否默认', order: 11, view: 'list', type: 'string', dictCode: 'yn'},
  status: {title: '状态', order: 12, view: 'list', type: 'string', dictCode: 'status'},
  pathCode: {title: '组合码', order: 13, view: 'text', type: 'string'},
};

export function getBpmFormSchema(_formData): FormSchema[] {
  return formSchema;
}

import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';

// 汇总列表列定义
export const summaryColumns: BasicColumn[] = [
  {
    title: '物料编码',
    align: "center",
    dataIndex: 'goodsCode',
    width: 120,
    fixed: 'left',
  },
  {
    title: '物料名称',
    align: "center",
    dataIndex: 'goodsName',
    width: 150,
    fixed: 'left',
  },
  {
    title: '规格型号',
    align: "center",
    dataIndex: 'goodsSpec',
    width: 120,
  },
  {
    title: '单位',
    align: "center",
    dataIndex: 'unit',
    width: 80,
  },
  {
    title: '项目类型',
    align: "center",
    dataIndex: 'goodsType_dictText',
    width: 100,
  },
  {
    title: '仓库',
    align: "center",
    dataIndex: 'warehouseName',
    width: 120,
  },
  {
    title: '库存情况',
    align: "center",
    dataIndex: 'totalQty',
    width: 180,
    slots: { customRender: 'totalQty' },
  },
  {
    title: '可用库存',
    align: "center",
    dataIndex: 'availableQty',
    width: 100,
    slots: { customRender: 'availableQty' },
  },
  {
    title: '批次数量',
    align: "center",
    dataIndex: 'batchCount',
    width: 100,
    slots: { customRender: 'batchCount' },
  },
  {
    title: '剩余天数',
    align: "center",
    dataIndex: 'remainingDays',
    width: 90,
  },
  {
    title: '效期预警',
    align: "center",
    dataIndex: 'nearestExpiryDate',
    width: 150,
    slots: { customRender: 'nearestExpiryDate' },
  },
  {
    title: '最近入库',
    align: "center",
    dataIndex: 'lastInTime',
    width: 150,
  },
];

// 汇总查询表单
export const summarySearchFormSchema: FormSchema[] = [
  {
    label: "物料编码",
    field: 'goodsCode',
    component: 'Input',
  },
  {
    label: "物料名称",
    field: 'goodsName',
    component: 'Input',
  },
  {
    label: "项目类型",
    field: 'goodsType',
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: "wms_item_type"
    },
  },
  {
    label: "仓库",
    field: 'warehouseId',
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: "mis_warehouse where del_flag='0' and status='1',name,id"
    },
  },
  {
    label: "效期预警",
    field: 'expiryWarning',
    component: 'Select',
    componentProps: {
      options: [
        { label: '全部', value: '' },
        { label: '已过期', value: 'expired' },
        { label: '7天内到期', value: '7' },
        { label: '30天内到期', value: '30' },
      ]
    },
  },
  {
    label: "库存状态",
    field: 'stockStatus',
    component: 'Select',
    componentProps: {
      options: [
        { label: '全部', value: '' },
        { label: '有可用库存', value: 'available' },
        { label: '库存紧张(可用<10%)', value: 'low' },
        { label: '已锁定完', value: 'locked' },
      ]
    },
  },
];

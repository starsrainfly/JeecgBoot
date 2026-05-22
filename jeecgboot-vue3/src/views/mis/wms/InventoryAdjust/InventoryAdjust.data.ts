import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '调整单号',
    align:"center",
    dataIndex: 'adjustNo'
   },
   {
    title: '盘点单号',
    align:"center",
    dataIndex: 'checkNo'
   },
   {
    title: '仓库',
    align:"center",
    dataIndex: 'warehouseId_dictText'
   },
   {
    title: '调整项数',
    align:"center",
    dataIndex: 'totalItems'
   },
   {
    title: '调整总数量',
    align:"center",
    dataIndex: 'totalDiffQty'
   },
   {
    title: '调整总金额',
    align:"center",
    dataIndex: 'totalDiffAmount'
   },
   {
    title: '差异原因汇总',
    align:"center",
    dataIndex: 'reasonSummary'
   },
   {
    title: '审核人',
    align:"center",
    dataIndex: 'approveUserName'
   },
   {
    title: '审核时间',
    align:"center",
    dataIndex: 'approveTime'
   },
   {
    title: '审核备注',
    align:"center",
    dataIndex: 'approveRemark'
   },
   {
    title: '审核状态',
    align:"center",
    dataIndex: 'approveStatus_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "调整单号",
      field: "adjustNo",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "仓库",
      field: "warehouseId",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '调整单号',
    field: 'adjustNo',
    component: 'Input',
  },
  {
    label: '盘点单id',
    field: 'checkId',
    component: 'Input',
  },
  {
    label: '盘点单号',
    field: 'checkNo',
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
    label: '调整项数',
    field: 'totalItems',
    component: 'InputNumber',
  },
  {
    label: '调整总数量',
    field: 'totalDiffQty',
    component: 'InputNumber',
  },
  {
    label: '调整总金额',
    field: 'totalDiffAmount',
    component: 'InputNumber',
  },
  {
    label: '差异原因汇总',
    field: 'reasonSummary',
    component: 'Input',
  },
  {
    label: '审核人id',
    field: 'approveUserId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_user where del_flag='0' and status='1',realname,id"
     },
  },
  {
    label: '审核时间',
    field: 'approveTime',
    component: 'DatePicker',
    componentProps: {
       showTime:true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
  },
  {
    label: '审核备注',
    field: 'approveRemark',
    component: 'Input',
  },
  {
    label: '审核状态',
    field: 'approveStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"approval_status"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入审核状态!'},
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
//子表单数据
//子表表格配置
export const inventoryAdjustDetailColumns: JVxeColumn[] = [
    {
      title: '编码',
      key: 'goodsCode',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '名称',
      key: 'goodsName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '规格',
      key: 'goodsSpec',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '颜色',
      key: 'goodsColor',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '批次号',
      key: 'batchNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '仓库',
      key: 'warehouseId',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mis_warehouse where del_flag='0' and status='1',name,id",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '区域',
      key: 'areaId',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '货架',
      key: 'shelfId',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mis_warehouse_shelf where del_flag='0' and status='1',shelf_code,id",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '库位',
      key: 'locationId',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mis_warehouse_location where del_flag='0' and status='1',location_code,id",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '库位路径',
      key: 'pathCode',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '调整类型',
      key: 'adjustType',
      type: JVxeTypes.select,
      options:[],
      dictCode:"wms_adjust_type",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '调整数量',
      key: 'adjustQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '单位',
      key: 'unit',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mis_unit where del_flag='0' and status='1',unit,unit",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '成本单价',
      key: 'costPrice',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '调整金额',
      key: 'adjustAmount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '调整前库存',
      key: 'beforeQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '调整后库存',
      key: 'afterQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '差异原因',
      key: 'diffReason',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '入库单号',
      key: 'stockInNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '出库单号',
      key: 'stockOutNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  adjustNo: {title: '调整单号',order: 0,view: 'text', type: 'string',},
  checkNo: {title: '盘点单号',order: 2,view: 'text', type: 'string',},
  warehouseId: {title: '仓库',order: 3,view: 'list', type: 'string',dictTable: "mis_warehouse where del_flag='0' and status='1'", dictCode: 'id', dictText: 'name',},
  totalItems: {title: '调整项数',order: 4,view: 'number', type: 'number',},
  totalDiffQty: {title: '调整总数量',order: 5,view: 'number', type: 'number',},
  totalDiffAmount: {title: '调整总金额',order: 6,view: 'number', type: 'number',},
  reasonSummary: {title: '差异原因汇总',order: 7,view: 'text', type: 'string',},
  approveUserName: {title: '审核人',order: 9,view: 'text', type: 'string',},
  approveTime: {title: '审核时间',order: 10,view: 'datetime', type: 'string',},
  approveRemark: {title: '审核备注',order: 11,view: 'text', type: 'string',},
  approveStatus: {title: '审核状态',order: 12,view: 'list', type: 'string',dictCode: 'approval_status',},
  //子表高级查询
  inventoryAdjustDetail: {
    title: '盘库调整单明细表',
    view: 'table',
    fields: {
        goodsCode: {title: '编码',order: 0,view: 'text', type: 'string',},
        goodsName: {title: '名称',order: 1,view: 'text', type: 'string',},
        goodsSpec: {title: '规格',order: 2,view: 'text', type: 'string',},
        goodsColor: {title: '颜色',order: 3,view: 'text', type: 'string',},
        batchNo: {title: '批次号',order: 4,view: 'text', type: 'string',},
        warehouseId: {title: '仓库',order: 5,view: 'list', type: 'string',dictTable: "mis_warehouse where del_flag='0' and status='1'", dictCode: 'id', dictText: 'name',},
        areaId: {title: '区域',order: 6,view: 'list', type: 'string',dictTable: "mis_warehouse_area where del_flag='0' and status='1'", dictCode: 'id', dictText: 'area_code',},
        shelfId: {title: '货架',order: 7,view: 'list', type: 'string',dictTable: "mis_warehouse_shelf where del_flag='0' and status='1'", dictCode: 'id', dictText: 'shelf_code',},
        locationId: {title: '库位',order: 8,view: 'list', type: 'string',dictTable: "mis_warehouse_location where del_flag='0' and status='1'", dictCode: 'id', dictText: 'location_code',},
        pathCode: {title: '库位路径',order: 9,view: 'text', type: 'string',},
        adjustType: {title: '调整类型',order: 10,view: 'list', type: 'string',dictCode: 'wms_adjust_type',},
        adjustQty: {title: '调整数量',order: 11,view: 'number', type: 'number',},
        unit: {title: '单位',order: 12,view: 'list', type: 'string',dictTable: "mis_unit where del_flag='0' and status='1'", dictCode: 'unit', dictText: 'unit',},
        costPrice: {title: '成本单价',order: 13,view: 'number', type: 'number',},
        adjustAmount: {title: '调整金额',order: 14,view: 'number', type: 'number',},
        beforeQty: {title: '调整前库存',order: 15,view: 'number', type: 'number',},
        afterQty: {title: '调整后库存',order: 16,view: 'number', type: 'number',},
        diffReason: {title: '差异原因',order: 17,view: 'text', type: 'string',},
        stockInNo: {title: '入库单号',order: 18,view: 'text', type: 'string',},
        stockOutNo: {title: '出库单号',order: 19,view: 'text', type: 'string',},
    }
  },
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
// 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
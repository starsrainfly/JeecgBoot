import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '核算单号',
    align:"center",
    dataIndex: 'calcNo'
   },
   {
    title: '核算类型:MANUAL手动/MONTHLY月度',
    align:"center",
    dataIndex: 'calcType'
   },
   {
    title: '核算日期',
    align:"center",
    dataIndex: 'calcDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '产品id',
    align:"center",
    dataIndex: 'productId'
   },
   {
    title: '产品编码',
    align:"center",
    dataIndex: 'productCode'
   },
   {
    title: '产品名称',
    align:"center",
    dataIndex: 'productName'
   },
   {
    title: '规格型号',
    align:"center",
    dataIndex: 'productSpec'
   },
   {
    title: '颜色',
    align:"center",
    dataIndex: 'productColor'
   },
   {
    title: '配方id',
    align:"center",
    dataIndex: 'recipeId'
   },
   {
    title: '配方编码',
    align:"center",
    dataIndex: 'recipeCode'
   },
   {
    title: '配方名称',
    align:"center",
    dataIndex: 'recipeName'
   },
   {
    title: '配方版本',
    align:"center",
    dataIndex: 'recipeVersion'
   },
   {
    title: '总配比',
    align:"center",
    dataIndex: 'proportionTotal'
   },
   {
    title: '配比类型',
    align:"center",
    dataIndex: 'proportionType'
   },
   {
    title: '最新成本合计(元/kg)',
    align:"center",
    dataIndex: 'totalCostLatest'
   },
   {
    title: '平均成本合计(元/kg)',
    align:"center",
    dataIndex: 'totalCostAvg'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '核算单号',
    field: 'calcNo',
    component: 'Input',
  },
  {
    label: '核算类型:MANUAL手动/MONTHLY月度',
    field: 'calcType',
    component: 'Input',
  },
  {
    label: '核算日期',
    field: 'calcDate',
    component: 'DatePicker',
    componentProps:{
      valueFormat: 'YYYY-MM-DD'
    },    
  },
  {
    label: '产品id',
    field: 'productId',
    component: 'Input',
  },
  {
    label: '产品编码',
    field: 'productCode',
    component: 'Input',
  },
  {
    label: '产品名称',
    field: 'productName',
    component: 'Input',
  },
  {
    label: '规格型号',
    field: 'productSpec',
    component: 'Input',
  },
  {
    label: '颜色',
    field: 'productColor',
    component: 'Input',
  },
  {
    label: '配方id',
    field: 'recipeId',
    component: 'Input',
  },
  {
    label: '配方编码',
    field: 'recipeCode',
    component: 'Input',
  },
  {
    label: '配方名称',
    field: 'recipeName',
    component: 'Input',
  },
  {
    label: '配方版本',
    field: 'recipeVersion',
    component: 'Input',
  },
  {
    label: '总配比',
    field: 'proportionTotal',
    component: 'InputNumber',
  },
  {
    label: '配比类型',
    field: 'proportionType',
    component: 'Input',
  },
  {
    label: '最新成本合计(元/kg)',
    field: 'totalCostLatest',
    component: 'InputNumber',
  },
  {
    label: '平均成本合计(元/kg)',
    field: 'totalCostAvg',
    component: 'InputNumber',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
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
export const costCalcDetailColumns: JVxeColumn[] = [
    {
      title: '核算主表id',
      key: 'calcId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '核算单号',
      key: 'calcNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '序号',
      key: 'serialNo',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '物料id',
      key: 'materialId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '物料编码',
      key: 'materialCode',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '物料名称',
      key: 'materialName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '规格型号',
      key: 'materialSpec',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '配比',
      key: 'proportion',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '单位',
      key: 'unit',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '价格来源:AVG库存均价/LATEST最新入库价/NONE无',
      key: 'priceSource',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '最新入库价',
      key: 'latestPrice',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '库存均价',
      key: 'avgPrice',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '计算用单价',
      key: 'calcPrice',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '金额',
      key: 'amount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '备注',
      key: 'remark',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  calcNo: {title: '核算单号',order: 0,view: 'text', type: 'string',},
  calcType: {title: '核算类型:MANUAL手动/MONTHLY月度',order: 1,view: 'text', type: 'string',},
  calcDate: {title: '核算日期',order: 2,view: 'date', type: 'string',},
  productId: {title: '产品id',order: 3,view: 'text', type: 'string',},
  productCode: {title: '产品编码',order: 4,view: 'text', type: 'string',},
  productName: {title: '产品名称',order: 5,view: 'text', type: 'string',},
  productSpec: {title: '规格型号',order: 6,view: 'text', type: 'string',},
  productColor: {title: '颜色',order: 7,view: 'text', type: 'string',},
  recipeId: {title: '配方id',order: 8,view: 'text', type: 'string',},
  recipeCode: {title: '配方编码',order: 9,view: 'text', type: 'string',},
  recipeName: {title: '配方名称',order: 10,view: 'text', type: 'string',},
  recipeVersion: {title: '配方版本',order: 11,view: 'text', type: 'string',},
  proportionTotal: {title: '总配比',order: 12,view: 'number', type: 'number',},
  proportionType: {title: '配比类型',order: 13,view: 'text', type: 'string',},
  totalCostLatest: {title: '最新成本合计(元/kg)',order: 14,view: 'number', type: 'number',},
  totalCostAvg: {title: '平均成本合计(元/kg)',order: 15,view: 'number', type: 'number',},
  remark: {title: '备注',order: 16,view: 'text', type: 'string',},
  //子表高级查询
  costCalcDetail: {
    title: '成本核算快照明细',
    view: 'table',
    fields: {
        calcId: {title: '核算主表id',order: 0,view: 'text', type: 'string',},
        calcNo: {title: '核算单号',order: 1,view: 'text', type: 'string',},
        serialNo: {title: '序号',order: 2,view: 'number', type: 'number',},
        materialId: {title: '物料id',order: 3,view: 'text', type: 'string',},
        materialCode: {title: '物料编码',order: 4,view: 'text', type: 'string',},
        materialName: {title: '物料名称',order: 5,view: 'text', type: 'string',},
        materialSpec: {title: '规格型号',order: 6,view: 'text', type: 'string',},
        proportion: {title: '配比',order: 7,view: 'number', type: 'number',},
        unit: {title: '单位',order: 8,view: 'text', type: 'string',},
        priceSource: {title: '价格来源:AVG库存均价/LATEST最新入库价/NONE无',order: 9,view: 'text', type: 'string',},
        latestPrice: {title: '最新入库价',order: 10,view: 'number', type: 'number',},
        avgPrice: {title: '库存均价',order: 11,view: 'number', type: 'number',},
        calcPrice: {title: '计算用单价',order: 12,view: 'number', type: 'number',},
        amount: {title: '金额',order: 13,view: 'number', type: 'number',},
        remark: {title: '备注',order: 14,view: 'text', type: 'string',},
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
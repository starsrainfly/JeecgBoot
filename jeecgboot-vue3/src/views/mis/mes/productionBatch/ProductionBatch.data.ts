import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '订单编号',
    align:"center",
    dataIndex: 'orderNo'
   },
   {
    title: '批次号',
    align:"center",
    dataIndex: 'batchNo'
   },
   {
    title: '序号',
    align:"center",
    dataIndex: 'batchSeq'
   },
   {
    title: '计划生产量(Kg)',
    align:"center",
    dataIndex: 'plannedQty'
   },
   {
    title: '实际生产量(Kg)',
    align:"center",
    dataIndex: 'actualQty'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '生产订单id',
    field: 'orderId',
    component: 'Input',
    show:false
  },
  {
    label: '订单编号',
    field: 'orderNo',
    component: 'Input',
    dynamicDisabled:true
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
    dynamicDisabled:true
  },
  {
    label: '序号',
    field: 'batchSeq',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入序号!'},
          ];
     },
    dynamicDisabled:true
  },
  {
    label: '计划生产量(Kg)',
    field: 'plannedQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划生产量(Kg)!'},
          ];
     },
  },
  {
    label: '实际生产量(Kg)',
    field: 'actualQty',
    component: 'InputNumber',
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mes_batch_status"
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
export const productionBatchBomColumns: JVxeColumn[] = [
    {
      title: '排序号',
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
      visible:false
    },
    {
      title: '物料编码',
      key: 'materialCode',
      type: JVxeTypes.popup,
      popupCode:"mdm_material_select",
      fieldConfig: [
        { source: 'id', target: 'materialId' },
        { source: 'material_code', target: 'materialCode' },
        { source: 'material_name', target: 'materialName' },
        { source: 'material_spec', target: 'materialSpec' },
      ],
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '物料名称',
      key: 'materialName',
      type: JVxeTypes.textarea,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '型号规格',
      key: 'materialSpec',
      type: JVxeTypes.textarea,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '配比',
      key: 'proportion',
      type: JVxeTypes.textarea,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '需求数量',
      key: 'plannedQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  orderNo: {title: '订单编号',order: 1,view: 'text', type: 'string',},
  batchNo: {title: '批次号',order: 2,view: 'text', type: 'string',},
  batchSeq: {title: '序号',order: 3,view: 'number', type: 'number',},
  plannedQty: {title: '计划生产量(Kg)',order: 4,view: 'number', type: 'number',},
  actualQty: {title: '实际生产量(Kg)',order: 5,view: 'number', type: 'number',},
  status: {title: '状态',order: 6,view: 'list', type: 'string',dictCode: 'mes_batch_status',},
  //子表高级查询
  productionBatchBom: {
    title: '生产批次物料清单',
    view: 'table',
    fields: {
        serialNo: {title: '排序号',order: 0,view: 'number', type: 'number',},
        materialId: {title: '物料id',order: 1,view: 'text', type: 'string',},
        materialCode: {title: '物料编码',order: 2,view: 'textarea', type: 'string',},
        materialName: {title: '物料名称',order: 3,view: 'textarea', type: 'string',},
        materialSpec: {title: '型号规格',order: 4,view: 'textarea', type: 'string',},
        proportion: {title: '配比',order: 5,view: 'number', type: 'number',},
        plannedQty: {title: '需求数量',order: 6,view: 'number', type: 'number',},
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

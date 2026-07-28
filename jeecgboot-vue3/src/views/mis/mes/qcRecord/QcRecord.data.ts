import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '质检工单号',
    align:"center",
    dataIndex: 'qcTaskNo'
   },
   {
    title: '来源工单号',
    align:"center",
    dataIndex: 'sourceTaskNo'
   },
   {
    title: '批次号',
    align:"center",
    dataIndex: 'batchNo'
   },
   {
    title: '生产订单号',
    align:"center",
    dataIndex: 'orderNo'
   },
   {
    title: '产品编号',
    align:"center",
    dataIndex: 'productCode'
   },
   {
    title: '产品名称',
    align:"center",
    dataIndex: 'productName'
   },
   {
    title: '质检结果(pass合格/fail不合格/rework返工)',
    align:"center",
    dataIndex: 'qcResult_dictText'
   },
   {
    title: '质检结论',
    align:"center",
    dataIndex: 'qcConclusion'
   },
   {
    title: '检验员id',
    align:"center",
    dataIndex: 'inspectorId_dictText'
   },
   {
    title: '检验时间',
    align:"center",
    dataIndex: 'inspectTime'
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
    label: '质检工单号',
    field: 'qcTaskNo',
    component: 'Input',
  },
  {
    label: '来源工单号',
    field: 'sourceTaskNo',
    component: 'Input',
  },
  {
    label: '批次号',
    field: 'batchNo',
    component: 'Input',
  },
  {
    label: '生产订单号',
    field: 'orderNo',
    component: 'Input',
  },
  {
    label: '产品编号',
    field: 'productCode',
    component: 'Input',
  },
  {
    label: '产品名称',
    field: 'productName',
    component: 'Input',
  },
  {
    label: '质检结果(pass合格/fail不合格/rework返工)',
    field: 'qcResult',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mes_qc_status"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入质检结果(pass合格/fail不合格/rework返工)!'},
          ];
     },
  },
  {
    label: '质检结论',
    field: 'qcConclusion',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入质检结论!'},
          ];
     },
  },
  {
    label: '检验员id',
    field: 'inspectorId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_user where del_flag='0' and status='1',realname,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入检验员id!'},
          ];
     },
  },
  {
    label: '检验时间',
    field: 'inspectTime',
    component: 'DatePicker',
    componentProps: {
       showTime:true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
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
export const qcRecordDetailColumns: JVxeColumn[] = [
    {
      title: '检验项目',
      key: 'itemName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '标准要求',
      key: 'standard',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '实测值',
      key: 'actualValue',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '单项结果(pass合格/fail不合格)',
      key: 'itemResult',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mes_qc_status",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '检测设备编码',
      key: 'equipmentCode',
      type: JVxeTypes.popup,
      popupCode:"mdm_equipment_select",
      fieldConfig: [
        { source: 'id', target: 'equipmentId' },
        { source: 'equipment_code', target: 'equipmentCode' },
        { source: 'equipment_name', target: 'equipmentName' },
      ],

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '检测设备名称',
      key: 'equipmentName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '排序号',
      key: 'sortNo',
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
  qcTaskNo: {title: '质检工单号',order: 0,view: 'text', type: 'string',},
  sourceTaskNo: {title: '来源工单号',order: 1,view: 'text', type: 'string',},
  batchNo: {title: '批次号',order: 2,view: 'text', type: 'string',},
  orderNo: {title: '生产订单号',order: 3,view: 'text', type: 'string',},
  productCode: {title: '产品编号',order: 4,view: 'text', type: 'string',},
  productName: {title: '产品名称',order: 5,view: 'text', type: 'string',},
  qcResult: {title: '质检结果(pass合格/fail不合格/rework返工)',order: 6,view: 'list', type: 'string',dictCode: 'mes_qc_status',},
  qcConclusion: {title: '质检结论',order: 7,view: 'text', type: 'string',},
  inspectorId: {title: '检验员id',order: 8,view: 'list', type: 'string',dictTable: "sys_user where del_flag='0' and status='1'", dictCode: 'id', dictText: 'realname',},
  inspectTime: {title: '检验时间',order: 9,view: 'datetime', type: 'string',},
  remark: {title: '备注',order: 10,view: 'text', type: 'string',},
  //子表高级查询
  qcRecordDetail: {
    title: '质检记录明细',
    view: 'table',
    fields: {
        itemName: {title: '检验项目',order: 0,view: 'text', type: 'string',},
        standard: {title: '标准要求',order: 1,view: 'text', type: 'string',},
        actualValue: {title: '实测值',order: 2,view: 'text', type: 'string',},
        itemResult: {title: '单项结果(pass合格/fail不合格)',order: 3,view: 'list', type: 'string',dictCode: 'mes_qc_status',},
        equipmentCode: {title: '检测设备编码',order: 4,view: 'popup', type: 'string',code: 'mdm_equipment_select', orgFields: 'equipment_code', destFields: 'equipmentCode', popupMulti: false,},
        equipmentName: {title: '检测设备名称',order: 5,view: 'text', type: 'string',},
        sortNo: {title: '排序号',order: 6,view: 'number', type: 'number',},
        remark: {title: '备注',order: 7,view: 'text', type: 'string',},
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
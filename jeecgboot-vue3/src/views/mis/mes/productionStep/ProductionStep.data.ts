import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '批号id',
    align:"center",
    dataIndex: 'batchId'
   },
   {
    title: '批次号',
    align:"center",
    sorter: true,
    dataIndex: 'batchNo'
   },
   {
    title: '生产订单号',
    align:"center",
    sorter: true,
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
    title: '顺序',
    align:"center",
    sorter: true,
    dataIndex: 'stepSeq'
   },
   {
    title: '步骤编号',
    align:"center",
    sorter: true,
    dataIndex: 'stepCode'
   },
   {
    title: '步骤名称',
    align:"center",
    dataIndex: 'stepName'
   },
   {
    title: '操作说明',
    align:"center",
    dataIndex: 'stepDesc'
   },
   {
    title: '计划设备编码',
    align:"center",
    dataIndex: 'equipmentCode'
   },
   {
    title: '计划设备名称',
    align:"center",
    dataIndex: 'equipmentName'
   },
   {
    title: '计划设备型号',
    align:"center",
    dataIndex: 'model'
   },
   {
    title: '计划设备类型',
    align:"center",
    dataIndex: 'equipmentType_dictText'
   },
   {
    title: '计划耗时（单位分钟））',
    align:"center",
    dataIndex: 'duration'
   },
   {
    title: '计划设备设置',
    align:"center",
    dataIndex: 'equipmentSettings'
   },
   {
    title: '实际设备编码',
    align:"center",
    dataIndex: 'actualEquipmentCode'
   },
   {
    title: '实际设备名称',
    align:"center",
    dataIndex: 'actualEquipmentName'
   },
   {
    title: '实际设备型号',
    align:"center",
    dataIndex: 'actualModel'
   },
   {
    title: '实际设备类型',
    align:"center",
    dataIndex: 'actualEquipmentType_dictText'
   },
   {
    title: '实际耗时（分钟）',
    align:"center",
    dataIndex: 'actualDuration'
   },
   {
    title: '实际设备设置',
    align:"center",
    dataIndex: 'actualEquipmentSettings'
   },
   {
    title: '指派操作员',
    align:"center",
    dataIndex: 'assignedOperatorName_dictText'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '实际开始时间',
    align:"center",
    dataIndex: 'actualStart'
   },
   {
    title: '实际结束时间',
    align:"center",
    dataIndex: 'actualEnd'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "批号id",
      field: 'batchId',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "批次号",
      field: 'batchNo',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "产品编号",
      field: 'productCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "产品名称",
      field: 'productName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "步骤名称",
      field: 'stepName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "实际设备编码",
      field: 'actualEquipmentCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_equipment_select",
            fieldConfig: [
                { source: 'id', target: 'actualEquipmentId' },
                { source: 'equipment_code', target: 'actualEquipmentCode' },
                { source: 'equipment_name', target: 'actualEquipmentName' },
                { source: 'model', target: 'actualModel' },
                { source: 'equipment_type', target: 'actualEquipmentType' },
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "实际设备名称",
      field: 'actualEquipmentName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "指派操作员",
      field: 'assignedOperatorName',
      component: 'JSelectUser',
      componentProps:{
      },
      //colProps: {span: 6},
 	},
	{
      label: "状态",
      field: 'status',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mes_step_status"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '批号id',
    field: 'batchId',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '批次号',
    field: 'batchNo',
    component: 'Input',
    dynamicDisabled:true
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
    dynamicDisabled:true
  },
  {
    label: '产品名称',
    field: 'productName',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '顺序',
    field: 'stepSeq',
    component: 'InputNumber',
    dynamicDisabled:true
  },
  {
    label: '步骤编号',
    field: 'stepCode',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '步骤名称',
    field: 'stepName',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '操作说明',
    field: 'stepDesc',
    component: 'InputTextArea',
  },
  {
    label: '计划设备id',
    field: 'equipmentId',
    component: 'Input',
  },
  {
    label: '计划设备编码',
    field: 'equipmentCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_equipment_select",
            fieldConfig: [
                { source: 'id', target: 'equipmentId' },
                { source: 'equipment_code', target: 'equipmentCode' },
                { source: 'equipment_name', target: 'equipmentName' },
                { source: 'model', target: 'model' },
                { source: 'equipment_type', target: 'equipmentType' },
            ],
            multi:true
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划设备编码!'},
          ];
     },
  },
  {
    label: '计划设备名称',
    field: 'equipmentName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划设备名称!'},
          ];
     },
  },
  {
    label: '计划设备型号',
    field: 'model',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划设备型号!'},
          ];
     },
  },
  {
    label: '计划设备类型',
    field: 'equipmentType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mdm_equipment_type"
     },
  },
  {
    label: '计划耗时（单位分钟））',
    field: 'duration',
    component: 'InputNumber',
  },
  {
    label: '计划设备设置',
    field: 'equipmentSettings',
    component: 'InputTextArea',
  },
  {
    label: '实际使用设备id',
    field: 'actualEquipmentId',
    component: 'Input',
  },
  {
    label: '实际设备编码',
    field: 'actualEquipmentCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_equipment_select",
            fieldConfig: [
                { source: 'id', target: 'actualEquipmentId' },
                { source: 'equipment_code', target: 'actualEquipmentCode' },
                { source: 'equipment_name', target: 'actualEquipmentName' },
                { source: 'model', target: 'actualModel' },
                { source: 'equipment_type', target: 'actualEquipmentType' },
            ],
            multi:true
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入实际设备编码!'},
          ];
     },
  },
  {
    label: '实际设备名称',
    field: 'actualEquipmentName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入实际设备名称!'},
          ];
     },
  },
  {
    label: '实际设备型号',
    field: 'actualModel',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入实际设备型号!'},
          ];
     },
  },
  {
    label: '实际设备类型',
    field: 'actualEquipmentType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mdm_equipment_type"
     },
  },
  {
    label: '实际耗时（分钟）',
    field: 'actualDuration',
    component: 'InputNumber',
  },
  {
    label: '实际设备设置',
    field: 'actualEquipmentSettings',
    component: 'Input',
  },
  {
    label: '指派操作员id',
    field: 'assignedOperatorId',
    component: 'Input',
  },
  {
    label: '指派操作员',
    field: 'assignedOperatorName',
    component: 'JSelectUser',
    componentProps:{
    },
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mes_step_status"
     },
  },
  {
    label: '实际开始时间',
    field: 'actualStart',
    component: 'DatePicker',
    componentProps: {
       showTime: true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
  },
  {
    label: '实际结束时间',
    field: 'actualEnd',
    component: 'DatePicker',
    componentProps: {
       showTime: true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
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
  batchId: {title: '批号id',order: 0,view: 'text', type: 'string',},
  batchNo: {title: '批次号',order: 1,view: 'text', type: 'string',},
  orderNo: {title: '生产订单号',order: 2,view: 'text', type: 'string',},
  productCode: {title: '产品编号',order: 3,view: 'text', type: 'string',},
  productName: {title: '产品名称',order: 4,view: 'text', type: 'string',},
  stepSeq: {title: '顺序',order: 5,view: 'number', type: 'number',},
  stepCode: {title: '步骤编号',order: 6,view: 'text', type: 'string',},
  stepName: {title: '步骤名称',order: 7,view: 'text', type: 'string',},
  stepDesc: {title: '操作说明',order: 8,view: 'textarea', type: 'string',},
  equipmentCode: {title: '计划设备编码',order: 10,view: 'popup', type: 'string',code: 'mdm_equipment_select', orgFields: 'equipment_code', destFields: 'equipmentCode', popupMulti: false,},
  equipmentName: {title: '计划设备名称',order: 11,view: 'text', type: 'string',},
  model: {title: '计划设备型号',order: 12,view: 'text', type: 'string',},
  equipmentType: {title: '计划设备类型',order: 13,view: 'list', type: 'string',dictCode: 'mdm_equipment_type',},
  duration: {title: '计划耗时（单位分钟））',order: 14,view: 'number', type: 'number',},
  equipmentSettings: {title: '计划设备设置',order: 15,view: 'textarea', type: 'string',},
  actualEquipmentCode: {title: '实际设备编码',order: 17,view: 'popup', type: 'string',code: 'mdm_equipment_select', orgFields: 'equipment_code', destFields: 'actualEquipmentCode', popupMulti: false,},
  actualEquipmentName: {title: '实际设备名称',order: 18,view: 'text', type: 'string',},
  actualModel: {title: '实际设备型号',order: 19,view: 'text', type: 'string',},
  actualEquipmentType: {title: '实际设备类型',order: 20,view: 'list', type: 'string',dictCode: 'mdm_equipment_type',},
  actualDuration: {title: '实际耗时（分钟）',order: 21,view: 'number', type: 'number',},
  actualEquipmentSettings: {title: '实际设备设置',order: 22,view: 'text', type: 'string',},
  assignedOperatorName: {title: '指派操作员',order: 24,view: 'sel_user', type: 'string',},
  status: {title: '状态',order: 25,view: 'list', type: 'string',dictCode: 'mes_step_status',},
  actualStart: {title: '实际开始时间',order: 26,view: 'datetime', type: 'string',},
  actualEnd: {title: '实际结束时间',order: 27,view: 'datetime', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
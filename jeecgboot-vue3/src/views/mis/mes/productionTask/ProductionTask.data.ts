import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '工单编号',
    align:"center",
    sorter: true,
    dataIndex: 'taskNo'
   },
   {
    title: '工单名称',
    align:"center",
    dataIndex: 'taskName'
   },
   {
    title: '工序',
    align:"center",
    sorter: true,
    dataIndex: 'sequence'
   },
   {
    title: '操作说明',
    align:"center",
    dataIndex: 'taskDesc'
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
    title: '计划设备编码',
    align:"center",
    dataIndex: 'planEquipmentCode'
   },
   {
    title: '计划设备名称',
    align:"center",
    dataIndex: 'planEquipmentName'
   },
   {
    title: '计划设备型号',
    align:"center",
    dataIndex: 'planModel'
   },
   {
    title: '计划设备类型',
    align:"center",
    dataIndex: 'planEquipmentType_dictText'
   },
   {
    title: '计划耗时（单位分钟））',
    align:"center",
    dataIndex: 'planDuration'
   },
   {
    title: '计划设备设置',
    align:"center",
    dataIndex: 'planEquipmentSettings'
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
    title: '指派操作员id',
    align:"center",
    dataIndex: 'assignedOperatorId_dictText'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '实际开始时间',
    align:"center",
    dataIndex: 'actualStartTime'
   },
   {
    title: '实际结束时间',
    align:"center",
    dataIndex: 'actualEndTime'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "工单名称",
      field: 'taskName',
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
      label: "生产订单号",
      field: 'orderNo',
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
      label: "指派操作员id",
      field: 'assignedOperatorId',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"sys_user where del_flag='0' and status='1',realname,id"
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
    label:'id',
    field:'id',
    component: 'Input',
    show:false
  },
  {
    label: '工单编号',
    field: 'taskNo',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '工单名称',
    field: 'taskName',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '工序',
    field: 'sequence',
    component: 'InputNumber',
    dynamicDisabled:true
  },
  {
    label: '操作说明',
    field: 'taskDesc',
    component: 'InputTextArea',
  },
  {
    label: '批次id',
    field: 'batchId',
    component: 'Input',
    show:false
  },
  {
    label: '批次号',
    field: 'batchNo',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '产品id',
    field: 'productId',
    component: 'Input',
    show:false
  },
  {
    label: '生产订单号',
    field: 'orderNo',
    component: 'Input',
    dynamicDisabled:true
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
    label: '计划设备id',
    field: 'planEquipmentId',
    component: 'Input',
    show:false
  },
  {
    label: '任务类型',
    field: 'taskType',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"mes_task_type"
    },
  },
  {
    label: '工艺明细id',
    field: 'routingDetailId',
    component: 'Input',
    show:false
  },
  {
    label: '是否需要质检',
    field: 'qcRequired',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"yn"
    },
  },
  {
    label: '计划设备编码',
    field: 'planEquipmentCode',
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
            multi:false
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
    field: 'planEquipmentName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划设备名称!'},
          ];
     },
  },
  {
    label: '计划设备型号',
    field: 'planModel',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划设备型号!'},
          ];
     },
  },
  {
    label: '计划设备类型',
    field: 'planEquipmentType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mdm_equipment_type"
     },
  },
  {
    label: '计划耗时（单位分钟））',
    field: 'planDuration',
    component: 'InputNumber',
  },
  {
    label: '计划设备设置',
    field: 'planEquipmentSettings',
    component: 'InputTextArea',
  },
  {
    label: '实际设备id',
    field: 'actualEquipmentId',
    component: 'Input',
    show:false
  },
  {
    label: '实际设备编码',
    field: 'actualEquipmentCode',
    component: 'JPopup',
    componentProps: ({formActionType}) => {
      const {setFieldsValue} = formActionType;
      return {
        setFieldsValue: setFieldsValue,
        code: "mdm_equipment_select",
        fieldConfig: [
          {source: 'id', target: 'actualEquipmentId'},
          {source: 'equipment_code', target: 'actualEquipmentCode'},
          {source: 'equipment_name', target: 'actualEquipmentName'},
          {source: 'model', target: 'actualModel'},
          {source: 'equipment_type', target: 'equipmentType'},
        ],
        multi: false
      }
    },
    // dynamicRules: ({model,schema}) => {
    //   return [
    //     { required: true, message: '请输入实际设备编码!'},
    //   ];
    // },
  },
  {
    label: '实际设备名称',
    field: 'actualEquipmentName',
    component: 'Input',
    // dynamicRules: ({model,schema}) => {
    //   return [
    //     { required: true, message: '请输入实际设备名称!'},
    //   ];
    // },
  },
  {
    label: '实际设备型号',
    field: 'actualModel',
    component: 'Input',
    // dynamicRules: ({model,schema}) => {
    //   return [
    //     { required: true, message: '请输入实际设备型号!'},
    //   ];
    // },
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
    label: '实际耗时（单位分钟））',
    field: 'actualDuration',
    component: 'InputNumber',
  },
  {
    label: '实际设备设置',
    field: 'actualEquipmentSettings',
    component: 'InputTextArea',
  },
  {
    label: '指派操作员',
    field: 'assignedOperatorId',
    component:'JDictSelectTag',
    componentProps:{
      dictCode:"sys_user where del_flag='0' and status='1',realname,id"
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请选择指派操作员!'},
      ];
    },
  },
  {
    label: '状态',
    field: 'status',
    component:'JDictSelectTag',
    componentProps:{
      dictCode:"mes_step_status"
    },
  },

  ]
// // 高级查询数据
export const superQuerySchema = {
  orderNo: {title: '生产编号',order: 0,view: 'text', type: 'string',},
  // productCode: {title: '产品编码',order: 1,view: 'popup', type: 'string',code: 'mdm_product_select', orgFields: 'product_code', destFields: 'productCode', popupMulti: false,},
  // productName: {title: '产品名称',order: 2,view: 'text', type: 'string',},

  status: {title: '状态',order: 16,view: 'list', type: 'string',dictCode: 'mes_step_status',},

};

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[]{
// 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

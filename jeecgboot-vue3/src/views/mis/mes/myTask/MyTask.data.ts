// MyTask.data.ts

import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';


export const columns: BasicColumn[] = [
  {
    title: '工单编号',
    dataIndex: 'taskNo',
    width: 140,
    fixed: 'left',
  },
  {
    title: '工单类型',
    dataIndex: 'taskType_dictText',
    width: 80,
    // customRender: ({ text }) => {
    //   const config = taskTypeMap[text] || { label: text, color: 'default' };
    //   return h(
    //     Tag,
    //     {
    //       color: config.color,
    //       style: 'margin: 0',
    //     },
    //     { default: () => config.label }
    //   );
    // },
  },
  {
    title: '工单名称',
    dataIndex: 'taskName',
    width: 160,
    ellipsis: true,
  },
  {
    title: '批次号',
    dataIndex: 'batchNo',
    width: 140,
  },
  {
    title: '产品名称',
    dataIndex: 'productName',
    width: 140,
    ellipsis: true,
  },
  {
    title: '工序',
    dataIndex: 'sequence',
    width: 60,
    align: 'center',
  },
  {
    title: '计划设备',
    dataIndex: 'planEquipmentName',
    width: 120,
    ellipsis: true,
  },
  {
    title: '状态',
    dataIndex: 'status_dictText',
    width: 90,

  },
  {
    title: '计划开始',
    dataIndex: 'planStartTime',
    width: 140,
  },
  {
    title: '实际开始',
    dataIndex: 'actualStartTime',
    width: 140,
  },
  {
    title: '实际完工',
    dataIndex: 'actualEndTime',
    width: 140,
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
  },
  {
    label: '状态',
    field: 'status',
    component:'JDictSelectTag',
    componentProps:{
      dictCode:"mes_step_status"
    },
  },

];

export const ProductionExecuteformSchema: FormSchema[] =[
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
    dynamicDisabled:true,
    componentProps:{
      readonly:true,
    }
  },
  {
    label: '工单名称',
    field: 'taskName',
    component: 'Input',
    dynamicDisabled:true,
    componentProps:{
      readonly:true,
    }
  },
  {
    label: '工序',
    field: 'sequence',
    component: 'InputNumber',
    componentProps:{
      readonly:true,
    },
    dynamicDisabled:true
  },
  {
    label: '操作说明',
    field: 'taskDesc',
    component: 'InputTextArea',
    componentProps:{
      readonly:true,
    },
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
    componentProps:{
      readonly:true,
    },
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
    componentProps:{
      readonly:true,
    },
    dynamicDisabled:true
  },
  {
    label: '产品编号',
    field: 'productCode',
    component: 'Input',
    componentProps:{
      readonly:true,
    },
    dynamicDisabled:true
  },
  {
    label: '产品名称',
    field: 'productName',
    component: 'Input',
    componentProps:{
      readonly:true,
    },
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
      dictCode:"mes_task_type",
      readonly:true,
      disabled:true,
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
      dictCode:"yn",
      readonly: true,
      disabled:true,
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
        multi:false,
        readonly:true,
        disabled:true,
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
    componentProps:{
      readonly:true,
    },
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
    componentProps:{
      readonly:true,
    },
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
      dictCode:"mdm_equipment_type",
      readonly: true,
      disabled: true,
    },
  },
  {
    label: '计划耗时（单位分钟））',
    field: 'planDuration',
    component: 'InputNumber',
    componentProps:{
      readonly: true,
    }
  },
  {
    label: '计划设备设置',
    field: 'planEquipmentSettings',
    component: 'InputTextArea',
    componentProps:{
      readonly: true
    }
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
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入实际设备类型!'},
      ];
    },
  },
  {
    label: '实际耗时（单位分钟））',
    field: 'actualDuration',
    component: 'InputNumber',
    componentProps:{
      readonly: true
    }
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
      dictCode:"sys_user where del_flag='0' and status='1',realname,id",
      disabled:true,
    },

  },
  {
    label: '状态',
    field: 'status',
    component:'JDictSelectTag',
    componentProps:{
      dictCode:"mes_step_status"
    },
    show:false
  },
];

// 查询表单 (仿照 ProductionTask 写法)
export const searchFormSchema: FormSchema[] = [
  {
    label: '工单编号',
    field: 'taskNo',
    component: 'Input',
  },
  {
    label: '工单类型',
    field: 'taskType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'mes_task_type', // 请根据实际字典Code修改
    },
  },
  {
    label: '批次号',
    field: 'batchNo',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'mes_step_status', // 请根据实际字典Code修改
    },
  },
];

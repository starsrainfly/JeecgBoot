import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '工艺编码',
    align:"center",
    dataIndex: 'routingCode'
   },
   {
    title: '工艺名称',
    align:"center",
    dataIndex: 'routingName'
   },
   {
    title: '版本',
    align:"center",
    dataIndex: 'version'
   },
   {
    title: '是否启用',
    align:"center",
    dataIndex: 'isActive_dictText'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "工艺编码",
      field: "routingCode",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "工艺名称",
      field: "routingName",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "版本",
      field: "version",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "是否启用",
      field: "isActive",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"yn"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '工艺编码',
    field: 'routingCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入工艺编码!'},
          ];
     },
  },
  {
    label: '工艺名称',
    field: 'routingName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入工艺名称!'},
          ];
     },
  },
  {
    label: '版本',
    field: 'version',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入版本!'},
          ];
     },
  },
  {
    label: '是否启用',
    field: 'isActive',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入是否启用!'},
          ];
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
export const processRoutingStepColumns: JVxeColumn[] = [
    {
      title: '工序顺序',
      key: 'stepSeq',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '工序编码',
      key: 'stepCode',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '工序名称',
      key: 'stepName',
      type: JVxeTypes.textarea,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '操作要求',
      key: 'stepDesc',
      type: JVxeTypes.textarea,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '设备编码',
      key: 'equipmentCode',
      type: JVxeTypes.popup,
      popupCode:"mdm_equipment_select",
      fieldConfig: [
        { source: 'id', target: 'equipmentId' },
        { source: 'equipment_code', target: 'equipmentCode' },
        { source: 'equipment_name', target: 'equipmentName' },
        { source: 'model', target: 'model' },
        { source: 'equipment_type', target: 'equipmentType' },
      ],

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '设备名称',
      key: 'equipmentName',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '型号',
      key: 'model',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '设备类型',
      key: 'equipmentType',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '设备设置',
      key: 'equipmentSettings',
      type: JVxeTypes.textarea,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '标准耗时（单位：分）',
      key: 'duration',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '所需设备数量',
      key: 'requiredEquipmentCount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:1,
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '是否配料',
      key: 'isMaterialStep',
      type: JVxeTypes.select,
      options:[],
      dictCode:"yn",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
  {
    title: '是否最后完工工序',
    key: 'isFinishStep',
    type: JVxeTypes.select,
    options:[],
    dictCode:"yn",
    width:"200px",
    placeholder: '请输入${title}',
    defaultValue:'',
    validateRules: [
      { required: true, message: '${title}不能为空' },
    ],
    // 校验：一个工艺只能有一个 is_finish_step=1
  },
    {
      title: '是否需要质检',
      key: 'qcRequired',
      type: JVxeTypes.select,
      options:[],
      dictCode:"yn",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  routingCode: {title: '工艺编码',order: 0,view: 'text', type: 'string',},
  routingName: {title: '工艺名称',order: 1,view: 'text', type: 'string',},
  version: {title: '版本',order: 2,view: 'text', type: 'string',},
  isActive: {title: '是否启用',order: 3,view: 'list', type: 'string',dictCode: 'yn',},
  remark: {title: '备注',order: 4,view: 'text', type: 'string',},
  //子表高级查询
  processRoutingStep: {
    title: '工序步骤',
    view: 'table',
    fields: {
        stepSeq: {title: '工序顺序',order: 0,view: 'number', type: 'number',},
        stepCode: {title: '工序编码',order: 1,view: 'text', type: 'string',},
        stepName: {title: '工序名称',order: 2,view: 'text', type: 'string',},
        stepDesc: {title: '操作要求',order: 3,view: 'text', type: 'string',},
        equipmentId: {title: '设备id',order: 4,view: 'text', type: 'string',},
        equipmentCode: {title: '设备编码',order: 5,view: 'popup', type: 'string',code: 'mdm_equipment_select', orgFields: 'equipment_code', destFields: 'equipmentCode', popupMulti: false,},
        equipmentName: {title: '设备名称',order: 6,view: 'text', type: 'string',},
        model: {title: '型号',order: 7,view: 'text', type: 'string',},
        equipmentType: {title: '设备类型',order: 8,view: 'text', type: 'string',},
        equipmentSettings: {title: '设备设置',order: 9,view: 'text', type: 'string',},
        duration: {title: '标准耗时（单位：分）',order: 10,view: 'number', type: 'number',},
        requiredEquipmentCount: {title: '所需设备数量',order: 11,view: 'number', type: 'number',},
        isMaterialStep: {title: '是否配料',order: 12,view: 'text', type: 'string',},
        qcRequired: {title: '是否需要质检',order: 13,view: 'list', type: 'string',dictCode: 'yn',},
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

import {BasicColumn, FormSchema} from '/@/components/Table';
import {JVxeColumn, JVxeTypes} from '/@/components/jeecg/JVxeTable/types'
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '配方编号',
    align:"center",
    dataIndex: 'recipeCode'
   },
   {
    title: '配方名称',
    align:"center",
    dataIndex: 'recipeName'
   },
   {
    title: '颜色',
    align:"center",
    dataIndex: 'color'
   },
   {
    title: '版本',
    align:"center",
    dataIndex: 'version'
   },
  {
    title: '工艺名称',
    align:"center",
    dataIndex: 'routingName',

  },
  {
    title: '工艺版本',
    align:"center",
    dataIndex: 'routingVersion',
  },
   {
    title:'是否发布',
     align:"center",
     dataIndex:'publishStatus_dictText'
   },
   {
    title: '技术要求',
    align:"center",
    dataIndex: 'technics'
   },
   {
    title: '主配人',
    align:"center",
    dataIndex: 'formulatorFirst'
   },
   {
    title: '副配人',
    align:"center",
    dataIndex: 'formulatorSecond'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
   {
    title: '注意事项',
    align:"center",
    dataIndex: 'notes'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
  {
    title:'发布人',
    align:"center",
    dataIndex:'publishBy'
  },
  {
    title:'发布日期',
    align:"center",
    dataIndex:'publishTime'
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: "配方编号",
    field: "recipeCode",
    component: 'JInput',
  },
  {
    label: "配方名称",
    field: "recipeName",
    component: 'JInput',
  },
  {
    label: "颜色",
    field: "color",
    component: 'JInput',
  },
	{
      label: "状态",
      field: "status",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"status"
      },
      //colProps: {span: 6},
 	},
  {
    label:"是否发布",
    field:"publishStatus",
    component: 'JSelectMultiple',
    componentProps:{
      dictCode:"yn"
    },
  },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '配方编号',
    field: 'recipeCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入配方编号!'},
          ];
     },
  },
  {
    label: '配方名称',
    field: 'recipeName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入配方名称!'},
          ];
     },
  },
  {
    label:'颜色',
    field:'color',
    component:'Input',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入颜色!'},
      ];
    },
  },
  {
    label: '版本',
    field: 'version',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入配方版本!'},
      ];
    },
  },
  {
    label: '技术要求',
    field: 'technics',
    component: 'InputTextArea',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入技术要求!'},
          ];
     },
  },
  {
    label: '工艺id',
    field: 'routingId',
    component: 'Input',
    show:false
  },
  {
    label: '工艺名称',
    field: 'routingName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return{
        setFieldsValue:setFieldsValue,
        code:"mdm_routing_select",
        fieldConfig: [
          { source: 'id', target: 'routingId' },
          { source: 'routing_name', target: 'routingName' },
          { source: 'version', target: 'routingVersion' },
        ],
        multi:false
      }
    },

    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入工艺名称!'},
      ];
    },
  },
  {
    label: '工艺版本',
    field: 'routingVersion',
    component: 'Input',
  },
  {
    label: '占比类型',
    field: 'proportionType',
    defaultValue: "1",
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"mdm_proportion_type",

    },

  },
  {
    label: '配比总和',
    field: 'proportionTotal',
    component: 'Input',
    componentProps: {
      readonly: true, // 设置为只读，自动计算
      style: { color: '#000', fontWeight: 'bold' }
    },
    dynamicRules: ({ model, schema }) => {
      return [
        {
          validator: (_, value) => {
            // 只有当占比类型为1(标准)时才校验
            if (model.proportionType === '1') {
              const total = parseFloat(value) || 0;
              if (total !== 100) {
                return Promise.reject('标准类型的配比总和必须等于100！');
              }
            }
            return Promise.resolve();
          }
        }
      ];
    },
  },
  {
    label: '主配人',
    field: 'formulatorFirst',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入主配人!'},
          ];
     },
  },
  {
    label: '副配人',
    field: 'formulatorSecond',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入副配人!'},
          ];
     },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
  },
  {
    label: '注意事项',
    field: 'notes',
    component: 'InputTextArea',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入注意事项!'},
          ];
     },
  },
  {
    label: '状态',
    field: 'status',
    defaultValue: "1",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mdm_recipe_status"
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
export const recipeDetailColumns: JVxeColumn[] = [
    {
      title: '序号',
      key: 'serialNo',
      type: JVxeTypes.inputNumber,
      width:"100px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    // {
    //   title: '物料编码',
    //   key: 'materialCode',
    //   type: JVxeTypes.popup,
    //   popupCode:"mdm_material_select",
    //   fieldConfig: [
    //     { source: 'id', target: 'materialId' },
    //     { source: 'material_code', target: 'materialCode' },
    //     { source: 'material_name', target: 'materialName' },
    //     { source: 'material_spec', target: 'materialSpec' },
    //   ],
    //
    //   width:"150px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    //     validateRules: [
    //       { required: true, message: '${title}不能为空' },
    //     ],
    // },
    {
      title: '物料编码',
      key: 'materialCode',
      type: JVxeTypes.slot,        // 👈 由原来的 popup 改为自定义插槽
      slotName: 'materialCode',
      width:"150px",
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '物料名称',
      key: 'materialName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '型号规格',
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
      width:"100px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title:'单位',
      key:'unit',
      type:JVxeTypes.select,
      options:[],
      dictCode:"mis_unit,unit,unit ",
      width:"100px",
      placeholder: '请输入${title}',
      defaultValue:"kg",
    },
    {
      title: '备注',
      key: 'remark',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '描述',
      key: 'description',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  recipeCode: {title: '配方编号',order: 0,view: 'text', type: 'string',},
  recipeName: {title: '配方名称',order: 1,view: 'text', type: 'string',},
  technics: {title: '技术要求',order: 2,view: 'textarea', type: 'string',},
  formulatorFirst: {title: '主配人',order: 3,view: 'text', type: 'string',},
  formulatorSecond: {title: '副配人',order: 4,view: 'text', type: 'string',},
  remark: {title: '备注',order: 5,view: 'textarea', type: 'string',},
  notes: {title: '注意事项',order: 6,view: 'textarea', type: 'string',},
  status: {title: '状态',order: 7,view: 'list', type: 'string',dictCode: 'mdm_recipe_status',},
  version:{title: '版本',order: 8,view: 'list', type: 'string',},
  publishStatus:{title: '是否发布',order: 9,view: 'list', type: 'string',dictCode: 'yn',},
  //子表高级查询
  recipeDetail: {
    title: '配方明细',
    view: 'table',
    fields: {
        serialNo: {title: '序号',order: 0,view: 'number', type: 'number',},
        materialCode: {title: '物料编码',order: 1,view: 'popup', type: 'string',code: 'mis_material_select', orgFields: 'material_code', destFields: 'materialCode', popupMulti: false,},
        materialName: {title: '物料名称',order: 2,view: 'text', type: 'string',},
        materialSpec: {title: '型号规格',order: 3,view: 'text', type: 'string',},
        proportion: {title: '配比',order: 4,view: 'number', type: 'number',},
        remark: {title: '备注',order: 5,view: 'text', type: 'string',},
        description: {title: '描述',order: 6,view: 'text', type: 'string',},
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

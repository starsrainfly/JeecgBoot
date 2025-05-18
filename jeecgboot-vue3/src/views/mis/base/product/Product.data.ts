import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '产品编码',
    align: 'center',
    sorter: true,
    dataIndex: 'productCode'
   },
   {
    title: '产品名称',
    align: 'left',
    sorter: true,
    dataIndex: 'productName'
   },
   {
    title: '型号规格',
    align: 'center',
    dataIndex: 'productSpec'
   },
   {
    title: '配方编码',
    align: 'center',
    dataIndex: 'recipeCode'
   },
   {
    title: '产品描述',
    align: 'center',
    dataIndex: 'description'
   },
   {
    title: '特点',
    align: 'center',
    dataIndex: 'characteristic'
   },
   {
    title: '颜色',
    align: 'center',
    sorter: true,
    dataIndex: 'color'
   },
   {
    title: '用途',
    align: 'center',
    dataIndex: 'purpose'
   },
   {
    title: '粘度',
    align: 'center',
    sorter: true,
    dataIndex: 'viscosity'
   },
   {
    title: '触变',
    align: 'center',
    sorter: true,
    dataIndex: 'thixotropy'
   },
   {
    title: '密度比重',
    align: 'center',
    sorter: true,
    dataIndex: 'density'
   },
   {
    title: '保存期',
    align: 'center',
    sorter: true,
    dataIndex: 'shelfLife'
   },
   {
    title: '硬度',
    align: 'center',
    sorter: true,
    dataIndex: 'hardness'
   },
   {
    title: '拉力',
    align: 'center',
    sorter: true,
    dataIndex: 'pull'
   },
   {
    title: '配比',
    align: 'center',
    sorter: true,
    dataIndex: 'proportion'
   },
   {
    title: '光泽度',
    align: 'center',
    dataIndex: 'gloss'
   },
   {
    title: '固化条件',
    align: 'center',
    dataIndex: 'cureCondition'
   },
   {
    title: '耐温(℃)',
    align: 'center',
    dataIndex: 'temperature'
   },
   {
    title: '胶化时间(min)',
    align: 'center',
    dataIndex: 'gelTime'
   },
   {
    title: '抗弯强度',
    align: 'center',
    dataIndex: 'bending'
   },
   {
    title: '抗压强度',
    align: 'center',
    dataIndex: 'compression'
   },
   {
    title: '备注',
    align: 'center',
    dataIndex: 'remark'
   },
   {
    title: '状态',
    align: 'center',
    dataIndex: 'status_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: "产品编码",
    field: "productCode",
    component: 'JInput',
  },
  {
    label: "产品名称",
    field: "productName",
    component: 'JInput',
  },
  {
    label: "型号规格",
    field: "productSpec",
    component: 'JInput',
  },
	{
      label: "配方编码",
      field: "recipeCode",
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mis_recipe_select",
            fieldConfig: [
                { source: 'id', target: 'recipeId' },
                { source: 'recipe_code', target: 'recipeCode' },
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
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
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '产品编码',
    field: 'productCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入产品编码!'},
          ];
     },
  },
  {
    label: '产品名称',
    field: 'productName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入产品名称!'},
          ];
     },
  },
  {
    label: '型号规格',
    field: 'productSpec',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入型号规格!'},
          ];
     },
  },
  {
    label: '配方编码',
    field: 'recipeCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mis_recipe_select",
            fieldConfig: [
                { source: 'id', target: 'recipeId' },
                { source: 'recipe_code', target: 'recipeCode' },
            ],
            multi:true
        }
    },

  },
  {
    label: '产品描述',
    field: 'description',
    component: 'Input',
  },
  {
    label: '特点',
    field: 'characteristic',
    component: 'Input',
  },
  {
    label: '颜色',
    field: 'color',
    component: 'Input',
  },
  {
    label: '用途',
    field: 'purpose',
    component: 'Input',
  },
  {
    label: '粘度',
    field: 'viscosity',
    component: 'Input',
  },
  {
    label: '触变',
    field: 'thixotropy',
    component: 'Input',
  },
  {
    label: '密度比重',
    field: 'density',
    component: 'Input',
  },
  {
    label: '保存期',
    field: 'shelfLife',
    component: 'InputNumber',
  },
  {
    label: '硬度',
    field: 'hardness',
    component: 'Input',
  },
  {
    label: '拉力',
    field: 'pull',
    component: 'Input',
  },
  {
    label: '配比',
    field: 'proportion',
    component: 'Input',
  },
  {
    label: '光泽度',
    field: 'gloss',
    component: 'Input',
  },
  {
    label: '固化条件',
    field: 'cureCondition',
    component: 'Input',
  },
  {
    label: '耐温(℃)',
    field: 'temperature',
    component: 'Input',
  },
  {
    label: '胶化时间(min)',
    field: 'gelTime',
    component: 'Input',
  },
  {
    label: '抗弯强度',
    field: 'bending',
    component: 'Input',
  },
  {
    label: '抗压强度',
    field: 'compression',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'status',
    defaultValue: "1",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"status"
     },
  },
  {
    label: '父级节点',
    field: 'pid',
    component: 'JTreeSelect',
    componentProps: {
      dict: "mis_product,product_name,id",
      pidField: "pid",
      pidValue: "0",
      hasChildField: "has_child",
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
  productCode: {title: '产品编码',order: 0,view: 'text', type: 'string',},
  productName: {title: '产品名称',order: 1,view: 'text', type: 'string',},
  productSpec: {title: '型号规格',order: 2,view: 'text', type: 'string',},
  recipeCode: {title: '配方编码',order: 3,view: 'popup', type: 'string',code: 'mis_recipe_select', orgFields: 'recipe_code', destFields: 'recipeCode', popupMulti: false,},
  description: {title: '产品描述',order: 4,view: 'text', type: 'string',},
  characteristic: {title: '特点',order: 5,view: 'text', type: 'string',},
  color: {title: '颜色',order: 6,view: 'text', type: 'string',},
  purpose: {title: '用途',order: 7,view: 'text', type: 'string',},
  viscosity: {title: '粘度',order: 8,view: 'text', type: 'string',},
  thixotropy: {title: '触变',order: 9,view: 'text', type: 'string',},
  density: {title: '密度比重',order: 10,view: 'text', type: 'string',},
  shelfLife: {title: '保存期',order: 11,view: 'number', type: 'number',},
  hardness: {title: '硬度',order: 12,view: 'text', type: 'string',},
  pull: {title: '拉力',order: 13,view: 'text', type: 'string',},
  proportion: {title: '配比',order: 14,view: 'text', type: 'string',},
  gloss: {title: '光泽度',order: 15,view: 'text', type: 'string',},
  cureCondition: {title: '固化条件',order: 16,view: 'text', type: 'string',},
  temperature: {title: '耐温(℃)',order: 17,view: 'text', type: 'string',},
  gelTime: {title: '胶化时间(min)',order: 18,view: 'text', type: 'string',},
  bending: {title: '抗弯强度',order: 19,view: 'text', type: 'string',},
  compression: {title: '抗压强度',order: 20,view: 'text', type: 'string',},
  remark: {title: '备注',order: 21,view: 'text', type: 'string',},
  status: {title: '状态',order: 22,view: 'list', type: 'string',dictCode: 'status',},
};


/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

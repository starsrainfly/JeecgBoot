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
    dataIndex: 'productCode',
    width: 150
  },
  {
    title: '产品名称',
    align: 'left',
    sorter: true,
    dataIndex: 'productName',
    width: 200
  },
  {
    title: '颜色',
    align: 'left',
    sorter: true,
    dataIndex: 'color',
    width: 100
  },
  {
    title: '型号规格',
    align: 'center',
    dataIndex: 'productSpec',
    width: 120
  },
  {
    title: '配方编码',
    align: 'center',
    dataIndex: 'recipeCode',
    width: 120
  },
  {
    title: '产品颜色',
    align: 'center',
    dataIndex: 'productColor',
    width: 100
  },
  {
    title: '产品描述',
    align: 'center',
    dataIndex: 'description',
    width: 150,
    ellipsis: true
  },
  {
    title: '特点',
    align: 'center',
    dataIndex: 'characteristic',
    width: 120
  },
  {
    title: '备注',
    align: 'center',
    dataIndex: 'remark',
    width: 120
  },
  {
    title: '状态',
    align: 'center',
    dataIndex: 'status_dictText',
    width: 80
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
    label:'颜色',
    field:"color",
    component:'JInput',
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
  },
  {
    label: "状态",
    field: "status",
    component: 'JSelectMultiple',
    componentProps:{
      dictCode:"status"
    },
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
    label:'配方id',
    field:'recipeId',
    component:'Input',
    show:false
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
          { source: 'recipe_name', target: 'recipeName' },
          { source: 'version', target: 'recipeVersion' },
          { source: 'color', target: 'productColor' },
        ],
        multi:false
      }
    },
  },
  {
    label: '配方名称',
    field: 'recipeName',
    component: 'Input',
    componentProps:{
      readOnly:true
    }
  },
  {
    label: '配方版本',
    field: 'recipeVersion',
    component: 'Input',
    componentProps:{
      readOnly:true
    }
  },
  {
    label: '配方颜色',
    field: 'productColor',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入配方颜色!'},
      ];
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
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'status',
    defaultValue: '1',
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
  recipeCode: {title: '配方编码',order: 3,view: 'popup', type: 'string',code: 'mis_recipe_select', orgFields: 'id,recipe_code', destFields: 'recipe_id,recipeCode', popupMulti: false,},
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
  recipeId: {title: '配方id',order: 23,view: 'text', type: 'string',},
};

export function getBpmFormSchema(_formData): FormSchema[]{
  return formSchema;
}

import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [

   {
    title: '内包装规格',
    align:"center",
    dataIndex: 'innerPackageSpec'
   },

   {
    title: '外包装规格',
    align:"center",
    dataIndex: 'outerPackageSpec'
   },
   {
    title: '每箱几桶',
    align:"center",
    sorter: true,
    dataIndex: 'innerPerOuter'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [

	{
      label: "内包装",
      field: 'innerPackageId',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mis_material where is_package='1' and package_type='0',material_spec,id"
      },
      //colProps: {span: 6},
 	},
	{
      label: "外包装",
      field: 'outerPackageId',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mis_material where is_package='1' and package_type='1',material_spec,id"
      },
      //colProps: {span: 6},
 	},
	{
      label: "每箱几桶",
      field: 'innerPerOuter',
      component: 'InputNumber',
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [

  {
    label: '内包装',
    field: 'innerPackageId',
    component: 'JDictSelectTag',
    show:false,
    componentProps:{
        dictCode:"mis_material where is_package='1' and package_type='0',description,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入内包装!'},
          ];
     },
  },
  {
    label: '内包规格',
    field: 'innerPackageSpec',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_package_select",
          param:{
            packageType:"'0'"
          },
            fieldConfig: [
                { source: 'id', target: 'innerPackageId' },
                { source: 'description', target: 'innerPackageSpec' },
            ],
            multi:false
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入内包规格!'},
          ];
     },
  },

  {
    label: '外包装',
    field: 'outerPackageId',
    component: 'JDictSelectTag',
    show:false,

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入外包装!'},
          ];
     },
  },
  {
    label: '外包规格',
    field: 'outerPackageSpec',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return{
        setFieldsValue:setFieldsValue,
        code:"mdm_package_select",
        param:{
          packageType:"'1'"
        },
        fieldConfig: [
          { source: 'id', target: 'outerPackageId' },
          { source: 'description', target: 'outerPackageSpec' },
        ],
        multi:false
      }
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入外包规格!'},
      ];
    },
  },

  {
    label: '每箱几桶',
    field: 'innerPerOuter',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入每箱几桶!'},
          ];
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

  innerPackageId: {title: '内包装',order: 3,view: 'list', type: 'string',dictTable: "mis_material where is_package='1' and package_type='0'", dictCode: 'id', dictText: 'material_spec',},
  innerPackageSpec: {title: '内包装规格',order: 5,view: 'text', type: 'string',},
  outerPackageId: {title: '外包装',order: 6,view: 'list', type: 'string',dictTable: "mis_material where is_package='1' and package_type='1'", dictCode: 'id', dictText: 'material_spec',},
  outerPackageSpec: {title: '外包装规格',order: 8,view: 'text', type: 'string',},
  innerPerOuter: {title: '每箱几桶',order: 9,view: 'number', type: 'number',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

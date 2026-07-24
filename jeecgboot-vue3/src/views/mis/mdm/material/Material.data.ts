import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '物料编码',
    align: 'left',
    dataIndex: 'materialCode',
     width:300,
     sorter: true,
   },
   {
    title: '物料名称',
    align: 'center',
    dataIndex: 'materialName',
     sorter: true,
   },
   {
    title: '物料英文名称',
    align: 'center',
    dataIndex: 'materialNameEn'
   },
  {
    title: '规格型号',
    align: 'center',
    dataIndex: 'materialSpec'
  },
   {
    title: '描述',
    align: 'center',
    dataIndex: 'description'
   },

   // {
   //  title: '版本',
   //  align: 'center',
   //  dataIndex: 'version'
   // },
   {
    title: '是否符合ROHS',
    align: 'center',
    dataIndex: 'isrohs_dictText'
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
  //  {
  //   title: '是否为包装物料',
  //   align: 'center',
  //   dataIndex: 'isPackage_dictText'
  //  },
  // {
  //   title: '包装类型',
  //   align:'center',
  //   dataIndex:'packageType_dictText'
  //   },
   {
    title: '材料类型',
    align:'center',
    dataIndex:'materialType_dictText'
    },
   {
    title: '包装容量数值',
    align: 'center',
    dataIndex: 'packageCapacity'
   },
   {
    title: '包装容量单位',
    align: 'center',
    dataIndex: 'packageCapacityUnit'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "物料编码",
      field: "materialCode",
      component: 'Input',
      //colProps: {span: 6},
     },
	{
      label: "物料名称",
      field: "materialName",
      component: 'Input',
      //colProps: {span: 6},
     },
	{
      label: "物料英文名称",
      field: "materialNameEn",
      component: 'Input',
      //colProps: {span: 6},
     },
	{
      label: "描述",
      field: "description",
      component: 'Input',
      //colProps: {span: 6},
     },
	{
      label: "规格型号",
      field: "materialSpec",
      component: 'Input',
      //colProps: {span: 6},
     },
	// {
  //     label: "版本",
  //     field: "version",
  //     component: 'Input',
  //     //colProps: {span: 6},
  //    },
	{
      label: "是否符合ROHS",
      field: "isrohs",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"yn"
      },
      //colProps: {span: 6},
     },
	{
      label: "备注",
      field: "remark",
      component: 'Input',
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
	// {
  //     label: "是否为包装物料",
  //     field: "isPackage",
  //     component: 'JSelectMultiple',
  //     componentProps:{
  //         dictCode:"yn"
  //     },
  //     //colProps: {span: 6},
  //    },
  {
    label: '材料类型',
    field: "materialType",
    component:'JSelectMultiple',
    componentProps:{
      dictCode: "mdm_material_type"
    }
  },

];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '父级节点',
    field: 'pid',
    component: 'JTreeSelect',
    componentProps: {
      dict: "mis_material,material_code,id",
      pidField: "pid",
      pidValue: "0",
      hasChildField: "has_child",
    },
  },
  {
    label: '物料编码',
    field: 'materialCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入物料编码!'},
                 {...rules.duplicateCheckRule('mis_material', 'material_code',model,schema)[0]},
          ];
     },
  },
  {
    label: '物料名称',
    field: 'materialName',
    component: 'Input',
    componentProps: ({ formModel, formActionType }) => ({
      onChange: (e) => {
        const name = e.target.value;
        const spec = formModel.materialSpec || '';
        formActionType.setFieldsValue({
          description: [name, spec].filter(Boolean).join(' ')
        });
      }
    }),
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入物料名称!'},
          ];
     },
  },
  // {
  //   label: '物料英文名称',
  //   field: 'materialNameEn',
  //   component: 'Input',
  // },

  {
    label: '规格型号',
    field: 'materialSpec',
    component: 'Input',
    componentProps: ({ formModel, formActionType }) => ({
      onChange: (e) => {
        const spec = e.target.value;
        const name = formModel.materialName || '';
        formActionType.setFieldsValue({
          description: [name, spec].filter(Boolean).join(' ')
        });
      }
    }),
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入规格型号!'},
          ];
     },
  },
  {
    label: '描述',
    field: 'description',
    component: 'Input',
    componentProps: {
      readonly: true,
      // 或 disabled: true（但 disabled 不会提交值！）
    },
  },
  {
    label: '版本',
    field: 'version',
    component: 'Input',
  },
  {
    label: '是否符合ROHS',
    field: 'isRohs',

    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yn"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入是否符合ROHS!'},
          ];
     },
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
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入状态!'},
          ];
     },
  },
  // {
  //   label: '是否为包装物料',
  //   field: 'isPackage',
  //   defaultValue: "0",
  //   component: 'JDictSelectTag',
  //   componentProps:{
  //       dictCode:"yn"
  //    },
  // },
  // {
  //   label: '包装类型',
  //   field: 'packageType',
  //   component: 'JDictSelectTag',
  //   componentProps:{
  //     dictCode:"mdm_package_type"
  //   },
  //
  //  show: ({ values }) => values.isPackage === '1',
  //   dynamicRules: ({ model }) => {
  //     // 当 isPackage 为 "1"（是）时，packageType 必填
  //     if (model.isPackage === '1') {
  //       return [{ required: true, message: '请选择包装类型！' }];
  //     }
  //     return []; // 否则不校验
  //   },
  // },
  {
    label: '材料类型',
    field: "materialType",
    component:'JDictSelectTag',
    componentProps:{
      dictCode: "mdm_material_type"
    },

  },
  {
    label: '包装容量数值',
    field: 'packageCapacity',
    component: 'InputNumber',
   show: ({ values }) =>  values.materialType == 'INNER_PACK',
    dynamicRules: ({ model }) => {
      return  model.materialType == 'INNER_PACK' ? [{ required: true, message: '请输入包装容量数值！' }] : [];
    },
  },
  {
    label: '包装容量单位',
    field: 'packageCapacityUnit',
    component: 'Input',
   show: ({ values }) =>  values.materialType == 'INNER_PACK',
    dynamicRules: ({ model }) => {
      return  model.materialType == 'INNER_PACK' ? [{ required: true, message: '请输入包装容量单位！' }] : [];
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
  materialCode: {title: '物料编码',order: 1,view: 'text', type: 'string',},
  materialName: {title: '物料名称',order: 2,view: 'text', type: 'string',},
  materialNameEn: {title: '物料英文名称',order: 3,view: 'text', type: 'string',},
  description: {title: '描述',order: 4,view: 'text', type: 'string',},
  materialSpec: {title: '规格型号',order: 5,view: 'text', type: 'string',},
  version: {title: '版本',order: 6,view: 'text', type: 'string',},
  isrohs: {title: '是否符合ROHS',order: 7,view: 'number', type: 'number',dictCode: 'yn',},
  remark: {title: '备注',order: 8,view: 'text', type: 'string',},
  status: {title: '状态',order: 9,view: 'list', type: 'string',dictCode: 'status',},
  // isPackage: {title: '是否为包装物料',order: 10,view: 'list', type: 'string',dictCode: 'yn',},
  materialType:{title:'物料类型',order:10,view: 'list', type:'string', dictCode:'mdm_material_type'},
  packageCapacity: {title: '包装容量数值',order: 11,view: 'number', type: 'number',},
  packageCapacityUnit: {title: '包装容量单位',order: 12,view: 'text', type: 'string',},
};


/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';

//列表数据
export const columns: BasicColumn[] = [
   {
    title: '物料编码',
    align: 'center',
    dataIndex: 'materialCode'
   },
   {
    title: '物料名称',
    align: 'left',
    dataIndex: 'materialName'
   },
   {
    title: '描述',
    align: 'center',
    dataIndex: 'description'
   },
   {
    title: '规格型号',
    align: 'center',
    dataIndex: 'materialSpec'
   },
   {
    title: '版本',
    align: 'center',
    dataIndex: 'version'
   },
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
];
//查询数据
export const searchFormSchema: FormSchema[] = [
     {
      label: "物料编码",
      field: "materialCode",
      component: 'Input', //TODO 范围查询
      //colProps: {span: 6},
	},
     {
      label: "物料名称",
      field: "materialName",
      component: 'Input', //TODO 范围查询
      //colProps: {span: 6},
	},
     {
      label: "描述",
      field: "description",
      component: 'Input', //TODO 范围查询
      //colProps: {span: 6},
	},
     {
      label: "规格型号",
      field: "materialSpec",
      component: 'Input', //TODO 范围查询
      //colProps: {span: 6},
	},
	{
      label: "状态",
      field: "status",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"status",
      },
      //colProps: {span: 6},
     },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '父级节点',
    field: 'pid',
    component: 'JTreeSelect',
    componentProps: {
      dict: "mis_material,material_name,id",
      pidField: "pid",
      pidValue: "0",
      hasChildField: "has_child",
    },
  },
  {
    label: '物料编码',
    field: 'materialCode',
    component: 'Input',
  },
  {
    label: '物料名称',
    field: 'materialName',
    component: 'Input',
  },
  {
    label: '描述',
    field: 'description',
    component: 'Input',
  },
  {
    label: '规格型号',
    field: 'materialSpec',
    component: 'Input',
  },
  {
    label: '版本',
    field: 'version',
    component: 'Input',
  },
  {
    label: '是否符合ROHS',
    field: 'isrohs',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"yn",
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
    // defaultValue: "1",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:'status',
      // options: [
      //   { label: '正常', value: 1 },
      //   { label: '冻结', value: 2 },
      // ],
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
  description: {title: '描述',order: 3,view: 'text', type: 'string',},
  materialSpec: {title: '规格型号',order: 4,view: 'text', type: 'string',},
  version: {title: '版本',order: 5,view: 'text', type: 'string',},
  isrohs: {title: '是否符合ROHS',order: 6,view: 'number', type: 'number',dictCode: 'yn',},
  remark: {title: '备注',order: 7,view: 'text', type: 'string',},
  status: {title: '状态',order: 8,view: 'list', type: 'string',dictCode: 'status',},
};


/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

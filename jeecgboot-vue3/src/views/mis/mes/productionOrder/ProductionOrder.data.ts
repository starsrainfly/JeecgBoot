import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '生产编号',
    align:"center",
    sorter: true,
    dataIndex: 'orderNo'
   },
   {
    title: '产品编码',
    align:"center",
    sorter: true,
    dataIndex: 'productCode'
   },
   {
    title: '产品名称',
    align:"center",
    sorter: true,
    dataIndex: 'productName'
   },
   {
    title: '计划产量(Kg)',
    align:"center",
    dataIndex: 'plannedQty'
   },
   {
    title: '单釜产量(Kg)',
    align:"center",
    dataIndex: 'batchSize'
   },
   {
    title: '批次数量',
    align:"center",
    dataIndex: 'batchCount'
   },
   {
    title: '内包装',
    align:"center",
    dataIndex: 'innerPackageId_dictText'
   },
   {
    title: '内包装数量',
    align:"center",
    dataIndex: 'innerPackageQty'
   },
   {
    title: '外包装',
    align:"center",
    dataIndex: 'outerPackageId_dictText'
   },
   {
    title: '外包装数量',
    align:"center",
    dataIndex: 'outerPackageQty'
   },
   {
    title: '每外包含内包数量',
    align:"center",
    dataIndex: 'outerInnerPerOuter'
   },
   {
    title: '计划开工',
    align:"center",
    dataIndex: 'plannedStartDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '计划完工',
    align:"center",
    dataIndex: 'plannedEndDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '实际开工',
    align:"center",
    dataIndex: 'actualStartTime'
   },
   {
    title: '实际完工',
    align:"center",
    dataIndex: 'actualEndTime'
   },
   {
    title: '交货日期',
    align:"center",
    dataIndex: 'deliveryDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "产品编码",
      field: "productCode",
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_product_select",
            fieldConfig: [
                { source: 'id', target: 'productId' },
                { source: 'product_code', target: 'productCode' },
                { source: 'product_name', target: 'productName' },
                { source: 'recipe_id', target: 'recipeId' },
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "产品名称",
      field: "productName",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "内包装",
      field: "innerPackageId",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mis_material where is_package='1' and package_type='0',description,id"
      },
      //colProps: {span: 6},
 	},
	{
      label: "外包装",
      field: "outerPackageId",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mis_material where is_package='1' and package_type='1',description,id"
      },
      //colProps: {span: 6},
 	},
	{
      label: "状态",
      field: "status",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mes_production_status"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '生产编号',
    field: 'orderNo',
    component: 'Input',
  },
  {
    label:'产品id',
    field:'productId',
    component:'Input',
    show:false
  },
  {
    label: '产品编码',
    field: 'productCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_product_select",
            fieldConfig: [
                { source: 'id', target: 'productId' },
                { source: 'product_code', target: 'productCode' },
                { source: 'product_name', target: 'productName' },
                { source: 'recipe_id', target: 'recipeId' },
            ],
            multi:true
        }
    },

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
    componentProps:{
      readonly:true,
    }
  },
  // {
  //   label:'配方id',
  //   field:'recipeId',
  //   component:'Input',
  // },
  {
    label: '计划产量(Kg)',
    field: 'plannedQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入计划产量(Kg)!'},
          ];
     },
  },
  {
    label: '单釜产量(Kg)',
    field: 'batchSize',
    defaultValue: 50,
    component: 'InputNumber',
  },
  {
    label: '批次数量',
    field: 'batchCount',
    defaultValue: 1,
    component: 'InputNumber',
  },
  {
    label: '内包装',
    field: 'innerPackageId',
    component: 'JDictSelectTag',
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
    label: '内包数量',
    field: 'innerPackageQty',
    component: 'InputNumber',
  },
  {
    label:'内包容量',
    field:'innerPackageCapacity',
    component:'InputNumber',
  },
  {
    label: '外包装',
    field: 'outerPackageId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_material where is_package='1' and package_type='1',description,id"
     },
  },
  {
    label: '外包数量',
    field: 'outerPackageQty',
    component: 'InputNumber',
  },
  {
    label: '每外包含内包量',
    field: 'outerInnerPerOuter',
    component: 'InputNumber',
  },
  {
    label: '交货日期',
    field: 'deliveryDate',
    component: 'DatePicker',
    componentProps:{
      valueFormat: 'YYYY-MM-DD'
    },    
  },
  {
    label: '状态',//0草稿1已下达，2部分完成3已完成
    field: 'status',
    defaultValue: "0",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mes_production_status"
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
export const productionOrderDetailColumns: JVxeColumn[] = [
    {
      title: '计划编码',
      key: 'planNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  {
    title:'明细id',
    key: 'planDetailId',
    type: JVxeTypes.input,
    width:"200px",
    placeholder: '请输入${title}',
    defaultValue:'',
  },
  {
    title:'产品编码',
    key:'productCode',
    type:JVxeTypes.input,
    width:"200px",
    show:false
  },
  {
    title:'产品名称',
    key:'productName',
    type:JVxeTypes.input,
    width:"200px",
    disabled:true,
   // show:false
  },
    {
      title: '销售订单',
      key: 'salesOrderNo',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '计划类型',
      key: 'planType',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mes_plan_type",
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '客户编码',
      key: 'customerCode',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '客户名称',
      key: 'customerName',
      type: JVxeTypes.input,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '计划分配量',
      key: 'planAllocatedQty',
      type: JVxeTypes.inputNumber,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '本次执行数量',
      key: 'allocatedQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '请输入本次执行数量' },
        {
          validator: (cellValue, row, column, record) => {
            // 关键修复：兼容 JVxeTable 不同版本参数传递方式
            const rowData = row || record || {};

            // 防御性检查
            if (!rowData || typeof rowData !== 'object') {
              console.warn('验证时 row 数据异常:', { cellValue, row, column, record });
              return true;
            }

            // 兼容可能的字段名（planAllocatedQty 或 planAllocatedQty）
            const original = Number(rowData.planAllocatedQty ?? rowData.planAllocatedQty ?? 0) || 0;
            const current = Number(cellValue) || 0;

            if (current > original) {
              return `不能大于计划分配量(${original})`;
            }
            return true;
          }
        }
      ]
    },
    {
      title: '交货日期',
      key: 'deliverDate',
      type: JVxeTypes.date,
      disabled:true,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '优化级',
      key: 'priorityLevel',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mes_priority_level",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:"3",
    },
    {
      title: '备注',
      key: 'remark',
      type: JVxeTypes.textarea,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  orderNo: {title: '生产编号',order: 0,view: 'text', type: 'string',},
  productCode: {title: '产品编码',order: 1,view: 'popup', type: 'string',code: 'mdm_product_select', orgFields: 'product_code', destFields: 'productCode', popupMulti: false,},
  productName: {title: '产品名称',order: 2,view: 'text', type: 'string',},
  plannedQty: {title: '计划产量(Kg)',order: 3,view: 'number', type: 'number',},
  batchSize: {title: '单釜产量(Kg)',order: 4,view: 'number', type: 'number',},
  batchCount: {title: '批次数量',order: 5,view: 'number', type: 'number',},
  innerPackageId: {title: '内包装',order: 6,view: 'list', type: 'string',dictTable: "mis_material where is_package='1' and package_type='0'", dictCode: 'id', dictText: 'description',},
  innerPackageQty: {title: '内包装数量',order: 7,view: 'number', type: 'number',},
  outerPackageId: {title: '外包装',order: 8,view: 'list', type: 'string',dictTable: "mis_material where is_package='1' and package_type='1'", dictCode: 'id', dictText: 'description',},
  outerPackageQty: {title: '外包装数量',order: 9,view: 'number', type: 'number',},
  outerInnerPerOuter: {title: '每外包含内包数量',order: 10,view: 'number', type: 'number',},
  plannedStartDate: {title: '计划开工',order: 11,view: 'date', type: 'string',},
  plannedEndDate: {title: '计划完工',order: 12,view: 'date', type: 'string',},
  actualStartTime: {title: '实际开工',order: 13,view: 'datetime', type: 'string',},
  actualEndTime: {title: '实际完工',order: 14,view: 'datetime', type: 'string',},
  deliveryDate: {title: '交货日期',order: 15,view: 'date', type: 'string',},
  status: {title: '状态',order: 16,view: 'list', type: 'string',dictCode: 'mes_production_status',},
  //子表高级查询
  productionOrderDetail: {
    title: '生产订单明细',
    view: 'table',
    fields: {
        planDetailId: {title: '计划明细id',order: 0,view: 'text', type: 'string',},
        planNo: {title: '计划编码',order: 0,view: 'text', type: 'string',},
        salesOrderNo: {title: '销售订单',order: 1,view: 'text', type: 'string',},
        planType: {title: '计划类型',order: 2,view: 'list', type: 'string',dictCode: 'mes_plan_type',},
        customerCode: {title: '客户编码',order: 3,view: 'text', type: 'string',},
        customerName: {title: '客户名称',order: 4,view: 'text', type: 'string',},
        planAllocatedQty: {title: '计划分配量',order: 5,view: 'number', type: 'number',},
        allocatedQty: {title: '本次执行数量',order: 6,view: 'number', type: 'number',},
        deliverDate: {title: '交货日期',order: 7,view: 'date', type: 'string',},
        priorityLevel: {title: '优化级',order: 8,view: 'list', type: 'string',dictCode: 'mes_priority_level',},
        remark: {title: '备注',order: 9,view: 'textarea', type: 'string',},
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

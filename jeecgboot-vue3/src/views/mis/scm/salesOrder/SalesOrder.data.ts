import {BasicColumn, FormSchema} from '/@/components/Table';
import {JVxeColumn, JVxeTypes} from '/@/components/jeecg/JVxeTable/types'


import {h} from 'vue'
import {SearchOutlined} from '@ant-design/icons-vue'
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '订单编号',
    align:"center",
    dataIndex: 'orderNo'
   },
   {
    title: '订单日期',
    align:"center",
    dataIndex: 'orderDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '客户',
    align:"center",
    dataIndex: 'customerId'
   },
   {
    title: '业务员',
    align:"center",
    dataIndex: 'salesmanName'
   },
   {
    title: '币种代码',
    align:"center",
    dataIndex: 'currencyCode_dictText'
   },
   {
    title: '汇率',
    align:"center",
    dataIndex: 'exchangeRate'
   },
   {
    title: '付款账期(天)',
    align:"center",
    dataIndex: 'paymentDays'
   },
   {
    title: '收货地址',
    align:"center",
    dataIndex: 'deliveryAddress_dictText'
   },
   {
    title: '收货人',
    align:"center",
    dataIndex: 'deliveryConsignee'
   },
   {
    title: '联系电话',
    align:"center",
    dataIndex: 'deliveryPhone'
   },
   {
    title: '订单总额',
    align:"center",
    dataIndex: 'totalAmount'
   },
   {
    title: '本位币金额',
    align:"center",
    dataIndex: 'totalAmountLocal'
   },
   {
    title: '销售审批状态',
    align:"center",
    dataIndex: 'salesApprovalStatus'
   },
   {
    title: '财务审批状态',
    align:"center",
    dataIndex: 'financeApprovalStatus'
   },
   {
    title: '销售审批通过时间',
    align:"center",
    dataIndex: 'salesApprovedAt'
   },
   {
    title: '销售审批人',
    align:"center",
    dataIndex: 'salesApproverName'
   },
   {
    title: '财务审批通过时间',
    align:"center",
    dataIndex: 'financeApprovedAt'
   },
   {
    title: '财务审批人',
    align:"center",
    dataIndex: 'financeApproveName'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
   {
    title: '订单状态',
    align:"center",
    dataIndex: 'orderStatus'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "订单编号",
      field: "orderNo",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "订单日期",
      field: "orderDate",
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD'
      },      
      //colProps: {span: 6},
 	},
	{
      label: "客户",
      field: "customerId",
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_customer",
            fieldConfig: [
                { source: 'id', target: 'customerId' },
                { source: 'payment_days', target: 'paymentDays' },
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "业务员id",
      field: "salesmanId",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"sys_user,realname,id"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '订单编号',
    field: 'orderNo',
    component: 'Input',
  },
  {
    label: '订单日期',
    field: 'orderDate',
    component: 'DatePicker',
    componentProps:{
      valueFormat: 'YYYY-MM-DD'
    },    
  },
  {
    label: '客户',
    field: 'customerId',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_customer",
            fieldConfig: [
                { source: 'id', target: 'customerId' },
                { source: 'payment_days', target: 'paymentDays' },
            ],
            multi:true
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入客户!'},
          ];
     },
  },
  {
    label: '业务员id',
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_user,realname,id"
     },
  },
  {
    label: '币种代码',
    field: 'currencyCode',
    defaultValue: "CNY",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_currency,currency_name,currency_code"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入币种代码!'},
          ];
     },
  },
  {
    label: '汇率',
    field: 'exchangeRate',
    defaultValue: 1,
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入汇率!'},
          ];
     },
  },
  {
    label: '付款账期(天)',
    field: 'paymentDays',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入付款账期(天)!'},
          ];
     },
  },
  {
    label: '收货地址',
    field: 'deliveryAddress',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:""
     },
  },
  {
    label: '收货人',
    field: 'deliveryConsignee',
    component: 'Input',
  },
  {
    label: '联系电话',
    field: 'deliveryPhone',
    component: 'Input',
  },
  {
    label: '订单总额',
    field: 'totalAmount',
    component: 'InputNumber',
  },
  {
    label: '本位币金额',
    field: 'totalAmountLocal',
    component: 'InputNumber',
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
export const salesOrderLineColumns: JVxeColumn[] = [
    {
      title: '产品编码',
      key: 'itemCode',
      // type: JVxeTypes.popup,
      // popupCode:"mdm_product_select",
      // fieldConfig: [
      //   { source: 'id', target: 'itemId' },
      //   { source: 'product_code', target: 'itemCode' },
      //   { source: 'product_name', target: 'itemName' },
      //   { source: 'product_spec', target: 'itemSpec' },
      // ],
      type: JVxeTypes.input,
      renderEdit: ({ row, rowIndex, $table }) => {
        return h('div', { style: { display: 'flex', alignItems: 'center' } }, [
          h(
            'a-input',
            {
              value: row.itemCode,
              readonly: true,
              onClick: () => {
                console.log('🖱️ 产品编码被点击！');
                if (openPriceSelectModal) {
                  openPriceSelectModal(row, rowIndex, $table)
                }
              },
              placeholder: '点击选择价格策略',
              style: { cursor: 'pointer', backgroundColor: '#fafafa' },
            },
            { suffix: h(SearchOutlined) }
          ),
        ])
      },

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '产品名称',
      key: 'itemName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '单位',
      key: 'unit',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:"kg",
    },
    {
      title: '数量',
      key: 'quantity',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '单价',
      key: 'unitPrice',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '金额',
      key: 'lineAmount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '本币金额',
      key: 'lineAmountLocal',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '是否赠品',
      key: 'isGift',
      type: JVxeTypes.select,
      options:[],
      dictCode:"yn",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:"0",
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
      title: '税率(%)',
      key: 'taxRate',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:13,
    },
    {
      title: '税额',
      key: 'taxAmount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:0,
    },
    {
      title: '包装名称',
      key: 'packageName',
      type: JVxeTypes.popup,
      popupCode:"mdm_package_select",
      fieldConfig: [
        { source: 'id', target: 'packageItemId' },
        { source: 'material_name', target: 'packageName' },
        { source: 'material_spec', target: 'packageSpec' },
        { source: 'package_capacity', target: 'packageCapacity' },
        { source: 'package_capacity_unit', target: 'packageCapacityUnit' },
      ],

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '包装规格',
      key: 'packageSpec',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '包装容量',
      key: 'packageCapacity',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '包装单位',
      key: 'packageCapacityUnit',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  orderNo: {title: '订单编号',order: 0,view: 'text', type: 'string',},
  orderDate: {title: '订单日期',order: 1,view: 'date', type: 'string',},
  customerId: {title: '客户',order: 2,view: 'popup', type: 'string',code: 'scm_customer', orgFields: 'id', destFields: 'customerId', popupMulti: false,},
  salesmanName: {title: '业务员',order: 4,view: 'text', type: 'string',},
  currencyCode: {title: '币种代码',order: 5,view: 'list', type: 'string',dictTable: "mis_currency", dictCode: 'currency_code', dictText: 'currency_name',},
  exchangeRate: {title: '汇率',order: 6,view: 'number', type: 'number',},
  paymentDays: {title: '付款账期(天)',order: 7,view: 'number', type: 'number',},
  deliveryAddress: {title: '收货地址',order: 8,view: 'list', type: 'string',dictCode: '',},
  deliveryConsignee: {title: '收货人',order: 9,view: 'text', type: 'string',},
  deliveryPhone: {title: '联系电话',order: 10,view: 'text', type: 'string',},
  totalAmount: {title: '订单总额',order: 11,view: 'number', type: 'number',},
  totalAmountLocal: {title: '本位币金额',order: 12,view: 'number', type: 'number',},
  salesApprovalStatus: {title: '销售审批状态',order: 13,view: 'text', type: 'string',},
  financeApprovalStatus: {title: '财务审批状态',order: 14,view: 'text', type: 'string',},
  salesApprovedAt: {title: '销售审批通过时间',order: 15,view: 'datetime', type: 'string',},
  salesApproverName: {title: '销售审批人',order: 16,view: 'text', type: 'string',},
  financeApprovedAt: {title: '财务审批通过时间',order: 17,view: 'datetime', type: 'string',},
  financeApproveName: {title: '财务审批人',order: 18,view: 'text', type: 'string',},
  remark: {title: '备注',order: 19,view: 'text', type: 'string',},
  orderStatus: {title: '订单状态',order: 20,view: 'text', type: 'string',},
  //子表高级查询
  salesOrderLine: {
    title: '销售订单明细表',
    view: 'table',
    fields: {
        itemCode: {title: '产品编码',order: 0,view: 'popup', type: 'string',code: 'mdm_product_select', orgFields: 'product_code', destFields: 'itemCode', popupMulti: false,},
        itemName: {title: '产品名称',order: 1,view: 'text', type: 'string',},
        unit: {title: '单位',order: 2,view: 'text', type: 'string',},
        quantity: {title: '数量',order: 3,view: 'number', type: 'number',},
        unitPrice: {title: '单价',order: 4,view: 'number', type: 'number',},
        lineAmount: {title: '金额',order: 5,view: 'number', type: 'number',},
        lineAmountLocal: {title: '本币金额',order: 6,view: 'number', type: 'number',},
        isGift: {title: '是否赠品',order: 7,view: 'list', type: 'string',dictCode: 'yn',},
        remark: {title: '备注',order: 8,view: 'text', type: 'string',},
        taxRate: {title: '税率(%)',order: 9,view: 'number', type: 'number',},
        taxAmount: {title: '税额',order: 10,view: 'number', type: 'number',},
        packageName: {title: '包装名称',order: 11,view: 'popup', type: 'string',code: 'mdm_package_select', orgFields: 'material_name', destFields: 'packageName', popupMulti: false,},
        packageSpec: {title: '包装规格',order: 12,view: 'text', type: 'string',},
        packageCapacity: {title: '包装容量',order: 13,view: 'number', type: 'number',},
        packageCapacityUnit: {title: '包装单位',order: 14,view: 'text', type: 'string',},
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

//价格策略产品弹窗
export let openPriceSelectModal: ((row: any, rowIndex: number, tableRef: any) => void) | null = null

export const setPriceSelectModalOpener = (fn: (row: any, rowIndex: number, tableRef: any) => void) => {
  openPriceSelectModal = fn
}

import {BasicColumn, FormSchema} from '/@/components/Table';
import {JVxeColumn, JVxeTypes} from '/@/components/jeecg/JVxeTable/types'
import {nextTick, watch} from 'vue';
import {useUserStore} from "@/store/modules/user";
import dayjs from "dayjs";

const userStore = useUserStore();
const userInfo = userStore.getUserInfo;
let isAdmin = userInfo.roles?.includes('admin') || userInfo.username === 'admin';

//列表数据
export const columns: BasicColumn[] = [
   {
    title: '订单编号',
    align:"center",
    dataIndex: 'orderNo'
   },
   {
    title: '订单类型',
    align:"center",
    dataIndex: 'orderType_dictText'
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
    title: '交货日期',
    align:"center",
    dataIndex: 'deliveryDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '客户编码',
    align:"center",
    dataIndex: 'customerCode'
   },
   {
    title: '客户名称',
    align:"center",
    dataIndex: 'customerName'
   },
   {
    title: '业务员',
    align:"center",
    dataIndex: 'salesmanId_dictText'
   },
  {
    title: '订单总额',
    align:"center",
    dataIndex: 'orderTotal'
  },
  {
    title: '去税总额',
    align:"center",
    dataIndex: 'orderNet'
  },
  {
    title: '税金总额',
    align:"center",
    dataIndex: 'orderTax'
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
    title: '结算方式',
    align:"center",
    dataIndex: 'paymentMethod_dictText'
   },
   {
    title: '付款账期(天)',
    align:"center",
    dataIndex: 'paymentDays'
   },

   {
    title: '收货地址',
    align:"center",
    dataIndex: 'deliveryAddress'
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
    title: '业务审批状态',
    align:"center",
    dataIndex: 'salesApproveStatus_dictText'
   },
   {
    title: '财务审批状态',
    align:"center",
    dataIndex: 'financeApproveStatus_dictText'
   },
   {
    title: '业务审批时间',
    align:"center",
    dataIndex: 'salesApproveTime'
   },
   {
    title: '业务审批人',
    align:"center",
    dataIndex: 'salesApproverName'
   },

   {
    title: '财务审批时间',
    align:"center",
    dataIndex: 'financeApproveTime'
   },
   {
    title: '财务审批人',
    align:"center",
    dataIndex: 'financeApproverName'
   },
   {
    title: '订单状态',
    align:"center",
    dataIndex: 'orderStatus_dictText'
   },
   {
    title: '发货状态',
    align:"center",
    dataIndex: 'deliveryStatus_dictText'
   },
   {
    title: '应收日期',
    align:"center",
    dataIndex: 'receivableDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '结算状态',
    align:"center",
    dataIndex: 'settleStatus_dictText'
   },
   {
    title: '已开票金额',
    align:"center",
    dataIndex: 'invoiceAmount'
   },
   {
    title: '客户订单号',
    align:"center",
    dataIndex: 'customerOrderNo'
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
      label: "客户名称",
      field: "customerName",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "业务员",
      field: "salesmanId",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"sys_user,realname,id",
        disabled:!isAdmin,
      },
    defaultValue: userInfo.id,
      //colProps: {span: 6},
 	},
  {
    label: '业务审批状态',
    field: 'salesApproveStatus',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"approval_status"
    },
  },
  {
    label: '财务审批状态',
    field: 'financeApproveStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"approval_status"
     },
  },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '订单编号',
    field: 'orderNo',
    component: 'Input',
    componentProps:{
      readonly: true,
    },
  },
  {
    label: '订单类型',
    field: 'orderType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"scm_order_type"
     },
    defaultValue:'NORMAL',
  },
  {
    label: '订单日期',
    field: 'orderDate',
    component: 'DatePicker',
    // componentProps:{
    //   valueFormat: 'YYYY-MM-DD',
    //
    // },
    componentProps: (params) => {
      const { formActionType } = params;
      return {
        valueFormat: 'YYYY-MM-DD',
        onChange: (dateValue) => {
          // 通过全局状态或事件总线触发计算
          // 简单方式：修改一个隐藏的触发字段
          const { getFieldsValue, setFieldsValue } = formActionType;
          const { paymentDays } = getFieldsValue();
          if (dateValue && paymentDays) {
            const receivableDate = dayjs(dateValue).add(Number(paymentDays), 'day').format('YYYY-MM-DD');
            setFieldsValue({ receivableDate });
          }
        }
      };
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入订单日期!'},
          ];
     },
  },
  {
    label: '交货日期',
    field: 'deliveryDate',
    component: 'DatePicker',
    componentProps:{
      valueFormat: 'YYYY-MM-DD'
    },    
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入交货日期!'},
          ];
     },
  },
  {
    label:'客户id',
    field:'customerId',
    component: 'Input',
    show:false,
  },
  {
    label: '客户编码',
    field: 'customerCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue, getFieldsValue} = formActionType;

        return{
            setFieldsValue:setFieldsValue,
            code:"scm_customer",
          param: {
            salesmanId: userInfo.id,  // 传业务员ID参数
          },
            fieldConfig: [
                { source: 'id', target: 'customerId' },
                { source: 'customer_code', target: 'customerCode' },
                { source: 'customer_name', target: 'customerName' },
                { source: 'payment_method',target: 'paymentMethod'},
                { source: 'payment_days', target: 'paymentDays' },
                { source: 'receiver_name', target: 'deliveryConsignee' },
                { source: 'address', target: 'deliveryAddress' },
                { source: 'receiver_phone', target: 'deliveryPhone' },
                { source: 'delivery_address_id', target: 'deliveryAddressId' },
            ],
            multi:false,

        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入客户编码!'},
          ];
     },
  },
  {
    label: '客户名称',
    field: 'customerName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入客户名称!'},
          ];
     },
  },
  {
    label: '业务员',
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_user,realname,id",
      disabled:!isAdmin,
     },
    defaultValue:userInfo.id,
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入业务员!'},
          ];
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
    label: '结算方式',
    field: 'paymentMethod',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"payment_method",
      disabled:true,
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入结算方式!'},
      ];
    },
  },
  {
    label: '付款账期(天)',
    field: 'paymentDays',
    component: 'InputNumber',
    componentProps: (params) => {
      const { formActionType , formModel,schema} = params || {};
      watch(
        () => formModel.paymentDays,
        (newVal, oldVal) => {
          // 避免初始化时的空值触发
          if (newVal === oldVal) return;
          if (newVal === undefined || newVal === null || newVal === '') return;

          const { orderDate } = formActionType.getFieldsValue();
          if (!orderDate) return;

          const receivableDate = dayjs(orderDate).add(Number(newVal), 'day').format('YYYY-MM-DD');

          // 使用 nextTick 避免循环
          nextTick(() => {
            formActionType.setFieldsValue({ receivableDate });
          });
        },
        { immediate: false } // 避免初始化时触发
      );


      return {
        readonly: true,
        onChange: (val) => {
          // 使用 setTimeout 确保表单值已更新
          setTimeout(() => {
            const { getFieldsValue, setFieldsValue } = formActionType || {};
            if (!getFieldsValue || !setFieldsValue) return;

            const { orderDate } = getFieldsValue();
            if (orderDate && val !== undefined && val !== null && val !== '') {
              const receivableDate = dayjs(orderDate).add(Number(val), 'day').format('YYYY-MM-DD');
              setFieldsValue({ receivableDate });
            }
          }, 0);
        }
      };

    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入付款账期(天)!'},
          ];
     },
  },
  {
    label: '收货地址id',
    field: 'deliveryAddressId',
    component: 'Input',
    show:false
  },
  {
    label: '收货地址',
    field: 'deliveryAddress',
    component: 'Input',
    // componentProps: ({ formActionType }) => {
    //     const {setFieldsValue} = formActionType;
    //     return{
    //         setFieldsValue:setFieldsValue,
    //         code:"scm_customer_address",
    //         fieldConfig: [
    //             { source: 'id', target: 'deliverAddressId' },
    //             { source: 'address', target: 'deliveryAddress' },
    //             { source: 'name', target: 'deliveryConsignee' },
    //             { source: 'phone_number', target: 'deliveryPhone' },
    //         ],
    //         multi:true
    //     }
    // },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入收货地址!'},
          ];
     },
  },
  {
    label: '收货人',
    field: 'deliveryConsignee',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入收货人!'},
          ];
     },
  },
  {
    label: '联系电话',
    field: 'deliveryPhone',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入联系电话!'},
          ];
     },
  },
  {
    label: '订单总价',
    field: 'orderTotal',
    component: 'InputNumber',
    componentProps:{
      readonly: true,
    },

  },
  {
    label: '订单净额',
    field: 'orderNet',
    component: 'InputNumber',
    componentProps:{
      readonly: true,
    },
  },
  {
    label: '订单税金',
    field: 'orderTax',
    component: 'InputNumber',
    componentProps:{
      readonly: true,
    },
  },

  // {
  //   label: '财务审批状态',
  //   field: 'financeApproveStatus',
  //   component: 'JDictSelectTag',
  //   componentProps:{
  //       dictCode:"approval_status"
  //    },
  // },
  // {
  //   label: '销售审批时间',
  //   field: 'salesApproveTime',
  //   component: 'DatePicker',
  //   componentProps: {
  //      showTime:true,
  //      valueFormat: 'YYYY-MM-DD HH:mm:ss'
  //    },
  // },
  // {
  //   label: '销售审批人id',
  //   field: 'salesApproverId',
  //   component: 'Input',
  // },
  // {
  //   label: '销售审批人',
  //   field: 'salesApproverName',
  //   component: 'Input',
  // },
  // {
  //   label: '财务审批人id',
  //   field: 'financeApproverId',
  //   component: 'Input',
  // },
  // {
  //   label: '财务审批人',
  //   field: 'financeApproverName',
  //   component: 'Input',
  // },
  // {
  //   label: '财务审批意见',
  //   field: 'financeApproveRemark',
  //   component: 'Input',
  // },
  // {
  //   label: '订单状态',
  //   field: 'orderStatus',
  //   component: 'JDictSelectTag',
  //   componentProps:{
  //       dictCode:"scm_order_status"
  //    },
  // },
  // {
  //   label: '发货状态',
  //   field: 'deliveryStatus',
  //   component: 'JDictSelectTag',
  //   componentProps:{
  //       dictCode:"wms_delivery_status"
  //    },
  // },
  {
    label: '应收日期',
    field: 'receivableDate',
    component: 'DatePicker',
    componentProps:{
      valueFormat: 'YYYY-MM-DD'
    },    
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入应收日期!'},
          ];
     },
  },
  // {
  //   label: '结算状态',
  //   field: 'settleStatus',
  //   component: 'JDictSelectTag',
  //   componentProps:{
  //       dictCode:"fms_settlement_status"
  //    },
  // },
  // {
  //   label: '已开票金额',
  //   field: 'invoiceAmount',
  //   component: 'InputNumber',
  // },
  {
    label: '客户订单号',
    field: 'customerOrderNo',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
  {
    label: '业务审批状态',
    field: 'salesApproveStatus',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"approval_status"
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入审批状态!'},
      ];
    },
  },
  {
    label: '业务审批意见',
    field: 'salesApproveRemark',
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
export const salesOrderDetailColumns: JVxeColumn[] = [
    {
      title:'产品id',
      key:'productId',
      type:JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      visible:false,
    },
    {
      title: '产品编码',
      key: 'productCode',
      type: JVxeTypes.slot,
      slotName: 'productCodeSlot', // 定义插槽名称
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '产品名称',
      key: 'productName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '单位',
      key: 'unit',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mis_unit where del_flag='0' and status='1',unit,unit",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:"kg",
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '数量',
      key: 'orderQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '单价',
      key: 'unitPrice',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '税率(%)',
      key: 'taxRate',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:13,
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },

    {
      title: '金额',
      key: 'detailAmount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      disabled:true,
    },
    {
      title: '不含税金额',
      key: 'netAmount',         // 不含税金额
      type: JVxeTypes.inputNumber,
      width:"120px",
      disabled:true,
    },
    {
      title: '税额',
      key: 'taxAmount',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:0,
      disabled:true,
    },
    // {
    //   title: '报价单id',
    //   key: 'offerId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '报价单明细id',
    //   key: 'offerDetailId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },

    // {
    //   title: '关联原订单明细id',
    //   key: 'returnSourceId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },

    {
      title: '包装名称',
      key: 'packageName',
      type: JVxeTypes.input,
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
      title: '备注',
      key: 'remark',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '报价单号',
      key: 'offerNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '发货状态',
      key: 'deliveryStatus',
      type: JVxeTypes.select,
      options:[],
      dictCode:"wms_delivery_status",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:"0",
      disabled:true,
    },
    {
      title: '发货数量',
      key: 'deliveryQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      disabled:true,
    },
    // {
    //   title: '已分配库存数量',
    //   key: 'allocatedStockQty',
    //   type: JVxeTypes.inputNumber,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '已计划生产量',
    //   key: 'plannedQty',
    //   type: JVxeTypes.inputNumber,
    //   width: "200px",
    // },
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
      totalAmount: {title: '金额',order: 5,view: 'number', type: 'number',},
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

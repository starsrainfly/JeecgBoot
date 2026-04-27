import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
import { useUserStore } from '/@/store/modules/user';
import {getRateByCode} from "@/views/mis/scm/priceoffer/PriceOffer.api";
import {message} from "ant-design-vue";

const userStore = useUserStore();

const userInfo = userStore.getUserInfo;
let isAdmin = userInfo.roles?.includes('admin') || userInfo.username === 'admin';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '报价单号',
    align:"center",
    dataIndex: 'offerNo'
   },
   {
    title: '报价日期',
    align:"center",
    dataIndex: 'offerDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
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
    title: '审核状态',
    align:"center",
    dataIndex: 'approveStatus_dictText'
   },
   {
    title: '审核人',
    align:"center",
    dataIndex: 'approverName'
   },
   {
    title: '审核时间',
    align:"center",
    dataIndex: 'approveTime'
   },
   {
    title: '审核备注',
    align:"center",
    dataIndex: 'approveRemark'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
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
    label: '报价单号',
    field: 'offerNo',
    component: 'Input',
  },
  {
    label: '报价日期',
    field: 'offerDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  {
    label: '业务员',
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"sys_user where del_flag='0' and status='1',realname,id",
      disabled:!isAdmin,
    },
    defaultValue:userInfo.id
  },
  {
    label: '客户ID',
    field: 'customerId',
    component: 'Input',
    show:false
  },
  {
    label: '客户名称',
    field: 'customerName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return{
        setFieldsValue:setFieldsValue,
        code:"scm_customer",
        fieldConfig: [
          { source: 'id', target: 'customerId' },
          { source: 'customer_name', target: 'customerName' },
        ],
        multi:false
      }
    },
  },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '报价单号',
    field: 'offerNo',
    component: 'Input',
  },
  {
    label: '报价日期',
    field: 'offerDate',
    component: 'DatePicker',
    componentProps:{
      valueFormat: 'YYYY-MM-DD'
    },    
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入报价日期!'},
          ];
     },
  },
  {
    label: '客户ID',
    field: 'customerId',
    component: 'Input',
    show:false
  },
  {
    label: '客户名称',
    field: 'customerName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"scm_customer",
            fieldConfig: [
                { source: 'id', target: 'customerId' },
                { source: 'customer_name', target: 'customerName' },
            ],
            multi:false
        }
    },

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
        dictCode:"sys_user where del_flag='0' and status='1',realname,id",
      disabled:!isAdmin,
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入业务员!'},
          ];
     },
    defaultValue:userInfo.id,
  },
  {
    label: '业务员名称',
    field: 'salesmanName',
    component: 'Input',
    show:false
  },
  {
    label: '币种代码',
    field: 'currencyCode',
    component: 'JDictSelectTag',
    componentProps: ({ formActionType }) => {
      const { setFieldsValue } = formActionType;
      return {
        dictCode: 'mis_currency where del_flag=0 and status=1,currency_code,currency_code',
        // 币种变化时自动获取汇率
        onChange: async (currencyCode: string) => {
          if (!currencyCode) {
            setFieldsValue({ exchangeRate: undefined });
            return;
          }
          // 调用接口获取汇率
          const res = await getRateByCode(currencyCode);
          console.log("res:" + res);
          setFieldsValue({ exchangeRate: res });

        },
      };
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
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入币种汇率!'},
      ];
    },
  },
  {
    label: '审核状态',
    field: 'approveStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"approval_status"
     },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入审核状态!'},
      ];
    },
  },
  // {
  //   label: '审核人ID',
  //   field: 'approverId',
  //   component: 'Input',
  // },
  // {
  //   label: '审核人名称',
  //   field: 'approverName',
  //   component: 'Input',
  // },
  // {
  //   label: '审核通过时间',
  //   field: 'approveTime',
  //   component: 'DatePicker',
  //   componentProps: {
  //      showTime:true,
  //      valueFormat: 'YYYY-MM-DD HH:mm:ss'
  //    },
  // },
  {
    label: '审核备注',
    field: 'approveRemark',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'status',
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
export const priceOfferDetailColumns: JVxeColumn[] = [
    {
      title: '产品ID',
      key: 'productId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
      visible:false
    },
    {
      title: '产品编码',
      key: 'productCode',
      type: JVxeTypes.popup,
      popupCode:"mdm_product_select",
      fieldConfig: [
        { source: 'id', target: 'productId' },
        { source: 'product_code', target: 'productCode' },
        { source: 'product_name', target: 'productName' },
        { source: 'product_spec', target: 'productSpec' },
        { source: 'product_color',target: 'productColor'},
      ],

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
    },
    {
      title: '产品规格',
      key: 'productSpec',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  {
    title:'产品颜色',
    key:'productColor',
    type:JVxeTypes.input,
    width:"200px",
    placeholder: '请输入${title}',
    defaultValue:'',
  },
  {
    title: '定制编码',
    key: 'customProductCode',
    type: JVxeTypes.input,
    width:"200px",
    placeholder: '请输入${title}',
    defaultValue:'',
  },
    {
      title: '定制名称',
      key: 'customProductName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '定制规格',
      key: 'customProductSpec',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '价格类型',
      key: 'priceType',
      type: JVxeTypes.select,
      options:[],
      dictCode:"mdm_price_type",
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
      title: '包装ID',
      key: 'packageId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      visible:false
    },
    {
      title: '包装编码',
      key: 'packageCode',
      type: JVxeTypes.popup,
      popupCode:"mdm_package_select",
      fieldConfig: [
        { source: 'id', target: 'packageId' },
        { source: 'material_code', target: 'packageCode' },
        { source: 'material_name', target: 'packageName' },
        { source: 'description', target: 'packageSpec' },
        { source: 'package_capacity', target: 'packageCapacity'},
        { source:'package_capacity_unit',target: 'packageCapacityUnit'},
      ],

      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
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
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '容量单位',
      key: 'packageCapacityUnit',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '数量区间-最小',
      key: 'qtyMin',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:50,
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '数量区间-最大',
      key: 'qtyMax',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:99999,
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '生效日期',
      key: 'effectiveDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '失效日期',
      key: 'expiryDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },

    {
      title: '最小起订量',
      key: 'minOrderQty',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '数量步长',
      key: 'qtyStep',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '状态',
      key: 'status',
      type: JVxeTypes.select,
      options:[],
      dictCode:"status",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '禁用原因',
      key: 'disabledReason',
      type: JVxeTypes.input,
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
  ]


// 高级查询数据
export const superQuerySchema = {
  offerNo: {title: '报价单号',order: 0,view: 'text', type: 'string',},
  offerDate: {title: '报价日期',order: 1,view: 'date', type: 'string',},
  customerName: {title: '客户名称',order: 3,view: 'popup', type: 'string',code: 'scm_customer', orgFields: 'customer_name', destFields: 'customerName', popupMulti: false,},
  salesmanId: {title: '业务员',order: 4,view: 'list', type: 'string',dictTable: "sys_user where del_flag='0' and status='1'", dictCode: 'id', dictText: 'realname',},
  salesmanName: {title: '业务员名称',order: 5,view: 'text', type: 'string',},
  currencyCode: {title: '币种代码',order: 6,view: 'list', type: 'string',dictTable: "mis_currency where del_flag='0' and status='1'", dictCode: 'currency_code', dictText: 'currency_code',},
  exchangeRate: {title: '汇率',order: 7,view: 'number', type: 'number',},
  approveStatus: {title: '审核状态',order: 8,view: 'list', type: 'string',dictCode: 'approval_status',},
  approverName: {title: '审核人名称',order: 10,view: 'text', type: 'string',},
  approveTime: {title: '审核通过时间',order: 11,view: 'datetime', type: 'string',},
  approveRemark: {title: '审核备注',order: 12,view: 'text', type: 'string',},
  status: {title: '状态',order: 13,view: 'list', type: 'string',dictCode: 'status',},
  remark: {title: '备注',order: 14,view: 'text', type: 'string',},
  //子表高级查询
  priceOfferDetail: {
    title: '报价单明细',
    view: 'table',
    fields: {
        productCode: {title: '产品编码',order: 1,view: 'popup', type: 'string',code: 'mdm_product_select', orgFields: 'product_code', destFields: 'productCode', popupMulti: false,},
        productName: {title: '产品标准名称',order: 2,view: 'text', type: 'string',},
        productSpec: {title: '产品规格',order: 3,view: 'text', type: 'string',},
        customProductName: {title: '客户定制产品名称',order: 4,view: 'text', type: 'string',},
        customProductSpec: {title: '客户定制规格',order: 5,view: 'text', type: 'string',},
        unit: {title: '单位',order: 6,view: 'list', type: 'string',dictTable: "mis_unit where del_flag='0' and status='1'", dictCode: 'unit', dictText: 'unit',},
        packageCode: {title: '包装编码',order: 8,view: 'popup', type: 'string',code: 'mdm_package_select', orgFields: 'material_code', destFields: 'packageCode', popupMulti: false,},
        packageName: {title: '包装名称',order: 9,view: 'text', type: 'string',},
        packageSpec: {title: '包装规格',order: 10,view: 'text', type: 'string',},
        priceType: {title: '价格类型',order: 11,view: 'list', type: 'string',dictCode: 'mdm_price_type',},
        qtyMin: {title: '数量区间-最小',order: 12,view: 'number', type: 'number',},
        qtyMax: {title: '数量区间-最大',order: 13,view: 'number', type: 'number',},
        effectiveDate: {title: '生效日期',order: 14,view: 'date', type: 'string',},
        expiryDate: {title: '失效日期',order: 15,view: 'date', type: 'string',},
        unitPrice: {title: '单价（含税）',order: 16,view: 'number', type: 'number',},
        taxRate: {title: '税率(%)',order: 17,view: 'number', type: 'number',},
        minOrderQty: {title: '最小起订量',order: 18,view: 'number', type: 'number',},
        qtyStep: {title: '数量步长',order: 19,view: 'number', type: 'number',},
        status: {title: '状态',order: 20,view: 'list', type: 'string',dictCode: 'status',},
        disabledReason: {title: '禁用原因',order: 21,view: 'text', type: 'string',},
        remark: {title: '备注',order: 22,view: 'text', type: 'string',},
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

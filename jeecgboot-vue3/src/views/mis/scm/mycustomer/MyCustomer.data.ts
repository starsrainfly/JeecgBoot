import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
import {getAreaTextByCode} from "@/components/Form/src/utils/Area";
import {useUserStore} from "@/store/modules/user";
const userStore = useUserStore();

const userInfo = userStore.getUserInfo;
let isAdmin = userInfo.roles?.includes('admin') || userInfo.username === 'admin';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '客户编码',
    align:"center",
    sorter: true,
    dataIndex: 'customerCode'
   },
   {
    title: '客户名称',
    align:"center",
    sorter: true,
    dataIndex: 'customerName'
   },
   {
    title: '客户评分',
    align:"center",
    sorter: true,
    dataIndex: 'customerRating'
   },
   {
    title: '法人',
    align:"center",
    sorter: true,
    dataIndex: 'corporation'
   },
   {
    title: '客户类型',
    align:"center",
    sorter: true,
    dataIndex: 'customerType_dictText'
   },
  {
    title:'业务员',
    align:'center',
    dataIndex:'salesmanId_dictText'
  },
   {
    title: '账期（天）',
    align:"center",
    sorter: true,
    dataIndex: 'paymentDays'
   },
    {
      title: '付款方式',
      align:"center",
      dataIndex: 'paymentType_dictText'
    },
   {
    title: '省市区',
    align:"center",
    dataIndex: 'districtName'
   },
   {
    title: '地址',
    align:"center",
    dataIndex: 'address',
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '审核时间',
    align:"center",
    dataIndex: 'approvalDate'
   },
   {
    title: '审核状态',
    align:"center",
    dataIndex: 'approvalStatus_dictText'
   },
   {
    title:'审核意见',
    align:"center",
    dataIndex:'approvalRemark'
   },
   {
    title: '审核人',
    align:"center",
    dataIndex: 'approvalUser'
   },
  {
    title: '客户介绍',
    align:"center",
    dataIndex: 'about'
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
    label: "客户编码",
    field: "customerCode",
    component: 'JInput',
  },
  {
    label: "客户名称",
    field: "customerName",
    component: 'JInput',
  },
	{
      label: "客户评分",
      field: "customerRating",
      component: 'InputNumber',
      //colProps: {span: 6},
 	},
  {
    label: "法人",
    field: "corporation",
    component: 'JInput',
  },
	{
      label: "客户类型",
      field: "customerType",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"customer_type"
      },
      //colProps: {span: 6},
 	},
  {
    label: '贸易类型',
    field: 'tradeType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'scm_trade_type',  // N-内贸, W-外贸
    },

  },
	{
      label: "账期（天）",
      field: "paymentDays",
      component: 'Input',
      //colProps: {span: 6},
 	},


  {
    label: "省市区",
    field: "districtCode",
    component: 'JAreaLinkage',
    componentProps: {
      saveCode: 'region',
    },
    //colProps: {span: 6},
  },

  {
    label:"业务员",
    field: 'salesmanId',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"sys_user where del_flag='0' and status='1',realname,id",
      disabled:!isAdmin,
    },
    defaultValue:userInfo.id
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
    label: '审核状态',
    field: 'approvalStatus',
    component:'JDictSelectTag',
    componentProps:{
      dictCode:"approval_status",
    },
  },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '客户编码',
    field: 'customerCode',
    component: 'Input',
    componentProps:{
      readonly:true
    }
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
    label: '客户评分',
    field: 'customerRating',
    component: 'Rate',
    componentProps:{
      disabled:!isAdmin,
    },
    defaultValue:2,
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入客户评分!'},
          ];
     },

  },
  {
    label: '法人',
    field: 'corporation',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入法人!'},
          ];
     },
  },
  {
    label: '客户类型',
    field: 'customerType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"customer_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入客户类型!'},
          ];
     },
  },
  {
    label: '贸易类型',
    field: 'tradeType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'scm_trade_type',  // N-内贸, W-外贸
    },
    defaultValue:'N',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入贸易类型!'},
      ];
    },
  },
  {
    label: '账期（天）',
    field: 'paymentDays',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入账期（天）!'},
          ];
     },
  },
  {
    label: '付款方式',
    field: 'paymentType',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"payment_type"
    },
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入付款方式!'},
      ];
    },
  },


  {
    label: '省市区',
    field: 'districtCode',
    component: 'JAreaLinkage',
    componentProps: {
      saveCode: 'region',
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入省名称!'},
          ];
     },
  },

  {
    label: '公司地址',
    field: 'address',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入地址!'},
          ];
     },
  },
  {
    label:'业务员',
    field:'salesmanId',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"sys_user where del_flag='0' and status='1',realname,id",
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
    label: '国家/地区',
    field: 'regionCode',        // 外贸：存2位国家码
    component: 'JDictSelectTag',
    //show: ({ values }) => values.tradeType === 'W',
    componentProps: {
      dictCode: 'mdm_country_code', // US/JP/DE...
    },
    defaultValue:'CN',
    dynamicRules: ({model,schema}) => {
      return [
        { required: true, message: '请输入国家/地区!'},
      ];
    },
  },
  {
    label: '状态',
    field: 'status',
    defaultValue: "1",
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
    label: '客户介绍',
    field: 'about',
    component: 'InputTextArea',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
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
export const customerAddressColumns: JVxeColumn[] = [
    {
      title: '省市区',
      key: 'districtCode',
      type: JVxeTypes.pca,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '详细地址',
      key: 'address',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    // {
    //   title: '区/县名称',
    //   key: 'districtName',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    {
      title: '收货人',
      key: 'receiverName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '电话',
      key: 'receiverPhone',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '默认',
      key: 'isDefault',
      type: JVxeTypes.select,
      options:[],
      dictCode:"yn",
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
      defaultValue:"1",
    },
  ]
export const customerQualificationColumns: JVxeColumn[] = [
    // {
    //   title: '客户ID',
    //   key: 'customerId',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    // {
    //   title: '客户编码（冗余）',
    //   key: 'customerCode',
    //   type: JVxeTypes.input,
    //   width:"200px",
    //   placeholder: '请输入${title}',
    //   defaultValue:'',
    // },
    {
      title: '资质名称',
      key: 'qualificationName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '资质编号/证书编号',
      key: 'qualificationNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '资质类型',
      key: 'qualificationType',
      type: JVxeTypes.select,
      options:[],
      dictCode:"qualification_type",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '生效日期',
      key: 'beginDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '到期日期',
      key: 'expiryDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '发证机构',
      key: 'issuingAuthority',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '资质图片',
      key: 'qualificationPic',
      type: JVxeTypes.image,
      token:true,
      responseName:"message",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '资质文件',
      key: 'qualificationFile',
      type: JVxeTypes.file,
      token:true,
      responseName:"message",
      width:"200px",
      placeholder: '请选择文件',
      defaultValue:'',
    },
    {
      title: '资质状态',
      key: 'qualificationStatus',
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
      title: '是否关键资质',
      key: 'isKeyQualification',
      type: JVxeTypes.select,
      options:[],
      dictCode:"yn",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
      validateRules: [
        { required: true, message: '${title}不能为空' },
      ],
    },
    {
      title: '预警提前天数（预留）',
      key: 'alertDays',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'30',
    },
    {
      title: '预警状态',
      key: 'alertStatus',
      type: JVxeTypes.select,
      options:[],
      dictCode:"scm_alert_status",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'0',
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
export const customerContactColumns: JVxeColumn[] = [
    {
      title: '联系人',
      key: 'name',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '职位',
      key: 'position',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '电话',
      key: 'phone',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '邮箱',
      key: 'email',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '微信',
      key: 'wechat',
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
    {
      title: '联系人类型',
      key: 'contactType',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]
export const customerSalesmanColumns: JVxeColumn[] = [
    {
      title: '业务员',
      key: 'salesmanId',
      type: JVxeTypes.select,
      options:[],
      dictCode:"sys_user where del_flag='0' and status='1',realname,id",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
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
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  customerCode: {title: '客户编码',order: 0,view: 'text', type: 'string',},
  customerName: {title: '客户名称',order: 1,view: 'text', type: 'string',},
  customerRating: {title: '客户评分',order: 2,view: 'number', type: 'number',},
  corporation: {title: '法人',order: 3,view: 'text', type: 'string',},
  customerType: {title: '客户类型',order: 4,view: 'list', type: 'string',dictCode: 'customer_type',},
  about: {title: '客户介绍',order: 5,view: 'textarea', type: 'string',},
  remark: {title: '备注',order: 6,view: 'textarea', type: 'string',},
  paymentDays: {title: '账期（天）',order: 7,view: 'text', type: 'string',},
  provinceCode: {title: '省编码',order: 8,view: 'text', type: 'string',},
  provinceName: {title: '省名称',order: 9,view: 'pca', type: 'string',},
  cityCode: {title: '市编码',order: 10,view: 'text', type: 'string',},
  cityName: {title: '市名称',order: 11,view: 'text', type: 'string',},
  districtCode: {title: '区编码',order: 12,view: 'text', type: 'string',},
  districtName: {title: '区名称',order: 13,view: 'text', type: 'string',},
  address: {title: '地址',order: 14,view: 'text', type: 'string',},
  status: {title: '状态',order: 15,view: 'list', type: 'string',dictCode: 'status',},
  approvalDate: {title: '审核时间',order: 16,view: 'datetime', type: 'string',},
  approvalStatus: {title: '审核状态',order: 17,view: 'list', type: 'string',dictCode: 'approval_status',},
  paymentType: {title: '付款方式',order: 18,view: 'list', type: 'string',dictCode: 'payment_type',},
  approvalUser: {title: '审核人',order: 19,view: 'text', type: 'string',},
  //子表高级查询
  customerAddress: {
    title: '客户地址',
    view: 'table',
    fields: {
        provinceName: {title: '省名称',order: 1,view: 'pca', type: 'string',},
        cityName: {title: '市名称',order: 3,view: 'text', type: 'string',},
        districtName: {title: '区/县名称',order: 5,view: 'text', type: 'string',},
        receiverName: {title: '收货人',order: 6,view: 'text', type: 'string',},
        receiverPhone: {title: '电话',order: 7,view: 'text', type: 'string',},
        address: {title: '详细地址',order: 8,view: 'text', type: 'string',},
        isDefault: {title: '默认',order: 9,view: 'list', type: 'string',dictCode: 'yn',},
        status: {title: '状态',order: 10,view: 'list', type: 'string',dictCode: 'status',},
    }
  },
  customerQualification: {
    title: '客户质证',
    view: 'table',
    fields: {
        customerId: {title: '客户ID',order: 0,view: 'text', type: 'string',},
        customerCode: {title: '客户编码（冗余）',order: 1,view: 'text', type: 'string',},
        qualificationName: {title: '资质名称',order: 2,view: 'text', type: 'string',},
        qualificationNo: {title: '资质编号/证书编号',order: 3,view: 'text', type: 'string',},
        qualificationType: {title: '资质类型',order: 4,view: 'list', type: 'string',dictCode: 'qualification_type',},
        beginDate: {title: '生效日期',order: 5,view: 'date', type: 'string',},
        expiryDate: {title: '到期日期',order: 6,view: 'date', type: 'string',},
        issuingAuthority: {title: '发证机构',order: 7,view: 'text', type: 'string',},
        qualificationPic: {title: '资质图片',order: 8,view: 'image', type: 'string',},
        qualificationFile: {title: '资质文件',order: 9,view: 'file', type: 'string',},
        qualificationStatus: {title: '资质状态：0-过期 1-有效 2-即将过期',order: 10,view: 'text', type: 'string',},
        isKeyQualification: {title: '是否关键资质：0-否 1-是',order: 11,view: 'list', type: 'string',dictCode: 'yn',},
        alertDays: {title: '预警提前天数（预留）',order: 12,view: 'number', type: 'number',},
        alertStatus: {title: '预警状态：0-正常 1-预警中 2-已忽略（预留）',order: 13,view: 'text', type: 'string',},
        remark: {title: '备注',order: 14,view: 'text', type: 'string',},
    }
  },
  customerContact: {
    title: '客户联系人',
    view: 'table',
    fields: {
        name: {title: '联系人',order: 0,view: 'text', type: 'string',},
        position: {title: '职位',order: 1,view: 'text', type: 'string',},
        phone: {title: '电话',order: 2,view: 'text', type: 'string',},
        email: {title: '邮箱',order: 3,view: 'text', type: 'string',},
        wechat: {title: '微信',order: 4,view: 'text', type: 'string',},
        remark: {title: '备注',order: 5,view: 'text', type: 'string',},
        contactType: {title: '联系人类型',order: 6,view: 'text', type: 'string',},
    }
  },
  customerSalesman: {
    title: '客户销售员',
    view: 'table',
    fields: {
        salesmanId: {title: '业务员',order: 0,view: 'list', type: 'string',dictCode: '',},
        status: {title: '状态',order: 1,view: 'list', type: 'string',dictCode: 'status',},
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

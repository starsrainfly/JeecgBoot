import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '供应商编码',
    align:"center",
    dataIndex: 'supplierCode'
   },
   {
    title: '供应商名称',
    align:"center",
    dataIndex: 'supplierName'
   },
   {
    title: '简称',
    align:"center",
    dataIndex: 'shortName'
   },
   {
    title: '注册税号',
    align:"center",
    dataIndex: 'taxRegistrationNo'
   },
   {
    title: '注册类型',
    align:"center",
    dataIndex: 'registeredCapital'
   },
   {
    title: '开户行',
    align:"center",
    dataIndex: 'openBank'
   },
   {
    title: '法人',
    align:"center",
    dataIndex: 'legalPerson'
   },
   {
    title: '账号',
    align:"center",
    dataIndex: 'accountNo'
   },
   {
    title: '账户名称',
    align:"center",
    dataIndex: 'accountName'
   },
   {
    title: '供应商类型',
    align:"center",
    dataIndex: 'supplierType_dictText'
   },
   {
    title: '账期(天)',
    align:"center",
    dataIndex: 'paymentDays'
   },
   {
    title: '等级',
    align:"center",
    dataIndex: 'level'
   },
   {
    title: '省市区',
    align:"center",
    dataIndex: 'areaId',
   },
   {
    title: '供应商地址',
    align:"center",
    dataIndex: 'supplierAddress'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '审核标识',
    align:"center",
    dataIndex: 'auditFlag_dictText'
   },
   {
    title: '审核人',
    align:"center",
    dataIndex: 'auditor'
   },
   {
    title: '审核时间',
    align:"center",
    dataIndex: 'auditDate'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: "供应商编码",
    field: "supplierCode",
    component: 'JInput',
  },
  {
    label: "供应商名称",
    field: "supplierName",
    component: 'JInput',
  },
  {
    label: "简称",
    field: "shortName",
    component: 'JInput',
  },
  {
    label: "法人",
    field: "legalPerson",
    component: 'JInput',
  },
	{
      label: "供应商类型",
      field: "supplierType",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"supplier_type"
      },
      //colProps: {span: 6},
 	},
	{
      label: "账期(天)",
      field: "paymentDays",
      component: 'InputNumber',
      //colProps: {span: 6},
 	},
	{
      label: "等级",
      field: "level",
      component: 'InputNumber',
      //colProps: {span: 6},
 	},
	{
      label: "省市区",
      field: "areaId",
      component: 'JAreaLinkage',
      componentProps: {
        saveCode: 'region',
      },
      //colProps: {span: 6},
 	},
  {
    label: "供应商地址",
    field: "supplierAddress",
    component: 'JInput',
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
    label: '供应商编码',
    field: 'supplierCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入供应商编码!'},
          ];
     },
  },
  {
    label: '供应商名称',
    field: 'supplierName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入供应商名称!'},
          ];
     },
  },
  {
    label: '简称',
    field: 'shortName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入简称!'},
          ];
     },
  },
  {
    label: '注册税号',
    field: 'taxRegistrationNo',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入注册税号!'},
          ];
     },
  },
  {
    label: '注册类型',
    field: 'registeredCapital',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入注册类型!'},
          ];
     },
  },
  {
    label: '开户行',
    field: 'openBank',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入开户行!'},
          ];
     },
  },
  {
    label: '法人',
    field: 'legalPerson',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入法人!'},
          ];
     },
  },
  {
    label: '账号',
    field: 'accountNo',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入账号!'},
          ];
     },
  },
  {
    label: '账户名称',
    field: 'accountName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入账户名称!'},
          ];
     },
  },
  {
    label: '供应商类型',
    field: 'supplierType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"supplier_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入供应商类型!'},
          ];
     },
  },
  {
    label: '账期(天)',
    field: 'paymentDays',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入账期(天)!'},
          ];
     },
  },
  {
    label: '等级',
    field: 'level',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入等级!'},
          ];
     },
  },
  {
    label: '省市区',
    field: 'areaId',
    component: 'JAreaLinkage',
    componentProps: {
      saveCode: 'region',
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入省市区!'},
          ];
     },
  },
  {
    label: '供应商地址',
    field: 'supplierAddress',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入供应商地址!'},
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
    label: '审核标识',
    field: 'auditFlag',
    defaultValue: 0,
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"approval_status"
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
export const supplierQualificationColumns: JVxeColumn[] = [
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
      title: '开始日期',
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
      title: '有效期',
      key: 'validity',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
        ],
    },
    {
      title: '有效日期',
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
      title: '图片',
      key: 'qualificationPic',
      type: JVxeTypes.image,
      token:true,
      responseName:"message",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '文件',
      key: 'qualificationFile',
      type: JVxeTypes.file,
      token:true,
      responseName:"message",
      width:"200px",
      placeholder: '请选择文件',
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
    {
      title: '备注',
      key: 'remark',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]
export const supplierContactColumns: JVxeColumn[] = [
    {
      title: '联系人',
      key: 'contact',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '职位',
      key: 'job',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '固定电话',
      key: 'telNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '手机号码',
      key: 'mobileNo',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '传真',
      key: 'fax',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '电子邮箱',
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
      title: 'QQ号',
      key: 'qq',
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
    {
      title: '状态',
      key: 'status',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]
export const supplierPurchaserColumns: JVxeColumn[] = [
    {
      title: '采购员',
      key: 'purchaser',
      type: JVxeTypes.userSelect,
      props:{
      },
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
  supplierCode: {title: '供应商编码',order: 0,view: 'text', type: 'string',},
  supplierName: {title: '供应商名称',order: 1,view: 'text', type: 'string',},
  shortName: {title: '简称',order: 2,view: 'text', type: 'string',},
  taxRegistrationNo: {title: '注册税号',order: 3,view: 'text', type: 'string',},
  registeredCapital: {title: '注册类型',order: 4,view: 'text', type: 'string',},
  openBank: {title: '开户行',order: 5,view: 'text', type: 'string',},
  legalPerson: {title: '法人',order: 6,view: 'text', type: 'string',},
  accountNo: {title: '账号',order: 7,view: 'text', type: 'string',},
  accountName: {title: '账户名称',order: 8,view: 'text', type: 'string',},
  supplierType: {title: '供应商类型',order: 9,view: 'list', type: 'string',dictCode: 'supplier_type',},
  paymentDays: {title: '账期(天)',order: 10,view: 'number', type: 'number',},
  level: {title: '等级',order: 11,view: 'number', type: 'number',},
  areaId: {title: '省市区',order: 12,view: 'pca', type: 'string',},
  supplierAddress: {title: '供应商地址',order: 13,view: 'text', type: 'string',},
  remark: {title: '备注',order: 14,view: 'text', type: 'string',},
  status: {title: '状态',order: 15,view: 'list', type: 'string',dictCode: 'status',},
  auditFlag: {title: '审核标识',order: 16,view: 'number', type: 'number',dictCode: 'approval_status',},
  auditor: {title: '审核人',order: 17,view: 'text', type: 'string',},
  auditDate: {title: '审核时间',order: 18,view: 'datetime', type: 'string',},
  //子表高级查询
  supplierQualification: {
    title: '供应商质证表',
    view: 'table',
    fields: {
        qualificationName: {title: '资质名称',order: 0,view: 'text', type: 'string',},
        beginDate: {title: '开始日期',order: 1,view: 'date', type: 'string',},
        validity: {title: '有效期',order: 2,view: 'number', type: 'number',},
        expiryDate: {title: '有效日期',order: 3,view: 'date', type: 'string',},
        qualificationType: {title: '资质类型',order: 4,view: 'list', type: 'string',dictCode: 'qualification_type',},
        qualificationPic: {title: '图片',order: 5,view: 'image', type: 'string',},
        qualificationFile: {title: '文件',order: 6,view: 'file', type: 'string',},
        status: {title: '状态',order: 7,view: 'list', type: 'string',dictCode: 'status',},
        remark: {title: '备注',order: 8,view: 'text', type: 'string',},
    }
  },
  supplierContact: {
    title: '供应商联系人',
    view: 'table',
    fields: {
        contact: {title: '联系人',order: 0,view: 'text', type: 'string',},
        job: {title: '职位',order: 1,view: 'text', type: 'string',},
        telNo: {title: '固定电话',order: 2,view: 'text', type: 'string',},
        mobileNo: {title: '手机号码',order: 3,view: 'text', type: 'string',},
        fax: {title: '传真',order: 4,view: 'text', type: 'string',},
        email: {title: '电子邮箱',order: 5,view: 'text', type: 'string',},
        wechat: {title: '微信',order: 6,view: 'text', type: 'string',},
        qq: {title: 'QQ号',order: 7,view: 'text', type: 'string',},
        contactType: {title: '联系人类型',order: 8,view: 'text', type: 'string',},
        status: {title: '状态',order: 9,view: 'text', type: 'string',},
    }
  },
  supplierPurchaser: {
    title: '供应商采购员',
    view: 'table',
    fields: {
        purchaser: {title: '采购员',order: 0,view: 'sel_user', type: 'string',},
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
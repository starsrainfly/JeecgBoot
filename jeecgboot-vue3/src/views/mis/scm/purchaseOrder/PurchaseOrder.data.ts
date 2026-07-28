import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'

import {useUserStore} from '/@/store/modules/user';

const userStore = useUserStore();
const userInfo = userStore.getUserInfo;

//列表数据
export const columns: BasicColumn[] = [
  { title: '采购单号', align:"center", dataIndex: 'orderNo' },
  {
    title: '申请日期', align:"center", dataIndex: 'orderDate',
    customRender:({text}) => !text ? "" : (text.length > 10 ? text.substr(0,10) : text),
  },
  { title: '供应商', align:"center", dataIndex: 'supplierName' },
  { title: '采购员', align:"center", dataIndex: 'purchaserName' },
  { title: '币种', align:"center", dataIndex: 'currencyCode_dictText' },
  { title: '汇率', align:"center", dataIndex: 'exchangeRate' },
  { title: '含税总额', align:"center", dataIndex: 'orderTotal' },
  {
    title: '要求到货日期', align:"center", dataIndex: 'expectedDate',
    customRender:({text}) => !text ? "" : (text.length > 10 ? text.substr(0,10) : text),
  },
  { title: '业务状态', align:"center", dataIndex: 'status_dictText' },
  { title: '审核状态', align:"center", dataIndex: 'approveStatus_dictText' },
  { title: '审批人', align:"center", dataIndex: 'approveName' },
  { title: '审批时间', align:"center", dataIndex: 'approveTime' },
  { title: '备注', align:"center", dataIndex: 'remark' },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: "采购单号",
    field: "orderNo",
    component: 'Input',
  },
  {
    label: "供应商",
    field: "supplierName",
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return{
        setFieldsValue:setFieldsValue,
        code:"scm_supplier",
        fieldConfig: [
          { source: 'id', target: 'supplierId' },
          { source: 'supplier_name', target: 'supplierName' },
        ],
        multi:true
      }
    },
  },
  {
    label: "业务状态",
    field: "status",
    component: 'JSelectMultiple',
    componentProps:{ dictCode:"scm_purchase_status" },
  },
  {
    label: "审核状态",
    field: "approveStatus",
    component: 'JDictSelectTag',
    componentProps:{ dictCode:"approval_status" },
  },
];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '采购单号',
    field: 'orderNo',
    component: 'Input',
    dynamicDisabled: true, //后端自动生成
  },
  {
    label: '申请日期',
    field: 'orderDate',
    component: 'DatePicker',
    componentProps:{ valueFormat: 'YYYY-MM-DD' },
    dynamicRules: () => [{ required: true, message: '请选择申请日期!'}],
  },
  {
    label: '供应商id',
    field: 'supplierId',
    component: 'Input',
    show: false,
  },
  {
    label: '供应商',
    field: 'supplierName',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
      const {setFieldsValue} = formActionType;
      return{
        setFieldsValue:setFieldsValue,
        code:"scm_supplier",
        fieldConfig: [
          { source: 'id', target: 'supplierId' },
          { source: 'supplier_name', target: 'supplierName' },
          { source: 'payment_days', target: 'paymentDays' },
        ],
        multi:false  //一张采购单只能一个供应商
      }
    },
    dynamicRules: () => [{ required: true, message: '请选择供应商!'}],
  },
  {
    label: '采购员',
    field: 'purchaserId',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"sys_user where status='1' and del_flag='0',realname,id"
    },
    defaultValue:userInfo.id,
    dynamicRules: () => [{ required: true, message: '请选择采购员!'}],
  },
  {
    label: '币种',
    field: 'currencyCode',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"mis_currency where del_flag='0' and status='1',currency_name,currency_code"
    },
    defaultValue: 'CNY',
    dynamicRules: () => [{ required: true, message: '请选择币种!'}],
  },
  {
    label: '汇率',
    field: 'exchangeRate',
    component: 'InputNumber',
    defaultValue: 1,
    dynamicRules: () => [{ required: true, message: '请输入汇率!'}],
  },
  {
    label: '含税总额',
    field: 'orderTotal',
    component: 'InputNumber',
    dynamicDisabled: true, //自动计算
  },
  {
    label: '不含税总额',
    field: 'orderNet',
    component: 'InputNumber',
    dynamicDisabled: true, //自动计算
  },
  {
    label: '税金',
    field: 'orderTax',
    component: 'InputNumber',
    dynamicDisabled: true, //自动计算
  },
  {
    label: '要求到货日期',
    field: 'expectedDate',
    component: 'DatePicker',
    componentProps:{ valueFormat: 'YYYY-MM-DD' },
  },
  {
    label: '付款账期(天)',
    field: 'paymentDays',
    component: 'InputNumber',
  },
  {
    label: '建议入库仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps:{ dictCode:"mis_warehouse,name,id" },
  },
  {
    label: '仓库名称',
    field: 'warehouseName',
    component: 'Input',
    show: false,
  },
  {
    label: '业务状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{ dictCode:"scm_purchase_status", disabled:true },
  },
  {
    label: '审核状态',
    field: 'approveStatus',
    component: 'JDictSelectTag',
    componentProps:{ dictCode:"approval_status", disabled:true },
  },
  {
    label: '审批人',
    field: 'approveName',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '审核备注',
    field: 'approveRemark',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false
  },
];

//子表表格配置
export const purchaseOrderDetailColumns: JVxeColumn[] = [
  {
    title: '采购单号',
    key: 'orderNo',
    type: JVxeTypes.input,
    width:"120px",
    visible:false
  },
  {
    title: '物料id',
    key: 'goodsId',
    type: JVxeTypes.input,
    width:"120px",
    visible:false,
    validateRules: [
      { required: true, message: '${title}不能为空' },
    ],
  },
  {
    title: '物料类型',
    key: 'goodsType',
    type: JVxeTypes.select,
    options:[],
    dictCode:"mdm_material_type",
    width:"120px",
    visible:false
  },
  {
    title: '需求来源',
    key: 'sourceType',
    type: JVxeTypes.select,
    options:[],
    dictCode:"scm_purchase_source",
    width:"120px",
    defaultValue:'MANUAL',
    visible:false
  },
  {
    title: '物料需求id',
    key: 'sourceRequirementId',
    type: JVxeTypes.input,
    width:"120px",
    visible:false
  },
  {
    title: '明细状态',
    key: 'detailStatus',
    type: JVxeTypes.select,
    options:[],
    dictCode:"scm_purchase_status",
    width:"110px",
    disabled:true,
  },
  // {
  //   title: '物料编码',
  //   key: 'goodsCode',
  //   type: JVxeTypes.popup,
  //  // popupCode:"mdm_material_select",
  //   fieldConfig: [
  //     { source: 'id', target: 'goodsId' },
  //     { source: 'material_code', target: 'goodsCode' },
  //     { source: 'material_name', target: 'goodsName' },
  //     { source: 'material_spec', target: 'goodsSpec' },
  //     { source: 'material_type', target: 'goodsType' },
  //   ],
  //   width:"160px",
  //   validateRules: [
  //     { required: true, message: '${title}不能为空' },
  //   ],
  // },
  {
    title: '物料编码',
    key: 'goodsCode',
    // 原来 popup 的配置全部删掉，改成 slot
    type: JVxeTypes.slot,
    slotName: 'goodsCode',
    width: "160px",
    validateRules: [
      { required: true, message: '${title}不能为空' },
    ],
  },
  {
    title: '物料名称',
    key: 'goodsName',
    type: JVxeTypes.input,
    width:"160px",
    validateRules: [
      { required: true, message: '${title}不能为空' },
    ],
  },
  {
    title: '规格型号',
    key: 'goodsSpec',
    type: JVxeTypes.input,
    width:"130px",
  },
  {
    title: '单位',
    key: 'unit',
    type: JVxeTypes.select,
    options:[],
    dictCode:"mis_unit where status='1' and del_flag='0',unit,unit",
    width:"100px",
    defaultValue:"kg",
    validateRules: [
      { required: true, message: '${title}不能为空' },
    ],
  },
  {
    title: '采购数量',
    key: 'orderQty',
    type: JVxeTypes.inputNumber,
    width:"120px",
    validateRules: [
      { required: true, message: '${title}不能为空' },
    ],
  },
  {
    title: '累计入库数量',
    key: 'receivedQty',
    type: JVxeTypes.inputNumber,
    disabled:true,
    width:"120px",
  },
  {
    title: '含税单价',
    key: 'unitPrice',
    type: JVxeTypes.inputNumber,
    width:"120px",
    validateRules: [
      { required: true, message: '${title}不能为空' },
    ],
  },
  {
    title: '税率(%)',
    key: 'taxRate',
    type: JVxeTypes.inputNumber,
    width:"100px",
    defaultValue: 13,
    validateRules: [
      { required: true, message: '${title}不能为空' },
    ],
  },
  {
    title: '含税金额',
    key: 'detailAmount',
    type: JVxeTypes.inputNumber,
    disabled:true,
    width:"120px",
  },
  {
    title: '不含税金额',
    key: 'netAmount',
    type: JVxeTypes.inputNumber,
    disabled:true,
    width:"120px",
  },
  {
    title: '税额',
    key: 'taxAmount',
    type: JVxeTypes.inputNumber,
    disabled:true,
    width:"110px",
  },
  {
    title: '期望到货日期',
    key: 'expectedDate',
    type: JVxeTypes.date,
    width:"150px",
  },
  {
    title: '备注',
    key: 'remark',
    type: JVxeTypes.input,
    width:"150px",
  },
];

// 高级查询数据
export const superQuerySchema = {
  orderNo: {title: '采购单号',order: 0,view: 'text', type: 'string',},
  orderDate: {title: '申请日期',order: 1,view: 'date', type: 'string',},
  supplierName: {title: '供应商',order: 2,view: 'popup', type: 'string',code: 'scm_supplier', orgFields: 'supplier_name', destFields: 'supplierName', popupMulti: false,},
  purchaserName: {title: '采购员',order: 3,view: 'text', type: 'string',},
  status: {title: '业务状态',order: 4,view: 'list', type: 'string',dictCode: 'scm_purchase_status',},
  approveStatus: {title: '审核状态',order: 5,view: 'list', type: 'string',dictCode:'approval_status'},
  remark: {title: '备注',order: 6,view: 'text', type: 'string',},
};

/** 行金额计算：含税=数量×单价，不含税=含税/(1+税率%)，税额=差值 */
export function calcDetailAmount(row) {
  const qty = Number(row.orderQty) || 0;
  const price = Number(row.unitPrice) || 0;
  const rate = (row.taxRate === '' || row.taxRate == null) ? 13 : Number(row.taxRate);
  const detailAmount = +(qty * price).toFixed(4);
  const netAmount = +(detailAmount / (1 + rate / 100)).toFixed(4);
  const taxAmount = +(detailAmount - netAmount).toFixed(4);
  return { detailAmount, netAmount, taxAmount };
}

/** 主表金额汇总 */
export function sumOrderAmounts(list) {
  let orderTotal = 0, orderNet = 0, orderTax = 0;
  (list || []).forEach(r => {
    orderTotal += Number(r.detailAmount) || 0;
    orderNet += Number(r.netAmount) || 0;
    orderTax += Number(r.taxAmount) || 0;
  });
  return {
    orderTotal: +orderTotal.toFixed(4),
    orderNet: +orderNet.toFixed(4),
    orderTax: +orderTax.toFixed(4),
  };
}

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[]{
  return formSchema;
}

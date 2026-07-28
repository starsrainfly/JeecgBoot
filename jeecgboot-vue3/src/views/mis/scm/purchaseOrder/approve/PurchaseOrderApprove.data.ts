import {FormSchema} from '/@/components/Table';

// 列表列直接复用采购订单的，不用维护两份
export { columns } from '../PurchaseOrder.data';

//查询数据：默认只看待审核
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
    label: "审核状态",
    field: "approveStatus",
    component: 'JDictSelectTag',
    componentProps:{ dictCode:"approval_status" },
    defaultValue: '0',   // 默认待审核
  },
];

//审核表单
export const approveFormSchema: FormSchema[] = [
  {
    label: '审核结果',
    field: 'approveStatus',
    component: 'RadioGroup',
    componentProps: {
      options: [
        { label: '通过', value: '1' },
        { label: '拒绝', value: '2' },
      ],
    },
    defaultValue: '1',
    dynamicRules: () => [{ required: true, message: '请选择审核结果!'}],
  },
  {
    label: '审核备注',
    field: 'approveRemark',
    component: 'InputTextArea',
    componentProps: { rows: 3, placeholder: '拒绝时必填审核备注' },
  },
];

import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '作业编号',
    align:"center",
    dataIndex: 'taskNo'
   },

   {
    title: '批次号',
    align:"center",
    dataIndex: 'batchNo'
   },

   {
    title: '触发方式',
    align:"center",
    dataIndex: 'triggerType_dictText'
   },

   {
    title: '产品编码',
    align:"center",
    dataIndex: 'productCode'
   },
   {
    title: '产品名称',
    align:"center",
    dataIndex: 'productName'
   },
   {
    title: '产品颜色',
    align:"center",
    dataIndex: 'productColor'
   },
   {
    title: '打印产品名称',
    align:"center",
    dataIndex: 'printProductName'
   },

   {
    title: '模板编码',
    align:"center",
    dataIndex: 'templateCode'
   },
   {
    title: '标签枚数',
    align:"center",
    dataIndex: 'labelQty'
   },
   {
    title: '打印份数',
    align:"center",
    dataIndex: 'copies'
   },
   {
    title: '标签宽度（mm）',
    align:"center",
    dataIndex: 'labelWidth'
   },
   {
    title: '标签高度（mm)',
    align:"center",
    dataIndex: 'labelHeight'
   },
   {
    title: '二维码内容',
    align:"center",
    dataIndex: 'qrContent'
   },
   {
    title: '二维码图片Base64',
    align:"center",
    dataIndex: 'qrImage'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '公司',
    align:"center",
    dataIndex: 'companyId_dictText'
   },
   // {
   //  title: '公司名称',
   //  align:"center",
   //  dataIndex: 'companyName'
   // },
   {
    title: '实际打印时间',
    align:"center",
    dataIndex: 'printTime'
   },
   // {
   //  title: '失败原因',
   //  align:"center",
   //  dataIndex: 'failReason'
   // },
   // {
   //  title: '打印机类型',
   //  align:"center",
   //  dataIndex: 'printerType'
   // },
   // {
   //  title: '打印机名称',
   //  align:"center",
   //  dataIndex: 'printerName'
   // },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remark'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "作业编号",
      field: 'taskNo',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "批次号",
      field: 'batchNo',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mes_production_batch_select",
            fieldConfig: [
                { source: 'id', target: 'batchId' },
                { source: 'batch_no', target: 'batchNo' },
                { source: 'product_id', target: 'productId' },
                { source: 'product_code', target: 'productCode' },
                { source: 'product_name', target: 'productName' },
                { source: 'product_color', target: 'productColor' },
                { source: 'product_name', target: 'printProductName' },
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "触发方式",
      field: 'triggerType',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mdm_trigger_type"
      },
      //colProps: {span: 6},
 	},
	{
      label: "产品编码",
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
                { source: 'product_color', target: 'productColor' },
                { source: 'product_name', target: 'printProductName' },
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "产品名称",
      field: 'productName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "打印产品名称",
      field: 'printProductName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "模板编码",
      field: 'templateCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_label_template",
            fieldConfig: [
                { source: 'id', target: 'templateId' },
                { source: 'template_code', target: 'templateCode' },
                { source: 'label_width', target: 'labelWidth' },
                { source: 'label_height', target: 'labelHeight' },
            ],
            multi:true
        }
    },

      //colProps: {span: 6},
 	},
	{
      label: "公司",
      field: 'companyId',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"sys_depart where del_flag='0' and org_category='1' and org_type='1',depart_name,id"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '作业编号',
    field: 'taskNo',
    component: 'Input',
    componentProps:{
      readonly:true
    }
  },
  {
    label: '生产批次ID',
    field: 'batchId',
    component: 'Input',
    show:false
  },
  {
    label: '批次号',
    field: 'batchNo',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mes_production_batch_select",
            fieldConfig: [
                { source: 'id', target: 'batchId' },
                { source: 'batch_no', target: 'batchNo' },
                { source: 'product_id', target: 'productId' },
                { source: 'product_code', target: 'productCode' },
                { source: 'product_name', target: 'productName' },
                { source: 'product_color', target: 'productColor' },
                { source: 'product_name', target: 'printProductName' },
            ],
            multi:false
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入批次号!'},
          ];
     },
  },

  {
    label: '触发方式',
    field: 'triggerType',
    defaultValue: "MANUAL",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mdm_trigger_type"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入触发方式!'},
          ];
     },
  },
  {
    label: '产品ID',
    field: 'productId',
    component: 'Input',
    show:false,
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
                { source: 'product_color', target: 'productColor' },
                { source: 'product_name', target: 'printProductName' },
            ],
            multi:false
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
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入产品名称!'},
          ];
     },
  },
  {
    label: '产品颜色',
    field: 'productColor',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入产品颜色!'},
          ];
     },
  },
  {
    label: '打印产品名称',
    field: 'printProductName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入打印产品名称!'},
          ];
     },
  },
  {
    label: '模板ID',
    field: 'templateId',
    component: 'Input',
    show:false

  },
  {
    label: '模板编码',
    field: 'templateCode',
    component: 'JPopup',
    componentProps: ({ formActionType }) => {
        const {setFieldsValue} = formActionType;
        return{
            setFieldsValue:setFieldsValue,
            code:"mdm_label_template",
            fieldConfig: [
                { source: 'id', target: 'templateId' },
                { source: 'template_code', target: 'templateCode' },
                { source: 'label_width', target: 'labelWidth' },
                { source: 'label_height', target: 'labelHeight' },
            ],
            multi:false
        }
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入模板编码!'},
          ];
     },
  },
  {
    label: '标签枚数',
    field: 'labelQty',
    component: 'InputNumber',
  },
  {
    label: '打印份数',
    field: 'copies',
    component: 'InputNumber',
  },
  {
    label: '标签宽度（mm）',
    field: 'labelWidth',
    component: 'InputNumber',
  },
  {
    label: '标签高度（mm)',
    field: 'labelHeight',
    component: 'InputNumber',
  },
  {
    label: '二维码内容',
    field: 'qrContent',
    component: 'InputTextArea',
  },
  {
    label: '二维码图片Base64',
    field: 'qrImage',
    component: 'InputTextArea',
  },
  {
    label: '状态',
    field: 'status',
    defaultValue: "PENDING",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mdm_print_status"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入状态!'},
          ];
     },
  },
  {
    label: '公司',
    field: 'companyId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sys_depart where del_flag='0' and org_category='1' and org_type='1',depart_name,id"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入公司!'},
          ];
     },
  },
  // {
  //   label: '公司名称',
  //   field: 'companyName',
  //   component: 'Input',
  // },
  // {
  //   label: '实际打印时间',
  //   field: 'printTime',
  //   component: 'DatePicker',
  //   componentProps: {
  //      showTime: true,
  //      valueFormat: 'YYYY-MM-DD HH:mm:ss'
  //    },
  // },
  // {
  //   label: '失败原因',
  //   field: 'failReason',
  //   component: 'Input',
  // },
  // {
  //   label: '打印机类型',
  //   field: 'printerType',
  //   component: 'Input',
  // },
  // {
  //   label: '打印机名称',
  //   field: 'printerName',
  //   component: 'Input',
  // },
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

// 高级查询数据
export const superQuerySchema = {
  taskNo: {title: '作业编号（LP20240401001）',order: 0,view: 'text', type: 'string',},
  batchId: {title: '生产批次ID',order: 1,view: 'text', type: 'string',},
  batchNo: {title: '批次号',order: 2,view: 'popup', type: 'string',code: 'mes_production_batch_select', orgFields: 'batch_no', destFields: 'batchNo', popupMulti: false,},
  productionTaskId: {title: '工单ID',order: 3,view: 'text', type: 'string',},
  productionTaskNo: {title: '工单号',order: 4,view: 'text', type: 'string',},
  triggerType: {title: '触发方式：MANUAL手动/AUTO完工自动',order: 5,view: 'list', type: 'string',dictCode: 'mdm_trigger_type',},
  productId: {title: '产品ID',order: 6,view: 'text', type: 'string',},
  productCode: {title: '产品编码',order: 7,view: 'popup', type: 'string',code: 'mdm_product_select', orgFields: 'product_code', destFields: 'productCode', popupMulti: false,},
  productName: {title: '产品名称',order: 8,view: 'text', type: 'string',},
  productColor: {title: '产品颜色',order: 9,view: 'text', type: 'string',},
  printProductName: {title: '打印产品名称',order: 10,view: 'text', type: 'string',},
  templateId: {title: '模板ID',order: 11,view: 'text', type: 'string',},
  templateCode: {title: '模板编码',order: 12,view: 'popup', type: 'string',code: 'mdm_label_template', orgFields: 'template_code', destFields: 'templateCode', popupMulti: false,},
  labelQty: {title: '标签枚数',order: 13,view: 'number', type: 'number',},
  copies: {title: '打印份数',order: 14,view: 'number', type: 'number',},
  labelWidth: {title: '标签宽度（mm）',order: 15,view: 'number', type: 'number',},
  labelHeight: {title: '标签高度（mm)',order: 16,view: 'number', type: 'number',},
  qrContent: {title: '二维码内容',order: 17,view: 'textarea', type: 'string',},
  qrImage: {title: '二维码图片Base64',order: 18,view: 'textarea', type: 'string',},
  status: {title: '状态：PENDING待打印/PRINTING打印中/COMPLETED已完成/FAILED失败',order: 19,view: 'list', type: 'string',dictCode: 'mdm_print_status',},
  companyId: {title: '公司',order: 20,view: 'list', type: 'string',dictTable: "sys_depart where del_flag='0' and org_category='1' and org_type='1'", dictCode: 'id', dictText: 'depart_name',},
  companyName: {title: '公司名称',order: 21,view: 'text', type: 'string',},
  printTime: {title: '实际打印时间',order: 22,view: 'datetime', type: 'string',},
  failReason: {title: '失败原因',order: 23,view: 'text', type: 'string',},
  printerType: {title: '打印机类型',order: 24,view: 'text', type: 'string',},
  printerName: {title: '打印机名称',order: 25,view: 'text', type: 'string',},
  remark: {title: '备注',order: 26,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

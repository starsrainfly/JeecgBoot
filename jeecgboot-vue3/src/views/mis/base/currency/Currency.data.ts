import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '币种代码',
    align:"center",
    dataIndex: 'currencyCode'
   },
   {
    title: '币种名称',
    align:"center",
    dataIndex: 'currencyName'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '创建人',
    align:"center",
    dataIndex: 'createBy'
   },
   {
    title: '创建日期',
    align:"center",
    dataIndex: 'createTime'
   },
   {
    title: '更新人',
    align:"center",
    dataIndex: 'updateBy'
   },
   {
    title: '更新日期',
    align:"center",
    dataIndex: 'updateTime'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "币种代码",
      field: "currencyCode",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "币种名称",
      field: "currencyName",
      component: 'Input',
      //colProps: {span: 6},
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
    label: '币种代码',
    field: 'currencyCode',
    component: 'Input',
  },
  {
    label: '币种名称',
    field: 'currencyName',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'status',
    defaultValue: 1,
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"status"
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

//子表列表数据
export const exchangeRateColumns: BasicColumn[] = [
   {
    title: '汇率',
    align:"center",
    dataIndex: 'exchangeRate'
   },
   {
    title: '生效日期',
    align:"center",
    dataIndex: 'effectiveDate'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '创建人',
    align:"center",
    dataIndex: 'createBy'
   },
   {
    title: '创建日期',
    align:"center",
    dataIndex: 'createTime'
   },
   {
    title: '更新人',
    align:"center",
    dataIndex: 'updateBy'
   },
   {
    title: '更新日期',
    align:"center",
    dataIndex: 'updateTime'
   },
];
//子表表单数据
export const exchangeRateFormSchema: FormSchema[] = [
  // TODO 子表隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false
  },
  {
    label: '汇率',
    field: 'exchangeRate',
    component: 'InputNumber',
  },
  {
    label: '生效日期',
    field: 'effectiveDate',
    component: 'DatePicker',
    componentProps: {
       showTime:true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"status"
     },
  },
];

// 高级查询数据
export const superQuerySchema = {
  currencyCode: {title: '币种代码',order: 0,view: 'text', type: 'string',},
  currencyName: {title: '币种名称',order: 1,view: 'text', type: 'string',},
  status: {title: '状态',order: 2,view: 'number', type: 'number',dictCode: 'status',},
  createBy: {title: '创建人',order: 3,view: 'text', type: 'string',},
  createTime: {title: '创建日期',order: 4,view: 'datetime', type: 'string',},
  updateBy: {title: '更新人',order: 5,view: 'text', type: 'string',},
  updateTime: {title: '更新日期',order: 6,view: 'datetime', type: 'string',},
};

import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
import { Tag } from 'ant-design-vue';
import { h } from 'vue';
import { Icon } from '/@/components/Icon';
//列表数据
export const columns: BasicColumn[] = [
   // {
   //  title: '订单id',
   //  align:"center",
   //  sorter: true,
   //  dataIndex: 'orderId',
   // },
   {
    title: '生产单号',
    align:"center",
    sorter: true,
    dataIndex: 'orderNo'
   },
  {
    title:'生产批号',
    align:"center",
    sorter:true,
    dataIndex:'batchNo',
    width:160
  },
   {
    title: '物料编码',
    align:"center",
    sorter: true,
    dataIndex: 'materialCode'
   },
   {
    title: '物料名称',
    align:"center",
    dataIndex: 'materialName'
   },
   {
    title: '规格型号',
    align:"center",
    dataIndex: 'materialSpec'
   },
   {
    title: '单位',
    align:"center",
    dataIndex: 'unit',
     width:'80'
   },
   {
    title: '需求数量',
    align:"center",
    sorter: true,
    dataIndex: 'requiredQty',
     width:100,
   },
   {
    title: '已发数量',
    align:"center",
    dataIndex: 'issuedQty',
     width:100,
   },
   {
    title: '剩余待发',
    align:"center",
    dataIndex: 'remainingQty',
     width:100,
   },
  {
    title: '可用库存',
    align:"center",
    dataIndex:'availableStockQty',
    width:260,
    customRender: ({ record, text }) => {
      const available = parseFloat(text) || 0;
      const locked = parseFloat(record.lockedQty) || 0;
      const realAvailable = Math.max(0, available - locked);

      const required = parseFloat(record.requiredQty) || 0;
      const issued = parseFloat(record.issuedQty) || 0;
      const overApply = parseFloat(record.overApplyQty) || 0;
      const realRemaining = Math.max(0, required - issued - locked);

      const isZero = realAvailable <= 0;
      const isShortage = realAvailable < realRemaining;

      let color = isZero ? '#ff4d4f' : isShortage ? '#faad14' : '#52c41a';
      let icon = isZero ? 'ant-design:close-circle-outlined' :
        isShortage ? 'ant-design:warning-outlined' : 'ant-design:check-circle-outlined';
      let label = isZero ? (locked > 0 ? '已锁定' : '缺货') :
        isShortage ? `缺${(realRemaining - realAvailable).toFixed(2)}` : '充足';

      return h('div', {
        style: {
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '4px',
          color: color,
          fontWeight: isShortage || isZero ? 'bold' : 'normal',
          backgroundColor: isShortage || isZero ? '#fff2f0' : 'transparent',
          padding: '4px 8px',
          borderRadius: '4px',
          border: isShortage || isZero ? `1px solid ${color}` : 'none'
        }
      }, [
        h(Icon, { icon: icon, style: { color: color } }),
        h('span', { style: { fontWeight: 'bold' } }, realAvailable.toFixed(2)),
        locked > 0 ? h('span', {
          style: { fontSize: '11px', color: '#999', textDecoration: 'line-through', marginLeft: '4px' }
        }, `(${available.toFixed(2)})`) : null,
        locked > 0 ? h('span', {
          style: {
            fontSize: '11px',
            color: '#1890ff',
            marginLeft: '4px',
            border: '1px solid #1890ff',
            padding: '0 4px',
            borderRadius: '2px',
            backgroundColor: '#e6f7ff'
          }
        }, `锁定${locked.toFixed(2)}`) : null,
        overApply > 0 ? h('span', {
          style: {
            fontSize: '11px',
            color: '#fa8c16',
            marginLeft: '4px',
            border: '1px solid #fa8c16',
            padding: '0 4px',
            borderRadius: '2px',
            backgroundColor: '#fff7e6'
          }
        }, `超${overApply.toFixed(2)}`) : null,
        h('span', { style: { fontSize: '11px', marginLeft: '4px', opacity: 0.8 } }, `(${label})`)
      ]);
    }
  },
   {
    title: '需求日期',
    align:"center",
    sorter: true,
    dataIndex: 'requiredDate',
     width:100,
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   // {
   //  title: '优先级',
   //  align:"center",
   //  dataIndex: 'priority'
   // },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText',
     customRender: ({ text, record }) => {
       const statusMap = {
         '0': { color: 'default', text: '待发货' },
         '1': { color: 'processing', text: '部分发料' },
         '2': { color: 'success', text: '已完成' },
         '3': { color: 'error', text: '已取消' }
       };
       const status = statusMap[text] || statusMap['0'];

       const locked = parseFloat(record.lockedQty) || 0;
       const over = parseFloat(record.overApplyQty) || 0;
       const issued = parseFloat(record.issuedQty) || 0;
       const required = parseFloat(record.requiredQty) || 0;

       const progress = required > 0 ? ((issued + locked) / required * 100).toFixed(1) : '0';

       return h('div', {
         style: { display: 'flex', flexDirection: 'column', alignItems: 'center' }
       }, [
         h('div', {}, [
           h(Tag, { color: status.color }, () => status.text),
           over > 0 ? h(Tag, { color: 'orange', style: { marginLeft: '4px' } }, () => `超${over.toFixed(0)}`) : null
         ]),
         h('div', {
           style: { fontSize: '11px', color: '#666', marginTop: '2px' }
         }, `${progress}% (${issued.toFixed(0)}+${locked.toFixed(0)}/${required.toFixed(0)})`)
       ]);
     }
   },
   {
    title: '目标仓库',
    align:"center",
    sorter: true,
    dataIndex: 'warehouseId_dictText',
     width:100,
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "物料编码",
      field: 'materialCode',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "物料名称",
      field: 'materialName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "规格型号",
      field: 'materialSpec',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "需求日期",
      field: 'requiredDate',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD'
      },
      //colProps: {span: 6},
 	},
	{
      label: "状态",
      field: 'status',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"mes_production_material_status"
      },
      //colProps: {span: 6},
 	},
	{
      label: "目标仓库",
      field: 'warehouseId',
      component: 'Input',
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '订单id',
    field: 'orderId',
    component: 'Input',
    show:false
  },
  {
    label: '生产单号',
    field: 'orderNo',
    component: 'Input',
  },
  {
    label: '物料id',
    field: 'materialId',
    component: 'Input',
    show:false
  },
  {
    label: '物料编码',
    field: 'materialCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入物料编码!'},
          ];
     },
    dynamicDisabled:true
  },
  {
    label: '物料名称',
    field: 'materialName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入物料名称!'},
          ];
     },
    dynamicDisabled:true
  },
  {
    label: '规格型号',
    field: 'materialSpec',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入规格型号!'},
          ];
     },
    dynamicDisabled:true
  },
  {
    label: '单位',
    field: 'unit',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入单位!'},
          ];
     },
  },
  {
    label: '需求数量',
    field: 'requiredQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入需求数量!'},
          ];
     },
  },
  {
    label: '已发数量',
    field: 'issuedQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入已发数量!'},
          ];
     },
  },
  {
    label: '剩余待发',
    field: 'remainingQty',
    component: 'InputNumber',
  },
  {
    label: '需求日期',
    field: 'requiredDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  // {
  //   label: '优先级',
  //   field: 'priority',
  //   component: 'InputNumber',
  // },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mes_production_material_status"
     },
  },
  {
    label: '目标仓库',
    field: 'warehouseId',
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
  orderId: {title: '订单id',order: 0,view: 'text', type: 'string',},
  orderNo: {title: '生产单号',order: 1,view: 'text', type: 'string',},
  materialCode: {title: '物料编码',order: 3,view: 'text', type: 'string',},
  materialName: {title: '物料名称',order: 4,view: 'text', type: 'string',},
  materialSpec: {title: '规格型号',order: 5,view: 'text', type: 'string',},
  unit: {title: '单位',order: 6,view: 'text', type: 'string',},
  requiredQty: {title: '需求数量',order: 7,view: 'number', type: 'number',},
  issuedQty: {title: '已发数量',order: 8,view: 'number', type: 'number',},
  remainingQty: {title: '剩余待发',order: 9,view: 'number', type: 'number',},
  requiredDate: {title: '需求日期',order: 10,view: 'date', type: 'string',},
  priority: {title: '优先级',order: 11,view: 'number', type: 'number',},
  status: {title: '状态',order: 12,view: 'list', type: 'string',dictCode: 'mes_production_material_status',},
  warehouseId: {title: '目标仓库',order: 13,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}

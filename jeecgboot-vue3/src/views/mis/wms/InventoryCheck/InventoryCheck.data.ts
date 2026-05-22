import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
import { JVxeTypes, JVxeColumn } from '/@/components/jeecg/JVxeTable/types';

//列表数据
export const columns: BasicColumn[] = [
   {
    title: '盘库单号',
    align:"center",
    sorter: true,
    dataIndex: 'checkNo'
   },
   {
    title: '盘库范围',
    align:"center",
    sorter: true,
    dataIndex: 'checkScope_dictText'
   },
   {
    title: '盘库方法',
    align:"center",
    sorter: true,
    dataIndex: 'checkMethod_dictText'
   },
   {
    title: '盘点仓库',
    align:"center",
    dataIndex: 'warehouseId_dictText'
   },
   {
    title: '盘点区域',
    align:"center",
    dataIndex: 'areaId_dictText'
   },
   {
    title: '货架',
    align:"center",
    dataIndex: 'shelfId_dictText'
   },

   {
    title: '盘库状态',
    align:"center",
    sorter: true,
    dataIndex: 'checkStatus_dictText'
   },
   {
    title: '审核状态',
    align:"center",
    sorter: true,
    dataIndex: 'approveStatus_dictText'
   },
   // {
   //  title: '盘点人',
   //  align:"center",
   //  dataIndex: 'checkUserId'
   // },
   {
    title: '盘点人姓名',
    align:"center",
    dataIndex: 'checkUserName'
   },
   {
    title: '盘点开始时间',
    align:"center",
    sorter: true,
    dataIndex: 'checkStartTime'
   },
   {
    title: '盘点完成时间',
    align:"center",
    sorter: true,
    dataIndex: 'checkFinishedTime'
   },
   {
    title: '总项数',
    align:"center",
    dataIndex: 'totalItems'
   },
   {
    title: '已盘项数',
    align:"center",
    dataIndex: 'checkedItems'
   },
   {
    title: '差异项数',
    align:"center",
    sorter: true,
    dataIndex: 'diffItems'
   },
   {
    title: '差异金额',
    align:"center",
    sorter: true,
    dataIndex: 'diffAmount'
   },

   {
    title: '审核人',
    align:"center",
    dataIndex: 'approveUser'
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
    title: '物料类型',
    align:"center",
    dataIndex: 'goodsType_dictText'
  },

  {
    title: '产品编码',
    align:"center",
    dataIndex: 'goodsCode'
  },
  {
    title: '名称',
    align:"center",
    dataIndex: 'goodsName'
  },
  {
    title: '规格',
    align:"center",
    dataIndex: 'goodsSpec'
  },
  {
    title: '颜色',
    align:"center",
    dataIndex: 'goodsColor'
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
      label: "盘库单号",
      field: "checkNo",
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "盘库范围",
      field: "checkScope",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"wms_check_scope"
      },
      //colProps: {span: 6},
 	},
	{
      label: "盘库方法",
      field: "checkMethod",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"wms_check_method"
      },
      //colProps: {span: 6},
 	},
  {
    label: "仓库",
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel, formActionType }) => ({
      dictCode: "mis_warehouse where del_flag='0' and status='1',name,id",
      // 关键：仓库变更时清空所有下级
      onChange: (value: string) => {
        // 清空区域、货架、货位
        formActionType?.setFieldsValue({
          areaId: undefined,
          shelfId: undefined,
          locationId: undefined,
        });
      },
    }),
    // componentProps:{
    //     dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
    // },
    //colProps: {span: 6},
  },
  {
    label: "区域",
    field: 'areaId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel, formActionType }) => ({
      key: formModel?.warehouseId || 'empty',
      dictCode: formModel?.warehouseId
        ? `mis_warehouse_area,name,id,del_flag='0' and status='1' and warehouse_id='${formModel.warehouseId}'`
        : '',
      placeholder: formModel?.warehouseId ? "请选择区域" : "请先选择仓库",
      onChange: (value: string) => {
        formActionType?.setFieldsValue({
          shelfId: undefined,
          locationId: undefined,
        });
      },
    }),
    // componentProps:{
    //     dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id"
    // },
    //colProps: {span: 6},
  },
  {
    label: "货架",
    field: 'shelfId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel, formActionType }) => ({
      key: formModel?.areaId || 'empty',
      dictCode: formModel?.areaId
        ? `mis_warehouse_shelf,name,id,del_flag='0' and status='1' and area_id='${formModel.areaId}'`
        : '',
      placeholder: formModel?.areaId ? "请选择区域" : "请先选择仓库",
      onChange: (value: string) => {
        formActionType?.setFieldsValue({
          locationId: undefined,
        });
      },
    }),
    // componentProps:{
    //     dictCode:"mis_warehouse_shelf where del_flag ='0' and status='1',shelf_code,id"
    // },
    //colProps: {span: 6},
  },
  {
    label: "位置",
    field: 'locationId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.shelfId || 'empty',
      dictCode: formModel?.shelfId
        ? `mis_warehouse_location,path_code,id,del_flag='0' and status='1' and shelf_id='${formModel.shelfId}'`
        : '',
      placeholder: formModel?.shelfId ? "请选择目标货位" : "请先选择目标货架",
    }),
    // componentProps:{
    //     dictCode:"mis_warehouse_location where del_flag='0' and status='1',path_code,id"
    // },
    //colProps: {span: 6},
  },
	{
      label: "盘库状态",
      field: "checkStatus",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"wms_check_status"
      },
      //colProps: {span: 6},
 	},
	{
      label: "审核状态",
      field: "approveStatus",
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"approval_status",
      },
      //colProps: {span: 6},
 	},
     {
      label: "盘点开始时间",
      field: "checkStartTime",
      component: 'RangePicker',
      componentProps: {
          valueType: 'Date',
          showTime:true
      },
      //colProps: {span: 6},
	},
     {
      label: "盘点完成时间",
      field: "checkFinishedTime",
      component: 'RangePicker',
      componentProps: {
          valueType: 'Date',
          showTime:true
      },
      //colProps: {span: 6},
	},
];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '盘库单号',
    field: 'checkNo',
    component: 'Input',
    componentProps:{
      readonly:true,
    }
  },
  {
    label: '盘库范围',
    field: 'checkScope',
    component: 'JDictSelectTag',
    componentProps: ({ formActionType }) => {
      const { setFieldsValue } = formActionType;
      return {
        dictCode: 'wms_check_scope',
        // 监听值变化
        onChange: (value: string) => {
          console.log('checkScope onChange:', value);

          // 清空编码相关字段
          const clearFields: Record<string, any> = {
            goodsCode: undefined,
            goodsId: undefined,
            goodsName: undefined,
            goodsSpec: undefined,
            goodsColor: undefined,
            goodsType: value === '2' ? 'PRODUCT' : undefined,
          };

          setFieldsValue(clearFields);
        },
      };
    },

    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入盘库范围（1位，2产品，3全仓）!'},
          ];
     },
  },
  {
    label: '盘库方法',
    field: 'checkMethod',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_check_method"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入盘库方法（1明盘，2盲盘）!'},
          ];
     },
  },
  {
    label: "盘点仓库",
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel, formActionType }) => ({
      dictCode: "mis_warehouse where del_flag='0' and status='1',name,id",
      // 关键：仓库变更时清空所有下级
      onChange: (value: string) => {
        // 清空区域、货架、货位
        formActionType?.setFieldsValue({
          areaId: undefined,
          shelfId: undefined,
          locationId: undefined,
        });
      },
    }),
    // 按库位或全仓时必填
    dynamicRules: ({ model }) => {
      return (model?.checkScope === '1' || model?.checkScope === '4')
        ? [{ required: true, message: '请选择盘点仓库!' }]
        : [];
    },
  },
  {
    label: "盘点区域",
    field: 'areaId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel, formActionType }) => ({
      key: formModel?.warehouseId || 'empty',
      dictCode: formModel?.warehouseId
        ? `mis_warehouse_area,name,id,del_flag='0' and status='1' and warehouse_id='${formModel.warehouseId}'`
        : '',
      placeholder: formModel?.warehouseId ? "请选择区域" : "请先选择仓库",
      onChange: (value: string) => {
        formActionType?.setFieldsValue({
          shelfId: undefined,
          locationId: undefined,
        });
      },
    }),

  },
  {
    label: "盘点货架",
    field: 'shelfId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel, formActionType }) => ({
      key: formModel?.areaId || 'empty',
      dictCode: formModel?.areaId
        ? `mis_warehouse_shelf,name,id,del_flag='0' and status='1' and area_id='${formModel.areaId}'`
        : '',
      placeholder: formModel?.areaId ? "请选择区域" : "请先选择仓库",
      onChange: (value: string) => {
        formActionType?.setFieldsValue({
          locationId: undefined,
        });
      },
    }),

  },
  {
    label: "盘点位置",
    field: 'locationId',
    component: 'JDictSelectTag',
    componentProps: ({ formModel }) => ({
      key: formModel?.shelfId || 'empty',
      dictCode: formModel?.shelfId
        ? `mis_warehouse_location,path_code,id,del_flag='0' and status='1' and shelf_id='${formModel.shelfId}'`
        : '',
      placeholder: formModel?.shelfId ? "请选择目标货位" : "请先选择目标货架",
    }),
    // componentProps:{
    //     dictCode:"mis_warehouse_location where del_flag='0' and status='1',path_code,id"
    // },
    //colProps: {span: 6},
  },
  {
    label: '物料类型',
    field: 'goodsType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_item_type"
     },
    show: ({ model }) => model.checkScope === '2' || model.checkScope === '3',
  },
  {
    label: '产品id',
    field: 'goodsId',
    component: 'Input',
    show:false
  },

  {
    label: '编码',
    field: 'goodsCode',
    component: 'JPopup',
    componentProps: ({ formActionType,formModel }) => {
      // 根据盘库范围返回不同的 popup 配置
      const {setFieldsValue} = formActionType;
      if (formModel.checkScope === '3') {
        // 按物料

        // 先清了类型（如果前面有）
        //  setFieldsValue({ goodsType: '' });

        return {
          setFieldsValue:setFieldsValue,
          code: 'mdm_material_select',
          fieldConfig: [
            { source: 'id', target: 'goodsId' },
            { source: 'material_code', target: 'goodsCode' },
            { source: 'material_name', target: 'goodsName' },
            { source: 'material_spec', target: 'goodsSpec' },
            { source: 'material_type', target: 'goodsType' },
          ],
          multi: false,
        };
      } else if (formModel.checkScope === '2') {
        // 按产品
        const isProduct = formModel.checkScope === '2';

        // 如果是产品，提前设置 goodsType 默认值
        if (isProduct && !formModel.goodsType) {
          setFieldsValue({ goodsType: 'PRODUCT' });
        }
        return {
          setFieldsValue:setFieldsValue,
          code: 'mdm_product_select',
          fieldConfig: [
            { source: 'id', target: 'goodsId' },
            { source: 'product_code', target: 'goodsCode' },
            { source: 'product_name', target: 'goodsName' },
            { source: 'product_spec', target: 'goodsSpec' },
            { source: 'product_color', target: 'goodsColor' },

          ],
          multi: false,
        };
      }
      else{ //默认物料
        return {
          setFieldsValue:setFieldsValue,
          code: 'mdm_material_select',
          fieldConfig: [
            { source: 'id', target: 'goodsId' },
            { source: 'material_code', target: 'goodsCode' },
            { source: 'material_name', target: 'goodsName' },
            { source: 'material_spec', target: 'goodsSpec' },
            { source: 'material_type', target: 'goodsType' },
          ],
          multi: false,
        };
      }
      return { disabled: true }; // 全仓时禁用
    },
    show: ({ model }) => model.checkScope === '2' || model.checkScope === '3',
    dynamicRules: ({ model }) => {
      return (model.checkScope === '2' || model.checkScope === '3')
        ? [{ required: true, message: '请选择编码!' }]
        : [];
    },
  },
  // ===== 公共显示字段（名称、规格等） =====
  {
    label: '名称',
    field: 'goodsName',
    component: 'Input',
    dynamicDisabled: true, // 从JPopup自动回填，不可手动编辑
    show: ({ model }) => model.checkScope === '3' || model.checkScope === '2',
  },
  {
    label: '规格',
    field: 'goodsSpec',
    component: 'Input',
    dynamicDisabled: true,
    show: ({ model }) => model.checkScope === '3' || model.checkScope === '2',
  },
  {
    label: '颜色',
    field: 'goodsColor',
    component: 'Input',
    dynamicDisabled: true,
    // 产品才有颜色，物料没有
    show: ({ model }) => model.checkScope === '2',
  },
  // {
  //   label: '产品编码',
  //   field: 'goodsCode',
  //   component: 'Input',
  // },
  // {
  //   label: '名称',
  //   field: 'goodsName',
  //   component: 'Input',
  // },
  // {
  //   label: '规格',
  //   field: 'goodsSpec',
  //   component: 'Input',
  // },
  // {
  //   label: '颜色',
  //   field: 'goodsColor',
  //   component: 'Input',
  // },
  {
    label: '盘库状态',
    field: 'checkStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_check_status"
     },
    dynamicDisabled:true
  },
  {
    label: '审核状态',
    field: 'approveStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"approval_status"
     },
    dynamicDisabled:true
  },
  {
    label: '盘点人',
    field: 'checkUserId',
    component: 'JDictSelectTag',
    componentProps:{
      dictCode:"sys_user where del_flag='0' and status='1',realname,id"
    },
  },
  {
    label: '盘点人姓名',
    field: 'checkUserName',
    component: 'Input',
    show:false
  },
  // {
  //   label: '盘点开始时间',
  //   field: 'checkStartTime',
  //   component: 'DatePicker',
  //   componentProps: {
  //      showTime:true,
  //      valueFormat: 'YYYY-MM-DD HH:mm:ss'
  //    },
  // },
  // {
  //   label: '盘点完成时间',
  //   field: 'checkFinishedTime',
  //   component: 'DatePicker',
  //   componentProps: {
  //      showTime:true,
  //      valueFormat: 'YYYY-MM-DD HH:mm:ss'
  //    },
  // },
  // {
  //   label: '总项数',
  //   field: 'totalItems',
  //   component: 'InputNumber',
  // },
  // {
  //   label: '已盘项数',
  //   field: 'checkedItems',
  //   component: 'InputNumber',
  // },
  // {
  //   label: '差异项数',
  //   field: 'diffItems',
  //   component: 'InputNumber',
  // },
  // {
  //   label: '差异金额',
  //   field: 'diffAmount',
  //   component: 'InputNumber',
  // },
  // {
  //   label: '审核人id',
  //   field: 'approveId',
  //   component: 'Input',
  // },
  // {
  //   label: '审核人',
  //   field: 'approveUser',
  //   component: 'Input',
  // },
  // {
  //   label: '审核时间',
  //   field: 'approveTime',
  //   component: 'DatePicker',
  //   componentProps: {
  //      showTime:true,
  //      valueFormat: 'YYYY-MM-DD HH:mm:ss'
  //    },
  // },
  // {
  //   label: '审核备注',
  //   field: 'approveRemark',
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

//子表列表数据
export const inventoryCheckDetailColumns: BasicColumn[] = [
   // {
   //  title: '盘点单id',
   //  align:"center",
   //  dataIndex: 'checkId'
   // },
   // {
   //  title: '库存id',
   //  align:"center",
   //  dataIndex: 'stockId'
   // },
   // {
   //  title: '物料id',
   //  align:"center",
   //  dataIndex: 'goodsId'
   // },
   {
    title: '编码',
    align:"center",
    dataIndex: 'goodsCode'
   },
   {
    title: '名称',
    align:"center",
    dataIndex: 'goodsName'
   },
   {
    title: '规格',
    align:"center",
    dataIndex: 'goodsSpec'
   },
   {
    title: '颜色',
    align:"center",
    dataIndex: 'goodsColor'
   },
   {
    title: '类型',
    align:"center",
    dataIndex: 'goodsType_dictText'
   },
   {
    title: '批次号',
    align:"center",
    dataIndex: 'batchNo'
   },
   {
    title: '生产日期',
    align:"center",
    dataIndex: 'productionDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '有效期至',
    align:"center",
    dataIndex: 'expiryDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '仓库',
    align:"center",
    dataIndex: 'warehouseId_dictText'
   },
   {
    title: '区域',
    align:"center",
    dataIndex: 'areaId_dictText'
   },
   {
    title: '货架',
    align:"center",
    dataIndex: 'shelfId_dictText'
   },
   {
    title: '货位',
    align:"center",
    dataIndex: 'locationId_dictText'
   },

   {
    title: '系统库存数量',
    align:"center",
    dataIndex: 'systemQty'
   },
   {
    title: '实盘数量',
    align:"center",
    dataIndex: 'actualQty'
   },
   {
    title: '差异数量',
    align:"center",
    dataIndex: 'diffQty'
   },
   {
    title: '单位',
    align:"center",
    dataIndex: 'unit_dictText'
   },
   {
    title: '成本单价',
    align:"center",
    dataIndex: 'costPrice'
   },
   {
    title: '差异金额',
    align:"center",
    dataIndex: 'diffAmount'
   },
   {
    title: '差异原因',
    align:"center",
    dataIndex: 'diffReason'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'checkStatus_dictText'
   },
];
//子表表单数据
export const inventoryCheckDetailFormSchema: FormSchema[] = [
  // TODO 子表隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false
  },
  {
    label: '盘点单id',
    field: 'checkId',
    component: 'Input',
    show:false
  },
  {
    label: '库存id',
    field: 'stockId',
    component: 'Input',
  },
  {
    label: '物料id',
    field: 'goodsId',
    component: 'Input',
  },
  {
    label: '编码',
    field: 'goodsCode',
    component: 'Input',
  },
  {
    label: '名称',
    field: 'goodsName',
    component: 'Input',
  },
  {
    label: '规格',
    field: 'goodsSpec',
    component: 'Input',
  },
  {
    label: '颜色',
    field: 'goodsColor',
    component: 'Input',
  },
  {
    label: '类型',
    field: 'goodsType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_item_type"
     },
  },
  {
    label: '批次号',
    field: 'batchNo',
    component: 'Input',
  },
  {
    label: '生产日期',
    field: 'productionDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  {
    label: '有效期至',
    field: 'expiryDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse where del_flag='0' and status='1',name,id"
     },
  },
  {
    label: '区域',
    field: 'areaId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_area where del_flag='0' and status='1',area_code,id"
     },
  },
  {
    label: '货架',
    field: 'shelfId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_shelf where del_flag='0' and status='1',shelf_code,id"
     },
  },
  {
    label: '货位',
    field: 'locationId',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_warehouse_location where del_flag='0' and status='1',path_code,id"
     },
  },

  {
    label: '系统库存数量',
    field: 'systemQty',
    component: 'InputNumber',
  },
  {
    label: '实盘数量',
    field: 'actualQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入实盘数量!'},
          ];
     },
  },
  {
    label: '差异数量',
    field: 'diffQty',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入差异数量!'},
          ];
     },
  },
  {
    label: '单位',
    field: 'unit',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"mis_unit where del_flag='0' and status='1',unit,unit"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入单位!'},
          ];
     },
  },
  {
    label: '成本单价',
    field: 'costPrice',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入成本单价!'},
          ];
     },
  },
  {
    label: '差异金额',
    field: 'diffAmount',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入差异金额!'},
          ];
     },
  },
  {
    label: '差异原因',
    field: 'diffReason',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'checkStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"wms_check_status"
     },
  },
];


// ===== 预览模式列（只读，精简） =====
export const previewColumns: BasicColumn[] = [
  {
    title: '编码',
    align: "center",
    dataIndex: 'goodsCode',
    width: 120,
    fixed: 'left'
  },
  {
    title: '名称',
    align: "center",
    dataIndex: 'goodsName',
    width: 150,
    fixed: 'left'
  },
  {
    title: '规格',
    align: "center",
    dataIndex: 'goodsSpec',
    width: 100
  },
  {
    title: '类型',
    align: "center",
    dataIndex: 'goodsType_dictText',
    width: 80,
    // customRender: ({ text }) => {
    //   const map: Record<string, string> = {
    //     'RAW': '原料',
    //     'PRODUCT': '产品',
    //     'INNER_PACK': '内包',
    //     'OUTER_PACK': '外包'
    //   };
    //   return map[text] || text;
    // }
  },
  {
    title: '批次号',
    align: "center",
    dataIndex: 'batchNo',
    width: 140
  },
  {
    title: '系统库存',
    align: "center",
    dataIndex: 'systemQty',
    width: 100
  },
  {
    title: '单位',
    align: "center",
    dataIndex: 'unit',
    width: 60
  }
];

// ===== 盘点作业页表格列 =====
export const checkJobColumns: JVxeColumn[] = [
  // ===== 只读列 =====
  {
    title: '编码',
    key: 'goodsCode',
    type: JVxeTypes.input,       // ← 只读
    width: "100px",
    disabled:true
  },
  {
    title: '名称',
    key: 'goodsName',
    type: JVxeTypes.input,       // ← 只读
    width: "100px",
    disabled:true
  },
  {
    title: '规格',
    key: 'goodsSpec',
    type: JVxeTypes.input,       // ← 只读
    width: "100px",
    disabled:true
  },
  {
    title: '颜色',
    key: 'goodsColor',
    type: JVxeTypes.input,       // ← 只读
    width: "80px",
    disabled:true
  },
  {
    title: '类型',
    key: 'goodsType_dictText',
    type: JVxeTypes.input,       // ← 只读
    width: "80px",
    disabled:true
  },
  {
    title: '批次号',
    key: 'batchNo',
    type: JVxeTypes.input,       // ← 只读
    width: "170px",
    disabled:true
  },
  {
    title: '系统库存',
    key: 'systemQty',
    type: JVxeTypes.slot,       // ← slot 自定义（盲盘显示***）
    width: "100px",
    slotName: 'systemQty'
  },

  // ===== 可编辑列 =====
  {
    title: '实盘数量',
    key: 'actualQty',
    type: JVxeTypes.inputNumber, // ← 可编辑数字
    width: "120px",
    placeholder: '请输入',
   // defaultValue: '',
    validateRules: [
      { required: true, message: '实盘数量必填' }
    ],
  },

  // 差异数量 - 只读，自动计算
  {
    title: '差异数量',
    key: 'diffQty',
    type: JVxeTypes.slot,        // ← slot 自定义（带颜色）
    width: "100px",
    slotName: 'diffQty'
  },

  {
    title: '单位',
    key: 'unit_dictText',
    type: JVxeTypes.input,        // ← 只读
    width: "70px",

  },
  {
    title: '成本单价',
    key: 'costPrice',
    type: JVxeTypes.inputNumber, // ← 可编辑
    width: "100px",
  },

  // 差异金额 - 只读，自动计算
  {
    title: '差异金额',
    key: 'diffAmount',
    type: JVxeTypes.slot, // 或 text
    width: "100px",
    //disabled:true
    slotName:'diffAmount',
  },

  {
    title: '差异原因',
    key: 'diffReason',
    type: JVxeTypes.input,        // ← 可编辑文本
    width: "150px",
    placeholder: '有差异时必填',
  },

  // ===== 状态列 =====
  {
    title: '状态',
    key: 'checkStatus_dictText',
    type: JVxeTypes.input,         // ← 只读
    width: "80px",
    disabled:true
  },

  // ===== 库位列 =====
  {
    title: '仓库',
    key: 'warehouseId_dictText',
    type: JVxeTypes.input,
    width: "80px",
    disabled:true
  },
  {
    title: '区域',
    key: 'areaId_dictText',
    type: JVxeTypes.input,
    width: "80px",
    disabled:true
  },
  {
    title: '货架',
    key: 'shelfId_dictText',
    type: JVxeTypes.input,
    width: "80px",
    disabled:true
  },
  {
    title: '货位',
    key: 'locationId_dictText',
    type: JVxeTypes.input,
    width: "140px",
    disabled:true
  },

  // ===== 操作列 =====
  {
    title: '操作',
    key: 'action',
    type: JVxeTypes.slot,         // ← slot 自定义按钮
    width: "120px",
    fixed: 'right',
    slotName: 'action'
  },
];



// ===== 审核页差异明细表格列（只显示关键字段） =====
export const approveDiffColumns: BasicColumn[] = [
  {
    title: '编码',
    align: "center",
    dataIndex: 'goodsCode',
    width: 120
  },
  {
    title: '名称',
    align: "center",
    dataIndex: 'goodsName',
    width: 150
  },
  {
    title: '规格',
    align: "center",
    dataIndex: 'goodsSpec',
    width: 120
  },
  {
    title: '系统库存',
    align: "center",
    dataIndex: 'systemQty',
    width: 100
  },
  {
    title: '实盘数量',
    align: "center",
    dataIndex: 'actualQty',
    width: 100
  },
  {
    title: '差异数量',
    align: "center",
    dataIndex: 'diffQty',
    width: 100
  },
  {
    title: '差异金额',
    align: "center",
    dataIndex: 'diffAmount',
    width: 100
  },
  {
    title: '差异原因',
    align: "center",
    dataIndex: 'diffReason',
    width: 200
  },
];

// 高级查询数据
export const superQuerySchema = {
  checkNo: {title: '盘库单号（PD)',order: 0,view: 'text', type: 'string',},
  checkScope: {title: '盘库范围（1位，2产品，3全仓）',order: 1,view: 'list', type: 'string',dictCode: 'wms_check_scope',},
  checkMethod: {title: '盘库方法（1明盘，2盲盘）',order: 2,view: 'list', type: 'string',dictCode: 'wms_check_method',},
  warehouseId: {title: '盘点仓库',order: 3,view: 'list', type: 'string',dictTable: "mis_warehouse where del_flag='0' and status='1'", dictCode: 'id', dictText: 'name',},
  areaId: {title: '盘点区域',order: 4,view: 'list', type: 'string',dictTable: "mis_warehouse_area where del_flag='0' and status='1'", dictCode: 'id', dictText: 'area_code',},
  shelfId: {title: '货架',order: 5,view: 'list', type: 'string',dictTable: "mis_warehouse_shelf where del_flag='0' and status='1'", dictCode: 'id', dictText: 'shelf_code',},
  goodsType: {title: '物料类型',order: 6,view: 'list', type: 'string',dictCode: 'wms_item_type',},
  goodsId: {title: '产品id',order: 7,view: 'text', type: 'string',},
  goodsCode: {title: '产品编码',order: 8,view: 'text', type: 'string',},
  goodsName: {title: '名称',order: 9,view: 'text', type: 'string',},
  goodsSpec: {title: '规格',order: 10,view: 'text', type: 'string',},
  goodsColor: {title: '颜色',order: 11,view: 'text', type: 'string',},
  checkStatus: {title: '盘库状态',order: 12,view: 'list', type: 'string',dictCode: 'wms_check_status',},
  approveStatus: {title: '审核状态',order: 13,view: 'list', type: 'string',dictCode: 'approval_status',},
  checkUserId: {title: '盘点人',order: 14,view: 'text', type: 'string',},
  checkUserName: {title: '盘点人姓名',order: 15,view: 'text', type: 'string',},
  checkStartTime: {title: '盘点开始时间',order: 16,view: 'datetime', type: 'string',},
  checkFinishedTime: {title: '盘点完成时间',order: 17,view: 'datetime', type: 'string',},
  totalItems: {title: '总项数',order: 18,view: 'number', type: 'number',},
  checkedItems: {title: '已盘项数',order: 19,view: 'number', type: 'number',},
  diffItems: {title: '差异项数',order: 20,view: 'number', type: 'number',},
  diffAmount: {title: '差异金额',order: 21,view: 'number', type: 'number',},
  approveId: {title: '审核人id',order: 22,view: 'text', type: 'string',},
  approveUser: {title: '审核人',order: 23,view: 'text', type: 'string',},
  approveTime: {title: '审核时间',order: 24,view: 'datetime', type: 'string',},
  approveRemark: {title: '审核备注',order: 25,view: 'text', type: 'string',},
  remark: {title: '备注',order: 26,view: 'text', type: 'string',},
};

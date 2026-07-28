import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  // 原有工单接口
  list = '/mes/productionTask/list',
  save='/mes/productionTask/add',
  edit='/mes/productionTask/edit',
  deleteOne = '/mes/productionTask/delete',
  deleteBatch = '/mes/productionTask/deleteBatch',
  importExcel = '/mes/productionTask/importExcel',
  exportXls = '/mes/productionTask/exportXls',

  // 新增：我的工单相关接口
  myTaskList = '/mes/productionTask/myTaskList',
  startTask = '/mes/productionTask/start',
  completeTask = '/mes/productionTask/complete',
  reportQc = '/mes/productionTask/reportQc',

  // 新增：配料相关接口（从 productionBatch 复制）
  productionBatchBomList = '/mes/productionBatch/queryProductionBatchBomByMainId',
  queryMaterialActual = '/mes/productionBatchMaterialActual/list',
  addMaterialActual = '/mes/productionBatchMaterialActual/add',
  deleteMaterialActual = '/mes/productionBatchMaterialActual/delete',
  startWeighing ='/mes/productionBatch/startWeighing',
  completeWeighing ='/mes/productionBatch/completeWeighing',
  setBatchStatus ='/mes/productionBatch/setBatchStatus',

  previewQcItems = '/mes/qcRecord/previewItems',
  completeQc = '/mes/qcRecord/complete',
}

/**
 * 导出api
 */
export const getExportUrl = Api.exportXls;

/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;

/**
 * 列表接口
 */
export const list = (params) =>
  defHttp.get({url: Api.list, params});

/**
 * 删除单个
 */
export const deleteOne = (params,handleSuccess) => {
  return defHttp.delete({url: Api.deleteOne, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 */
export const batchDelete = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({url: Api.deleteBatch, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({url: url, params});
}

// ==================== 我的工单专用接口 ====================

/**
 * 我的工单列表（当前操作员）
 */
export const myTaskList = (params) =>
  defHttp.get({url: Api.myTaskList, params});

/**
 * 开始任务
 */
export const startTask = (params) =>
  defHttp.post({url: Api.startTask, params});

/**
 * 完成任务
 */
export const completeTask = (params) =>
  defHttp.post({url: Api.completeTask, params});

/**
 * 报检（生成质检工单）
 */
export const reportQc = (params) =>
  defHttp.post({url: Api.reportQc, params});

// ==================== 配料执行专用接口 ====================
/**
 * 设置批次开始配料
 * @param params -> batchId
 */
export const startWeighing =(params) =>
  defHttp.post({url:Api.startWeighing, params});
/**
 * 设置 完成配料
 * @param params -> batchId
 */
export const completeWeighing = (params) =>
  defHttp.post({url:Api.completeWeighing,params})


export const setBatchStatus = (params) =>
  defHttp.post({url:Api.setBatchStatus,params})

/**
 * 查询批次BOM清单
 */
export const productionBatchBomList = (params) =>
  defHttp.get({url: Api.productionBatchBomList, params});

/**
 * 查询物料称重记录
 */
export const queryMaterialActual = (params) =>
  defHttp.get({url: Api.queryMaterialActual, params});

/**
 * 添加称重记录
 */
export const addMaterialActual = (params) =>
  defHttp.post({url: Api.addMaterialActual, params});

/**
 * 删除称重记录
 */
export const deleteMaterialActual = (params, handleSuccess) => {
  return defHttp.delete({url: Api.deleteMaterialActual, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
};

/**
 * 预生成检验项目（根据配方技术指标）
 */
export const previewQcItems = (params) =>
  defHttp.get({url: Api.previewQcItems, params});

/**
 * 提交质检结果
 */
export const completeQc = (params) =>
  defHttp.post({url: Api.completeQc, params});

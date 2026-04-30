import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/wms/stockIn/list',
  save='/wms/stockIn/add',
  edit='/wms/stockIn/edit',
  deleteOne = '/wms/stockIn/delete',
  deleteBatch = '/wms/stockIn/deleteBatch',
  importExcel = '/wms/stockIn/importExcel',
  exportXls = '/wms/stockIn/exportStockInXls',//'/wms/stockIn/exportXls',
  stockInDetailList = '/wms/stockIn/queryStockInDetailByMainId',
  stockInDetailSave='/wms/stockIn/addStockInDetail',
  stockInDetailEdit='/wms/stockIn/editStockInDetail',
  stockInDetailDelete = '/wms/stockIn/deleteStockInDetail',
  stockInDetailDeleteBatch = '/wms/stockIn/deleteBatchStockInDetail',

  listDetailAll = '/wms/stockIn/listDetailAll',
  exportDetailAll = '/wms/stockIn/exportDetailAll',
}
/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;

/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;
/**
 * 列表接口
 * @param params
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
 * @param params
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
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({url: url, params});
}
/**
 * 列表接口
 * @param params
 */
export const stockInDetailList = (params) => {
  if(params['id']){
    return defHttp.get({url: Api.stockInDetailList, params});
  }
  return Promise.resolve({});
}


/**
 * 删除单个
 */
export const stockInDetailDelete = (params,handleSuccess) => {
  return defHttp.delete({url: Api.stockInDetailDelete, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}
/**
 * 批量删除
 * @param params
 */
export const stockInDetailDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({url: Api.stockInDetailDeleteBatch, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}
/**
 * 保存或者更新
 * @param params
 */
export const  stockInDetailSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.stockInDetailEdit : Api.stockInDetailSave;
  return defHttp.post({url: url, params});
}
/**
 * 导入
 */
export const stockInDetailImportUrl = '/wms/stockIn/importStockInDetail'

/**
 * 导出
 */
export const stockInDetailExportXlsUrl = '/wms/stockIn/exportStockInDetail'

// 纯明细查询列表
export const listDetailAll = (params) =>
  defHttp.get({url: Api.listDetailAll, params});

// 纯明细导出URL
export const getDetailExportUrl = Api.exportDetailAll;

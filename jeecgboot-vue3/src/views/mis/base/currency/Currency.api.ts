import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/currency/currency/list',
  save='/currency/currency/add',
  edit='/currency/currency/edit',
  deleteOne = '/currency/currency/delete',
  deleteBatch = '/currency/currency/deleteBatch',
  importExcel = '/currency/currency/importExcel',
  exportXls = '/currency/currency/exportXls',
  exchangeRateList = '/currency/currency/listExchangeRateByMainId',
  exchangeRateSave='/currency/currency/addExchangeRate',
  exchangeRateEdit='/currency/currency/editExchangeRate',
  exchangeRateDelete = '/currency/currency/deleteExchangeRate',
  exchangeRateDeleteBatch = '/currency/currency/deleteBatchExchangeRate',
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
export const exchangeRateList = (params) => {
  if(params['currencyId']){
    return defHttp.get({url: Api.exchangeRateList, params});
  }
  return Promise.resolve({});
}


/**
 * 删除单个
 */
export const exchangeRateDelete = (params,handleSuccess) => {
  return defHttp.delete({url: Api.exchangeRateDelete, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}
/**
 * 批量删除
 * @param params
 */
export const exchangeRateDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({url: Api.exchangeRateDeleteBatch, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}
/**
 * 保存或者更新
 * @param params
 */
export const  exchangeRateSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.exchangeRateEdit : Api.exchangeRateSave;
  return defHttp.post({url: url, params});
}
/**
 * 导入
 */
export const exchangeRateImportUrl = '/currency/currency/importExchangeRate'

/**
 * 导出
 */
export const exchangeRateExportXlsUrl = '/currency/currency/exportExchangeRate'

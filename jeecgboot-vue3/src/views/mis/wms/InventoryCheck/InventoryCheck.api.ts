import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/wms/inventoryCheck/list',
  save='/wms/inventoryCheck/add',
  edit='/wms/inventoryCheck/edit',
  deleteOne = '/wms/inventoryCheck/delete',
  deleteBatch = '/wms/inventoryCheck/deleteBatch',
  importExcel = '/wms/inventoryCheck/importExcel',
  exportXls = '/wms/inventoryCheck/exportXls',
  inventoryCheckDetailList = '/wms/inventoryCheck/listInventoryCheckDetailByMainId',
  inventoryCheckDetailSave='/wms/inventoryCheck/addInventoryCheckDetail',
  inventoryCheckDetailEdit='/wms/inventoryCheck/editInventoryCheckDetail',
  inventoryCheckDetailDelete = '/wms/inventoryCheck/deleteInventoryCheckDetail',
  inventoryCheckDetailDeleteBatch = '/wms/inventoryCheck/deleteBatchInventoryCheckDetail',

  startCheck = '/wms/inventoryCheck/startCheck',
  confirmDetail = '/wms/inventoryCheck/confirmDetail',
  batchConfirm = '/wms/inventoryCheck/batchConfirm',
  approveCheck = '/wms/inventoryCheck/approveCheck',
  previewStock = '/wms/inventoryCheck/previewStock',
  finishCheck = '/wms/inventoryCheck/finishCheck',
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
export const inventoryCheckDetailList = (params) => {
  if(params['checkId']){
    return defHttp.get({url: Api.inventoryCheckDetailList, params});
  }
  return Promise.resolve({});
}


/**
 * 删除单个
 */
export const inventoryCheckDetailDelete = (params,handleSuccess) => {
  return defHttp.delete({url: Api.inventoryCheckDetailDelete, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}
/**
 * 批量删除
 * @param params
 */
export const inventoryCheckDetailDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({url: Api.inventoryCheckDetailDeleteBatch, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}
/**
 * 保存或者更新
 * @param params
 */
export const  inventoryCheckDetailSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.inventoryCheckDetailEdit : Api.inventoryCheckDetailSave;
  return defHttp.post({url: url, params});
}
/**
 * 导入
 */
export const inventoryCheckDetailImportUrl = '/wms/inventoryCheck/importInventoryCheckDetail'

/**
 * 导出
 */
export const inventoryCheckDetailExportXlsUrl = '/wms/inventoryCheck/exportInventoryCheckDetail'

/**
* 开始盘点
*/
export const startCheck = (params) => {
  return defHttp.post({url: Api.startCheck, params},{joinParamsToUrl: true});
};
/**
 * 单条盘点确认
 * @param params
 */
export const confirmDetail = (params) => {
  return defHttp.post({url: Api.confirmDetail, params});
};
/**
 * 批量盘点确认
 * @param params
 */
export const batchConfirm = (params) => {
  return defHttp.post({url: Api.batchConfirm, params});
};

/**
 * 审核盘点
 * @param params
 */
export const approveCheck = (params) => {
  return defHttp.post({url: Api.approveCheck, params});
};

/**
 * 预览库存（盘点前查看工作量）
 */
export const inventoryCheckPreview = (params: {id: string}) => {
  return defHttp.get({url: Api.previewStock, params});
};

export const finishCheck = (params:{id:string}) =>{
  return defHttp.post({url: Api.finishCheck, params}, {joinParamsToUrl: true});
}

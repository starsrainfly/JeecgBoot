import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/wms/stockOut/list',
  save='/wms/stockOut/apply',
  edit='/wms/stockOut/edit',
  deleteOne = '/wms/stockOut/delete',
  deleteBatch = '/wms/stockOut/deleteBatch',
  importExcel = '/wms/stockOut/importExcel',
  exportXls = '/wms/stockOut/exportXls',
  stockOutDetailList = '/wms/stockOut/queryStockOutDetailByMainId',
  cancel='/wms/stockOut/cancel',
  reject='/wms/stockOut/reject',
  approve='/wms/stockOut/approve',
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
 * 查询子表数据
 * @param params
 */
export const stockOutDetailList = Api.stockOutDetailList;
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
 * 出库审核
 * @param params
 */
export const approveStockOut = (params) => {
  return defHttp.post({url:Api.approve, params})
}
/**
 * 审核拒绝
 * @param params
 */
export const rejectStockOut =(params) => {
  return defHttp.post({url:Api.reject, params});
}
/**
 * 取消申请
 * @param params 传id
 */
export const cancelStockOut = (params) => {
  return defHttp.post({url:Api.cancel, params})
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

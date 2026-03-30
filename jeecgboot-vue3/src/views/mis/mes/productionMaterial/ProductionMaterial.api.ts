import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/mes/productionMaterial/list',
  save='/mes/productionMaterial/add',
  edit='/mes/productionMaterial/edit',
  deleteOne = '/mes/productionMaterial/delete',
  deleteBatch = '/mes/productionMaterial/deleteBatch',
  importExcel = '/mes/productionMaterial/importExcel',
  exportXls = '/mes/productionMaterial/exportXls',
  getBatchesByOrder = '/mes/productionMaterial/getBatchesByOrder',
  getMaterialSummary = '/mes/productionMaterial/getMaterialSummary',
  submitStockOutApply = '/mes/misStockOut/submitApply',
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
 * 根据订单ID获取批次列表
 */
export const getBatchesByOrder = (orderId: string) => {
  // 使用 params 对象，让 axios 自动处理 URL 参数
  return defHttp.get({
    url: Api.getBatchesByOrder,
    params: { orderId: orderId }  // 明确指定参数名
  }, {
    // 确保不拼接 URL
    joinParamsToUrl: false
  });
};


// 根据批次ID获取物料汇总（真实数据）
export const getMaterialSummary = (params: { batchIds?: string[], orderId?: string, materialReqIds?: string[] }) => {
  return defHttp.post({ url: Api.getMaterialSummary, data: params });
};

/**
 * 提交出库申请（统一接口，支持单条/批量/按订单）
 */
// 提交出库申请
export const submitStockOutApply = (data: any) => {
  return defHttp.post({ url: Api.submitStockOutApply, data });
};

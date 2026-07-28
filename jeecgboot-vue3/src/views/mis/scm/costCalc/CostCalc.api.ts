import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/scm/costCalc/list',
  save='/scm/costCalc/add',
  edit='/scm/costCalc/edit',
  deleteOne = '/scm/costCalc/delete',
  deleteBatch = '/scm/costCalc/deleteBatch',
  importExcel = '/scm/costCalc/importExcel',
  exportXls = '/scm/costCalc/exportXls',
  costCalcDetailList = '/scm/costCalc/queryCostCalcDetailByMainId',
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
export const costCalcDetailList = Api.costCalcDetailList;
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

// 产品列表
export const getProductList = (params: any) =>
  defHttp.get({ url: '/scm/costCalc/productList', params }, { isTransformResponse: true });

// 实时核算
export const calculateCost = (productId: string) =>
  defHttp.get({ url: '/scm/costCalc/calculate', params: { productId } }, { isTransformResponse: true });

// 保存快照
export const saveSnapshot = (data: any) =>
  defHttp.post({ url: '/scm/costCalc/saveSnapshot', data }, { isTransformResponse: true });

// 快照明细
export const getSnapshotDetail = (id: string) =>
  defHttp.get({ url: '/scm/costCalc/snapshotDetail', params: { id } }, { isTransformResponse: true });

// 快照标准CRUD（复用生成的基础接口）
export const listSnapshot = (params: any) =>
  defHttp.get({ url: '/scm/costCalc/list', params }, { isTransformResponse: true });

export const deleteSnapshot = (id: string) =>
  defHttp.delete({ url: '/scm/costCalc/delete', params: { id } }, { isTransformResponse: true });

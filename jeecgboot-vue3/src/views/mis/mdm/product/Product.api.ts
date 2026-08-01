import {defHttp} from "/@/utils/http/axios";
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/product/product/rootList',
  save='/product/product/add',
  edit='/product/product/edit',
  deleteProduct = '/product/product/delete',
  importExcel = '/product/product/importExcel',
  exportXls = '/product/product/exportXls',
  loadTreeData = '/product/product/loadTreeRoot',
  getChildList = '/product/product/childList',
  getChildListBatch = '/product/product/getChildListBatch',
  getRecipeById = '/Recipe/recipe/queryById',   // ← 新增
}

/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;
/**
 * 导入api
 * @param params
 */
export const getImportUrl = Api.importExcel;
/**
 * 列表接口
 * @param params
 */
export const list = (params) =>
  defHttp.get({url: Api.list, params});
/**
 * 删除
 */
export const deleteProduct = (params,handleSuccess) => {
  return defHttp.delete({url: Api.deleteProduct, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}
/**
 * 批量删除
 * @param params
 */
export const batchDeleteProduct = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({url: Api.deleteProduct, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}
/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdateDict = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({url: url, params});
}
/**
 * 查询全部树形节点数据
 * @param params
 */
export const loadTreeData = (params) =>
  defHttp.get({url: Api.loadTreeData,params});
/**
 * 查询子节点数据
 * @param params
 */
export const getChildList = (params) =>
  defHttp.get({url: Api.getChildList, params});
/**
 * 批量查询子节点数据
 * @param params
 */
export const getChildListBatch = (params) =>
  defHttp.get({url: Api.getChildListBatch, params},{isTransformResponse:false});

/**
 * 根据ID查询配方主表
 * @param params
 */
export const getRecipeById = (params) =>
  defHttp.get({url: Api.getRecipeById, params});

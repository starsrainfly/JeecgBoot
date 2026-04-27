import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/mes/productionPlan/list',
  save='/mes/productionPlan/add',
  edit='/mes/productionPlan/edit',
  deleteOne = '/mes/productionPlan/delete',
  deleteBatch = '/mes/productionPlan/deleteBatch',
  importExcel = '/mes/productionPlan/importExcel',
  exportXls = '/mes/productionPlan/exportXls',
  productionPlanDetailList = '/mes/productionPlan/queryProductionPlanDetailByMainId',
  publish = '/mes/productionPlan/publish',
  publishBatch = '/mes/productionPlan/publishBatch',
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
export const productionPlanDetailList = Api.productionPlanDetailList;
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
 * 计划发布
 */
export const publishPlan = (params,handleSuccess) => {
  return defHttp.post({url: Api.publish, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}
/**
 * 批量发布计划
 * @param params
 * @param handleSuccess
 */
export const publishPlanBatch = (params, handleSuccess) =>{
  createConfirm({
    iconType: 'warning',
    title: '确认发布计划',
    content: '是否发布选中计划',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.post({url: Api.publishBatch, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}

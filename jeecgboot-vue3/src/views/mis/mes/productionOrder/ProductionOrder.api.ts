import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/mes/productionOrder/list',
  save='/mes/productionOrder/add',
  edit='/mes/productionOrder/edit',
  deleteOne = '/mes/productionOrder/delete',
  deleteBatch = '/mes/productionOrder/deleteBatch',
  importExcel = '/mes/productionOrder/importExcel',
  exportXls = '/mes/productionOrder/exportXls',
  productionOrderDetailList = '/mes/productionOrder/queryProductionOrderDetailByMainId',

  // 新增接口
  getPackageMapping = '/mdm/packageMapping/getByInnerAndOuter',  // 查询包装映射
  getPlanDetailList = '/mes/productionPlan/listForOrder',  // 查询可用计划明细
  batchRelease = '/mes/productionOrder/batchRelease',              // 批量下达任务
  release = '/mes/productionOrder/release',
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
export const productionOrderDetailList = Api.productionOrderDetailList;
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
 * 查询包装映射关系
 */
export const getPackageMapping = (params: {innerPackageId: string, outerPackageId: string}) => {
  return defHttp.get({url: Api.getPackageMapping, params});
}

/**
 * 查询可用计划明细（已发布、有剩余）
 */
export const getPlanDetailList = (params) => {
  return defHttp.get({url: Api.getPlanDetailList, params});
}

/**下达任务*/
export const releaseOne = (params,handleSuccess) => {
  return defHttp.post({url: Api.release, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}
/**批量下达任务*/
export const batchRelease = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认下达',
    content: '是否下达选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.post({url: Api.batchRelease, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}

import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/scm/purchaseOrder/list',
  save='/scm/purchaseOrder/add',
  edit='/scm/purchaseOrder/edit',
  deleteOne = '/scm/purchaseOrder/delete',
  deleteBatch = '/scm/purchaseOrder/deleteBatch',
  importExcel = '/scm/purchaseOrder/importExcel',
  exportXls = '/scm/purchaseOrder/exportXls',
  purchaseOrderDetailList = '/scm/purchaseOrder/queryPurchaseOrderDetailByMainId',

  approve = '/scm/purchaseOrder/approve',
  exchangeRate ='/mdm/currency/currency/getRateByCode',

  // ===== 采购执行跟踪新增接口 =====
  trackingList = '/scm/purchaseOrder/trackingList',
  trackingDetail = '/scm/purchaseOrder/trackingDetail',
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
export const purchaseOrderDetailList = Api.purchaseOrderDetailList;
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

// 文件末尾追加
/** 审核 */
export const approve = (params) => defHttp.post({url: Api.approve, params});

/** 查询币种最新汇率 TODO: 按你们汇率表实际接口路径调整
 * exchangeRate ='/mdm/currency/currency/getRateByCode', */
export const getLatestRate = (params) => defHttp.get({url: Api.exchangeRate, params});


/* ==================== 采购执行跟踪新增接口 ==================== */

/**
 * 采购执行跟踪-分页列表
 */
export const trackingList = (params) => defHttp.get({ url: Api.trackingList, params });

/**
 * 采购执行跟踪-详情
 */
export const trackingDetail = (id) => defHttp.get({ url: Api.trackingDetail, params: { id } });

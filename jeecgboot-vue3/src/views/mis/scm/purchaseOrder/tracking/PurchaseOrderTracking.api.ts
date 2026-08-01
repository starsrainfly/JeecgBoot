import { defHttp } from '/@/utils/http/axios';

enum Api {
  trackingList = '/scm/purchaseOrder/trackingList',
  trackingDetail = '/scm/purchaseOrder/trackingDetail',
}

/**
 * 采购执行跟踪-分页列表
 */
export const trackingList = (params) => defHttp.get({ url: Api.trackingList, params });

/**
 * 采购执行跟踪-详情
 */
export const trackingDetail = (id) => defHttp.get({ url: Api.trackingDetail, params: { id } });

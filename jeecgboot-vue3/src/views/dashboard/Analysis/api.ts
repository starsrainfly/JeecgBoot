import { defHttp } from '/@/utils/http/axios';

enum Api {
  loginfo = '/sys/loginfo',
  visitInfo = '/sys/visitInfo',
  production = '/mes/productionDashboard/data',
  sales ='/scm/salesDashboard/summary',
  warehouse ='/wms/dashboard/warehouse',
}
/**
 * 日志统计信息
 * @param params
 */
export const getLoginfo = (params) => defHttp.get({ url: Api.loginfo, params }, { isTransformResponse: false });
/**
 * 访问量信息
 * @param params
 */
export const getVisitInfo = (params) => defHttp.get({ url: Api.visitInfo, params }, { isTransformResponse: false });

/**
 * 获取仓储工作台数据
 */
export const getWarehouseData = (params?) =>
  defHttp.get({ url: Api.warehouse, params }, { isTransformResponse: false });

/**
 * 获取销售工作台数据
 */
export const getSalesData = (params?) =>
  defHttp.get({ url: Api.sales, params }, { isTransformResponse: false });

/**
 * 获取销售数据汇总（首页用，全量）
 */
export const getSalesSummary = (params?) =>
  defHttp.get({ url: Api.sales, params }, { isTransformResponse: false });
/**
 * 获取生产工作台数据
 */
export const getProductionData = (params?) =>
  defHttp.get({ url: Api.production, params: { viewType: 'manager' } }, { isTransformResponse: false });

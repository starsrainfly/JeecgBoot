import {defHttp} from '/@/utils/http/axios';

enum Api {
  summary = '/wms/stock/summary',
  detailByGoods = '/wms/stock/detailByGoods',
  exportXls = '/wms/stock/exportXls',
}

/**
 * 汇总列表接口
 */
export const getSummaryList = (params) =>
  defHttp.get({url: Api.summary, params});

/**
 * 查询物料批次明细
 */
export const getDetailByGoods = (params) =>
  defHttp.get({url: Api.detailByGoods, params});

/**
 * 导出url
 */
export const getExportUrl = Api.exportXls;

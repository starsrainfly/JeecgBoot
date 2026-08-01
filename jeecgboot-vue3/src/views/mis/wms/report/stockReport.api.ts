import { defHttp } from '/@/utils/http/axios';

enum Api {
  inSummaryBySupplier = '/wms/stockReport/inSummaryBySupplier',
  outSummaryByCustomer = '/wms/stockReport/outSummaryByCustomer',
  monthlyReport = '/wms/stockReport/monthlyReport',
  warehouseSummary = '/wms/stockReport/warehouseSummary',
  // 导出
  exportInSummary = '/wms/stockReport/exportInSummary',
  exportOutSummary = '/wms/stockReport/exportOutSummary',
  exportMonthlyReport = '/wms/stockReport/exportMonthlyReport',
  exportWarehouseSummary = '/wms/stockReport/exportWarehouseSummary',
}

/** 入库汇总表（按供应商） */
export const inSummaryBySupplier = (params) => defHttp.get({ url: Api.inSummaryBySupplier, params });

/** 出库汇总表（按客户） */
export const outSummaryByCustomer = (params) => defHttp.get({ url: Api.outSummaryByCustomer, params });

/** 收发存月报（按物料） */
export const monthlyReport = (params) => defHttp.get({ url: Api.monthlyReport, params });

/** 仓库收发存汇总 */
export const warehouseSummary = (params) => defHttp.get({ url: Api.warehouseSummary, params });

/** 导出URL */
export const getExportInSummaryUrl = Api.exportInSummary;
export const getExportOutSummaryUrl = Api.exportOutSummary;
export const getExportMonthlyReportUrl = Api.exportMonthlyReport;
export const getExportWarehouseSummaryUrl = Api.exportWarehouseSummary;

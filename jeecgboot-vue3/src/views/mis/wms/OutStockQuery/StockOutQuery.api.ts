import { defHttp } from '/@/utils/http/axios';

// 查询列表（带合计）
export const listDetailAll = (params) => {
  return defHttp.get({ url: '/wms/stockOut/listDetailAll', params });
};

// 导出
export const getDetailExportUrl = '/wms/stockOut/exportStockOutDetailXls';

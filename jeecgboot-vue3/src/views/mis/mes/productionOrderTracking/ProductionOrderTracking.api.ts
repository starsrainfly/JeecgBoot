import {defHttp} from '/@/utils/http/axios';

enum Api {
  list = '/mes/productionOrderTracking/list',
  exportXls = '/mes/productionOrderTracking/exportXls',
}

export const getList = (params) =>
  defHttp.get({url: Api.list, params});

export const getExportUrl = Api.exportXls;

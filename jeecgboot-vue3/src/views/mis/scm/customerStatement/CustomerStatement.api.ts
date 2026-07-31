import {defHttp} from '/@/utils/http/axios';

enum Api {
  list = '/scm/customerStatement/list',
  exportXls = '/scm/customerStatement/exportXls',
}

export const getList = (params) =>
  defHttp.get({url: Api.list, params});

export const getExportUrl = Api.exportXls;

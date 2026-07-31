import {defHttp} from '/@/utils/http/axios';

enum Api {
  list = '/scm/receivableAging/list',
  exportXls = '/scm/receivableAging/exportXls',
}

export const getList = (params) =>
  defHttp.get({url: Api.list, params});

export const getExportUrl = Api.exportXls;

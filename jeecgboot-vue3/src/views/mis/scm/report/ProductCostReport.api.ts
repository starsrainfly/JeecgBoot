import {defHttp} from '/@/utils/http/axios';

enum Api {
  list = '/scm/costCalcReport/list',
  exportXls = '/scm/costCalcReport/exportXls',
  dashboard = '/scm/costCalcReport/dashboard',
}

export const getExportUrl = Api.exportXls;

export const list = (params) =>
  defHttp.get({url: Api.list, params}, {isTransformResponse: true});

export const dashboard = (params) =>
  defHttp.get({url: Api.dashboard, params}, {isTransformResponse: true});

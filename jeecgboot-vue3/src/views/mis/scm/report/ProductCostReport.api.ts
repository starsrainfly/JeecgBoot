import {defHttp} from '/@/utils/http/axios';

enum Api {
  list = '/scm/costCalcReport/list',
  exportXls = '/scm/costCalcReport/exportXls',
}

export const getExportUrl = Api.exportXls;

export const list = (params) =>
  defHttp.get({url: Api.list, params}, {isTransformResponse: true});

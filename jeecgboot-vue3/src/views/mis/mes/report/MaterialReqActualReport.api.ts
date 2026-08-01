import {defHttp} from '/@/utils/http/axios';

enum Api {
  list = '/mes/materialReqActualReport/list',
  batchDetail = '/mes/materialReqActualReport/batchDetail',
  exportXls = '/mes/materialReqActualReport/exportXls',
}

export const getExportUrl = Api.exportXls;

export const list = (params) =>
  defHttp.get({url: Api.list, params}, {isTransformResponse: true});

export const batchDetail = (params) =>
  defHttp.get({url: Api.batchDetail, params}, {isTransformResponse: true});

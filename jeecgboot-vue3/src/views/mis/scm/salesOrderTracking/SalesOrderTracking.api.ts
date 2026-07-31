import {defHttp} from '/@/utils/http/axios';

enum Api {
  list = '/scm/salesOrderTracking/list',
}

export const getList = (params) =>
  defHttp.get({url: Api.list, params});

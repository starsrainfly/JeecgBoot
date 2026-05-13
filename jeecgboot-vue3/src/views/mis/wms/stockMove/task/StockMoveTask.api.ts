import { defHttp } from '/@/utils/http/axios';

enum Api {
  PENDING_LIST = '/wms/stockMove/pendingList',
  DO_MOVE = '/wms/stockMove/doMove',
  BATCH_MOVE = '/wms/stockMove/batchMove',
}

/** 查询可移库库存列表 */
export const pendingList = (params) => defHttp.get({ url: Api.PENDING_LIST, params });

/** 执行移库 */
export const doMove = (params) => defHttp.post({ url: Api.DO_MOVE, params });

/** 批量移库 */
export const batchMove = (params) => defHttp.post({ url: Api.BATCH_MOVE, params });

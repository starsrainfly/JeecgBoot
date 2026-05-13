// src/views/wms/shelfTask/ShelfTask.api.ts
import { defHttp } from '/@/utils/http/axios';


enum Api {
  PENDING_LIST = '/wms/shelfRecord/pendingList',
  DO_SHELF = '/wms/shelfRecord/doShelf',
  BATCH_SHELF = '/wms/shelfRecord/batchShelf',
}

// 查询待上架库存列表
export const pendingList = (params) =>
  defHttp.get({ url: Api.PENDING_LIST, params });

// 执行上架
export const doShelf = (data: any) => defHttp.post({ url: Api.DO_SHELF, data });

// 批量上架
export const batchShelf = (data: any[]) => defHttp.post({ url: Api.BATCH_SHELF, data });

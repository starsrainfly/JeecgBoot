// views/mes/productionBatchCompletionReport/ProductionBatchCompletionReport.api.ts
import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/mes/productionBatch/listNew',
  exportXls = '/mes/productionBatch/exportXls',
  getWeighingDetail = '/mes/productionBatch/getWeighingDetail',
  getTaskList = '/mes/productionTask/list',
}

export const getList = (params) => defHttp.get({ url: Api.list, params });
export const getExportUrl = Api.exportXls;
export const getWeighingDetail = (batchId: string) =>
  defHttp.get({ url: Api.getWeighingDetail, params: { batchId } });
export const getTaskList = (params) => defHttp.get({ url: Api.getTaskList, params });

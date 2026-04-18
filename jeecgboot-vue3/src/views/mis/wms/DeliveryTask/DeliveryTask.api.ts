import { defHttp } from '/@/utils/http/axios';

enum Api {
  taskList = '/wms/delivery/taskList',
  pendingLines = '/wms/delivery/pendingOrderLines',
  scanCode = '/wms/delivery/scanCode',
  scanDeliver = '/wms/delivery/scanDeliver',
}

// 待发货任务列表（订单维度）
export const getTaskList = (params) => defHttp.get({ url: Api.taskList, params });

// 获取订单未发货明细
export const getPendingLines = (params: { orderId: string }) =>
  defHttp.get({ url: Api.pendingLines, params });

// 扫码解析
export const scanCode = (params: any) => defHttp.post({ url: Api.scanCode, params });

// 扫码发货提交
export const scanDeliver = (params: any) => defHttp.post({ url: Api.scanDeliver, params });

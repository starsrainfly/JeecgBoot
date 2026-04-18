import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

enum Api {
  list = '/wms/delivery/list',
  deleteOne = '/wms/delivery/delete',
  pendingOrderLines = '/wms/delivery/pendingOrderLines',
  scanCode = '/wms/delivery/scanCode',
  scanDeliver = '/wms/delivery/scanDeliver',
}

/**
 * 发货单列表
 */
export const list = (params) => defHttp.get({ url: Api.list, params });

/**
 * 删除发货单
 */
export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 查询未发货订单明细
 */
export const getPendingOrderLines = (params: { orderId: string }) =>
  defHttp.get({ url: Api.pendingOrderLines, params });

/**
 * 扫码解析
 */
export const scanCode = (params: any) => defHttp.post({ url: Api.scanCode, params });

/**
 * 扫码发货提交
 */
export const scanDeliver = (params: any) => defHttp.post({ url: Api.scanDeliver, params });

import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/mes/materialRequirement/list',
  save='/mes/materialRequirement/add',
  edit='/mes/materialRequirement/edit',
  deleteOne = '/mes/materialRequirement/delete',
  deleteBatch = '/mes/materialRequirement/deleteBatch',
  importExcel = '/mes/materialRequirement/importExcel',
  exportXls = '/mes/materialRequirement/exportXls',
  getBatchesByOrder = '/mes/materialRequirement/getBatchesByOrder',
  getMaterialSummaryByBatches = '/mes/materialRequirement/getMaterialSummary'
}
/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;
/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;
/**
 * 列表接口
 * @param params
 */
export const list = (params) =>
  defHttp.get({url: Api.list, params});

/**
 * 删除单个
 */
export const deleteOne = (params,handleSuccess) => {
  return defHttp.delete({url: Api.deleteOne, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}
/**
 * 批量删除
 * @param params
 */
export const batchDelete = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({url: Api.deleteBatch, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}
/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({url: url, params});
}

/**
 * 根据订单ID获取批次列表
 */
export const getBatchesByOrder = (params: { orderId: string }) =>
  defHttp.get({ url: Api.getBatchesByOrder, params });

/**
 * 根据批次ID获取物料汇总（待发料）
 */
export const getMaterialSummaryByBatches = (params: {
  batchIds: string[];
  orderId?: string;
}) => defHttp.post({ url: Api.getMaterialSummaryByBatches, params });

/**
 * 提交出库申请（统一接口，支持单条/批量/按订单）
 */
export const submitIssueApply = (params: {
  applyType: string;
  expectDate: string;
  urgentLevel: string;
  remark: string;
  orderId?: string;
  batchIds?: string[];
  detailList: Array<{
    materialReqId?: string;
    materialId: string;
    applyQty: number;
    overApplyReason?: string;
    sourceRecords?: any[];
  }>;
}) => defHttp.post({ url: '/mes/stockOutApply/submit', params });

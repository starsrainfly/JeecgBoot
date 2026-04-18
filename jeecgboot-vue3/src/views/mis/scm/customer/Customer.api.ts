import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/scm/customer/list',
  save='/scm/customer/add',
  edit='/scm/customer/edit',
  deleteOne = '/scm/customer/delete',
  deleteBatch = '/scm/customer/deleteBatch',
  importExcel = '/scm/customer/importExcel',
  exportXls = '/scm/customer/exportXls',
  customerAddressList = '/scm/customer/queryCustomerAddressByMainId',
  customerQualificationList = '/scm/customer/queryCustomerQualificationByMainId',
  customerContactList = '/scm/customer/queryCustomerContactByMainId',
  customerSalesmanList = '/scm/customer/queryCustomerSalesmanByMainId',
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
 * 查询子表数据
 * @param params
 */
export const customerAddressList = Api.customerAddressList;
/**
 * 查询子表数据
 * @param params
 */
export const customerQualificationList = Api.customerQualificationList;
/**
 * 查询子表数据
 * @param params
 */
export const customerContactList = Api.customerContactList;
/**
 * 查询子表数据
 * @param params
 */
export const customerSalesmanList = Api.customerSalesmanList;
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

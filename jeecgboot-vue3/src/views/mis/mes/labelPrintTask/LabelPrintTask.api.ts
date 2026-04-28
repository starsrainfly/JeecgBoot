import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/mes/labelPrintTask/list',
  save='/mes/labelPrintTask/add',
  edit='/mes/labelPrintTask/edit',
  deleteOne = '/mes/labelPrintTask/delete',
  deleteBatch = '/mes/labelPrintTask/deleteBatch',
  importExcel = '/mes/labelPrintTask/importExcel',
  exportXls = '/mes/labelPrintTask/exportXls',
  // 新增接口
  print = '/mes/labelPrintTask/print',
  getCompanyInfo = '/sys/sysDepart/queryById',
  getTemplateInfo = '/mdm/labelTemplate/queryById',
  generateImage ='/mes/labelPrintTask/generateImage',
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
 * 执行打印
 * @param params {id, copies}
 */
export const printLabel = (params) => {
  return defHttp.post({url: Api.print, params});
}
/**
 * 获取公司/部门信息（用于公司选择后联动显示名称）
 * @param id 部门ID
 */
export const getCompanyInfo = (id) => {
  return defHttp.get({url: Api.getCompanyInfo, params: {id}});
}

export const getTemplateInfo =(params) =>{
  return defHttp.get({url:Api.getTemplateInfo,params});
}
// 生成标签预览图片
export const generateLabelImage = (params: { id: string; dpi?: number }) => {
  return defHttp.get({ url: Api.generateImage, params }, { isTransformResponse: false });
};
// 确认打印（记录份数）
export const confirmPrint = (params: { id: string; copies: number }) => {
  return defHttp.post({ url: Api.print, params }, { isTransformResponse: false });
};

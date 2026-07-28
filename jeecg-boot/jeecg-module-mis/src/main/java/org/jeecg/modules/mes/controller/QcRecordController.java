package org.jeecg.modules.mes.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mes.entity.QcRecordDetail;
import org.jeecg.modules.mes.entity.QcRecord;
import org.jeecg.modules.mes.vo.QcRecordPage;
import org.jeecg.modules.mes.service.IQcRecordService;
import org.jeecg.modules.mes.service.IQcRecordDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;


 /**
 * @Description: 质检记录
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
@Tag(name="质检记录")
@RestController
@RequestMapping("/mes/qcRecord")
@Slf4j
public class QcRecordController {
	@Autowired
	private IQcRecordService qcRecordService;
	@Autowired
	private IQcRecordDetailService qcRecordDetailService;
	
	/**
	 * 分页列表查询
	 *
	 * @param qcRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "质检记录-分页列表查询")
	@Operation(summary="质检记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<QcRecord>> queryPageList(QcRecord qcRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<QcRecord> queryWrapper = QueryGenerator.initQueryWrapper(qcRecord, req.getParameterMap());
		Page<QcRecord> page = new Page<QcRecord>(pageNo, pageSize);
		IPage<QcRecord> pageList = qcRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param qcRecordPage
	 * @return
	 */
	@AutoLog(value = "质检记录-添加")
	@Operation(summary="质检记录-添加")
    @RequiresPermissions("mes:mis_qc_record:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody QcRecordPage qcRecordPage) {
		QcRecord qcRecord = new QcRecord();
		BeanUtils.copyProperties(qcRecordPage, qcRecord);
		qcRecordService.saveMain(qcRecord, qcRecordPage.getQcRecordDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param qcRecordPage
	 * @return
	 */
	@AutoLog(value = "质检记录-编辑")
	@Operation(summary="质检记录-编辑")
    @RequiresPermissions("mes:mis_qc_record:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody QcRecordPage qcRecordPage) {
		QcRecord qcRecord = new QcRecord();
		BeanUtils.copyProperties(qcRecordPage, qcRecord);
		QcRecord qcRecordEntity = qcRecordService.getById(qcRecord.getId());
		if(qcRecordEntity==null) {
			return Result.error("未找到对应数据");
		}
		qcRecordService.updateMain(qcRecord, qcRecordPage.getQcRecordDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "质检记录-通过id删除")
	@Operation(summary="质检记录-通过id删除")
    @RequiresPermissions("mes:mis_qc_record:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		qcRecordService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "质检记录-批量删除")
	@Operation(summary="质检记录-批量删除")
    @RequiresPermissions("mes:mis_qc_record:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.qcRecordService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "质检记录-通过id查询")
	@Operation(summary="质检记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<QcRecord> queryById(@RequestParam(name="id",required=true) String id) {
		QcRecord qcRecord = qcRecordService.getById(id);
		if(qcRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(qcRecord);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "质检记录明细通过主表ID查询")
	@Operation(summary="质检记录明细主表ID查询")
	@GetMapping(value = "/queryQcRecordDetailByMainId")
	public Result<List<QcRecordDetail>> queryQcRecordDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<QcRecordDetail> qcRecordDetailList = qcRecordDetailService.selectByMainId(id);
		return Result.OK(qcRecordDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param qcRecord
    */
    @RequiresPermissions("mes:mis_qc_record:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, QcRecord qcRecord) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<QcRecord> queryWrapper = QueryGenerator.initQueryWrapper(qcRecord, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<QcRecord> qcRecordList = qcRecordService.list(queryWrapper);

      // Step.3 组装pageList
      List<QcRecordPage> pageList = new ArrayList<QcRecordPage>();
      for (QcRecord main : qcRecordList) {
          QcRecordPage vo = new QcRecordPage();
          BeanUtils.copyProperties(main, vo);
          List<QcRecordDetail> qcRecordDetailList = qcRecordDetailService.selectByMainId(main.getId());
          vo.setQcRecordDetailList(qcRecordDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "质检记录列表");
      mv.addObject(NormalExcelConstants.CLASS, QcRecordPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("质检记录数据", "导出人:"+sysUser.getRealname(), "质检记录"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mes:mis_qc_record:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // 获取上传文件对象
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<QcRecordPage> list = ExcelImportUtil.importExcel(file.getInputStream(), QcRecordPage.class, params);
              for (QcRecordPage page : list) {
                  QcRecord po = new QcRecord();
                  BeanUtils.copyProperties(page, po);
                  qcRecordService.saveMain(po, page.getQcRecordDetailList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.OK("文件导入失败！");
    }


	 /**
	  * 预生成检验项目（打开质检弹窗时调用）
	  */
	 @Operation(summary = "质检-预生成检验项目")
	 @GetMapping(value = "/previewItems")
	 public Result<List<Map<String, Object>>> previewItems(@RequestParam(name = "taskId") String taskId) {
		 return Result.OK(qcRecordService.previewItems(taskId));
	 }

	 /**
	  * 质检提交（保存记录 + 完工工单）
	  */
	 @AutoLog(value = "质检-提交质检结果")
	 @Operation(summary = "质检-提交质检结果")
	 @PostMapping(value = "/complete")
	 public Result<String> complete(@RequestBody QcRecordPage page) {
		 qcRecordService.completeQc(page);
		 return Result.OK("质检完成！");
	 }

}

package org.jeecg.modules.mdm.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mdm.entity.LogisticsCompany;
import org.jeecg.modules.mdm.service.ILogisticsCompanyService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 物流公司表
 * @Author: jeecg-boot
 * @Date:   2026-04-14
 * @Version: V1.0
 */
@Tag(name="物流公司表")
@RestController
@RequestMapping("/mdm/logisticsCompany")
@Slf4j
public class LogisticsCompanyController extends JeecgController<LogisticsCompany, ILogisticsCompanyService> {
	@Autowired
	private ILogisticsCompanyService logisticsCompanyService;
	
	/**
	 * 分页列表查询
	 *
	 * @param logisticsCompany
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "物流公司表-分页列表查询")
	@Operation(summary="物流公司表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<LogisticsCompany>> queryPageList(LogisticsCompany logisticsCompany,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("companyType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<LogisticsCompany> queryWrapper = QueryGenerator.initQueryWrapper(logisticsCompany, req.getParameterMap(),customeRuleMap);
		// 【添加默认排序】
		queryWrapper.orderByAsc("sort_order");  // 按sort_order升序
		Page<LogisticsCompany> page = new Page<LogisticsCompany>(pageNo, pageSize);
		IPage<LogisticsCompany> pageList = logisticsCompanyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param logisticsCompany
	 * @return
	 */
	@AutoLog(value = "物流公司表-添加")
	@Operation(summary="物流公司表-添加")
	@RequiresPermissions("mdm:mis_logistics_company:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody LogisticsCompany logisticsCompany) {
		logisticsCompanyService.save(logisticsCompany);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param logisticsCompany
	 * @return
	 */
	@AutoLog(value = "物流公司表-编辑")
	@Operation(summary="物流公司表-编辑")
	@RequiresPermissions("mdm:mis_logistics_company:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody LogisticsCompany logisticsCompany) {
		logisticsCompanyService.updateById(logisticsCompany);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物流公司表-通过id删除")
	@Operation(summary="物流公司表-通过id删除")
	@RequiresPermissions("mdm:mis_logistics_company:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		logisticsCompanyService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "物流公司表-批量删除")
	@Operation(summary="物流公司表-批量删除")
	@RequiresPermissions("mdm:mis_logistics_company:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.logisticsCompanyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "物流公司表-通过id查询")
	@Operation(summary="物流公司表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<LogisticsCompany> queryById(@RequestParam(name="id",required=true) String id) {
		LogisticsCompany logisticsCompany = logisticsCompanyService.getById(id);
		if(logisticsCompany==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(logisticsCompany);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param logisticsCompany
    */
    @RequiresPermissions("mdm:mis_logistics_company:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LogisticsCompany logisticsCompany) {
        return super.exportXls(request, logisticsCompany, LogisticsCompany.class, "物流公司表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mdm:mis_logistics_company:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, LogisticsCompany.class);
    }

	 	 /**
	  * 根据物流单号识别快递公司
	  * 支持通过前缀、长度、正则三种方式匹配
	  */
		 @Operation(summary = "物流单号识别快递公司")
		 @GetMapping(value = "/identifyByTrackingNo")
		 public Result<LogisticsCompany> identifyByTrackingNo(@RequestParam("trackingNo") String trackingNo) {
			 if (oConvertUtils.isEmpty(trackingNo)) {
				 return Result.error("物流单号不能为空");
			 }

			 String upperNo = trackingNo.toUpperCase().trim();
			 int noLength = upperNo.length();

			 // 查询所有启用的
			 QueryWrapper<LogisticsCompany> wrapper = new QueryWrapper<>();
			 wrapper.eq("status", "1");
			 wrapper.eq("del_flag", "0");
			 wrapper.orderByAsc("sort_order");
			 List<LogisticsCompany> list = logisticsCompanyService.list(wrapper);

			 // ========== 1. 前缀+长度 联合匹配（最准确） ==========
			 for (LogisticsCompany company : list) {
				 boolean prefixMatch = false;
				 boolean lengthMatch = false;

				 // 检查前缀
				 if (StringUtils.isNotBlank(company.getTrackingPrefixes())) {
					 String[] prefixes = company.getTrackingPrefixes().split(",");
					 for (String prefix : prefixes) {
						 if (upperNo.startsWith(prefix.trim().toUpperCase())) {
							 prefixMatch = true;
							 break;
						 }
					 }
				 }

				 // 检查长度
				 if (StringUtils.isNotBlank(company.getTrackingLengths())) {
					 String[] lengths = company.getTrackingLengths().split(",");
					 for (String len : lengths) {
						 try {
							 if (noLength == Integer.parseInt(len.trim())) {
								 lengthMatch = true;
								 break;
							 }
						 } catch (NumberFormatException e) {}
					 }
				 }

				 // 前缀和长度都匹配
				 if (prefixMatch && lengthMatch) {
					 return Result.OK(company);
				 }
			 }

			 // ========== 2. 纯正则匹配（无固定前缀的单号） ==========
			 for (LogisticsCompany company : list) {
				 if (StringUtils.isNotBlank(company.getTrackingPatterns())) {
					 String[] patterns = company.getTrackingPatterns().split(";");
					 for (String pattern : patterns) {
						 String p = pattern.trim();
						 if (StringUtils.isBlank(p)) continue;
						 try {
							 if (upperNo.matches(p)) {
								 return Result.OK(company);
							 }
						 } catch (Exception e) {
							 log.warn("正则表达式错误：{}, 单号：{}", p, trackingNo);
						 }
					 }
				 }
			 }

			 // ========== 3. 仅长度匹配（最后兜底） ==========
//			 for (LogisticsCompany company : list) {
//				 if (StringUtils.isNotBlank(company.getTrackingLengths())) {
//					 String[] lengths = company.getTrackingLengths().split(",");
//					 for (String len : lengths) {
//						 try {
//							 if (noLength == Integer.parseInt(len.trim())) {
//								 return Result.OK(company);
//							 }
//						 } catch (NumberFormatException e) {}
//					 }
//				 }
//			 }

			 return Result.error("未识别到对应的快递公司");
		 }

}

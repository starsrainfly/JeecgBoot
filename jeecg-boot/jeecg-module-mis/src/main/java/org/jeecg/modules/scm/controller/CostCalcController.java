package org.jeecg.modules.scm.controller;

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

import org.jeecg.modules.scm.dto.CostCalcSnapshotDto;
import org.jeecg.modules.scm.vo.CostCalcDetailVo;
import org.jeecg.modules.scm.vo.CostCalcProductVo;
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
import org.jeecg.modules.scm.entity.CostCalcDetail;
import org.jeecg.modules.scm.entity.CostCalc;
import org.jeecg.modules.scm.vo.CostCalcPage;
import org.jeecg.modules.scm.service.ICostCalcService;
import org.jeecg.modules.scm.service.ICostCalcDetailService;
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
 * @Description: 成本核算快照
 * @Author: jeecg-boot
 * @Date:   2026-07-28
 * @Version: V1.0
 */
@Tag(name="成本核算快照")
@RestController
@RequestMapping("/scm/costCalc")
@Slf4j
public class CostCalcController {
	@Autowired
	private ICostCalcService costCalcService;
	@Autowired
	private ICostCalcDetailService costCalcDetailService;
	
	/**
	 * 分页列表查询
	 *
	 * @param costCalc
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "成本核算快照-分页列表查询")
	@Operation(summary="成本核算快照-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CostCalc>> queryPageList(CostCalc costCalc,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<CostCalc> queryWrapper = QueryGenerator.initQueryWrapper(costCalc, req.getParameterMap());
		Page<CostCalc> page = new Page<CostCalc>(pageNo, pageSize);
		IPage<CostCalc> pageList = costCalcService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param costCalcPage
	 * @return
	 */
	@AutoLog(value = "成本核算快照-添加")
	@Operation(summary="成本核算快照-添加")
    @RequiresPermissions("scm:mis_cost_calc:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CostCalcPage costCalcPage) {
		CostCalc costCalc = new CostCalc();
		BeanUtils.copyProperties(costCalcPage, costCalc);
		costCalcService.saveMain(costCalc, costCalcPage.getCostCalcDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param costCalcPage
	 * @return
	 */
	@AutoLog(value = "成本核算快照-编辑")
	@Operation(summary="成本核算快照-编辑")
    @RequiresPermissions("scm:mis_cost_calc:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CostCalcPage costCalcPage) {
		CostCalc costCalc = new CostCalc();
		BeanUtils.copyProperties(costCalcPage, costCalc);
		CostCalc costCalcEntity = costCalcService.getById(costCalc.getId());
		if(costCalcEntity==null) {
			return Result.error("未找到对应数据");
		}
		costCalcService.updateMain(costCalc, costCalcPage.getCostCalcDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "成本核算快照-通过id删除")
	@Operation(summary="成本核算快照-通过id删除")
    @RequiresPermissions("scm:mis_cost_calc:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		costCalcService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "成本核算快照-批量删除")
	@Operation(summary="成本核算快照-批量删除")
    @RequiresPermissions("scm:mis_cost_calc:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.costCalcService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "成本核算快照-通过id查询")
	@Operation(summary="成本核算快照-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CostCalc> queryById(@RequestParam(name="id",required=true) String id) {
		CostCalc costCalc = costCalcService.getById(id);
		if(costCalc==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(costCalc);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "成本核算快照明细通过主表ID查询")
	@Operation(summary="成本核算快照明细主表ID查询")
	@GetMapping(value = "/queryCostCalcDetailByMainId")
	public Result<List<CostCalcDetail>> queryCostCalcDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<CostCalcDetail> costCalcDetailList = costCalcDetailService.selectByMainId(id);
		return Result.OK(costCalcDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param costCalc
    */
    @RequiresPermissions("scm:mis_cost_calc:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CostCalc costCalc) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<CostCalc> queryWrapper = QueryGenerator.initQueryWrapper(costCalc, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<CostCalc> costCalcList = costCalcService.list(queryWrapper);

      // Step.3 组装pageList
      List<CostCalcPage> pageList = new ArrayList<CostCalcPage>();
      for (CostCalc main : costCalcList) {
          CostCalcPage vo = new CostCalcPage();
          BeanUtils.copyProperties(main, vo);
          List<CostCalcDetail> costCalcDetailList = costCalcDetailService.selectByMainId(main.getId());
          vo.setCostCalcDetailList(costCalcDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "成本核算快照列表");
      mv.addObject(NormalExcelConstants.CLASS, CostCalcPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("成本核算快照数据", "导出人:"+sysUser.getRealname(), "成本核算快照"));
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
    @RequiresPermissions("scm:mis_cost_calc:importExcel")
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
              List<CostCalcPage> list = ExcelImportUtil.importExcel(file.getInputStream(), CostCalcPage.class, params);
              for (CostCalcPage page : list) {
                  CostCalc po = new CostCalc();
                  BeanUtils.copyProperties(page, po);
                  costCalcService.saveMain(po, page.getCostCalcDetailList());
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

	 @AutoLog(value = "成本核算-产品列表")
	 @Operation(summary = "产品列表（带配方）")
	 @GetMapping(value = "/productList")
	 public Result<IPage<CostCalcProductVo>> productList(
			 @RequestParam(name = "productCode", required = false) String productCode,
			 @RequestParam(name = "productName", required = false) String productName,
			 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		 Page<CostCalcProductVo> page = new Page<>(pageNo, pageSize);
		 IPage<CostCalcProductVo> pageList = costCalcService.queryProductList(page, productCode, productName);
		 return Result.OK(pageList);
	 }

	 @AutoLog(value = "成本核算-实时计算")
	 @Operation(summary = "实时计算产品材料成本")
	 @GetMapping(value = "/calculate")
	 public Result<CostCalcDetailVo> calculate(@RequestParam(name = "productId") String productId) {
		 CostCalcDetailVo vo = costCalcService.calculateCost(productId);
		 return Result.OK(vo);
	 }

	 @AutoLog(value = "成本核算-保存快照")
	 @Operation(summary = "保存成本快照")
	 @PostMapping(value = "/saveSnapshot")
	 public Result<CostCalc> saveSnapshot(@RequestBody CostCalcSnapshotDto dto) {
		 CostCalc calc = costCalcService.saveSnapshot(dto);
		 return Result.OK("保存成功", calc);
	 }

	 @AutoLog(value = "成本核算-快照明细")
	 @Operation(summary = "查看快照明细")
	 @GetMapping(value = "/snapshotDetail")
	 public Result<CostCalcDetailVo> snapshotDetail(@RequestParam(name = "id") String id) {
		 CostCalcDetailVo vo = costCalcService.getSnapshotDetail(id);
		 return Result.OK(vo);
	 }

	 @AutoLog(value = "成本核算-月度自动核算")
	 @Operation(summary = "月度自动核算（供XXL-JOB调用）")
	 @PostMapping(value = "/monthlyAutoCalc")
	 public Result<String> monthlyAutoCalc() {
		 costCalcService.monthlyAutoCalc();
		 return Result.OK("月度核算完成");
	 }

}

package org.jeecg.modules.mes.controller;

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
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mes.entity.ProductionStep;
import org.jeecg.modules.mes.service.IProductionStepService;

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
 * @Description: 派工管理
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Tag(name="派工管理")
@RestController
@RequestMapping("/mes/productionStep")
@Slf4j
public class ProductionStepController extends JeecgController<ProductionStep, IProductionStepService> {
	@Autowired
	private IProductionStepService productionStepService;
	
	/**
	 * 分页列表查询
	 *
	 * @param productionStep
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "派工管理-分页列表查询")
	@Operation(summary="派工管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ProductionStep>> queryPageList(ProductionStep productionStep,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<ProductionStep> queryWrapper = QueryGenerator.initQueryWrapper(productionStep, req.getParameterMap(),customeRuleMap);
		Page<ProductionStep> page = new Page<ProductionStep>(pageNo, pageSize);
		IPage<ProductionStep> pageList = productionStepService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param productionStep
	 * @return
	 */
	@AutoLog(value = "派工管理-添加")
	@Operation(summary="派工管理-添加")
	@RequiresPermissions("mes:mis_production_step:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProductionStep productionStep) {
		productionStepService.save(productionStep);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param productionStep
	 * @return
	 */
	@AutoLog(value = "派工管理-编辑")
	@Operation(summary="派工管理-编辑")
	@RequiresPermissions("mes:mis_production_step:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProductionStep productionStep) {
		productionStepService.updateById(productionStep);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "派工管理-通过id删除")
	@Operation(summary="派工管理-通过id删除")
	@RequiresPermissions("mes:mis_production_step:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		productionStepService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "派工管理-批量删除")
	@Operation(summary="派工管理-批量删除")
	@RequiresPermissions("mes:mis_production_step:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.productionStepService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "派工管理-通过id查询")
	@Operation(summary="派工管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ProductionStep> queryById(@RequestParam(name="id",required=true) String id) {
		ProductionStep productionStep = productionStepService.getById(id);
		if(productionStep==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(productionStep);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param productionStep
    */
    @RequiresPermissions("mes:mis_production_step:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProductionStep productionStep) {
        return super.exportXls(request, productionStep, ProductionStep.class, "派工管理");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mes:mis_production_step:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ProductionStep.class);
    }

}

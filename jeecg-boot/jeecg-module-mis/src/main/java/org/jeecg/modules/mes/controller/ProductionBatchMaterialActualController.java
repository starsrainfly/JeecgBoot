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
import org.jeecg.modules.mes.entity.ProductionBatchMaterialActual;
import org.jeecg.modules.mes.service.IProductionBatchMaterialActualService;

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
 * @Description: 生产实际投料明细
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Tag(name="生产实际投料明细")
@RestController
@RequestMapping("/mes/productionBatchMaterialActual")
@Slf4j
public class ProductionBatchMaterialActualController extends JeecgController<ProductionBatchMaterialActual, IProductionBatchMaterialActualService> {
	@Autowired
	private IProductionBatchMaterialActualService productionBatchMaterialActualService;
	
	/**
	 * 分页列表查询
	 *
	 * @param productionBatchMaterialActual
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "生产实际投料明细-分页列表查询")
	@Operation(summary="生产实际投料明细-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ProductionBatchMaterialActual>> queryPageList(ProductionBatchMaterialActual productionBatchMaterialActual,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<ProductionBatchMaterialActual> queryWrapper = QueryGenerator.initQueryWrapper(productionBatchMaterialActual, req.getParameterMap());
		Page<ProductionBatchMaterialActual> page = new Page<ProductionBatchMaterialActual>(pageNo, pageSize);
		IPage<ProductionBatchMaterialActual> pageList = productionBatchMaterialActualService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param productionBatchMaterialActual
	 * @return
	 */
	@AutoLog(value = "生产实际投料明细-添加")
	@Operation(summary="生产实际投料明细-添加")
	@RequiresPermissions("mes:mis_production_batch_material_actual:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProductionBatchMaterialActual productionBatchMaterialActual) {
		productionBatchMaterialActualService.save(productionBatchMaterialActual);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param productionBatchMaterialActual
	 * @return
	 */
	@AutoLog(value = "生产实际投料明细-编辑")
	@Operation(summary="生产实际投料明细-编辑")
	@RequiresPermissions("mes:mis_production_batch_material_actual:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProductionBatchMaterialActual productionBatchMaterialActual) {
		productionBatchMaterialActualService.updateById(productionBatchMaterialActual);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "生产实际投料明细-通过id删除")
	@Operation(summary="生产实际投料明细-通过id删除")
	@RequiresPermissions("mes:mis_production_batch_material_actual:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		productionBatchMaterialActualService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "生产实际投料明细-批量删除")
	@Operation(summary="生产实际投料明细-批量删除")
	@RequiresPermissions("mes:mis_production_batch_material_actual:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.productionBatchMaterialActualService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产实际投料明细-通过id查询")
	@Operation(summary="生产实际投料明细-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ProductionBatchMaterialActual> queryById(@RequestParam(name="id",required=true) String id) {
		ProductionBatchMaterialActual productionBatchMaterialActual = productionBatchMaterialActualService.getById(id);
		if(productionBatchMaterialActual==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(productionBatchMaterialActual);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param productionBatchMaterialActual
    */
    @RequiresPermissions("mes:mis_production_batch_material_actual:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProductionBatchMaterialActual productionBatchMaterialActual) {
        return super.exportXls(request, productionBatchMaterialActual, ProductionBatchMaterialActual.class, "生产实际投料明细");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mes:mis_production_batch_material_actual:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ProductionBatchMaterialActual.class);
    }

}

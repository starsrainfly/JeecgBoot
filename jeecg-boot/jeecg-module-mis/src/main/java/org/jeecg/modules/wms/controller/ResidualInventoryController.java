package org.jeecg.modules.wms.controller;

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
import org.jeecg.modules.wms.entity.ResidualInventory;
import org.jeecg.modules.wms.service.IResidualInventoryService;

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
 * @Description: 余料库表
 * @Author: jeecg-boot
 * @Date:   2026-04-11
 * @Version: V1.0
 */
@Tag(name="余料库表")
@RestController
@RequestMapping("/wms/residualInventory")
@Slf4j
public class ResidualInventoryController extends JeecgController<ResidualInventory, IResidualInventoryService> {
	@Autowired
	private IResidualInventoryService residualInventoryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param residualInventory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "余料库表-分页列表查询")
	@Operation(summary="余料库表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ResidualInventory>> queryPageList(ResidualInventory residualInventory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<ResidualInventory> queryWrapper = QueryGenerator.initQueryWrapper(residualInventory, req.getParameterMap());
		Page<ResidualInventory> page = new Page<ResidualInventory>(pageNo, pageSize);
		IPage<ResidualInventory> pageList = residualInventoryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param residualInventory
	 * @return
	 */
	@AutoLog(value = "余料库表-添加")
	@Operation(summary="余料库表-添加")
	@RequiresPermissions("wms:mis_residual_inventory:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ResidualInventory residualInventory) {
		residualInventoryService.save(residualInventory);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param residualInventory
	 * @return
	 */
	@AutoLog(value = "余料库表-编辑")
	@Operation(summary="余料库表-编辑")
	@RequiresPermissions("wms:mis_residual_inventory:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ResidualInventory residualInventory) {
		residualInventoryService.updateById(residualInventory);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "余料库表-通过id删除")
	@Operation(summary="余料库表-通过id删除")
	@RequiresPermissions("wms:mis_residual_inventory:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		residualInventoryService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "余料库表-批量删除")
	@Operation(summary="余料库表-批量删除")
	@RequiresPermissions("wms:mis_residual_inventory:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.residualInventoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "余料库表-通过id查询")
	@Operation(summary="余料库表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ResidualInventory> queryById(@RequestParam(name="id",required=true) String id) {
		ResidualInventory residualInventory = residualInventoryService.getById(id);
		if(residualInventory==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(residualInventory);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param residualInventory
    */
    @RequiresPermissions("wms:mis_residual_inventory:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ResidualInventory residualInventory) {
        return super.exportXls(request, residualInventory, ResidualInventory.class, "余料库表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wms:mis_residual_inventory:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ResidualInventory.class);
    }

}

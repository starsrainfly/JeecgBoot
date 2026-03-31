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
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.service.IStockService;

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
 * @Description: 库存记录表
 * @Author: jeecg-boot
 * @Date:   2026-03-31
 * @Version: V1.0
 */
@Tag(name="库存记录表")
@RestController
@RequestMapping("/wms/stock")
@Slf4j
public class StockController extends JeecgController<Stock, IStockService> {
	@Autowired
	private IStockService stockService;
	
	/**
	 * 分页列表查询
	 *
	 * @param stock
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "库存记录表-分页列表查询")
	@Operation(summary="库存记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Stock>> queryPageList(Stock stock,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<Stock> queryWrapper = QueryGenerator.initQueryWrapper(stock, req.getParameterMap());
		Page<Stock> page = new Page<Stock>(pageNo, pageSize);
		IPage<Stock> pageList = stockService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param stock
	 * @return
	 */
	@AutoLog(value = "库存记录表-添加")
	@Operation(summary="库存记录表-添加")
	@RequiresPermissions("wms:mis_stock:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Stock stock) {
		stockService.save(stock);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param stock
	 * @return
	 */
	@AutoLog(value = "库存记录表-编辑")
	@Operation(summary="库存记录表-编辑")
	@RequiresPermissions("wms:mis_stock:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Stock stock) {
		stockService.updateById(stock);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "库存记录表-通过id删除")
	@Operation(summary="库存记录表-通过id删除")
	@RequiresPermissions("wms:mis_stock:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		stockService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "库存记录表-批量删除")
	@Operation(summary="库存记录表-批量删除")
	@RequiresPermissions("wms:mis_stock:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.stockService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "库存记录表-通过id查询")
	@Operation(summary="库存记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Stock> queryById(@RequestParam(name="id",required=true) String id) {
		Stock stock = stockService.getById(id);
		if(stock==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(stock);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param stock
    */
    @RequiresPermissions("wms:mis_stock:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Stock stock) {
        return super.exportXls(request, stock, Stock.class, "库存记录表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wms:mis_stock:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Stock.class);
    }

}

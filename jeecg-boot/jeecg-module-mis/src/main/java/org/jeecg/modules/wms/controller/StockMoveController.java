package org.jeecg.modules.wms.controller;

import java.math.BigDecimal;
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
import org.jeecg.modules.wms.entity.StockMove;
import org.jeecg.modules.wms.service.IStockMoveService;

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
 * @Description: 移库记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
@Tag(name="移库记录表")
@RestController
@RequestMapping("/wms/stockMove")
@Slf4j
public class StockMoveController extends JeecgController<StockMove, IStockMoveService> {
	@Autowired
	private IStockMoveService stockMoveService;
	
	/**
	 * 分页列表查询
	 *
	 * @param stockMove
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "移库记录表-分页列表查询")
	@Operation(summary="移库记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StockMove>> queryPageList(StockMove stockMove,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<StockMove> queryWrapper = QueryGenerator.initQueryWrapper(stockMove, req.getParameterMap());
		Page<StockMove> page = new Page<StockMove>(pageNo, pageSize);
		IPage<StockMove> pageList = stockMoveService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param stockMove
	 * @return
	 */
	@AutoLog(value = "移库记录表-添加")
	@Operation(summary="移库记录表-添加")
	@RequiresPermissions("wms:mis_stock_move:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody StockMove stockMove) {
		stockMoveService.save(stockMove);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param stockMove
	 * @return
	 */
	@AutoLog(value = "移库记录表-编辑")
	@Operation(summary="移库记录表-编辑")
	@RequiresPermissions("wms:mis_stock_move:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody StockMove stockMove) {
		stockMoveService.updateById(stockMove);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "移库记录表-通过id删除")
	@Operation(summary="移库记录表-通过id删除")
	@RequiresPermissions("wms:mis_stock_move:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		stockMoveService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "移库记录表-批量删除")
	@Operation(summary="移库记录表-批量删除")
	@RequiresPermissions("wms:mis_stock_move:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.stockMoveService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "移库记录表-通过id查询")
	@Operation(summary="移库记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<StockMove> queryById(@RequestParam(name="id",required=true) String id) {
		StockMove stockMove = stockMoveService.getById(id);
		if(stockMove==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(stockMove);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param stockMove
    */
    @RequiresPermissions("wms:mis_stock_move:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StockMove stockMove) {
        return super.exportXls(request, stockMove, StockMove.class, "移库记录表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wms:mis_stock_move:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, StockMove.class);
    }

	 // ==================== 移库作业 ====================

	 // ==================== 移库作业接口 ====================

	 /**
	  * 查询可移库库存列表（已上架的库存）
	  */
	 @AutoLog(value = "移库作业-查询可移库库存")
	 @Operation(summary = "移库作业-查询可移库库存")
	 @GetMapping(value = "/pendingList")
	 public Result<IPage<Stock>> queryMovePendingList(
			 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			 Stock stock) {
		 Page<Stock> page = new Page<>(pageNo, pageSize);
		 IPage<Stock> pageList = stockMoveService.queryMovePendingList(page, stock);
		 return Result.OK(pageList);
	 }

	 /**
	  * 执行移库（单条）
	  */
	 @AutoLog(value = "移库作业-执行移库")
	 @Operation(summary = "移库作业-执行移库")
	 @RequiresPermissions("wms:stock_move:doMove")
	 @PostMapping(value = "/doMove")
	 public Result<String> doMove(@RequestBody StockMove moveRecord) {
		 stockMoveService.doMove(moveRecord);
		 return Result.OK("移库成功");
	 }

	 /**
	  * 批量移库（事务控制，任一失败全部回滚）
	  */
	 @AutoLog(value = "移库作业-批量移库")
	 @Operation(summary = "移库作业-批量移库")
	 @RequiresPermissions("wms:stock_move:batchMove")
	 @PostMapping(value = "/batchMove")
	 public Result<String> batchMove(@RequestBody List<StockMove> records) {
		 stockMoveService.batchMove(records);
		 return Result.OK("批量移库成功");
	 }



}

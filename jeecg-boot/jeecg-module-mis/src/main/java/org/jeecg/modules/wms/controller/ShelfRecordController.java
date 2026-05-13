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
import org.jeecg.modules.wms.entity.ShelfRecord;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.service.IShelfRecordService;

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
import org.springframework.transaction.annotation.Transactional;
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
 * @Description: 上架记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
@Tag(name="上架记录表")
@RestController
@RequestMapping("/wms/shelfRecord")
@Slf4j
public class ShelfRecordController extends JeecgController<ShelfRecord, IShelfRecordService> {
	@Autowired
	private IShelfRecordService shelfRecordService;
	
	/**
	 * 分页列表查询
	 *
	 * @param shelfRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "上架记录表-分页列表查询")
	@Operation(summary="上架记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ShelfRecord>> queryPageList(ShelfRecord shelfRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<ShelfRecord> queryWrapper = QueryGenerator.initQueryWrapper(shelfRecord, req.getParameterMap());
		Page<ShelfRecord> page = new Page<ShelfRecord>(pageNo, pageSize);
		IPage<ShelfRecord> pageList = shelfRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param shelfRecord
	 * @return
	 */
	@AutoLog(value = "上架记录表-添加")
	@Operation(summary="上架记录表-添加")
	@RequiresPermissions("wms:mis_shelf_record:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ShelfRecord shelfRecord) {
		shelfRecordService.save(shelfRecord);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param shelfRecord
	 * @return
	 */
	@AutoLog(value = "上架记录表-编辑")
	@Operation(summary="上架记录表-编辑")
	@RequiresPermissions("wms:mis_shelf_record:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ShelfRecord shelfRecord) {
		shelfRecordService.updateById(shelfRecord);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "上架记录表-通过id删除")
	@Operation(summary="上架记录表-通过id删除")
	@RequiresPermissions("wms:mis_shelf_record:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		shelfRecordService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "上架记录表-批量删除")
	@Operation(summary="上架记录表-批量删除")
	@RequiresPermissions("wms:mis_shelf_record:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.shelfRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "上架记录表-通过id查询")
	@Operation(summary="上架记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ShelfRecord> queryById(@RequestParam(name="id",required=true) String id) {
		ShelfRecord shelfRecord = shelfRecordService.getById(id);
		if(shelfRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(shelfRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param shelfRecord
    */
    @RequiresPermissions("wms:mis_shelf_record:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ShelfRecord shelfRecord) {
        return super.exportXls(request, shelfRecord, ShelfRecord.class, "上架记录表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wms:mis_shelf_record:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ShelfRecord.class);
    }

	 // ==================== 新增业务方法 ====================

	 /**
	  * 查询待上架库存列表
	  */
	 @AutoLog(value = "上架作业-查询待上架库存")
	 @Operation(summary = "上架作业-查询待上架库存")
	 @GetMapping(value = "/pendingList")
	 public Result<IPage<Stock>> queryPendingList(
			 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			 Stock stock) {
		 Page<Stock> page = new Page<>(pageNo, pageSize);
		 IPage<Stock> pageList = shelfRecordService.queryPendingList(page, stock);
		 return Result.OK(pageList);
	 }



	 /**
	  * 执行上架
	  */
	 @AutoLog(value = "上架作业-执行上架")
	 @Operation(summary = "上架作业-执行上架")
	 @PostMapping(value = "/doShelf")
	 public Result<String> doShelf( @RequestBody ShelfRecord shelfRecord) {
		 shelfRecordService.doShelf(shelfRecord);
		 return Result.OK("上架成功");
	 }

	 /**
	  * 批量上架
	  */
	 @AutoLog(value = "上架作业-批量上架")
	 @Operation(summary = "上架作业-批量上架")
	 @PostMapping(value = "/batchShelf")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<String> batchShelf(@RequestBody List<ShelfRecord> records) {
		 for (ShelfRecord record : records) {
			 shelfRecordService.doShelf(record);
		 }
		 return Result.OK("批量上架成功");
	 }
 }

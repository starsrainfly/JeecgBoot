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
import org.jeecg.modules.wms.entity.WarehouseLocation;
import org.jeecg.modules.wms.service.IStockService;
import org.jeecg.modules.wms.service.IWarehouseLocationService;

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
 * @Description: 库位管理
 * @Author: jeecg-boot
 * @Date:   2026-04-05
 * @Version: V1.0
 */
@Tag(name="库位管理")
@RestController
@RequestMapping("/wms/warehouseLocation")
@Slf4j
public class WarehouseLocationController extends JeecgController<WarehouseLocation, IWarehouseLocationService> {
	@Autowired
	private IWarehouseLocationService warehouseLocationService;
	@Autowired
	private IStockService stockService;
	/**
	 * 分页列表查询
	 *
	 * @param warehouseLocation
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "库位管理-分页列表查询")
	@Operation(summary="库位管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WarehouseLocation>> queryPageList(WarehouseLocation warehouseLocation,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("warehouseId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("areaId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("shelfId", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<WarehouseLocation> queryWrapper = QueryGenerator.initQueryWrapper(warehouseLocation, req.getParameterMap(),customeRuleMap);
		Page<WarehouseLocation> page = new Page<WarehouseLocation>(pageNo, pageSize);
		IPage<WarehouseLocation> pageList = warehouseLocationService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param warehouseLocation
	 * @return
	 */
	@AutoLog(value = "库位管理-添加")
	@Operation(summary="库位管理-添加")
	@RequiresPermissions("wms:mis_warehouse_location:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WarehouseLocation warehouseLocation) {
		// 校验货位编码唯一性
		Result<String> checkResult = checkLocationCodeUnique(warehouseLocation);
		if (!checkResult.isSuccess()) {
			return checkResult;
		}

		warehouseLocationService.save(warehouseLocation);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param warehouseLocation
	 * @return
	 */
	@AutoLog(value = "库位管理-编辑")
	@Operation(summary="库位管理-编辑")
	@RequiresPermissions("wms:mis_warehouse_location:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WarehouseLocation warehouseLocation) {
		if (oConvertUtils.isEmpty(warehouseLocation.getId())) {
			return Result.error("编辑失败：缺少主键ID");
		}
		// 校验货位编码唯一性（排除自身）
		Result<String> checkResult = checkLocationCodeUnique(warehouseLocation);
		if (!checkResult.isSuccess()) {
			return checkResult;
		}
		warehouseLocationService.updateById(warehouseLocation);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "库位管理-通过id删除")
	@Operation(summary="库位管理-通过id删除")
	@RequiresPermissions("wms:mis_warehouse_location:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		// 检查是否有库存（可选，根据业务需要）
		 long stockCount = stockService.count(
		     new QueryWrapper<Stock>()
					 .eq("location_id", id)
					 .eq("del_flag", "0")
		 );
		 if (stockCount > 0) {
		     return Result.error("删除失败：该货位下存在库存，请先处理库存");
		 }
		warehouseLocationService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "库位管理-批量删除")
	@Operation(summary="库位管理-批量删除")
	@RequiresPermissions("wms:mis_warehouse_location:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> idList = Arrays.asList(ids.split(","));

		// 检查是否有库存（可选，根据业务需要）
		long stockCount = stockService.count(
				new QueryWrapper<Stock>()
						.eq("location_id", idList)
						.eq("del_flag", "0")
		);
		if (stockCount > 0) {
			return Result.error("删除失败：该货位下存在库存，请先处理库存");
		}

		this.warehouseLocationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "库位管理-通过id查询")
	@Operation(summary="库位管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WarehouseLocation> queryById(@RequestParam(name="id",required=true) String id) {
		WarehouseLocation warehouseLocation = warehouseLocationService.getById(id);
		if(warehouseLocation==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(warehouseLocation);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param warehouseLocation
    */
    @RequiresPermissions("wms:mis_warehouse_location:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WarehouseLocation warehouseLocation) {
        return super.exportXls(request, warehouseLocation, WarehouseLocation.class, "库位管理");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wms:mis_warehouse_location:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WarehouseLocation.class);
    }

	 /**
	  * 校验货位编码唯一性（同一仓库+同一区域+同一货架下不允许重复）
	  * 添加时：全库校验；编辑时：排除当前记录
	  */
	 private Result<String> checkLocationCodeUnique(WarehouseLocation warehouseLocation) {
		 if (oConvertUtils.isEmpty(warehouseLocation.getWarehouseId())) {
			 return Result.error("所属仓库不能为空");
		 }
		 if (oConvertUtils.isEmpty(warehouseLocation.getAreaId())) {
			 return Result.error("所属区域不能为空");
		 }
		 if (oConvertUtils.isEmpty(warehouseLocation.getShelfId())) {
			 return Result.error("所属货架不能为空");
		 }
		 if (oConvertUtils.isEmpty(warehouseLocation.getLocationCode())) {
			 return Result.error("货位编码不能为空");
		 }

		 QueryWrapper<WarehouseLocation> queryWrapper = new QueryWrapper<>();
		 queryWrapper.eq("warehouse_id", warehouseLocation.getWarehouseId());
		 queryWrapper.eq("area_id", warehouseLocation.getAreaId());
		 queryWrapper.eq("shelf_id", warehouseLocation.getShelfId());
		 queryWrapper.eq("location_code", warehouseLocation.getLocationCode());

		 // 编辑时排除当前记录
		 if (oConvertUtils.isNotEmpty(warehouseLocation.getId())) {
			 queryWrapper.ne("id", warehouseLocation.getId());
		 }

		 long count = warehouseLocationService.count(queryWrapper);
		 if (count > 0) {
			 return Result.error("该货架下已存在货位编码【" + warehouseLocation.getLocationCode() + "】");
		 }

		 return Result.OK();
	 }

}

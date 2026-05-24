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
import org.jeecg.modules.wms.entity.Warehouse;
import org.jeecg.modules.wms.entity.WarehouseArea;
import org.jeecg.modules.wms.service.IWarehouseAreaService;
import org.jeecg.modules.wms.service.IWarehouseService;

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
 * @Description: 仓库信息
 * @Author: jeecg-boot
 * @Date:   2026-04-06
 * @Version: V1.0
 */
@Tag(name="仓库信息")
@RestController
@RequestMapping("/wms/warehouse")
@Slf4j
public class WarehouseController extends JeecgController<Warehouse, IWarehouseService> {
	@Autowired
	private IWarehouseService warehouseService;
	@Autowired
	private IWarehouseAreaService warehouseAreaService;
	/**
	 * 分页列表查询
	 *
	 * @param warehouse
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "仓库信息-分页列表查询")
	@Operation(summary="仓库信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Warehouse>> queryPageList(Warehouse warehouse,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("warehouseType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<Warehouse> queryWrapper = QueryGenerator.initQueryWrapper(warehouse, req.getParameterMap(),customeRuleMap);
		Page<Warehouse> page = new Page<Warehouse>(pageNo, pageSize);
		IPage<Warehouse> pageList = warehouseService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param warehouse
	 * @return
	 */
	@AutoLog(value = "仓库信息-添加")
	@Operation(summary="仓库信息-添加")
	//@RequiresPermissions("wms:mis_warehouse:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Warehouse warehouse) {
		// 校验仓库编码全局唯一
		if (checkCodeExists(warehouse.getWarehouseCode(), null)) {
			return Result.error("仓库编码【" + warehouse.getWarehouseCode() + "】已存在");
		}
		warehouseService.save(warehouse);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param warehouse
	 * @return
	 */
	@AutoLog(value = "仓库信息-编辑")
	@Operation(summary="仓库信息-编辑")
	//@RequiresPermissions("wms:mis_warehouse:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Warehouse warehouse) {
		if (oConvertUtils.isEmpty(warehouse.getId())) {
			return Result.error("编辑失败：缺少主键ID");
		}
		// 校验仓库编码全局唯一（排除自身）
		if (checkCodeExists(warehouse.getWarehouseCode(), warehouse.getId())) {
			return Result.error("仓库编码【" + warehouse.getWarehouseCode() + "】已存在");
		}
		warehouseService.updateById(warehouse);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "仓库信息-通过id删除")
	@Operation(summary="仓库信息-通过id删除")
	//@RequiresPermissions("wms:mis_warehouse:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		Warehouse warehouse = warehouseService.getById(id);
		if (warehouse == null) {
			return Result.error("未找到对应数据");
		}

		// 检查是否有下级区域
		long areaCount = warehouseAreaService.count(
				new QueryWrapper<WarehouseArea>()
						.eq("warehouse_id", id)
						.eq("del_flag","0")
		);
		if (areaCount > 0) {
			return Result.error("删除失败：该仓库下存在" + areaCount + "个区域，请先删除区域");
		}
		warehouseService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "仓库信息-批量删除")
	@Operation(summary="仓库信息-批量删除")
	//@RequiresPermissions("wms:mis_warehouse:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> idList = Arrays.asList(ids.split(","));

		// 检查是否有下级区域
		long areaCount = warehouseAreaService.count(
				new QueryWrapper<WarehouseArea>().in("warehouse_id", idList)
						.eq("del_flag","0")
		);
		if (areaCount > 0) {
			return Result.error("删除失败：选中的仓库下存在区域，请先删除区域");
		}

		this.warehouseService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "仓库信息-通过id查询")
	@Operation(summary="仓库信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Warehouse> queryById(@RequestParam(name="id",required=true) String id) {
		Warehouse warehouse = warehouseService.getById(id);
		if(warehouse==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(warehouse);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param warehouse
    */
   // @RequiresPermissions("wms:mis_warehouse:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Warehouse warehouse) {
        return super.exportXls(request, warehouse, Warehouse.class, "仓库信息");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
   // @RequiresPermissions("wms:mis_warehouse:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Warehouse.class);
    }

	 // 辅助方法：检查编码是否已存在
	 private boolean checkCodeExists(String code, String excludeId) {
		 if (oConvertUtils.isEmpty(code)) return false;
		 QueryWrapper<Warehouse> qw = new QueryWrapper<>();
		 qw.eq("warehouse_code", code);
		 if (oConvertUtils.isNotEmpty(excludeId)) {
			 qw.ne("id", excludeId);
		 }
		 return warehouseService.count(qw) > 0;
	 }

}

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
import org.jeecg.modules.wms.entity.WarehouseLocation;
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

}

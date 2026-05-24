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
import org.jeecg.modules.wms.entity.WarehouseArea;
import org.jeecg.modules.wms.entity.WarehouseShelf;
import org.jeecg.modules.wms.service.IWarehouseAreaService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.wms.service.IWarehouseShelfService;
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
 * @Description: 仓库区域
 * @Author: jeecg-boot
 * @Date:   2026-04-05
 * @Version: V1.0
 */
@Tag(name="仓库区域")
@RestController
@RequestMapping("/wms/warehouseArea")
@Slf4j
public class WarehouseAreaController extends JeecgController<WarehouseArea, IWarehouseAreaService> {
	@Autowired
	private IWarehouseAreaService warehouseAreaService;
	@Autowired
	private IWarehouseShelfService warehouseShelfService;
	/**
	 * 分页列表查询
	 *
	 * @param warehouseArea
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "仓库区域-分页列表查询")
	@Operation(summary="仓库区域-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WarehouseArea>> queryPageList(WarehouseArea warehouseArea,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("warehouseId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<WarehouseArea> queryWrapper = QueryGenerator.initQueryWrapper(warehouseArea, req.getParameterMap(),customeRuleMap);
		Page<WarehouseArea> page = new Page<WarehouseArea>(pageNo, pageSize);
		IPage<WarehouseArea> pageList = warehouseAreaService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param warehouseArea
	 * @return
	 */
	@AutoLog(value = "仓库区域-添加")
	@Operation(summary="仓库区域-添加")
	@RequiresPermissions("wms:mis_warehouse_area:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WarehouseArea warehouseArea) {
		WarehouseArea area = warehouseAreaService.getAreaByCode(warehouseArea.getWarehouseId(),warehouseArea.getAreaCode());
		if(area != null) {
			return Result.error("该仓库下已经存在该区域编码！");
		}
		warehouseAreaService.save(warehouseArea);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param warehouseArea
	 * @return
	 */
	@AutoLog(value = "仓库区域-编辑")
	@Operation(summary="仓库区域-编辑")
	@RequiresPermissions("wms:mis_warehouse_area:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WarehouseArea warehouseArea) {
		if (oConvertUtils.isEmpty(warehouseArea.getId())) {
			return Result.error("编辑失败：缺少主键ID");
		}

		WarehouseArea areaSource = warehouseAreaService.getById(warehouseArea.getId());
		if (areaSource == null) {
			return Result.error("未找到对应数据");
		}

		// 1. STAGING编码不能修改
		if ("STAGING".equals(areaSource.getAreaCode())) {
			if (!"STAGING".equals(warehouseArea.getAreaCode())) {
				return Result.error("系统默认编码【STAGING】不能修改！");
			}
		}

		// 2. 如果编码修改了，校验新编码唯一性（同一仓库内）
		if (!areaSource.getAreaCode().equals(warehouseArea.getAreaCode())) {
			WarehouseArea existArea = warehouseAreaService.getAreaByCode(
					warehouseArea.getWarehouseId(),
					warehouseArea.getAreaCode()
			);
			if (existArea != null) {
				return Result.error("该仓库下已经存在区域编码【" + warehouseArea.getAreaCode() + "】");
			}
		}

		warehouseAreaService.updateById(warehouseArea);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "仓库区域-通过id删除")
	@Operation(summary="仓库区域-通过id删除")
	@RequiresPermissions("wms:mis_warehouse_area:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       // 查询要删除的记录
		WarehouseArea area = warehouseAreaService.getById(id);
		if (area == null) {
			return Result.error("未找到对应数据");
		}
		if("STAGING".equals(area.getAreaCode())) {
			return Result.error("删除失败：暂存区（STAGING）为系统默认区域，不允许删除");
		}

		// 检查是否有下级货架（未删除的）
		long shelfCount = warehouseShelfService.count(
				new QueryWrapper<WarehouseShelf>()
						.eq("area_id", id)
						.eq("del_flag", "0")
		);
		if (shelfCount > 0) {
			return Result.error("删除失败：该区域下存在" + shelfCount + "个货架，请先删除货架");
		}

		warehouseAreaService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "仓库区域-批量删除")
	@Operation(summary="仓库区域-批量删除")
	@RequiresPermissions("wms:mis_warehouse_area:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {

		List<String> idList = Arrays.asList(ids.split(","));

		// 检查是否包含受保护的暂存区
		List<WarehouseArea> areaList = warehouseAreaService.listByIds(idList);
        boolean hasStaging = areaList.stream()
                .anyMatch(area -> "STAGING".equals(area.getAreaCode()));

        if (hasStaging) {
            return Result.error("删除失败：选中的记录包含系统默认暂存区(STAGING)，请重新选择后再删除");
        }

		// 检查是否有下级货架（未删除的）
		long shelfCount = warehouseShelfService.count(
				new QueryWrapper<WarehouseShelf>()
						.in("area_id", idList)
						.eq("del_flag", "0")
		);
		if (shelfCount > 0) {
			return Result.error("删除失败：选中的区域下存在货架，请先删除货架");
		}

		this.warehouseAreaService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "仓库区域-通过id查询")
	@Operation(summary="仓库区域-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WarehouseArea> queryById(@RequestParam(name="id",required=true) String id) {
		WarehouseArea warehouseArea = warehouseAreaService.getById(id);
		if(warehouseArea==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(warehouseArea);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param warehouseArea
    */
    @RequiresPermissions("wms:mis_warehouse_area:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WarehouseArea warehouseArea) {
        return super.exportXls(request, warehouseArea, WarehouseArea.class, "仓库区域");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wms:mis_warehouse_area:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WarehouseArea.class);
    }

}

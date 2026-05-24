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
import org.jeecg.modules.wms.entity.WarehouseShelf;
import org.jeecg.modules.wms.service.IWarehouseLocationService;
import org.jeecg.modules.wms.service.IWarehouseShelfService;

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
 * @Description: 货架信息
 * @Author: jeecg-boot
 * @Date:   2026-04-05
 * @Version: V1.0
 */
@Tag(name="货架信息")
@RestController
@RequestMapping("/wms/warehouseShelf")
@Slf4j
public class WarehouseShelfController extends JeecgController<WarehouseShelf, IWarehouseShelfService> {
	@Autowired
	private IWarehouseShelfService warehouseShelfService;
	@Autowired
	private IWarehouseLocationService warehouseLocationService;
	/**
	 * 分页列表查询
	 *
	 * @param warehouseShelf
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "货架信息-分页列表查询")
	@Operation(summary="货架信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WarehouseShelf>> queryPageList(WarehouseShelf warehouseShelf,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("warehouseId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("areaId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<WarehouseShelf> queryWrapper = QueryGenerator.initQueryWrapper(warehouseShelf, req.getParameterMap(),customeRuleMap);
		Page<WarehouseShelf> page = new Page<WarehouseShelf>(pageNo, pageSize);
		IPage<WarehouseShelf> pageList = warehouseShelfService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param warehouseShelf
	 * @return
	 */
	@AutoLog(value = "货架信息-添加")
	@Operation(summary="货架信息-添加")
	@RequiresPermissions("wms:mis_warehouse_shelf:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WarehouseShelf warehouseShelf) {
		// 校验货架编码唯一性
		Result<String> checkResult = checkShelfCodeUnique(warehouseShelf);
		if (!checkResult.isSuccess()) {
			return checkResult;
		}
		warehouseShelfService.save(warehouseShelf);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param warehouseShelf
	 * @return
	 */
	@AutoLog(value = "货架信息-编辑")
	@Operation(summary="货架信息-编辑")
	@RequiresPermissions("wms:mis_warehouse_shelf:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WarehouseShelf warehouseShelf) {
		if (oConvertUtils.isEmpty(warehouseShelf.getId())) {
			return Result.error("编辑失败：缺少主键ID");
		}
		// 校验货架编码唯一性（排除自身）
		Result<String> checkResult = checkShelfCodeUnique(warehouseShelf);
		if (!checkResult.isSuccess()) {
			return checkResult;
		}
		warehouseShelfService.updateById(warehouseShelf);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "货架信息-通过id删除")
	@Operation(summary="货架信息-通过id删除")
	@RequiresPermissions("wms:mis_warehouse_shelf:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		WarehouseShelf shelf = warehouseShelfService.getById(id);
		if (shelf == null) {
			return Result.error("未找到对应数据");
		}

		// 检查是否有下级货位（未删除的）
		long locCount = warehouseLocationService.count(
				new QueryWrapper<WarehouseLocation>()
						.eq("shelf_id", id)
						.eq("del_flag", "0")
		);
		if (locCount > 0) {
			return Result.error("删除失败：该货架下存在" + locCount + "个货位，请先删除货位");
		}

		warehouseShelfService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "货架信息-批量删除")
	@Operation(summary="货架信息-批量删除")
	@RequiresPermissions("wms:mis_warehouse_shelf:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> idList = Arrays.asList(ids.split(","));

		// 检查是否有下级货位（未删除的）
		long locCount = warehouseLocationService.count(
				new QueryWrapper<WarehouseLocation>()
						.in("shelf_id", idList)
						.eq("del_flag", "0")
		);
		if (locCount > 0) {
			return Result.error("删除失败：选中的货架下存在货位，请先删除货位");
		}
		this.warehouseShelfService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "货架信息-通过id查询")
	@Operation(summary="货架信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WarehouseShelf> queryById(@RequestParam(name="id",required=true) String id) {
		WarehouseShelf warehouseShelf = warehouseShelfService.getById(id);
		if(warehouseShelf==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(warehouseShelf);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param warehouseShelf
    */
    @RequiresPermissions("wms:mis_warehouse_shelf:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WarehouseShelf warehouseShelf) {
        return super.exportXls(request, warehouseShelf, WarehouseShelf.class, "货架信息");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wms:mis_warehouse_shelf:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WarehouseShelf.class);
    }


	 /**
	  * 校验货架编码唯一性（同一仓库内内一区域下不允许重复）
	  * 添加时：全库校验；编辑时：排除当前记录
	  */
	 private Result<String> checkShelfCodeUnique(WarehouseShelf warehouseShelf) {
		 if (oConvertUtils.isEmpty(warehouseShelf.getAreaId())) {
			 return Result.error("所属区域编不能为空");
		 }
		 if (oConvertUtils.isEmpty(warehouseShelf.getWarehouseId())) {
			 return Result.error("所属仓库不能为空");
		 }
		 if(oConvertUtils.isEmpty(warehouseShelf.getShelfCode())) {
			 return Result.error("货架编码不能为空");
		 }

		 QueryWrapper<WarehouseShelf> queryWrapper = new QueryWrapper<>();
		 queryWrapper.eq("warehouse_id", warehouseShelf.getWarehouseId());
		 queryWrapper.eq("area_id", warehouseShelf.getAreaId());
		 queryWrapper.eq("shelf_code", warehouseShelf.getShelfCode());

		 // 编辑时排除当前记录
		 if (oConvertUtils.isNotEmpty(warehouseShelf.getId())) {
			 queryWrapper.ne("id", warehouseShelf.getId());
		 }

		 long count = warehouseShelfService.count(queryWrapper);
		 if (count > 0) {
			 return Result.error("同一仓库内区域编码【" + warehouseShelf.getShelfCode() + "】已存在，请勿重复添加");
		 }

		 return Result.OK();
	 }
}

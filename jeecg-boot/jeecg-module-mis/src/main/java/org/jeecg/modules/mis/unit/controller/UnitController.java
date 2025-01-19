package org.jeecg.modules.mis.unit.controller;

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
import org.jeecg.modules.mis.unit.entity.Unit;
import org.jeecg.modules.mis.unit.service.IUnitService;

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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 计量单位
 * @Author: jeecg-boot
 * @Date:   2024-11-16
 * @Version: V1.0
 */
@Api(tags="计量单位")
@RestController
@RequestMapping("/unit/unit")
@Slf4j
public class UnitController extends JeecgController<Unit, IUnitService> {
	@Autowired
	private IUnitService unitService;
	
	/**
	 * 分页列表查询
	 *
	 * @param unit
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "计量单位-分页列表查询")
	@ApiOperation(value="计量单位-分页列表查询", notes="计量单位-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Unit>> queryPageList(Unit unit,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<Unit> queryWrapper = QueryGenerator.initQueryWrapper(unit, req.getParameterMap(),customeRuleMap);
		Page<Unit> page = new Page<Unit>(pageNo, pageSize);
		IPage<Unit> pageList = unitService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param unit
	 * @return
	 */
	@AutoLog(value = "计量单位-添加")
	@ApiOperation(value="计量单位-添加", notes="计量单位-添加")
	@RequiresPermissions("unit:mis_unit:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Unit unit) {
		unitService.save(unit);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param unit
	 * @return
	 */
	@AutoLog(value = "计量单位-编辑")
	@ApiOperation(value="计量单位-编辑", notes="计量单位-编辑")
	@RequiresPermissions("unit:mis_unit:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Unit unit) {
		unitService.updateById(unit);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "计量单位-通过id删除")
	@ApiOperation(value="计量单位-通过id删除", notes="计量单位-通过id删除")
	@RequiresPermissions("unit:mis_unit:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		unitService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "计量单位-批量删除")
	@ApiOperation(value="计量单位-批量删除", notes="计量单位-批量删除")
	@RequiresPermissions("unit:mis_unit:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.unitService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "计量单位-通过id查询")
	@ApiOperation(value="计量单位-通过id查询", notes="计量单位-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Unit> queryById(@RequestParam(name="id",required=true) String id) {
		Unit unit = unitService.getById(id);
		if(unit==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(unit);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param unit
    */
    @RequiresPermissions("unit:mis_unit:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Unit unit) {
        return super.exportXls(request, unit, Unit.class, "计量单位");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("unit:mis_unit:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Unit.class);
    }

}

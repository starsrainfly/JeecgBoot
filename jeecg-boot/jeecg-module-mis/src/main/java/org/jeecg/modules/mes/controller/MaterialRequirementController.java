package org.jeecg.modules.mes.controller;

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
import org.jeecg.modules.mes.entity.MaterialRequirement;
import org.jeecg.modules.mes.service.IMaterialRequirementService;

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
 * @Description: 物料需求表
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Tag(name="物料需求表")
@RestController
@RequestMapping("/mes/materialRequirement")
@Slf4j
public class MaterialRequirementController extends JeecgController<MaterialRequirement, IMaterialRequirementService> {
	@Autowired
	private IMaterialRequirementService materialRequirementService;
	
	/**
	 * 分页列表查询
	 *
	 * @param materialRequirement
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "物料需求表-分页列表查询")
	@Operation(summary="物料需求表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<MaterialRequirement>> queryPageList(MaterialRequirement materialRequirement,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<MaterialRequirement> queryWrapper = QueryGenerator.initQueryWrapper(materialRequirement, req.getParameterMap(),customeRuleMap);
		Page<MaterialRequirement> page = new Page<MaterialRequirement>(pageNo, pageSize);
		IPage<MaterialRequirement> pageList = materialRequirementService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param materialRequirement
	 * @return
	 */
	@AutoLog(value = "物料需求表-添加")
	@Operation(summary="物料需求表-添加")
	@RequiresPermissions("mes:mis_material_requirement:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody MaterialRequirement materialRequirement) {
		materialRequirementService.save(materialRequirement);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param materialRequirement
	 * @return
	 */
	@AutoLog(value = "物料需求表-编辑")
	@Operation(summary="物料需求表-编辑")
	@RequiresPermissions("mes:mis_material_requirement:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody MaterialRequirement materialRequirement) {
		materialRequirementService.updateById(materialRequirement);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物料需求表-通过id删除")
	@Operation(summary="物料需求表-通过id删除")
	@RequiresPermissions("mes:mis_material_requirement:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		materialRequirementService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "物料需求表-批量删除")
	@Operation(summary="物料需求表-批量删除")
	@RequiresPermissions("mes:mis_material_requirement:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.materialRequirementService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "物料需求表-通过id查询")
	@Operation(summary="物料需求表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<MaterialRequirement> queryById(@RequestParam(name="id",required=true) String id) {
		MaterialRequirement materialRequirement = materialRequirementService.getById(id);
		if(materialRequirement==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(materialRequirement);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param materialRequirement
    */
    @RequiresPermissions("mes:mis_material_requirement:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, MaterialRequirement materialRequirement) {
        return super.exportXls(request, materialRequirement, MaterialRequirement.class, "物料需求表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mes:mis_material_requirement:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, MaterialRequirement.class);
    }

}

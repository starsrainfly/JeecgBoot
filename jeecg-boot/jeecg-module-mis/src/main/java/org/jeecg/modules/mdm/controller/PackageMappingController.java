package org.jeecg.modules.mdm.controller;

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
import org.jeecg.modules.mdm.entity.PackageMapping;
import org.jeecg.modules.mdm.service.IPackageMappingService;

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
 * @Description: 内外包装映射表
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
@Tag(name="内外包装映射表")
@RestController
@RequestMapping("/mdm/packageMapping")
@Slf4j
public class PackageMappingController extends JeecgController<PackageMapping, IPackageMappingService> {
	@Autowired
	private IPackageMappingService packageMappingService;
	
	/**
	 * 分页列表查询
	 *
	 * @param packageMapping
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "内外包装映射表-分页列表查询")
	@Operation(summary="内外包装映射表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PackageMapping>> queryPageList(PackageMapping packageMapping,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("innerPackageId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("outerPackageId", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<PackageMapping> queryWrapper = QueryGenerator.initQueryWrapper(packageMapping, req.getParameterMap(),customeRuleMap);
		Page<PackageMapping> page = new Page<PackageMapping>(pageNo, pageSize);
		IPage<PackageMapping> pageList = packageMappingService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param packageMapping
	 * @return
	 */
	@AutoLog(value = "内外包装映射表-添加")
	@Operation(summary="内外包装映射表-添加")
	@RequiresPermissions("mdm:mis_package_mapping:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PackageMapping packageMapping) {
		packageMappingService.save(packageMapping);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param packageMapping
	 * @return
	 */
	@AutoLog(value = "内外包装映射表-编辑")
	@Operation(summary="内外包装映射表-编辑")
	@RequiresPermissions("mdm:mis_package_mapping:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PackageMapping packageMapping) {
		packageMappingService.updateById(packageMapping);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "内外包装映射表-通过id删除")
	@Operation(summary="内外包装映射表-通过id删除")
	@RequiresPermissions("mdm:mis_package_mapping:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		packageMappingService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "内外包装映射表-批量删除")
	@Operation(summary="内外包装映射表-批量删除")
	@RequiresPermissions("mdm:mis_package_mapping:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.packageMappingService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "内外包装映射表-通过id查询")
	@Operation(summary="内外包装映射表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PackageMapping> queryById(@RequestParam(name="id",required=true) String id) {
		PackageMapping packageMapping = packageMappingService.getById(id);
		if(packageMapping==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(packageMapping);
	}

	 /**
	  * 通过内外包的id获得内外包的对应关系
	  * @param innerPackageId
	  * @param outerPackageId
	  * @return
	  */
	 //@AutoLog(value = "内外包装映射表-通过id查询")
	 @Operation(summary="内外包装映射表-通过内外包装id查询")
	 @GetMapping("/getByInnerAndOuter")
	public Result<PackageMapping> getByInnerAndOuter(@RequestParam String innerPackageId, @RequestParam String outerPackageId) {
		PackageMapping packageMapping = packageMappingService.getByInnerAndOuter(innerPackageId, outerPackageId);
		if(packageMapping==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(packageMapping);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param packageMapping
    */
    @RequiresPermissions("mdm:mis_package_mapping:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PackageMapping packageMapping) {
        return super.exportXls(request, packageMapping, PackageMapping.class, "内外包装映射表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mdm:mis_package_mapping:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PackageMapping.class);
    }

}

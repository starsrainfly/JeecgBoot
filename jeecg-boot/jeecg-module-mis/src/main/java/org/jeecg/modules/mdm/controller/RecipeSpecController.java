package org.jeecg.modules.mdm.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.mdm.entity.RecipeSpec;
import org.jeecg.modules.mdm.service.IRecipeSpecService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: mis_recipe_spec
 * @Author: jeecg-boot
 * @Date:   2026-01-13
 * @Version: V1.0
 */
@Tag(name="mis_recipe_spec")
@RestController
@RequestMapping("/recipe/recipeSpec")
@Slf4j
public class RecipeSpecController extends JeecgController<RecipeSpec, IRecipeSpecService> {
	@Autowired
	private IRecipeSpecService recipeSpecService;
	
	/**
	 * 分页列表查询
	 *
	 * @param recipeSpec
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "mis_recipe_spec-分页列表查询")
	@Operation(summary="mis_recipe_spec-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RecipeSpec>> queryPageList(RecipeSpec recipeSpec,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<RecipeSpec> queryWrapper = QueryGenerator.initQueryWrapper(recipeSpec, req.getParameterMap());
		Page<RecipeSpec> page = new Page<RecipeSpec>(pageNo, pageSize);
		IPage<RecipeSpec> pageList = recipeSpecService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param recipeSpec
	 * @return
	 */
	@AutoLog(value = "mis_recipe_spec-添加")
	@Operation(summary="mis_recipe_spec-添加")
	@RequiresPermissions("recipe:mis_recipe_spec:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RecipeSpec recipeSpec) {
		recipeSpecService.save(recipeSpec);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param recipeSpec
	 * @return
	 */
	@AutoLog(value = "mis_recipe_spec-编辑")
	@Operation(summary="mis_recipe_spec-编辑")
	@RequiresPermissions("recipe:mis_recipe_spec:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RecipeSpec recipeSpec) {
		recipeSpecService.updateById(recipeSpec);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "mis_recipe_spec-通过id删除")
	@Operation(summary="mis_recipe_spec-通过id删除")
	@RequiresPermissions("recipe:mis_recipe_spec:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		recipeSpecService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "mis_recipe_spec-批量删除")
	@Operation(summary="mis_recipe_spec-批量删除")
	@RequiresPermissions("recipe:mis_recipe_spec:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.recipeSpecService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "mis_recipe_spec-通过id查询")
	@Operation(summary="mis_recipe_spec-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RecipeSpec> queryById(@RequestParam(name="id",required=true) String id) {
		RecipeSpec recipeSpec = recipeSpecService.getById(id);
		if(recipeSpec==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(recipeSpec);
	}
	 @Operation(summary="mis_recipe_spec-通过RecipeId查询")
	 @GetMapping(value = "/getByRecipeId")
	public Result<RecipeSpec> getByRecipeId(@RequestParam(name="recipeId",required=true)String recipeId) {
		 RecipeSpec recipeSpec = recipeSpecService.getByRecipeId(recipeId);
//		 if(recipeSpec==null) {
//			 return Result.error("未找到对应数据");
//		 }
		 return Result.OK(recipeSpec);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param recipeSpec
    */
    @RequiresPermissions("recipe:mis_recipe_spec:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RecipeSpec recipeSpec) {
        return super.exportXls(request, recipeSpec, RecipeSpec.class, "mis_recipe_spec");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("recipe:mis_recipe_spec:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, RecipeSpec.class);
    }

}

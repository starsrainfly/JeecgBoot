package org.jeecg.modules.mdm.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.mdm.entity.Item;
import org.jeecg.modules.mdm.service.IItemService;

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
 * @Description: 统一库存项表
 * @Author: jeecg-boot
 * @Date:   2026-01-15
 * @Version: V1.0
 */
@Tag(name="统一库存项表")
@RestController
@RequestMapping("/masterdata/item")
@Slf4j
public class ItemController extends JeecgController<Item, IItemService> {
	@Autowired
	private IItemService itemService;
	
	/**
	 * 分页列表查询
	 *
	 * @param item
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "统一库存项表-分页列表查询")
	@Operation(summary="统一库存项表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Item>> queryPageList(Item item,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("itemType", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<Item> queryWrapper = QueryGenerator.initQueryWrapper(item, req.getParameterMap(),customeRuleMap);
		Page<Item> page = new Page<Item>(pageNo, pageSize);
		IPage<Item> pageList = itemService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param item
	 * @return
	 */
	@AutoLog(value = "统一库存项表-添加")
	@Operation(summary="统一库存项表-添加")
	//@RequiresPermissions("masterdata:mis_item:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Item item) {
		itemService.save(item);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param item
	 * @return
	 */
	@AutoLog(value = "统一库存项表-编辑")
	@Operation(summary="统一库存项表-编辑")
	//@RequiresPermissions("masterdata:mis_item:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Item item) {
		itemService.updateById(item);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "统一库存项表-通过id删除")
	@Operation(summary="统一库存项表-通过id删除")
	//@RequiresPermissions("masterdata:mis_item:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		itemService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "统一库存项表-批量删除")
	@Operation(summary="统一库存项表-批量删除")
	//@RequiresPermissions("masterdata:mis_item:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.itemService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "统一库存项表-通过id查询")
	@Operation(summary="统一库存项表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Item> queryById(@RequestParam(name="id",required=true) String id) {
		Item item = itemService.getById(id);
		if(item==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(item);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param item
    */
    @RequiresPermissions("masterdata:mis_item:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Item item) {
        return super.exportXls(request, item, Item.class, "统一库存项表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("masterdata:mis_item:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Item.class);
    }

}

package org.jeecg.modules.scm.controller;

import java.math.BigDecimal;
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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.zhyd.oauth.utils.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.scm.entity.PricingStrategy;
import org.jeecg.modules.scm.service.IPricingStrategyService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.scm.vo.PricingStrategyVo;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
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
 * @Description: 价格策略
 * @Author: jeecg-boot
 * @Date:   2026-02-01
 * @Version: V1.0
 */
@Tag(name="价格策略")
@RestController
@RequestMapping("/scm/pricingStrategy")
@Slf4j
public class PricingStrategyController extends JeecgController<PricingStrategy, IPricingStrategyService> {
	@Autowired
	private IPricingStrategyService pricingStrategyService;

	 @Autowired
	 private ISysUserService sysUserService;
	/**
	 * 分页列表查询
	 *
	 * @param pricingStrategy
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "价格策略-分页列表查询")
	@Operation(summary="价格策略-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PricingStrategy>> queryPageList(PricingStrategy pricingStrategy,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("salesmanUserId", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<PricingStrategy> queryWrapper = QueryGenerator.initQueryWrapper(pricingStrategy, req.getParameterMap(),customeRuleMap);
		Page<PricingStrategy> page = new Page<PricingStrategy>(pageNo, pageSize);
		IPage<PricingStrategy> pageList = pricingStrategyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 分页列表查询
	  *
	  * @param pricingStrategy
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "价格策略-分页列表查询")
	 @Operation(summary="价格策略-分页列表查询")
	 @GetMapping(value = "/list_new")
	 public Result<IPage<PricingStrategyVo>> queryPageList_new(PricingStrategy pricingStrategy,
														 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														 HttpServletRequest req) {
		 // 自定义查询规则
		 Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
		 // 自定义多选的查询规则为：LIKE_WITH_OR
		 customeRuleMap.put("salesmanUserId", QueryRuleEnum.LIKE_WITH_OR);
		 QueryWrapper<PricingStrategy> queryWrapper = QueryGenerator.initQueryWrapper(pricingStrategy, req.getParameterMap(),customeRuleMap);
		 Page<PricingStrategy> page = new Page<PricingStrategy>(pageNo, pageSize);
		// IPage<PricingStrategy> pageList = pricingStrategyService.page(page, queryWrapper);
		 IPage<PricingStrategyVo> pageList = pricingStrategyService.queryPageList(pricingStrategy, page);
		 return Result.OK(pageList);
	 }

	 /**
	  * 分页列表查询
	  *
	  * @param pricingStrategy
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "价格策略选择-分页列表查询")
	 @Operation(summary="价格策略选择-分页列表查询")
	 @GetMapping(value = "/priceSelectList")
	 public  Result<IPage<PricingStrategyVo>>queryPageBestPricingStrategies(PricingStrategy pricingStrategy,
																			@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
																			@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
																			HttpServletRequest req){
		 Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
		 // 自定义多选的查询规则为：LIKE_WITH_OR
		 customeRuleMap.put("salesmanUserId", QueryRuleEnum.LIKE_WITH_OR);
		 QueryWrapper<PricingStrategy> queryWrapper = QueryGenerator.initQueryWrapper(pricingStrategy, req.getParameterMap(),customeRuleMap);
		 Page<PricingStrategy> page = new Page<PricingStrategy>(pageNo, pageSize);
		 // IPage<PricingStrategy> pageList = pricingStrategyService.page(page, queryWrapper);
		 IPage<PricingStrategyVo> pageList = pricingStrategyService.selectBestPricingStrategies(pricingStrategy, page);
		 return Result.OK(pageList);
	 }
	
	/**
	 *   添加
	 *
	 * @param pricingStrategy
	 * @return
	 */
	@AutoLog(value = "价格策略-添加")
	@Operation(summary="价格策略-添加")
	@RequiresPermissions("scm:mis_pricing_strategy:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PricingStrategy pricingStrategy) {
		if(!StringUtils.isEmpty(pricingStrategy.getSalesmanId())){
			SysUser systemUser = sysUserService.getById(pricingStrategy.getSalesmanId());
			pricingStrategy.setSalesmanName(systemUser.getRealname());
		}
		pricingStrategyService.save(pricingStrategy);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param pricingStrategy
	 * @return
	 */
	@AutoLog(value = "价格策略-编辑")
	@Operation(summary="价格策略-编辑")
	@RequiresPermissions("scm:mis_pricing_strategy:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PricingStrategy pricingStrategy) {
		if(!StringUtils.isEmpty(pricingStrategy.getSalesmanId())){
			SysUser systemUser = sysUserService.getById(pricingStrategy.getSalesmanId());
			pricingStrategy.setSalesmanName(systemUser.getRealname());
		}
		else{
			pricingStrategy.setSalesmanName("");
		}
		pricingStrategyService.updateById(pricingStrategy);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "价格策略-通过id删除")
	@Operation(summary="价格策略-通过id删除")
	@RequiresPermissions("scm:mis_pricing_strategy:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		pricingStrategyService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "价格策略-批量删除")
	@Operation(summary="价格策略-批量删除")
	@RequiresPermissions("scm:mis_pricing_strategy:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.pricingStrategyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "价格策略-通过id查询")
	@Operation(summary="价格策略-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PricingStrategy> queryById(@RequestParam(name="id",required=true) String id) {
		PricingStrategy pricingStrategy = pricingStrategyService.getById(id);
		if(pricingStrategy==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(pricingStrategy);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param pricingStrategy
    */
    @RequiresPermissions("scm:mis_pricing_strategy:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PricingStrategy pricingStrategy) {
        return super.exportXls(request, pricingStrategy, PricingStrategy.class, "价格策略");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("scm:mis_pricing_strategy:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PricingStrategy.class);
    }


	 /**
	  * 匹配价格策略（供销售订单使用）
	  *
	  * @param customerId        客户ID（可选）
	  * @param salesmanUserId    业务员用户ID（可选）
	  * @param itemId            物料ID（必填）
	  * @return PricingStrategy 实体（含价格、最小起订量等所有字段）
	  */
	 @GetMapping("/matchPrice")
	 public Result<PricingStrategy> matchPrice(
			 @RequestParam(required = false) String customerId,
			 @RequestParam(required = false) String salesmanUserId,
			 @RequestParam String itemId,
			 @RequestParam String packageId) {

		 PricingStrategy matched = pricingStrategyService.matchPrice(
				 customerId, salesmanUserId, itemId, packageId);

		 if (matched == null) {
			 return Result.error("未找到有效的价格策略");
		 }

		 return Result.OK(matched); // 直接返回实体
	 }
}

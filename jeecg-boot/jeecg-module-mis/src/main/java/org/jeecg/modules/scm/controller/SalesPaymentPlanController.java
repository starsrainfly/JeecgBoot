package org.jeecg.modules.scm.controller;

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
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.scm.entity.SalesPaymentPlan;
import org.jeecg.modules.scm.service.ISalesPaymentPlanService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.scm.vo.SalesPaymentPlanVo;
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
 * @Description: 销售收款计划
 * @Author: jeecg-boot
 * @Date:   2026-04-22
 * @Version: V1.0
 */
@Tag(name="销售收款计划")
@RestController
@RequestMapping("/scm/salesPaymentPlan")
@Slf4j
public class SalesPaymentPlanController extends JeecgController<SalesPaymentPlan, ISalesPaymentPlanService> {
	@Autowired
	private ISalesPaymentPlanService salesPaymentPlanService;
	@Autowired
	private ISerialNoService serialNoService;
	/**
	 * 分页列表查询
	 *
	 * @param salesPaymentPlan
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "销售收款计划-分页列表查询")
	@Operation(summary="销售收款计划-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SalesPaymentPlan>> queryPageList(SalesPaymentPlan salesPaymentPlan,
														 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														 HttpServletRequest req) {
		// 自定义查询规则
		Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
		// 自定义多选的查询规则为：LIKE_WITH_OR
		customeRuleMap.put("paymentMethod", QueryRuleEnum.LIKE_WITH_OR);
		customeRuleMap.put("planStatus", QueryRuleEnum.LIKE_WITH_OR);
		QueryWrapper<SalesPaymentPlan> queryWrapper = QueryGenerator.initQueryWrapper(salesPaymentPlan, req.getParameterMap(),customeRuleMap);
		Page<SalesPaymentPlan> page = new Page<SalesPaymentPlan>(pageNo, pageSize);
		IPage<SalesPaymentPlan> pageList = salesPaymentPlanService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
//	public Result<IPage<SalesPaymentPlanVo>> queryPageList(SalesPaymentPlanVo salesPaymentPlanVo,
//														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
//														   HttpServletRequest req) {
//        // 自定义查询规则
////        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
////        // 自定义多选的查询规则为：LIKE_WITH_OR
////        customeRuleMap.put("paymentMethod", QueryRuleEnum.LIKE_WITH_OR);
////        customeRuleMap.put("planStatus", QueryRuleEnum.LIKE_WITH_OR);
////        QueryWrapper<SalesPaymentPlanVo> queryWrapper = QueryGenerator.initQueryWrapper(salesPaymentPlanVo, req.getParameterMap(),customeRuleMap);
//		Page<SalesPaymentPlanVo> page = new Page<SalesPaymentPlanVo>(pageNo, pageSize);
//		IPage<SalesPaymentPlanVo> pageList = salesPaymentPlanService.queryPageList(page, salesPaymentPlanVo);
//		return Result.OK(pageList);
//	}
	
	/**
	 *   添加
	 *
	 * @param salesPaymentPlan
	 * @return
	 */
	@AutoLog(value = "销售收款计划-添加")
	@Operation(summary="销售收款计划-添加")
	@RequiresPermissions("scm:mis_sales_payment_plan:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SalesPaymentPlan salesPaymentPlan) {
		String planNo = serialNoService.generateSerialNo(SerialNoPrefixEnum.RECEIVABLE_PLAN.getPrefix());
		salesPaymentPlan.setPlanNo(planNo);
		salesPaymentPlanService.save(salesPaymentPlan);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param salesPaymentPlan
	 * @return
	 */
	@AutoLog(value = "销售收款计划-编辑")
	@Operation(summary="销售收款计划-编辑")
	@RequiresPermissions("scm:mis_sales_payment_plan:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SalesPaymentPlan salesPaymentPlan) {
		salesPaymentPlanService.updateById(salesPaymentPlan);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "销售收款计划-通过id删除")
	@Operation(summary="销售收款计划-通过id删除")
	@RequiresPermissions("scm:mis_sales_payment_plan:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		salesPaymentPlanService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "销售收款计划-批量删除")
	@Operation(summary="销售收款计划-批量删除")
	@RequiresPermissions("scm:mis_sales_payment_plan:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.salesPaymentPlanService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "销售收款计划-通过id查询")
	@Operation(summary="销售收款计划-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SalesPaymentPlan> queryById(@RequestParam(name="id",required=true) String id) {
		SalesPaymentPlan salesPaymentPlan = salesPaymentPlanService.getById(id);
		if(salesPaymentPlan==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(salesPaymentPlan);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param salesPaymentPlan
    */
    @RequiresPermissions("scm:mis_sales_payment_plan:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SalesPaymentPlan salesPaymentPlan) {
        return super.exportXls(request, salesPaymentPlan, SalesPaymentPlan.class, "销售收款计划");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("scm:mis_sales_payment_plan:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SalesPaymentPlan.class);
    }

}

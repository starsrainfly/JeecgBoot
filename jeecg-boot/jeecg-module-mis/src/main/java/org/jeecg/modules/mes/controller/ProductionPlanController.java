package org.jeecg.modules.mes.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.common.utils.SerialNoUtils;
import org.jeecg.modules.mes.vo.ProductionPlanDetailVo;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mes.entity.ProductionPlanDetail;
import org.jeecg.modules.mes.entity.ProductionPlan;
import org.jeecg.modules.mes.vo.ProductionPlanPage;
import org.jeecg.modules.mes.service.IProductionPlanService;
import org.jeecg.modules.mes.service.IProductionPlanDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;


 /**
 * @Description: 生产计划
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
@Tag(name="生产计划")
@RestController
@RequestMapping("/mes/productionPlan")
@Slf4j
public class ProductionPlanController {
	@Autowired
	private IProductionPlanService productionPlanService;
	@Autowired
	private IProductionPlanDetailService productionPlanDetailService;
	 @Autowired
	 private ISerialNoService serialNoService;
	 @Autowired
	 private ISysDepartService departService;
	/**
	 * 分页列表查询
	 *
	 * @param productionPlan
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "生产计划-分页列表查询")
	@Operation(summary="生产计划-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ProductionPlan>> queryPageList(ProductionPlan productionPlan,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("planType", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<ProductionPlan> queryWrapper = QueryGenerator.initQueryWrapper(productionPlan, req.getParameterMap(),customeRuleMap);
		Page<ProductionPlan> page = new Page<ProductionPlan>(pageNo, pageSize);
		IPage<ProductionPlan> pageList = productionPlanService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 查询可用计划明细（用于生成生产订单）
	  * 条件：已发布 + 未全部分配
	  */
	 //@AutoLog(value = "生产计划-分页列表查询")
	 @Operation(summary="生产计划-查询可用计划明细")
	 @GetMapping("/listForOrder")
	 public Result<IPage<ProductionPlanDetailVo>> listForOrder(
			 @RequestParam(required = false) String planNo,
			 @RequestParam(required = false) String productCode,
			 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		 Page<ProductionPlanDetailVo> page = new Page<>(pageNo, pageSize);
		 IPage<ProductionPlanDetailVo> result = productionPlanDetailService.listAvailableForOrder(page, planNo, productCode);

		 return Result.OK(result);
	 }
	/**
	 *   添加
	 *
	 * @param productionPlanPage
	 * @return
	 */
	@AutoLog(value = "生产计划-添加")
	@Operation(summary="生产计划-添加")
    @RequiresPermissions("mes:mis_production_plan:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProductionPlanPage productionPlanPage) {
		ProductionPlan productionPlan = new ProductionPlan();
		BeanUtils.copyProperties(productionPlanPage, productionPlan);
		productionPlan.setPlanNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.PRODUCTION_PLAN.getPrefix()));
		if(productionPlan.getCompanyId() != null){
			SysDepart depart =  departService.getDepartById(productionPlan.getCompanyId());
			if(depart != null){
				productionPlan.setCompanyName(depart.getDepartName());
			}
		}
		productionPlanService.saveMain(productionPlan, productionPlanPage.getProductionPlanDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param productionPlanPage
	 * @return
	 */
	@AutoLog(value = "生产计划-编辑")
	@Operation(summary="生产计划-编辑")
    @RequiresPermissions("mes:mis_production_plan:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProductionPlanPage productionPlanPage) {
		ProductionPlan productionPlan = new ProductionPlan();
		BeanUtils.copyProperties(productionPlanPage, productionPlan);
		ProductionPlan productionPlanEntity = productionPlanService.getById(productionPlan.getId());
		if(productionPlanEntity==null) {
			return Result.error("未找到对应数据");
		}
		if(productionPlan.getCompanyId() != null){
			SysDepart depart =  departService.getDepartById(productionPlan.getCompanyId());
			if(depart != null){
				productionPlan.setCompanyName(depart.getDepartName());
			}
		}

		productionPlanService.updateMain(productionPlan, productionPlanPage.getProductionPlanDetailList());
		return Result.OK("编辑成功!");
	}
	
		 /**
	  * 发布计划
	  * @param planId
	  * @return
	  */
	 @AutoLog(value = "生产计划-发布计划")
	 @Operation(summary="生产计划-发布计划")
	 @RequiresPermissions("mes:mis_production_plan:publish")
	 @RequestMapping(value = "/publish")
	public Result<String> publishPlan(@RequestParam(name="planId",required=true) String planId) {

		ProductionPlan productionPlanEntity = productionPlanService.getById(planId);
		if(productionPlanEntity==null) {
			return Result.error("未找到对应数据");
		}
		productionPlanService.publishPlan(planId);
		return Result.OK("发布计划成功");
	}

	 /**
	  * 批量发布计划
	  * @param planIds
	  * @return
	  */
	 @AutoLog(value = "生产计划-批量发布计划")
	 @Operation(summary="生产计划-批量发布计划")
	 @RequiresPermissions("mes:mis_production_plan:publishBatch")
	 @RequestMapping(value = "/publishBatch")
	public  Result<String> publishPlanBatch(@RequestParam(name="planIds", required = true) String planIds) {

		 productionPlanService.publishPlanBatch(Arrays.asList(planIds.split(",")));
		return Result.OK("发布计划成功");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "生产计划-通过id删除")
	@Operation(summary="生产计划-通过id删除")
    @RequiresPermissions("mes:mis_production_plan:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		productionPlanService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "生产计划-批量删除")
	@Operation(summary="生产计划-批量删除")
    @RequiresPermissions("mes:mis_production_plan:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.productionPlanService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产计划-通过id查询")
	@Operation(summary="生产计划-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ProductionPlan> queryById(@RequestParam(name="id",required=true) String id) {
		ProductionPlan productionPlan = productionPlanService.getById(id);
		if(productionPlan==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(productionPlan);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产计划明细表通过主表ID查询")
	@Operation(summary="生产计划明细表主表ID查询")
	@GetMapping(value = "/queryProductionPlanDetailByMainId")
	public Result<List<ProductionPlanDetail>> queryProductionPlanDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<ProductionPlanDetail> productionPlanDetailList = productionPlanDetailService.selectByMainId(id);
		return Result.OK(productionPlanDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param productionPlan
    */
    @RequiresPermissions("mes:mis_production_plan:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProductionPlan productionPlan) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<ProductionPlan> queryWrapper = QueryGenerator.initQueryWrapper(productionPlan, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<ProductionPlan> productionPlanList = productionPlanService.list(queryWrapper);

      // Step.3 组装pageList
      List<ProductionPlanPage> pageList = new ArrayList<ProductionPlanPage>();
      for (ProductionPlan main : productionPlanList) {
          ProductionPlanPage vo = new ProductionPlanPage();
          BeanUtils.copyProperties(main, vo);
          List<ProductionPlanDetail> productionPlanDetailList = productionPlanDetailService.selectByMainId(main.getId());
          vo.setProductionPlanDetailList(productionPlanDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "生产计划列表");
      mv.addObject(NormalExcelConstants.CLASS, ProductionPlanPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("生产计划数据", "导出人:"+sysUser.getRealname(), "生产计划"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mes:mis_production_plan:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // 获取上传文件对象
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<ProductionPlanPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ProductionPlanPage.class, params);
              for (ProductionPlanPage page : list) {
                  ProductionPlan po = new ProductionPlan();
                  BeanUtils.copyProperties(page, po);
                  productionPlanService.saveMain(po, page.getProductionPlanDetailList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.OK("文件导入失败！");
    }

}

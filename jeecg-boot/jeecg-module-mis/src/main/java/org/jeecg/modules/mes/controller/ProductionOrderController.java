package org.jeecg.modules.mes.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.common.utils.SerialNoUtils;
import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
import org.jeecg.modules.mdm.entity.Product;
import org.jeecg.modules.mdm.entity.Recipe;
import org.jeecg.modules.mdm.entity.RecipeDetail;
import org.jeecg.modules.mdm.service.IProcessRoutingStepService;
import org.jeecg.modules.mdm.service.IProductService;
import org.jeecg.modules.mdm.service.IRecipeDetailService;
import org.jeecg.modules.mdm.service.IRecipeService;
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
import org.jeecg.modules.mes.entity.ProductionOrderDetail;
import org.jeecg.modules.mes.entity.ProductionOrder;
import org.jeecg.modules.mes.vo.ProductionOrderPage;
import org.jeecg.modules.mes.service.IProductionOrderService;
import org.jeecg.modules.mes.service.IProductionOrderDetailService;
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
 * @Description: 生产订单
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
@Tag(name="生产订单")
@RestController
@RequestMapping("/mes/productionOrder")
@Slf4j
public class ProductionOrderController {
	@Autowired
	private IProductionOrderService productionOrderService;
	@Autowired
	private IProductionOrderDetailService productionOrderDetailService;

	 @Autowired
	 private ISerialNoService serialNoService;
	 @Autowired
	 private IProductService productService;
	 @Autowired
	 private ISysDepartService departService;
	 @Autowired
	 private IRecipeService recipeService;
	 @Autowired
	 private IRecipeDetailService recipeDetailService;
	 @Autowired
	 private IProcessRoutingStepService processRoutingStepService;
	/**
	 * 分页列表查询
	 *
	 * @param productionOrder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "生产订单-分页列表查询")
	@Operation(summary="生产订单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ProductionOrder>> queryPageList(ProductionOrder productionOrder,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("innerPackageId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("outerPackageId", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<ProductionOrder> queryWrapper = QueryGenerator.initQueryWrapper(productionOrder, req.getParameterMap(),customeRuleMap);
		Page<ProductionOrder> page = new Page<ProductionOrder>(pageNo, pageSize);
		IPage<ProductionOrder> pageList = productionOrderService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 @AutoLog(value = "生产订单-下达")
	 @Operation(summary="生产订单-下达")
	 @RequiresPermissions("mes:mis_production_order:release")
	 @PostMapping(value = "/release")
	public Result<String> release(@RequestParam String id){
		 ProductionOrder productionOrder = productionOrderService.getById(id);
		 if(productionOrder==null) {
			 return Result.error("未找到对应数据");
		 }
		productionOrderService.releaseOrder(id);
		return Result.OK("添加成功！");
	}

	 @AutoLog(value = "生产订单-批量下达")
	 @Operation(summary="生产订单-批量下达")
	 @RequiresPermissions("mes:mis_production_order:release")
	 @PostMapping(value = "/batchRelease")
	 public Result<String> batchRelease(@RequestParam String ids){

		 return Result.OK("添加成功！");
	 }

	/**
	 *   添加
	 *
	 * @param productionOrderPage
	 * @return
	 */
	@AutoLog(value = "生产订单-添加")
	@Operation(summary="生产订单-添加")
    @RequiresPermissions("mes:mis_production_order:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProductionOrderPage productionOrderPage) {
		ProductionOrder productionOrder = new ProductionOrder();
		BeanUtils.copyProperties(productionOrderPage, productionOrder);
		productionOrder.setOrderNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.PRODUCTION_ORDER.getPrefix()));
		//通过产品信息 获取配方信息，并更新订单表
		Product productEntity = productService.getById(productionOrder.getProductId());
		if(productEntity==null) {
			return Result.error("未找到对应产品数据");
		}
		if(productionOrder.getCompanyId() != null){
			SysDepart depart =  departService.getDepartById(productionOrder.getCompanyId());
			if(depart != null){
				productionOrder.setCompanyName(depart.getDepartName());
			}
		}

		productionOrder.setRecipeId(productEntity.getRecipeId());
		productionOrder.setRecipeCode(productEntity.getRecipeCode());
		productionOrder.setRecipeName(productEntity.getRecipeName());
		productionOrder.setRecipeVersion(productEntity.getRecipeVersion());

		productionOrderService.saveMain(productionOrder, productionOrderPage.getProductionOrderDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param productionOrderPage
	 * @return
	 */
	@AutoLog(value = "生产订单-编辑")
	@Operation(summary="生产订单-编辑")
    @RequiresPermissions("mes:mis_production_order:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProductionOrderPage productionOrderPage) {
		ProductionOrder productionOrder = new ProductionOrder();
		BeanUtils.copyProperties(productionOrderPage, productionOrder);
		ProductionOrder productionOrderEntity = productionOrderService.getById(productionOrder.getId());
		if(productionOrderEntity==null) {
			return Result.error("未找到对应数据");
		}
		if(productionOrder.getCompanyId() != null){
			SysDepart depart =  departService.getDepartById(productionOrder.getCompanyId());
			if(depart != null){
				productionOrder.setCompanyName(depart.getDepartName());
			}
		}
		productionOrderService.updateMain(productionOrder, productionOrderPage.getProductionOrderDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "生产订单-通过id删除")
	@Operation(summary="生产订单-通过id删除")
    @RequiresPermissions("mes:mis_production_order:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		productionOrderService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "生产订单-批量删除")
	@Operation(summary="生产订单-批量删除")
    @RequiresPermissions("mes:mis_production_order:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.productionOrderService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产订单-通过id查询")
	@Operation(summary="生产订单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ProductionOrder> queryById(@RequestParam(name="id",required=true) String id) {
		ProductionOrder productionOrder = productionOrderService.getById(id);
		if(productionOrder==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(productionOrder);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产订单明细通过主表ID查询")
	@Operation(summary="生产订单明细主表ID查询")
	@GetMapping(value = "/queryProductionOrderDetailByMainId")
	public Result<List<ProductionOrderDetail>> queryProductionOrderDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<ProductionOrderDetail> productionOrderDetailList = productionOrderDetailService.selectByMainId(id);
		return Result.OK(productionOrderDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param productionOrder
    */
    @RequiresPermissions("mes:mis_production_order:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProductionOrder productionOrder) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<ProductionOrder> queryWrapper = QueryGenerator.initQueryWrapper(productionOrder, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<ProductionOrder> productionOrderList = productionOrderService.list(queryWrapper);

      // Step.3 组装pageList
      List<ProductionOrderPage> pageList = new ArrayList<ProductionOrderPage>();
      for (ProductionOrder main : productionOrderList) {
          ProductionOrderPage vo = new ProductionOrderPage();
          BeanUtils.copyProperties(main, vo);
          List<ProductionOrderDetail> productionOrderDetailList = productionOrderDetailService.selectByMainId(main.getId());
          vo.setProductionOrderDetailList(productionOrderDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "生产订单列表");
      mv.addObject(NormalExcelConstants.CLASS, ProductionOrderPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("生产订单数据", "导出人:"+sysUser.getRealname(), "生产订单"));
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
    @RequiresPermissions("mes:mis_production_order:importExcel")
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
              List<ProductionOrderPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ProductionOrderPage.class, params);
              for (ProductionOrderPage page : list) {
                  ProductionOrder po = new ProductionOrder();
                  BeanUtils.copyProperties(page, po);
                  productionOrderService.saveMain(po, page.getProductionOrderDetailList());
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

	 /**
	  * 获取订单汇总配料单打印数据
	  */
	 @AutoLog(value = "生产订单-汇总配料单打印数据")
	 @Operation(summary="生产订单-汇总配料单打印数据")
	 @GetMapping(value = "/getOrderBatchingPrintData")
	 public Result<Map<String, Object>> getOrderBatchingPrintData(@RequestParam String orderId) {
		 ProductionOrder order = productionOrderService.getById(orderId);
		 if (order == null) {
			 return Result.error("订单不存在");
		 }

		 String recipeId = order.getRecipeId();
		 BigDecimal plannedQty = order.getPlannedQty();
		 int batchCount = order.getBatchCount();
		 BigDecimal batchSize = order.getBatchSize();

		 // 1. 配方技术要求
		 Recipe recipe = recipeService.getById(recipeId);

		 // 2. 工艺步骤
		 List<Map<String, Object>> processSteps = new ArrayList<>();
		 if (recipe != null && StrUtil.isNotBlank(recipe.getRoutingId())) {
			 List<ProcessRoutingStep> steps = processRoutingStepService.list(
					 new LambdaQueryWrapper<ProcessRoutingStep>()
							 .eq(ProcessRoutingStep::getRoutingId, recipe.getRoutingId())
							 .orderByAsc(ProcessRoutingStep::getStepSeq)
			 );
			 for (ProcessRoutingStep s : steps) {
				 Map<String, Object> stepMap = new HashMap<>();
				 stepMap.put("stepSeq", s.getStepSeq());
				 stepMap.put("stepName", s.getStepName());
				 stepMap.put("stepDesc", s.getStepDesc());
				 processSteps.add(stepMap);
			 }
		 }

		 // 3. 客户信息（从订单明细取第一条）
		 List<ProductionOrderDetail> orderDetails = productionOrderDetailService.selectByMainId(orderId);
		 String customerCode = "";
		 String customerName = "";
		 if (!orderDetails.isEmpty()) {
			 ProductionOrderDetail detail = orderDetails.get(0);
			 customerCode = StrUtil.nullToEmpty(detail.getCustomerCode());
			 customerName = StrUtil.nullToEmpty(detail.getCustomerName());
		 }

		 // 4. 配方BOM，按订单总量计算
		 List<RecipeDetail> recipeDetails = recipeDetailService.selectByMainId(recipeId);
		 BigDecimal proportionTotal = recipe != null && recipe.getProportionTotal() != null
				 ? recipe.getProportionTotal()
				 : BigDecimal.valueOf(100);

		 List<Map<String, Object>> materialList = new ArrayList<>();
		 BigDecimal totalPlannedQty = BigDecimal.ZERO;

		 for (RecipeDetail rd : recipeDetails) {
			 BigDecimal totalQty = plannedQty.multiply(rd.getProportion())
					 .divide(proportionTotal, 4, RoundingMode.HALF_UP);

			 totalPlannedQty = totalPlannedQty.add(totalQty);

			 Map<String, Object> material = new HashMap<>();
			 material.put("serialNo", rd.getSerialNo());
			 material.put("materialCode", rd.getMaterialCode());
			 material.put("materialName", rd.getMaterialName());
			 material.put("materialSpec", rd.getMaterialSpec());
			 material.put("plannedQty", totalQty);
			 material.put("proportion", rd.getProportion());
			 material.put("unit", rd.getUnit());
			 material.put("materialBatchNo", "");
			 materialList.add(material);
		 }

		 Map<String, Object> result = new HashMap<>();
		 result.put("companyName", order.getCompanyName());
		 result.put("taskNo", order.getOrderNo());
		 result.put("batchNo", order.getOrderNo());
		 result.put("orderNo", order.getOrderNo());
		 result.put("customerCode", customerCode);
		 result.put("customerName", customerName);
		 result.put("productCode", order.getProductCode());
		 result.put("productName", order.getProductName());
		 result.put("productColor", order.getProductColor());
		 result.put("plannedQty", plannedQty);
		 result.put("batchSize", batchSize);
		 result.put("batchCount", batchCount);
		 result.put("productionDate", null);
		 result.put("technics", recipe != null ? StrUtil.nullToEmpty(recipe.getTechnics()) : "");
		 result.put("taskDesc", "");
		 result.put("processSteps", processSteps);
		 result.put("materialList", materialList);
		 result.put("totalPlannedQty", totalPlannedQty.setScale(2,RoundingMode.HALF_UP));
		 result.put("notes", recipe != null ? StrUtil.nullToEmpty(recipe.getNotes()) : "");
		 result.put("createBy", order.getCreateBy());

		 return Result.OK(result);
	 }

}

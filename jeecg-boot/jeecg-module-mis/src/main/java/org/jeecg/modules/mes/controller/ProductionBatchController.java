package org.jeecg.modules.mes.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.mes.entity.ProductionBatchMaterialActual;
import org.jeecg.modules.mes.service.IProductionBatchMaterialActualService;
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
import org.jeecg.modules.mes.entity.ProductionBatchBom;
import org.jeecg.modules.mes.entity.ProductionBatch;
import org.jeecg.modules.mes.vo.ProductionBatchPage;
import org.jeecg.modules.mes.service.IProductionBatchService;
import org.jeecg.modules.mes.service.IProductionBatchBomService;
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
 * @Description: 生产批次
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Tag(name="生产批次")
@RestController
@RequestMapping("/mes/productionBatch")
@Slf4j
public class ProductionBatchController {
	@Autowired
	private IProductionBatchService productionBatchService;
	@Autowired
	private IProductionBatchBomService productionBatchBomService;
	@Autowired
	private IProductionBatchMaterialActualService materialActualService;
	 @Autowired
	 private ISysDepartService departService;
	/**
	 * 分页列表查询
	 *
	 * @param productionBatch
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "生产批次-分页列表查询")
	@Operation(summary="生产批次-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ProductionBatch>> queryPageList(ProductionBatch productionBatch,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<ProductionBatch> queryWrapper = QueryGenerator.initQueryWrapper(productionBatch, req.getParameterMap());
		Page<ProductionBatch> page = new Page<ProductionBatch>(pageNo, pageSize);
		IPage<ProductionBatch> pageList = productionBatchService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 //@AutoLog(value = "生产批次-分页列表查询")
	 @Operation(summary="生产批次-分页列表查询")
	 @GetMapping(value = "/listNew")
	 public Result<IPage<ProductionBatchPage>> queryPageListNew(ProductionBatch productionBatch,
														 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														 HttpServletRequest req) {
		 QueryWrapper<ProductionBatch> queryWrapper = QueryGenerator.initQueryWrapper(productionBatch, req.getParameterMap());
		 Page<ProductionBatch> page = new Page<ProductionBatch>(pageNo, pageSize);
		 IPage<ProductionBatch> pageList = productionBatchService.page(page, queryWrapper);

		 // 转换为 ProductionBatchPage 并填充配料统计信息
		 List<ProductionBatchPage> voList = fillWeighingSummary(pageList.getRecords());

		 // 构建新的分页结果
		 Page<ProductionBatchPage> voPage = new Page<>(pageNo, pageSize, pageList.getTotal());
		 voPage.setRecords(voList);

		 return Result.OK(voPage);
	 }
	
	/**
	 *   添加
	 *
	 * @param productionBatchPage
	 * @return
	 */
	@AutoLog(value = "生产批次-添加")
	@Operation(summary="生产批次-添加")
    @RequiresPermissions("mes:mis_production_batch:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProductionBatchPage productionBatchPage) {
		ProductionBatch productionBatch = new ProductionBatch();
		BeanUtils.copyProperties(productionBatchPage, productionBatch);
		if(productionBatch.getCompanyId() != null){
			SysDepart depart =  departService.getDepartById(productionBatch.getCompanyId());
			if(depart != null){
				productionBatch.setCompanyCode(depart.getOrgCode());
				productionBatch.setCompanyName(depart.getDepartName());
			}
		}

		productionBatchService.saveMain(productionBatch, productionBatchPage.getProductionBatchBomList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param productionBatchPage
	 * @return
	 */
	@AutoLog(value = "生产批次-编辑")
	@Operation(summary="生产批次-编辑")
    @RequiresPermissions("mes:mis_production_batch:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProductionBatchPage productionBatchPage) {
		ProductionBatch productionBatch = new ProductionBatch();
		BeanUtils.copyProperties(productionBatchPage, productionBatch);
		ProductionBatch productionBatchEntity = productionBatchService.getById(productionBatch.getId());
		if(productionBatchEntity==null) {
			return Result.error("未找到对应数据");
		}
		if(productionBatch.getCompanyId() != null){
			SysDepart depart =  departService.getDepartById(productionBatch.getCompanyId());
			if(depart != null){
				productionBatch.setCompanyCode(depart.getOrgCode());
				productionBatch.setCompanyName(depart.getDepartName());
			}
		}
		productionBatchService.updateMain(productionBatch, productionBatchPage.getProductionBatchBomList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "生产批次-通过id删除")
	@Operation(summary="生产批次-通过id删除")
    @RequiresPermissions("mes:mis_production_batch:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		productionBatchService.delMain(id);
		return Result.OK("删除成功!");
	}
	 /**
	  *   通过id设置状态
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "生产批次-通过id设置状态")
	 @Operation(summary="生产批次-通过id设置状态")
	 @DeleteMapping(value = "/setStatus")
	 public Result<String> setStatus(@RequestParam(name="id",required=true) String id, @RequestParam(name="status",required=true)String status) {
		 ProductionBatch productionBatch = productionBatchService.getById(id);
		 if(productionBatch==null) {
			 return Result.error("该批次不存在");
		 }
		 productionBatchService.setStatus(id,status);
		 return Result.OK("设置成功!");
	 }
	 @AutoLog(value = "生产批次-通过id设置开始配料")
	 @Operation(summary="生产批次-通过id设置开始配料")
	 @DeleteMapping(value = "/startWeighing")
	 public Result<String> startWeighing(@RequestParam(name="id",required=true) String id) {
		 ProductionBatch productionBatch = productionBatchService.getById(id);
		 if(productionBatch==null) {
			 return Result.error("该批次不存在");
		 }
		 //设置生产日期及失效日期
		 LocalDate productionDate = LocalDate.now();
		 LocalDate expiredDate = productionDate.plusDays(productionBatch.getShelfLife());
		 productionBatch.setProductionDate(Date.from(productionDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		 productionBatch.setExpiryDate(Date.from(expiredDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

		 productionBatch.setWeighingStartTime(new Date());
		 productionBatch.setStatus("WEIGHING");
		 productionBatchService.updateById(productionBatch);
		 return Result.OK("设置成功!");
	 }

	 @AutoLog(value = "生产批次-通过id设置完成配料")
	 @Operation(summary="生产批次-通过id设置完成配料")
	 @DeleteMapping(value = "/completeWeighing")
	 public Result<String> completeWeighing(@RequestParam(name="id",required=true) String id) {
		 ProductionBatch productionBatch = productionBatchService.getById(id);
		 if(productionBatch==null) {
			 return Result.error("该批次不存在");
		 }
		 // 查询称重记录

		 List<ProductionBatchMaterialActual> actualList = materialActualService.list(
				 new LambdaQueryWrapper<ProductionBatchMaterialActual>()
						 .eq(ProductionBatchMaterialActual::getBatchId, productionBatch.getId())
						 .eq(ProductionBatchMaterialActual::getDelFlag, "0")  // 只查未删除的
		 );
		 BigDecimal totalWeight = actualList.stream()
				 .map(ProductionBatchMaterialActual::getActualQty)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);

		 productionBatch.setActualQty(totalWeight);
		 productionBatch.setWeighingEndTime(new Date());
		 productionBatch.setStatus("WEIGHED");
		 productionBatchService.updateById(productionBatch);
		 return Result.OK("设置成功!");
	 }

	 /**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "生产批次-批量删除")
	@Operation(summary="生产批次-批量删除")
    @RequiresPermissions("mes:mis_production_batch:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.productionBatchService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产批次-通过id查询")
	@Operation(summary="生产批次-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ProductionBatch> queryById(@RequestParam(name="id",required=true) String id) {
		ProductionBatch productionBatch = productionBatchService.getById(id);
		if(productionBatch==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(productionBatch);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "生产批次物料清单通过主表ID查询")
	@Operation(summary="生产批次物料清单主表ID查询")
	@GetMapping(value = "/queryProductionBatchBomByMainId")
	public Result<List<ProductionBatchBom>> queryProductionBatchBomListByMainId(@RequestParam(name="id",required=true) String id) {
		List<ProductionBatchBom> productionBatchBomList = productionBatchBomService.selectByMainId(id);
		return Result.OK(productionBatchBomList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param productionBatch
    */
    @RequiresPermissions("mes:mis_production_batch:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProductionBatch productionBatch) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<ProductionBatch> queryWrapper = QueryGenerator.initQueryWrapper(productionBatch, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<ProductionBatch> productionBatchList = productionBatchService.list(queryWrapper);

      // Step.3 组装pageList
      List<ProductionBatchPage> pageList = new ArrayList<ProductionBatchPage>();
      for (ProductionBatch main : productionBatchList) {
          ProductionBatchPage vo = new ProductionBatchPage();
          BeanUtils.copyProperties(main, vo);
          List<ProductionBatchBom> productionBatchBomList = productionBatchBomService.selectByMainId(main.getId());
          vo.setProductionBatchBomList(productionBatchBomList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "生产批次列表");
      mv.addObject(NormalExcelConstants.CLASS, ProductionBatchPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("生产批次数据", "导出人:"+sysUser.getRealname(), "生产批次"));
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
    @RequiresPermissions("mes:mis_production_batch:importExcel")
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
              List<ProductionBatchPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ProductionBatchPage.class, params);
              for (ProductionBatchPage page : list) {
                  ProductionBatch po = new ProductionBatch();
                  BeanUtils.copyProperties(page, po);
                  productionBatchService.saveMain(po, page.getProductionBatchBomList());
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
	  * 批量填充配料统计信息到 ProductionBatchPage
	  */
	 private List<ProductionBatchPage> fillWeighingSummary(List<ProductionBatch> batchList) {
		 if (batchList.isEmpty()) {
			 return new ArrayList<>();
		 }

		 // 1. 收集所有批次ID
		 List<String> batchIds = batchList.stream()
				 .map(ProductionBatch::getId)
				 .collect(Collectors.toList());

		 // 2. 批量查询所有批次的物料BOM信息（只查未删除的）
		 List<ProductionBatchBom> allBomList = productionBatchBomService.list(
				 new LambdaQueryWrapper<ProductionBatchBom>()
						 .in(ProductionBatchBom::getBatchId, batchIds)
						 .eq(ProductionBatchBom::getDelFlag, "0")  // 只查未删除的
		 );

		 // 3. 批量查询所有批次的称重记录（只查未删除的！！！）
		 List<ProductionBatchMaterialActual> allActualList = materialActualService.list(
				 new LambdaQueryWrapper<ProductionBatchMaterialActual>()
						 .in(ProductionBatchMaterialActual::getBatchId, batchIds)
						 .eq(ProductionBatchMaterialActual::getDelFlag, "0")  // 只查未删除的
		 );

		 // 4. 按批次ID分组
		 Map<String, List<ProductionBatchBom>> bomMap = allBomList.stream()
				 .collect(Collectors.groupingBy(ProductionBatchBom::getBatchId));

		 Map<String, List<ProductionBatchMaterialActual>> actualMap = allActualList.stream()
				 .collect(Collectors.groupingBy(ProductionBatchMaterialActual::getBatchId));

		 // 5. 转换为 ProductionBatchPage 并计算统计信息
		 List<ProductionBatchPage> result = new ArrayList<>();
		 for (ProductionBatch batch : batchList) {
			 ProductionBatchPage vo = new ProductionBatchPage();
			 BeanUtils.copyProperties(batch, vo);

			 String batchId = batch.getId();
			 List<ProductionBatchBom> bomList = bomMap.getOrDefault(batchId, Collections.emptyList());
			 List<ProductionBatchMaterialActual> actualList = actualMap.getOrDefault(batchId, Collections.emptyList());

			 // 计算统计信息
			 int totalBom = bomList.size();
			 int completedBom = 0;
			 BigDecimal totalActualWeight = BigDecimal.ZERO;

			 for (ProductionBatchBom bom : bomList) {
				 // 累加该物料的所有称重记录（只累加未删除的）
				 BigDecimal actualQty = actualList.stream()
						 .filter(a -> a.getBatchBomId().equals(bom.getId()))
						 .map(ProductionBatchMaterialActual::getActualQty)
						 .reduce(BigDecimal.ZERO, BigDecimal::add);

				 // 设置精度为4位小数，避免浮点误差
				 actualQty = actualQty.setScale(4, RoundingMode.HALF_UP);
				 BigDecimal plannedQty = bom.getPlannedQty() != null
						 ? bom.getPlannedQty().setScale(4, RoundingMode.HALF_UP)
						 : BigDecimal.ZERO;

				 // 累加到批次总重量
				 totalActualWeight = totalActualWeight.add(actualQty);

				 // 判断是否完成（使用compareTo比较，精确到4位小数）
				 if (actualQty.compareTo(plannedQty) >= 0) {
					 completedBom++;
				 }
			 }

			 // 设置统计字段
			 vo.setTotalBom(totalBom);
			 vo.setCompletedBom(completedBom);
			 vo.setPercent(totalBom == 0 ? 0 : (completedBom * 100 / totalBom));
			 vo.setTotalActualWeight(totalActualWeight);
			 vo.setWeighingStatus(completedBom == 0 ? "PENDING" :
					 (completedBom == totalBom ? "WEIGHED" : "WEIGHING"));

			 result.add(vo);
		 }

		 return result;
	 }
	 /**
	  * 获取批次配料汇总信息（用于列表显示）
	  */
	 @GetMapping("/getWeighingSummary")
	 public Result<Map<String, Object>> getWeighingSummary(@RequestParam String batchId) {
		 // 查询批次下所有物料
		 List<ProductionBatchBom> bomList = productionBatchBomService.list(
				 new LambdaQueryWrapper<ProductionBatchBom>()
						 .eq(ProductionBatchBom::getBatchId, batchId)
		 );

		 // 查询所有称重记录
		 List<ProductionBatchMaterialActual> actualList = materialActualService.list(
				 new LambdaQueryWrapper<ProductionBatchMaterialActual>()
						 .eq(ProductionBatchMaterialActual::getBatchId, batchId)
		 );

		 // 计算汇总信息
		 int totalBom = bomList.size();
		 int completedBom = 0;
		 BigDecimal totalActualWeight = java.math.BigDecimal.ZERO;

		 for (ProductionBatchBom bom : bomList) {
			 BigDecimal actualQty = actualList.stream()
					 .filter(a -> a.getBatchBomId().equals(bom.getId()))
					 .map(ProductionBatchMaterialActual::getActualQty)
					 .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add); // 累加;

			 // 2. 累加到总重量
			 totalActualWeight = totalActualWeight.add(actualQty);
			 // 使用 compareTo 进行比较：
			 // 返回 1 表示 actualQty > plannedQty
			 // 返回 0 表示 actualQty == plannedQty
			 // 返回 -1 表示 actualQty < plannedQty
			 if (actualQty.compareTo( bom.getPlannedQty()) >= 0) {
				 completedBom++;
			 }
		 }

		 Map<String, Object> result = new HashMap<>();
		 result.put("totalBom", totalBom);
		 result.put("completedBom", completedBom);
		 result.put("percent", totalBom == 0 ? 0 : (completedBom * 100 / totalBom));
		 result.put("totalActualWeight", totalActualWeight);
		 result.put("weighingStatus", completedBom == 0 ? "PENDING" :
				 (completedBom == totalBom ? "WEIGHED" : "WEIGHING"));

		 return Result.OK(result);
	 }

	 /**
	  * 获取批次配料详细信息（用于弹窗）
	  */
	 @GetMapping("/getWeighingDetail")
	 public Result<Map<String, Object>> getWeighingDetail(@RequestParam String batchId) {
		 // 1. 查询批次基本信息
		 ProductionBatch batch = productionBatchService.getById(batchId);
		 if (batch == null) {
			 return Result.error("批次不存在");
		 }

		 // 2. 查询批次下的所有物料清单 (BOM)
		 List<ProductionBatchBom> bomList = productionBatchBomService.list(
				 new LambdaQueryWrapper<ProductionBatchBom>()
						 .eq(ProductionBatchBom::getBatchId, batchId)
		 );

		 // 3. 查询所有称重记录
		 List<ProductionBatchMaterialActual> actualList = materialActualService.list(
				 new LambdaQueryWrapper<ProductionBatchMaterialActual>()
						 .eq(ProductionBatchMaterialActual::getBatchId, batchId)
						 .orderByAsc(ProductionBatchMaterialActual::getCreateTime) // 按时间排序
		 );

		 // 4. 计算每个物料的称重记录和汇总信息
		 List<Map<String, Object>> processedBomList = new ArrayList<>();
		 for (ProductionBatchBom bom : bomList) {
			 // 获取该BOM项的所有称重记录
			 List<ProductionBatchMaterialActual> recordsForThisBom = actualList.stream()
					 .filter(record -> bom.getId().equals(record.getBatchBomId()))
					 .collect(Collectors.toList());

			 // 计算总实际重量
			 java.math.BigDecimal totalActualQty = recordsForThisBom.stream()
					 .map(ProductionBatchMaterialActual::getActualQty)
					 .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

			 // 计算完成百分比
			 java.math.BigDecimal plannedQty = bom.getPlannedQty();
			 int progressPercent = 0;
			 if (plannedQty != null && plannedQty.compareTo(java.math.BigDecimal.ZERO) > 0) {
				 progressPercent = totalActualQty.multiply(new java.math.BigDecimal(100))
						 .divideToIntegralValue(plannedQty).intValue();
			 }

			 boolean isComplete = totalActualQty.compareTo(plannedQty) >= 0;

			 Map<String, Object> processedBomItem = new HashMap<>();
			 processedBomItem.put("id", bom.getId());
			 processedBomItem.put("serialNo", bom.getSerialNo()); // 序号
			 processedBomItem.put("materialCode", bom.getMaterialCode()); // 物料编码
			 processedBomItem.put("materialName", bom.getMaterialName()); // 物料名称
			 processedBomItem.put("materialSpec", bom.getMaterialSpec()); // 规格
			 processedBomItem.put("proportion", bom.getProportion()); // 配比
			 processedBomItem.put("plannedQty", bom.getPlannedQty()); // 计划量
			 processedBomItem.put("totalActualQty", totalActualQty); // 该物料的实际总重量
			 processedBomItem.put("progressPercent", progressPercent); // 完成百分比
			 processedBomItem.put("isComplete", isComplete); // 是否完成
			 processedBomItem.put("weighingRecords", recordsForThisBom); // 该物料的所有称重记录

			 processedBomList.add(processedBomItem);
		 }

		 // 5. 将所有称重记录按时间顺序整理为流水账
		 List<Map<String, Object>> weighingRecords = actualList.stream().map(record -> {
			 Map<String, Object> recordMap = new HashMap<>();
			 // 根据 record.getBatchBomId() 找到对应的 bom 以获取物料名称等信息
			 Optional<ProductionBatchBom> foundBom = bomList.stream()
					 .filter(b -> b.getId().equals(record.getBatchBomId()))
					 .findFirst();

			 recordMap.put("id", record.getId());
			 recordMap.put("createTime", record.getCreateTime());
			 recordMap.put("materialName", foundBom.map(ProductionBatchBom::getMaterialName).orElse("未知物料"));
			 recordMap.put("actualQty", record.getActualQty());
			 recordMap.put("operatorName", record.getOperatorName());
			 recordMap.put("batchBomId", record.getBatchBomId());
			 return recordMap;
		 }).collect(Collectors.toList());

		 // 6. 组织返回数据
		 Map<String, Object> result = new HashMap<>();
		 result.put("batchInfo", batch); // 批次基本信息
		 result.put("bomList", processedBomList); // 物料及称重汇总
		 result.put("weighingRecords", weighingRecords); // 所有称重记录流水

		 return Result.OK(result);
	 }
}

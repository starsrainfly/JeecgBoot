package org.jeecg.modules.mes.controller;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.common.enums.ProductionBatchStatusEnum;
import org.jeecg.modules.common.enums.ProductionOrderStatusEnum;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
import org.jeecg.modules.mdm.entity.Recipe;
import org.jeecg.modules.mdm.service.IProcessRoutingStepService;
import org.jeecg.modules.mdm.service.IRecipeService;
import org.jeecg.modules.mes.entity.*;
import org.jeecg.modules.mes.mapper.ProductionMaterialMapper;
import org.jeecg.modules.mes.service.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.mes.vo.ProductionTaskVo;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
 * @Description: 工单表
 * @Author: jeecg-boot
 * @Date:   2026-03-11
 * @Version: V1.0
 */
@Tag(name="工单表")
@RestController
@RequestMapping("/mes/productionTask")
@Slf4j
public class ProductionTaskController extends JeecgController<ProductionTask, IProductionTaskService> {
	@Autowired
	private IProductionTaskService productionTaskService;

	 @Autowired
	 private IProductionBatchMaterialActualService materialActualService;

	 @Autowired
	 private ISerialNoService serialNoService;

	 @Autowired
	 private ISysUserService sysUserService;

	 @Autowired
	 private IProcessRoutingStepService processRoutingStepService;
	 @Autowired
	 private IProductionBatchService productionBatchService;
	 @Autowired
	 private IRecipeService recipeService;
	 @Autowired
	 private ProductionMaterialMapper productionMaterialMapper;
	 @Autowired
	 private IProductionBatchBomService productionBatchBomService;
	 @Autowired
	 private IProductionOrderService productionOrderService;
	 @Autowired
	 private IProductionOrderDetailService productionOrderDetailService;

	/*// 查询包装物料（内包+外包）
List<ProductionMaterial> packageMaterials = productionMaterialService.list(
    new LambdaQueryWrapper<ProductionMaterial>()
        .eq(ProductionMaterial::getBatchId, batch.getId())
        .in(ProductionMaterial::getMaterialType, "1", "2") // 1=内包, 2=外包
);*/
	/**
	 * 分页列表查询
	 *
	 * @param productionTask
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "工序表-分页列表查询")
	@Operation(summary="工序表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ProductionTaskVo>> queryPageList(ProductionTaskVo productionTaskVo,
														 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														 HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
       // QueryWrapper<ProductionTask> queryWrapper = QueryGenerator.initQueryWrapper(productionTask, req.getParameterMap(),customeRuleMap);
		Page<ProductionTaskVo> page = new Page<ProductionTaskVo>(pageNo, pageSize);
		IPage<ProductionTaskVo> pageList = productionTaskService.getPageList(page,productionTaskVo); //productionTaskService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param productionTask
	 * @return
	 */
	@AutoLog(value = "工序表-添加")
	@Operation(summary="工序表-添加")
	@RequiresPermissions("mes:mis_production_task:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProductionTask productionTask) {
		productionTaskService.save(productionTask);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param productionTask
	 * @return
	 */
	@AutoLog(value = "工序表-编辑")
	@Operation(summary="工序表-编辑")
	@RequiresPermissions("mes:mis_production_task:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProductionTask productionTask) {
		if(!oConvertUtils.isEmpty(productionTask.getAssignedOperatorId())){
			SysUser user = sysUserService.getById(productionTask.getAssignedOperatorId());
			if(user!=null ){
				productionTask.setAssignedOperatorName(user.getRealname());
			}
		}

		productionTaskService.updateById(productionTask);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "工序表-通过id删除")
	@Operation(summary="工序表-通过id删除")
	@RequiresPermissions("mes:mis_production_task:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		productionTaskService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "工序表-批量删除")
	@Operation(summary="工序表-批量删除")
	@RequiresPermissions("mes:mis_production_task:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.productionTaskService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "工序表-通过id查询")
	@Operation(summary="工序表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ProductionTask> queryById(@RequestParam(name="id",required=true) String id) {
		ProductionTask productionTask = productionTaskService.getById(id);
		if(productionTask==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(productionTask);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param productionTask
    */
    @RequiresPermissions("mes:mis_production_task:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProductionTask productionTask) {
        return super.exportXls(request, productionTask, ProductionTask.class, "工序表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mes:mis_production_task:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ProductionTask.class);
    }

	 /**
	  * 我的工单列表（当前登录操作员）
	  */
	 @AutoLog(value = "我的工单-列表查询")
	 @Operation(summary="我的工单-列表查询")
	 @GetMapping(value = "/myTaskList")
	 public Result<IPage<ProductionTask>> myTaskList(
			 @RequestParam(name="taskType", required=false) String taskType,
			 @RequestParam(name="status", required=false) String status,
			 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
			 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
			 HttpServletRequest req) {

		 // 获取当前登录用户
		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 String operatorId = loginUser.getId();

		 LambdaQueryWrapper<ProductionTask> queryWrapper = new LambdaQueryWrapper<>();
		 queryWrapper.eq(ProductionTask::getAssignedOperatorId, operatorId);

		 // 按类型筛选
		 if (StrUtil.isNotEmpty(taskType)) {
			 queryWrapper.eq(ProductionTask::getTaskType, taskType);
		 }

		 // 按状态筛选
		 if (StrUtil.isNotEmpty(status)) {
			 queryWrapper.eq(ProductionTask::getStatus, status);
		 } else {
			 // 默认排除已完成的
			 queryWrapper.notIn(ProductionTask::getStatus, Arrays.asList("completed", "cancelled"));
		 }

		 queryWrapper.orderByAsc(ProductionTask::getSequence)
				 .orderByAsc(ProductionTask::getCreateTime);

		 Page<ProductionTask> page = new Page<>(pageNo, pageSize);
		 IPage<ProductionTask> pageList = productionTaskService.page(page, queryWrapper);

		 return Result.OK(pageList);
	 }

	 /**
	  * 开始任务
	  */
	 @AutoLog(value = "我的工单-开始任务")
	 @Operation(summary="我的工单-开始任务")
	 @Transactional(rollbackFor = Exception.class)
	 @RequestMapping(value = "/start", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> startTask(@RequestBody ProductionTask productionTask) {
		 String taskId = productionTask.getId();
		 ProductionTask task = productionTaskService.getById(taskId);

		 if (task == null) {
			 return Result.error("工单不存在");
		 }

		 // 更新状态为执行中
		 task.setStatus("PROCESSING");
		 task.setActualStartTime(new Date());
		 task.setActualEquipmentId(productionTask.getActualEquipmentId());
		 task.setActualEquipmentName(productionTask.getActualEquipmentName());
		 task.setActualEquipmentSettings(productionTask.getActualEquipmentSettings());
		 task.setActualEquipmentType(productionTask.getActualEquipmentType());
		 task.setActualEquipmentCode(productionTask.getActualEquipmentCode());
		 task.setActualModel(productionTask.getActualModel());

		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 task.setActualOperatorId(loginUser.getId());  // 记录实际执行人
		 task.setActualOperatorName(loginUser.getRealname());

		 productionTaskService.updateById(task);
		 //添加更新生产订单的实际开工时间
		 ProductionBatch productionBatch = productionBatchService.getById(productionTask.getBatchId());
		 if(productionBatch !=null) {
			 ProductionOrder productionOrder = productionOrderService.getById(productionBatch.getOrderId());
			 if(productionOrder !=null) {
				 productionOrder.setActualStartTime(new Date());
				 productionOrderService.updateById(productionOrder);
			 }
		 }

		 return Result.OK("任务已开始");
	 }

	 /**
	  * 完成任务
	  */
	 @AutoLog(value = "我的工单-完成任务")
	 @Operation(summary="我的工单-完成任务")
	 @Transactional(rollbackFor = Exception.class)
	 @RequestMapping(value = "/complete", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> completeTask(@RequestBody ProductionTask productionTask) {
		 String taskId = productionTask.getId();
		 ProductionTask task = productionTaskService.getById(taskId);

		 if (task == null) {
			 return Result.error("工单不存在");
		 }

		 task.setStatus("COMPLETED");
		 task.setActualEndTime(new Date());

		 // 计算实际耗时（分钟）
		 if (task.getActualStartTime() != null) {
			 long duration = (task.getActualEndTime().getTime() - task.getActualStartTime().getTime()) / (1000 * 60);
			 task.setActualDuration((int) duration);
		 }

		 productionTaskService.updateById(task);

		 // 判断是否是最后工序，更新批次状态
		 updateBatchIfFinishStep(task);

		 return Result.OK("任务已完成");
	 }
	 /**
	  * 如果是最后工序，更新批次为"可入库"
	  */
	 @Transactional(rollbackFor = Exception.class)
	 public void updateBatchIfFinishStep(ProductionTask task) {
		 String batchId = task.getBatchId();
		 if (StrUtil.isEmpty(batchId)) {
			 return;
		 }

		 // 获取工艺步骤
		 ProcessRoutingStep step = processRoutingStepService.getById(task.getRoutingDetailId());
		 if (step == null || !"1".equals(step.getIsFinishStep())) {
			 return;  // 不是最后工序，不处理
		 }

		 // 是最后工序：更新批次为生产完成
		 ProductionBatch batch = productionBatchService.getById(batchId);
		 if (batch == null) {
			 return;
		 }

		 // 校验：必须先完成配料（有actualQty）
		 if (batch.getActualQty() == null || batch.getActualQty().compareTo(BigDecimal.ZERO) <= 0) {
			 log.error("批次【{}】最后工序完工，但未完成配料称重！", batch.getBatchNo());
			 // 这里可以抛异常阻止，或者只是记录日志
			 // throw new JeecgBootException("请先完成配料称重");
			 return;
		 }

		 batch.setStatus(ProductionBatchStatusEnum.COMPLETED.getValue());      // 生产完成
		 batch.setInStockStatus("0");        // 未入库
		 // remainQty 和 inStockQty 在 completeWeighing 时已初始化
		 // 如果之前没初始化，这里兜底
		 if (batch.getRemainQty() == null) {
			 batch.setRemainQty(batch.getActualQty());
		 }
		 if (batch.getInStockQty() == null) {
			 batch.setInStockQty(BigDecimal.ZERO);
		 }

		 productionBatchService.updateById(batch);

		 log.info("批次【{}】最后工序【{}】完工，生产完成，可入库",
				 batch.getBatchNo(), step.getStepName());

		 // 3. 订单级完工判定
		 String orderId = batch.getOrderId();
		 if (StrUtil.isNotEmpty(orderId)) {
			 checkAndUpdateOrderCompletion(orderId);
		 }
	 }

	 /**
	  * 检查订单下所有批次完工状态
	  * 部分完成 → 状态2，全部完成 → 状态3 + 完工时间
	  */
	 private void checkAndUpdateOrderCompletion(String orderId) {
		 // 查询该订单下所有批次
		 List<ProductionBatch> batchList = productionBatchService.list(
				 new LambdaQueryWrapper<ProductionBatch>()
						 .eq(ProductionBatch::getOrderId, orderId)
		 );

		 if (CollUtil.isEmpty(batchList)) {
			 return;
		 }

		 long totalCount = batchList.size();
		 long completedCount = batchList.stream()
				 .filter(b -> ProductionBatchStatusEnum.COMPLETED.getValue().equals(b.getStatus()))
				 .count();

		 ProductionOrder order = productionOrderService.getById(orderId);
		 if (order == null) {
			 return;
		 }

		 String currentStatus = order.getStatus();
		 String newStatus = null;

		 if (completedCount == 0) {
			 // 没有任何批次完工，保持原状态（理论上不会走到这里，因为当前批次刚被标记完成）
			 return;
		 } else if (completedCount < totalCount) {
			 // 部分完成
			 newStatus = ProductionOrderStatusEnum.PARTIAL_COMPLETED.getValue();
		 } else {
			 // 全部完成
			 newStatus = ProductionOrderStatusEnum.COMPLETED.getValue();
		 }

		 // 防重复更新：状态未变化则不更新
		 if (newStatus.equals(currentStatus)) {
			 log.info("订单【{}】状态已为【{}】，无需重复更新", orderId, currentStatus);
			 return;
		 }

		 order.setStatus(newStatus);

		 // 只有全部完成时才更新实际完工时间
		 if (ProductionOrderStatusEnum.COMPLETED.getValue().equals(newStatus)) {
			 order.setActualEndTime(new Date());
			 log.info("订单【{}】所有批次已完工（{}/{}），状态更新为【完成】，完工时间：{}",
					 orderId, completedCount, totalCount, order.getActualEndTime());
		 } else {
			 log.info("订单【{}】部分完工（{}/{}），状态更新为【部分完成】",
					 orderId, completedCount, totalCount);
		 }

		 productionOrderService.updateById(order);
	 }

	 /**
	  * 报检（生成质检工单）
	  */
	 @AutoLog(value = "我的工单-报检")
	 @Operation(summary="我的工单-报检")
	 @PostMapping(value = "/reportQc")
	 public Result<String> reportQc(@RequestBody Map<String, String> params) {
		 String taskId = params.get("taskId");
		 ProductionTask sourceTask = productionTaskService.getById(taskId);

		 if (sourceTask == null) {
			 return Result.error("工单不存在");
		 }

		 // 1. 先完成当前任务
		 sourceTask.setStatus("COMPLETED");
		 sourceTask.setActualEndTime(new Date());

		 // 计算实际耗时（分钟）
		 if (sourceTask.getActualStartTime() != null) {
			 long duration = (sourceTask.getActualEndTime().getTime() - sourceTask.getActualStartTime().getTime()) / (1000 * 60);
			 sourceTask.setActualDuration((int) duration);
		 }
		 sourceTask.setQcStatus("1");  // 已报检
		 productionTaskService.updateById(sourceTask);

		 // 2. 生成质检工单
		 ProductionTask qcTask = new ProductionTask();
		 qcTask.setTaskType("qc");  // 质检类型
		 qcTask.setBatchId(sourceTask.getBatchId());
		 qcTask.setBatchNo(sourceTask.getBatchNo());
		 qcTask.setOrderNo(sourceTask.getOrderNo());
		 qcTask.setProductId(sourceTask.getProductId());
		 qcTask.setProductCode(sourceTask.getProductCode());
		 qcTask.setProductName(sourceTask.getProductName());
		 qcTask.setSequence(sourceTask.getSequence());
		 String taskNo =serialNoService.generateSerialNo(SerialNoPrefixEnum.PRODUCTION_WORK_ORDER.getPrefix());
		 qcTask.setTaskNo(taskNo);
		 qcTask.setTaskName(sourceTask.getProductName() + "-质检");
		 qcTask.setSourceTaskId(sourceTask.getId());  // 关联来源工单
		 qcTask.setStatus("PENDING");

		 // 质检员从质检组分配（这里简化处理，实际可配置）
		 // qcTask.setAssignedOperatorId(...);

		 productionTaskService.save(qcTask);



		 return Result.OK("报检成功，质检工单已生成");
	 }

	 /**
	  * 查询物料称重记录
	  */
	 @GetMapping(value = "/queryMaterialActual")
	 public Result<List<ProductionBatchMaterialActual>> queryMaterialActual(
			 @RequestParam String batchId,
			 @RequestParam(required = false) String batchBomId) {

		 LambdaQueryWrapper<ProductionBatchMaterialActual> wrapper = new LambdaQueryWrapper<>();
		 wrapper.eq(ProductionBatchMaterialActual::getBatchId, batchId);
		// wrapper.eq(ProductionBatchMaterialActual::getDelFlag, "0");

		 if (StrUtil.isNotEmpty(batchBomId)) {
			 wrapper.eq(ProductionBatchMaterialActual::getBatchBomId, batchBomId);
		 }

		 wrapper.orderByDesc(ProductionBatchMaterialActual::getCreateTime);
		 List<ProductionBatchMaterialActual> list = materialActualService.list(wrapper);

		 return Result.OK(list);
	 }

	 /**
	  * 获取配料工单打印数据（模式1：单工单）
	  */
	 @AutoLog(value = "配料工单-获取打印数据")
	 @Operation(summary="配料工单-获取打印数据")
	 @GetMapping(value = "/getBatchingPrintData")
	 public Result<Map<String, Object>> getBatchingPrintData(@RequestParam String taskId) {
		 ProductionTask task = productionTaskService.getById(taskId);
		 if (task == null) {
			 return Result.error("工单不存在");
		 }

		 // 必须是配料工序
		 ProcessRoutingStep step = processRoutingStepService.getById(task.getRoutingDetailId());
		 if (step == null || !"1".equals(step.getIsMaterialStep())) {
			 return Result.error("该工单不是配料工序");
		 }

		 String batchId = task.getBatchId();
		 String orderNo = task.getOrderNo();

		 // 1. 批次信息
		 ProductionBatch batch = productionBatchService.getById(batchId);

		 // 2. 订单信息 + 客户信息（从明细取）
		 ProductionOrder order = productionOrderService.getOne(
				 new LambdaQueryWrapper<ProductionOrder>()
						 .eq(ProductionOrder::getOrderNo, orderNo)
		 );

		 String customerCode = "";
		 String customerName = "";
		 if (order != null) {
			 // 从生产订单明细取客户信息（销售类型有，备货类型为空）
			 List<ProductionOrderDetail> orderDetails = productionOrderDetailService.list(
					 new LambdaQueryWrapper<ProductionOrderDetail>()
							 .eq(ProductionOrderDetail::getOrderId, order.getId())
							 .orderByAsc(ProductionOrderDetail::getCreateTime)
							 .last("LIMIT 1")
			 );
			 if (!orderDetails.isEmpty()) {
				 ProductionOrderDetail detail = orderDetails.get(0);
				 customerCode = StrUtil.nullToEmpty(detail.getCustomerCode());
				 customerName = StrUtil.nullToEmpty(detail.getCustomerName());
			 }
		 }

		 // 3. 配方技术要求
		 Recipe recipe = null;
		 if (batch != null && StrUtil.isNotBlank(batch.getRecipeId())) {
			 recipe = recipeService.getById(batch.getRecipeId());
		 }

		 // 4. 工艺步骤（标准工艺）
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

		 // 5. BOM物料清单 + 出库物料批号
		 List<ProductionBatchBom> bomList = productionBatchBomService.list(
				 new LambdaQueryWrapper<ProductionBatchBom>()
						 .eq(ProductionBatchBom::getBatchId, batchId)
						 .orderByAsc(ProductionBatchBom::getSerialNo)
		 );

		 List<Map<String, Object>> materialList = new ArrayList<>();
		 for (ProductionBatchBom bom : bomList) {
			 Map<String, Object> material = new HashMap<>();
			 material.put("serialNo", bom.getSerialNo());
			 material.put("materialCode", bom.getMaterialCode());
			 material.put("materialName", bom.getMaterialName());
			 material.put("materialSpec", bom.getMaterialSpec());
			 material.put("plannedQty", bom.getPlannedQty());
			 material.put("proportion", bom.getProportion());
			 material.put("unit", bom.getUnit());

			 // 查询出库物料批号
			 List<String> batchNos = productionMaterialMapper.getMaterialBatchNo(batchId, bom.getMaterialId());
			 material.put("materialBatchNo", CollUtil.join(batchNos, ","));

			 materialList.add(material);
		 }

		 // 6. 汇总合计
		 BigDecimal totalPlannedQty = bomList.stream()
				 .map(ProductionBatchBom::getPlannedQty)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);

		 // 7. 组装返回数据
		 Map<String, Object> result = new HashMap<>();
		 result.put("companyName", task.getCompanyName());
		 result.put("taskNo", task.getTaskNo());
		 result.put("batchNo", task.getBatchNo());
		 result.put("orderNo", orderNo);
		 result.put("customerCode", customerCode);
		 result.put("customerName", customerName);
		 result.put("productCode", task.getProductCode());
		 result.put("productName", task.getProductName());
		 result.put("productColor", task.getProductColor());
		 result.put("plannedQty", batch != null ? batch.getPlannedQty() : BigDecimal.ZERO);
		 result.put("batchSize", batch != null ? batch.getPlannedQty() : BigDecimal.ZERO);
		 result.put("batchCount", 1);
		 result.put("productionDate", batch != null ? batch.getProductionDate() : null);
		 result.put("technics", recipe != null ? StrUtil.nullToEmpty(recipe.getTechnics()) : "");
		 result.put("taskDesc", StrUtil.nullToEmpty(task.getTaskDesc()));
		 result.put("processSteps", processSteps);
		 result.put("materialList", materialList);
		 result.put("totalPlannedQty", totalPlannedQty);
		 result.put("notes", recipe != null ? StrUtil.nullToEmpty(recipe.getNotes()) : "");
		 result.put("createBy", task.getCreateBy());

		 return Result.OK(result);
	 }
}

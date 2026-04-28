package org.jeecg.modules.mes.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.mdm.entity.Material;
import org.jeecg.modules.mdm.entity.Recipe;
import org.jeecg.modules.mdm.entity.RecipeDetail;
import org.jeecg.modules.mdm.service.*;
import org.jeecg.modules.mes.entity.*;
import org.jeecg.modules.mes.mapper.ProductionOrderDetailMapper;
import org.jeecg.modules.mes.mapper.ProductionOrderMapper;
import org.jeecg.modules.mes.service.*;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 生产订单
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
@Service
public class ProductionOrderServiceImpl extends ServiceImpl<ProductionOrderMapper, ProductionOrder> implements IProductionOrderService {

	@Autowired
	private ProductionOrderMapper productionOrderMapper;
	@Autowired
	private ProductionOrderDetailMapper productionOrderDetailMapper;

	@Autowired
	private IRecipeService recipeService;  // 需确认是否存在

	@Autowired
	private IRecipeDetailService recipeDetailService;  // 需确认是否存在

	@Autowired
	private IProcessRoutingService routingService;  // 工艺主表

	@Autowired
	private IProcessRoutingStepService routingStepService;  // 工艺明细

	@Autowired
	private IProductionBatchService batchService;

	@Autowired
	private IProductionMaterialService productionMaterialService;

	@Autowired
	private IProductionTaskService taskService;
	@Autowired
	private IMaterialService materialService;
	@Autowired
	private IProductionOrderDetailService productionOrderDetailService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(ProductionOrder productionOrder, List<ProductionOrderDetail> productionOrderDetailList) {
		productionOrderMapper.insert(productionOrder);
		if(productionOrderDetailList!=null && productionOrderDetailList.size()>0) {
			for(ProductionOrderDetail entity:productionOrderDetailList) {
				//外键设置
				entity.setOrderId(productionOrder.getId());
				productionOrderDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(ProductionOrder productionOrder,List<ProductionOrderDetail> productionOrderDetailList) {
		productionOrderMapper.updateById(productionOrder);
		
		//1.先删除子表数据
		productionOrderDetailMapper.deleteByMainId(productionOrder.getId());
		
		//2.子表数据重新插入
		if(productionOrderDetailList!=null && productionOrderDetailList.size()>0) {
			for(ProductionOrderDetail entity:productionOrderDetailList) {
				//外键设置
				entity.setOrderId(productionOrder.getId());
				productionOrderDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		productionOrderDetailMapper.deleteByMainId(id);
		productionOrderMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			productionOrderDetailMapper.deleteByMainId(id.toString());
			productionOrderMapper.deleteById(id);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void BatchReleaseOrder(String ids) {
		List<String> idlist = Arrays.asList(ids.split(","));
		for(String id:idlist) {
			//1、获得生产订单
			ProductionOrder order = this.getById(id);
			if (order == null || !"0".equals(order.getStatus())) {
				throw new JeecgBootException("订单不存在或状态不正确");
			}

			String recipeId = order.getRecipeId();
			BigDecimal plannedQty = order.getPlannedQty();
			String orderNo = order.getOrderNo();
			String orderId = order.getId();

			//2、获取配方明细（BOM）
			List<RecipeDetail> recipeDetails = recipeDetailService.selectByMainId(recipeId);
			if (CollectionUtils.isEmpty(recipeDetails)) {
				throw new JeecgBootException("配方明细为空");
			}
			// 获取配方主表得到 routing_id
			Recipe recipe = recipeService.getById(recipeId);
			String routingId = recipe.getRoutingId();  // 工艺路线ID
			BigDecimal proportionTotal = recipe.getProportionTotal();

			if (proportionTotal == null || proportionTotal.compareTo(BigDecimal.ZERO) <= 0) {
				proportionTotal = BigDecimal.valueOf(100);
			}
			int batchCount = order.getBatchCount();
			BigDecimal batchSize = order.getBatchSize();
			// 4. 循环生成批次
			List<ProductionMaterial> allMaterials = new ArrayList<>();

			for (int i = 1; i <= batchCount; i++) {
				String batchNo = orderNo + "-" + i;

				// 4.1 创建批次主表
				ProductionBatch batch = new ProductionBatch();
				//batch.setId(UUID.randomUUID().toString());
				batch.setBatchNo(batchNo);
				batch.setOrderId(orderId);
				batch.setOrderNo(orderNo);
				batch.setRecipeId(recipeId);
				batch.setProductCode(order.getProductCode());
				batch.setProductName(order.getProductName());

				// 计算该批次计划数量（最后一批次处理余数）
				BigDecimal batchPlannedQty = (i == batchCount)
						? plannedQty.subtract(batchSize.multiply(BigDecimal.valueOf(batchCount - 1)))
						: batchSize;
				batch.setPlannedQty(batchPlannedQty);
				batch.setStatus("0");

				// 4.2 创建批次BOM子表
				List<ProductionBatchBom> batchBomList = new ArrayList<>();
				for (RecipeDetail recipeDetail : recipeDetails) {
					ProductionBatchBom bom = new ProductionBatchBom();
					//bom.setId(UUID.randomUUID().toString());
					//bom.setBatchId(batch.getId());
					bom.setMaterialCode(recipeDetail.getMaterialCode());
					bom.setMaterialName(recipeDetail.getMaterialName());
					// 计算该批次物料需求 = 批次计划数量 * 配方占比
					BigDecimal materialQty = batchPlannedQty.multiply(recipeDetail.getProportion())
							.divide(proportionTotal, 6, RoundingMode.HALF_UP);
					bom.setPlannedQty(materialQty);
					bom.setUnit(recipeDetail.getUnit());
					batchBomList.add(bom);

					// 累计物料需求（按物料汇总）
					//allMaterials.add(createMaterial(orderId, batch.getId(), recipeDetail, materialQty));
				}

				// 保存批次主子表
				batchService.saveMain(batch, batchBomList);

				// 4.3 生成工序任务（工单）
				String taskNoPrefix = "WO" + batchNo;
				//taskService.generateTasks(batch.getId(), routingId, taskNoPrefix);
			}

			// 5. 批量保存物料需求（按物料合并）
			//saveMergedMaterials(allMaterials);

			// 6. 更新订单状态
			order.setStatus("RELEASED");
			//order.setReleaseTime(new Date());
			this.updateById(order);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void releaseOrder(String id) {
		// 1. 校验并获取订单
		ProductionOrder order = getAndValidateOrder(id);
		String orderId = order.getId();
		String orderNo = order.getOrderNo();
		String recipeId = order.getRecipeId();
		BigDecimal plannedQty = order.getPlannedQty();
		int batchCount = order.getBatchCount();
		BigDecimal batchSize = order.getBatchSize();

		// 2. 获取配方信息
		Recipe recipe = getAndValidateRecipe(recipeId);
		String routingId = recipe.getRoutingId();
		BigDecimal proportionTotal = getValidProportionTotal(recipe.getProportionTotal());

		// 3. 获取配方明细
		List<RecipeDetail> recipeDetails = getAndValidateRecipeDetails(recipeId);

		// 4. 获取并校验包装明细
		List<ProductionOrderDetail> packageDetails = getAndValidatePackageDetails(orderId);

		// 5. 预计算每个批次的包装分配方案
		Map<Integer, List<BatchPackageAllocation>> batchPackagePlan = calculatePackageAllocation(
				packageDetails, batchCount, batchSize, plannedQty);

		// 6. 循环生成批次及关联数据
		for (int i = 1; i <= batchCount; i++) {
			String batchNo = generateBatchNo(orderNo, i);
			BigDecimal batchPlannedQty = calculateBatchPlannedQty(i, batchCount, batchSize, plannedQty);
			List<BatchPackageAllocation> allocations = batchPackagePlan.getOrDefault(i, Collections.emptyList());

			// 6.1 创建批次
			ProductionBatch batch = createBatch(order, recipe, batchNo, i, batchPlannedQty);

			// 6.2 创建批次BOM
			List<ProductionBatchBom> batchBomList = createBatchBoms(recipeDetails, batchPlannedQty, proportionTotal);
			batchService.saveMain(batch, batchBomList);

			// 6.3 保存原料物料需求
			saveRawMaterials(order, batch, batchNo, batchBomList);

			// 6.4 保存包装物料需求
			savePackageMaterials(order, batch, batchNo, allocations);

			// 6.5 生成工序任务（传入包装信息字符串）
			String packageInfo = buildPackageInfoString(allocations);
			taskService.generateTasks(batch, routingId, packageInfo);
		}

		// 7. 更新订单状态
		updateOrderReleased(order);
	}

	/**
	 * 创建物料需求对象
	 */
	private ProductionMaterial createMaterial(String orderId, String orderNo, String batchId,String batchNo,
											  RecipeDetail recipeDetail, BigDecimal qty) {
		ProductionMaterial material = new ProductionMaterial();
		material.setId(UUID.randomUUID().toString());
		material.setOrderId(orderId);
		material.setOrderNo(orderNo);
		material.setBatchId(batchId);
		material.setBatchNo(batchNo);
		material.setMaterialCode(recipeDetail.getMaterialCode());
		material.setMaterialName(recipeDetail.getMaterialName());
		material.setRequiredQty(qty);
		material.setUnit(recipeDetail.getUnit());
		material.setStatus("0");
		return material;
	}

	/**
	 * 获取并校验订单
	 */
	private ProductionOrder getAndValidateOrder(String id) {
		ProductionOrder order = this.getById(id);
		if (order == null || !"0".equals(order.getStatus())) {
			throw new JeecgBootException("订单不存在或状态不正确");
		}
		return order;
	}

	/**
	 * 获取并校验配方
	 */
	private Recipe getAndValidateRecipe(String recipeId) {
		Recipe recipe = recipeService.getById(recipeId);
		if (recipe == null) {
			throw new JeecgBootException("配方不存在");
		}
		return recipe;
	}

	/**
	 * 获取有效的总占比
	 */
	private BigDecimal getValidProportionTotal(BigDecimal proportionTotal) {
		if (proportionTotal == null || proportionTotal.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.valueOf(100);
		}
		return proportionTotal;
	}

	/**
	 * 获取并校验配方明细
	 */
	private List<RecipeDetail> getAndValidateRecipeDetails(String recipeId) {
		List<RecipeDetail> details = recipeDetailService.selectByMainId(recipeId);
		if (CollectionUtils.isEmpty(details)) {
			throw new JeecgBootException("配方明细为空");
		}
		return details;
	}

	/**
	 * 获取并校验包装明细
	 */
	private List<ProductionOrderDetail> getAndValidatePackageDetails(String orderId) {
		List<ProductionOrderDetail> orderDetails = productionOrderDetailService.selectByMainId(orderId);

		List<ProductionOrderDetail> packageDetails = orderDetails.stream()
				.filter(d -> StringUtils.isNotBlank(d.getInnerPackageId())
						&& d.getInnerPackageCapacity() != null
						&& d.getInnerPackageCapacity().compareTo(BigDecimal.ZERO) > 0
						&& d.getAllocatedQty() != null
						&& d.getAllocatedQty().compareTo(BigDecimal.ZERO) > 0)
				.sorted((d1, d2) -> d2.getInnerPackageCapacity().compareTo(d1.getInnerPackageCapacity()))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(packageDetails)) {
			throw new JeecgBootException("订单未配置包装信息");
		}
		return packageDetails;
	}

	/**
	 * 生成批次号
	 */
	private String generateBatchNo(String orderNo, int seq) {
		return orderNo + "-" + String.format("%02d", seq);
	}

	/**
	 * 计算批次计划数量
	 */
	private BigDecimal calculateBatchPlannedQty(int currentSeq, int totalCount, BigDecimal batchSize, BigDecimal totalQty) {
		if (currentSeq == totalCount) {
			// 最后一批次：处理余数
			return totalQty.subtract(batchSize.multiply(BigDecimal.valueOf(totalCount - 1)));
		}
		return batchSize;
	}

	/**
	 * 创建批次主表
	 */
	private ProductionBatch createBatch(ProductionOrder order, Recipe recipe,
										String batchNo, int seq, BigDecimal plannedQty) {
		ProductionBatch batch = new ProductionBatch();
		batch.setBatchSeq(seq);
		batch.setBatchNo(batchNo);
		batch.setOrderId(order.getId());
		batch.setOrderNo(order.getOrderNo());
		batch.setRecipeId(recipe.getId());
		batch.setRecipeCode(order.getRecipeCode());
		batch.setRecipeName(order.getRecipeName());
		batch.setRecipeVersion(order.getRecipeVersion());
		batch.setProductId(order.getProductId());
		batch.setProductCode(order.getProductCode());
		batch.setProductName(order.getProductName());
		batch.setProductColor(order.getProductColor());
		batch.setPlannedQty(plannedQty);
		batch.setShelfLife(order.getShelfLife());
		batch.setCompanyId(order.getCompanyId());
		batch.setCompanyName(order.getCompanyName());
		batch.setProductionDate(order.getReleaseTime());

		if(batch.getProductionDate() != null && batch.getShelfLife() != null){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(batch.getProductionDate());
			calendar.add(Calendar.DATE, batch.getShelfLife());
			batch.setExpiryDate(calendar.getTime()); //设置失效日期
		}

		batch.setStatus("0");
		return batch;
	}

	/**
	 * 创建批次BOM列表
	 */
	private List<ProductionBatchBom> createBatchBoms(List<RecipeDetail> recipeDetails,
													 BigDecimal batchPlannedQty,
													 BigDecimal proportionTotal) {
		List<ProductionBatchBom> list = new ArrayList<>();
		for (RecipeDetail detail : recipeDetails) {
			ProductionBatchBom bom = new ProductionBatchBom();
			bom.setSerialNo(detail.getSerialNo());
			bom.setMaterialId(detail.getMaterialId());
			bom.setMaterialCode(detail.getMaterialCode());
			bom.setMaterialName(detail.getMaterialName());
			bom.setMaterialSpec(detail.getMaterialSpec());
			bom.setProportion(detail.getProportion());
			bom.setUnit(detail.getUnit());

			// 计算物料需求：(批次数量 × 配比) / 总占比
			BigDecimal materialQty = batchPlannedQty.multiply(detail.getProportion())
					.divide(proportionTotal, 3, RoundingMode.HALF_UP);
			bom.setPlannedQty(materialQty);
			list.add(bom);
		}
		return list;
	}

	/**
	 * 保存原料物料需求
	 */
	private void saveRawMaterials(ProductionOrder order, ProductionBatch batch,
								  String batchNo, List<ProductionBatchBom> boms) {
		for (ProductionBatchBom bom : boms) {
			ProductionMaterial material = new ProductionMaterial();
			material.setOrderId(order.getId());
			material.setOrderNo(order.getOrderNo());
			material.setBatchId(batch.getId());
			material.setBatchNo(batchNo);
			material.setMaterialId(bom.getMaterialId());
			material.setMaterialCode(bom.getMaterialCode());
			material.setMaterialName(bom.getMaterialName());
			material.setMaterialSpec(bom.getMaterialSpec());
			material.setRequiredQty(bom.getPlannedQty());
			material.setRemainingQty(bom.getPlannedQty());
			material.setUnit(bom.getUnit());
			material.setMaterialType("0");
			material.setStatus("0");
			productionMaterialService.save(material);
		}
	}

	/**
	 * 保存包装物料需求
	 */
	private void savePackageMaterials(ProductionOrder order, ProductionBatch batch,
									  String batchNo, List<BatchPackageAllocation> allocations) {
		for (BatchPackageAllocation alloc : allocations) {
			// 内包装
			saveInnerPackageMaterial(order, batch, batchNo, alloc);

			// 外包装
			if (StringUtils.isNotBlank(alloc.getOuterPackageId()) && alloc.getCalculatedOuterQty() > 0) {
				saveOuterPackageMaterial(order, batch, batchNo, alloc);
			}
		}
	}

	/**
	 * 保存内包装物料
	 */
	private void saveInnerPackageMaterial(ProductionOrder order, ProductionBatch batch,
										  String batchNo, BatchPackageAllocation alloc) {
		Material materialEntity = materialService.getById(alloc.getInnerPackageId());
		if (materialEntity == null) {
			throw new JeecgBootException("内包装物料不存在：" + alloc.getInnerPackageId());
		}

		ProductionMaterial innerMaterial = new ProductionMaterial();
		innerMaterial.setOrderId(order.getId());
		innerMaterial.setOrderNo(order.getOrderNo());
		innerMaterial.setBatchId(batch.getId());
		innerMaterial.setBatchNo(batchNo);
		innerMaterial.setMaterialId(alloc.getInnerPackageId());
		innerMaterial.setMaterialCode(materialEntity.getMaterialCode());
		innerMaterial.setMaterialName(materialEntity.getMaterialName());
		innerMaterial.setMaterialSpec(alloc.getInnerPackageSpec());
		innerMaterial.setRequiredQty(BigDecimal.valueOf(alloc.getAllocatedInnerQty()));
		innerMaterial.setRemainingQty(BigDecimal.valueOf(alloc.getAllocatedInnerQty()));
		innerMaterial.setUnit(alloc.getInnerPackageUnit());
		innerMaterial.setMaterialType("1");
		innerMaterial.setStatus("0");
		innerMaterial.setOrderDetailId(alloc.getOrderDetailId());
		productionMaterialService.save(innerMaterial);
	}

	/**
	 * 保存外包装物料
	 */
	private void saveOuterPackageMaterial(ProductionOrder order, ProductionBatch batch,
										  String batchNo, BatchPackageAllocation alloc) {
		Material materialEntity = materialService.getById(alloc.getOuterPackageId());
		if (materialEntity == null) {
			throw new JeecgBootException("外包装物料不存在：" + alloc.getOuterPackageId());
		}

		ProductionMaterial outerMaterial = new ProductionMaterial();
		outerMaterial.setOrderId(order.getId());
		outerMaterial.setOrderNo(order.getOrderNo());
		outerMaterial.setBatchId(batch.getId());
		outerMaterial.setBatchNo(batchNo);
		outerMaterial.setMaterialId(alloc.getOuterPackageId());
		outerMaterial.setMaterialCode(materialEntity.getMaterialCode());
		outerMaterial.setMaterialName(materialEntity.getMaterialName());
		outerMaterial.setMaterialSpec(alloc.getInnerPackageSpec());
		outerMaterial.setRequiredQty(BigDecimal.valueOf(alloc.getCalculatedOuterQty()));
		outerMaterial.setRemainingQty(BigDecimal.valueOf(alloc.getCalculatedOuterQty()));
		outerMaterial.setUnit(alloc.getOuterPackageUnit());
		outerMaterial.setMaterialType("OUTER_PACK");
		outerMaterial.setStatus("0");
		outerMaterial.setOrderDetailId(alloc.getOrderDetailId());
		productionMaterialService.save(outerMaterial);
	}

	/**
	 * 构建包装信息字符串（用于工序任务描述）
	 */
	private String buildPackageInfoString(List<BatchPackageAllocation> allocations) {
		if (CollectionUtils.isEmpty(allocations)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("【包装要求】\n");

		int num = 1;
		for (BatchPackageAllocation alloc : allocations) {
			sb.append(num).append(". ");
			sb.append(alloc.getInnerPackageSpec())
					.append("×").append(alloc.getAllocatedInnerQty())
					.append(alloc.getInnerPackageUnit());

			if (StringUtils.isNotBlank(alloc.getOuterPackageId()) && alloc.getCalculatedOuterQty() > 0) {
				sb.append(" → ")
						.append(alloc.getOuterPackageSpec())
						.append("×").append(alloc.getCalculatedOuterQty())
						.append(alloc.getOuterPackageUnit());
			}
			sb.append("\n");
			num++;
		}

		return sb.toString();
	}

	/**
	 * 更新订单为已下达状态
	 */
	private void updateOrderReleased(ProductionOrder order) {
		order.setStatus("1");
		order.setReleaseTime(new Date());
		this.updateById(order);
	}

	/**
	 * 核心算法：大规格优先分配，允许混装
	 * 存在平分后进位多包装的问题
	 */
	private Map<Integer, List<BatchPackageAllocation>> calculatePackageAllocation1(
			List<ProductionOrderDetail> sortedDetails,
			int batchCount,
			BigDecimal batchSize,
			BigDecimal totalQty) {

		Map<Integer, List<BatchPackageAllocation>> result = new HashMap<>();
		Map<Integer, BigDecimal> batchRemaining = new HashMap<>();

		// 初始化批次容量
		BigDecimal remaining = totalQty;
		for (int i = 1; i <= batchCount; i++) {
			BigDecimal currentBatchSize = (i == batchCount)
					? remaining
					: batchSize.min(remaining);
			batchRemaining.put(i, currentBatchSize);
			remaining = remaining.subtract(currentBatchSize);
			result.put(i, new ArrayList<>());
		}

		// 按规格从大到小处理（大规格优先）
		for (ProductionOrderDetail detail : sortedDetails) {
			BigDecimal capacity = detail.getInnerPackageCapacity();
			BigDecimal detailQty = detail.getAllocatedQty();
			Integer innerPerOuter = detail.getInnerPerOuter();

			if (capacity == null || capacity.compareTo(BigDecimal.ZERO) <= 0) continue;

			// 计算该明细需要的总内包数
			int totalPackages = detailQty.divide(capacity, 0, RoundingMode.CEILING).intValue();
			int remainingPackages = totalPackages;

			// 从第1个批次开始，能装就装（贪心）
			for (int batchSeq = 1; batchSeq <= batchCount && remainingPackages > 0; batchSeq++) {
				BigDecimal space = batchRemaining.getOrDefault(batchSeq, BigDecimal.ZERO);

				if (space.compareTo(capacity) < 0) continue;

				// 计算能装多少个
				int maxFit = space.divide(capacity, 0, RoundingMode.FLOOR).intValue();
				int allocate = Math.min(maxFit, remainingPackages);

				if (allocate > 0) {
					// 创建分配记录
					BatchPackageAllocation alloc = new BatchPackageAllocation();
					alloc.setOrderDetailId(detail.getId());
					alloc.setInnerPackageId(detail.getInnerPackageId());
					alloc.setInnerPackageSpec(detail.getInnerPackageSpec());
					alloc.setInnerPackageCapacity(detail.getInnerPackageCapacity());
					alloc.setInnerPackageUnit(detail.getInnerPackageUnit());
					alloc.setOuterPackageId(detail.getOuterPackageId());
					alloc.setOuterPackageSpec(detail.getOuterPackageSpec());
					alloc.setOuterPackageUnit(detail.getOuterPackageUnit());
					alloc.setInnerPerOuter(innerPerOuter);
					alloc.setAllocatedInnerQty(allocate);
					alloc.setCalculatedOuterQty((int) Math.ceil((double) allocate / innerPerOuter));

					result.get(batchSeq).add(alloc);

					// 更新剩余
					remainingPackages -= allocate;
					BigDecimal used = capacity.multiply(BigDecimal.valueOf(allocate));
					batchRemaining.put(batchSeq, space.subtract(used));
				}
			}

			// 如果还有剩余，强制放到最后一个批次
			if (remainingPackages > 0) {
				int lastBatch = batchCount;
				BigDecimal lastSpace = batchRemaining.getOrDefault(lastBatch, BigDecimal.ZERO);

				// 创建分配记录
				BatchPackageAllocation alloc = new BatchPackageAllocation();
				alloc.setOrderDetailId(detail.getId());
				alloc.setInnerPackageId(detail.getInnerPackageId());
				alloc.setInnerPackageSpec(detail.getInnerPackageSpec());
				alloc.setInnerPackageCapacity(detail.getInnerPackageCapacity());
				alloc.setInnerPackageUnit(detail.getInnerPackageUnit());
				alloc.setOuterPackageId(detail.getOuterPackageId());
				alloc.setOuterPackageSpec(detail.getOuterPackageSpec());
				alloc.setOuterPackageUnit(detail.getOuterPackageUnit());
				alloc.setInnerPerOuter(innerPerOuter);
				alloc.setAllocatedInnerQty(remainingPackages);
				alloc.setCalculatedOuterQty((int) Math.ceil((double) remainingPackages / innerPerOuter));

				result.get(lastBatch).add(alloc);

				BigDecimal used = capacity.multiply(BigDecimal.valueOf(remainingPackages));
				batchRemaining.put(lastBatch, lastSpace.subtract(used));
			}
		}

		return result;
	}

	/**
	 * 核心算法：大规格优先分配，允许混装
	 * 策略：余数前置 - 外包装总数先算好，余数放在前面批次
	 */
	private Map<Integer, List<BatchPackageAllocation>> calculatePackageAllocation(
			List<ProductionOrderDetail> sortedDetails,
			int batchCount,
			BigDecimal batchSize,
			BigDecimal totalQty) {

		Map<Integer, List<BatchPackageAllocation>> result = new HashMap<>();
		Map<Integer, BigDecimal> batchRemaining = new HashMap<>();

		// 初始化批次容量
		BigDecimal remaining = totalQty;
		for (int i = 1; i <= batchCount; i++) {
			BigDecimal currentBatchSize = (i == batchCount)
					? remaining
					: batchSize.min(remaining);
			batchRemaining.put(i, currentBatchSize);
			remaining = remaining.subtract(currentBatchSize);
			result.put(i, new ArrayList<>());
		}

		// 按规格从大到小处理（大规格优先）
		for (ProductionOrderDetail detail : sortedDetails) {
			BigDecimal capacity = detail.getInnerPackageCapacity();
			BigDecimal detailQty = detail.getAllocatedQty();
			Integer innerPerOuter = detail.getInnerPerOuter();

			if (capacity == null || capacity.compareTo(BigDecimal.ZERO) <= 0
					|| innerPerOuter == null || innerPerOuter <= 0) continue;

			// ========== 步骤1：计算该规格总量 ==========
			// 总内包数（向上取整）
			int totalInnerPackages = detailQty.divide(capacity, 0, RoundingMode.CEILING).intValue();
			// 总外包数（向上取整）- 这是正确的总量！
			int totalOuterPackages = (int) Math.ceil((double) totalInnerPackages / innerPerOuter);

			// ========== 步骤2：按批次分配内包（贪心策略）==========
			// 记录每个批次分配的内包数
			Map<Integer, Integer> batchInnerCount = new HashMap<>();
			int remainingPackages = totalInnerPackages;

			for (int batchSeq = 1; batchSeq <= batchCount && remainingPackages > 0; batchSeq++) {
				BigDecimal space = batchRemaining.getOrDefault(batchSeq, BigDecimal.ZERO);

				if (space.compareTo(capacity) < 0) continue;

				int maxFit = space.divide(capacity, 0, RoundingMode.FLOOR).intValue();
				int allocate = Math.min(maxFit, remainingPackages);

				if (allocate > 0) {
					batchInnerCount.put(batchSeq, allocate);
					remainingPackages -= allocate;
					BigDecimal used = capacity.multiply(BigDecimal.valueOf(allocate));
					batchRemaining.put(batchSeq, space.subtract(used));
				}
			}

			// 强制放到最后一个批次（如果有剩余）
			if (remainingPackages > 0) {
				int lastBatch = batchCount;
				batchInnerCount.put(lastBatch,
						batchInnerCount.getOrDefault(lastBatch, 0) + remainingPackages);

				BigDecimal lastSpace = batchRemaining.getOrDefault(lastBatch, BigDecimal.ZERO);
				BigDecimal used = capacity.multiply(BigDecimal.valueOf(remainingPackages));
				batchRemaining.put(lastBatch, lastSpace.subtract(used));
			}

			// ========== 步骤3：余数前置策略分配外包 ==========
			// 计算基础分配和余数
			int baseOuter = totalOuterPackages / batchInnerCount.size();  // 每个批次基础数量
			int remainder = totalOuterPackages % batchInnerCount.size();  // 余数（给前面批次）

			// 按批次顺序排序
			List<Integer> batchSeqs = new ArrayList<>(batchInnerCount.keySet());
			Collections.sort(batchSeqs);

			// 分配外包（前remainder个批次多分1个）
			for (int i = 0; i < batchSeqs.size(); i++) {
				int batchSeq = batchSeqs.get(i);
				int innerCount = batchInnerCount.get(batchSeq);

				// 前 remainder 个批次：baseOuter + 1，其余：baseOuter
				int outerCount = baseOuter + (i < remainder ? 1 : 0);

				// 创建分配记录
				BatchPackageAllocation alloc = new BatchPackageAllocation();
				alloc.setOrderDetailId(detail.getId());
				alloc.setInnerPackageId(detail.getInnerPackageId());
				alloc.setInnerPackageSpec(detail.getInnerPackageSpec());
				alloc.setInnerPackageCapacity(detail.getInnerPackageCapacity());
				alloc.setInnerPackageUnit(detail.getInnerPackageUnit());
				alloc.setOuterPackageId(detail.getOuterPackageId());
				alloc.setOuterPackageSpec(detail.getOuterPackageSpec());
				alloc.setOuterPackageUnit(detail.getOuterPackageUnit());
				alloc.setInnerPerOuter(innerPerOuter);
				alloc.setAllocatedInnerQty(innerCount);
				alloc.setCalculatedOuterQty(outerCount);

				result.get(batchSeq).add(alloc);
			}
		}

		return result;
	}
	/**
	 * 核心算法：大规格优先分配，允许混装
	 * 修复：外包装按总量计算，避免重复进位 余数后置
	 */
	private Map<Integer, List<BatchPackageAllocation>> calculatePackageAllocation_after(
			List<ProductionOrderDetail> sortedDetails,
			int batchCount,
			BigDecimal batchSize,
			BigDecimal totalQty) {

		Map<Integer, List<BatchPackageAllocation>> result = new HashMap<>();
		Map<Integer, BigDecimal> batchRemaining = new HashMap<>();

		// 初始化批次容量
		BigDecimal remaining = totalQty;
		for (int i = 1; i <= batchCount; i++) {
			BigDecimal currentBatchSize = (i == batchCount)
					? remaining
					: batchSize.min(remaining);
			batchRemaining.put(i, currentBatchSize);
			remaining = remaining.subtract(currentBatchSize);
			result.put(i, new ArrayList<>());
		}

		// 按规格从大到小处理（大规格优先）
		for (ProductionOrderDetail detail : sortedDetails) {
			BigDecimal capacity = detail.getInnerPackageCapacity();
			BigDecimal detailQty = detail.getAllocatedQty();
			Integer innerPerOuter = detail.getInnerPerOuter();

			if (capacity == null || capacity.compareTo(BigDecimal.ZERO) <= 0
					|| innerPerOuter == null || innerPerOuter <= 0) continue;

			// ========== 关键修复：先算该规格总量 ==========
			// 总内包数（向上取整）
			int totalInnerPackages = detailQty.divide(capacity, 0, RoundingMode.CEILING).intValue();
			// 总外包数（向上取整）- 这是正确的总量！
			int totalOuterPackages = (int) Math.ceil((double) totalInnerPackages / innerPerOuter);

			// 记录每个批次分配的内包数，用于后续分配外包
			Map<Integer, Integer> batchInnerCount = new HashMap<>();
			int allocatedInnerTotal = 0;

			// 从第1个批次开始，能装就装（贪心分配内包）
			int remainingPackages = totalInnerPackages;
			for (int batchSeq = 1; batchSeq <= batchCount && remainingPackages > 0; batchSeq++) {
				BigDecimal space = batchRemaining.getOrDefault(batchSeq, BigDecimal.ZERO);

				if (space.compareTo(capacity) < 0) continue;

				int maxFit = space.divide(capacity, 0, RoundingMode.FLOOR).intValue();
				int allocate = Math.min(maxFit, remainingPackages);

				if (allocate > 0) {
					batchInnerCount.put(batchSeq, allocate);
					allocatedInnerTotal += allocate;

					remainingPackages -= allocate;
					BigDecimal used = capacity.multiply(BigDecimal.valueOf(allocate));
					batchRemaining.put(batchSeq, space.subtract(used));
				}
			}

			// 强制放到最后一个批次（如果有剩余）
			if (remainingPackages > 0) {
				int lastBatch = batchCount;
				batchInnerCount.put(lastBatch,
						batchInnerCount.getOrDefault(lastBatch, 0) + remainingPackages);
				allocatedInnerTotal += remainingPackages;

				BigDecimal lastSpace = batchRemaining.getOrDefault(lastBatch, BigDecimal.ZERO);
				BigDecimal used = capacity.multiply(BigDecimal.valueOf(remainingPackages));
				batchRemaining.put(lastBatch, lastSpace.subtract(used));
				remainingPackages = 0;
			}

			// ========== 关键修复：分配外包（只有最后一个批次进位）==========
			int allocatedOuterTotal = 0;
			List<Integer> batchSeqs = new ArrayList<>(batchInnerCount.keySet());
			Collections.sort(batchSeqs); // 确保按批次顺序处理

			for (int i = 0; i < batchSeqs.size(); i++) {
				int batchSeq = batchSeqs.get(i);
				int innerCount = batchInnerCount.get(batchSeq);
				int outerCount;

				if (i == batchSeqs.size() - 1) {
					// 最后一个批次：用总量减去前面已分配的（确保不超额）
					outerCount = totalOuterPackages - allocatedOuterTotal;
				} else {
					// 前面的批次：按内包数比例分配，不进位（向下取整）
					outerCount = innerCount / innerPerOuter;
					allocatedOuterTotal += outerCount;
				}

				// 创建分配记录
				BatchPackageAllocation alloc = new BatchPackageAllocation();
				alloc.setOrderDetailId(detail.getId());
				alloc.setInnerPackageId(detail.getInnerPackageId());
				alloc.setInnerPackageSpec(detail.getInnerPackageSpec());
				alloc.setInnerPackageCapacity(detail.getInnerPackageCapacity());
				alloc.setInnerPackageUnit(detail.getInnerPackageUnit());
				alloc.setOuterPackageId(detail.getOuterPackageId());
				alloc.setOuterPackageSpec(detail.getOuterPackageSpec());
				alloc.setOuterPackageUnit(detail.getOuterPackageUnit());
				alloc.setInnerPerOuter(innerPerOuter);
				alloc.setAllocatedInnerQty(innerCount);
				alloc.setCalculatedOuterQty(outerCount);

				result.get(batchSeq).add(alloc);
			}
		}

		return result;
	}
	/**
	 * 创建包装分配对象
	 */
	private BatchPackageAllocation createAllocation(ProductionOrderDetail detail,
													int innerQty,
													Integer innerPerOuter) {
		BatchPackageAllocation alloc = new BatchPackageAllocation();
		alloc.setOrderDetailId(detail.getId());
		alloc.setInnerPackageId(detail.getInnerPackageId());
		alloc.setInnerPackageSpec(detail.getInnerPackageSpec());
		alloc.setInnerPackageCapacity(detail.getInnerPackageCapacity());
		alloc.setInnerPackageUnit(detail.getInnerPackageUnit());
		alloc.setOuterPackageId(detail.getOuterPackageId());
		alloc.setOuterPackageSpec(detail.getOuterPackageSpec());
		alloc.setOuterPackageUnit(detail.getOuterPackageUnit());
		alloc.setInnerPerOuter(innerPerOuter);
		alloc.setAllocatedInnerQty(innerQty);
		alloc.setCalculatedOuterQty((int) Math.ceil((double) innerQty / innerPerOuter));
		return alloc;
	}

}

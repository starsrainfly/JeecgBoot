package org.jeecg.modules.mes.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.utils.SerialNoUtils;
import org.jeecg.modules.mdm.entity.Recipe;
import org.jeecg.modules.mdm.entity.RecipeDetail;
import org.jeecg.modules.mdm.service.IProcessRoutingService;
import org.jeecg.modules.mdm.service.IProcessRoutingStepService;
import org.jeecg.modules.mdm.service.IRecipeDetailService;
import org.jeecg.modules.mdm.service.IRecipeService;
import org.jeecg.modules.mdm.service.impl.RecipeDetailServiceImpl;
import org.jeecg.modules.mes.entity.*;
import org.jeecg.modules.mes.mapper.ProductionOrderDetailMapper;
import org.jeecg.modules.mes.mapper.ProductionOrderMapper;
import org.jeecg.modules.mes.service.IProductionBatchService;
import org.jeecg.modules.mes.service.IProductionMaterialService;
import org.jeecg.modules.mes.service.IProductionOrderService;
import org.jeecg.modules.mes.service.IProductionTaskService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;

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
	private IProductionMaterialService materialService;

	@Autowired
	private IProductionTaskService taskService;
	
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
	public void releaseOrder(String ids) {
		List<String> idlist = Arrays.asList(ids.split(","));
		for(String id:idlist) {
			//1、获得生产订单
			ProductionOrder order = this.getById(id);
			if (order == null || !"CREATED".equals(order.getStatus())) {
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
				batch.setStatus("CREATED");

				// 4.2 创建批次BOM子表
				List<ProductionBatchBom> batchBomList = new ArrayList<>();
				for (RecipeDetail recipeDetail : recipeDetails) {
					ProductionBatchBom bom = new ProductionBatchBom();
					//bom.setId(UUID.randomUUID().toString());
					//bom.setBatchId(batch.getId());
					bom.setMaterialCode(recipeDetail.getMaterialCode());
					bom.setMaterialName(recipeDetail.getMaterialName());
					// 计算该批次物料需求 = 批次计划数量 * 配方占比
					BigDecimal materialQty = batchPlannedQty.multiply(recipeDetail.getProportion());
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

}

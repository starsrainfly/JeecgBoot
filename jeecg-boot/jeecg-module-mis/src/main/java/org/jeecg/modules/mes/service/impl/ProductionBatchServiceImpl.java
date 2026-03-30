package org.jeecg.modules.mes.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.mes.entity.ProductionBatch;
import org.jeecg.modules.mes.entity.ProductionBatchBom;
import org.jeecg.modules.mes.entity.ProductionBatchMaterialActual;
import org.jeecg.modules.mes.mapper.ProductionBatchBomMapper;
import org.jeecg.modules.mes.mapper.ProductionBatchMapper;
import org.jeecg.modules.mes.mapper.ProductionBatchMaterialActualMapper;
import org.jeecg.modules.mes.service.IProductionBatchService;
import org.jeecg.modules.mes.vo.ProductionBatchPage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 生产批次
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Service
public class ProductionBatchServiceImpl extends ServiceImpl<ProductionBatchMapper, ProductionBatch> implements IProductionBatchService {

	@Autowired
	private ProductionBatchMapper productionBatchMapper;
	@Autowired
	private ProductionBatchBomMapper productionBatchBomMapper;

	@Autowired
	private ProductionBatchMaterialActualMapper productionBatchMaterialActualMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(ProductionBatch productionBatch, List<ProductionBatchBom> productionBatchBomList) {
		productionBatchMapper.insert(productionBatch);
		if(productionBatchBomList!=null && productionBatchBomList.size()>0) {
			for(ProductionBatchBom entity:productionBatchBomList) {
				//外键设置
				entity.setBatchId(productionBatch.getId());
				productionBatchBomMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(ProductionBatch productionBatch,List<ProductionBatchBom> productionBatchBomList) {
		productionBatchMapper.updateById(productionBatch);
		
		//1.先删除子表数据
		productionBatchBomMapper.deleteByMainId(productionBatch.getId());
		
		//2.子表数据重新插入
		if(productionBatchBomList!=null && productionBatchBomList.size()>0) {
			for(ProductionBatchBom entity:productionBatchBomList) {
				//外键设置
				entity.setBatchId(productionBatch.getId());
				productionBatchBomMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		productionBatchBomMapper.deleteByMainId(id);
		productionBatchMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			productionBatchBomMapper.deleteByMainId(id.toString());
			productionBatchMapper.deleteById(id);
		}
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setStatus(String id, String status) {
		ProductionBatch productionBatch = productionBatchMapper.selectById(id);
		productionBatch.setStatus(status);
		productionBatchMapper.updateById(productionBatch);
	}


	/**
	 * 检查配料是否完成
	 * 判断逻辑：所有物料的实际称重总和 >= 计划需求数量（允许0.01kg误差）
	 */
	private boolean checkWeighingCompleted(String batchId) {
		// 1. 查询该批次所有物料清单
		List<ProductionBatchBom> bomList = productionBatchBomMapper.selectList(
				new LambdaQueryWrapper<ProductionBatchBom>()
						.eq(ProductionBatchBom::getBatchId, batchId)
		);

		// 没有物料清单，认为不需要配料，直接完成
		if (CollUtil.isEmpty(bomList)) {
			return true;
		}

		// 2. 查询所有称重记录
		List<ProductionBatchMaterialActual> actualList = productionBatchMaterialActualMapper.selectList(
				new LambdaQueryWrapper<ProductionBatchMaterialActual>()
						.eq(ProductionBatchMaterialActual::getBatchId, batchId)
		);

		// 3. 逐个物料检查是否完成
		for (ProductionBatchBom bom : bomList) {
			// 计算该物料的实际称重总和
			BigDecimal actualQty = actualList.stream()
					.filter(a -> bom.getId().equals(a.getBatchBomId()))
					.map(ProductionBatchMaterialActual::getActualQty)
					.filter(Objects::nonNull)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			// 计划需求数量
			BigDecimal plannedQty = bom.getPlannedQty();
			if (plannedQty == null) {
				plannedQty = BigDecimal.ZERO;
			}

			// 判断：实际 < 计划 - 0.01，则认为未完成
			// 允许0.01kg的误差（浮点数精度问题）
			if (actualQty.compareTo(plannedQty.subtract(new BigDecimal("0.01"))) < 0) {
				return false; // 有物料未完成
			}
		}

		// 所有物料都完成
		return true;
	}

	/**
	 * 更新配料状态 - 嵌入到统一状态流
	 */
	@Override
	@Transactional
	public void updateBatchStatus(String batchId) {
		ProductionBatch batch = this.getById(batchId);
		if (batch == null) return;

		String currentStatus = batch.getStatus();

		// 只有在待配料或配料中状态才更新配料相关
		if (!Arrays.asList("PENDING", "WEIGHING").contains(currentStatus)) {
			return; // 已开工或已完成，不再更新配料状态
		}

		// 查询称重记录
		List<ProductionBatchMaterialActual> actualList = productionBatchMaterialActualMapper.selectList(
				new LambdaQueryWrapper<ProductionBatchMaterialActual>()
						.eq(ProductionBatchMaterialActual::getBatchId, batchId)
						.eq(ProductionBatchMaterialActual::getDelFlag, "0")  // 只查未删除的
		);

		// 第一次称重
		if ("PENDING".equals(currentStatus) && !actualList.isEmpty()) {
			batch.setStatus("WEIGHING");
			// 记录第一次称重时间
			Date startTime = actualList.stream()
					.map(ProductionBatchMaterialActual::getCreateTime)
					.filter(Objects::nonNull)
					.min(Date::compareTo)
					.orElse(new Date());
			batch.setWeighingStartTime(startTime);
		}

		// 检查配料是否完成
		boolean weighingCompleted = checkWeighingCompleted(batchId);

		if (weighingCompleted && "WEIGHING".equals(batch.getStatus())) {
			batch.setStatus("WEIGHED");
			batch.setWeighingEndTime(new Date());

			// 更新 actualQty = 总投料量
			BigDecimal totalWeight = actualList.stream()
					.map(ProductionBatchMaterialActual::getActualQty)
					.filter(Objects::nonNull)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			batch.setActualQty(totalWeight);
		}

		this.updateById(batch);
	}

	/**
	 * 实时计算配料汇总
	 */
	private void calculateWeighingSummary(ProductionBatchPage page, String batchId) {
		// 1. 查询物料清单
		List<ProductionBatchBom> bomList = productionBatchBomMapper.selectList(
				new LambdaQueryWrapper<ProductionBatchBom>()
						.eq(ProductionBatchBom::getBatchId, batchId)
		);

		// 2. 查询称重记录
		List<ProductionBatchMaterialActual> actualList = productionBatchMaterialActualMapper.selectList(
				new LambdaQueryWrapper<ProductionBatchMaterialActual>()
						.eq(ProductionBatchMaterialActual::getBatchId, batchId)
		);

		// 3. 计算汇总
		int totalBom = bomList.size();
		int completedBom = 0;
		BigDecimal totalActualWeight = BigDecimal.ZERO;

		for (ProductionBatchBom bom : bomList) {
			BigDecimal actualQty = actualList.stream()
					.filter(a -> bom.getId().equals(a.getBatchBomId()))
					.map(ProductionBatchMaterialActual::getActualQty)
					.filter(Objects::nonNull)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			totalActualWeight = totalActualWeight.add(actualQty);

			// 判断是否完成（实际 >= 计划，允许0.01kg误差）
			if (actualQty.compareTo(bom.getPlannedQty()) >= -0.01) {
				completedBom++;
			}
		}

		// 4. 设置计算字段
		page.setTotalBom(totalBom);
		page.setCompletedBom(completedBom);
		page.setPercent(totalBom == 0 ? 0 : (completedBom * 100 / totalBom));
		page.setTotalActualWeight(totalActualWeight);

		// 5. 如果数据库中没有 weighingStatus，根据计算结果推断
		if (page.getWeighingStatus() == null) {
			if (completedBom == 0) {
				page.setWeighingStatus("PENDING");
			} else if (completedBom == totalBom) {
				page.setWeighingStatus("WEIGHED");
			} else {
				page.setWeighingStatus("WEIGHING");
			}
		}
	}

	/**
	 * 查询列表 - 实时计算配料进度
	 */
	@Override
	public List<ProductionBatchPage> queryPageWeighingProgressList(QueryWrapper<ProductionBatch> queryWrapper) {
		List<ProductionBatch> batchList = this.list(queryWrapper);

		List<ProductionBatchPage> result = new ArrayList<>();
		for (ProductionBatch batch : batchList) {
			ProductionBatchPage page = new ProductionBatchPage();
			BeanUtils.copyProperties(batch, page);

			// 实时计算配料汇总信息
			calculateWeighingSummary(page, batch.getId());

			result.add(page);
		}

		return result;
	}

}

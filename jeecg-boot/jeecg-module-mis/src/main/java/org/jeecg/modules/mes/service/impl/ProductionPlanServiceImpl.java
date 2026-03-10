package org.jeecg.modules.mes.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.mes.entity.ProductionPlan;
import org.jeecg.modules.mes.entity.ProductionPlanDetail;
import org.jeecg.modules.mes.mapper.ProductionPlanDetailMapper;
import org.jeecg.modules.mes.mapper.ProductionPlanMapper;
import org.jeecg.modules.mes.service.IProductionPlanService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @Description: 生产计划
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
@Service
public class ProductionPlanServiceImpl extends ServiceImpl<ProductionPlanMapper, ProductionPlan> implements IProductionPlanService {

	@Autowired
	private ProductionPlanMapper productionPlanMapper;
	@Autowired
	private ProductionPlanDetailMapper productionPlanDetailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(ProductionPlan productionPlan, List<ProductionPlanDetail> productionPlanDetailList) {
		productionPlanMapper.insert(productionPlan);
		if(productionPlanDetailList!=null && productionPlanDetailList.size()>0) {
			for(ProductionPlanDetail entity:productionPlanDetailList) {
				//外键设置
				entity.setPlanId(productionPlan.getId());
				productionPlanDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(ProductionPlan productionPlan,List<ProductionPlanDetail> productionPlanDetailList) {
		productionPlanMapper.updateById(productionPlan);
		
		//1.先删除子表数据
		productionPlanDetailMapper.deleteByMainId(productionPlan.getId());
		
		//2.子表数据重新插入
		if(productionPlanDetailList!=null && productionPlanDetailList.size()>0) {
			for(ProductionPlanDetail entity:productionPlanDetailList) {
				//外键设置
				entity.setPlanId(productionPlan.getId());
				productionPlanDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		productionPlanDetailMapper.deleteByMainId(id);
		productionPlanMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			productionPlanDetailMapper.deleteByMainId(id.toString());
			productionPlanMapper.deleteById(id);
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void publishPlan(String planId) {
		productionPlanMapper.updatePlanStatus(planId,"1");
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void publishPlanBatch(Collection<? extends Serializable> idList) {
		if(idList == null || idList.isEmpty()) {
			throw new JeecgBootException("ID列表不能为空");
		}
		// 转换为String列表
		List<String> strIdList = idList.stream()
				.map(Object::toString)
				.map(String::trim)
				.filter(StringUtils::isNotBlank)
				.distinct()
				.collect(Collectors.toList());

		if (strIdList.isEmpty()) {
			throw new JeecgBootException("ID列表不能为空");
		}
		productionPlanMapper.updatePlanStatusBatch(strIdList,"1");
//		for(Serializable id:idList) {
//			productionPlanMapper.updatePlanStatus(id.toString(), "1");
//		}
	}
}

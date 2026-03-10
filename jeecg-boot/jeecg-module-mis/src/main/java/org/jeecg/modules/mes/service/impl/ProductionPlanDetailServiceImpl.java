package org.jeecg.modules.mes.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.mes.entity.ProductionPlanDetail;
import org.jeecg.modules.mes.mapper.ProductionPlanDetailMapper;
import org.jeecg.modules.mes.service.IProductionPlanDetailService;
import org.jeecg.modules.mes.vo.ProductionPlanDetailVo;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 生产计划明细表
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
@Service
public class ProductionPlanDetailServiceImpl extends ServiceImpl<ProductionPlanDetailMapper, ProductionPlanDetail> implements IProductionPlanDetailService {
	
	@Autowired
	private ProductionPlanDetailMapper productionPlanDetailMapper;
	
	@Override
	public List<ProductionPlanDetail> selectByMainId(String mainId) {
		return productionPlanDetailMapper.selectByMainId(mainId);
	}

	@Override
	public IPage<ProductionPlanDetailVo> listAvailableForOrder(Page<ProductionPlanDetailVo> page,
															   String planNo,
															   String productCode) {
		// 直接调用 Mapper 自定义 SQL
		return productionPlanDetailMapper.selectAvailableForOrder(page, planNo, productCode);
	}
}

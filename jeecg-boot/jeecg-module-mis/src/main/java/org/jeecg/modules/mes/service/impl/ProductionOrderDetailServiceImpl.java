package org.jeecg.modules.mes.service.impl;

import org.jeecg.modules.mes.entity.ProductionOrderDetail;
import org.jeecg.modules.mes.mapper.ProductionOrderDetailMapper;
import org.jeecg.modules.mes.service.IProductionOrderDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 生产订单明细
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
@Service
public class ProductionOrderDetailServiceImpl extends ServiceImpl<ProductionOrderDetailMapper, ProductionOrderDetail> implements IProductionOrderDetailService {
	
	@Autowired
	private ProductionOrderDetailMapper productionOrderDetailMapper;
	
	@Override
	public List<ProductionOrderDetail> selectByMainId(String mainId) {
		return productionOrderDetailMapper.selectByMainId(mainId);
	}
}

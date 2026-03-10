package org.jeecg.modules.mes.service.impl;

import org.jeecg.modules.mes.entity.ProductionOrder;
import org.jeecg.modules.mes.entity.ProductionOrderDetail;
import org.jeecg.modules.mes.mapper.ProductionOrderDetailMapper;
import org.jeecg.modules.mes.mapper.ProductionOrderMapper;
import org.jeecg.modules.mes.service.IProductionOrderService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
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
	
}

package org.jeecg.modules.wms.service.impl;

import org.jeecg.modules.wms.entity.Delivery;
import org.jeecg.modules.wms.entity.DeliveryDetail;
import org.jeecg.modules.wms.mapper.DeliveryDetailMapper;
import org.jeecg.modules.wms.mapper.DeliveryMapper;
import org.jeecg.modules.wms.service.IDeliveryService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 发货表
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Service
public class DeliveryServiceImpl extends ServiceImpl<DeliveryMapper, Delivery> implements IDeliveryService {

	@Autowired
	private DeliveryMapper deliveryMapper;
	@Autowired
	private DeliveryDetailMapper deliveryDetailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(Delivery delivery, List<DeliveryDetail> deliveryDetailList) {
		deliveryMapper.insert(delivery);
		if(deliveryDetailList!=null && deliveryDetailList.size()>0) {
			for(DeliveryDetail entity:deliveryDetailList) {
				//外键设置
				entity.setDeliveryId(delivery.getId());
				deliveryDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(Delivery delivery,List<DeliveryDetail> deliveryDetailList) {
		deliveryMapper.updateById(delivery);
		
		//1.先删除子表数据
		deliveryDetailMapper.deleteByMainId(delivery.getId());
		
		//2.子表数据重新插入
		if(deliveryDetailList!=null && deliveryDetailList.size()>0) {
			for(DeliveryDetail entity:deliveryDetailList) {
				//外键设置
				entity.setDeliveryId(delivery.getId());
				deliveryDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		deliveryDetailMapper.deleteByMainId(id);
		deliveryMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			deliveryDetailMapper.deleteByMainId(id.toString());
			deliveryMapper.deleteById(id);
		}
	}
	
}

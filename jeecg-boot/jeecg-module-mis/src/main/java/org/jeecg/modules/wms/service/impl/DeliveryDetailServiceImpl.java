package org.jeecg.modules.wms.service.impl;

import org.jeecg.modules.wms.entity.DeliveryDetail;
import org.jeecg.modules.wms.mapper.DeliveryDetailMapper;
import org.jeecg.modules.wms.service.IDeliveryDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 发货明细
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Service
public class DeliveryDetailServiceImpl extends ServiceImpl<DeliveryDetailMapper, DeliveryDetail> implements IDeliveryDetailService {
	
	@Autowired
	private DeliveryDetailMapper deliveryDetailMapper;
	
	@Override
	public List<DeliveryDetail> selectByMainId(String mainId) {
		return deliveryDetailMapper.selectByMainId(mainId);
	}
}

package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.PurchaseOrderDetail;
import org.jeecg.modules.scm.mapper.PurchaseOrderDetailMapper;
import org.jeecg.modules.scm.service.IPurchaseOrderDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 采购明细
 * @Author: jeecg-boot
 * @Date:   2026-07-27
 * @Version: V1.0
 */
@Service
public class PurchaseOrderDetailServiceImpl extends ServiceImpl<PurchaseOrderDetailMapper, PurchaseOrderDetail> implements IPurchaseOrderDetailService {
	
	@Autowired
	private PurchaseOrderDetailMapper purchaseOrderDetailMapper;
	
	@Override
	public List<PurchaseOrderDetail> selectByMainId(String mainId) {
		return purchaseOrderDetailMapper.selectByMainId(mainId);
	}

	@Override
	public List<Map<String, Object>> selectAppliedQtyByOrderId(String orderId) {
		return baseMapper.selectAppliedQtyByOrderId(orderId);
	}

	@Override
	public List<Map<String, Object>> selectSummaryByOrderIds(List<String> orderIds) {
		return purchaseOrderDetailMapper.selectSummaryByOrderIds(orderIds);
	}

	@Override
	public List<Map<String, Object>> selectAppliedQtyByOrderIds(List<String> orderIds) {
		return purchaseOrderDetailMapper.selectAppliedQtyByOrderIds(orderIds);
	}
}

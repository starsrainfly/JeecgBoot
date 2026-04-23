package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.ReceiptOrder;
import org.jeecg.modules.scm.entity.ReceiptOrderDetail;
import org.jeecg.modules.scm.entity.SalesPaymentPlan;
import org.jeecg.modules.scm.mapper.ReceiptOrderDetailMapper;
import org.jeecg.modules.scm.mapper.ReceiptOrderMapper;
import org.jeecg.modules.scm.service.IReceiptOrderService;
import org.jeecg.modules.scm.service.ISalesPaymentPlanService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 收款单
 * @Author: jeecg-boot
 * @Date:   2026-04-23
 * @Version: V1.0
 */
@Service
public class ReceiptOrderServiceImpl extends ServiceImpl<ReceiptOrderMapper, ReceiptOrder> implements IReceiptOrderService {

	@Autowired
	private ReceiptOrderMapper receiptOrderMapper;
	@Autowired
	private ReceiptOrderDetailMapper receiptOrderDetailMapper;
	@Autowired
	private ISalesPaymentPlanService paymentPlanService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(ReceiptOrder receiptOrder, List<ReceiptOrderDetail> receiptOrderDetailList) {
		receiptOrderMapper.insert(receiptOrder);
		if(receiptOrderDetailList!=null && receiptOrderDetailList.size()>0) {
			for(ReceiptOrderDetail entity:receiptOrderDetailList) {
				//外键设置
				entity.setReceiptId(receiptOrder.getId());
				receiptOrderDetailMapper.insert(entity);
				// 3. 更新收款计划
				SalesPaymentPlan plan = paymentPlanService.getById(entity.getPlanId());
				if (plan != null) {
					// 计算新的已收金额
					BigDecimal newPaidAmount = plan.getPaidAmount().add(entity.getReceiptAmount());
					plan.setPaidAmount(newPaidAmount);

					// 计算未收金额
					BigDecimal unpaidAmount = plan.getPlanAmount().subtract(newPaidAmount);
                    plan.setUnpaidAmount(unpaidAmount);
					// 更新状态
					if (unpaidAmount.compareTo(BigDecimal.ZERO) <= 0) {
						plan.setPlanStatus("2"); // 已结清
					} else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
						plan.setPlanStatus("1"); // 部分结算
					}

					paymentPlanService.updateById(plan);
				}
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(ReceiptOrder receiptOrder,List<ReceiptOrderDetail> receiptOrderDetailList) {
		receiptOrderMapper.updateById(receiptOrder);
		
		//1.先删除子表数据
		receiptOrderDetailMapper.deleteByMainId(receiptOrder.getId());
		
		//2.子表数据重新插入
		if(receiptOrderDetailList!=null && receiptOrderDetailList.size()>0) {
			for(ReceiptOrderDetail entity:receiptOrderDetailList) {
				//外键设置
				entity.setReceiptId(receiptOrder.getId());
				receiptOrderDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		receiptOrderDetailMapper.deleteByMainId(id);
		receiptOrderMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			receiptOrderDetailMapper.deleteByMainId(id.toString());
			receiptOrderMapper.deleteById(id);
		}
	}
	
}

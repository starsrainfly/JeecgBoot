package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.PurchaseOrderDetail;
import org.jeecg.modules.scm.entity.PurchaseOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 采购订单
 * @Author: jeecg-boot
 * @Date:   2026-07-27
 * @Version: V1.0
 */
public interface IPurchaseOrderService extends IService<PurchaseOrder> {

	/**
	 * 添加一对多
	 *
	 * @param purchaseOrder
	 * @param purchaseOrderDetailList
	 */
	public void saveMain(PurchaseOrder purchaseOrder,List<PurchaseOrderDetail> purchaseOrderDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param purchaseOrder
   * @param purchaseOrderDetailList
	 */
	public void updateMain(PurchaseOrder purchaseOrder,List<PurchaseOrderDetail> purchaseOrderDetailList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	/** 审核（通过/拒绝） */
	void approve(PurchaseOrder purchaseOrder);

	/** 入库审核通过后的到货回写：累加receivedQty并联动状态 */
	void addReceivedQty(String orderDetailId, java.math.BigDecimal actualQty);
	
}

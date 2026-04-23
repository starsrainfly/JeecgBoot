package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.ReceiptOrderDetail;
import org.jeecg.modules.scm.entity.ReceiptOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 收款单
 * @Author: jeecg-boot
 * @Date:   2026-04-23
 * @Version: V1.0
 */
public interface IReceiptOrderService extends IService<ReceiptOrder> {

	/**
	 * 添加一对多
	 *
	 * @param receiptOrder
	 * @param receiptOrderDetailList
	 */
	public void saveMain(ReceiptOrder receiptOrder,List<ReceiptOrderDetail> receiptOrderDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param receiptOrder
   * @param receiptOrderDetailList
	 */
	public void updateMain(ReceiptOrder receiptOrder,List<ReceiptOrderDetail> receiptOrderDetailList);
	
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
	
}

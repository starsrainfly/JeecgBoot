package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.SalesOrderDetail;
import org.jeecg.modules.scm.entity.SalesOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 销售订单主表
 * @Author: jeecg-boot
 * @Date:   2026-04-20
 * @Version: V1.0
 */
public interface ISalesOrderService extends IService<SalesOrder> {

	/**
	 * 添加一对多
	 *
	 * @param salesOrder
	 * @param salesOrderDetailList
	 */
	public void saveMain(SalesOrder salesOrder,List<SalesOrderDetail> salesOrderDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param salesOrder
   * @param salesOrderDetailList
	 */
	public void updateMain(SalesOrder salesOrder,List<SalesOrderDetail> salesOrderDetailList);
	
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

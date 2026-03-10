package org.jeecg.modules.mes.service;

import org.jeecg.modules.mes.entity.ProductionOrderDetail;
import org.jeecg.modules.mes.entity.ProductionOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 生产订单
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
public interface IProductionOrderService extends IService<ProductionOrder> {

	/**
	 * 添加一对多
	 *
	 * @param productionOrder
	 * @param productionOrderDetailList
	 */
	public void saveMain(ProductionOrder productionOrder,List<ProductionOrderDetail> productionOrderDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param productionOrder
   * @param productionOrderDetailList
	 */
	public void updateMain(ProductionOrder productionOrder,List<ProductionOrderDetail> productionOrderDetailList);
	
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

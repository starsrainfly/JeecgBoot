package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.DeliveryDetail;
import org.jeecg.modules.wms.entity.Delivery;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 发货表
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
public interface IDeliveryService extends IService<Delivery> {

	/**
	 * 添加一对多
	 *
	 * @param delivery
	 * @param deliveryDetailList
	 */
	public void saveMain(Delivery delivery,List<DeliveryDetail> deliveryDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param delivery
   * @param deliveryDetailList
	 */
	public void updateMain(Delivery delivery,List<DeliveryDetail> deliveryDetailList);
	
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

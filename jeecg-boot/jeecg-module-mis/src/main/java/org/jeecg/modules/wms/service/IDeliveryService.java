package org.jeecg.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.DeliveryDetail;
import org.jeecg.modules.wms.entity.Delivery;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.vo.DeliveryTaskVo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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


	/**
	 * 待发货任务列表查询
	 */
	IPage<DeliveryTaskVo> queryTaskList(Page<DeliveryTaskVo> page, Map<String, Object> param);
}

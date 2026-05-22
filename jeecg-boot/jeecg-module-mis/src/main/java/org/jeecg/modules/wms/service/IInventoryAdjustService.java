package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.InventoryAdjustDetail;
import org.jeecg.modules.wms.entity.InventoryAdjust;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 盘点调整单主表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
public interface IInventoryAdjustService extends IService<InventoryAdjust> {

	/**
	 * 添加一对多
	 *
	 * @param inventoryAdjust
	 * @param inventoryAdjustDetailList
	 */
	public void saveMain(InventoryAdjust inventoryAdjust,List<InventoryAdjustDetail> inventoryAdjustDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param inventoryAdjust
   * @param inventoryAdjustDetailList
	 */
	public void updateMain(InventoryAdjust inventoryAdjust,List<InventoryAdjustDetail> inventoryAdjustDetailList);
	
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

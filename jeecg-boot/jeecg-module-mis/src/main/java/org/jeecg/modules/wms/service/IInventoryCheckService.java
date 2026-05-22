package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.InventoryCheckDetail;
import org.jeecg.modules.wms.entity.InventoryCheck;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 盘库主表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
public interface IInventoryCheckService extends IService<InventoryCheck> {

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
	 * 预览库存
	 * @param checkId
	 */
	public List<InventoryCheckDetail> previewStock(String checkId);
	/**
	 * 开始盘点
	 * @param id
	 */
	public void startCheck(String id);



	/**
	 * 审核盘点
	 * @param inventoryCheck
	 */
	public void approveCheck(InventoryCheck inventoryCheck);
}

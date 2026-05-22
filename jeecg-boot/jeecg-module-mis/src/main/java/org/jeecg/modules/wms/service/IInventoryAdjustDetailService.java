package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.InventoryAdjustDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 盘库调整单明细表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
public interface IInventoryAdjustDetailService extends IService<InventoryAdjustDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<InventoryAdjustDetail>
	 */
	public List<InventoryAdjustDetail> selectByMainId(String mainId);
}

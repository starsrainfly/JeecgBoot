package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.InventoryCheckDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 盘库明细表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
public interface IInventoryCheckDetailService extends IService<InventoryCheckDetail> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<InventoryCheckDetail>
   */
	public List<InventoryCheckDetail> selectByMainId(String mainId);
}

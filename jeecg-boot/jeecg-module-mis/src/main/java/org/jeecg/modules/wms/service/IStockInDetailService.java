package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.StockInDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 入库明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
public interface IStockInDetailService extends IService<StockInDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<StockInDetail>
	 */
	public List<StockInDetail> selectByMainId(String mainId);
}

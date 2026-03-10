package org.jeecg.modules.mes.service;

import org.jeecg.modules.mes.entity.ProductionOrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 生产订单明细
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
public interface IProductionOrderDetailService extends IService<ProductionOrderDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<ProductionOrderDetail>
	 */
	public List<ProductionOrderDetail> selectByMainId(String mainId);
}

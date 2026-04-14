package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.DeliveryDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 发货明细
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
public interface IDeliveryDetailService extends IService<DeliveryDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<DeliveryDetail>
	 */
	public List<DeliveryDetail> selectByMainId(String mainId);
}

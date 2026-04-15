package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.PriceOfferDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 报价单明细
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
public interface IPriceOfferDetailService extends IService<PriceOfferDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<PriceOfferDetail>
	 */
	public List<PriceOfferDetail> selectByMainId(String mainId);
}

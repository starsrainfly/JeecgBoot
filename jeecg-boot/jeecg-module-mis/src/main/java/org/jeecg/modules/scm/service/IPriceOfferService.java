package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.scm.entity.PriceOfferDetail;
import org.jeecg.modules.scm.entity.PriceOffer;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.scm.vo.PriceOfferDetailVo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 报价单
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
public interface IPriceOfferService extends IService<PriceOffer> {

	/**
	 * 添加一对多
	 *
	 * @param priceOffer
	 * @param priceOfferDetailList
	 */
	public void saveMain(PriceOffer priceOffer,List<PriceOfferDetail> priceOfferDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param priceOffer
   * @param priceOfferDetailList
	 */
	public void updateMain(PriceOffer priceOffer,List<PriceOfferDetail> priceOfferDetailList);
	
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
	 * 审核通过后，禁用历史重复价格
	 */
	void disableDuplicateAfterApprove(String offerId);

	/**
	 * 报价明细分页查询
	 */
	IPage<PriceOfferDetailVo> getDetailVoPage(Page<PriceOfferDetailVo> page, PriceOfferDetailVo vo);
	
}

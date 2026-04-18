package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.scm.entity.PriceOffer;
import org.jeecg.modules.scm.entity.PriceOfferDetail;
import org.jeecg.modules.scm.mapper.PriceOfferDetailMapper;
import org.jeecg.modules.scm.mapper.PriceOfferMapper;
import org.jeecg.modules.scm.service.IPriceOfferDetailService;
import org.jeecg.modules.scm.service.IPriceOfferService;
import org.jeecg.modules.scm.vo.PriceOfferDetailVo;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.Objects;

/**
 * @Description: 报价单
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Service
public class PriceOfferServiceImpl extends ServiceImpl<PriceOfferMapper, PriceOffer> implements IPriceOfferService {

	@Autowired
	private PriceOfferMapper priceOfferMapper;
	@Autowired
	private PriceOfferDetailMapper priceOfferDetailMapper;

	@Autowired
	private IPriceOfferDetailService priceOfferDetailService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(PriceOffer priceOffer, List<PriceOfferDetail> priceOfferDetailList) {
		priceOfferMapper.insert(priceOffer);
		if(priceOfferDetailList!=null && priceOfferDetailList.size()>0) {
			for(PriceOfferDetail entity:priceOfferDetailList) {
				//外键设置
				entity.setOfferId(priceOffer.getId());
				priceOfferDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(PriceOffer priceOffer,List<PriceOfferDetail> priceOfferDetailList) {
		priceOfferMapper.updateById(priceOffer);
		
		//1.先删除子表数据
		priceOfferDetailMapper.deleteByMainId(priceOffer.getId());
		
		//2.子表数据重新插入
		if(priceOfferDetailList!=null && priceOfferDetailList.size()>0) {
			for(PriceOfferDetail entity:priceOfferDetailList) {
				//外键设置
				entity.setOfferId(priceOffer.getId());
				priceOfferDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		priceOfferDetailMapper.deleteByMainId(id);
		priceOfferMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			priceOfferDetailMapper.deleteByMainId(id.toString());
			priceOfferMapper.deleteById(id);
		}
	}

	@Override
	public void disableDuplicateAfterApprove(String offerId) {
		// 1. 获取当前报价单
		PriceOffer offer = this.getById(offerId);
		if (offer == null) return;

		// 2. 获取当前报价单所有明细
		List<PriceOfferDetail> newDetails = priceOfferDetailService.lambdaQuery()
				.eq(PriceOfferDetail::getOfferId, offerId)
				.eq(PriceOfferDetail::getDelFlag, "0")
				.list();

		// 3. 对每个新明细，禁用历史重复
		for (PriceOfferDetail newDetail : newDetails) {
			disableDuplicate(offer, newDetail);
		}
	}

	@Override
	public IPage<PriceOfferDetailVo> getDetailVoPage(Page<PriceOfferDetailVo> page, PriceOfferDetailVo vo) {
		return priceOfferMapper.selectDetailVoPage(page, vo);
	}

	/**
	 * 禁用单个新明细的历史重复记录
	 */
	private void disableDuplicate(PriceOffer offer, PriceOfferDetail newDetail) {
		// 查该客户该产品的其他有效明细
		List<PriceOfferDetail> historyList = priceOfferDetailService.lambdaQuery()
				.ne(PriceOfferDetail::getId, newDetail.getId())
				.eq(PriceOfferDetail::getProductId, newDetail.getProductId())
				.eq(PriceOfferDetail::getStatus, "1")
				.eq(PriceOfferDetail::getDelFlag, "0")
				.list();

		for (PriceOfferDetail history : historyList) {
			// 查历史所属报价单
			PriceOffer historyOffer = this.getById(history.getOfferId());
			if (historyOffer == null || !"2".equals(historyOffer.getApproveStatus())) {
				continue;
			}

			// 同一客户 + 维度相同
			if (isSameCustomerAndDimension(offer, newDetail, historyOffer, history)) {
				history.setStatus("0");
				history.setDisabledReason(String.format("被报价单[%s]替代", offer.getOfferNo()));
				priceOfferDetailService.updateById(history);
			}
		}
	}

	/**
	 * 判断同一客户且维度相同
	 */
	private boolean isSameCustomerAndDimension(PriceOffer newOffer, PriceOfferDetail newDetail,
											   PriceOffer oldOffer, PriceOfferDetail oldDetail) {
		// 同一客户
		if (!Objects.equals(newOffer.getCustomerId(), oldOffer.getCustomerId())) {
			return false;
		}

		// 价格类型相同
		if (!Objects.equals(newDetail.getPriceType(), oldDetail.getPriceType())) {
			return false;
		}

		// 包装相同
		if (!Objects.equals(newDetail.getPackageId(), oldDetail.getPackageId())) {
			return false;
		}

		// 数量区间重叠
		if (!isQtyOverlap(oldDetail.getQtyMin(), oldDetail.getQtyMax(),
				newDetail.getQtyMin(), newDetail.getQtyMax())) {
			return false;
		}

		// 时间范围重叠
		if (!isDateOverlap(oldDetail.getEffectiveDate(), oldDetail.getExpiryDate(),
				newDetail.getEffectiveDate(), newDetail.getExpiryDate())) {
			return false;
		}

		return true;
	}

	/**
	 * 数量区间重叠
	 */
	private boolean isQtyOverlap(BigDecimal oldMin, BigDecimal oldMax,
								 BigDecimal newMin, BigDecimal newMax) {
		oldMin = oldMin == null ? BigDecimal.ZERO : oldMin;
		oldMax = oldMax == null ? new BigDecimal("999999.9999") : oldMax;
		newMin = newMin == null ? BigDecimal.ZERO : newMin;
		newMax = newMax == null ? new BigDecimal("999999.9999") : newMax;

		return oldMin.compareTo(newMax) <= 0 && newMin.compareTo(oldMax) <= 0;
	}

	/**
	 * 时间范围重叠
	 */
	private boolean isDateOverlap(Date oldStart, Date oldEnd, Date newStart, Date newEnd) {
		if (oldStart == null) oldStart = new Date(0);
		if (newStart == null) newStart = new Date(0);

		// 都永久有效
		if (oldEnd == null && newEnd == null) return true;

		// 旧永久有效
		if (oldEnd == null) {
			return newEnd == null || !newEnd.before(oldStart);
		}

		// 新永久有效
		if (newEnd == null) {
			return !oldEnd.before(newStart);
		}

		// 都有结束时间
		return !oldStart.after(newEnd) && !newStart.after(oldEnd);
	}

}

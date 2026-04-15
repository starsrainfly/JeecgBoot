package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.PriceOfferDetail;
import org.jeecg.modules.scm.mapper.PriceOfferDetailMapper;
import org.jeecg.modules.scm.service.IPriceOfferDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 报价单明细
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Service
public class PriceOfferDetailServiceImpl extends ServiceImpl<PriceOfferDetailMapper, PriceOfferDetail> implements IPriceOfferDetailService {
	
	@Autowired
	private PriceOfferDetailMapper priceOfferDetailMapper;
	
	@Override
	public List<PriceOfferDetail> selectByMainId(String mainId) {
		return priceOfferDetailMapper.selectByMainId(mainId);
	}
}

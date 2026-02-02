package org.jeecg.modules.base.service.impl;

import org.jeecg.modules.base.entity.Currency;
import org.jeecg.modules.base.mapper.ExchangeRateMapper;
import org.jeecg.modules.base.mapper.CurrencyMapper;
import org.jeecg.modules.base.service.ICurrencyService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: 币种表
 * @Author: jeecg-boot
 * @Date:   2024-11-16
 * @Version: V1.0
 */
@Service
public class CurrencyServiceImpl extends ServiceImpl<CurrencyMapper, Currency> implements ICurrencyService {

	@Autowired
	private CurrencyMapper currencyMapper;
	@Autowired
	private ExchangeRateMapper exchangeRateMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		exchangeRateMapper.deleteByMainId(id);
		currencyMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			exchangeRateMapper.deleteByMainId(id.toString());
			currencyMapper.deleteById(id);
		}
	}
	
}

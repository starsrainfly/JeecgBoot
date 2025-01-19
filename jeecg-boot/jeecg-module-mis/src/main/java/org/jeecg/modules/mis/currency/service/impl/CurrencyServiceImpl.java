package org.jeecg.modules.mis.currency.service.impl;

import org.jeecg.modules.mis.currency.entity.Currency;
import org.jeecg.modules.mis.currency.entity.ExchangeRate;
import org.jeecg.modules.mis.currency.mapper.ExchangeRateMapper;
import org.jeecg.modules.mis.currency.mapper.CurrencyMapper;
import org.jeecg.modules.mis.currency.service.ICurrencyService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
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

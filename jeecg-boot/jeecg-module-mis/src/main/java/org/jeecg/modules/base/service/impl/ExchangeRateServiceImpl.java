package org.jeecg.modules.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.base.entity.Currency;
import org.jeecg.modules.base.entity.ExchangeRate;
import org.jeecg.modules.base.mapper.ExchangeRateMapper;
import org.jeecg.modules.base.service.ICurrencyService;
import org.jeecg.modules.base.service.IExchangeRateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 汇率表
 * @Author: jeecg-boot
 * @Date:   2024-11-16
 * @Version: V1.0
 */
@Service
public class ExchangeRateServiceImpl extends ServiceImpl<ExchangeRateMapper, ExchangeRate> implements IExchangeRateService {
	
	@Autowired
	private ExchangeRateMapper exchangeRateMapper;
	@Autowired
	private ICurrencyService currencyService;
	
	@Override
	public List<ExchangeRate> selectByMainId(String mainId) {
		return exchangeRateMapper.selectByMainId(mainId);
	}

	@Override
	public BigDecimal getRateByCode(String currencyCode) {
		Currency currency = currencyService.lambdaQuery()
				.eq(Currency::getCurrencyCode,currencyCode)
				.eq(Currency::getStatus,"1")
				.eq(Currency::getDelFlag,"0").one();
		if(currency==null){
			throw new JeecgBootException("币种不存在或已禁用");
		}
		//1、人民币 默认 1
		if("CNY".equals(currencyCode)){
			return BigDecimal.ONE;
		}
		// 2. 查最新启用汇率
		ExchangeRate rate = exchangeRateMapper.selectOne(
				new LambdaQueryWrapper<ExchangeRate>()
						.eq(ExchangeRate::getCurrencyId, currency.getId())
						.eq(ExchangeRate::getStatus, "1")
						.eq(ExchangeRate::getDelFlag, "0")
						.le(ExchangeRate::getEffectiveDate, new Date())
						.orderByDesc(ExchangeRate::getEffectiveDate)
						.last("LIMIT 1")
		);

		if (rate == null || rate.getExchangeRate() == null) {
			throw new JeecgBootException("该币种暂无有效汇率");
		}

		return rate.getExchangeRate();
	}
}

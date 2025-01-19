package org.jeecg.modules.mis.currency.service.impl;

import org.jeecg.modules.mis.currency.entity.ExchangeRate;
import org.jeecg.modules.mis.currency.mapper.ExchangeRateMapper;
import org.jeecg.modules.mis.currency.service.IExchangeRateService;
import org.springframework.stereotype.Service;
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
	
	@Override
	public List<ExchangeRate> selectByMainId(String mainId) {
		return exchangeRateMapper.selectByMainId(mainId);
	}
}

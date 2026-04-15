package org.jeecg.modules.base.service;

import org.jeecg.modules.base.entity.ExchangeRate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 汇率表
 * @Author: jeecg-boot
 * @Date:   2024-11-16
 * @Version: V1.0
 */
public interface IExchangeRateService extends IService<ExchangeRate> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<ExchangeRate>
   */
	public List<ExchangeRate> selectByMainId(String mainId);

    /**
     * 根据币种代码获取最新启用汇率
     */
    BigDecimal getRateByCode(String currencyCode);
}

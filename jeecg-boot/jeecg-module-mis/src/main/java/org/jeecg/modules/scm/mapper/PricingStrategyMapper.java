package org.jeecg.modules.scm.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.entity.PricingStrategy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.scm.vo.PricingStrategyVo;

/**
 * @Description: 价格策略
 * @Author: jeecg-boot
 * @Date:   2026-02-01
 * @Version: V1.0
 */
public interface PricingStrategyMapper extends BaseMapper<PricingStrategy> {

    public IPage<PricingStrategyVo> selectWithPackage(Page<PricingStrategy> page, @Param("entity") PricingStrategy pricingStrategy);

    public IPage<PricingStrategyVo> selectBestPricingStrategies(Page<PricingStrategy> page, @Param("entity") PricingStrategy pricingStrategy);
}

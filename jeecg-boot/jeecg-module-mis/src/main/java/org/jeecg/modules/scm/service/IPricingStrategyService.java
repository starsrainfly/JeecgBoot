package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.scm.entity.PricingStrategy;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.scm.vo.PricingStrategyVo;

import java.math.BigDecimal;

/**
 * @Description: 价格策略
 * @Author: jeecg-boot
 * @Date:   2026-02-01
 * @Version: V1.0
 */
public interface IPricingStrategyService extends IService<PricingStrategy> {

    /**
     * 匹配价格 返回单独一个实体或空
     * 客户 + 业务员 + 指定包装
     * 客户 + 指定包装
     * 业务员 + 指定包装
     * 全局默认 + 指定包装
     * 1️⃣	客户 + 产品 + 业务员 + 指定包装 + 有效期内	最精准（如：大客户专属价）
     * 2️⃣	客户 + 产品 + 指定包装 + 有效期内	客户通用价
     * 3️⃣	业务员 + 产品 + 指定包装 + 有效期内	业务员特价
     * 4️⃣	产品 + 指定包装 + 有效期内（全局默认价）	标准售价
     * 5️⃣	无匹配 → 使用产品主数据中的标准成本 or 报错	兜底
     */
    public PricingStrategy matchPrice(String customerId, String salesmanUserId, String itemId, String packageItemId);

    public IPage<PricingStrategyVo> queryPageList(PricingStrategy pricingStrategy, Page<PricingStrategy> page);

    public IPage<PricingStrategyVo> selectBestPricingStrategies(PricingStrategy pricingStrategy, Page<PricingStrategy> page);
}

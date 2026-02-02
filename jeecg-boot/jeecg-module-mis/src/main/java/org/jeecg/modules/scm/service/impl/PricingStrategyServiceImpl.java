package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import org.jeecg.modules.scm.entity.PricingStrategy;
import org.jeecg.modules.scm.mapper.PricingStrategyMapper;
import org.jeecg.modules.scm.service.IPricingStrategyService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Description: 价格策略
 * @Author: jeecg-boot
 * @Date:   2026-02-01
 * @Version: V1.0
 */
@Service
public class PricingStrategyServiceImpl extends ServiceImpl<PricingStrategyMapper, PricingStrategy> implements IPricingStrategyService {

    /**
     * 匹配价格
     * 1️⃣	客户 + 产品 + 业务员 + 有效期内	最精准（如：大客户专属价）
     * 2️⃣	客户 + 产品 + 有效期内	客户通用价
     * 3️⃣	业务员 + 产品 + 有效期内	业务员特价
     * 4️⃣	产品 + 有效期内（全局默认价）	标准售价
     * 5️⃣	无匹配 → 使用产品主数据中的标准成本 or 报错	兜底
    */

    public PricingStrategy matchPrice(String customerId, String salesmanUserId, String itemId) {
        if (StrUtil.isBlank(itemId)) {
            return null; // 物料ID不能为空
        }

        LocalDate today = LocalDate.now();

        // 构建基础查询条件：产品 + 生效中 + 日期有效
        LambdaQueryWrapper<PricingStrategy> baseQuery = new LambdaQueryWrapper<PricingStrategy>()
                .eq(PricingStrategy::getItemId, itemId)
                .eq(PricingStrategy::getIsActive, "1")
                .le(PricingStrategy::getEffectiveFrom, today)
                .ge(PricingStrategy::getEffectiveTo, today);

        // 1. 优先级最高：客户 + 业务员
        if (StrUtil.isNotBlank(customerId) && StrUtil.isNotBlank(salesmanUserId)) {
            PricingStrategy strategy = this.getOne(
                    baseQuery.clone()
                            .eq(PricingStrategy::getCustomerId, customerId)
                            .eq(PricingStrategy::getSalesmanUserId, salesmanUserId)
            );
            if (strategy != null) {
                return strategy;
            }
        }

        // 2. 客户专属价（无业务员限制）
        if (StrUtil.isNotBlank(customerId)) {
            PricingStrategy strategy = this.getOne(
                    baseQuery.clone()
                            .eq(PricingStrategy::getCustomerId, customerId)
                            .isNull(PricingStrategy::getSalesmanUserId) // 业务员必须为空
            );
            if (strategy != null) {
                return strategy;
            }
        }

        // 3. 业务员特价（无客户限制）
        if (StrUtil.isNotBlank(salesmanUserId)) {
            PricingStrategy strategy = this.getOne(
                    baseQuery.clone()
                            .eq(PricingStrategy::getSalesmanUserId, salesmanUserId)
                            .isNull(PricingStrategy::getCustomerId) // 客户必须为空
            );
            if (strategy != null) {
                return strategy;
            }
        }

        // 4. 全局默认价（客户和业务员都为空）
        return this.getOne(
                baseQuery.clone()
                        .isNull(PricingStrategy::getCustomerId)
                        .isNull(PricingStrategy::getSalesmanUserId)
        );

        // 如果以上都未匹配，this.getOne() 会返回 null
    }



}

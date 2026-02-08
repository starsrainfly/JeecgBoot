package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import org.jeecg.modules.scm.entity.PricingStrategy;
import org.jeecg.modules.scm.mapper.PricingStrategyMapper;
import org.jeecg.modules.scm.service.IPricingStrategyService;
import org.jeecg.modules.scm.vo.PricingStrategyVo;
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

    public PricingStrategy matchPrice(String customerId, String salesmanUserId, String itemId, String packageItemId) {
        if (StrUtil.isBlank(itemId)) {
            return null; // 物料ID不能为空
        }

        LocalDate today = LocalDate.now();
// 构建基础查询条件（不含客户/业务员/包装）
        LambdaQueryWrapper<PricingStrategy> baseQuery = new LambdaQueryWrapper<PricingStrategy>()
                .eq(PricingStrategy::getItemId, itemId)
                .eq(PricingStrategy::getIsActive, "1")
                .le(PricingStrategy::getEffectiveFrom, today)
                .ge(PricingStrategy::getEffectiveTo, today);

        // ========== 第一阶段：尝试匹配【指定包装】的价格策略 ==========
        LambdaQueryWrapper<PricingStrategy> withPackageQuery = baseQuery.clone();
        if (StrUtil.isNotBlank(packageItemId)) {
            withPackageQuery.eq(PricingStrategy::getPackageItemId, packageItemId);
        } else {
            withPackageQuery.isNull(PricingStrategy::getPackageItemId);
        }

        // 1. 客户 + 业务员 + 包装
        if (StrUtil.isNotBlank(customerId) && StrUtil.isNotBlank(salesmanUserId)) {
            PricingStrategy strategy = this.getOne(
                    withPackageQuery.clone()
                            .eq(PricingStrategy::getCustomerId, customerId)
                            .eq(PricingStrategy::getSalesmanId, salesmanUserId)
            );
            if (strategy != null) {
                return strategy;
            }
        }

        // 2. 客户专属 + 包装
        if (StrUtil.isNotBlank(customerId)) {
            PricingStrategy strategy = this.getOne(
                    withPackageQuery.clone()
                            .eq(PricingStrategy::getCustomerId, customerId)
                            .isNull(PricingStrategy::getSalesmanId)
            );
            if (strategy != null) {
                return strategy;
            }
        }

        // 3. 业务员特价 + 包装
        if (StrUtil.isNotBlank(salesmanUserId)) {
            PricingStrategy strategy = this.getOne(
                    withPackageQuery.clone()
                            .eq(PricingStrategy::getSalesmanId, salesmanUserId)
                            .isNull(PricingStrategy::getCustomerId)
            );
            if (strategy != null) {
                return strategy;
            }
        }

        // 4. 全局默认 + 包装
        PricingStrategy defaultWithPackage = this.getOne(
                withPackageQuery.clone()
                        .isNull(PricingStrategy::getCustomerId)
                        .isNull(PricingStrategy::getSalesmanId)
        );
        if (defaultWithPackage != null) {
            return defaultWithPackage;
        }

        // ========== 第二阶段：【未匹配到指定包装】→ 回退到“无包装”策略（兼容旧数据）==========
        if (StrUtil.isNotBlank(packageItemId)) {
            // 只有当传入了 packageItemId 但未匹配到时，才回退
            LambdaQueryWrapper<PricingStrategy> withoutPackageQuery = baseQuery.clone()
                    .isNull(PricingStrategy::getPackageItemId); // 明确要求无包装

            // 再次按优先级匹配（无包装版本）
            if (StrUtil.isNotBlank(customerId) && StrUtil.isNotBlank(salesmanUserId)) {
                PricingStrategy strategy = this.getOne(
                        withoutPackageQuery.clone()
                                .eq(PricingStrategy::getCustomerId, customerId)
                                .eq(PricingStrategy::getSalesmanId, salesmanUserId)
                );
                if (strategy != null) return strategy;
            }

            if (StrUtil.isNotBlank(customerId)) {
                PricingStrategy strategy = this.getOne(
                        withoutPackageQuery.clone()
                                .eq(PricingStrategy::getCustomerId, customerId)
                                .isNull(PricingStrategy::getSalesmanId)
                );
                if (strategy != null) return strategy;
            }

            if (StrUtil.isNotBlank(salesmanUserId)) {
                PricingStrategy strategy = this.getOne(
                        withoutPackageQuery.clone()
                                .eq(PricingStrategy::getSalesmanId, salesmanUserId)
                                .isNull(PricingStrategy::getCustomerId)
                );
                if (strategy != null) return strategy;
            }

            // 最终全局默认（无包装）
            return this.getOne(
                    withoutPackageQuery.clone()
                            .isNull(PricingStrategy::getCustomerId)
                            .isNull(PricingStrategy::getSalesmanId)
            );
        }

        // 如果 packageItemId 为空，且前面没匹配到，默认返回 null
        return null;

    }

    /**
     * 重写 Online 表单的分页查询方法
     *
     * @param pricingStrategy 前端传入的查询条件（基于原始实体）
     * @param page 分页参数（泛型为原始实体）
     * @return 返回扩展 VO 的分页结果（含关联字段）
     */
    @Override
    public IPage<PricingStrategyVo> queryPageList(PricingStrategy pricingStrategy, Page<PricingStrategy> page) {
        // 调用 Mapper 自定义方法，执行 LEFT JOIN 查询
        return baseMapper.selectWithPackage(page, pricingStrategy);
    }

    @Override
    public IPage<PricingStrategyVo> selectBestPricingStrategies(PricingStrategy pricingStrategy, Page<PricingStrategy> page) {
        return baseMapper.selectBestPricingStrategies(page, pricingStrategy);
    }

}

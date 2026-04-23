package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.scm.entity.SalesPaymentPlan;
import org.jeecg.modules.scm.mapper.SalesPaymentPlanMapper;
import org.jeecg.modules.scm.service.ISalesPaymentPlanService;
import org.jeecg.modules.scm.vo.SalesPaymentPlanVo;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 销售收款计划
 * @Author: jeecg-boot
 * @Date:   2026-04-22
 * @Version: V1.0
 */
@Service
public class SalesPaymentPlanServiceImpl extends ServiceImpl<SalesPaymentPlanMapper, SalesPaymentPlan> implements ISalesPaymentPlanService {

    @Override
    public IPage<SalesPaymentPlanVo> queryPageList(Page<SalesPaymentPlanVo> page, SalesPaymentPlanVo vo) {
        return baseMapper.queryPageList(page, vo);
    }
}

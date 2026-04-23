package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.scm.entity.SalesPaymentPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.scm.vo.SalesPaymentPlanVo;

/**
 * @Description: 销售收款计划
 * @Author: jeecg-boot
 * @Date:   2026-04-22
 * @Version: V1.0
 */
public interface ISalesPaymentPlanService extends IService<SalesPaymentPlan> {
    public IPage<SalesPaymentPlanVo> queryPageList(Page<SalesPaymentPlanVo> page, SalesPaymentPlanVo vo);
}

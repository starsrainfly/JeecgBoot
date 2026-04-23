package org.jeecg.modules.scm.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.entity.SalesPaymentPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.scm.vo.SalesPaymentPlanVo;

/**
 * @Description: 销售收款计划
 * @Author: jeecg-boot
 * @Date:   2026-04-22
 * @Version: V1.0
 */
public interface SalesPaymentPlanMapper extends BaseMapper<SalesPaymentPlan> {

    IPage<SalesPaymentPlanVo> queryPageList(Page<SalesPaymentPlanVo> page, @Param("vo") SalesPaymentPlanVo vo);
}

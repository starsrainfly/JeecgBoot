package org.jeecg.modules.scm.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.scm.service.ICostCalcService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CostCalcMonthlyJob implements Job {

    @Autowired
    private ICostCalcService costCalcService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("========== 开始执行月度成本自动核算 ==========");
        try {
            costCalcService.monthlyAutoCalc();
            log.info("========== 月度成本自动核算执行完成 ==========");
        } catch (Exception e) {
            log.error("月度成本自动核算执行失败", e);
            throw new JobExecutionException(e);
        }
    }
}
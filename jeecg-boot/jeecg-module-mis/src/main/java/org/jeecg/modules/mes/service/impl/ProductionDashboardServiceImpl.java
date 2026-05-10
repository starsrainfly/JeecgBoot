package org.jeecg.modules.mes.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mes.mapper.ProductionOrderMapper;
import org.jeecg.modules.mes.service.IProductionDashboardService;
import org.jeecg.modules.mes.vo.ProductionDashboardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class ProductionDashboardServiceImpl implements IProductionDashboardService {

    @Autowired
    private ProductionOrderMapper productionOrderMapper;

    @Override
    public ProductionDashboardVo getProductionDashboard(String viewType, String operatorId) {
        ProductionDashboardVo vo = new ProductionDashboardVo();

        boolean isManager = "manager".equals(viewType);

        if (isManager) {
            // ========== 管理者视图 ==========
            // 本月计划产量
            BigDecimal monthPlanQty = productionOrderMapper.getMonthPlanQty();
            vo.setMonthPlanQty(monthPlanQty != null ? monthPlanQty : BigDecimal.ZERO);

            // 本月实际产量
            BigDecimal monthActualQty = productionOrderMapper.getMonthActualQty();
            vo.setMonthActualQty(monthActualQty != null ? monthActualQty : BigDecimal.ZERO);

            // 完工率
            if (monthPlanQty != null && monthPlanQty.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = monthActualQty.multiply(new BigDecimal("100"))
                        .divide(monthPlanQty, 2, RoundingMode.HALF_UP);
                vo.setCompletionRate(rate);
            } else {
                vo.setCompletionRate(BigDecimal.ZERO);
            }

            // 进行中批次
            Long runningBatchCount = productionOrderMapper.getRunningBatchCount();
            vo.setRunningBatchCount(runningBatchCount != null ? runningBatchCount : 0L);

            // 待配料批次
            Long pendingBatchCount = productionOrderMapper.getPendingBatchCount();
            vo.setPendingBatchCount(pendingBatchCount != null ? pendingBatchCount : 0L);

            // 待派工工单
            Long pendingTaskCount = productionOrderMapper.getPendingTaskCount();
            vo.setPendingTaskCount(pendingTaskCount != null ? pendingTaskCount : 0L);

            // 列表数据
            vo.setTodayBatches(productionOrderMapper.getTodayBatches());
            vo.setRunningBatches(productionOrderMapper.getRunningBatches());
            vo.setPendingWeighBatches(productionOrderMapper.getPendingWeighBatches());
            vo.setPendingTasks(productionOrderMapper.getPendingTasks());

            // 预警数据
            vo.setSafetyStockWarnings(productionOrderMapper.getSafetyStockWarnings());
            vo.setEquipmentWarnings(productionOrderMapper.getEquipmentWarnings());

            // 图表数据
            vo.setWeekTrend(productionOrderMapper.getWeekTrend());
            vo.setProductDist(productionOrderMapper.getProductDist());
            vo.setTaskStatusDist(productionOrderMapper.getTaskStatusDist());

        } else {
            // ========== 工人视图 ==========
            // 我的本月产量
            BigDecimal myMonthActualQty = productionOrderMapper.getMyMonthActualQty(operatorId);
            vo.setMyMonthActualQty(myMonthActualQty != null ? myMonthActualQty : BigDecimal.ZERO);

            // 我的待办工单
            Long myPendingTaskCount = productionOrderMapper.getMyPendingTaskCount(operatorId);
            vo.setMyPendingTaskCount(myPendingTaskCount != null ? myPendingTaskCount : 0L);

            // 我的已完成工单
            Long myCompletedTaskCount = productionOrderMapper.getMyCompletedTaskCount(operatorId);
            vo.setMyCompletedTaskCount(myCompletedTaskCount != null ? myCompletedTaskCount : 0L);

            // 我的工单列表
            vo.setMyTasks(productionOrderMapper.getMyTasks(operatorId));
        }

        return vo;
    }
}

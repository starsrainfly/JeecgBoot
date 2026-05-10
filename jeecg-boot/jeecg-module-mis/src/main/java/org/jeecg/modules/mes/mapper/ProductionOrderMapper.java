package org.jeecg.modules.mes.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mdm.vo.ManagerDashboardVo;
import org.jeecg.modules.mes.entity.ProductionOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 生产订单
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
public interface ProductionOrderMapper extends BaseMapper<ProductionOrder> {

    List<ManagerDashboardVo.ProduceOrderStatus> selectStatusDistribution();
    List<ManagerDashboardVo.RecentProduceOrder> selectRecentPending(@Param("limit") int limit);

    // ========== 以下追加：生产工作台专用SQL ==========

    // --- 指标卡片（管理者） ---
    BigDecimal getMonthPlanQty();

    BigDecimal getMonthActualQty();

    Long getRunningBatchCount();

    Long getPendingBatchCount();

    Long getPendingTaskCount();

    // --- 指标卡片（工人） ---
    BigDecimal getMyMonthActualQty(@Param("operatorId") String operatorId);

    Long getMyPendingTaskCount(@Param("operatorId") String operatorId);

    Long getMyCompletedTaskCount(@Param("operatorId") String operatorId);

    // --- 列表数据（管理者） ---
    List<Map<String, Object>> getTodayBatches();

    List<Map<String, Object>> getRunningBatches();

    List<Map<String, Object>> getPendingWeighBatches();

    List<Map<String, Object>> getPendingTasks();

    // --- 列表数据（工人） ---
    List<Map<String, Object>> getMyTasks(@Param("operatorId") String operatorId);

    // --- 预警数据 ---
    List<Map<String, Object>> getSafetyStockWarnings();

    List<Map<String, Object>> getEquipmentWarnings();

    // --- 图表数据 ---
    List<Map<String, Object>> getWeekTrend();

    List<Map<String, Object>> getProductDist();

    List<Map<String, Object>> getTaskStatusDist();
}

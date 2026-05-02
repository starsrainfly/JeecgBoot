package org.jeecg.modules.scm.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mdm.vo.ManagerDashboardVo;
import org.jeecg.modules.scm.entity.SalesOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 销售订单主表
 * @Author: jeecg-boot
 * @Date:   2026-04-20
 * @Version: V1.0
 */
public interface SalesOrderMapper extends BaseMapper<SalesOrder> {

    // 本月销售额
    BigDecimal selectMonthSalesAmount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 待销售审核数
    long selectPendingSalesAuditCount();

    // 待财务审核数
    long selectPendingFinanceAuditCount();

    // 近30天趋势
    List<ManagerDashboardVo.DailySalesTrend> select30DaysTrend(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 最近待审核订单
    List<ManagerDashboardVo.RecentSalesOrder> selectRecentPendingAudit(@Param("limit") int limit);
}

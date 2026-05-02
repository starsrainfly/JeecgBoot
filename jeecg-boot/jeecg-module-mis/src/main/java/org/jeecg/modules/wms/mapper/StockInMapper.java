package org.jeecg.modules.wms.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.entity.StockIn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.wms.vo.WarehouseDashboardVo;

/**
 * @Description: 入库表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
public interface StockInMapper extends BaseMapper<StockIn> {

    BigDecimal selectTodayInQty(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    List<WarehouseDashboardVo.DailyTrend> select7DaysInTrend(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<WarehouseDashboardVo.PendingInItem> selectRecentPending(@Param("limit") int limit);
}

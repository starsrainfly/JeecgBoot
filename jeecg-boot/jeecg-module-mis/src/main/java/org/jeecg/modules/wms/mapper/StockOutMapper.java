package org.jeecg.modules.wms.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.entity.StockOut;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.wms.vo.WarehouseDashboardVo;

/**
 * @Description: 出库表
 * @Author: jeecg-boot
 * @Date:   2026-04-09
 * @Version: V1.0
 */
public interface StockOutMapper extends BaseMapper<StockOut> {

    BigDecimal selectTodayOutQty(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    List<WarehouseDashboardVo.DailyTrend> select7DaysOutTrend(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * 月度出库
     * @param start
     * @param end
     * @param isProduct
     * @return
     */
    BigDecimal selectMonthOutAmount(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("isProduct") String isProduct);
}

package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;

/**
 * 出库汇总表（按客户）
 */
@Data
@Schema(description = "出库汇总")
public class StockOutSummaryVo {

    @Excel(name = "客户", width = 20)
    @Schema(description = "客户")
    private String customerName;

    @Excel(name = "期间", width = 12)
    @Schema(description = "期间(yyyy-MM)")
    private String period;

    @Excel(name = "出库笔数", width = 12)
    @Schema(description = "出库笔数")
    private Integer outCount;

    @Excel(name = "出库数量合计", width = 15)
    @Schema(description = "出库数量合计")
    private BigDecimal totalQty;

    @Excel(name = "销售金额合计", width = 15)
    @Schema(description = "销售金额合计")
    private BigDecimal totalSalesAmount;

    @Excel(name = "成本金额合计", width = 15)
    @Schema(description = "成本金额合计")
    private BigDecimal totalCostAmount;
}
package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;

/**
 * 入库汇总表（按供应商）
 */
@Data
@Schema(description = "入库汇总")
public class StockInSummaryVo {

    @Excel(name = "供应商", width = 20)
    @Schema(description = "供应商")
    private String supplierName;

    @Excel(name = "期间", width = 12)
    @Schema(description = "期间(yyyy-MM)")
    private String period;

    @Excel(name = "入库笔数", width = 12)
    @Schema(description = "入库笔数")
    private Integer inCount;

    @Excel(name = "入库数量合计", width = 15)
    @Schema(description = "入库数量合计")
    private BigDecimal totalQty;

    @Excel(name = "入库金额合计", width = 15)
    @Schema(description = "入库金额合计")
    private BigDecimal totalAmount;
}
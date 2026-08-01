package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;

/**
 * 库存收发存汇总（按仓库+月）
 */
@Data
@Schema(description = "仓库收发存汇总")
public class StockWarehouseSummaryVo {

    @Schema(description = "仓库Id")
    private String warehouseId;

    @Excel(name = "仓库", width = 20)
    @Schema(description = "仓库")
    private String warehouseName;

    @Excel(name = "期间", width = 12)
    @Schema(description = "期间(yyyy-MM)")
    private String period;

    @Excel(name = "期初数量", width = 15)
    @Schema(description = "期初数量")
    private BigDecimal openingQty;

    @Excel(name = "期初金额", width = 15)
    @Schema(description = "期初金额")
    private BigDecimal openingAmount;

    @Excel(name = "入库数量", width = 15)
    @Schema(description = "本期入库数量")
    private BigDecimal inQty;

    @Excel(name = "入库金额", width = 15)
    @Schema(description = "本期入库金额")
    private BigDecimal inAmount;

    @Excel(name = "出库数量", width = 15)
    @Schema(description = "本期出库数量")
    private BigDecimal outQty;

    @Excel(name = "出库金额", width = 15)
    @Schema(description = "本期出库金额")
    private BigDecimal outAmount;

    @Excel(name = "期末数量", width = 15)
    @Schema(description = "期末数量")
    private BigDecimal closingQty;

    @Excel(name = "期末金额", width = 15)
    @Schema(description = "期末金额")
    private BigDecimal closingAmount;
}
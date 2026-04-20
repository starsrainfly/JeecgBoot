package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.wms.entity.Stock;

import java.io.Serializable;
import java.util.List;

/**
 * 扫码解析库存返回Vo
 */
@Schema(description = "扫码解析库存返回Vo")
@Data
public class ScanStockVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否匹配订单 */
    @Schema(description = "是否匹配订单")
    private Boolean matched;

    /** 提示信息 */
    @Schema(description = "提示信息")
    private String msg;

    /** 扫码内容 */
    @Schema(description = "扫码内容")
    private String scanCode;

    /** 订单ID */
    @Schema(description = "订单ID")
    private String orderId;

    /** 解析出的产品编码 */
    @Schema(description = "解析出的产品编码")
    private String goodsCode;

    /** 解析出的批次号 */
    @Schema(description = "解析出的批次号")
    private String batchNo;

    /** 匹配的订单明细 */
    @Schema(description = "匹配的订单明细")
    private List<PendingOrderLineVo> orderLines;

    /** FIFO可用库存列表 */
    @Schema(description = "FIFO可用库存列表")
    private List<Stock> stocks;
}

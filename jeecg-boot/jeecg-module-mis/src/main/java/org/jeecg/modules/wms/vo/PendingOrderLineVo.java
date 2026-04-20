package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 未发货订单明细Vo
 */
@Schema(description = "未发货订单明细Vo")
@Data
public class PendingOrderLineVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(description = "主键")
    private String id;

    /** 订单id */
    @Schema(description = "订单id")
    private String orderId;

    /** 产品id */
    @Schema(description = "产品id")
    private String itemId;

    /** 产品编码 */
    @Schema(description = "产品编码")
    private String itemCode;

    /** 产品名称 */
    @Schema(description = "产品名称")
    private String itemName;

    /** 规格型号 */
    @Schema(description = "规格型号")
    private String itemSpec;

    /** 单位 */
    @Schema(description = "单位")
    private String unit;

    /** 数量 */
    @Schema(description = "数量")
    private BigDecimal quantity;

    /** 单价 */
    @Schema(description = "单价")
    private BigDecimal unitPrice;

    /** 金额 */
    @Schema(description = "金额")
    private BigDecimal lineAmount;

    /** 已发货数量 */
    @Schema(description = "已发货数量")
    private BigDecimal deliveredQty;

    /** 剩余可发数量 */
    @Schema(description = "剩余可发数量")
    private BigDecimal remainingQty;

    /** 行号 */
    @Schema(description = "行号")
    private Integer sortIndex;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}

package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 扫码发货明细项Vo
 */
@Schema(description = "扫码发货明细项Vo")
@Data
public class ScanDeliveryItemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 销售订单明细ID */
    @Schema(description = "销售订单明细ID")
    private String sourceDetailId;

    /** 库存记录ID */
    @Schema(description = "库存记录ID")
    private String stockId;

    /** 产品ID */
    @Schema(description = "产品ID")
    private String goodsId;

    /** 产品编码 */
    @Schema(description = "产品编码")
    private String goodsCode;

    /** 产品名称 */
    @Schema(description = "产品名称")
    private String goodsName;

    /** 规格型号 */
    @Schema(description = "规格型号")
    private String goodsSpec;

    /** 单位 */
    @Schema(description = "单位")
    private String unit;

    /** 生产批次ID */
    @Schema(description = "生产批次ID")
    private String productionBatchId;

    /** 批次号 */
    @Schema(description = "批次号")
    private String productionBatchNo;

    /** 生产日期 */
    @Schema(description = "生产日期")
    private Date productionDate;

    /** 有效期至 */
    @Schema(description = "有效期至")
    private Date expiryDate;

    /** 仓库ID */
    @Schema(description = "仓库ID")
    private String warehouseId;

    /** 仓库名称 */
    @Schema(description = "仓库名称")
    private String warehouseName;

    /** 实际发货数量 */
    @Schema(description = "实际发货数量")
    private BigDecimal actualQty;

    /** 单价 */
    @Schema(description = "单价")
    private BigDecimal unitPrice;

    /** 扫码内容 */
    @Schema(description = "扫码内容")
    private String scanCode;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}

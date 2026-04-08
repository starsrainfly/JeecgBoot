package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存汇总VO
 * 按物料+仓库维度汇总
 */
@Data
@Schema(description="库存汇总VO")
public class StockSummaryVo {
    /**物料ID*/
    @Schema(description = "物料ID")
    private String goodsId;

    /**物料编码*/
    @Schema(description = "物料编码")
    private String goodsCode;

    /**物料名称*/
    @Schema(description = "物料名称")
    private String goodsName;

    /**规格型号*/
    @Schema(description = "规格型号")
    private String goodsSpec;

    /**单位*/
    @Schema(description = "单位")
    private String unit;

    /**项目类型*/
    @Dict(dicCode = "wms_item_type")
    @Schema(description = "项目类型")
    private String goodsType;

    /**仓库ID*/
    @Schema(description = "仓库ID")
    private String warehouseId;

    /**仓库名称*/
    @Schema(description = "仓库名称")
    private String warehouseName;

    /**总库存数量*/
    @Schema(description = "总库存数量")
    private BigDecimal totalQty;

    /**总锁定数量*/
    @Schema(description = "总锁定数量")
    private BigDecimal totalLockedQty;

    /**可用库存数量*/
    @Schema(description = "可用库存数量")
    private BigDecimal availableQty;

    /**批次数量*/
    @Schema(description = "批次数量")
    private Integer batchCount;

    /**最早入库时间*/
    @Schema(description = "最早入库时间")
    private Date firstInTime;

    /**最近入库时间*/
    @Schema(description = "最近入库时间")
    private Date lastInTime;

    /**最近效期（最快过期）*/
    @Schema(description = "最近效期")
    private Date nearestExpiryDate;

    /**过期批次数量*/
    @Schema(description = "过期批次数量")
    private Integer expiredBatchCount;

    /**近效期批次数量（7天内）*/
    @Schema(description = "近效期批次数量")
    private Integer nearExpiryBatchCount;
}

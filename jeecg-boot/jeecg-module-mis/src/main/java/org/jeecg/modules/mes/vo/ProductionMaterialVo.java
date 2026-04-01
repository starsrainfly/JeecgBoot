package org.jeecg.modules.mes.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.mes.entity.ProductionMaterial;

import java.math.BigDecimal;

/**
 * 物料需求表 VO（包含分组统计字段）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "物料需求表VO（包含分组统计字段）")
public class ProductionMaterialVo extends ProductionMaterial {

    private static final long serialVersionUID = 1L;

    // ========== 批次分组统计字段（getBatchesByOrder 用）==========

    /**物料数量统计*/
    @Schema(description = "物料数量统计")
    private Integer materialCount;

    /**批次剩余待发合计*/
    @Schema(description = "批次剩余待发合计")
    private BigDecimal totalRemainingQty;

    // ========== 物料汇总统计字段（getMaterialSummary 用）==========

    /**关联批次号列表（逗号分隔）*/
    @Schema(description = "关联批次号列表")
    private String batchNos;

    /**物料需求ID列表（逗号分隔）*/
    @Schema(description = "物料需求ID列表")
    private String materialReqIds;

    /**批次ID列表（逗号分隔）*/
    @Schema(description = "批次ID列表")
    private String batchIds;

    // 继承的 remainingQty 用于存储汇总后的剩余数量
   //可用库存
    private BigDecimal availableStockQty;
}
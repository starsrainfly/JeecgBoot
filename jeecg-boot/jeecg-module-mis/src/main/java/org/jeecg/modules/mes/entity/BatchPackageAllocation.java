package org.jeecg.modules.mes.entity;

import lombok.Data;

import java.math.BigDecimal;
/**
 * 算法临时对象：记录【某个批次】要分配【哪种包装】【多少个】
 * 仅用于 calculatePackageAllocation 方法内部，不持久化
 */
@Data
public class BatchPackageAllocation {
    private String orderDetailId;        // 来源订单明细ID（追溯用）

    // 包装定义（从ProductionOrderDetail复制，避免算法中频繁查对象）
    private String innerPackageId;
    private String innerPackageSpec;
    private BigDecimal innerPackageCapacity;
    private String innerPackageUnit;

    private String outerPackageId;
    private String outerPackageSpec;
    private String outerPackageUnit;
    private Integer innerPerOuter;

    // 分配结果
    private Integer allocatedInnerQty;   // 该批次分配的内包数
    private Integer calculatedOuterQty;  // 计算出的外包数
}

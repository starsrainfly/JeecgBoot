package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.scm.entity.SalesOrderDetail;

import java.math.BigDecimal;
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "待发货订单明细VO")
public class PendingOrderLineVo  extends SalesOrderDetail {
    private static final long serialVersionUID = 1L;

    @Schema(description = "已发数量（计算字段）")
    private BigDecimal deliveredQty;

    @Schema(description = "剩余可发数量（计算字段）")
    private BigDecimal remainingQty;
}

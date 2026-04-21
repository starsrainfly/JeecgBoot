package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.scm.entity.SalesOrder;

import java.io.Serializable;
import java.util.List;

/**
 * 未发货订单明细Vo
 */
@Schema(description = "未发货订单明细Vo")
@Data
public class PendingOrderInfoVo  {
    private static final long serialVersionUID = 1L;
    @Schema(description = "订单主表")
    private SalesOrder order;  // 订单主表
    @Schema(description = "发货明细")
    private List<PendingOrderLineVo> lines;  // 未发货明细
}

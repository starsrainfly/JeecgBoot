package org.jeecg.modules.wms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description="发货任务表")
@Data
public class DeliveryTaskVo {
    private String id;
    private String orderNo;
    private String customerId;
    private String customerName;
    private String salesmanId;
    private String salesmanName;
    /**发货时间*/
    @Excel(name = "订单日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "订单日期")
    private Date orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date deliveryDate;

    private String consignee;
    private String consigneePhone;
    private String consigneeAddress;

    /** 订单总数量 */
    private BigDecimal totalQty;
    /** 已发数量 */
    private BigDecimal deliveredQty;
    /** 剩余数量 */
    private BigDecimal remainingQty;

    /** 发货状态：0-未开始 1-部分发货 2-已完成 */
    @Excel(name = "状态", width = 15, dicCode = "wms_delivery_status")
    @Dict(dicCode = "wms_delivery_status")
    @Schema(description = "状态")
    private String deliveryStatus;

    /** 是否预警：0-否 1-是 */
    private Integer isUrgent;

    @Schema(description = "公司id")
    private String companyId;
    @Schema(description = "公司名称")
    private String companyName;
    @Schema(description = "公司编码")
    private String companyCode;
}

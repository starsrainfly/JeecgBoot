package org.jeecg.modules.scm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SalesOrderTrackingVo {
    private String id;
    private String orderNo;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orderDate;

    private String customerId;
    private String customerName;
    private String salesmanId;
    private String salesmanName;
    private String currencyCode;
    private BigDecimal exchangeRate;
    private BigDecimal orderTotal;
    private BigDecimal orderNet;
    private BigDecimal orderTax;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date receivableDate;

    private Integer paymentDays;
    @Dict(dicCode = "scm_order_status")
    private String orderStatus;
    @Dict(dicCode = "wms_delivery_status")
    private String deliveryStatus;
    @Dict(dicCode = "fms_settlement_status")
    private String settleStatus;
    @Dict(dicCode = "approval_status")
    private String salesApproveStatus;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 执行数据
    private BigDecimal totalQty;
    private BigDecimal deliveredQty;
    private BigDecimal undeliveredQty;
    private BigDecimal receivedAmount;
    private BigDecimal unreceivedAmount;
}
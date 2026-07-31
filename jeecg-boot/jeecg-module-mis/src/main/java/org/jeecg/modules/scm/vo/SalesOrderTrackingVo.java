package org.jeecg.modules.scm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SalesOrderTrackingVo {
    private String id;
    @Excel(name = "订单号", width = 20)
    private String orderNo;
    @Excel(name = "订单日期", width = 12, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orderDate;

    private String customerId;
    @Excel(name = "客户名称", width = 20)
    private String customerName;
    private String salesmanId;
    @Excel(name = "业务员", width = 10)
    private String salesmanName;
    @Excel(name = "币种", width = 8)
    private String currencyCode;
    @Excel(name = "汇率", width = 10, type = 10)
    private BigDecimal exchangeRate;
    @Excel(name = "订单总额", width = 14, type = 10)
    private BigDecimal orderTotal;
    @Excel(name = "不含税总额", width = 14, type = 10)
    private BigDecimal orderNet;
    @Excel(name = "税金", width = 14, type = 10)
    private BigDecimal orderTax;
    @Excel(name = "交货日期", width = 12, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;
    @Excel(name = "应收日期", width = 12, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date receivableDate;
    @Excel(name = "账期(天)", width = 10)
    private Integer paymentDays;
    @Excel(name = "订单状态", width = 10)
    @Dict(dicCode = "scm_order_status")
    private String orderStatus;
    @Excel(name = "发货状态", width = 10)
    @Dict(dicCode = "wms_delivery_status")
    private String deliveryStatus;
    @Excel(name = "结算状态", width = 10)
    @Dict(dicCode = "fms_settlement_status")
    private String settleStatus;
    @Excel(name = "销售审批状态", width = 12)
    @Dict(dicCode = "approval_status")
    private String salesApproveStatus;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 执行数据
    @Excel(name = "订单数量", width = 12, type = 10)
    private BigDecimal totalQty;
    @Excel(name = "已发货数量", width = 12, type = 10)
    private BigDecimal deliveredQty;
    @Excel(name = "未发货数量", width = 12, type = 10)
    private BigDecimal undeliveredQty;
    @Excel(name = "已收款", width = 14, type = 10)
    private BigDecimal receivedAmount;
    @Excel(name = "未收款", width = 14, type = 10)
    private BigDecimal unreceivedAmount;
}
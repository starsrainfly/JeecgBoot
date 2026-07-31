package org.jeecg.modules.scm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CustomerStatementVo {

    @Excel(name = "客户名称", width = 20)
    private String customerName;

    @Excel(name = "订单号", width = 20)
    private String orderNo;

    @Excel(name = "订单日期", width = 12, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orderDate;

    @Excel(name = "应收日期", width = 12, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date receivableDate;

    @Excel(name = "订单总额", width = 14, type = 10)
    private BigDecimal orderTotal;

    @Excel(name = "发货金额", width = 14, type = 10)
    private BigDecimal deliveredAmount;

    @Excel(name = "已收款", width = 14, type = 10)
    private BigDecimal receivedAmount;

    @Excel(name = "欠款", width = 14, type = 10)
    private BigDecimal outstanding;

    @Excel(name = "逾期天数", width = 10)
    private Integer overdueDays;
}

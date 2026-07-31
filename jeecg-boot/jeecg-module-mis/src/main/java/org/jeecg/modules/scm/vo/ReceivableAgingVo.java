package org.jeecg.modules.scm.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;
//应收账龄分析
@Data
public class ReceivableAgingVo {

    @Excel(name = "客户名称", width = 20)
    private String customerName;

    @Excel(name = "总欠款", width = 14, type = 10)
    private BigDecimal totalOutstanding;

    @Excel(name = "1个月内", width = 14, type = 10)
    private BigDecimal current;

    @Excel(name = "1-3个月", width = 14, type = 10)
    private BigDecimal aging1to3;

    @Excel(name = "3-6个月", width = 14, type = 10)
    private BigDecimal aging3to6;

    @Excel(name = "6个月以上", width = 14, type = 10)
    private BigDecimal aging6plus;
}

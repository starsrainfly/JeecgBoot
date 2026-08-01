package org.jeecg.modules.scm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "产品成本核算报表VO")
public class CostCalcReportVo {
    private String id;
    private String productId;

    @Excel(name = "产品编码", width = 15)
    private String productCode;

    @Excel(name = "产品名称", width = 20)
    private String productName;

    @Excel(name = "规格型号", width = 15)
    private String productSpec;

    @Excel(name = "颜色", width = 10)
    private String productColor;

    @Excel(name = "配方编码", width = 15)
    private String recipeCode;

    @Excel(name = "配方名称", width = 15)
    private String recipeName;

    @Excel(name = "核算日期", width = 12, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date calcDate;

    @Excel(name = "核算类型", width = 12)
    private String calcType;

    @Excel(name = "最新成本(元/kg)", width = 15, type = 10)
    private BigDecimal totalCostLatest;

    @Excel(name = "平均成本(元/kg)", width = 15, type = 10)
    private BigDecimal totalCostAvg;

    @Excel(name = "上期最新成本", width = 15, type = 10)
    private BigDecimal lastPeriodCost;

    @Excel(name = "涨跌额", width = 12, type = 10)
    private BigDecimal changeAmount;

    @Excel(name = "涨跌率(%)", width = 12, type = 10)
    private BigDecimal changeRate;
}

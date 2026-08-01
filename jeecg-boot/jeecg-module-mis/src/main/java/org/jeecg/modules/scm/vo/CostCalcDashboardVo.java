package org.jeecg.modules.scm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "成本核算看板VO")
public class CostCalcDashboardVo {
    @Schema(description = "本期核算产品数")
    private Integer calcProductCount;

    @Schema(description = "总产品数")
    private Integer totalProductCount;

    @Schema(description = "本期总成本金额(万元) —— 注：当前为各单位成本之和，如需精确总成本需乘产量")
    private BigDecimal totalCostAmount;

    @Schema(description = "成本上涨产品数")
    private Integer riseCount;

    @Schema(description = "成本异常产品数(环比绝对值>5%)")
    private Integer abnormalCount;

    @Schema(description = "近6个月成本趋势")
    private List<Map<String, Object>> trendData;

    @Schema(description = "TOP10单位成本最高")
    private List<CostCalcReportVo> top10HighCost;

    @Schema(description = "TOP10涨幅最大")
    private List<CostCalcReportVo> top10Rise;
}

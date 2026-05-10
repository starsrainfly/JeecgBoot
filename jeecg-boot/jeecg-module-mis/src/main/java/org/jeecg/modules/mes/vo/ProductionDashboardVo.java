package org.jeecg.modules.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("生产工作台数据")
public class ProductionDashboardVo {
    // ========== 指标卡片（管理者视图） ==========
    @ApiModelProperty("本月计划产量")
    private BigDecimal monthPlanQty;

    @ApiModelProperty("本月实际产量")
    private BigDecimal monthActualQty;

    @ApiModelProperty("本月完工率%")
    private BigDecimal completionRate;

    @ApiModelProperty("进行中批次")
    private Long runningBatchCount;

    @ApiModelProperty("待配料批次")
    private Long pendingBatchCount;

    @ApiModelProperty("待派工工单")
    private Long pendingTaskCount;

    // ========== 指标卡片（工人视图） ==========
    @ApiModelProperty("我的本月产量")
    private BigDecimal myMonthActualQty;

    @ApiModelProperty("我的待办工单")
    private Long myPendingTaskCount;

    @ApiModelProperty("我的已完成工单")
    private Long myCompletedTaskCount;

    // ========== 列表数据（管理者） ==========
    @ApiModelProperty("今日待开工批次")
    private List<Map<String, Object>> todayBatches;

    @ApiModelProperty("进行中批次")
    private List<Map<String, Object>> runningBatches;

    @ApiModelProperty("待配料批次")
    private List<Map<String, Object>> pendingWeighBatches;

    @ApiModelProperty("待派工工单")
    private List<Map<String, Object>> pendingTasks;

    // ========== 列表数据（工人） ==========
    @ApiModelProperty("我的工单列表")
    private List<Map<String, Object>> myTasks;

    // ========== 预警数据（管理者） ==========
    @ApiModelProperty("安全库存预警")
    private List<Map<String, Object>> safetyStockWarnings;

    @ApiModelProperty("设备状态异常")
    private List<Map<String, Object>> equipmentWarnings;

    // ========== 图表数据（管理者） ==========
    @ApiModelProperty("近7天产量趋势")
    private List<Map<String, Object>> weekTrend;

    @ApiModelProperty("本月产品产量分布")
    private List<Map<String, Object>> productDist;

    @ApiModelProperty("工单状态分布")
    private List<Map<String, Object>> taskStatusDist;
}

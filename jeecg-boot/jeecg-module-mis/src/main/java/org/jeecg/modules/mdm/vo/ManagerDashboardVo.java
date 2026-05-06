package org.jeecg.modules.mdm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("管理员工作台首页数据Vo")
public class ManagerDashboardVo {
    @ApiModelProperty("本月销售额")
    private BigDecimal monthSalesAmount = BigDecimal.ZERO;

    @ApiModelProperty("本月回款额")
    private BigDecimal monthReceiptAmount = BigDecimal.ZERO;

    @ApiModelProperty("待生产工单数")
    private Long pendingProduceOrderCount = 0L;

    @ApiModelProperty("待审核单据总数")
    private Long pendingAuditCount = 0L;

    @ApiModelProperty("库存预警物料数")
    private Long stockWarningCount = 0L;

    @ApiModelProperty("本月出库额")
    private BigDecimal monthOutAmount = BigDecimal.ZERO;

    @ApiModelProperty("本月材料出库额")
    private BigDecimal monthMaterialOutAmount = BigDecimal.ZERO;
    @ApiModelProperty("本月产品出库额")
    private BigDecimal monthProductOutAmount = BigDecimal.ZERO;

    @ApiModelProperty("近30天销售趋势")
    private List<DailySalesTrend> salesTrendList;

    @ApiModelProperty("本月收支对比")
    private MonthIncomeExpense monthIncomeExpense;

    @ApiModelProperty("生产工单状态分布")
    private List<ProduceOrderStatus> produceOrderStatusList;

    @ApiModelProperty("待审核单据分布")
    private List<PendingAuditDist> pendingAuditDistList;

    @ApiModelProperty("最近待审核销售订单")
    private List<RecentSalesOrder> recentSalesOrderList;

    @ApiModelProperty("最近待生产工单")
    private List<RecentProduceOrder> recentProduceOrderList;

    // ========== 内部类 ==========

    @Data
    @ApiModel("每日销售趋势")
    public static class DailySalesTrend {
        private String date;
        private BigDecimal salesAmount;
        private BigDecimal receiptAmount;
    }

    @Data
    @ApiModel("本月收支对比")
    public static class MonthIncomeExpense {
        private BigDecimal salesAmount;   // 销售额
        private BigDecimal receiptAmount; // 回款额
        private BigDecimal outAmount;     // 出库成本
    }

    @Data
    @ApiModel("生产工单状态分布")
    public static class ProduceOrderStatus {
        @Dict(dicCode = "mes_production_status")
        private String status;
        private String status_dictText;
        private Long count;
    }

    @Data
    @ApiModel("待审核单据分布")
    public static class PendingAuditDist {
        private String auditType;     // SALES-销售, FINANCE-财务, IN-入库, OUT-出库
        private String auditTypeText;
        private Long count;
    }

    @Data
    @ApiModel("最近待审核销售订单")
    public static class RecentSalesOrder {
        private String id;
        private String orderNo;
        private String customerName;
        private String salesmanName;
        private BigDecimal orderTotal;
        @Dict(dicCode = "approval_status")
        private String salesApproveStatus;
        private String salesApproveStatus_dictText;
        @Dict(dicCode = "approval_status")
        private String financeApproveStatus;
        private String financeApproveStatus_dictText;
        private Date createTime;
    }

    @Data
    @ApiModel("最近待生产工单")
    public static class RecentProduceOrder {
        private String id;
        private String orderNo;
        private String productName;
        private BigDecimal plannedQty;
        @Dict(dicCode = "mes_production_status")
        private String status;
        private String status_dictText;
        private Date plannedStartDate;
    }
}

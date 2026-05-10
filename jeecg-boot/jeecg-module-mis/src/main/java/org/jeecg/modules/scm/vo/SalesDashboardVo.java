package org.jeecg.modules.scm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("销售工作台数据")
public class SalesDashboardVo {
    // ========== 指标卡片 ==========
    @ApiModelProperty("本月订单金额")
    private BigDecimal monthOrderAmount;

    @ApiModelProperty("本月回款金额")
    private BigDecimal monthReceiptAmount;

    @ApiModelProperty("待审核报价单数")
    private Long pendingQuoteCount;

    @ApiModelProperty("待审核订单数")
    private Long pendingOrderCount;

    @ApiModelProperty("本月新增客户数")
    private Long monthNewCustomerCount;

    @ApiModelProperty("本月待收款金额")
    private BigDecimal monthUnpaidAmount;

    // ========== 待办列表 ==========
    @ApiModelProperty("待审核报价单（前10条）")
    private List<Map<String, Object>> pendingQuotes;

    @ApiModelProperty("待审核订单（前10条）")
    private List<Map<String, Object>> pendingOrders;

    @ApiModelProperty("待发货订单（前10条）")
    private List<Map<String, Object>> pendingDeliveryOrders;

    @ApiModelProperty("近7天到期订单（前10条）")
    private List<Map<String, Object>> nearDeliveryOrders;

    @ApiModelProperty("本月待收款计划（前10条）")
    private List<Map<String, Object>> pendingPaymentPlans;

    // ========== 图表数据 ==========
    @ApiModelProperty("近12个月销售趋势")
    private List<Map<String, Object>> monthTrend;

    @ApiModelProperty("本月产品销售TOP5")
    private List<Map<String, Object>> productTop5;

    @ApiModelProperty("客户类型分布")
    private List<Map<String, Object>> customerTypeDist;

    // ========== 新增：首页需要 ==========
    @ApiModelProperty("业务员业绩TOP5")
    private List<Map<String, Object>> salesmanTop5;
}

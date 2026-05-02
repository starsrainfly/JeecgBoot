package org.jeecg.modules.wms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Schema(description="仓库工作台首页数据")
public class WarehouseDashboardVo {


    @Schema(description = "库存预警物料数")
    private Long stockWarningCount = 0L;

    @ApiModelProperty("待材料入库审核")
    private Long pendingMaterialInCount = 0L;

    @ApiModelProperty("待产品入库审核")
    private Long pendingProductInCount = 0L;

    @ApiModelProperty("待材料出库审核")
    private Long pendingMaterialOutCount = 0L;

    @ApiModelProperty("待产品出库审核")
    private Long pendingProductOutCount = 0L;

    @Schema(description = "今日入库数量")
    private BigDecimal todayInQty = BigDecimal.ZERO;

    @Schema(description = "今日出库数量")
    private BigDecimal todayOutQty = BigDecimal.ZERO;

    @Schema(description = "库存锁定总数量")
    private BigDecimal lockedQty = BigDecimal.ZERO;

    @Schema(description = "近7天出入库趋势")
    private List<DailyTrend> trendList;

    @Schema(description = "待审核入库单列表")
    private List<PendingInItem> pendingInList;

    @Schema(description = "库存预警物料Top5")
    private List<WarningMaterial> warningMaterialList;
    @Schema(description = "效期预警批次列表")
    private List<ExpiryAlertItem> expiryAlertList;

    @Data
    @Schema(description = "每日趋势")
    public static class DailyTrend {
        private String date;
        private BigDecimal inQty;
        private BigDecimal outQty;
    }

    @Data
    @Schema(description = "待审核入库单项")
    public static class PendingInItem {
        private String id;
        private String stockInNo;
        private String supplierName;

        @Dict(dicCode = "wms_stock_in_type")
        private String stockInType;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        private Date createTime;

        private BigDecimal totalAmount;
    }

    @Data
    @Schema(description = "预警物料")
    public static class WarningMaterial {
        private String materialId;
        private String materialCode;
        private String materialName;
        private String spec;
        private BigDecimal availableQty;
        private BigDecimal safetyStock;
        private BigDecimal maxStock;
        private BigDecimal shortageQty;
        private String warningType;     // 0=缺货 1=积压 2=近效期

        // 新增：用于 Java 计算
        private Integer materialAlertDays;
        private Integer productAlertDays;
        private String isProduct;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date nearestExpiryDate;

        private BigDecimal expiryQty;
    }

    @Data
    @Schema(description = "效期预警批次")
    public static class ExpiryAlertItem {
        private String stockId;
        private String materialId;
        private String materialCode;
        private String materialName;
        private String spec;
        private String batchNo;
        private BigDecimal quantity;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date expiryDate;

        private Integer remainDays;
        private Integer alertDays;
        private String warehouseId;
        private String areaId;
        private String shelfId;
        private String locationId;
    }


}

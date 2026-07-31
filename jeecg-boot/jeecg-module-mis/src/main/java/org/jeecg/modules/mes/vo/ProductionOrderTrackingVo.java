package org.jeecg.modules.mes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductionOrderTrackingVo {
    private String id;
    @Excel(name = "生产单号", width = 20)
    private String orderNo;
    @Excel(name = "产品编码", width = 15)
    private String productCode;
    @Excel(name = "产品名称", width = 20)
    private String productName;
    @Excel(name = "颜色", width = 10)
    private String productColor;
    @Excel(name = "配方编码", width = 12)
    private String recipeCode;
    @Excel(name = "配方名称", width = 15)
    private String recipeName;
    @Excel(name = "计划产量", width = 12, type = 10)
    private BigDecimal plannedQty;
    @Excel(name = "单釜产量", width = 12, type = 10)
    private BigDecimal batchSize;
    @Excel(name = "计划批次", width = 10)
    private Integer batchCount;
    /**状态0草稿1已下达，2部分完成3已完成*/
    @Excel(name = "状态0草稿1已下达，2部分完成3已完成", width = 15, dicCode = "mes_production_status")
    @Dict(dicCode = "mes_production_status")
    @Schema(description = "状态0草稿1已下达，2部分完成3已完成")
    private String status;
    @Excel(name = "计划开工", width = 12, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date plannedStartDate;
    @Excel(name = "计划完工", width = 12, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date plannedEndDate;
    @Excel(name = "实际开工", width = 16, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualStartTime;
    @Excel(name = "实际完工", width = 16, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualEndTime;

    // 批次汇总
    @Excel(name = "总批次", width = 10)
    private Integer totalBatches;
    @Excel(name = "已入库批次", width = 12)
    private Integer completedBatches;   // 已完全入库
    @Excel(name = "质检合格批次", width = 14)
    private Integer qcPassBatches;      // 质检合格
    @Excel(name = "实际总产量", width = 12, type = 10)
    private BigDecimal totalActualQty;  // 实际总产量
    @Excel(name = "已入库总量", width = 12, type = 10)
    private BigDecimal totalInStockQty; // 已入库总量
}

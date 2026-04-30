package org.jeecg.modules.wms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.wms.entity.StockInDetail;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
@Data
@Schema(description="入库明细查询表")
public class StockInDetailVo extends StockInDetail {
    // 主表关联字段
//    /**入库单号*/
//    @Excel(name = "入库单号", width = 15)
//    @Schema(description = "入库单号")
//    private String stockInNo;
    /**入库类型*/
    @Excel(name = "入库类型", width = 15, dicCode = "wms_stock_in_type")
    @Dict(dicCode = "wms_stock_in_type")
    @Schema(description = "入库类型")
    private String stockInType;

    /**供应商*/
    @Excel(name = "供应商", width = 15)
    @Schema(description = "供应商")
    private String supplierName;


    /**客户*/
    @Excel(name = "客户", width = 15)
    @Schema(description = "客户")
    private String customerName;
    /**仓库*/
    @Excel(name = "仓库", width = 15, dictTable = "mis_warehouse", dicText = "name", dicCode = "id")
    @Dict(dictTable = "mis_warehouse", dicText = "name", dicCode = "id")
    @Schema(description = "仓库")
    private String warehouseId;
//    /**仓库名称*/
//    @Excel(name = "仓库名称", width = 15)
//    @Schema(description = "仓库名称")
//    private String warehouseName;
    /**采购员*/
    @Excel(name = "采购员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @Schema(description = "采购员")
    private String purchaserId;
//    /**采购员*/
//    @Excel(name = "采购员", width = 15)
//    @Schema(description = "采购员")
//    private String purchaserName;
//    /**制单人id*/
//    @Excel(name = "制单人id", width = 15)
//    @Schema(description = "制单人id")
//    private String operatorUserId;
    /**制单人*/
    @Excel(name = "制单人", width = 15)
    @Schema(description = "制单人")
    private String operatorName;
    /**状态*/
    @Excel(name = "状态", width = 15, dicCode = "wms_stock_in_status")
    @Dict(dicCode = "wms_stock_in_status")
    @Schema(description = "状态")
    private String status;
//    /**审批人id*/
//    @Excel(name = "审批人id", width = 15)
//    @Schema(description = "审批人id")
//    private String approveId;
    /**审批人*/
    @Excel(name = "审批人", width = 15)
    @Schema(description = "审批人")
    private String approveName;
    /**审批时间*/
    @Excel(name = "审批时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审批时间")
    private Date approveTime;
    /**申请时间*/
    @Excel(name = "申请时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "申请时间")
    private Date applyTime;
    /**审核状态*/
    @Excel(name = "审核状态", width = 15, dicCode = "approval_status")
    @Schema(description = "审核状态")
    @Dict(dicCode = "approval_status")
    private String approveStatus;
    /**审核备注*/
    @Excel(name = "审核备注", width = 15)
    @Schema(description = "审核备注")
    private String approveRemark;
    /**入库时间*/
    @Excel(name = "入库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "入库时间")
    private Date stockInTime;
    /**来源订单类型*/
    @Excel(name = "来源订单类型", width = 15)
    @Schema(description = "来源订单类型")
    private String sourceOrderType;
//    /**来源单据id*/
//    @Excel(name = "来源单据id", width = 15)
//    @Schema(description = "来源单据id")
//    private String sourceOrderId;
    /**来源单据号*/
    @Excel(name = "来源单据号", width = 15)
    @Schema(description = "来源单据号")
    private String sourceOrderNo;
    /**备注*/
    @Excel(name = "主表备注", width = 15)
    @Schema(description = "主表备注")
    private String mainRemark;

    // 入库时间范围查询字段
    @Schema(description = "入库时间起")
    private String stockInTime_begin;

    @Schema(description = "入库时间止")
    private String stockInTime_end;
}

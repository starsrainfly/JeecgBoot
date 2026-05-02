package org.jeecg.modules.wms.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.wms.entity.StockOutDetail;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockOutDetailVo {
    /**出库单号*/
    @Excel(name = "出库单号", width = 15)
    @Schema(description = "出库单号")
    private String stockOutNo;
    /**出库类型*/
    @Excel(name = "出库类型", width = 15, dicCode = "wms_stock_out_type")
    @Dict(dicCode = "wms_stock_out_type")
    @Schema(description = "出库类型")
    private String stockOutType;
    /**销售订单*/
//    @Excel(name = "销售订单", width = 15)
//    @Schema(description = "销售订单")
//    private String sourceOrderId;
    /**来源单据编号*/
    @Excel(name = "来源单据编号", width = 15)
    @Schema(description = "来源单据编号")
    private String sourceOrderCode;
    /**客户id*/
//    @Excel(name = "客户id", width = 15)
//    @Schema(description = "客户id")
//    private String customerId;
    /**客户名称*/
    @Excel(name = "客户名称", width = 15)
    @Schema(description = "客户名称")
    private String customerName;
    /**领用人id*/
//    @Excel(name = "领用人id", width = 15, dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
//    @Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
//    @Schema(description = "领用人id")
//    private String requesterUserId;
    /**领用人*/
    @Excel(name = "领用人", width = 15)
    @Schema(description = "领用人")
    private String requesterName;
    /**仓库id*/
    @Excel(name = "仓库id", width = 15, dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Dict(dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Schema(description = "仓库id")
    private String warehouseId;
    /**仓库*/
    @Excel(name = "仓库", width = 15)
    @Schema(description = "仓库")
    private String warehouseName;
    /**成本总额*/
    @Excel(name = "成本总额", width = 15)
    @Schema(description = "成本总额")
    private BigDecimal totalCost;
    /**销售总额*/
    @Excel(name = "销售总额", width = 15)
    @Schema(description = "销售总额")
    private BigDecimal totalSales;
//    /**操作人id*/
//    @Excel(name = "操作人id", width = 15)
//    @Schema(description = "操作人id")
//    private String operatorUserId;
    /**操作人*/
    @Excel(name = "操作人", width = 15)
    @Schema(description = "操作人")
    private String operatorName;
    /**业务状态*/
    @Excel(name = "业务状态", width = 15, dicCode = "wms_stock_out_status")
    @Dict(dicCode = "wms_stock_out_status")
    @Schema(description = "业务状态")
    private String status;
    /**是否产品*/
    @Excel(name = "是否产品", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @Schema(description = "是否产品")
    private String isProduct;
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
    /**审核备注*/
    @Excel(name = "审核备注", width = 15)
    @Schema(description = "审核备注")
    private String approveRemark;
    /**审核状态*/
    @Excel(name = "审核状态", width = 15, dicCode = "approval_status")
    @Dict(dicCode = "approval_status")
    @Schema(description = "审核状态")
    private String approveStatus;
    /**申请时间*/
    @Excel(name = "申请时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "申请时间")
    private Date applyTime;
    /**需求时间*/
    @Excel(name = "需求时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "需求时间")
    private Date requiredDate;
    /**出库时间*/
    @Excel(name = "出库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "出库时间")
    private Date stockOutTime;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String mainRemark;

    /**类型*/
    @Excel(name = "类型", width = 15, dicCode = "mdm_material_type")
    @Dict(dicCode = "mdm_material_type")
    @Schema(description = "类型")
    private String goodsType;
    /**编码*/
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String goodsCode;
    /**名称*/
    @Excel(name = "名称", width = 15)
    @Schema(description = "名称")
    private String goodsName;
    /**规格型号*/
    @Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String goodsSpec;
    /**单位*/
    @Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
    /**申请数量*/
    @Excel(name = "申请数量", width = 15)
    @Schema(description = "申请数量")
    private java.math.BigDecimal applyQty;
    /**实发数量*/
    @Excel(name = "实发数量", width = 15)
    @Schema(description = "实发数量")
    private java.math.BigDecimal actualQty;
    /**是否超量*/
    @Excel(name = "是否超量", width = 15,dicCode = "yn")
    @Dict(dicCode = "yn")
    @Schema(description = "是否超量")
    private String overFlag;
    /**超量数量*/
    @Excel(name = "超量数量", width = 15)
    @Schema(description = "超量数量")
    private BigDecimal overQty;
    /**来源类型 NORMAL-正常库存 RESIDUAL-余料库*/
    @Excel(name = "来源类型 NORMAL-正常库存 RESIDUAL-余料库", width = 15)
    @Schema(description = "来源类型 NORMAL-正常库存 RESIDUAL-余料库")
    private String sourceType;

//    /**余料id*/
//    @Excel(name = "余料id", width = 15)
//    @Schema(description = "余料id")
//    private String residualId;  // 【新增】
    /**批次号*/
    @Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNo;
    /**序列号*/
    @Excel(name = "序列号", width = 15)
    @Schema(description = "序列号")
    private String serialNo;
    /**生产日期*/
    @Excel(name = "生产日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "生产日期")
    private Date productionDate;
    /**质保天数*/
    @Excel(name = "质保天数", width = 15)
    @Schema(description = "质保天数")
    private Integer shelfLife;
    /**有效期至*/
    @Excel(name = "有效期至", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "有效期至")
    private Date expiryDate;
    /**成本单价*/
    @Excel(name = "成本单价", width = 15)
    @Schema(description = "成本单价")
    private java.math.BigDecimal costPrice;
    /**销售单价*/
    @Excel(name = "销售单价", width = 15)
    @Schema(description = "销售单价")
    private java.math.BigDecimal salesPrice;
    /**成本金额*/
    @Excel(name = "成本金额", width = 15)
    @Schema(description = "成本金额")
    private java.math.BigDecimal costTotal;
    /**销售金额*/
    @Excel(name = "销售金额", width = 15)
    @Schema(description = "销售金额")
    private java.math.BigDecimal salesTotal;
//    /**来源单据明细id*/
//    @Excel(name = "来源单据明细id", width = 15)
//    @Schema(description = "来源单据明细id")
//    private String sourceDetailId;
//    /**是否删除*/
//    @Excel(name = "是否删除", width = 15)
//    @Schema(description = "是否删除")
//    @TableLogic
//    private String delFlag;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
//    /**需求表id*/
//    @Excel(name = "需求表id", width = 15)
//    @Schema(description = "需求表id")
//    private String requirementId;
//    /**生产批次id*/
//    @Excel(name = "生产批次id", width = 15)
//    @Schema(description = "生产批次id")
//    private String productionBatchId;
    /**生产批次号*/
    @Excel(name = "生产批次号", width = 15)
    @Schema(description = "生产批次号")
    private String productionBatchNo;

    /**收货地址*/
    @Excel(name = "收货地址", width = 15)
    @Schema(description = "收货地址")
    private String deliverAddress;
    /**收货人*/
    @Excel(name = "收货人", width = 15)
    @Schema(description = "收货人")
    private String consignee;
    /**收货人电话*/
    @Excel(name = "收货人电话", width = 15)
    @Schema(description = "收货人电话")
    private String consigneePhone;

    // 出库时间范围查询字段
    @Schema(description = "出库时间起")
    private String stockOutTime_begin;

    @Schema(description = "出库时间止")
    private String stockOutTime_end;

    @Schema(description = "需求日期起")
    private String requiredDate_begin;

    @Schema(description = "需求日期止")
    private String requiredDate_end;
}

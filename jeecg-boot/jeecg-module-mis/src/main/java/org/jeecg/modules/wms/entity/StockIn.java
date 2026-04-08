package org.jeecg.modules.wms.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 入库表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
@Schema(description="入库表")
@Data
@TableName("mis_stock_in")
public class StockIn implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**入库单号*/
	@Excel(name = "入库单号", width = 15)
    @Schema(description = "入库单号")
    private String stockInNo;
	/**入库类型*/
	@Excel(name = "入库类型", width = 15, dicCode = "wms_stock_in_type")
    @Dict(dicCode = "wms_stock_in_type")
    @Schema(description = "入库类型")
    private String stockInType;
	/**供应商id*/
	@Excel(name = "供应商id", width = 15)
    @Schema(description = "供应商id")
    private String supplierId;
	/**供应商*/
	@Excel(name = "供应商", width = 15)
    @Schema(description = "供应商")
    private String supplierName;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
    @Schema(description = "客户id")
    private String customerId;
	/**客户*/
	@Excel(name = "客户", width = 15)
    @Schema(description = "客户")
    private String customerName;
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "mis_warehouse", dicText = "name", dicCode = "id")
    @Dict(dictTable = "mis_warehouse", dicText = "name", dicCode = "id")
    @Schema(description = "仓库")
    private String warehouseId;
	/**仓库名称*/
	@Excel(name = "仓库名称", width = 15)
    @Schema(description = "仓库名称")
    private String warehouseName;
	/**采购员*/
	@Excel(name = "采购员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @Schema(description = "采购员")
    private String purchaserId;
	/**采购员*/
	@Excel(name = "采购员", width = 15)
    @Schema(description = "采购员")
    private String purchaserName;
	/**制单人id*/
	@Excel(name = "制单人id", width = 15)
    @Schema(description = "制单人id")
    private String operatorUserId;
	/**制单人*/
	@Excel(name = "制单人", width = 15)
    @Schema(description = "制单人")
    private String operatorName;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "wms_stock_in_status")
    @Dict(dicCode = "wms_stock_in_status")
    @Schema(description = "状态")
    private String status;
	/**审批人id*/
	@Excel(name = "审批人id", width = 15)
    @Schema(description = "审批人id")
    private String approveId;
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
	/**来源单据id*/
	@Excel(name = "来源单据id", width = 15)
    @Schema(description = "来源单据id")
    private String sourceOrderId;
	/**来源单据号*/
	@Excel(name = "来源单据号", width = 15)
    @Schema(description = "来源单据号")
    private String sourceOrderNo;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
    /**是否产品*/
    @Excel(name = "是否产品", width = 15, dicCode = "yn")
    @Schema(description = "是否产品")
    @Dict(dicCode = "yn")
    private String isProduct;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private String sysOrgCode;
}

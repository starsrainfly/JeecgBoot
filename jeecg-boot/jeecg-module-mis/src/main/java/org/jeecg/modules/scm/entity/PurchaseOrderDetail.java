package org.jeecg.modules.scm.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 采购明细
 * @Author: jeecg-boot
 * @Date:   2026-07-27
 * @Version: V1.0
 */
@Schema(description="采购明细")
@Data
@TableName("mis_purchase_order_detail")
public class PurchaseOrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**采购单id*/
    @Schema(description = "采购单id")
    private String orderId;
	/**采购单号*/
	@Excel(name = "采购单号", width = 15)
    @Schema(description = "采购单号")
    private String orderNo;
	/**物料类型*/
	@Excel(name = "物料类型", width = 15)
    @Schema(description = "物料类型")
    private String goodsType;
	/**物料id*/
	@Excel(name = "物料id", width = 15)
    @Schema(description = "物料id")
    private String goodsId;
	/**物料编码*/
	@Excel(name = "物料编码", width = 15)
    @Schema(description = "物料编码")
    private String goodsCode;
	/**物料名称*/
	@Excel(name = "物料名称", width = 15)
    @Schema(description = "物料名称")
    private String goodsName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String goodsSpec;
	/**单位*/
	@Excel(name = "单位", width = 15, dictTable = "mis_unit where status='1' and del_flag='0'", dicText = "unit", dicCode = "unit")
    @Schema(description = "单位")
    private String unit;
	/**采购数量*/
	@Excel(name = "采购数量", width = 15)
    @Schema(description = "采购数量")
    private java.math.BigDecimal orderQty;
	/**累计入库数量*/
	@Excel(name = "累计入库数量", width = 15)
    @Schema(description = "累计入库数量")
    private java.math.BigDecimal receivedQty;
	/**含税单价*/
	@Excel(name = "含税单价", width = 15)
    @Schema(description = "含税单价")
    private java.math.BigDecimal unitPrice;
	/**税率(%)*/
	@Excel(name = "税率(%)", width = 15)
    @Schema(description = "税率(%)")
    private java.math.BigDecimal taxRate;
	/**税额*/
	@Excel(name = "税额", width = 15)
    @Schema(description = "税额")
    private java.math.BigDecimal taxAmount;
	/**含税金额*/
	@Excel(name = "含税金额", width = 15)
    @Schema(description = "含税金额")
    private java.math.BigDecimal detailAmount;
	/**不含税金额*/
	@Excel(name = "不含税金额", width = 15)
    @Schema(description = "不含税金额")
    private java.math.BigDecimal netAmount;
	/**期望到货日期*/
	@Excel(name = "期望到货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "期望到货日期")
    private Date expectedDate;
	/**需求来源*/
	@Excel(name = "需求来源", width = 15, dicCode = "scm_purchase_source")
    @Schema(description = "需求来源")
    private String sourceType;
	/**物料需求id*/
	@Excel(name = "物料需求id", width = 15)
    @Schema(description = "物料需求id")
    private String sourceRequirementId;
	/**明细状态*/
	@Excel(name = "明细状态", width = 15, dicCode = "scm_purchase_status")
    @Schema(description = "明细状态")
    @Dict(dicCode = "scm_purchase_status")
    private String detailStatus;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
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

    @TableField(exist = false)
    private java.math.BigDecimal remainingQty;

    /** 在途申请数量（待审核的入库申请合计，不存库） */
    @TableField(exist = false)
    private BigDecimal appliedQty;
}

package org.jeecg.modules.scm.entity;

import java.io.Serializable;
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
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 收款明细
 * @Author: jeecg-boot
 * @Date:   2026-04-23
 * @Version: V1.0
 */
@Schema(description="收款明细")
@Data
@TableName("mis_receipt_order_detail")
public class ReceiptOrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**收款单id*/
    @Schema(description = "收款单id")
    private String receiptId;
	/**收款计划id*/
	@Excel(name = "收款计划id", width = 15)
    @Schema(description = "收款计划id")
    private String planId;
	/**收款计划单号*/
	@Excel(name = "收款计划单号", width = 15)
    @Schema(description = "收款计划单号")
    private String planNo;
	/**计划名称*/
	@Excel(name = "计划名称", width = 15)
    @Schema(description = "计划名称")
    private String planName;
	/**订单id*/
	@Excel(name = "订单id", width = 15)
    @Schema(description = "订单id")
    private String salesOrderId;
	/**订单号*/
	@Excel(name = "订单号", width = 15)
    @Schema(description = "订单号")
    private String salesOrderNo;
	/**应收金额*/
	@Excel(name = "应收金额", width = 15)
    @Schema(description = "应收金额")
    private java.math.BigDecimal planAmount;
	/**已收金额*/
	@Excel(name = "已收金额", width = 15)
    @Schema(description = "已收金额")
    private java.math.BigDecimal alreadyReceipt;
	/**核销金额*/
	@Excel(name = "核销金额", width = 15)
    @Schema(description = "核销金额")
    private java.math.BigDecimal receiptAmount;
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
}

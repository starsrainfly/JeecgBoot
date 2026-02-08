package org.jeecg.modules.scm.entity;

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
 * @Description: 销售订单主表
 * @Author: jeecg-boot
 * @Date:   2026-02-07
 * @Version: V1.0
 */
@Schema(description="销售订单主表")
@Data
@TableName("mis_sales_order")
public class SalesOrder implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**订单编号*/
	@Excel(name = "订单编号", width = 15)
    @Schema(description = "订单编号")
    private String orderNo;
	/**订单日期*/
	@Excel(name = "订单日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "订单日期")
    private Date orderDate;
	/**客户*/
	@Excel(name = "客户", width = 15)
    @Schema(description = "客户")
    private String customerId;
	/**业务员id*/
	@Excel(name = "业务员id", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @Schema(description = "业务员id")
    private String salesmanId;
	/**业务员*/
	@Excel(name = "业务员", width = 15)
    @Schema(description = "业务员")
    private String salesmanName;
	/**币种代码*/
	@Excel(name = "币种代码", width = 15, dictTable = "mis_currency", dicText = "currency_name", dicCode = "currency_code")
    @Dict(dictTable = "mis_currency", dicText = "currency_name", dicCode = "currency_code")
    @Schema(description = "币种代码")
    private String currencyCode;
	/**汇率*/
	@Excel(name = "汇率", width = 15)
    @Schema(description = "汇率")
    private java.math.BigDecimal exchangeRate;
	/**付款账期(天)*/
	@Excel(name = "付款账期(天)", width = 15)
    @Schema(description = "付款账期(天)")
    private Integer paymentDays;
	/**收货地址id*/
	@Excel(name = "收货地址id", width = 15)
    @Schema(description = "收货地址id")
    private String deliveryAddressId;
	/**收货地址*/
	@Excel(name = "收货地址", width = 15)
    @Schema(description = "收货地址")
    private String deliveryAddress;
	/**收货人*/
	@Excel(name = "收货人", width = 15)
    @Schema(description = "收货人")
    private String deliveryConsignee;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @Schema(description = "联系电话")
    private String deliveryPhone;
	/**订单总额*/
	@Excel(name = "订单总额", width = 15)
    @Schema(description = "订单总额")
    private java.math.BigDecimal totalAmount;
	/**本位币金额*/
	@Excel(name = "本位币金额", width = 15)
    @Schema(description = "本位币金额")
    private java.math.BigDecimal totalAmountLocal;
	/**销售审批状态*/
	@Excel(name = "销售审批状态", width = 15)
    @Schema(description = "销售审批状态")
    private String salesApprovalStatus;
	/**财务审批状态*/
	@Excel(name = "财务审批状态", width = 15)
    @Schema(description = "财务审批状态")
    private String financeApprovalStatus;
	/**销售审批通过时间*/
	@Excel(name = "销售审批通过时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "销售审批通过时间")
    private Date salesApprovedAt;
	/**销售审批人id*/
	@Excel(name = "销售审批人id", width = 15)
    @Schema(description = "销售审批人id")
    private String salesApproverUserId;
	/**销售审批人*/
	@Excel(name = "销售审批人", width = 15)
    @Schema(description = "销售审批人")
    private String salesApproverName;
	/**财务审批通过时间*/
	@Excel(name = "财务审批通过时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "财务审批通过时间")
    private Date financeApprovedAt;
	/**财务审批人id*/
	@Excel(name = "财务审批人id", width = 15)
    @Schema(description = "财务审批人id")
    private String financeApproverUserId;
	/**财务审批人*/
	@Excel(name = "财务审批人", width = 15)
    @Schema(description = "财务审批人")
    private String financeApproveName;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**订单状态*/
	@Excel(name = "订单状态", width = 15)
    @Schema(description = "订单状态")
    private String orderStatus;
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

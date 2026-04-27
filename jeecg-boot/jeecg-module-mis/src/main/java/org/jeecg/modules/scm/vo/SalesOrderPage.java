package org.jeecg.modules.scm.vo;

import java.math.BigDecimal;
import java.util.List;
import org.jeecg.modules.scm.entity.SalesOrder;
import org.jeecg.modules.scm.entity.SalesOrderDetail;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 销售订单主表
 * @Author: jeecg-boot
 * @Date:   2026-04-20
 * @Version: V1.0
 */
@Data
@Schema(description="销售订单主表")
public class SalesOrderPage {

	/**主键*/
	@Schema(description = "主键")
    private String id;
	/**订单编号*/
	@Excel(name = "订单编号", width = 15)
	@Schema(description = "订单编号")
    private String orderNo;
	/**订单类型*/
	@Excel(name = "订单类型", width = 15, dicCode = "scm_order_type")
    @Dict(dicCode = "scm_order_type")
	@Schema(description = "订单类型")
    private String orderType;
	/**订单日期*/
	@Excel(name = "订单日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@Schema(description = "订单日期")
    private Date orderDate;
	/**交货日期*/
	@Excel(name = "交货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@Schema(description = "交货日期")
    private Date deliveryDate;
	/**客户*/
	@Excel(name = "客户", width = 15)
	@Schema(description = "客户")
    private String customerId;
	/**客户编码*/
	@Excel(name = "客户编码", width = 15)
	@Schema(description = "客户编码")
    private String customerCode;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
	@Schema(description = "客户名称")
    private String customerName;
	/**业务员*/
	@Excel(name = "业务员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
	@Schema(description = "业务员")
    private String salesmanId;
	/**业务员*/
	@Excel(name = "业务员", width = 15)
	@Schema(description = "业务员")
    private String salesmanName;
	/**公司ID*/
	@Excel(name = "公司ID", width = 15)
	@Schema(description = "公司ID")
	private String companyId;
	/**公司编码*/
	@Excel(name = "公司编码", width = 15)
	@Schema(description = "公司编码")
	private String companyCode;
	/**公司名称*/
	@Excel(name = "公司名称", width = 15)
	@Schema(description = "公司名称")
	private String companyName;
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
	private java.math.BigDecimal orderTotal;
	/**去税总额*/
	@Excel(name = "去税总额", width = 15)
	@Schema(description = "去税总额")
	private BigDecimal orderNet;
	/**订单总税额*/
	@Excel(name = "订单总税额", width = 15)
	@Schema(description = "订单总税额")
	private BigDecimal orderTax;
	/**销售审批状态*/
	@Excel(name = "销售审批状态", width = 15, dicCode = "approval_status")
    @Dict(dicCode = "approval_status")
	@Schema(description = "销售审批状态")
    private String salesApproveStatus;
	/**财务审批状态*/
	@Excel(name = "财务审批状态", width = 15, dicCode = "approval_status")
    @Dict(dicCode = "approval_status")
	@Schema(description = "财务审批状态")
    private String financeApproveStatus;
	/**销售审批时间*/
	@Excel(name = "销售审批时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "销售审批时间")
    private Date salesApproveTime;
	/**销售审批人id*/
	@Excel(name = "销售审批人id", width = 15)
	@Schema(description = "销售审批人id")
    private String salesApproverId;
	/**销售审批人*/
	@Excel(name = "销售审批人", width = 15)
	@Schema(description = "销售审批人")
    private String salesApproverName;
	/**财务审批时间*/
	@Excel(name = "财务审批时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "财务审批时间")
    private Date financeApproveTime;
	/**财务审批人id*/
	@Excel(name = "财务审批人id", width = 15)
	@Schema(description = "财务审批人id")
    private String financeApproverId;
	/**财务审批人*/
	@Excel(name = "财务审批人", width = 15)
	@Schema(description = "财务审批人")
    private String financeApproverName;
	/**订单状态*/
	@Excel(name = "订单状态", width = 15, dicCode = "scm_order_status")
    @Dict(dicCode = "scm_order_status")
	@Schema(description = "订单状态")
    private String orderStatus;
	/**发货状态*/
	@Excel(name = "发货状态", width = 15, dicCode = "wms_delivery_status")
    @Dict(dicCode = "wms_delivery_status")
	@Schema(description = "发货状态")
    private String deliveryStatus;
	/**应收日期*/
	@Excel(name = "应收日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@Schema(description = "应收日期")
    private Date receivableDate;
	/**结算状态*/
	@Excel(name = "结算状态", width = 15, dicCode = "fms_settlement_status")
    @Dict(dicCode = "fms_settlement_status")
	@Schema(description = "结算状态")
    private String settleStatus;
	/**已开票金额*/
	@Excel(name = "已开票金额", width = 15)
	@Schema(description = "已开票金额")
    private java.math.BigDecimal invoiceAmount;
	/**客户订单号*/
	@Excel(name = "客户订单号", width = 15)
	@Schema(description = "客户订单号")
    private String customerOrderNo;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@Schema(description = "备注")
    private String remark;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
	@Schema(description = "是否删除")
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
	/**结算方式*/
	@Excel(name = "结算方式", width = 15, dicCode = "payment_method")
    @Dict(dicCode = "payment_method")
	@Schema(description = "结算方式")
    private String paymentMethod;

	/**业务审批意见*/
	@Excel(name = "业务审批意见", width = 15)
	@Schema(description = "业务审批意见")
	private String salesApproveRemark;
	/**财务审批意见*/
	@Excel(name = "财务审批意见", width = 15)
	@Schema(description = "财务审批意见")
	private String financeApproveRemark;

	@ExcelCollection(name="销售订单明细表")
	@Schema(description = "销售订单明细表")
	private List<SalesOrderDetail> salesOrderDetailList;

}

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
 * @Description: 收款单
 * @Author: jeecg-boot
 * @Date:   2026-04-23
 * @Version: V1.0
 */
@Schema(description="收款单")
@Data
@TableName("mis_receipt_order")
public class ReceiptOrder implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**收款单号*/
	@Excel(name = "收款单号", width = 15)
    @Schema(description = "收款单号")
    private String receiptNo;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
    @Schema(description = "客户id")
    private String customerId;
	/**客户编号*/
	@Excel(name = "客户编号", width = 15)
    @Schema(description = "客户编号")
    private String customerCode;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
    @Schema(description = "客户名称")
    private String customerName;
	/**业务员*/
	@Excel(name = "业务员", width = 15, dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Schema(description = "业务员")
    private String salesmanId;
	/**业务员姓名*/
	@Excel(name = "业务员姓名", width = 15)
    @Schema(description = "业务员姓名")
    private String salesmanName;
	/**收款日期*/
	@Excel(name = "收款日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "收款日期")
    private Date receiptDate;
	/**收款总额*/
	@Excel(name = "收款总额", width = 15)
    @Schema(description = "收款总额")
    private java.math.BigDecimal receiptAmount;
	/**币种*/
	@Excel(name = "币种", width = 15, dictTable = "mis_currency where del_flag='0' and status='1'", dicText = "currency_code", dicCode = "currency_code")
    @Dict(dictTable = "mis_currency where del_flag='0' and status='1'", dicText = "currency_code", dicCode = "currency_code")
    @Schema(description = "币种")
    private String currencyCode;
	/**汇率*/
	@Excel(name = "汇率", width = 15)
    @Schema(description = "汇率")
    private java.math.BigDecimal exchangeRate;
	/**收款方式*/
	@Excel(name = "收款方式", width = 15, dicCode = "payment_method")
    @Dict(dicCode = "payment_method")
    @Schema(description = "收款方式")
    private String paymentMethod;

    // 我方收款信息
    @Excel(name = "收款银行", width = 15)
    @Schema(description = "收款银行")
    private String receiptBankName;

    /**收款银行账户*/
    @Excel(name = "收款银行账户", width = 15)
    @Schema(description = "收款银行账户")
    private String bankAccount;

    // 对方付款信息
    @Excel(name = "付款银行", width = 15)
    @Schema(description = "付款银行")
    private String payerBankName;

    @Excel(name = "付款银行账号", width = 15)
    @Schema(description = "付款银行账号")
    private String payerAccount;
    @Excel(name = "付款人/公司名称", width = 15)
    @Schema(description = "付款人/公司名称")
    private String payerName;
	/**银行流水号/支票号*/
	@Excel(name = "银行流水号/支票号", width = 15)
    @Schema(description = "银行流水号/支票号")
    private String referenceNo;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
    @Dict(dicCode = "status")
    @Schema(description = "状态")
    private String status;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15, dicCode = "del_flag")
    @Dict(dicCode = "del_flag")
    @Schema(description = "删除标识")
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

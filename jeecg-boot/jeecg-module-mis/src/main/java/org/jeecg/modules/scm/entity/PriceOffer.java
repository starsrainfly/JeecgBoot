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
 * @Description: 报价单
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Schema(description="报价单")
@Data
@TableName("mis_price_offer")
public class PriceOffer implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**报价单号*/
	@Excel(name = "报价单号", width = 15)
    @Schema(description = "报价单号")
    private String offerNo;
	/**报价日期*/
	@Excel(name = "报价日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "报价日期")
    private Date offerDate;
	/**客户ID*/
	@Excel(name = "客户ID", width = 15)
    @Schema(description = "客户ID")
    private String customerId;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
    @Schema(description = "客户名称")
    private String customerName;
	/**业务员*/
	@Excel(name = "业务员", width = 15, dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Schema(description = "业务员")
    private String salesmanId;
	/**业务员名称*/
	@Excel(name = "业务员名称", width = 15)
    @Schema(description = "业务员名称")
    private String salesmanName;
	/**币种代码*/
	@Excel(name = "币种代码", width = 15, dictTable = "mis_currency where del_flag='0' and status='1'", dicText = "currency_code", dicCode = "currency_code")
    @Dict(dictTable = "mis_currency where del_flag='0' and status='1'", dicText = "currency_code", dicCode = "currency_code")
    @Schema(description = "币种代码")
    private String currencyCode;
	/**汇率*/
	@Excel(name = "汇率", width = 15)
    @Schema(description = "汇率")
    private java.math.BigDecimal exchangeRate;
	/**审核状态*/
	@Excel(name = "审核状态", width = 15, dicCode = "approval_status")
    @Dict(dicCode = "approval_status")
    @Schema(description = "审核状态")
    private String approveStatus;
	/**审核人ID*/
	@Excel(name = "审核人ID", width = 15)
    @Schema(description = "审核人ID")
    private String approverId;
	/**审核人名称*/
	@Excel(name = "审核人名称", width = 15)
    @Schema(description = "审核人名称")
    private String approverName;
	/**审核通过时间*/
	@Excel(name = "审核通过时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核通过时间")
    private Date approveTime;
	/**审核备注*/
	@Excel(name = "审核备注", width = 15)
    @Schema(description = "审核备注")
    private String approveRemark;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
    @Dict(dicCode = "status")
    @Schema(description = "状态")
    private String status;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15, dicCode = "del_flag")
    @Dict(dicCode = "del_flag")
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

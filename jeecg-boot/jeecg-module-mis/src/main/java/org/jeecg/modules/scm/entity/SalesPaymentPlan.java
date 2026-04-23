package org.jeecg.modules.scm.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 销售收款计划
 * @Author: jeecg-boot
 * @Date:   2026-04-22
 * @Version: V1.0
 */
@Data
@TableName("mis_sales_payment_plan")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="销售收款计划")
public class SalesPaymentPlan implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**计划单号*/
	@Excel(name = "计划单号", width = 15)
    @Schema(description = "计划单号")
    private String planNo;
	/**关联订单id*/
	@Excel(name = "关联订单id", width = 15)
    @Schema(description = "关联订单id")
    private String salesOrderId;
	/**销售订单号*/
	@Excel(name = "销售订单号", width = 15)
    @Schema(description = "销售订单号")
    private String salesOrderNo;
	/**客户id*/
	@Excel(name = "客户", width = 15,dictTable = "mis_customer", dicText = "customer_name", dicCode = "id")
    @Schema(description = "客户")
    @Dict(dictTable = "mis_customer", dicText = "customer_name", dicCode = "id")
    private String customerId;
    /**客户id*/
    @Excel(name = "业务员", width = 15,dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @Schema(description = "业务员")
    private String salesmanId;
	/**计划名称*/
	@Excel(name = "计划名称", width = 15)
    @Schema(description = "计划名称")
    private String planName;
	/**期数*/
	@Excel(name = "期数", width = 15)
    @Schema(description = "期数")
    private Integer planStage;
	/**计划收款金额*/
	@Excel(name = "计划收款金额", width = 15)
    @Schema(description = "计划收款金额")
    private BigDecimal planAmount;
	/**计划收款日期*/
	@Excel(name = "计划收款日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "计划收款日期")
    private Date planDate;
	/**结算方式*/
	@Excel(name = "结算方式", width = 15, dicCode = "payment_method")
	@Dict(dicCode = "payment_method")
    @Schema(description = "结算方式")
    private String paymentMethod;
	/**账期天数*/
	@Excel(name = "账期天数", width = 15)
    @Schema(description = "账期天数")
    private Integer paymentDays;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "fms_settlement_status")
	@Dict(dicCode = "fms_settlement_status")
    @Schema(description = "状态")
    private String planStatus;
	/**已付金额*/
	@Excel(name = "已付金额", width = 15)
    @Schema(description = "已付金额")
    private BigDecimal paidAmount;
	/**未付金额*/
	@Excel(name = "未付金额", width = 15)
    @Schema(description = "未付金额")
    private BigDecimal unpaidAmount;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15)
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

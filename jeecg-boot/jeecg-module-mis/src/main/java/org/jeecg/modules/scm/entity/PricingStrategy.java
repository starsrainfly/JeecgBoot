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
 * @Description: 价格策略
 * @Author: jeecg-boot
 * @Date:   2026-02-01
 * @Version: V1.0
 */
@Data
@TableName("mis_pricing_strategy")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="价格策略")
public class PricingStrategy implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**策略名称*/
	@Excel(name = "策略名称", width = 15)
    @Schema(description = "策略名称")
    private String strategyName;
	/**产品*/
	@Excel(name = "产品", width = 15)
    @Schema(description = "产品")
    private String itemId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String itemCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String itemName;
	/**币种代码*/
	@Excel(name = "币种代码", width = 15, dictTable = "mis_currency", dicText = "currency_name", dicCode = "currency_code")
	@Dict(dictTable = "mis_currency", dicText = "currency_name", dicCode = "currency_code")
    @Schema(description = "币种代码")
    private String currencyCode;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
    @Schema(description = "客户id")
    private String customerId;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
    @Schema(description = "客户名称")
    private String customerName;
	/**业务员id*/
	@Excel(name = "业务员id", width = 15, dictTable = "sys_user where del_flag='0'", dicText = "realname", dicCode = "id")
	@Dict(dictTable = "sys_user where del_flag='0'", dicText = "realname", dicCode = "id")
    @Schema(description = "业务员id")
    private String salesmanUserId;
	/**业务员*/
	@Excel(name = "业务员", width = 15)
    @Schema(description = "业务员")
    private String salesmanName;
	/**价格*/
	@Excel(name = "价格", width = 15)
    @Schema(description = "价格")
    private BigDecimal agreedPrice;
	/**最小起订量*/
	@Excel(name = "最小起订量", width = 15)
    @Schema(description = "最小起订量")
    private BigDecimal minQuantity;
	/**生效日期*/
	@Excel(name = "生效日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "生效日期")
    private Date effectiveFrom;
	/**失效日期*/
	@Excel(name = "失效日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "失效日期")
    private Date effectiveTo;
	/**是否需要特批*/
	@Excel(name = "是否需要特批", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否需要特批")
    private String requiresApproval;
	/**定价类型*/
	@Excel(name = "定价类型", width = 15, dicCode = "scm_pricing_type")
	@Dict(dicCode = "scm_pricing_type")
    @Schema(description = "定价类型")
    private String pricingType;
	/**是否启用*/
	@Excel(name = "是否启用", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否启用")
    private String isActive;
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
}

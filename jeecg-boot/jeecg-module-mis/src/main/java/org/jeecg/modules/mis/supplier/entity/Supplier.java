package org.jeecg.modules.mis.supplier.entity;

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
 * @Description: 供应商表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
@Schema(description="供应商表")
@Data
@TableName("mis_supplier")
public class Supplier implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**供应商编码*/
	@Excel(name = "供应商编码", width = 15)
    @Schema(description = "供应商编码")
    private String supplierCode;
	/**供应商名称*/
	@Excel(name = "供应商名称", width = 15)
    @Schema(description = "供应商名称")
    private String supplierName;
	/**简称*/
	@Excel(name = "简称", width = 15)
    @Schema(description = "简称")
    private String shortName;
	/**注册税号*/
	@Excel(name = "注册税号", width = 15)
    @Schema(description = "注册税号")
    private String taxRegistrationNo;
	/**注册类型*/
	@Excel(name = "注册类型", width = 15)
    @Schema(description = "注册类型")
    private String registeredCapital;
	/**开户行*/
	@Excel(name = "开户行", width = 15)
    @Schema(description = "开户行")
    private String openBank;
	/**法人*/
	@Excel(name = "法人", width = 15)
    @Schema(description = "法人")
    private String legalPerson;
	/**账号*/
	@Excel(name = "账号", width = 15)
    @Schema(description = "账号")
    private String accountNo;
	/**账户名称*/
	@Excel(name = "账户名称", width = 15)
    @Schema(description = "账户名称")
    private String accountName;
	/**供应商类型*/
	@Excel(name = "供应商类型", width = 15, dicCode = "supplier_type")
    @Dict(dicCode = "supplier_type")
    @Schema(description = "供应商类型")
    private String supplierType;
	/**账期(天)*/
	@Excel(name = "账期(天)", width = 15)
    @Schema(description = "账期(天)")
    private java.math.BigDecimal paymentDays;
	/**等级*/
	@Excel(name = "等级", width = 15)
    @Schema(description = "等级")
    private java.math.BigDecimal level;
	/**省市区*/
    @Excel(name = "省市区", width = 15,exportConvert=true,importConvert = true )
    @Schema(description = "省市区")
    private String areaId;

    public String convertisAreaId() {
        return SpringContextUtils.getBean(ProvinceCityArea.class).getText(areaId);
    }

    public void convertsetAreaId(String text) {
        this.areaId = SpringContextUtils.getBean(ProvinceCityArea.class).getCode(text);
    }
	/**供应商地址*/
	@Excel(name = "供应商地址", width = 15)
    @Schema(description = "供应商地址")
    private String supplierAddress;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
    @Dict(dicCode = "status")
    @Schema(description = "状态")
    private String status;
	/**删除*/
	@Excel(name = "删除", width = 15)
    @Schema(description = "删除")
    @TableLogic
    private String delFlag;
	/**审核标识*/
	@Excel(name = "审核标识", width = 15, dicCode = "approval_status")
    @Dict(dicCode = "approval_status")
    @Schema(description = "审核标识")
    private Integer auditFlag;
	/**审核人*/
	@Excel(name = "审核人", width = 15)
    @Schema(description = "审核人")
    private String auditor;
	/**审核时间*/
	@Excel(name = "审核时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核时间")
    private Date auditDate;
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

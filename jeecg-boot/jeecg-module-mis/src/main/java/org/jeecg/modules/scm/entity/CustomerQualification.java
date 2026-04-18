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
 * @Description: 客户质证
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Schema(description="客户质证")
@Data
@TableName("mis_customer_qualification")
public class CustomerQualification implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**客户ID*/
    @Schema(description = "客户ID")
    private String customerId;
	/**客户编码（冗余）*/
	@Excel(name = "客户编码（冗余）", width = 15)
    @Schema(description = "客户编码（冗余）")
    private String customerCode;
	/**资质名称*/
	@Excel(name = "资质名称", width = 15)
    @Schema(description = "资质名称")
    private String qualificationName;
	/**资质编号/证书编号*/
	@Excel(name = "资质编号/证书编号", width = 15)
    @Schema(description = "资质编号/证书编号")
    private String qualificationNo;
	/**资质类型*/
	@Excel(name = "资质类型", width = 15, dicCode = "qualification_type")
    @Schema(description = "资质类型")
    private String qualificationType;
	/**生效日期*/
	@Excel(name = "生效日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "生效日期")
    private Date beginDate;
	/**到期日期*/
	@Excel(name = "到期日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "到期日期")
    private Date expiryDate;
	/**发证机构*/
	@Excel(name = "发证机构", width = 15)
    @Schema(description = "发证机构")
    private String issuingAuthority;
	/**资质图片*/
	@Excel(name = "资质图片", width = 15)
    @Schema(description = "资质图片")
    private String qualificationPic;
	/**资质文件*/
	@Excel(name = "资质文件", width = 15)
    @Schema(description = "资质文件")
    private String qualificationFile;
	/**资质状态：0-过期 1-有效 2-即将过期*/
	@Excel(name = "资质状态：0-过期 1-有效 2-即将过期", width = 15)
    @Schema(description = "资质状态：0-过期 1-有效 2-即将过期")
    private String qualificationStatus;
	/**是否关键资质：0-否 1-是*/
	@Excel(name = "是否关键资质：0-否 1-是", width = 15, dicCode = "yn")
    @Schema(description = "是否关键资质：0-否 1-是")
    private String isKeyQualification;
	/**预警提前天数（预留）*/
	@Excel(name = "预警提前天数（预留）", width = 15)
    @Schema(description = "预警提前天数（预留）")
    private Integer alertDays;
	/**预警状态：0-正常 1-预警中 2-已忽略（预留）*/
	@Excel(name = "预警状态：0-正常 1-预警中 2-已忽略（预留）", width = 15)
    @Schema(description = "预警状态：0-正常 1-预警中 2-已忽略（预留）")
    private String alertStatus;
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

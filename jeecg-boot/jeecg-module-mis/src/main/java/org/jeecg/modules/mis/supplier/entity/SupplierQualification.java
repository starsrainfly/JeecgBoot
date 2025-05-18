package org.jeecg.modules.mis.supplier.entity;

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
 * @Description: 供应商质证表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
@Schema(description="供应商质证表")
@Data
@TableName("mis_supplier_qualification")
public class SupplierQualification implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**供应商编码*/
    @Schema(description = "供应商编码")
    private String supplierId;
	/**资质名称*/
	@Excel(name = "资质名称", width = 15)
    @Schema(description = "资质名称")
    private String qualificationName;
	/**开始日期*/
	@Excel(name = "开始日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "开始日期")
    private Date beginDate;
	/**有效期*/
	@Excel(name = "有效期", width = 15)
    @Schema(description = "有效期")
    private java.math.BigDecimal validity;
	/**有效日期*/
	@Excel(name = "有效日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "有效日期")
    private Date expiryDate;
	/**资质类型*/
	@Excel(name = "资质类型", width = 15, dicCode = "qualification_type")
    @Schema(description = "资质类型")
    private String qualificationType;
	/**图片*/
	@Excel(name = "图片", width = 15)
    @Schema(description = "图片")
    private String qualificationPic;
	/**文件*/
	@Excel(name = "文件", width = 15)
    @Schema(description = "文件")
    private String qualificationFile;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
    @Schema(description = "状态")
    private String status;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
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

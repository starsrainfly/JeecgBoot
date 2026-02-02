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
 * @Description: 供应商联系人
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Schema(description="供应商联系人")
@Data
@TableName("mis_supplier_contact")
public class SupplierContact implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private java.lang.String id;
	/**供应商id*/
    @Schema(description = "供应商id")
    private java.lang.String supplierId;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @Schema(description = "联系人")
    private java.lang.String contact;
	/**职位*/
	@Excel(name = "职位", width = 15)
    @Schema(description = "职位")
    private java.lang.String job;
	/**固定电话*/
	@Excel(name = "固定电话", width = 15)
    @Schema(description = "固定电话")
    private java.lang.String telNo;
	/**手机号码*/
	@Excel(name = "手机号码", width = 15)
    @Schema(description = "手机号码")
    private java.lang.String mobileNo;
	/**传真*/
	@Excel(name = "传真", width = 15)
    @Schema(description = "传真")
    private java.lang.String fax;
	/**电子邮箱*/
	@Excel(name = "电子邮箱", width = 15)
    @Schema(description = "电子邮箱")
    private java.lang.String email;
	/**微信*/
	@Excel(name = "微信", width = 15)
    @Schema(description = "微信")
    private java.lang.String wechat;
	/**QQ号*/
	@Excel(name = "QQ号", width = 15)
    @Schema(description = "QQ号")
    private java.lang.String qq;
	/**联系人类型*/
	@Excel(name = "联系人类型", width = 15)
    @Schema(description = "联系人类型")
    private java.lang.String contactType;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private java.lang.String status;
	/**删除*/
	@Excel(name = "删除", width = 15)
    @Schema(description = "删除")
    @TableLogic
    private java.lang.String delFlag;
	/**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
}

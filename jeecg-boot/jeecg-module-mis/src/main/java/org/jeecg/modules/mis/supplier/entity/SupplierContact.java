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
 * @Description: 供应商联系人
 * @Author: jeecg-boot
 * @Date:   2025-05-18
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
    private String id;
	/**供应商id*/
    @Schema(description = "供应商id")
    private String supplierId;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @Schema(description = "联系人")
    private String contact;
	/**职位*/
	@Excel(name = "职位", width = 15)
    @Schema(description = "职位")
    private String job;
	/**固定电话*/
	@Excel(name = "固定电话", width = 15)
    @Schema(description = "固定电话")
    private String telNo;
	/**手机号码*/
	@Excel(name = "手机号码", width = 15)
    @Schema(description = "手机号码")
    private String mobileNo;
	/**传真*/
	@Excel(name = "传真", width = 15)
    @Schema(description = "传真")
    private String fax;
	/**电子邮箱*/
	@Excel(name = "电子邮箱", width = 15)
    @Schema(description = "电子邮箱")
    private String email;
	/**微信*/
	@Excel(name = "微信", width = 15)
    @Schema(description = "微信")
    private String wechat;
	/**QQ号*/
	@Excel(name = "QQ号", width = 15)
    @Schema(description = "QQ号")
    private String qq;
	/**联系人类型*/
	@Excel(name = "联系人类型", width = 15)
    @Schema(description = "联系人类型")
    private String contactType;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;
	/**删除*/
	@Excel(name = "删除", width = 15)
    @Schema(description = "删除")
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

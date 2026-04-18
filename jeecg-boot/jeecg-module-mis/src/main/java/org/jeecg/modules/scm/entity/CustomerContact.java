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
 * @Description: 客户联系人
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Schema(description="客户联系人")
@Data
@TableName("mis_customer_contact")
public class CustomerContact implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**客户编码*/
    @Schema(description = "客户编码")
    private String customerId;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @Schema(description = "联系人")
    private String name;
	/**职位*/
	@Excel(name = "职位", width = 15)
    @Schema(description = "职位")
    private String position;
	/**电话*/
	@Excel(name = "电话", width = 15)
    @Schema(description = "电话")
    private String phone;
	/**邮箱*/
	@Excel(name = "邮箱", width = 15)
    @Schema(description = "邮箱")
    private String email;
	/**微信*/
	@Excel(name = "微信", width = 15)
    @Schema(description = "微信")
    private String wechat;
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
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
	/**联系人类型*/
	@Excel(name = "联系人类型", width = 15)
    @Schema(description = "联系人类型")
    private String contactType;
}

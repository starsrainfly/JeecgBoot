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
 * @Description: 客户地址
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Schema(description="客户地址")
@Data
@TableName("mis_customer_address")
public class CustomerAddress implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**客户编码*/
    @Schema(description = "客户编码")
    private String customerId;

    public String convertisDistrictCode() {
        return SpringContextUtils.getBean(ProvinceCityArea.class).getText(districtCode);
    }

    public void convertsetDistrictCode(String text) {
        this.districtCode = SpringContextUtils.getBean(ProvinceCityArea.class).getCode(text);
    }

	/**区编码*/
	@Excel(name = "区编码", width = 15,exportConvert=true,importConvert = true)
    @Schema(description = "区编码")
    private String districtCode;
	/**区/县名称*/
	@Excel(name = "区/县名称", width = 15)
    @Schema(description = "区/县名称")
    private String districtName;
	/**收货人*/
	@Excel(name = "收货人", width = 15)
    @Schema(description = "收货人")
    private String receiverName;
	/**电话*/
	@Excel(name = "电话", width = 15)
    @Schema(description = "电话")
    private String receiverPhone;
	/**详细地址*/
	@Excel(name = "详细地址", width = 15)
    @Schema(description = "详细地址")
    private String address;
	/**默认*/
	@Excel(name = "默认", width = 15, dicCode = "yn")
    @Schema(description = "默认")
    private String isDefault;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
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

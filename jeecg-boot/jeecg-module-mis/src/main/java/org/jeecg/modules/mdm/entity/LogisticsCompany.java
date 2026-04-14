package org.jeecg.modules.mdm.entity;

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
 * @Description: 物流公司表
 * @Author: jeecg-boot
 * @Date:   2026-04-14
 * @Version: V1.0
 */
@Data
@TableName("mis_logistics_company")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="物流公司表")
public class LogisticsCompany implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**公司编码*/
	@Excel(name = "公司编码", width = 15)
    @Schema(description = "公司编码")
    private String companyCode;
	/**公司名称*/
	@Excel(name = "公司名称", width = 15)
    @Schema(description = "公司名称")
    private String companyName;
	/**公司类型*/
	@Excel(name = "公司类型", width = 15, dicCode = "wms_logistics_type")
	@Dict(dicCode = "wms_logistics_type")
    @Schema(description = "公司类型")
    private String companyType;
	/**官方全称*/
	@Excel(name = "官方全称", width = 15)
    @Schema(description = "官方全称")
    private String officialName;
	/**官网*/
	@Excel(name = "官网", width = 15)
    @Schema(description = "官网")
    private String websit;
	/**查询连接*/
	@Excel(name = "查询连接", width = 15)
    @Schema(description = "查询连接")
    private String queryUrl;
	/**客服电话*/
	@Excel(name = "客服电话", width = 15)
    @Schema(description = "客服电话")
    private String contactPhone;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @Schema(description = "排序")
    private Integer sortOrder;
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

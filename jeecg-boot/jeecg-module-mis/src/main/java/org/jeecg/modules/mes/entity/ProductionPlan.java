package org.jeecg.modules.mes.entity;

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
 * @Description: 生产计划
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
@Schema(description="生产计划")
@Data
@TableName("mis_production_plan")
public class ProductionPlan implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**计划编号*/
	@Excel(name = "计划编号", width = 15)
    @Schema(description = "计划编号")
    private String planNo;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
    /**产品颜色*/
    @Excel(name = "产品颜色", width = 15)
    @Schema(description = "产品颜色")
    private String productColor;
	/**计划生产数量（kg）*/
	@Excel(name = "计划生产数量（kg）", width = 15)
    @Schema(description = "计划生产数量（kg）")
    private java.math.BigDecimal plannedQty;
    /**完工数量（kg）*/
    @Excel(name = "完工数量（kg）", width = 15)
    @Schema(description = "完工数量（kg）")
    private java.math.BigDecimal completedQty;
	/**计划类型*/
	@Excel(name = "计划类型", width = 15, dicCode = "mes_plan_type")
    @Dict(dicCode = "mes_plan_type")
    @Schema(description = "计划类型")
    private String planType;
	/**计划开工日期*/
	@Excel(name = "计划开工日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "计划开工日期")
    private Date plannedStartDate;
	/**计划完工日期*/
	@Excel(name = "计划完工日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "计划完工日期")
    private Date plannedEndDate;
	/**实际开工时间*/
	@Excel(name = "实际开工时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实际开工时间")
    private Date actualStartTime;
	/**实际完工时间*/
	@Excel(name = "实际完工时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实际完工时间")
    private Date actualEndTime;
	/**计划状态*/
	@Excel(name = "计划状态", width = 15, dicCode = "mes_plan_status")
    @Dict(dicCode = "mes_plan_status")
    @Schema(description = "计划状态")
    private String planStatus;
    /**公司ID*/
    @Excel(name = "公司ID", width = 15)
    @Dict(dictTable = "sys_depart where del_flag='0' and org_category='1' and org_type='1'", dicText = "depart_name", dicCode = "id")
    @Schema(description = "公司ID")
    private String companyId;
    /**公司名称*/
    @Excel(name = "公司名称", width = 15)
    @Schema(description = "公司名称")
    private String companyName;
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
}

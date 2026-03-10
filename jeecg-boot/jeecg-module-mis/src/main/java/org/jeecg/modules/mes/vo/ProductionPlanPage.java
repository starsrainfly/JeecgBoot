package org.jeecg.modules.mes.vo;

import java.math.BigDecimal;
import java.util.List;
import org.jeecg.modules.mes.entity.ProductionPlan;
import org.jeecg.modules.mes.entity.ProductionPlanDetail;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 生产计划
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
@Data
@Schema(description="生产计划")
public class ProductionPlanPage {

	/**主键*/
	@Schema(description = "主键")
    private String id;
	/**计划编号*/
	@Excel(name = "计划编号", width = 15)
	@Schema(description = "计划编号")
    private String planCode;
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
	/**计划生产数量（kg）*/
	@Excel(name = "计划生产数量（kg）", width = 15)
	@Schema(description = "计划生产数量（kg）")
    private java.math.BigDecimal plannedQty;
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
    private String delFlag;
	@ExcelCollection(name="生产计划明细表")
	@Schema(description = "生产计划明细表")
	private List<ProductionPlanDetail> productionPlanDetailList;

}

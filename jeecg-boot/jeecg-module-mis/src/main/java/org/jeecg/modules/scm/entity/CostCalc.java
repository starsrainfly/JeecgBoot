package org.jeecg.modules.scm.entity;

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
 * @Description: 成本核算快照
 * @Author: jeecg-boot
 * @Date:   2026-07-28
 * @Version: V1.0
 */
@Schema(description="成本核算快照")
@Data
@TableName("mis_cost_calc")
public class CostCalc implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**核算单号*/
	@Excel(name = "核算单号", width = 15)
    @Schema(description = "核算单号")
    private String calcNo;
	/**核算类型:MANUAL手动/MONTHLY月度*/
	@Excel(name = "核算类型:MANUAL手动/MONTHLY月度", width = 15)
    @Schema(description = "核算类型:MANUAL手动/MONTHLY月度")
    private String calcType;
	/**核算日期*/
	@Excel(name = "核算日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "核算日期")
    private Date calcDate;
	/**产品id*/
	@Excel(name = "产品id", width = 15)
    @Schema(description = "产品id")
    private String productId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String productSpec;
	/**颜色*/
	@Excel(name = "颜色", width = 15)
    @Schema(description = "颜色")
    private String productColor;
	/**配方id*/
	@Excel(name = "配方id", width = 15)
    @Schema(description = "配方id")
    private String recipeId;
	/**配方编码*/
	@Excel(name = "配方编码", width = 15)
    @Schema(description = "配方编码")
    private String recipeCode;
	/**配方名称*/
	@Excel(name = "配方名称", width = 15)
    @Schema(description = "配方名称")
    private String recipeName;
	/**配方版本*/
	@Excel(name = "配方版本", width = 15)
    @Schema(description = "配方版本")
    private String recipeVersion;
	/**总配比*/
	@Excel(name = "总配比", width = 15)
    @Schema(description = "总配比")
    private java.math.BigDecimal proportionTotal;
	/**配比类型*/
	@Excel(name = "配比类型", width = 15)
    @Schema(description = "配比类型")
    private String proportionType;
	/**最新成本合计(元/kg)*/
	@Excel(name = "最新成本合计(元/kg)", width = 15)
    @Schema(description = "最新成本合计(元/kg)")
    private java.math.BigDecimal totalCostLatest;
	/**平均成本合计(元/kg)*/
	@Excel(name = "平均成本合计(元/kg)", width = 15)
    @Schema(description = "平均成本合计(元/kg)")
    private java.math.BigDecimal totalCostAvg;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15)
    @Schema(description = "删除标识")
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

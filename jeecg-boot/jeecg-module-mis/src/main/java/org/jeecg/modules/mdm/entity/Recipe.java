package org.jeecg.modules.mdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 配方表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
@Schema(description="配方表")
@Data
@TableName("mis_recipe")
public class Recipe implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**配方编号*/
	@Excel(name = "配方编号", width = 15)
    @Schema(description = "配方编号")
    private String recipeCode;
	/**配方名称*/
	@Excel(name = "配方名称", width = 15)
    @Schema(description = "配方名称")
    private String recipeName;
	/**技术要求*/
	@Excel(name = "技术要求", width = 15)
    @Schema(description = "技术要求")
    private String technics;
	/**主配人*/
	@Excel(name = "主配人", width = 15)
    @Schema(description = "主配人")
    private String formulatorFirst;
	/**副配人*/
	@Excel(name = "副配人", width = 15)
    @Schema(description = "副配人")
    private String formulatorSecond;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**注意事项*/
	@Excel(name = "注意事项", width = 15)
    @Schema(description = "注意事项")
    private String notes;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "mdm_recipe_status")
    @Dict(dicCode = "mdm_recipe_status")
    @Schema(description = "状态")
    private String status;
    /**工艺id*/
    @Excel(name = "工艺id", width = 15)
    @Schema(description = "工艺id")
    private String routingId;
    /**工艺名称*/
    @Excel(name = "工艺名称", width = 15)
    @Schema(description = "工艺名称")
    private String routingName;
    /**工艺版本*/
    @Excel(name = "工艺版本", width = 15)
    @Schema(description = "工艺版本")
    private String routingVersion;

    /**占比类型*/
    @Excel(name = "占比类型", width = 15 , dicCode = "mdm_recipe_status")
    @Dict(dicCode = "mdm_proportion_type")
    @Schema(description = "占比类型")
    private String proportionType;
    /**总占比*/
    @Excel(name = "总占比", width = 15)
    @Schema(description = "总占比")
    private BigDecimal proportionTotal;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
	/**公司编号*/
	@Excel(name = "公司编号", width = 15)
    @Schema(description = "公司编号")
    private String companyCode;
    /**版本*/
    @Excel(name = "版本", width = 15)
    @Schema(description = "版本")
    private String version;
    /**是否发布*/
    @Excel(name = "是否发布", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @Schema(description = "是否发布")
    private String publishStatus;
    /**发布人*/
    @Schema(description = "发布人")
    private String publishBy;
    /**发布日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发布日期")
    private Date publishTime;
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

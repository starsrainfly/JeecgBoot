package org.jeecg.modules.mis.Recipe.vo;

import java.util.List;
import org.jeecg.modules.mis.Recipe.entity.Recipe;
import org.jeecg.modules.mis.Recipe.entity.RecipeDetail;
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
 * @Description: 配方表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
@Data
@Schema(description="配方表")
public class RecipePage {

	/**id*/
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
	@Excel(name = "状态", width = 15, dicCode = "status")
    @Dict(dicCode = "status")
	@Schema(description = "状态")
    private String status;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
	@Schema(description = "是否删除")
    private String delFlag;
	/**公司编号*/
	@Excel(name = "公司编号", width = 15)
	@Schema(description = "公司编号")
    private String companyCode;
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

	@ExcelCollection(name="配方明细")
	@Schema(description = "配方明细")
	private List<RecipeDetail> recipeDetailList;

}

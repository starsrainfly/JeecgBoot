package org.jeecg.modules.mdm.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: mis_recipe_spec
 * @Author: jeecg-boot
 * @Date:   2026-01-13
 * @Version: V1.0
 */
@Data
@TableName("mis_recipe_spec")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="mis_recipe_spec")
public class RecipeSpec implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**mis_recipe.id*/
	@Excel(name = "mis_recipe.id", width = 15)
    @Schema(description = "mis_recipe.id")
    private String recipeId;
	/**颜色*/
	@Excel(name = "颜色", width = 15)
    @Schema(description = "颜色")
    private String color;
	/**触变因子*/
	@Excel(name = "触变因子", width = 15)
    @Schema(description = "触变因子")
    private BigDecimal thixotropyFactor;
	/**比重*/
	@Excel(name = "比重", width = 15)
    @Schema(description = "比重")
    private BigDecimal specificWeight;
	/**粘度（mPa·s）*/
	@Excel(name = "粘度（mPa·s）", width = 15)
    @Schema(description = "粘度（mPa·s）")
    private BigDecimal viscosity;
	/**A组分比重*/
	@Excel(name = "A组分比重", width = 15)
    @Schema(description = "A组分比重")
    private BigDecimal specificWeightA;
	/**B组分比重*/
	@Excel(name = "B组分比重", width = 15)
    @Schema(description = "B组分比重")
    private BigDecimal specificWeightB;
	/**A组分粘度*/
	@Excel(name = "A组分粘度", width = 15)
    @Schema(description = "A组分粘度")
    private BigDecimal viscosityA;
	/**B组分粘度*/
	@Excel(name = "B组分粘度", width = 15)
    @Schema(description = "B组分粘度")
    private BigDecimal viscosityB;
	/**混合后粘度*/
	@Excel(name = "混合后粘度", width = 15)
    @Schema(description = "混合后粘度")
    private BigDecimal viscosityMix;
	/**硬度（Shore D）*/
	@Excel(name = "硬度（Shore D）", width = 15)
    @Schema(description = "硬度（Shore D）")
    private BigDecimal hardness;
	/**拉伸强度（MPa）*/
	@Excel(name = "拉伸强度（MPa）", width = 15)
    @Schema(description = "拉伸强度（MPa）")
    private BigDecimal pull;
	/**抗弯强度（MPa）*/
	@Excel(name = "抗弯强度（MPa）", width = 15)
    @Schema(description = "抗弯强度（MPa）")
    private BigDecimal bending;
	/**抗压强度（MPa）*/
	@Excel(name = "抗压强度（MPa）", width = 15)
    @Schema(description = "抗压强度（MPa）")
    private BigDecimal compression;
	/**剪切强度（MPa）*/
	@Excel(name = "剪切强度（MPa）", width = 15)
    @Schema(description = "剪切强度（MPa）")
    private BigDecimal shearBondStrength;
	/**体积电阻率（Ω·cm）*/
	@Excel(name = "体积电阻率（Ω·cm）", width = 15)
    @Schema(description = "体积电阻率（Ω·cm）")
    private BigDecimal volumeResistivity;
	/**电气强度（kV/mm）*/
	@Excel(name = "电气强度（kV/mm）", width = 15)
    @Schema(description = "电气强度（kV/mm）")
    private BigDecimal electricStrength;
	/**击穿电压（kV）*/
	@Excel(name = "击穿电压（kV）", width = 15)
    @Schema(description = "击穿电压（kV）")
    private BigDecimal breakdownVoltage;
	/**介电常数（@1kHz）*/
	@Excel(name = "介电常数（@1kHz）", width = 15)
    @Schema(description = "介电常数（@1kHz）")
    private BigDecimal dielectricConstant;
	/**膨胀系数（1/℃）*/
	@Excel(name = "膨胀系数（1/℃）", width = 15)
    @Schema(description = "膨胀系数（1/℃）")
    private BigDecimal expansivity;
	/**吸水率（%）*/
	@Excel(name = "吸水率（%）", width = 15)
    @Schema(description = "吸水率（%）")
    private BigDecimal waterAbsorption;
	/**耐温范围（如 -50~200℃）*/
	@Excel(name = "耐温范围（如 -50~200℃）", width = 15)
    @Schema(description = "耐温范围（如 -50~200℃）")
    private String temperature;
	/**固化条件（如 25℃/24h）*/
	@Excel(name = "固化条件（如 25℃/24h）", width = 15)
    @Schema(description = "固化条件（如 25℃/24h）")
    private String cureCondition;
	/**光泽度*/
	@Excel(name = "光泽度", width = 15)
    @Schema(description = "光泽度")
    private BigDecimal gloss;
	/**胶化时间（分）*/
	@Excel(name = "胶化时间（分）", width = 15)
    @Schema(description = "胶化时间（分）")
    private Integer gelTime;
	/**流动性（如 无流淌）*/
	@Excel(name = "流动性（如 无流淌）", width = 15)
    @Schema(description = "流动性（如 无流淌）")
    private String mobility;
	/**A:B 配比（如 100:30）*/
	@Excel(name = "A:B 配比（如 100:30）", width = 15)
    @Schema(description = "A:B 配比（如 100:30）")
    private String proportionAb;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;
	/**修改人*/
    @Schema(description = "修改人")
    private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间")
    private Date updateTime;
	/**部门*/
    @Schema(description = "部门")
    private String sysOrgCode;
}

package org.jeecg.modules.mes.entity;

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
 * @Description: 物料需求表
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Data
@TableName("mis_material_requirement")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="物料需求表")
public class MaterialRequirement implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**需求来源类型*/
	@Excel(name = "需求来源类型", width = 15)
    @Schema(description = "需求来源类型")
    private String sourceType;
	/**来源单据id*/
	@Excel(name = "来源单据id", width = 15)
    @Schema(description = "来源单据id")
    private String sourceId;
	/**来源单号*/
	@Excel(name = "来源单号", width = 15)
    @Schema(description = "来源单号")
    private String sourceNo;
	/**物料id*/
	@Excel(name = "物料id", width = 15)
    @Schema(description = "物料id")
    private String materialId;
	/**物料编码*/
	@Excel(name = "物料编码", width = 15)
    @Schema(description = "物料编码")
    private String materialCode;
	/**物料名称*/
	@Excel(name = "物料名称", width = 15)
    @Schema(description = "物料名称")
    private String materialName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String materialSpec;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
	/**需求数量*/
	@Excel(name = "需求数量", width = 15)
    @Schema(description = "需求数量")
    private BigDecimal requiredQty;
	/**已发数量*/
	@Excel(name = "已发数量", width = 15)
    @Schema(description = "已发数量")
    private BigDecimal issuedQty;
	/**需求日期*/
	@Excel(name = "需求日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "需求日期")
    private Date requiredDate;
	/**优先级*/
	@Excel(name = "优先级", width = 15)
    @Schema(description = "优先级")
    private Integer priority;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "mes_material_requirement_status")
	@Dict(dicCode = "mes_material_requirement_status")
    @Schema(description = "状态")
    private String status;
	/**目标仓库*/
	@Excel(name = "目标仓库", width = 15)
    @Schema(description = "目标仓库")
    private String warehouseId;
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

package org.jeecg.modules.mis.product.entity;

import java.io.Serializable;
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
import java.io.UnsupportedEncodingException;

/**
 * @Description: 产品信息
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
@Data
@TableName("mis_product")
@Schema(description="产品信息")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**型号规格*/
	@Excel(name = "型号规格", width = 15)
    @Schema(description = "型号规格")
    private String productSpec;
	/**配方id*/
	@Excel(name = "配方id", width = 15)
    @Schema(description = "配方id")
    private String recipeId;
	/**配方编码*/
	@Excel(name = "配方编码", width = 15)
    @Schema(description = "配方编码")
    private String recipeCode;
	/**产品描述*/
	@Excel(name = "产品描述", width = 15)
    @Schema(description = "产品描述")
    private String description;
	/**特点*/
	@Excel(name = "特点", width = 15)
    @Schema(description = "特点")
    private String characteristic;
	/**颜色*/
	@Excel(name = "颜色", width = 15)
    @Schema(description = "颜色")
    private String color;
	/**用途*/
	@Excel(name = "用途", width = 15)
    @Schema(description = "用途")
    private String purpose;
	/**粘度*/
	@Excel(name = "粘度", width = 15)
    @Schema(description = "粘度")
    private String viscosity;
	/**触变*/
	@Excel(name = "触变", width = 15)
    @Schema(description = "触变")
    private String thixotropy;
	/**密度比重*/
	@Excel(name = "密度比重", width = 15)
    @Schema(description = "密度比重")
    private String density;
	/**保存期*/
	@Excel(name = "保存期", width = 15)
    @Schema(description = "保存期")
    private BigDecimal shelfLife;
	/**硬度*/
	@Excel(name = "硬度", width = 15)
    @Schema(description = "硬度")
    private String hardness;
	/**拉力*/
	@Excel(name = "拉力", width = 15)
    @Schema(description = "拉力")
    private String pull;
	/**配比*/
	@Excel(name = "配比", width = 15)
    @Schema(description = "配比")
    private String proportion;
	/**光泽度*/
	@Excel(name = "光泽度", width = 15)
    @Schema(description = "光泽度")
    private String gloss;
	/**固化条件*/
	@Excel(name = "固化条件", width = 15)
    @Schema(description = "固化条件")
    private String cureCondition;
	/**耐温(℃)*/
	@Excel(name = "耐温(℃)", width = 15)
    @Schema(description = "耐温(℃)")
    private String temperature;
	/**胶化时间(min)*/
	@Excel(name = "胶化时间(min)", width = 15)
    @Schema(description = "胶化时间(min)")
    private String gelTime;
	/**抗弯强度*/
	@Excel(name = "抗弯强度", width = 15)
    @Schema(description = "抗弯强度")
    private String bending;
	/**抗压强度*/
	@Excel(name = "抗压强度", width = 15)
    @Schema(description = "抗压强度")
    private String compression;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
	@Dict(dicCode = "status")
    @Schema(description = "状态")
    private String status;
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
	/**父级节点*/
	@Excel(name = "父级节点", width = 15)
    @Schema(description = "父级节点")
    private String pid;
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否有子节点")
    private String hasChild;
}

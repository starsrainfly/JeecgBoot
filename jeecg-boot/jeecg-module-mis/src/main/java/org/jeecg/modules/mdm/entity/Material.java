package org.jeecg.modules.mdm.entity;

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
 * @Description: 物料表
 * @Author: jeecg-boot
 * @Date:   2026-02-03
 * @Version: V1.0
 */
@Data
@TableName("mis_material")
@Schema(description="物料表")
public class Material implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
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
	@Excel(name = "是否有子节点", width = 15)
    @Schema(description = "是否有子节点")
    private String hasChild;
	/**物料编码*/
	@Excel(name = "物料编码", width = 15)
    @Schema(description = "物料编码")
    private String materialCode;
	/**物料名称*/
	@Excel(name = "物料名称", width = 15)
    @Schema(description = "物料名称")
    private String materialName;
	/**物料英文名称*/
	@Excel(name = "物料英文名称", width = 15)
    @Schema(description = "物料英文名称")
    private String materialNameEn;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @Schema(description = "描述")
    private String description;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String materialSpec;
	/**版本*/
	@Excel(name = "版本", width = 15)
    @Schema(description = "版本")
    private String version;
	/**是否符合ROHS*/
	@Excel(name = "是否符合ROHS", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否符合ROHS")
    private String isRohs;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
    @Excel(name="效期预警天数")
    @Schema(description = "效期预警天数")
    private Integer expiryAlertDays;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
	@Dict(dicCode = "status")
    @Schema(description = "状态")
    private String status;
	/**是否为包装物料*/
	@Excel(name = "是否为包装物料", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否为包装物料")
    private String isPackage;
	/**包装容量数值*/
	@Excel(name = "包装容量数值", width = 15)
    @Schema(description = "包装容量数值")
    private BigDecimal packageCapacity;
	/**包装容量单位*/
	@Excel(name = "包装容量单位", width = 15)
    @Schema(description = "包装容量单位")
    private String packageCapacityUnit;
    /**包装类型*/
    @Excel(name = "包装类型", width = 15, dicCode = "mdm_package_type")
    @Dict(dicCode = "mdm_package_type")
    @Schema(description = "包装类型")
    private String packageType;
    /**材料类型*/
    @Excel(name = "材料类型", width = 15, dicCode = "mdm_material_type")
    @Dict(dicCode = "mdm_material_type")
    @Schema(description = "材料类型")
    private String materialType;
}

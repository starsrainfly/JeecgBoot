package org.jeecg.modules.mis.material.entity;

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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 物料表
 * @Author: jeecg-boot
 * @Date:   2024-11-14
 * @Version: V1.0
 */
@Data
@TableName("mis_material")
@ApiModel(value="mis_material对象", description="物料表")
public class Material implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**父级节点*/
	@Excel(name = "父级节点", width = 15)
    @ApiModelProperty(value = "父级节点")
    private String pid;
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;
	/**物料编码*/
	@Excel(name = "物料编码", width = 15)
    @ApiModelProperty(value = "物料编码")
    private String materialCode;
	/**物料名称*/
	@Excel(name = "物料名称", width = 15)
    @ApiModelProperty(value = "物料名称")
    private String materialName;
	/**物料英文名称*/
	@Excel(name = "物料英文名称", width = 15)
    @ApiModelProperty(value = "物料英文名称")
    private String materialNameEn;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String description;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String materialSpec;
	/**版本*/
	@Excel(name = "版本", width = 15)
    @ApiModelProperty(value = "版本")
    private String version;
	/**是否符合ROHS*/
	@Excel(name = "是否符合ROHS", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否符合ROHS")
    private String isrohs;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否删除")
    @TableLogic
    private String delFlag;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
	@Dict(dicCode = "status")
    @ApiModelProperty(value = "状态")
    private String status;
}

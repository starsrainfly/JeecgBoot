package org.jeecg.modules.mdm.entity;

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
 * @Description: 统一库存项表
 * @Author: jeecg-boot
 * @Date:   2026-01-15
 * @Version: V1.0
 */
@Data
@TableName("mis_item")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="统一库存项表")
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**项目类型*/
	@Excel(name = "项目类型", width = 15, dicCode = "item_type")
	@Dict(dicCode = "item_type")
    @Schema(description = "项目类型")
    private String itemType;
	/**项目id*/
	@Excel(name = "项目id", width = 15)
    @Schema(description = "项目id")
    private String itemId;
	/**编码*/
	@Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String code;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @Schema(description = "名称")
    private String name;
	/**型号规格*/
	@Excel(name = "型号规格", width = 15)
    @Schema(description = "型号规格")
    private String spec;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String isActive;
	/**所属部门*/
    @Schema(description = "所属部门")
    private String sysOrgCode;
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
}

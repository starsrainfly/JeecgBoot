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
 * @Description: 系统配置
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@TableName("mis_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="系统配置")
public class MisConfig implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**所属模块*/
	@Excel(name = "所属模块", width = 15, dicCode = "sys_module")
	@Dict(dicCode = "sys_module")
    @Schema(description = "所属模块")
    private String configModule;
	/**配置编码*/
	@Excel(name = "配置编码", width = 15)
    @Schema(description = "配置编码")
    private String configCode;
	/**配置名称*/
	@Excel(name = "配置名称", width = 15)
    @Schema(description = "配置名称")
    private String configName;
	/**配置值*/
	@Excel(name = "配置值", width = 15)
    @Schema(description = "配置值")
    private String configValue;
	/**值类型*/
	@Excel(name = "值类型", width = 15, dicCode = "sys_config_type")
	@Dict(dicCode = "sys_config_type")
    @Schema(description = "值类型")
    private String configType;
	/**说明*/
	@Excel(name = "说明", width = 15)
    @Schema(description = "说明")
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

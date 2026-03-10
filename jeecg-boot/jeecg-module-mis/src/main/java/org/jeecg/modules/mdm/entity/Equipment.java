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
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
@Data
@TableName("mis_equipment")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="设备表")
public class Equipment implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @Schema(description = "设备编号")
    private String equipmentCode;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @Schema(description = "设备名称")
    private String equipmentName;
	/**型号*/
	@Excel(name = "型号", width = 15)
    @Schema(description = "型号")
    private String model;
	/**设备类型*/
	@Excel(name = "设备类型", width = 15, dicCode = "mdm_equipment_type")
	@Dict(dicCode = "mdm_equipment_type")
    @Schema(description = "设备类型")
    private String equipmentType;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "mdm_equipment_status")
	@Dict(dicCode = "mdm_equipment_status")
    @Schema(description = "状态")
    private String status;
	/**位置*/
	@Excel(name = "位置", width = 15)
    @Schema(description = "位置")
    private String location;
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

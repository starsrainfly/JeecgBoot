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
 * @Description: 派工管理
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Data
@TableName("mis_production_step")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="派工管理")
public class ProductionStep implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**批号id*/
	@Excel(name = "批号id", width = 15)
    @Schema(description = "批号id")
    private String batchId;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNo;
	/**生产订单号*/
	@Excel(name = "生产订单号", width = 15)
    @Schema(description = "生产订单号")
    private String orderNo;
	/**产品编号*/
	@Excel(name = "产品编号", width = 15)
    @Schema(description = "产品编号")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**顺序*/
	@Excel(name = "顺序", width = 15)
    @Schema(description = "顺序")
    private Integer stepSeq;
	/**步骤编号*/
	@Excel(name = "步骤编号", width = 15)
    @Schema(description = "步骤编号")
    private String stepCode;
	/**步骤名称*/
	@Excel(name = "步骤名称", width = 15)
    @Schema(description = "步骤名称")
    private String stepName;
	/**操作说明*/
	@Excel(name = "操作说明", width = 15)
    @Schema(description = "操作说明")
    private String stepDesc;
	/**计划设备id*/
	@Excel(name = "计划设备id", width = 15)
    @Schema(description = "计划设备id")
    private String equipmentId;
	/**计划设备编码*/
	@Excel(name = "计划设备编码", width = 15)
    @Schema(description = "计划设备编码")
    private String equipmentCode;
	/**计划设备名称*/
	@Excel(name = "计划设备名称", width = 15)
    @Schema(description = "计划设备名称")
    private String equipmentName;
	/**计划设备型号*/
	@Excel(name = "计划设备型号", width = 15)
    @Schema(description = "计划设备型号")
    private String model;
	/**计划设备类型*/
	@Excel(name = "计划设备类型", width = 15, dicCode = "mdm_equipment_type")
	@Dict(dicCode = "mdm_equipment_type")
    @Schema(description = "计划设备类型")
    private String equipmentType;
	/**计划耗时（单位分钟））*/
	@Excel(name = "计划耗时（单位分钟））", width = 15)
    @Schema(description = "计划耗时（单位分钟））")
    private Integer duration;
	/**计划设备设置*/
	@Excel(name = "计划设备设置", width = 15)
    @Schema(description = "计划设备设置")
    private String equipmentSettings;
	/**实际使用设备id*/
	@Excel(name = "实际使用设备id", width = 15)
    @Schema(description = "实际使用设备id")
    private String actualEquipmentId;
	/**实际设备编码*/
	@Excel(name = "实际设备编码", width = 15)
    @Schema(description = "实际设备编码")
    private String actualEquipmentCode;
	/**实际设备名称*/
	@Excel(name = "实际设备名称", width = 15)
    @Schema(description = "实际设备名称")
    private String actualEquipmentName;
	/**实际设备型号*/
	@Excel(name = "实际设备型号", width = 15)
    @Schema(description = "实际设备型号")
    private String actualModel;
	/**实际设备类型*/
	@Excel(name = "实际设备类型", width = 15, dicCode = "mdm_equipment_type")
	@Dict(dicCode = "mdm_equipment_type")
    @Schema(description = "实际设备类型")
    private String actualEquipmentType;
	/**实际耗时（分钟）*/
	@Excel(name = "实际耗时（分钟）", width = 15)
    @Schema(description = "实际耗时（分钟）")
    private Integer actualDuration;
	/**实际设备设置*/
	@Excel(name = "实际设备设置", width = 15)
    @Schema(description = "实际设备设置")
    private String actualEquipmentSettings;
	/**指派操作员id*/
	@Excel(name = "指派操作员id", width = 15)
    @Schema(description = "指派操作员id")
    private String assignedOperatorId;
	/**指派操作员*/
	@Excel(name = "指派操作员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Schema(description = "指派操作员")
    private String assignedOperatorName;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "mes_step_status")
	@Dict(dicCode = "mes_step_status")
    @Schema(description = "状态")
    private String status;
	/**实际开始时间*/
	@Excel(name = "实际开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实际开始时间")
    private Date actualStart;
	/**实际结束时间*/
	@Excel(name = "实际结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实际结束时间")
    private Date actualEnd;
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

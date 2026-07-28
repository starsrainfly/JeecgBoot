package org.jeecg.modules.mes.entity;

import java.io.Serializable;
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
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 质检记录明细
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
@Schema(description="质检记录明细")
@Data
@TableName("mis_qc_record_detail")
public class QcRecordDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**质检记录id*/
    @Schema(description = "质检记录id")
    private String recordId;
	/**检验项目*/
	@Excel(name = "检验项目", width = 15)
    @Schema(description = "检验项目")
    private String itemName;
	/**标准要求*/
	@Excel(name = "标准要求", width = 15)
    @Schema(description = "标准要求")
    private String standard;
	/**实测值*/
	@Excel(name = "实测值", width = 15)
    @Schema(description = "实测值")
    private String actualValue;
	/**单项结果(pass合格/fail不合格)*/
	@Excel(name = "单项结果(pass合格/fail不合格)", width = 15, dicCode = "mes_qc_status")
    @Schema(description = "单项结果(pass合格/fail不合格)")
    private String itemResult;
	/**检测设备id*/
	@Excel(name = "检测设备id", width = 15)
    @Schema(description = "检测设备id")
    private String equipmentId;
	/**检测设备编码*/
	@Excel(name = "检测设备编码", width = 15)
    @Schema(description = "检测设备编码")
    private String equipmentCode;
	/**检测设备名称*/
	@Excel(name = "检测设备名称", width = 15)
    @Schema(description = "检测设备名称")
    private String equipmentName;
	/**排序号*/
	@Excel(name = "排序号", width = 15)
    @Schema(description = "排序号")
    private Integer sortNo;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
}

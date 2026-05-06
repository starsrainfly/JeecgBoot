package org.jeecg.modules.mdm.entity;

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
 * @Description: 工序步骤
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
@Schema(description="工序步骤")
@Data
@TableName("mis_process_routing_step")
public class ProcessRoutingStep implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**工艺id*/
    @Schema(description = "工艺id")
    private String routingId;
	/**工序顺序*/
	@Excel(name = "工序顺序", width = 15)
    @Schema(description = "工序顺序")
    private Integer stepSeq;
	/**工序编码*/
	@Excel(name = "工序编码", width = 15)
    @Schema(description = "工序编码")
    private String stepCode;
	/**工序名称*/
	@Excel(name = "工序名称", width = 15)
    @Schema(description = "工序名称")
    private String stepName;
	/**操作要求*/
	@Excel(name = "操作要求", width = 15)
    @Schema(description = "操作要求")
    private String stepDesc;
	/**设备id*/
	@Excel(name = "设备id", width = 15)
    @Schema(description = "设备id")
    private String equipmentId;
	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @Schema(description = "设备编码")
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
	@Excel(name = "设备类型", width = 15)
    @Schema(description = "设备类型")
    private String equipmentType;
	/**设备设置*/
	@Excel(name = "设备设置", width = 15)
    @Schema(description = "设备设置")
    private String equipmentSettings;
	/**标准耗时（单位：分）*/
	@Excel(name = "标准耗时（单位：分）", width = 15)
    @Schema(description = "标准耗时（单位：分）")
    private Integer duration;
	/**所需设备数量*/
	@Excel(name = "所需设备数量", width = 15)
    @Schema(description = "所需设备数量")
    private Integer requiredEquipmentCount;
	/**是否涉及物料*/
	@Excel(name = "是否配料", width = 15, dicCode = "yn")
    @Schema(description = "是否配料")
    private String isMaterialStep;
    /**是否涉及物料*/
    @Excel(name = "是否包装", width = 15, dicCode = "yn")
    @Schema(description = "是否包装")
    private String isPackageStep;
	/**是否需要质检*/
	@Excel(name = "是否需要质检", width = 15, dicCode = "yn")
    @Schema(description = "是否需要质检")
    private String qcRequired;
    /**是否完工工序（最后工序）*/
    @Excel(name = "是否完工工序（最后工序）", width = 15, dicCode = "yn")
    @Schema(description = "是否完工工序（最后工序）")
    private String isFinishStep;
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

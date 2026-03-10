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
 * @Description: 生产批次物料清单
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Schema(description="生产批次物料清单")
@Data
@TableName("mis_production_batch_bom")
public class ProductionBatchBom implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**生产批次id*/
    @Schema(description = "生产批次id")
    private String batchId;
	/**排序号*/
	@Excel(name = "排序号", width = 15)
    @Schema(description = "排序号")
    private Integer serialNo;
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
	/**型号规格*/
	@Excel(name = "型号规格", width = 15)
    @Schema(description = "型号规格")
    private String materialSpec;
	/**配比*/
	@Excel(name = "配比", width = 15)
    @Schema(description = "配比")
    private java.math.BigDecimal proportion;
	/**需求数量*/
	@Excel(name = "需求数量", width = 15)
    @Schema(description = "需求数量")
    private java.math.BigDecimal plannedQty;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15, dicCode = "del_flag")
    @Schema(description = "是否删除")
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

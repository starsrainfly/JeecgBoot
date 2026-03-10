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
 * @Description: 生产实际投料明细
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Data
@TableName("mis_production_batch_material_actual")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="生产实际投料明细")
public class ProductionBatchMaterialActual implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**批次id*/
	@Excel(name = "批次id", width = 15)
    @Schema(description = "批次id")
    private String batchId;
	/**bomid*/
	@Excel(name = "bomid", width = 15)
    @Schema(description = "bomid")
    private String batchBomId;
	/**生产订单号*/
	@Excel(name = "生产订单号", width = 15)
    @Schema(description = "生产订单号")
    private String orderNo;
	/**批次编号*/
	@Excel(name = "批次编号", width = 15)
    @Schema(description = "批次编号")
    private String batchNo;
	/**产品id*/
	@Excel(name = "产品id", width = 15)
    @Schema(description = "产品id")
    private String productId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**bom顺序号*/
	@Excel(name = "bom顺序号", width = 15)
    @Schema(description = "bom顺序号")
    private Integer bomSerialNo;
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
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String materialSpec;
	/**计划用量(kg)*/
	@Excel(name = "计划用量(kg)", width = 15)
    @Schema(description = "计划用量(kg)")
    private BigDecimal plannedQty;
	/**实际称重(kg)*/
	@Excel(name = "实际称重(kg)", width = 15)
    @Schema(description = "实际称重(kg)")
    private BigDecimal actualQty;
	/**操作员id*/
	@Excel(name = "操作员id", width = 15)
    @Schema(description = "操作员id")
    private String operatorId;
	/**操作员*/
	@Excel(name = "操作员", width = 15)
    @Schema(description = "操作员")
    private String operatorName;
	/**完成时间*/
	@Excel(name = "完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "完成时间")
    private Date completeTime;
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

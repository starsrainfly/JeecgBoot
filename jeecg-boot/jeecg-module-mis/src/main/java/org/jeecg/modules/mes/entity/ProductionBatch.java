package org.jeecg.modules.mes.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
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

/**
 * @Description: 生产批次
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Schema(description="生产批次")
@Data
@TableName("mis_production_batch")
public class ProductionBatch implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**生产订单id*/
	@Excel(name = "生产订单id", width = 15)
    @Schema(description = "生产订单id")
    private String orderId;
	/**订单编号*/
	@Excel(name = "订单编号", width = 15)
    @Schema(description = "订单编号")
    private String orderNo;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNo;
	/**序号*/
	@Excel(name = "序号", width = 15)
    @Schema(description = "序号")
    private Integer batchSeq;
    @Excel(name = "产品id", width = 15)
    @Schema(description = "产品id")
    private String productId;
    @Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
    @Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
    @Excel(name = "配方id", width = 15)
    @Schema(description = "配方id")
    private String recipeId;
    @Excel(name = "配方名称", width = 15)
    @Schema(description = "配方名称")
    private String recipeName;
    @Excel(name = "配方编码", width = 15)
    @Schema(description = "配方编码")
    private String recipeCode;
    @Excel(name = "配方版本", width = 15)
    @Schema(description = "配方版本")
    private String recipeVersion;
	/**计划生产量(Kg)*/
	@Excel(name = "计划生产量(Kg)", width = 15)
    @Schema(description = "计划生产量(Kg)")
    private java.math.BigDecimal plannedQty;
	/**实际生产量(Kg)*/
	@Excel(name = "实际生产量(Kg)", width = 15)
    @Schema(description = "实际生产量(Kg)")
    private java.math.BigDecimal actualQty;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "mes_batch_status")
    @Dict(dicCode = "mes_batch_status")
    @Schema(description = "状态")
    private String status;
    /**生产日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "生产日期")
    private Date productionDate;
    /**质保天数*/
    @Excel(name = "质保天数", width = 15)
    @Schema(description = "质保天数")
    private Integer shelfLife;
    /**失效日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "失效日期")
    private Date expiryDate;
    /**已入库数量*/
    @Excel(name = "已入库数量", width = 15)
    @Schema(description = "已入库数量")
    private Integer inStockQty;
    /**剩余可入库数量*/
    @Excel(name = "剩余可入库数量", width = 15)
    @Schema(description = "剩余可入库数量")
    private Integer remainQty;
    /**入库状态 0未入库，1部分入库，2已入库*/
    @Excel(name = "入库状态", width = 15)
    @Schema(description = "入库状态")
    private String inStockStatus;
    @Excel(name = "质检状态", width = 15, dicCode = "mes_qc_status")
    @Dict(dicCode = "mes_qc_status")
    @Schema(description = "质检状态")
    private String qcStatus;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15, dicCode = "del_flag")
    @Dict(dicCode = "del_flag")
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

    // 配料时间记录（用于审计和追溯）
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "配料开始时间")
    private Date weighingStartTime;   // 第一次称重时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "配料完成时间")
    private Date weighingEndTime;     // 配料完成时间
}

package org.jeecg.modules.mes.vo;

import java.util.List;
import org.jeecg.modules.mes.entity.ProductionBatch;
import org.jeecg.modules.mes.entity.ProductionBatchBom;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 生产批次
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Data
@Schema(description="生产批次")
public class ProductionBatchPage {

	/**主键*/
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
	/**是否删除*/
	@Excel(name = "是否删除", width = 15, dicCode = "del_flag")
    @Dict(dicCode = "del_flag")
	@Schema(description = "是否删除")
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

	// ==================== 新增配料汇总信息字段 ====================

	/**物料总数*/
	@Excel(name = "物料总数", width = 10)
	@Schema(description = "该批次物料清单总数")
	private Integer totalBom;

	/**已完成配料的物料数*/
	@Excel(name = "已完成物料数", width = 12)
	@Schema(description = "已完成配料的物料数量")
	private Integer completedBom;

	/**配料完成百分比*/
	@Excel(name = "配料进度%", width = 12)
	@Schema(description = "配料完成百分比")
	private Integer percent;

	/**实际总投料量(Kg) - 所有物料称重总和*/
	@Excel(name = "实际总投料量(Kg)", width = 15)
	@Schema(description = "所有物料实际称重总和")
	private java.math.BigDecimal totalActualWeight;

	/**配料状态 - 更细粒度的状态*/
	@Excel(name = "配料状态", width = 12, dicCode = "mes_weighing_status")
	@Dict(dicCode = "mes_weighing_status")
	@Schema(description = "配料状态：PENDING-待配料，WEIGHING-配料中，WEIGHED-配料完成")
	private String weighingStatus;

	/**配料开始时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Excel(name = "配料开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "第一次称重时间")
	private Date weighingStartTime;

	/**配料完成时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Excel(name = "配料完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "所有物料配料完成时间")
	private Date weighingEndTime;

	/**主要配料操作员*/
	@Excel(name = "配料操作员", width = 15)
	@Schema(description = "主要配料操作员")
	private String weighingOperator;

	// =========================================================

	@ExcelCollection(name="生产批次物料清单")
	@Schema(description = "生产批次物料清单")
	private List<ProductionBatchBom> productionBatchBomList;

}

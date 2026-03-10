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
 * @Description: 生产计划明细表
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
@Schema(description="生产计划明细表")
@Data
@TableName("mis_production_plan_detail")
public class ProductionPlanDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**计划id*/
    @Schema(description = "计划id")
    private String planId;
	/**销售行id*/
	@Excel(name = "销售行id", width = 15)
    @Schema(description = "销售行id")
    private String salesOrderDetailId;
	/**销售订单id*/
	@Excel(name = "销售订单id", width = 15)
    @Schema(description = "销售订单id")
    private String salesOrderId;
	/**订单编号*/
	@Excel(name = "订单编号", width = 15)
    @Schema(description = "订单编号")
    private String salesOrderNo;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**产品规格*/
	@Excel(name = "产品规格", width = 15)
    @Schema(description = "产品规格")
    private String productSpec;
	/**订单数量*/
	@Excel(name = "订单数量", width = 15)
    @Schema(description = "订单数量")
    private java.math.BigDecimal demandQty;
	/**分配数量*/
	@Excel(name = "分配数量", width = 15)
    @Schema(description = "分配数量")
    private java.math.BigDecimal allocatedQty;
	/**已完成数量*/
	@Excel(name = "已完成数量", width = 15)
    @Schema(description = "已完成数量")
    private java.math.BigDecimal completedQty;
	/**包装名称*/
	@Excel(name = "包装名称", width = 15)
    @Schema(description = "包装名称")
    private String packageName;
	/**包装规格*/
	@Excel(name = "包装规格", width = 15)
    @Schema(description = "包装规格")
    private String packageSpec;
	/**包装容量*/
	@Excel(name = "包装容量", width = 15)
    @Schema(description = "包装容量")
    private java.math.BigDecimal packageCapacity;
	/**包装单位*/
	@Excel(name = "包装单位", width = 15)
    @Schema(description = "包装单位")
    private String packageCapacityUnit;
	/**客户*/
	@Excel(name = "客户", width = 15)
    @Schema(description = "客户")
    private String customerId;
	/**客户编码*/
	@Excel(name = "客户编码", width = 15)
    @Schema(description = "客户编码")
    private String customerCode;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
    @Schema(description = "客户名称")
    private String customerName;
	/**业务*/
	@Excel(name = "业务", width = 15)
    @Schema(description = "业务")
    private String salesmanId;
	/**业务员*/
	@Excel(name = "业务员", width = 15)
    @Schema(description = "业务员")
    private String salesmanName;
	/**交货日期*/
	@Excel(name = "交货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "交货日期")
    private Date deliveryDate;
	/**包装id*/
	@Excel(name = "包装id", width = 15)
    @Schema(description = "包装id")
    private String packageId;
	/**销售备注*/
	@Excel(name = "销售备注", width = 15)
    @Schema(description = "销售备注")
    private String salesRemark;
	/**计划备注*/
	@Excel(name = "计划备注", width = 15)
    @Schema(description = "计划备注")
    private String planRemark;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "mes_production_status")
    @Schema(description = "状态")
    private String detailStatus;
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
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
}

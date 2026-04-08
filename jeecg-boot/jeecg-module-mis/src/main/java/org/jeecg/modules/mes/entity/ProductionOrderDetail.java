package org.jeecg.modules.mes.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.aspect.annotation.Dict;
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
 * @Description: 生产订单明细
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
@Schema(description="生产订单明细")
@Data
@TableName("mis_production_order_detail")
public class ProductionOrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**生产订单id*/
    @Schema(description = "生产订单id")
    private String orderId;
	/**计划id*/
	@Excel(name = "计划id", width = 15)
    @Schema(description = "计划id")
    private String planId;
	/**计划编码*/
	@Excel(name = "计划编码", width = 15)
    @Schema(description = "计划编码")
    private String planNo;
	/**计划明细id*/
	@Excel(name = "计划明细id", width = 15)
    @Schema(description = "计划明细id")
    private String planDetailId;
	/**销售订单id*/
	@Excel(name = "销售订单id", width = 15)
    @Schema(description = "销售订单id")
    private String salesOrderId;
	/**销售订单*/
	@Excel(name = "销售订单", width = 15)
    @Schema(description = "销售订单")
    private String salesOrderNo;
	/**计划类型*/
	@Excel(name = "计划类型", width = 15, dicCode = "mes_plan_type")
    @Schema(description = "计划类型")
    private String planType;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
    @Schema(description = "客户id")
    private String customerId;
	/**客户编码*/
	@Excel(name = "客户编码", width = 15)
    @Schema(description = "客户编码")
    private String customerCode;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
    @Schema(description = "客户名称")
    private String customerName;
	/**计划分配量*/
	@Excel(name = "计划分配量", width = 15)
    @Schema(description = "计划分配量")
    private java.math.BigDecimal planAllocatedQty;
	/**本次执行数量*/
	@Excel(name = "本次执行数量", width = 15)
    @Schema(description = "本次执行数量")
    private java.math.BigDecimal allocatedQty;
	/**交货日期*/
	@Excel(name = "交货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "交货日期")
    private Date deliverDate;
	/**优化级*/
	@Excel(name = "优化级", width = 15, dicCode = "mes_priority_level")
    @Schema(description = "优化级")
    private String priorityLevel;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;

    /**内包装*/
    @Excel(name = "内包装", width = 15, dictTable = "mis_material where is_package='1' and package_type='0'", dicText = "description", dicCode = "id")
    @Dict(dictTable = "mis_material where is_package='1' and package_type='0'", dicText = "description", dicCode = "id")
    @Schema(description = "内包装")
    private String innerPackageId;
    /**内包装名称*/
    @Excel(name = "内包规格", width = 15)
    @Schema(description = "内包规格")
    private String innerPackageSpec;
    /**内包装容量*/
    @Excel(name = "内包容量", width = 15)
    @Schema(description = "内包容量")
    private java.math.BigDecimal innerPackageCapacity;
    /**内包装单位*/
    @Excel(name = "内包装容量单位", width = 15)
    @Schema(description = "内包装容量单位")
    private String innerPackageCapacityUnit;
    /**内包装数量*/
    @Excel(name = "内包装数量", width = 15)
    @Schema(description = "内包装数量")
    private java.math.BigDecimal innerPackageQty;
    /**外包装单位*/
    @Excel(name = "内包装单位", width = 15)
    @Schema(description = "内包装单位")
    private String innerPackageUnit;
    /**外包装*/
    @Excel(name = "外包装", width = 15, dictTable = "mis_material where is_package='1' and package_type='1'", dicText = "description", dicCode = "id")
    @Dict(dictTable = "mis_material where is_package='1' and package_type='1'", dicText = "description", dicCode = "id")
    @Schema(description = "外包装")
    private String outerPackageId;
    /**外包装单位*/
    @Excel(name = "外包装单位", width = 15)
    @Schema(description = "外包装单位")
    private String outerPackageUnit;
    /**外包装数量*/
    @Excel(name = "外包装数量", width = 15)
    @Schema(description = "外包装数量")
    private java.math.BigDecimal outerPackageQty;
    /**外包装规格*/
    @Excel(name = "外包装规格", width = 15)
    @Schema(description = "外包装规格")
    private String outerPackageSpec;
    /**每外包含内包数量*/
    @Excel(name = "每外包含内包数量", width = 15)
    @Schema(description = "每外包含内包数量")
    private Integer innerPerOuter;
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
}

package org.jeecg.modules.mes.vo;

import java.util.List;
import org.jeecg.modules.mes.entity.ProductionOrder;
import org.jeecg.modules.mes.entity.ProductionOrderDetail;
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
 * @Description: 生产订单
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
@Data
@Schema(description="生产订单")
public class ProductionOrderPage {

	/**主键*/
	@Schema(description = "主键")
    private String id;
	/**生产编号*/
	@Excel(name = "生产编号", width = 15)
	@Schema(description = "生产编号")
    private String orderNo;
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
	/**配方id*/
	@Excel(name = "配方id", width = 15)
	@Schema(description = "配方id")
    private String recipeId;
	/**配方编码*/
	@Excel(name = "配方编码", width = 15)
	@Schema(description = "配方编码")
    private String recipeCode;
	/**配方名称*/
	@Excel(name = "配方名称", width = 15)
	@Schema(description = "配方名称")
    private String recipeName;
	/**计划产量(Kg)*/
	@Excel(name = "计划产量(Kg)", width = 15)
	@Schema(description = "计划产量(Kg)")
    private java.math.BigDecimal plannedQty;
	/**单釜产量(Kg)*/
	@Excel(name = "单釜产量(Kg)", width = 15)
	@Schema(description = "单釜产量(Kg)")
    private java.math.BigDecimal batchSize;
	/**批次数量*/
	@Excel(name = "批次数量", width = 15)
	@Schema(description = "批次数量")
    private Integer batchCount;
	/**内包装*/
	@Excel(name = "内包装", width = 15, dictTable = "mis_material where is_package='1' and package_type='0'", dicText = "description", dicCode = "id")
    @Dict(dictTable = "mis_material where is_package='1' and package_type='0'", dicText = "description", dicCode = "id")
	@Schema(description = "内包装")
    private String innerPackageId;
	/**内包装名称*/
	@Excel(name = "内包装名称", width = 15)
	@Schema(description = "内包装名称")
    private String innerPackageName;
	/**内包装容量*/
	@Excel(name = "内包装容量", width = 15)
	@Schema(description = "内包装容量")
    private java.math.BigDecimal innerPackageCapacity;
	/**内包装单位*/
	@Excel(name = "内包装单位", width = 15)
	@Schema(description = "内包装单位")
    private String innerPackageCapacityUnit;
	/**内包装数量*/
	@Excel(name = "内包装数量", width = 15)
	@Schema(description = "内包装数量")
    private java.math.BigDecimal innerPackageQty;
	/**外包装*/
	@Excel(name = "外包装", width = 15, dictTable = "mis_material where is_package='1' and package_type='1'", dicText = "description", dicCode = "id")
    @Dict(dictTable = "mis_material where is_package='1' and package_type='1'", dicText = "description", dicCode = "id")
	@Schema(description = "外包装")
    private String outerPackageId;
	/**外包装名称*/
	@Excel(name = "外包装名称", width = 15)
	@Schema(description = "外包装名称")
    private String outPackageName;
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
    private Integer outerInnerPerOuter;
	/**计划开工*/
	@Excel(name = "计划开工", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@Schema(description = "计划开工")
    private Date plannedStartDate;
	/**计划完工*/
	@Excel(name = "计划完工", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@Schema(description = "计划完工")
    private Date plannedEndDate;
	/**实际开工*/
	@Excel(name = "实际开工", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "实际开工")
    private Date actualStartTime;
	/**实际完工*/
	@Excel(name = "实际完工", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "实际完工")
    private Date actualEndTime;
	/**交货日期*/
	@Excel(name = "交货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@Schema(description = "交货日期")
    private Date deliveryDate;
	/**状态0草稿1已下达，2部分完成3已完成*/
	@Excel(name = "状态0草稿1已下达，2部分完成3已完成", width = 15, dicCode = "mes_production_status")
    @Dict(dicCode = "mes_production_status")
	@Schema(description = "状态0草稿1已下达，2部分完成3已完成")
    private String status;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
	@Schema(description = "是否删除")
    private String delFlag;
	/**所属部门*/
	@Schema(description = "所属部门")
    private String sysOrgCode;
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

	@ExcelCollection(name="生产订单明细")
	@Schema(description = "生产订单明细")
	private List<ProductionOrderDetail> productionOrderDetailList;

}

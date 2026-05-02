package org.jeecg.modules.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 库存记录表
 * @Author: jeecg-boot
 * @Date:   2026-04-08
 * @Version: V1.0
 */
@Data
@TableName("mis_stock")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="库存记录表")
public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private java.lang.String id;
	/**项目id*/
	@Excel(name = "项目id", width = 15)
    @Schema(description = "项目id")
    private java.lang.String goodsId;
	/**项目编码*/
	@Excel(name = "项目编码", width = 15)
    @Schema(description = "项目编码")
    private java.lang.String goodsCode;
	/**项目名称*/
	@Excel(name = "项目名称", width = 15)
    @Schema(description = "项目名称")
    private java.lang.String goodsName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private java.lang.String goodsSpec;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private java.lang.String unit;
	/**项目类型*/
	@Excel(name = "项目类型", width = 15, dicCode = "wms_item_type")
	@Dict(dicCode = "wms_item_type")
    @Schema(description = "项目类型")
    private java.lang.String goodsType;
	/**仓库id*/
	@Excel(name = "仓库id", width = 15, dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
	@Dict(dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Schema(description = "仓库id")
    private java.lang.String warehouseId;
	/**区域id*/
	@Excel(name = "区域id", width = 15, dictTable = "mis_warehouse_area where del_flag='0' and status='1'", dicText = "area_code", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_area where del_flag='0' and status='1'", dicText = "area_code", dicCode = "id")
    @Schema(description = "区域id")
    private java.lang.String areaId;
	/**货架id*/
	@Excel(name = "货架id", width = 15, dictTable = "mis_warehouse_shelf where del_flag ='0' and status='1'", dicText = "shelf_code", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_shelf where del_flag ='0' and status='1'", dicText = "shelf_code", dicCode = "id")
    @Schema(description = "货架id")
    private java.lang.String shelfId;
	/**位置id*/
	@Excel(name = "位置id", width = 15, dictTable = "mis_warehouse_location where del_flag='0' and status='1'", dicText = "path_code", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_location where del_flag='0' and status='1'", dicText = "path_code", dicCode = "id")
    @Schema(description = "位置id")
    private java.lang.String locationId;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private java.lang.String batchNo;
	/**生产日期*/
	@Excel(name = "生产日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "生产日期")
    private java.util.Date productionDate;
	/**质保天数*/
	@Excel(name = "质保天数", width = 15)
    @Schema(description = "质保天数")
    private java.lang.Integer shelfLife;
	/**过期日（根据shelf_life自动计算）*/
	@Excel(name = "过期日（根据shelf_life自动计算）", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "过期日（根据shelf_life自动计算）")
    private java.util.Date expiryDate;
	/**库存数量*/
	@Excel(name = "库存数量", width = 15)
    @Schema(description = "库存数量")
    private java.math.BigDecimal quantity;
	/**已分配未出库量*/
	@Excel(name = "已分配未出库量", width = 15)
    @Schema(description = "已分配未出库量")
    private java.math.BigDecimal lockedQty;
	/**入库明细id*/
	@Excel(name = "入库明细id", width = 15)
    @Schema(description = "入库明细id")
    private java.lang.String inDetailId;
	/**生产批号id*/
	@Excel(name = "生产批号id", width = 15)
    @Schema(description = "生产批号id")
    private java.lang.String productionBatchId;
	/**供应商id*/
	@Excel(name = "供应商id", width = 15)
    @Schema(description = "供应商id")
    private java.lang.String supplierId;
	/**供应商名称*/
	@Excel(name = "供应商名称", width = 15)
    @Schema(description = "供应商名称")
    private java.lang.String supplierName;
	/**入库时间*/
	@Excel(name = "入库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "入库时间")
    private java.util.Date stockInTime;
	/**原始入库数量*/
	@Excel(name = "原始入库数量", width = 15)
    @Schema(description = "原始入库数量")
    private java.math.BigDecimal originalQty;
    /**安全库存*/
    @Excel(name = "安全库存", width = 15)
    @Schema(description = "安全库存")
    private BigDecimal safetyStockQty;
    /**库存上限*/
    @Excel(name = "库存上限", width = 15)
    @Schema(description = "库存上限")
    private BigDecimal maxStockQty;
    /**是否产品*/
    @Excel(name = "是否产品", width = 15, dicCode = "yn")
    @Schema(description = "是否产品")
    @Dict(dicCode = "yn")
    private String isProduct;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "wms_stock_status")
	@Dict(dicCode = "wms_stock_status")
    @Schema(description = "状态")
    private java.lang.String qcStatus;
    /**是否删除*/
    @Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
	/**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
}

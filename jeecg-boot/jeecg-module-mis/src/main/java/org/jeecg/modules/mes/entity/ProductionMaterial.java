package org.jeecg.modules.mes.entity;

import java.io.Serializable;
import java.util.Date;
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
 * @Description: 物料需求表
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
@Data
@TableName("mis_production_material")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="物料需求表")
public class ProductionMaterial implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**订单id*/
	@Excel(name = "订单id", width = 15)
    @Schema(description = "订单id")
    private String orderId;
    /**生产订单明细id*/
    @Schema(description = "生产订单明细id")
    private String orderDetailId;
	/**生产单号*/
	@Excel(name = "生产单号", width = 15)
    @Schema(description = "生产单号")
    private String orderNo;
    /**生产单号*/
    @Excel(name = "批次id", width = 15)
    @Schema(description = "批次id")
    private String batchId;
    /**生产单号*/
    @Excel(name = "批次单号", width = 15)
    @Schema(description = "批次单号")
    private String batchNo;
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
    /**物料类型 源材料-RAW，内包-package_inner，外包 package_outer*/
    @Excel(name = "物料类型", width = 15)
    @Schema(description = "物料类型")
    private String materialType;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
	/**需求数量*/
	@Excel(name = "需求数量", width = 15)
    @Schema(description = "需求数量")
    private BigDecimal requiredQty;
	/**已发数量*/
	@Excel(name = "已发数量", width = 15)
    @Schema(description = "已发数量")
    private BigDecimal issuedQty;
    /**锁定数量*/
    @Excel(name = "锁定数量", width = 15)
    @Schema(description = "锁定数量")
    private BigDecimal lockedQty;

    /**超量数量*/
    @Excel(name = "超量数量", width = 15)
    @Schema(description = "超量数量")
    private BigDecimal overQty;
	/**剩余待发*/
	@Excel(name = "剩余待发", width = 15)
    @Schema(description = "剩余待发")
    private BigDecimal remainingQty;
	/**需求日期*/
	@Excel(name = "需求日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "需求日期")
    private Date requiredDate;
	/**优先级*/
	@Excel(name = "优先级", width = 15)
    @Schema(description = "优先级")
    private Integer priority;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "mes_production_material_status")
	@Dict(dicCode = "mes_production_material_status")
    @Schema(description = "状态")
    private String status;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**目标仓库*/
	@Excel(name = "目标仓库", width = 15)
    @Schema(description = "目标仓库")
    @Dict(dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    private String warehouseId;

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
}

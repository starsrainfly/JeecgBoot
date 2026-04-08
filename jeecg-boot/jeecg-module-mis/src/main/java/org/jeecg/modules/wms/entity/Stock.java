package org.jeecg.modules.wms.entity;

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
 * @Description: 库存记录表
 * @Author: jeecg-boot
 * @Date:   2026-03-31
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
    private String id;
	/**项目id*/
	@Excel(name = "项目id", width = 15)
    @Schema(description = "项目id")
    private String goodsId;
	/**项目编码*/
	@Excel(name = "项目编码", width = 15)
    @Schema(description = "项目编码")
    private String goodsCode;
	/**项目名称*/
	@Excel(name = "项目名称", width = 15)
    @Schema(description = "项目名称")
    private String goodsName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String goodsSpec;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
	/**项目类型*/
	@Excel(name = "物品类型", width = 15)
    @Schema(description = "物品类型")
    private String goodsType;
	/**仓库id*/
	@Excel(name = "仓库id", width = 15)
    @Schema(description = "仓库id")
    private String warehouseId;
	/**区域id*/
	@Excel(name = "区域id", width = 15)
    @Schema(description = "区域id")
    private String areaId;
	/**货架id*/
	@Excel(name = "货架id", width = 15)
    @Schema(description = "货架id")
    private String shelfId;
	/**位置id*/
	@Excel(name = "位置id", width = 15)
    @Schema(description = "位置id")
    private String locationId;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNo;
    /**生产日期*/
    @Excel(name = "生产日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "生产日期")
    private Date productionDate;
    /**质保天数*/
    @Excel(name = "质保天数", width = 15)
    @Schema(description = "质保天数")
    private Integer shelfLife;

	/**过期日（根据shelf_life自动计算）*/
	@Excel(name = "过期日（根据shelf_life自动计算）", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "过期日（根据shelf_life自动计算）")
    private Date expiryDate;
	/**库存数量*/
	@Excel(name = "库存数量", width = 15)
    @Schema(description = "库存数量")
    private BigDecimal quantity;
	/**已分配未出库量*/
	@Excel(name = "已分配未出库量", width = 15)
    @Schema(description = "已分配未出库量")
    private BigDecimal lockedQty;
	/**入库明细id*/
	@Excel(name = "入库明细id", width = 15)
    @Schema(description = "入库明细id")
    private String inDetailId;
    /**生产批次id*/
    @Excel(name = "生产批次id", width = 15)
    @Schema(description = "生产批次id")
    private String productionBatchId;

	/**辅助单位*/
	@Excel(name = "辅助单位", width = 15)
    @Schema(description = "辅助单位")
    private String auxUnit;
	/**辅助单位数量*/
	@Excel(name = "辅助单位数量", width = 15)
    @Schema(description = "辅助单位数量")
    private BigDecimal auxQuantity;
	/**单位换算率*/
	@Excel(name = "单位换算率", width = 15)
    @Schema(description = "单位换算率")
    private BigDecimal conversionRate;
	/**供应商id*/
	@Excel(name = "供应商id", width = 15)
    @Schema(description = "供应商id")
    private String supplierId;
	/**供应商名称*/
	@Excel(name = "供应商名称", width = 15)
    @Schema(description = "供应商名称")
    private String supplierName;
	/**入库时间*/
	@Excel(name = "入库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "入库时间")
    private Date stockInTime;
	/**原始入库数量*/
	@Excel(name = "原始入库数量", width = 15)
    @Schema(description = "原始入库数量")
    private BigDecimal originalQty;

    /**质检状态*/
    @Excel(name = "质检状态", width = 15, dicCode = "mes_qc_status")
    @Schema(description = "质检状态")
    private String qcStatus;

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

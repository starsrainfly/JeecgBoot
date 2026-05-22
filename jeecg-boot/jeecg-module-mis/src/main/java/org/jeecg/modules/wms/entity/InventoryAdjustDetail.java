package org.jeecg.modules.wms.entity;

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
 * @Description: 盘库调整单明细表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Schema(description="盘库调整单明细表")
@Data
@TableName("wms_inventory_adjust_detail")
public class InventoryAdjustDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**调整单id*/
    @Schema(description = "调整单id")
    private String adjustId;
	/**盘点明细id*/
	@Excel(name = "盘点明细id", width = 15)
    @Schema(description = "盘点明细id")
    private String checkDetailId;
	/**库存id*/
	@Excel(name = "库存id", width = 15)
    @Schema(description = "库存id")
    private String stockId;
	/**物料id*/
	@Excel(name = "物料id", width = 15)
    @Schema(description = "物料id")
    private String goodsId;
	/**编码*/
	@Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String goodsCode;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @Schema(description = "名称")
    private String goodsName;
	/**规格*/
	@Excel(name = "规格", width = 15)
    @Schema(description = "规格")
    private String goodsSpec;
	/**颜色*/
	@Excel(name = "颜色", width = 15)
    @Schema(description = "颜色")
    private String goodsColor;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNo;
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Schema(description = "仓库")
    private String warehouseId;
	/**区域*/
	@Excel(name = "区域", width = 15, dictTable = "mis_warehouse_area where del_flag='0' and status='1'", dicText = "area_code", dicCode = "id")
    @Schema(description = "区域")
    private String areaId;
	/**货架*/
	@Excel(name = "货架", width = 15, dictTable = "mis_warehouse_shelf where del_flag='0' and status='1'", dicText = "shelf_code", dicCode = "id")
    @Schema(description = "货架")
    private String shelfId;
	/**库位*/
	@Excel(name = "库位", width = 15, dictTable = "mis_warehouse_location where del_flag='0' and status='1'", dicText = "location_code", dicCode = "id")
    @Schema(description = "库位")
    private String locationId;
	/**库位路径*/
	@Excel(name = "库位路径", width = 15)
    @Schema(description = "库位路径")
    private String pathCode;
	/**调整类型*/
	@Excel(name = "调整类型", width = 15, dicCode = "wms_adjust_type")
    @Schema(description = "调整类型")
    private String adjustType;
	/**调整数量*/
	@Excel(name = "调整数量", width = 15)
    @Schema(description = "调整数量")
    private java.math.BigDecimal adjustQty;
	/**单位*/
	@Excel(name = "单位", width = 15, dictTable = "mis_unit where del_flag='0' and status='1'", dicText = "unit", dicCode = "unit")
    @Schema(description = "单位")
    private String unit;
	/**成本单价*/
	@Excel(name = "成本单价", width = 15)
    @Schema(description = "成本单价")
    private java.math.BigDecimal costPrice;
	/**调整金额*/
	@Excel(name = "调整金额", width = 15)
    @Schema(description = "调整金额")
    private java.math.BigDecimal adjustAmount;
	/**调整前库存*/
	@Excel(name = "调整前库存", width = 15)
    @Schema(description = "调整前库存")
    private java.math.BigDecimal beforeQty;
	/**调整后库存*/
	@Excel(name = "调整后库存", width = 15)
    @Schema(description = "调整后库存")
    private java.math.BigDecimal afterQty;
	/**差异原因*/
	@Excel(name = "差异原因", width = 15)
    @Schema(description = "差异原因")
    private String diffReason;
	/**入库id*/
	@Excel(name = "入库id", width = 15)
    @Schema(description = "入库id")
    private String stockInId;
	/**入库单号*/
	@Excel(name = "入库单号", width = 15)
    @Schema(description = "入库单号")
    private String stockInNo;
	/**出库id*/
	@Excel(name = "出库id", width = 15)
    @Schema(description = "出库id")
    private String stockOutId;
	/**出库单号*/
	@Excel(name = "出库单号", width = 15)
    @Schema(description = "出库单号")
    private String stockOutNo;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15, dicCode = "del_flag")
    @Schema(description = "删除标识")
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

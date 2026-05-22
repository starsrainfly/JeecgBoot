package org.jeecg.modules.wms.entity;

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
 * @Description: 盘库明细表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Data
@TableName("wms_inventory_check_detail")
@Schema(description="盘库明细表")
public class InventoryCheckDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**盘点单id*/
    @Schema(description = "盘点单id")
    private String checkId;
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
	/**类型*/
	@Excel(name = "类型", width = 15)
    @Dict(dicCode = "wms_item_type")
    @Schema(description = "类型")
    private String goodsType;
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
	/**有效期至*/
	@Excel(name = "有效期至", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "有效期至")
    private Date expiryDate;
	/**仓库*/
	@Excel(name = "仓库", width = 15)
    @Dict(dicCode = "id",dicText = "name",dictTable = "mis_warehouse where del_flag='0' and status='1'")
    @Schema(description = "仓库")
    private String warehouseId;
	/**区域*/
	@Excel(name = "区域", width = 15)
    @Dict(dicCode = "id",dicText = "area_code",dictTable = "mis_warehouse_area where del_flag='0' and status='1'")
    @Schema(description = "区域")
    private String areaId;
	/**货架*/
	@Excel(name = "货架", width = 15)
    @Dict(dicCode = "id",dicText = "shelf_code",dictTable = "mis_warehouse_shelf where del_flag='0' and status='1'")
    @Schema(description = "货架")
    private String shelfId;
	/**货位*/
	@Excel(name = "货位", width = 15)
    @Dict(dicCode = "id",dicText = "path_code",dictTable = "mis_warehouse_location where del_flag='0' and status='1'")
    @Schema(description = "货位")
    private String locationId;
	/**系统库存数量*/
	@Excel(name = "系统库存数量", width = 15)
    @Schema(description = "系统库存数量")
    private java.math.BigDecimal systemQty;
	/**实盘数量*/
	@Excel(name = "实盘数量", width = 15)
    @Schema(description = "实盘数量")
    private java.math.BigDecimal actualQty;
	/**差异数量*/
	@Excel(name = "差异数量", width = 15)
    @Schema(description = "差异数量")
    private java.math.BigDecimal diffQty;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Dict(dicCode = "unit",dicText = "unit",dictTable = "mis_unit where del_flag='0' and status='1'")
    @Schema(description = "单位")
    private String unit;
	/**成本单价*/
	@Excel(name = "成本单价", width = 15)
    @Schema(description = "成本单价")
    private java.math.BigDecimal costPrice;
	/**差异金额*/
	@Excel(name = "差异金额", width = 15)
    @Schema(description = "差异金额")
    private java.math.BigDecimal diffAmount;
	/**差异原因*/
	@Excel(name = "差异原因", width = 15)
    @Schema(description = "差异原因")
    private String diffReason;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Dict(dicCode = "wms_check_status")
    @Schema(description = "状态")
    private String checkStatus;

    /**盘点时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "盘点时间")
    private Date checkTime;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15)
    @Dict(dicCode = "del_flag")
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

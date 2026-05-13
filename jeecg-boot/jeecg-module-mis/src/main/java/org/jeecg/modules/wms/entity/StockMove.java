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
 * @Description: 移库记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
@Data
@TableName("mis_stock_move")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="移库记录表")
public class StockMove implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**移库记录号*/
	@Excel(name = "移库记录号", width = 15)
    @Schema(description = "移库记录号")
    private String moveNo;
	/**原库存ID*/
	@Excel(name = "原库存ID", width = 15)
    @Schema(description = "原库存ID")
    private String fromStockId;
	/**新库存ID*/
	@Excel(name = "新库存ID", width = 15)
    @Schema(description = "新库存ID")
    private String toStockId;
	/**物品id*/
	@Excel(name = "物品id", width = 15)
    @Schema(description = "物品id")
    private String goodsId;
	/**物品编码*/
	@Excel(name = "物品编码", width = 15)
    @Schema(description = "物品编码")
    private String goodsCode;
	/**物品名称*/
	@Excel(name = "物品名称", width = 15)
    @Schema(description = "物品名称")
    private String goodsName;
	/**批号*/
	@Excel(name = "批号", width = 15)
    @Schema(description = "批号")
    private String batchNo;
	/**原仓库*/
	@Excel(name = "原仓库", width = 15, dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
	@Dict(dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Schema(description = "原仓库")
    private String fromWarehouseId;
	/**原区域*/
	@Excel(name = "原区域", width = 15, dictTable = "mis_warehouse_area where del_flag='0' and status='1'", dicText = "area_code", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_area where del_flag='0' and status='1'", dicText = "area_code", dicCode = "id")
    @Schema(description = "原区域")
    private String fromAreaId;
	/**原货架（AREA级别为NULL）*/
	@Excel(name = "原货架（AREA级别为NULL）", width = 15, dictTable = "mis_warehouse_shelf where del_flag ='0' and status='1'", dicText = "shelf_code", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_shelf where del_flag ='0' and status='1'", dicText = "shelf_code", dicCode = "id")
    @Schema(description = "原货架（AREA级别为NULL）")
    private String fromShelfId;
	/**原货位（AREA/SHELF级别为NULL）*/
	@Excel(name = "原货位（AREA/SHELF级别为NULL）", width = 15, dictTable = "mis_warehouse_location where del_flag='0' and status='1'", dicText = "path_code", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_location where del_flag='0' and status='1'", dicText = "path_code", dicCode = "id")
    @Schema(description = "原货位（AREA/SHELF级别为NULL）")
    private String fromLocationId;
	/**目标仓库*/
	@Excel(name = "目标仓库", width = 15, dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
	@Dict(dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Schema(description = "目标仓库")
    private String toWarehouseId;
	/**目标区域*/
	@Excel(name = "目标区域", width = 15, dictTable = "mis_warehouse_area where del_flag='0' and status='1'", dicText = "area_code", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_area where del_flag='0' and status='1'", dicText = "area_code", dicCode = "id")
    @Schema(description = "目标区域")
    private String toAreaId;
	/**目标货架（AREA级别为NULL）*/
	@Excel(name = "目标货架（AREA级别为NULL）", width = 15, dictTable = "mis_warehouse_shelf where del_flag ='0' and status='1'", dicText = "shelf_code", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_shelf where del_flag ='0' and status='1'", dicText = "shelf_code", dicCode = "id")
    @Schema(description = "目标货架（AREA级别为NULL）")
    private String toShelfId;
	/**目标货位（AREA/SHELF级别为NULL）*/
	@Excel(name = "目标货位（AREA/SHELF级别为NULL）", width = 15, dictTable = "mis_warehouse_location where del_flag='0' and status='1'", dicText = "path_code", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_location where del_flag='0' and status='1'", dicText = "path_code", dicCode = "id")
    @Schema(description = "目标货位（AREA/SHELF级别为NULL）")
    private String toLocationId;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @Schema(description = "数量")
    private BigDecimal moveQty;
	/**移库原因*/
	@Excel(name = "移库原因", width = 15)
    @Schema(description = "移库原因")
    private String moveReason;
	/**操作人*/
	@Excel(name = "操作人", width = 15, dictTable = "sys_user where del_flag = '0' and status='1'", dicText = "realname", dicCode = "id")
	@Dict(dictTable = "sys_user where del_flag = '0' and status='1'", dicText = "realname", dicCode = "id")
    @Schema(description = "操作人")
    private String operatorId;
	/**操作人姓名*/
	@Excel(name = "操作人姓名", width = 15)
    @Schema(description = "操作人姓名")
    private String operatorName;
	/**移库时间*/
	@Excel(name = "移库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "移库时间")
    private Date moveTime;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private String sysOrgCode;
	/**删除状态*/
	@Excel(name = "删除状态", width = 15)
    @Schema(description = "删除状态")
    @TableLogic
    private String delFlag;
	/**类型*/
	@Excel(name = "类型", width = 15, dicCode = "wms_item_type")
	@Dict(dicCode = "wms_item_type")
    @Schema(description = "类型")
    private String goodsType;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String goodsSpec;
	/**是否产品*/
	@Excel(name = "是否产品", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否产品")
    private String isProduct;
	/**颜色*/
	@Excel(name = "颜色", width = 15)
    @Schema(description = "颜色")
    private String goodsColor;
	/**单位*/
	@Excel(name = "单位", width = 15, dictTable = "mis_unit where del_flag='0' and status='1'", dicText = "unit", dicCode = "unit")
	@Dict(dictTable = "mis_unit where del_flag='0' and status='1'", dicText = "unit", dicCode = "unit")
    @Schema(description = "单位")
    private String unit;
}

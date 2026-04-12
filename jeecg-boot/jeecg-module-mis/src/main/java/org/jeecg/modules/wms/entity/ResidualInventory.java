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
 * @Description: 余料库表
 * @Author: jeecg-boot
 * @Date:   2026-04-12
 * @Version: V1.0
 */
@Data
@TableName("mis_residual_inventory")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="余料库表")
public class ResidualInventory implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
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
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
	@Dict(dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Schema(description = "仓库")
    private String warehouseId;
	/**原始数量*/
	@Excel(name = "原始数量", width = 15)
    @Schema(description = "原始数量")
    private BigDecimal originalQty;
	/**单价*/
	@Excel(name = "单价", width = 15)
    @Schema(description = "单价")
    private BigDecimal unitPrice;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @Schema(description = "金额")
    private BigDecimal totalAmount;
	/**余料数量*/
	@Excel(name = "余料数量", width = 15)
    @Schema(description = "余料数量")
    private BigDecimal qty;
	/**锁定数量*/
	@Excel(name = "锁定数量", width = 15)
    @Schema(description = "锁定数量")
    private BigDecimal lockedQty;
	/**生产订单id*/
	@Excel(name = "生产订单id", width = 15)
    @Schema(description = "生产订单id")
    private String productionOrderId;
	/**生产订单号*/
	@Excel(name = "生产订单号", width = 15)
    @Schema(description = "生产订单号")
    private String productionOrderNo;
	/**生产批次id*/
	@Excel(name = "生产批次id", width = 15)
    @Schema(description = "生产批次id")
    private String productionBatchId;
	/**生产批次号*/
	@Excel(name = "生产批次号", width = 15)
    @Schema(description = "生产批次号")
    private String productionBatchNo;
	/**出库单id*/
	@Excel(name = "出库单id", width = 15)
    @Schema(description = "出库单id")
    private String stockOutId;
	/**出库明细id*/
	@Excel(name = "出库明细id", width = 15)
    @Schema(description = "出库明细id")
    private String stockOutDetailId;
	/**物料批次号*/
	@Excel(name = "物料批次号", width = 15)
    @Schema(description = "物料批次号")
    private String materialBatchNo;
	/**状态 */
	@Excel(name = "状态 ", width = 15, dicCode = "wms_residual_status")
	@Dict(dicCode = "wms_residual_status")
    @Schema(description = "状态 ")
    private String status;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
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

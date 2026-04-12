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

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 出库明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-09
 * @Version: V1.0
 */
@Schema(description="出库明细表")
@Data
@TableName("mis_stock_out_detail")
public class StockOutDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**出库单号*/
	@Excel(name = "出库单号", width = 15)
    @Schema(description = "出库单号")
    private String stockOutNo;
	/**出库主表id*/
    @Schema(description = "出库主表id")
    private String stockOutId;
	/**物品id*/
	@Excel(name = "物品id", width = 15)
    @Schema(description = "物品id")
    private String goodsId;
	/**类型*/
	@Excel(name = "类型", width = 15, dicCode = "mdm_material_type")
    @Dict(dicCode = "mdm_material_type")
    @Schema(description = "类型")
    private String goodsType;
	/**编码*/
	@Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String goodsCode;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @Schema(description = "名称")
    private String goodsName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String goodsSpec;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
	/**申请数量*/
	@Excel(name = "申请数量", width = 15)
    @Schema(description = "申请数量")
    private java.math.BigDecimal applyQty;
	/**实发数量*/
	@Excel(name = "实发数量", width = 15)
    @Schema(description = "实发数量")
    private java.math.BigDecimal actualQty;
    /**是否超量*/
    @Excel(name = "是否超量", width = 15,dicCode = "yn")
    @Dict(dicCode = "yn")
    @Schema(description = "是否超量")
    private String overFlag;
    /**超量数量*/
    @Excel(name = "超量数量", width = 15)
    @Schema(description = "超量数量")
    private BigDecimal overQty;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNo;
	/**序列号*/
	@Excel(name = "序列号", width = 15)
    @Schema(description = "序列号")
    private String serialNo;
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
	/**有效期至*/
	@Excel(name = "有效期至", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "有效期至")
    private Date expiryDate;
	/**成本单价*/
	@Excel(name = "成本单价", width = 15)
    @Schema(description = "成本单价")
    private java.math.BigDecimal costPrice;
	/**销售单价*/
	@Excel(name = "销售单价", width = 15)
    @Schema(description = "销售单价")
    private java.math.BigDecimal salesPrice;
	/**成本金额*/
	@Excel(name = "成本金额", width = 15)
    @Schema(description = "成本金额")
    private java.math.BigDecimal costTotal;
	/**销售金额*/
	@Excel(name = "销售金额", width = 15)
    @Schema(description = "销售金额")
    private java.math.BigDecimal salesTotal;
	/**来源单据明细id*/
	@Excel(name = "来源单据明细id", width = 15)
    @Schema(description = "来源单据明细id")
    private String sourceDetailId;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @Schema(description = "是否删除")
    @TableLogic
    private String delFlag;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**需求表id*/
	@Excel(name = "需求表id", width = 15)
    @Schema(description = "需求表id")
    private String requirementId;
	/**生产批次id*/
	@Excel(name = "生产批次id", width = 15)
    @Schema(description = "生产批次id")
    private String productionBatchId;
	/**入库明细id*/
	@Excel(name = "入库明细id", width = 15)
    @Schema(description = "入库明细id")
    private String inDetailId;
	/**库存记录id*/
	@Excel(name = "库存记录id", width = 15)
    @Schema(description = "库存记录id")
    private String stockId;
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

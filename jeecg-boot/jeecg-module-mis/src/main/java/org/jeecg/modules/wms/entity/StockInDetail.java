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
 * @Description: 入库明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
@Schema(description="入库明细表")
@Data
@TableName("mis_stock_in_detail")
public class StockInDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**入库单号*/
	@Excel(name = "入库单号", width = 15)
    @Schema(description = "入库单号")
    private String stockInNo;
	/**入库表id*/
    @Schema(description = "入库表id")
    private String stockInId;
	/**类型（物料、产品）*/
	@Excel(name = "类型（物料、产品）", width = 15, dicCode = "wms_item_type")
    @Schema(description = "类型（物料、产品）")
    @Dict(dicCode = "wms_item_type")
    private String goodsType;
	/**物料id*/
	@Excel(name = "物料id", width = 15)
    @Schema(description = "物料id")
    private String goodsId;
	/**编码*/
	@Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String goodsCode;
	/**项目名称*/
	@Excel(name = "项目名称", width = 15)
    @Schema(description = "项目名称")
    private String goodsName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String goodsSpec;
    /**颜色*/
    @Excel(name = "颜色", width = 15)
    @Schema(description = "颜色")
    private String goodsColor;
	/**单位*/
	@Excel(name = "单位", width = 15, dictTable = "mis_unit where del_flag='0'", dicText = "unit", dicCode = "unit")
    @Schema(description = "单位")
    private String unit;
	/**申请数量*/
	@Excel(name = "申请数量", width = 15)
    @Schema(description = "申请数量")
    private java.math.BigDecimal applyQty;
	/**实收数量*/
	@Excel(name = "实收数量", width = 15)
    @Schema(description = "实收数量")
    private java.math.BigDecimal actualQty;
	/**币种*/
	@Excel(name = "币种", width = 15, dictTable = "mis_currency where del_flag='0' and status='1'", dicText = "currency_name", dicCode = "currency_code")
    @Schema(description = "币种")
    private String currency;
	/**汇率*/
	@Excel(name = "汇率", width = 15)
    @Schema(description = "汇率")
    private java.math.BigDecimal exchangeRate;
	/**单价（本币）*/
	@Excel(name = "单价（本币）", width = 15)
    @Schema(description = "单价（本币）")
    private java.math.BigDecimal unitPrice;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @Schema(description = "金额")
    private java.math.BigDecimal totalAmount;
	/**批号*/
	@Excel(name = "批号", width = 15)
    @Schema(description = "批号")
    private String batchNo;
	/**序列号*/
	@Excel(name = "序列号", width = 15)
    @Schema(description = "序列号")
    private String serialNo;
	/**质检状态*/
	@Excel(name = "质检状态", width = 15, dicCode = "mes_qc_status")
    @Dict(dicCode = "mes_qc_status")
    @Schema(description = "质检状态")
    private String qcStatus;
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
	/**生产批次id*/
	@Excel(name = "生产批次id", width = 15)
    @Schema(description = "生产批次id")
    private String productionBatchId;
	/**原销售/采购订单id*/
	@Excel(name = "原销售/采购订单id", width = 15)
    @Schema(description = "原销售/采购订单id")
    private String sourceDetailId;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
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

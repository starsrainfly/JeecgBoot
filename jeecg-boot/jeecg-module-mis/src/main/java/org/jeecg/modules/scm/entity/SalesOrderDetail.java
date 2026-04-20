package org.jeecg.modules.scm.entity;

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

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 销售订单明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-20
 * @Version: V1.0
 */
@Schema(description="销售订单明细表")
@Data
@TableName("mis_sales_order_detail")
public class SalesOrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**订单id*/
    @Schema(description = "订单id")
    private String orderId;
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
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String productSpec;
	/**单位*/
	@Excel(name = "单位", width = 15, dictTable = "mis_unit where del_flag='0' and status='1'", dicText = "unit", dicCode = "unit")
    @Schema(description = "单位")
    private String unit;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @Schema(description = "数量")
    private java.math.BigDecimal orderQty;
	/**单价*/
	@Excel(name = "单价", width = 15)
    @Schema(description = "单价")
    private java.math.BigDecimal unitPrice;
	/**税率(%)*/
	@Excel(name = "税率(%)", width = 15)
    @Schema(description = "税率(%)")
    private java.math.BigDecimal taxRate;
	/**税额*/
	@Excel(name = "税额", width = 15)
    @Schema(description = "税额")
    private java.math.BigDecimal taxAmount;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @Schema(description = "金额")
    private java.math.BigDecimal detailAmount;
    /**不含税金额*/
    @Excel(name = "不含税金额", width = 15)
    @Schema(description = "不含税金额")
    private BigDecimal netAmount;
	/**报价单id*/
	@Excel(name = "报价单id", width = 15)
    @Schema(description = "报价单id")
    private String offerId;
	/**报价单明细id*/
	@Excel(name = "报价单明细id", width = 15)
    @Schema(description = "报价单明细id")
    private String offerDetailId;
	/**报价单号*/
	@Excel(name = "报价单号", width = 15)
    @Schema(description = "报价单号")
    private String offerNo;
	/**发货状态*/
	@Excel(name = "发货状态", width = 15, dicCode = "wms_delivery_status")
    @Schema(description = "发货状态")
    private String deliveryStatus;
	/**发货数量*/
	@Excel(name = "发货数量", width = 15)
    @Schema(description = "发货数量")
    private java.math.BigDecimal deliveryQty;
	/**关联原订单明细id*/
	@Excel(name = "关联原订单明细id", width = 15)
    @Schema(description = "关联原订单明细id")
    private String returnSourceId;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**包装id*/
	@Excel(name = "包装id", width = 15)
    @Schema(description = "包装id")
    private String packageId;
	/**包装名称*/
	@Excel(name = "包装名称", width = 15)
    @Schema(description = "包装名称")
    private String packageName;
	/**包装规格*/
	@Excel(name = "包装规格", width = 15)
    @Schema(description = "包装规格")
    private String packageSpec;
	/**包装容量*/
	@Excel(name = "包装容量", width = 15)
    @Schema(description = "包装容量")
    private java.math.BigDecimal packageCapacity;
	/**包装单位*/
	@Excel(name = "包装单位", width = 15)
    @Schema(description = "包装单位")
    private String packageCapacityUnit;
	/**已分配库存数量*/
	@Excel(name = "已分配库存数量", width = 15)
    @Schema(description = "已分配库存数量")
    private java.math.BigDecimal allocatedStockQty;
	/**已计划生产量*/
	@Excel(name = "已计划生产量", width = 15)
    @Schema(description = "已计划生产量")
    private java.math.BigDecimal plannedQty;
	/**定制编号*/
	@Excel(name = "定制编号", width = 15)
    @Schema(description = "定制编号")
    private String customProductCode;
	/**定制名称*/
	@Excel(name = "定制名称", width = 15)
    @Schema(description = "定制名称")
    private String customProductName;
	/**定制规格*/
	@Excel(name = "定制规格", width = 15)
    @Schema(description = "定制规格")
    private String customProductSpec;
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

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
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 销售订单明细表
 * @Author: jeecg-boot
 * @Date:   2026-02-07
 * @Version: V1.0
 */
@Schema(description="销售订单明细表")
@Data
@TableName("mis_sales_order_line")
public class SalesOrderLine implements Serializable {
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
    private String itemId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String itemCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String itemName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String itemSpec;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @Schema(description = "数量")
    private java.math.BigDecimal quantity;
	/**单价*/
	@Excel(name = "单价", width = 15)
    @Schema(description = "单价")
    private java.math.BigDecimal unitPrice;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @Schema(description = "金额")
    private java.math.BigDecimal lineAmount;
	/**本币金额*/
	@Excel(name = "本币金额", width = 15)
    @Schema(description = "本币金额")
    private java.math.BigDecimal lineAmountLocal;
	/**定价来源*/
	@Excel(name = "定价来源", width = 15)
    @Schema(description = "定价来源")
    private String priceSourceType;
	/**价格表id*/
	@Excel(name = "价格表id", width = 15)
    @Schema(description = "价格表id")
    private String priceSourceId;
	/**是否赠品*/
	@Excel(name = "是否赠品", width = 15, dicCode = "yn")
    @Schema(description = "是否赠品")
    private String isGift;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**税率(%)*/
	@Excel(name = "税率(%)", width = 15)
    @Schema(description = "税率(%)")
    private java.math.BigDecimal taxRate;
	/**税额*/
	@Excel(name = "税额", width = 15)
    @Schema(description = "税额")
    private java.math.BigDecimal taxAmount;
	/**行号*/
	@Excel(name = "行号", width = 15)
    @Schema(description = "行号")
    private Integer sortIndex;
	/**包装id*/
	@Excel(name = "包装id", width = 15)
    @Schema(description = "包装id")
    private String packageItemId;
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

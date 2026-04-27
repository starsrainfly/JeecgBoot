package org.jeecg.modules.scm.entity;

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
 * @Description: 报价单明细
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Schema(description="报价单明细")
@Data
@TableName("mis_price_offer_detail")
public class PriceOfferDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**报价单ID*/
    @Schema(description = "报价单ID")
    private String offerId;
	/**产品ID*/
	@Excel(name = "产品ID", width = 15)
    @Schema(description = "产品ID")
    private String productId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**产品标准名称*/
	@Excel(name = "产品标准名称", width = 15)
    @Schema(description = "产品标准名称")
    private String productName;
	/**产品规格*/
	@Excel(name = "产品规格", width = 15)
    @Schema(description = "产品规格")
    private String productSpec;
    /**产品颜色*/
    @Excel(name = "产品颜色", width = 15)
    @Schema(description = "产品颜色")
    private String productColor;
    /**客户定制编码*/
    @Excel(name = "客户定制编码", width = 15)
    @Schema(description = "客户定制编码")
    private String customProductCode;
	/**客户定制产品名称*/
	@Excel(name = "客户定制名称", width = 15)
    @Schema(description = "客户定制名称")
    private String customProductName;
	/**客户定制规格*/
	@Excel(name = "客户定制规格", width = 15)
    @Schema(description = "客户定制规格")
    private String customProductSpec;
	/**单位*/
	@Excel(name = "单位", width = 15, dictTable = "mis_unit where del_flag='0' and status='1'", dicText = "unit", dicCode = "unit")
    @Schema(description = "单位")
    private String unit;
	/**包装ID*/
	@Excel(name = "包装ID", width = 15)
    @Schema(description = "包装ID")
    private String packageId;
	/**包装编码*/
	@Excel(name = "包装编码", width = 15)
    @Schema(description = "包装编码")
    private String packageCode;
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
    private BigDecimal packageCapacity;
    /**包装容量*/
    @Excel(name = "包装容量单位", width = 15)
    @Schema(description = "包装容量单位")
    private String packageCapacityUnit;
	/**价格类型*/
	@Excel(name = "价格类型", width = 15, dicCode = "mdm_price_type")
    @Schema(description = "价格类型")
    private String priceType;
	/**数量区间-最小*/
	@Excel(name = "数量区间-最小", width = 15)
    @Schema(description = "数量区间-最小")
    private java.math.BigDecimal qtyMin;
	/**数量区间-最大*/
	@Excel(name = "数量区间-最大", width = 15)
    @Schema(description = "数量区间-最大")
    private java.math.BigDecimal qtyMax;
	/**生效日期*/
	@Excel(name = "生效日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "生效日期")
    private Date effectiveDate;
	/**失效日期*/
	@Excel(name = "失效日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "失效日期")
    private Date expiryDate;
	/**单价（含税）*/
	@Excel(name = "单价（含税）", width = 15)
    @Schema(description = "单价（含税）")
    private java.math.BigDecimal unitPrice;
	/**税率(%)*/
	@Excel(name = "税率(%)", width = 15)
    @Schema(description = "税率(%)")
    private java.math.BigDecimal taxRate;
	/**最小起订量*/
	@Excel(name = "最小起订量", width = 15)
    @Schema(description = "最小起订量")
    private java.math.BigDecimal minOrderQty;
	/**数量步长*/
	@Excel(name = "数量步长", width = 15)
    @Schema(description = "数量步长")
    private java.math.BigDecimal qtyStep;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
    @Schema(description = "状态")
    @Dict(dicCode = "status")
    private String status;
	/**禁用原因*/
	@Excel(name = "禁用原因", width = 15)
    @Schema(description = "禁用原因")
    private String disabledReason;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15, dicCode = "del_flag")
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
}

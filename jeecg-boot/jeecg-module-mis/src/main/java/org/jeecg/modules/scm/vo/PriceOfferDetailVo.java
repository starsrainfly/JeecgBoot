package org.jeecg.modules.scm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "报价明细分页Vo")
public class PriceOfferDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报价明细ID")
    private String offerDetailId;

    @Schema(description = "报价单号")
    private String offerNo;

    @Schema(description = "客户ID")
    private String customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "业务员ID")
    @Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    private String salesmanId;

    @Schema(description = "业务员名称")
    private String salesmanName;

    @Schema(description = "产品ID")
    private String productId;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "产品标准名称")
    private String productName;

    @Schema(description = "产品规格")
    private String productSpec;

    @Schema(description = "客户定制编码")
    private String customProductCode;

    @Schema(description = "客户定制产品名称")
    private String customProductName;

    @Schema(description = "客户定制规格")
    private String customProductSpec;

    @Schema(description = "单位")
    @Dict(dictTable = "mis_unit where del_flag='0' and status='1'", dicText = "unit", dicCode = "unit")
    private String unit;

    @Schema(description = "包装ID")
    private String packageId;

    @Schema(description = "包装编码")
    private String packageCode;

    @Schema(description = "包装名称")
    private String packageName;

    @Schema(description = "包装规格")
    private String packageSpec;

    @Schema(description = "包装容量")
    private BigDecimal packageCapacity;

    @Schema(description = "价格类型")
    @Dict(dicCode = "mdm_price_type")
    private String priceType;

    @Schema(description = "数量区间-最小")
    private BigDecimal qtyMin;

    @Schema(description = "数量区间-最大")
    private BigDecimal qtyMax;

    @Schema(description = "生效日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;

    @Schema(description = "失效日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;

    @Schema(description = "单价（含税）")
    private BigDecimal unitPrice;

    @Schema(description = "税率(%)")
    private BigDecimal taxRate;

    @Schema(description = "最小起订量")
    private BigDecimal minOrderQty;

    @Schema(description = "数量步长")
    private BigDecimal qtyStep;

    @Schema(description = "状态")
    @Dict(dicCode = "status")
    private String status;
}
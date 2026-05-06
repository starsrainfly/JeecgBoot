package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 扫码发货请求Vo
 */
@Schema(description = "扫码发货请求Vo")
@Data
public class ScanDeliveryRequestVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 来源订单id */
    @Schema(description = "来源订单id")
    private String sourceOrderId;

    /** 来源订单号 */
    @Schema(description = "来源订单号")
    private String sourceOrderNo;

    /** 客户id */
    @Schema(description = "客户id")
    private String customerId;

    /** 客户名称 */
    @Schema(description = "客户名称")
    private String customerName;

    /** 收货人 */
    @Schema(description = "收货人")
    private String consignee;

    /** 收货人电话 */
    @Schema(description = "收货人电话")
    private String consigneePhone;

    /** 收货人地址 */
    @Schema(description = "收货人地址")
    private String consigneeAddress;

    /** 物流类型 */
    @Schema(description = "物流类型")
    private String logisticsType;

    /** 物流公司id */
    @Schema(description = "物流公司id")
    private String logisticsCompanyId;

    /** 物流公司编码 */
    @Schema(description = "物流公司编码")
    private String logisticsCompanyCode;

    /** 物流公司 */
    @Schema(description = "物流公司")
    private String logisticsCompany;

    /** 物流单号/车牌号 */
    @Schema(description = "物流单号/车牌号")
    private String logisticsNo;

    /** 物流费用 */
    @Schema(description = "物流费用")
    private BigDecimal logisticsCost;

    /** 司机电话 */
    @Schema(description = "司机电话")
    private String driverPhone;

    /** 发货时间 */
    @Schema(description = "发货时间")
    private Date deliveryTime;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 发货明细列表 */
    @Schema(description = "发货明细列表")
    private List<ScanDeliveryItemVo> deliveryItems;
}

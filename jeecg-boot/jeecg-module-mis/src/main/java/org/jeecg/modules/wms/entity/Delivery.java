package org.jeecg.modules.wms.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
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

/**
 * @Description: 发货表
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Schema(description="发货表")
@Data
@TableName("mis_delivery")
public class Delivery implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**发货单号*/
	@Excel(name = "发货单号", width = 15)
    @Schema(description = "发货单号")
    private String deliveryNo;
	/**来源类型*/
	@Excel(name = "来源类型", width = 15, dicCode = "wms_delivery_source")
    @Dict(dicCode = "wms_delivery_source")
    @Schema(description = "来源类型")
    private String sourceType;
	/**来源订单id*/
	@Excel(name = "来源订单id", width = 15)
    @Schema(description = "来源订单id")
    private String sourceOrderId;
	/**来源订单号*/
	@Excel(name = "来源订单号", width = 15)
    @Schema(description = "来源订单号")
    private String sourceOrderNo;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
    @Schema(description = "客户id")
    private String customerId;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
    @Schema(description = "客户名称")
    private String customerName;
	/**收货人*/
	@Excel(name = "收货人", width = 15)
    @Schema(description = "收货人")
    private String consignee;
	/**收货人电话*/
	@Excel(name = "收货人电话", width = 15)
    @Schema(description = "收货人电话")
    private String consigneePhone;
	/**收货人地址*/
	@Excel(name = "收货人地址", width = 15)
    @Schema(description = "收货人地址")
    private String consigneeAddress;
	/**物流类型*/
	@Excel(name = "物流类型", width = 15, dicCode = "wms_logistics_type")
    @Dict(dicCode = "wms_logistics_type")
    @Schema(description = "物流类型")
    private String logisticsType;
	/**物流公司id*/
	@Excel(name = "物流公司id", width = 15)
    @Schema(description = "物流公司id")
    private String logisticsCompanyId;
	/**物流公司编码*/
	@Excel(name = "物流公司编码", width = 15)
    @Schema(description = "物流公司编码")
    private String logisticsCompanyCode;
	/**物流公司*/
	@Excel(name = "物流公司", width = 15)
    @Schema(description = "物流公司")
    private String logisticsCompany;
	/**物流单号/车牌号*/
	@Excel(name = "物流单号/车牌号", width = 15)
    @Schema(description = "物流单号/车牌号")
    private String logisticsNo;
	/**物流费用*/
	@Excel(name = "物流费用", width = 15)
    @Schema(description = "物流费用")
    private java.math.BigDecimal logisticsCost;
	/**司机电话*/
	@Excel(name = "司机电话", width = 15)
    @Schema(description = "司机电话")
    private String driverPhone;
	/**发货时间*/
	@Excel(name = "发货时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发货时间")
    private Date deliveryTime;
	/**发货数量*/
	@Excel(name = "发货数量", width = 15)
    @Schema(description = "发货数量")
    private java.math.BigDecimal deliveryQty;
	/**发货金额*/
	@Excel(name = "发货金额", width = 15)
    @Schema(description = "发货金额")
    private java.math.BigDecimal deliveryAmount;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "wms_delivery_status")
    @Dict(dicCode = "wms_delivery_status")
    @Schema(description = "状态")
    private String status;
	/**出库单id*/
	@Excel(name = "出库单id", width = 15)
    @Schema(description = "出库单id")
    private String stockOutId;
	/**出库单号*/
	@Excel(name = "出库单号", width = 15)
    @Schema(description = "出库单号")
    private String stockOutNo;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**发货人*/
	@Excel(name = "发货人", width = 15)
    @Schema(description = "发货人")
    private String deliverBy;
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
    @Schema(description = "公司id")
    private String companyId;
    @Schema(description = "公司名称")
    private String companyName;
    @Schema(description = "公司编码")
    private String companyCode;
}

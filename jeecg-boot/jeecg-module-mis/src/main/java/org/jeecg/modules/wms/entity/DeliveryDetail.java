package org.jeecg.modules.wms.entity;

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
 * @Description: 发货明细
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Schema(description="发货明细")
@Data
@TableName("mis_delivery_detail")
public class DeliveryDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;
	/**发货单ID*/
    @Schema(description = "发货单ID")
    private String deliveryId;
	/**销售订单明细ID*/
	@Excel(name = "销售订单明细ID", width = 15)
    @Schema(description = "销售订单明细ID")
    private String sourceDetailId;
	/**产品ID*/
	@Excel(name = "产品ID", width = 15)
    @Schema(description = "产品ID")
    private String goodsId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String goodsCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String goodsName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String goodsSpec;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
	/**生产批次ID*/
	@Excel(name = "生产批次ID", width = 15)
    @Schema(description = "生产批次ID")
    private String productionBatchId;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String productionBatchNo;
	/**生产日期*/
	@Excel(name = "生产日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "生产日期")
    private Date productionDate;
	/**有效期至*/
	@Excel(name = "有效期至", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "有效期至")
    private Date expiryDate;
	/**库存记录ID*/
	@Excel(name = "库存记录ID", width = 15)
    @Schema(description = "库存记录ID")
    private String stockId;
	/**仓库ID*/
	@Excel(name = "仓库ID", width = 15)
    @Schema(description = "仓库ID")
    private String warehouseId;
	/**仓库名称*/
	@Excel(name = "仓库名称", width = 15)
    @Schema(description = "仓库名称")
    private String warehouseName;
	/**实际发货数量*/
	@Excel(name = "实际发货数量", width = 15)
    @Schema(description = "实际发货数量")
    private java.math.BigDecimal actualQty;
	/**单价*/
	@Excel(name = "单价", width = 15)
    @Schema(description = "单价")
    private java.math.BigDecimal unitPrice;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @Schema(description = "金额")
    private java.math.BigDecimal detailAmount;
	/**扫描的二维码内容*/
	@Excel(name = "扫描的二维码内容", width = 15)
    @Schema(description = "扫描的二维码内容")
    private String scanCode;
	/**扫码时间*/
	@Excel(name = "扫码时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "扫码时间")
    private Date scanTime;
	/**出库明细ID*/
	@Excel(name = "出库明细ID", width = 15)
    @Schema(description = "出库明细ID")
    private String stockOutDetailId;
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
}

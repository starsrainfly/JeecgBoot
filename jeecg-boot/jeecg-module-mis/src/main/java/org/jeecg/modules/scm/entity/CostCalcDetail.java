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
 * @Description: 成本核算快照明细
 * @Author: jeecg-boot
 * @Date:   2026-07-28
 * @Version: V1.0
 */
@Schema(description="成本核算快照明细")
@Data
@TableName("mis_cost_calc_detail")
public class CostCalcDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**核算主表id*/
    @Schema(description = "核算主表id")
    private String calcId;
	/**核算单号*/
	@Excel(name = "核算单号", width = 15)
    @Schema(description = "核算单号")
    private String calcNo;
	/**序号*/
	@Excel(name = "序号", width = 15)
    @Schema(description = "序号")
    private Integer serialNo;
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
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @Schema(description = "规格型号")
    private String materialSpec;
	/**配比*/
	@Excel(name = "配比", width = 15)
    @Schema(description = "配比")
    private java.math.BigDecimal proportion;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
	/**价格来源:AVG库存均价/LATEST最新入库价/NONE无*/
	@Excel(name = "价格来源:AVG库存均价/LATEST最新入库价/NONE无", width = 15)
    @Schema(description = "价格来源:AVG库存均价/LATEST最新入库价/NONE无")
    private String priceSource;
	/**最新入库价*/
	@Excel(name = "最新入库价", width = 15)
    @Schema(description = "最新入库价")
    private java.math.BigDecimal latestPrice;
	/**库存均价*/
	@Excel(name = "库存均价", width = 15)
    @Schema(description = "库存均价")
    private java.math.BigDecimal avgPrice;
	/**计算用单价*/
	@Excel(name = "计算用单价", width = 15)
    @Schema(description = "计算用单价")
    private java.math.BigDecimal calcPrice;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @Schema(description = "金额")
    private java.math.BigDecimal amount;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15)
    @Schema(description = "删除标识")
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

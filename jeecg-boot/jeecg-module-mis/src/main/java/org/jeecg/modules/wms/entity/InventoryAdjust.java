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
 * @Description: 盘点调整单主表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Schema(description="盘点调整单主表")
@Data
@TableName("wms_inventory_adjust")
public class InventoryAdjust implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**调整单号*/
	@Excel(name = "调整单号", width = 15)
    @Schema(description = "调整单号")
    private String adjustNo;
	/**盘点单id*/
	@Excel(name = "盘点单id", width = 15)
    @Schema(description = "盘点单id")
    private String checkId;
	/**盘点单号*/
	@Excel(name = "盘点单号", width = 15)
    @Schema(description = "盘点单号")
    private String checkNo;
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Dict(dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Schema(description = "仓库")
    private String warehouseId;
	/**调整项数*/
	@Excel(name = "调整项数", width = 15)
    @Schema(description = "调整项数")
    private Integer totalItems;
	/**调整总数量*/
	@Excel(name = "调整总数量", width = 15)
    @Schema(description = "调整总数量")
    private java.math.BigDecimal totalDiffQty;
	/**调整总金额*/
	@Excel(name = "调整总金额", width = 15)
    @Schema(description = "调整总金额")
    private java.math.BigDecimal totalDiffAmount;
	/**差异原因汇总*/
	@Excel(name = "差异原因汇总", width = 15)
    @Schema(description = "差异原因汇总")
    private String reasonSummary;
	/**审核人id*/
	@Excel(name = "审核人id", width = 15, dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Schema(description = "审核人id")
    private String approveUserId;
	/**审核人*/
	@Excel(name = "审核人", width = 15)
    @Schema(description = "审核人")
    private String approveUserName;
	/**审核时间*/
	@Excel(name = "审核时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核时间")
    private Date approveTime;
	/**审核备注*/
	@Excel(name = "审核备注", width = 15)
    @Schema(description = "审核备注")
    private String approveRemark;
	/**审核状态*/
	@Excel(name = "审核状态", width = 15, dicCode = "approval_status")
    @Dict(dicCode = "approval_status")
    @Schema(description = "审核状态")
    private String approveStatus;
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

package org.jeecg.modules.wms.vo;

import java.math.BigDecimal;
import java.util.List;
import org.jeecg.modules.wms.entity.StockOut;
import org.jeecg.modules.wms.entity.StockOutDetail;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 出库表
 * @Author: jeecg-boot
 * @Date:   2026-04-09
 * @Version: V1.0
 */
@Data
@Schema(description="出库表")
public class StockOutPage {

	/**主键*/
	@Schema(description = "主键")
    private String id;
	/**出库单号*/
	@Excel(name = "出库单号", width = 15)
	@Schema(description = "出库单号")
    private String stockOutNo;
	/**出库类型*/
	@Excel(name = "出库类型", width = 15, dicCode = "wms_stock_out_type")
    @Dict(dicCode = "wms_stock_out_type")
	@Schema(description = "出库类型")
    private String stockOutType;
	/**销售订单*/
	@Excel(name = "销售订单", width = 15)
	@Schema(description = "销售订单")
    private String sourceOrderId;
	/**来源单据编号*/
	@Excel(name = "来源单据编号", width = 15)
	@Schema(description = "来源单据编号")
    private String sourceOrderCode;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
	@Schema(description = "客户id")
    private String customerId;
	/**客户编码*/
	@Excel(name = "客户编码", width = 15)
	@Schema(description = "客户编码")
	private String customerCode;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
	@Schema(description = "客户名称")
    private String customerName;

	/**公司ID*/
	@Excel(name = "公司ID", width = 15)
	@Schema(description = "公司ID")
	private String companyId;
	/**公司编码*/
	@Excel(name = "公司编码", width = 15)
	@Schema(description = "公司编码")
	private String companyCode;
	/**公司名称*/
	@Excel(name = "公司名称", width = 15)
	@Schema(description = "公司名称")
	private String companyName;

	/**领用人id*/
	@Excel(name = "领用人id", width = 15, dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
	@Schema(description = "领用人id")
    private String requesterUserId;
	/**领用人*/
	@Excel(name = "领用人", width = 15)
	@Schema(description = "领用人")
    private String requesterName;
	/**仓库id*/
	@Excel(name = "仓库id", width = 15, dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Dict(dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
	@Schema(description = "仓库id")
    private String warehouseId;
	/**仓库*/
	@Excel(name = "仓库", width = 15)
	@Schema(description = "仓库")
    private String warehouseName;
	/**成本总额*/
	@Excel(name = "成本总额", width = 15)
	@Schema(description = "成本总额")
	private BigDecimal totalCost;
	/**销售总额*/
	@Excel(name = "销售总额", width = 15)
	@Schema(description = "销售总额")
	private BigDecimal totalSales;
	/**操作人id*/
	@Excel(name = "操作人id", width = 15)
	@Schema(description = "操作人id")
    private String operatorUserId;
	/**操作人*/
	@Excel(name = "操作人", width = 15)
	@Schema(description = "操作人")
    private String operatorName;
	/**业务状态*/
	@Excel(name = "业务状态", width = 15, dicCode = "wms_stock_out_status")
    @Dict(dicCode = "wms_stock_out_status")
	@Schema(description = "业务状态")
    private String status;
	/**是否产品*/
	@Excel(name = "是否产品", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
	@Schema(description = "是否产品")
    private String isProduct;
	/**审批人id*/
	@Excel(name = "审批人id", width = 15)
	@Schema(description = "审批人id")
    private String approveId;
	/**审批人*/
	@Excel(name = "审批人", width = 15)
	@Schema(description = "审批人")
    private String approveName;
	/**审批时间*/
	@Excel(name = "审批时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "审批时间")
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
	/**申请时间*/
	@Excel(name = "申请时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "申请时间")
    private Date applyTime;
	/**需求时间*/
	@Excel(name = "需求时间", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Schema(description = "需求时间")
	private Date requiredDate;
	/**出库时间*/
	@Excel(name = "出库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "出库时间")
    private Date stockOutTime;
	/**收货地址*/
	@Excel(name = "收货地址", width = 15)
	@Schema(description = "收货地址")
    private String deliverAddress;
	/**收货人*/
	@Excel(name = "收货人", width = 15)
	@Schema(description = "收货人")
    private String consignee;
	/**收货人电话*/
	@Excel(name = "收货人电话", width = 15)
	@Schema(description = "收货人电话")
    private String consigneePhone;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@Schema(description = "备注")
    private String remark;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
	@Schema(description = "是否删除")
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

	@ExcelCollection(name="出库明细表")
	@Schema(description = "出库明细表")
	private List<StockOutDetail> stockOutDetailList;

}

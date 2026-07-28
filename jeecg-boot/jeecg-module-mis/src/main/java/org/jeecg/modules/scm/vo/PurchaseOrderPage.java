package org.jeecg.modules.scm.vo;

import java.util.List;
import org.jeecg.modules.scm.entity.PurchaseOrder;
import org.jeecg.modules.scm.entity.PurchaseOrderDetail;
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
 * @Description: 采购订单
 * @Author: jeecg-boot
 * @Date:   2026-07-27
 * @Version: V1.0
 */
@Data
@Schema(description="采购订单")
public class PurchaseOrderPage {

	/**主键*/
	@Schema(description = "主键")
    private String id;
	/**采购单号*/
	@Excel(name = "采购单号", width = 15)
	@Schema(description = "采购单号")
    private String orderNo;
	/**申请日期*/
	@Excel(name = "申请日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@Schema(description = "申请日期")
    private Date orderDate;
	/**供应商id*/
	@Excel(name = "供应商id", width = 15)
	@Schema(description = "供应商id")
    private String supplierId;
	/**供应商*/
	@Excel(name = "供应商", width = 15)
	@Schema(description = "供应商")
    private String supplierName;
	/**采购员id*/
	@Excel(name = "采购员id", width = 15, dictTable = "sys_user where status='1' and del_flag='0'", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user where status='1' and del_flag='0'", dicText = "realname", dicCode = "id")
	@Schema(description = "采购员id")
    private String purchaserId;
	/**采购员*/
	@Excel(name = "采购员", width = 15)
	@Schema(description = "采购员")
    private String purchaserName;
	/**币种*/
	@Excel(name = "币种", width = 15, dictTable = "mis_currency", dicText = "currency_name", dicCode = "currency_code")
    @Dict(dictTable = "mis_currency", dicText = "currency_name", dicCode = "currency_code")
	@Schema(description = "币种")
    private String currencyCode;
	/**汇率*/
	@Excel(name = "汇率", width = 15)
	@Schema(description = "汇率")
    private java.math.BigDecimal exchangeRate;
	/**含税总额*/
	@Excel(name = "含税总额", width = 15)
	@Schema(description = "含税总额")
    private java.math.BigDecimal orderTotal;
	/**不含税总额*/
	@Excel(name = "不含税总额", width = 15)
	@Schema(description = "不含税总额")
    private java.math.BigDecimal orderNet;
	/**税金*/
	@Excel(name = "税金", width = 15)
	@Schema(description = "税金")
    private java.math.BigDecimal orderTax;
	/**要求到货日期*/
	@Excel(name = "要求到货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@Schema(description = "要求到货日期")
    private Date expectedDate;
	/**付款账期(天)*/
	@Excel(name = "付款账期(天)", width = 15)
	@Schema(description = "付款账期(天)")
    private Integer paymentDays;
	/**建议入库仓库id*/
	@Excel(name = "建议入库仓库id", width = 15, dictTable = "mis_warehouse", dicText = "name", dicCode = "id")
    @Dict(dictTable = "mis_warehouse", dicText = "name", dicCode = "id")
	@Schema(description = "建议入库仓库id")
    private String warehouseId;
	/**建议入库仓库*/
	@Excel(name = "建议入库仓库", width = 15)
	@Schema(description = "建议入库仓库")
    private String warehouseName;
	/**业务状态*/
	@Excel(name = "业务状态", width = 15, dicCode = "scm_purchase_status")
    @Dict(dicCode = "scm_purchase_status")
	@Schema(description = "业务状态")
    private String status;
	/**审核状态*/
	@Excel(name = "审核状态", width = 15, dicCode = "approval_status")
    @Dict(dicCode = "approval_status")
	@Schema(description = "审核状态")
    private String approveStatus;
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

	@ExcelCollection(name="采购明细")
	@Schema(description = "采购明细")
	private List<PurchaseOrderDetail> purchaseOrderDetailList;

}

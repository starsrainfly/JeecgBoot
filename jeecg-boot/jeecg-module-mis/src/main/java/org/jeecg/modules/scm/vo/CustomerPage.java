package org.jeecg.modules.scm.vo;

import java.util.List;
import org.jeecg.modules.scm.entity.Customer;
import org.jeecg.modules.scm.entity.CustomerAddress;
import org.jeecg.modules.scm.entity.CustomerQualification;
import org.jeecg.modules.scm.entity.CustomerContact;
import org.jeecg.modules.scm.entity.CustomerSalesman;
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
 * @Description: 客户信息
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Data
@Schema(description="客户信息")
public class CustomerPage {

	/**id*/
	@Schema(description = "id")
    private String id;
	/**所属部门*/
	@Schema(description = "所属部门")
    private String sysOrgCode;
	/**客户编码*/
	@Excel(name = "客户编码", width = 15)
	@Schema(description = "客户编码")
    private String customerCode;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
	@Schema(description = "客户名称")
    private String customerName;
	/**客户评分*/
	@Excel(name = "客户评分", width = 15)
	@Schema(description = "客户评分")
    private java.math.BigDecimal customerRating;
	/**法人*/
	@Excel(name = "法人", width = 15)
	@Schema(description = "法人")
    private String corporation;
	/**客户类型*/
	@Excel(name = "客户类型", width = 15, dicCode = "customer_type")
    @Dict(dicCode = "customer_type")
	@Schema(description = "客户类型")
    private String customerType;
	/**客户介绍*/
	@Excel(name = "客户介绍", width = 15)
	@Schema(description = "客户介绍")
    private String about;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@Schema(description = "备注")
    private String remark;
	/**账期（天）*/
	@Excel(name = "账期（天）", width = 15)
	@Schema(description = "账期（天）")
    private String paymentDays;

    public String convertisDistrictCode() {
        return SpringContextUtils.getBean(ProvinceCityArea.class).getText(districtName);
    }

    public void convertsetDistrictCode(String text) {
        this.districtName = SpringContextUtils.getBean(ProvinceCityArea.class).getCode(text);
    }

	/**区编码*/
	@Excel(name = "区编码", width = 15,exportConvert=true,importConvert = true)
	@Schema(description = "区编码")
    private String districtCode;
	/**区名称*/
	@Excel(name = "区名称", width = 15)
	@Schema(description = "区名称")
    private String districtName;
	/**地址*/
	@Excel(name = "地址", width = 15)
	@Schema(description = "地址")
    private String address;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
    @Dict(dicCode = "status")
	@Schema(description = "状态")
    private String status;
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
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
	@Schema(description = "是否删除")
    private String delFlag;
	/**审核时间*/
	@Excel(name = "审核时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "审核时间")
    private Date approvalDate;
	/**审核状态*/
	@Excel(name = "审核状态", width = 15, dicCode = "approval_status")
    @Dict(dicCode = "approval_status")
	@Schema(description = "审核状态")
    private String approvalStatus;
	/**付款方式*/
	@Excel(name = "付款方式", width = 15, dicCode = "payment_type")
    @Dict(dicCode = "payment_type")
	@Schema(description = "付款方式")
    private String paymentType;
	/**审核人*/
	@Excel(name = "审核人", width = 15)
	@Schema(description = "审核人")
    private String approvalUser;

	@Excel(name = "业务员", width = 15, dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
	@Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
	@Schema(description = "业务员")
	private String salesmanId;
	/**业务员姓名*/
	@Excel(name = "业务姓名", width = 15)
	@Schema(description = "业务姓名")
	private String salesmanName;
	@Excel(name = "贸易类型", width = 15, dicCode = "scm_trade_type")
	@Dict(dicCode = "scm_trade_type")
	@Schema(description = "贸易类型")
	private String tradeType;
	@Excel(name = "国家/区域", width = 15, dicCode = "mdm_country_code")
	@Dict(dicCode = "mdm_country_code")
	@Schema(description = "国家/区域")
	private String regionCode;
	@Excel(name = "审核意见", width = 15)
	@Schema(description = "审核意见")
	private String approvalRemark;
	/**审核人id*/
	@Excel(name = "审核人id", width = 15)
	@Schema(description = "审核人id")
	private String approvalId;
	@ExcelCollection(name="收货地址")
	@Schema(description = "收货地址")
	private List<CustomerAddress> customerAddressList;
	@ExcelCollection(name="客户质证")
	@Schema(description = "客户质证")
	private List<CustomerQualification> customerQualificationList;
	@ExcelCollection(name="客户联系人")
	@Schema(description = "客户联系人")
	private List<CustomerContact> customerContactList;
	@ExcelCollection(name="客户销售员")
	@Schema(description = "客户销售员")
	private List<CustomerSalesman> customerSalesmanList;

}

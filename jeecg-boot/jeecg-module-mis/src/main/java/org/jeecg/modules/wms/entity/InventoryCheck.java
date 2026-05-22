package org.jeecg.modules.wms.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecg.common.aspect.annotation.Dict;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 盘库主表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Data
@TableName("wms_inventory_check")
@Schema(description="盘库主表")
public class InventoryCheck implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**盘库单号（PD)*/
    @Excel(name = "盘库单号（PD)", width = 15)
    @Schema(description = "盘库单号（PD)")
    private String checkNo;
	/**盘库范围（1位，2产品，3全仓）*/
    @Excel(name = "盘库范围（1位，2产品，3全仓）", width = 15, dicCode = "wms_check_scope")
    @Dict(dicCode = "wms_check_scope")
    @Schema(description = "盘库范围（1位，2产品，3全仓）")
    private String checkScope;
	/**盘库方法（1明盘，2盲盘）*/
    @Excel(name = "盘库方法（1明盘，2盲盘）", width = 15, dicCode = "wms_check_method")
    @Dict(dicCode = "wms_check_method")
    @Schema(description = "盘库方法（1明盘，2盲盘）")
    private String checkMethod;
	/**盘点仓库*/
    @Excel(name = "盘点仓库", width = 15, dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Dict(dictTable = "mis_warehouse where del_flag='0' and status='1'", dicText = "name", dicCode = "id")
    @Schema(description = "盘点仓库")
    private String warehouseId;
	/**盘点区域*/
    @Excel(name = "盘点区域", width = 15, dictTable = "mis_warehouse_area where del_flag='0' and status='1'", dicText = "area_code", dicCode = "id")
    @Dict(dictTable = "mis_warehouse_area where del_flag='0' and status='1'", dicText = "area_code", dicCode = "id")
    @Schema(description = "盘点区域")
    private String areaId;
	/**货架*/
    @Excel(name = "货架", width = 15, dictTable = "mis_warehouse_shelf where del_flag='0' and status='1'", dicText = "shelf_code", dicCode = "id")
    @Dict(dictTable = "mis_warehouse_shelf where del_flag='0' and status='1'", dicText = "shelf_code", dicCode = "id")
    @Schema(description = "货架")
    private String shelfId;

    /**货位*/
    @Excel(name = "货位", width = 15, dictTable = "mis_warehouse_Location where del_flag='0' and status='1'", dicText = "path_code", dicCode = "id")
    @Dict(dictTable = "mis_warehouse_location where del_flag='0' and status='1'", dicText = "path_code", dicCode = "id")
    @Schema(description = "货位")
    private String locationId;
	/**物料类型*/
    @Excel(name = "物料类型", width = 15, dicCode = "wms_item_type")
    @Dict(dicCode = "wms_item_type")
    @Schema(description = "物料类型")
    private String goodsType;
	/**产品id*/
    @Excel(name = "产品id", width = 15)
    @Schema(description = "产品id")
    private String goodsId;
	/**产品编码*/
    @Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String goodsCode;
	/**名称*/
    @Excel(name = "名称", width = 15)
    @Schema(description = "名称")
    private String goodsName;
	/**规格*/
    @Excel(name = "规格", width = 15)
    @Schema(description = "规格")
    private String goodsSpec;
	/**颜色*/
    @Excel(name = "颜色", width = 15)
    @Schema(description = "颜色")
    private String goodsColor;
	/**盘库状态*/
    @Excel(name = "盘库状态", width = 15, dicCode = "wms_check_status")
    @Dict(dicCode = "wms_check_status")
    @Schema(description = "盘库状态")
    private String checkStatus;
	/**审核状态*/
    @Excel(name = "审核状态", width = 15, dicCode = "approval_status")
    @Dict(dicCode = "approval_status")
    @Schema(description = "审核状态")
    private String approveStatus;
	/**盘点人*/
    @Excel(name = "盘点人", width = 15, dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Schema(description = "盘点人")
    private String checkUserId;
	/**盘点人姓名*/
    @Excel(name = "盘点人姓名", width = 15)
    @Schema(description = "盘点人姓名")
    private String checkUserName;
	/**盘点开始时间*/
    @Excel(name = "盘点开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "盘点开始时间")
    private Date checkStartTime;
	/**盘点完成时间*/
    @Excel(name = "盘点完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "盘点完成时间")
    private Date checkFinishedTime;
	/**总项数*/
    @Excel(name = "总项数", width = 15)
    @Schema(description = "总项数")
    private Integer totalItems;
	/**已盘项数*/
    @Excel(name = "已盘项数", width = 15)
    @Schema(description = "已盘项数")
    private Integer checkedItems;
	/**差异项数*/
    @Excel(name = "差异项数", width = 15)
    @Schema(description = "差异项数")
    private Integer diffItems;
	/**差异金额*/
    @Excel(name = "差异金额", width = 15)
    @Schema(description = "差异金额")
    private java.math.BigDecimal diffAmount;
	/**审核人id*/
    @Excel(name = "审核人id", width = 15, dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Dict(dictTable = "sys_user where del_flag='0' and status='1'", dicText = "realname", dicCode = "id")
    @Schema(description = "审核人id")
    private String approveId;
	/**审核人*/
    @Excel(name = "审核人", width = 15)
    @Schema(description = "审核人")
    private String approveUser;
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
	/**备注*/
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;
	/**删除标识*/
    @Excel(name = "删除标识", width = 15, dicCode = "del_flag")
    @Dict(dicCode = "del_flag")
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

package org.jeecg.modules.mes.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: mis_label_print_task
 * @Author: jeecg-boot
 * @Date:   2026-04-27
 * @Version: V1.0
 */
@Data
@TableName("mis_label_print_task")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="mis_label_print_task")
public class LabelPrintTask implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;
	/**作业编号（LP20240401001）*/
	@Excel(name = "作业编号（LP20240401001）", width = 15)
    @Schema(description = "作业编号（LP20240401001）")
    private String taskNo;
	/**生产批次ID*/
	@Excel(name = "生产批次ID", width = 15)
    @Schema(description = "生产批次ID")
    private String batchId;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
    @Schema(description = "批次号")
    private String batchNo;
//	/**工单ID*/
//	@Excel(name = "工单ID", width = 15)
//    @Schema(description = "工单ID")
//    private String productionTaskId;
//	/**工单号*/
//	@Excel(name = "工单号", width = 15)
//    @Schema(description = "工单号")
//    private String productionTaskNo;
	/**触发方式：MANUAL手动/AUTO完工自动*/
	@Excel(name = "触发方式：MANUAL手动/AUTO完工自动", width = 15, dicCode = "mdm_trigger_type")
	@Dict(dicCode = "mdm_trigger_type")
    @Schema(description = "触发方式：MANUAL手动/AUTO完工自动")
    private String triggerType;
	/**产品ID*/
	@Excel(name = "产品ID", width = 15)
    @Schema(description = "产品ID")
    private String productId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**产品颜色*/
	@Excel(name = "产品颜色", width = 15)
    @Schema(description = "产品颜色")
    private String productColor;
	/**打印产品名称*/
	@Excel(name = "打印产品名称", width = 15)
    @Schema(description = "打印产品名称")
    private String printProductName;
	/**模板ID*/
	@Excel(name = "模板ID", width = 15)
    @Schema(description = "模板ID")
    private String templateId;
	/**模板编码*/
	@Excel(name = "模板编码", width = 15)
    @Schema(description = "模板编码")
    private String templateCode;
	/**标签枚数*/
	@Excel(name = "标签枚数", width = 15)
    @Schema(description = "标签枚数")
    private Integer labelQty;
	/**打印份数*/
	@Excel(name = "打印份数", width = 15)
    @Schema(description = "打印份数")
    private Integer copies;
	/**标签宽度（mm）*/
	@Excel(name = "标签宽度（mm）", width = 15)
    @Schema(description = "标签宽度（mm）")
    private Integer labelWidth;
	/**标签高度（mm)*/
	@Excel(name = "标签高度（mm)", width = 15)
    @Schema(description = "标签高度（mm)")
    private Integer labelHeight;
	/**二维码内容*/
	@Excel(name = "二维码内容", width = 15)
    @Schema(description = "二维码内容")
    private String qrContent;
	/**二维码图片Base64*/
	@Excel(name = "二维码图片Base64", width = 15)
    @Schema(description = "二维码图片Base64")
    private String qrImage;
	/**状态：PENDING待打印/PRINTING打印中/COMPLETED已完成/FAILED失败*/
	@Excel(name = "状态：PENDING待打印/PRINTING打印中/COMPLETED已完成/FAILED失败", width = 15, dicCode = "mdm_print_status")
	@Dict(dicCode = "mdm_print_status")
    @Schema(description = "状态：PENDING待打印/PRINTING打印中/COMPLETED已完成/FAILED失败")
    private String status;
	/**公司*/
	@Excel(name = "公司", width = 15, dictTable = "sys_depart where del_flag='0' and org_category='1' and org_type='1'", dicText = "depart_name", dicCode = "id")
	@Dict(dictTable = "sys_depart where del_flag='0' and org_category='1' and org_type='1'", dicText = "depart_name", dicCode = "id")
    @Schema(description = "公司")
    private String companyId;
	/**公司名称*/
	@Excel(name = "公司名称", width = 15)
    @Schema(description = "公司名称")
    private String companyName;
	/**实际打印时间*/
	@Excel(name = "实际打印时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实际打印时间")
    private Date printTime;
	/**失败原因*/
	@Excel(name = "失败原因", width = 15)
    @Schema(description = "失败原因")
    private String failReason;
	/**打印机类型*/
	@Excel(name = "打印机类型", width = 15)
    @Schema(description = "打印机类型")
    private String printerType;
	/**打印机名称*/
	@Excel(name = "打印机名称", width = 15)
    @Schema(description = "打印机名称")
    private String printerName;
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

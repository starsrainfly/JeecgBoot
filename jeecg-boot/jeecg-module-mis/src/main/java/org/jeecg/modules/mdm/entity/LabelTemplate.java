package org.jeecg.modules.mdm.entity;

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
 * @Description: 标签模板
 * @Author: jeecg-boot
 * @Date:   2026-04-24
 * @Version: V1.0
 */
@Data
@TableName("mis_label_template")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="标签模板")
public class LabelTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;
	/**模板编码*/
	@Excel(name = "模板编码", width = 15)
    @Schema(description = "模板编码")
    private String templateCode;
	/**模板名称*/
	@Excel(name = "模板名称", width = 15)
    @Schema(description = "模板名称")
    private String templateName;
	/**模板类型：PRODUCT产品标签/BATCH批次标签/LOCATION库位标签*/
	@Excel(name = "模板类型：PRODUCT产品标签/BATCH批次标签/LOCATION库位标签", width = 15, dicCode = "mdm_label_template_type")
	@Dict(dicCode = "mdm_label_template_type")
    @Schema(description = "模板类型：PRODUCT产品标签/BATCH批次标签/LOCATION库位标签")
    private String templateType;
	/**指定产品*/
	@Excel(name = "指定产品", width = 15)
    @Schema(description = "指定产品")
    @Dict(dicCode = "mis_product where del_flag='0' and status='1',productName,id")
    private String productId;
	/**标签宽度(mm)*/
	@Excel(name = "标签宽度(mm)", width = 15)
    @Schema(description = "标签宽度(mm)")
    private Integer labelWidth;
	/**标签高度(mm)*/
	@Excel(name = "标签高度(mm)", width = 15)
    @Schema(description = "标签高度(mm)")
    private Integer labelHeight;
	/**打印DPI*/
	@Excel(name = "打印DPI", width = 15)
    @Schema(description = "打印DPI")
    private Integer dpi;
	/**模板元素配置JSON*/
	@Excel(name = "模板元素配置JSON", width = 15)
    @Schema(description = "模板元素配置JSON")
    private String contentJson;
	/**是否默认模板：0否/1是*/
	@Excel(name = "是否默认模板：0否/1是", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否默认模板：0否/1是")
    private String isDefault;
	/**是否系统模板：0否/1是（系统模板不可删除）*/
	@Excel(name = "是否系统模板：0否/1是（系统模板不可删除）", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否系统模板：0否/1是（系统模板不可删除）")
    private String isSystem;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
	@Dict(dicCode = "status")
    @Schema(description = "状态")
    private String status;
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

package org.jeecg.modules.mdm.vo;

import java.util.List;
import org.jeecg.modules.mdm.entity.ProcessRouting;
import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
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
 * @Description: 工艺管理
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
@Data
@Schema(description="工艺管理")
public class ProcessRoutingPage {

	/**主键*/
	@Schema(description = "主键")
    private String id;
	/**工艺编码*/
	@Excel(name = "工艺编码", width = 15)
	@Schema(description = "工艺编码")
    private String routingCode;
	/**工艺名称*/
	@Excel(name = "工艺名称", width = 15)
	@Schema(description = "工艺名称")
    private String routingName;
	/**版本*/
	@Excel(name = "版本", width = 15)
	@Schema(description = "版本")
    private String version;
	/**是否启用*/
	@Excel(name = "是否启用", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
	@Schema(description = "是否启用")
    private String isActive;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@Schema(description = "备注")
    private String remark;
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

	@ExcelCollection(name="工序步骤")
	@Schema(description = "工序步骤")
	private List<ProcessRoutingStep> processRoutingStepList;

}

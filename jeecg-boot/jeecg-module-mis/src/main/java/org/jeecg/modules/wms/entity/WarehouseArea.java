package org.jeecg.modules.wms.entity;

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
 * @Description: 仓库区域
 * @Author: jeecg-boot
 * @Date:   2026-04-05
 * @Version: V1.0
 */
@Data
@TableName("mis_warehouse_area")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="仓库区域")
public class WarehouseArea implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "mis_warehouse", dicText = "name", dicCode = "id")
	@Dict(dictTable = "mis_warehouse", dicText = "name", dicCode = "id")
    @Schema(description = "仓库")
    private String warehouseId;
	/**区域编码*/
	@Excel(name = "区域编码", width = 15)
    @Schema(description = "区域编码")
    private String areaCode;
	/**区域名称*/
	@Excel(name = "区域名称", width = 15)
    @Schema(description = "区域名称")
    private String name;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @Schema(description = "描述")
    private String description;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
	@Dict(dicCode = "status")
    @Schema(description = "状态")
    private String status;
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

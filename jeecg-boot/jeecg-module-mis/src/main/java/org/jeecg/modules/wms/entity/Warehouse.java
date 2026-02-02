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
 * @Description: 仓库信息
 * @Author: jeecg-boot
 * @Date:   2026-01-15
 * @Version: V1.0
 */
@Data
@TableName("mis_warehouse")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="仓库信息")
public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**仓库编码*/
	@Excel(name = "仓库编码", width = 15)
    @Schema(description = "仓库编码")
    private String warehouseCode;
	/**仓库名称*/
	@Excel(name = "仓库名称", width = 15)
    @Schema(description = "仓库名称")
    private String name;
	/**仓库类型*/
	@Excel(name = "仓库类型", width = 15, dicCode = "warehouse_type")
	@Dict(dicCode = "warehouse_type")
    @Schema(description = "仓库类型")
    private String warehouseType;
	/**仓库容量*/
	@Excel(name = "仓库容量", width = 15)
    @Schema(description = "仓库容量")
    private BigDecimal capacity;
	/**启用库位*/
	@Excel(name = "启用库位", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "启用库位")
    private String locationEnabled;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "status")
	@Dict(dicCode = "status")
    @Schema(description = "状态")
    private String status;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15, dicCode = "del_flag")
	@Dict(dicCode = "del_flag")
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

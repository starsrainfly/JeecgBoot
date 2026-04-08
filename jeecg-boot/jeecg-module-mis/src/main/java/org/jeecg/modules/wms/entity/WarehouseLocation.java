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
 * @Description: 库位管理
 * @Author: jeecg-boot
 * @Date:   2026-04-05
 * @Version: V1.0
 */
@Data
@TableName("mis_warehouse_location")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="库位管理")
public class WarehouseLocation implements Serializable {
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
	/**区域*/
	@Excel(name = "区域", width = 15, dictTable = "mis_warehouse_area", dicText = "name", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_area", dicText = "name", dicCode = "id")
    @Schema(description = "区域")
    private String areaId;
	/**货架*/
	@Excel(name = "货架", width = 15, dictTable = "mis_warehouse_shelf", dicText = "name", dicCode = "id")
	@Dict(dictTable = "mis_warehouse_shelf", dicText = "name", dicCode = "id")
    @Schema(description = "货架")
    private String shelfId;
	/**库位编码*/
	@Excel(name = "库位编码", width = 15)
    @Schema(description = "库位编码")
    private String locationCode;
	/**库位名称*/
	@Excel(name = "库位名称", width = 15)
    @Schema(description = "库位名称")
    private String name;
	/**货位类型*/
	@Excel(name = "货位类型", width = 15, dicCode = "location_type")
	@Dict(dicCode = "location_type")
    @Schema(description = "货位类型")
    private String locationType;
	/**长(m)*/
	@Excel(name = "长(m)", width = 15)
    @Schema(description = "长(m)")
    private BigDecimal length;
	/**宽(m)*/
	@Excel(name = "宽(m)", width = 15)
    @Schema(description = "宽(m)")
    private BigDecimal width;
	/**高(m)*/
	@Excel(name = "高(m)", width = 15)
    @Schema(description = "高(m)")
    private BigDecimal height;
	/**体积(m³)*/
	@Excel(name = "体积(m³)", width = 15)
    @Schema(description = "体积(m³)")
    private BigDecimal volume;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @Schema(description = "描述")
    private String description;
	/**是否默认*/
	@Excel(name = "是否默认", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否默认")
    private String isDefault;
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
	/**组合码*/
	@Excel(name = "组合码", width = 15)
    @Schema(description = "组合码")
    private String pathCode;
}

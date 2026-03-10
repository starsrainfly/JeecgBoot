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
 * @Description: 内外包装映射表
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
@Data
@TableName("mis_package_mapping")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="内外包装映射表")
public class PackageMapping implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**产品id*/
	@Excel(name = "产品id", width = 15)
    @Schema(description = "产品id")
    private String productId;
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @Schema(description = "产品编码")
    private String productCode;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(description = "产品名称")
    private String productName;
	/**内包装*/
	@Excel(name = "内包装", width = 15, dictTable = "mis_material where is_package='1' and package_type='0'", dicText = "material_spec", dicCode = "id")
	@Dict(dictTable = "mis_material where is_package='1' and package_type='0'", dicText = "material_spec", dicCode = "id")
    @Schema(description = "内包装")
    private String innerPackageId;
	/**内包装名称*/
	@Excel(name = "内包装名称", width = 15)
    @Schema(description = "内包装名称")
    private String innerPackageName;
	/**内包装规格*/
	@Excel(name = "内包装规格", width = 15)
    @Schema(description = "内包装规格")
    private String innerPackageSpec;
	/**外包装*/
	@Excel(name = "外包装", width = 15, dictTable = "mis_material where is_package='1' and package_type='1'", dicText = "material_spec", dicCode = "id")
	@Dict(dictTable = "mis_material where is_package='1' and package_type='1'", dicText = "material_spec", dicCode = "id")
    @Schema(description = "外包装")
    private String outerPackageId;
	/**外包装名称*/
	@Excel(name = "外包装名称", width = 15)
    @Schema(description = "外包装名称")
    private String outerPackageName;
	/**外包装规格*/
	@Excel(name = "外包装规格", width = 15)
    @Schema(description = "外包装规格")
    private String outerPackageSpec;
	/**每箱几桶*/
	@Excel(name = "每箱几桶", width = 15)
    @Schema(description = "每箱几桶")
    private Integer innerPerOuter;
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

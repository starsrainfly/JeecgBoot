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
 * @Date:   2026-03-24
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
    private java.lang.String id;
    /**内包装*/
    @Excel(name = "内包装", width = 15)
    @Schema(description = "内包装")
    private java.lang.String innerPackageId;
    /**内包装规格*/
    @Excel(name = "内包装规格", width = 15)
    @Schema(description = "内包装规格")
    private java.lang.String innerPackageSpec;
    /**外包装*/
    @Excel(name = "外包装", width = 15)
    @Schema(description = "外包装")
    private java.lang.String outerPackageId;
    /**外包装规格*/
    @Excel(name = "外包装规格", width = 15)
    @Schema(description = "外包装规格")
    private java.lang.String outerPackageSpec;
    /**每箱几桶*/
    @Excel(name = "每箱几桶", width = 15)
    @Schema(description = "每箱几桶")
    private java.lang.Integer innerPerOuter;
    /**删除标识*/
    @Excel(name = "删除标识", width = 15, dicCode = "del_flag")
    @Dict(dicCode = "del_flag")
    @Schema(description = "删除标识")
    @TableLogic
    private java.lang.String delFlag;
    /**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
    /**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
    /**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
}

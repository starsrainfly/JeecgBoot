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
 * @Description: 质检项目配置
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
@Data
@TableName("mis_qc_item_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="质检项目配置")
public class QcItemConfig implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
	/**recipe_spec字段名(驼峰)*/
	@Excel(name = "recipe_spec字段名(驼峰)", width = 15)
    @Schema(description = "recipe_spec字段名(驼峰)")
    private String fieldName;
	/**检验项目名称*/
	@Excel(name = "检验项目名称", width = 15)
    @Schema(description = "检验项目名称")
    private String itemName;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @Schema(description = "单位")
    private String unit;
	/**是否默认带出(1是/0否)*/
	@Excel(name = "是否默认带出(1是/0否)", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @Schema(description = "是否默认带出(1是/0否)")
    private String enabled;
	/**排序号*/
	@Excel(name = "排序号", width = 15)
    @Schema(description = "排序号")
    private Integer sortNo;
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
}

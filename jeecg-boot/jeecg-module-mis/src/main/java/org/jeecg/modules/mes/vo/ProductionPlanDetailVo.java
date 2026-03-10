package org.jeecg.modules.mes.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.mes.entity.ProductionPlanDetail;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description="生产计划明细")
public class ProductionPlanDetailVo extends ProductionPlanDetail {

    /**计划编码*/
    @ExcelCollection(name="计划编码")
    @Schema(description = "计划编码")
    private String PlanNo;
    /**剩余数量*/
    @ExcelCollection(name="剩余数量")
    @Schema(description = "剩余数量")
    private BigDecimal RemainingQty;
    /** 产品名称（冗余显示） */
    @ExcelCollection(name="产品名称")
    @Schema(description = "产品名称")
    private String productName;

    /** 包装名称（冗余显示） */
    @ExcelCollection(name="包装名称")
    @Schema(description = "包装名称")
    private String packageName;

    /** 来源类型字典文本 */
    @Excel(name = "计划类型", width = 15, dicCode = "mes_plan_type")
    @Dict(dicCode = "mes_plan_type")
    @Schema(description = "计划类型")
    private String planType;
}

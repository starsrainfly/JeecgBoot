package org.jeecg.modules.scm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jeecg.modules.scm.entity.SalesPaymentPlan;
import org.jeecgframework.poi.excel.annotation.Excel;

public class SalesPaymentPlanVo extends SalesPaymentPlan {
    /** 客户名称 */
    @Excel(name = "客户名称", width = 15)
    @Schema(description = "客户名称")
    private String customerName;
}

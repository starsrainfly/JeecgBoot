package org.jeecg.modules.scm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "成本核算-产品列表VO")
public class CostCalcProductVo {
    private String productId;
    private String productCode;
    private String productName;
    private String productSpec;
    private String productColor;
    private String recipeId;
    private String recipeCode;
    private String recipeName;
    private String recipeVersion;
    private String proportionTotal;
    private String proportionType;
}

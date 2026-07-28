package org.jeecg.modules.scm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "成本核算-明细弹窗VO")
public class CostCalcDetailVo {
    private String productId;
    private String productCode;
    private String productName;
    private String productSpec;
    private String productColor;
    private String recipeId;
    private String recipeCode;
    private String recipeName;
    private String recipeVersion;
    private BigDecimal proportionTotal;
    private String proportionType;
    private BigDecimal totalCostLatest;
    private BigDecimal totalCostAvg;
    private Boolean hasUnpriced;
    private List<CostCalcMaterialVo> materialList;
}

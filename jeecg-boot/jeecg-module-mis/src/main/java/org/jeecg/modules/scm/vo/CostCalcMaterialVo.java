package org.jeecg.modules.scm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "成本核算-物料明细VO")
public class CostCalcMaterialVo {
    private String materialId;
    private String materialCode;
    private String materialName;
    private String materialSpec;
    private BigDecimal proportion;
    private String unit;
    private BigDecimal latestPrice;
    private BigDecimal avgPrice;
    private String priceSource;
    private BigDecimal calcPrice;
    private BigDecimal amount;
}

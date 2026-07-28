package org.jeecg.modules.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "保存快照DTO")
public class CostCalcSnapshotDto {

    private String productId;
    private String calcType;
    private String remark;
}

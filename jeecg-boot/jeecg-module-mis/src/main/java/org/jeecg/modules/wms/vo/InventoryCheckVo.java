package org.jeecg.modules.wms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.wms.entity.InventoryCheck;
import org.jeecg.modules.wms.entity.InventoryCheckDetail;

import java.util.List;

@Data
@Schema(description = "盘点单Vo（主表+子表）")
public class InventoryCheckVo  {

    /** 盘点单ID */
    private String id;

    @Schema(description = "盘点明细列表")
    private List<InventoryCheckDetail> inventoryCheckDetailList;
}

package org.jeecg.modules.mdm.vo;

import lombok.Data;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.vo.PendingOrderLineVo;

import java.util.List;
@Data
public class ProductScanData {
    /** 产品编码 */
    private String productCode;

    /** 批次号 */
    private String batchNo;

    /** 订单未发明细 */
    private List<PendingOrderLineVo> orderLines;

    /** FIFO可用库存 */
    private List<Stock> stocks;
}

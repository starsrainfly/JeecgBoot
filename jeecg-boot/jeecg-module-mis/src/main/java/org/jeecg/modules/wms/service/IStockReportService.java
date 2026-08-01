package org.jeecg.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.vo.StockInSummaryVo;
import org.jeecg.modules.wms.vo.StockMonthlyReportVo;
import org.jeecg.modules.wms.vo.StockOutSummaryVo;
import org.jeecg.modules.wms.vo.StockWarehouseSummaryVo;

import java.util.List;
import java.util.Map;

/**
 * 库存统计报表 Service
 */
public interface IStockReportService {

    /**
     * 入库汇总表（按供应商）
     */
    IPage<StockInSummaryVo> inSummaryBySupplier(Page<StockInSummaryVo> page, String startPeriod, String endPeriod, String supplierId);

    /**
     * 出库汇总表（按客户）
     */
    IPage<StockOutSummaryVo> outSummaryByCustomer(Page<StockOutSummaryVo> page, String startPeriod, String endPeriod, String customerId);

    /**
     * 收发存月报（按物料+月）
     */
    List<StockMonthlyReportVo> monthlyReport(String period, String warehouseId, String goodsCode);

    /**
     * 库存收发存汇总（按仓库+月）
     */
    List<StockWarehouseSummaryVo> warehouseSummary(String period);
}
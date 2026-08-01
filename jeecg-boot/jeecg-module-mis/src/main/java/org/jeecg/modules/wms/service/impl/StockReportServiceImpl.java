package org.jeecg.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.mapper.StockReportMapper;
import org.jeecg.modules.wms.service.IStockReportService;
import org.jeecg.modules.wms.vo.StockInSummaryVo;
import org.jeecg.modules.wms.vo.StockMonthlyReportVo;
import org.jeecg.modules.wms.vo.StockOutSummaryVo;
import org.jeecg.modules.wms.vo.StockWarehouseSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存统计报表 ServiceImpl
 *
 * 期初计算采用"反推法"：
 *   期初 = 当前库存 + 月份之后出库 - 月份之后入库
 * 优点：无需历史快照表，实时准确
 * 缺点：数据量极大时性能下降，历史数据删改会影响期初
 *
 * 后续建议：数据量 > 100万 时，增加 mis_stock_monthly_snapshot 快照表，
 * 每月末由定时任务自动生成，查询时直接读快照。
 */
@Service
public class StockReportServiceImpl implements IStockReportService {

    @Autowired
    private StockReportMapper stockReportMapper;

    @Override
    public IPage<StockInSummaryVo> inSummaryBySupplier(Page<StockInSummaryVo> page, String startPeriod, String endPeriod, String supplierId) {
        return stockReportMapper.selectInSummaryBySupplier(page, startPeriod, endPeriod, supplierId);
    }

    @Override
    public IPage<StockOutSummaryVo> outSummaryByCustomer(Page<StockOutSummaryVo> page, String startPeriod, String endPeriod, String customerId) {
        return stockReportMapper.selectOutSummaryByCustomer(page, startPeriod, endPeriod, customerId);
    }

    @Override
    public List<StockMonthlyReportVo> monthlyReport(String period, String warehouseId, String goodsCode) {
        // 1. 获取所有物料基础信息 + 当前库存
        List<StockMonthlyReportVo> list = stockReportMapper.selectGoodsBaseList(warehouseId, goodsCode);
        if (list.isEmpty()) {
            return list;
        }

        // 2. 查询指定月份入库
        List<Map<String, Object>> inList = stockReportMapper.selectInByPeriod(period, warehouseId, goodsCode);
        Map<String, Map<String, Object>> inMap = inList.stream()
                .collect(Collectors.toMap(m -> (String) m.get("goods_id"), m -> m, (a, b) -> a));

        // 3. 查询指定月份出库
        List<Map<String, Object>> outList = stockReportMapper.selectOutByPeriod(period, warehouseId, goodsCode);
        Map<String, Map<String, Object>> outMap = outList.stream()
                .collect(Collectors.toMap(m -> (String) m.get("goods_id"), m -> m, (a, b) -> a));

        // 4. 查询月份之后入库（用于反推期初）
        List<Map<String, Object>> inAfterList = stockReportMapper.selectInAfterPeriod(period, warehouseId, goodsCode);
        Map<String, Map<String, Object>> inAfterMap = inAfterList.stream()
                .collect(Collectors.toMap(m -> (String) m.get("goods_id"), m -> m, (a, b) -> a));

        // 5. 查询月份之后出库（用于反推期初）
        List<Map<String, Object>> outAfterList = stockReportMapper.selectOutAfterPeriod(period, warehouseId, goodsCode);
        Map<String, Map<String, Object>> outAfterMap = outAfterList.stream()
                .collect(Collectors.toMap(m -> (String) m.get("goods_id"), m -> m, (a, b) -> a));

        // 6. 组装计算
        for (StockMonthlyReportVo vo : list) {
            vo.setPeriod(period);

            BigDecimal currentQty = vo.getOpeningQty() == null ? BigDecimal.ZERO : vo.getOpeningQty();
            BigDecimal currentAmount = vo.getOpeningAmount() == null ? BigDecimal.ZERO : vo.getOpeningAmount();

            BigDecimal inQty = toBigDecimal(inMap.getOrDefault(vo.getGoodsId(), new HashMap<>()).get("qty"));
            BigDecimal inAmount = toBigDecimal(inMap.getOrDefault(vo.getGoodsId(), new HashMap<>()).get("amount"));
            BigDecimal outQty = toBigDecimal(outMap.getOrDefault(vo.getGoodsId(), new HashMap<>()).get("qty"));
            BigDecimal outAmount = toBigDecimal(outMap.getOrDefault(vo.getGoodsId(), new HashMap<>()).get("amount"));
            BigDecimal inAfterQty = toBigDecimal(inAfterMap.getOrDefault(vo.getGoodsId(), new HashMap<>()).get("qty"));
            BigDecimal inAfterAmount = toBigDecimal(inAfterMap.getOrDefault(vo.getGoodsId(), new HashMap<>()).get("amount"));
            BigDecimal outAfterQty = toBigDecimal(outAfterMap.getOrDefault(vo.getGoodsId(), new HashMap<>()).get("qty"));
            BigDecimal outAfterAmount = toBigDecimal(outAfterMap.getOrDefault(vo.getGoodsId(), new HashMap<>()).get("amount"));

            // 期初 = 当前库存 + 月份之后出库 - 月份之后入库
            BigDecimal openingQty = currentQty.add(outAfterQty).subtract(inAfterQty);
            BigDecimal openingAmount = currentAmount.add(outAfterAmount).subtract(inAfterAmount);

            // 期末 = 期初 + 月份入库 - 月份出库
            BigDecimal closingQty = openingQty.add(inQty).subtract(outQty);
            BigDecimal closingAmount = openingAmount.add(inAmount).subtract(outAmount);

            vo.setOpeningQty(openingQty);
            vo.setOpeningAmount(openingAmount);
            vo.setInQty(inQty);
            vo.setInAmount(inAmount);
            vo.setOutQty(outQty);
            vo.setOutAmount(outAmount);
            vo.setClosingQty(closingQty);
            vo.setClosingAmount(closingAmount);
        }

        return list.stream()
                .filter(vo -> vo.getOpeningQty().compareTo(BigDecimal.ZERO) != 0
                        || vo.getInQty().compareTo(BigDecimal.ZERO) != 0
                        || vo.getOutQty().compareTo(BigDecimal.ZERO) != 0
                        || vo.getClosingQty().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockWarehouseSummaryVo> warehouseSummary(String period) {
        List<StockWarehouseSummaryVo> list = stockReportMapper.selectWarehouseBaseList();
        if (list.isEmpty()) {
            return list;
        }

        List<Map<String, Object>> inList = stockReportMapper.selectInByPeriodGroupByWarehouse(period);
        Map<String, Map<String, Object>> inMap = inList.stream()
                .collect(Collectors.toMap(m -> (String) m.get("warehouse_id"), m -> m, (a, b) -> a));

        List<Map<String, Object>> outList = stockReportMapper.selectOutByPeriodGroupByWarehouse(period);
        Map<String, Map<String, Object>> outMap = outList.stream()
                .collect(Collectors.toMap(m -> (String) m.get("warehouse_id"), m -> m, (a, b) -> a));

        List<Map<String, Object>> inAfterList = stockReportMapper.selectInAfterPeriodGroupByWarehouse(period);
        Map<String, Map<String, Object>> inAfterMap = inAfterList.stream()
                .collect(Collectors.toMap(m -> (String) m.get("warehouse_id"), m -> m, (a, b) -> a));

        List<Map<String, Object>> outAfterList = stockReportMapper.selectOutAfterPeriodGroupByWarehouse(period);
        Map<String, Map<String, Object>> outAfterMap = outAfterList.stream()
                .collect(Collectors.toMap(m -> (String) m.get("warehouse_id"), m -> m, (a, b) -> a));

        for (StockWarehouseSummaryVo vo : list) {
            vo.setPeriod(period);

            BigDecimal currentQty = vo.getOpeningQty() == null ? BigDecimal.ZERO : vo.getOpeningQty();
            BigDecimal currentAmount = vo.getOpeningAmount() == null ? BigDecimal.ZERO : vo.getOpeningAmount();

            BigDecimal inQty = toBigDecimal(inMap.getOrDefault(vo.getWarehouseId(), new HashMap<>()).get("qty"));
            BigDecimal inAmount = toBigDecimal(inMap.getOrDefault(vo.getWarehouseId(), new HashMap<>()).get("amount"));
            BigDecimal outQty = toBigDecimal(outMap.getOrDefault(vo.getWarehouseId(), new HashMap<>()).get("qty"));
            BigDecimal outAmount = toBigDecimal(outMap.getOrDefault(vo.getWarehouseId(), new HashMap<>()).get("amount"));
            BigDecimal inAfterQty = toBigDecimal(inAfterMap.getOrDefault(vo.getWarehouseId(), new HashMap<>()).get("qty"));
            BigDecimal inAfterAmount = toBigDecimal(inAfterMap.getOrDefault(vo.getWarehouseId(), new HashMap<>()).get("amount"));
            BigDecimal outAfterQty = toBigDecimal(outAfterMap.getOrDefault(vo.getWarehouseId(), new HashMap<>()).get("qty"));
            BigDecimal outAfterAmount = toBigDecimal(outAfterMap.getOrDefault(vo.getWarehouseId(), new HashMap<>()).get("amount"));

            BigDecimal openingQty = currentQty.add(outAfterQty).subtract(inAfterQty);
            BigDecimal openingAmount = currentAmount.add(outAfterAmount).subtract(inAfterAmount);
            BigDecimal closingQty = openingQty.add(inQty).subtract(outQty);
            BigDecimal closingAmount = openingAmount.add(inAmount).subtract(outAmount);

            vo.setOpeningQty(openingQty);
            vo.setOpeningAmount(openingAmount);
            vo.setInQty(inQty);
            vo.setInAmount(inAmount);
            vo.setOutQty(outQty);
            vo.setOutAmount(outAmount);
            vo.setClosingQty(closingQty);
            vo.setClosingAmount(closingAmount);
        }

        return list.stream()
                .filter(vo -> vo.getOpeningQty().compareTo(BigDecimal.ZERO) != 0
                        || vo.getInQty().compareTo(BigDecimal.ZERO) != 0
                        || vo.getOutQty().compareTo(BigDecimal.ZERO) != 0
                        || vo.getClosingQty().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        return new BigDecimal(obj.toString());
    }
}

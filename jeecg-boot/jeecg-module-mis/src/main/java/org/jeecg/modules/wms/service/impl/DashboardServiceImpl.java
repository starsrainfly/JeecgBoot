package org.jeecg.modules.wms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.wms.entity.StockIn;
import org.jeecg.modules.wms.entity.StockOut;
import org.jeecg.modules.wms.mapper.StockInMapper;
import org.jeecg.modules.wms.mapper.StockMapper;
import org.jeecg.modules.wms.mapper.StockOutMapper;
import org.jeecg.modules.wms.service.IDashboardService;
import org.jeecg.modules.wms.vo.WarehouseDashboardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements  IDashboardService {

    @Autowired
    private StockInMapper stockInMapper;
    @Autowired
    private StockOutMapper stockOutMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private ISysDictService dictService;
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;

//    private static final String CACHE_KEY = "wms:dashboard:warehouse";
//    private static final long CACHE_MINUTES = 1;

    @Override
    public WarehouseDashboardVo getWarehouseDashboardData() {
//        WarehouseDashboardVo cached = (WarehouseDashboardVo) redisTemplate.opsForValue().get(CACHE_KEY);
//        if (cached != null) {
//            return cached;
//        }

        WarehouseDashboardVo vo = new WarehouseDashboardVo();
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);
        LocalDateTime weekStart = today.minusDays(6).atStartOfDay();

//        // 1. 待入库单数 - 主表统计，状态=0审核中
//        LambdaQueryWrapper<StockIn> inWrapper = new LambdaQueryWrapper<>();
//        inWrapper.eq(StockIn::getApproveStatus, "0")
//                .eq(StockIn::getDelFlag, "0");
//        vo.setPendingInCount(stockInMapper.selectCount(inWrapper));
//
//
//        // 2. 待出库单数 - 主表统计，状态=0审核中
//        LambdaQueryWrapper<StockOut> outWrapper = new LambdaQueryWrapper<>();
//        outWrapper.eq(StockOut::getApproveStatus, "0")
//                .eq(StockOut::getDelFlag, "0");
//        vo.setPendingOutCount(stockOutMapper.selectCount(outWrapper));
        // 1. 待材料入库 - is_product = '0'
        LambdaQueryWrapper<StockIn> materialInWrapper = new LambdaQueryWrapper<>();
        materialInWrapper.eq(StockIn::getApproveStatus, "0")
                .eq(StockIn::getIsProduct, "0")
                .eq(StockIn::getDelFlag, "0");
        vo.setPendingMaterialInCount(stockInMapper.selectCount(materialInWrapper));

// 2. 待产品入库 - is_product = '1'
        LambdaQueryWrapper<StockIn> productInWrapper = new LambdaQueryWrapper<>();
        productInWrapper.eq(StockIn::getApproveStatus, "0")
                .eq(StockIn::getIsProduct, "1")
                .eq(StockIn::getDelFlag, "0");
        vo.setPendingProductInCount(stockInMapper.selectCount(productInWrapper));

// 3. 待材料出库 - is_product = '0'
        LambdaQueryWrapper<StockOut> materialOutWrapper = new LambdaQueryWrapper<>();
        materialOutWrapper.eq(StockOut::getApproveStatus, "0")
                .eq(StockOut::getIsProduct, "0")
                .eq(StockOut::getDelFlag, "0");
        vo.setPendingMaterialOutCount(stockOutMapper.selectCount(materialOutWrapper));

// 4. 待产品出库 - is_product = '1'
        LambdaQueryWrapper<StockOut> productOutWrapper = new LambdaQueryWrapper<>();
        productOutWrapper.eq(StockOut::getApproveStatus, "0")
                .eq(StockOut::getIsProduct, "1")
                .eq(StockOut::getDelFlag, "0");
        vo.setPendingProductOutCount(stockOutMapper.selectCount(productOutWrapper));

        // 3. 库存预警数
        vo.setStockWarningCount(stockMapper.selectWarningCount());

        // 4. 今日入库数量 - 明细汇总，主表状态=1通过
        vo.setTodayInQty(stockInMapper.selectTodayInQty(todayStart, todayEnd));

        // 5. 今日出库数量 - 明细汇总，主表状态=1通过
        vo.setTodayOutQty(stockOutMapper.selectTodayOutQty(todayStart, todayEnd));

        // 6. 库存锁定总数量
        vo.setLockedQty(stockMapper.selectTotalLockedQty());

        // 7. 近7天趋势 - 合并出入库数据
        List<WarehouseDashboardVo.DailyTrend> inTrend = stockInMapper.select7DaysInTrend(weekStart, todayEnd);
        List<WarehouseDashboardVo.DailyTrend> outTrend = stockOutMapper.select7DaysOutTrend(weekStart, todayEnd);
        vo.setTrendList(mergeTrend(inTrend, outTrend));

        // 8. 待办列表 - 状态=0审核中
        List<WarehouseDashboardVo.PendingInItem> pendingInList = stockInMapper.selectRecentPending(5);

        // 2. 手动翻译字典
        for (WarehouseDashboardVo.PendingInItem item : pendingInList) {
            if (StrUtil.isNotBlank(item.getStockInType())) {
                String dictText = dictService.queryDictTextByKey("wms_stock_in_type", item.getStockInType());
                item.setStockInType_dictText(dictText);
            }
        }
        vo.setPendingInList(pendingInList);//stockInMapper.selectRecentPending(5)

        // 9. 预警Top5
       // vo.setWarningMaterialList(stockInventoryMapper.selectWarningTop5());
        List<WarehouseDashboardVo.WarningMaterial> warningList = stockMapper.selectWarningTop5();

        // 处理预警类型（MySQL 5.7 无法在 SQL 里完成，放到 Java 处理）

        for (WarehouseDashboardVo.WarningMaterial item : warningList) {
            String warningType = calculateWarningType(item, today);
            item.setWarningType(warningType);

            // 计算缺口/超量
            if ("0".equals(warningType)) {
                // 缺货：缺口 = 安全库存 - 可用库存（正数表示缺多少）
                item.setShortageQty(item.getSafetyStock().subtract(item.getAvailableQty()));
            }
            else if ("1".equals(warningType)) {
                // 积压：超量 = 可用库存 - 最高库存（正数表示超多少）
                item.setShortageQty(item.getAvailableQty().subtract(item.getMaxStock()));
            }
            else {
                // 不预警：显示 0 或 null
                item.setShortageQty(null);  // 或 BigDecimal.ZERO
            }
        }

        vo.setWarningMaterialList(warningList);

        // 10. 效期预警列表
        vo.setExpiryAlertList(stockMapper.selectExpiryAlertList());

       // redisTemplate.opsForValue().set(CACHE_KEY, vo, CACHE_MINUTES, TimeUnit.MINUTES);
        return vo;
    }

    /**
     * 计算预警类型
     * 0=缺货, 1=积压, 2=近效期
     */
    private String calculateWarningType(WarehouseDashboardVo.WarningMaterial item, LocalDate today) {
        BigDecimal availableQty = item.getAvailableQty();
        BigDecimal safetyStock = item.getSafetyStock();
        BigDecimal maxStock = item.getMaxStock();

        // 缺货优先
        if (safetyStock != null && safetyStock.compareTo(BigDecimal.ZERO) > 0
                && availableQty.compareTo(safetyStock) <= 0) {
            return "0";
        }

        // 积压次之
        if (maxStock != null && maxStock.compareTo(BigDecimal.ZERO) > 0
                && availableQty.compareTo(maxStock) >= 0) {
            return "1";
        }

        // 效期预警
        if (item.getNearestExpiryDate() != null) {
            int alertDays = item.getMaterialAlertDays() != null ? item.getMaterialAlertDays()
                    : (item.getProductAlertDays() != null ? item.getProductAlertDays() : 30);

            // 今天 + alertDays = 预警截止日期
            Date alertDate = Date.from(today.plusDays(alertDays)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
            // 最近效期 <= 预警截止日期，说明在预警范围内
            if (!item.getNearestExpiryDate().after(alertDate)) {
                return "2";
            }
        }

        return null; // 都没设置，不预警
    }

    private List<WarehouseDashboardVo.DailyTrend> mergeTrend(
            List<WarehouseDashboardVo.DailyTrend> inList,
            List<WarehouseDashboardVo.DailyTrend> outList) {

        Map<String, BigDecimal> inMap = inList.stream()
                .collect(Collectors.toMap(
                        WarehouseDashboardVo.DailyTrend::getDate,
                        WarehouseDashboardVo.DailyTrend::getInQty,
                        (v1, v2) -> v1.add(v2),
                        LinkedHashMap::new
                ));

        Map<String, BigDecimal> outMap = outList.stream()
                .collect(Collectors.toMap(
                        WarehouseDashboardVo.DailyTrend::getDate,
                        WarehouseDashboardVo.DailyTrend::getOutQty,
                        (v1, v2) -> v1.add(v2),
                        LinkedHashMap::new
                ));

        Set<String> allDates = new TreeSet<>();
        allDates.addAll(inMap.keySet());
        allDates.addAll(outMap.keySet());

        List<WarehouseDashboardVo.DailyTrend> result = new ArrayList<>();
        for (String date : allDates) {
            WarehouseDashboardVo.DailyTrend item = new WarehouseDashboardVo.DailyTrend();
            item.setDate(date);
            item.setInQty(inMap.getOrDefault(date, BigDecimal.ZERO));
            item.setOutQty(outMap.getOrDefault(date, BigDecimal.ZERO));
            result.add(item);
        }

        return result;
    }
}

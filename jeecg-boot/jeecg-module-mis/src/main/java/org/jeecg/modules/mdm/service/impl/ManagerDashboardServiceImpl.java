package org.jeecg.modules.mdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.mdm.service.IManagerDashboardService;

import org.jeecg.modules.mdm.vo.ManagerDashboardVo;
import org.jeecg.modules.mes.entity.ProductionOrder;
import org.jeecg.modules.mes.mapper.ProductionOrderMapper;
import org.jeecg.modules.scm.mapper.ReceiptOrderMapper;
import org.jeecg.modules.scm.mapper.SalesOrderMapper;
import org.jeecg.modules.wms.mapper.StockMapper;
import org.jeecg.modules.wms.mapper.StockOutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManagerDashboardServiceImpl implements IManagerDashboardService {

    @Autowired
    private SalesOrderMapper salesOrderMapper;
    @Autowired
    private ReceiptOrderMapper receiptOrderMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private ProductionOrderMapper productionOrderMapper;
    @Autowired
    private StockOutMapper stockOutMapper;

    @Override
    public ManagerDashboardVo getManagerDashboardData() {
        ManagerDashboardVo vo = new ManagerDashboardVo();
        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = today.atTime(LocalTime.MAX);

        // 1. 本月销售额（财务审核通过的销售订单）
        vo.setMonthSalesAmount(salesOrderMapper.selectMonthSalesAmount(monthStart, monthEnd));

        // 2. 本月回款额
        vo.setMonthReceiptAmount(receiptOrderMapper.selectMonthReceiptAmount(monthStart, monthEnd));

        // 3. 待生产工单数（状态=1已下达）
        LambdaQueryWrapper<ProductionOrder> produceWrapper = new LambdaQueryWrapper<>();
        produceWrapper.eq(ProductionOrder::getStatus, "1")
                .eq(ProductionOrder::getDelFlag, "0");
        vo.setPendingProduceOrderCount(productionOrderMapper.selectCount(produceWrapper));

        // 4. 待审核单据总数
        long pendingSalesAudit = salesOrderMapper.selectPendingSalesAuditCount();
        long pendingFinanceAudit = salesOrderMapper.selectPendingFinanceAuditCount();
        // 出入库待审核数复用仓库的
        long pendingInAudit = stockMapper.selectPendingInAuditCount();
        long pendingOutAudit = stockMapper.selectPendingOutAuditCount();
        vo.setPendingAuditCount(pendingSalesAudit + pendingFinanceAudit + pendingInAudit + pendingOutAudit);

        // 5. 库存预警数（复用仓库）
        vo.setStockWarningCount(stockMapper.selectWarningCount());

        // 6. 本月出库额（产品出库）
        // 6. 本月材料出库额（is_product = '0'）
        vo.setMonthMaterialOutAmount(stockOutMapper.selectMonthOutAmount(monthStart, monthEnd, "0"));

        // 7. 本月产品出库额（is_product = '1'）
        vo.setMonthProductOutAmount(stockOutMapper.selectMonthOutAmount(monthStart, monthEnd, "1"));

        // 8. 近30天销售趋势
        LocalDateTime day30Start = today.minusDays(29).atStartOfDay();
        vo.setSalesTrendList(salesOrderMapper.select30DaysTrend(day30Start, monthEnd));

        // 9. 本月收支对比
        ManagerDashboardVo.MonthIncomeExpense incomeExpense = new ManagerDashboardVo.MonthIncomeExpense();
        incomeExpense.setSalesAmount(vo.getMonthSalesAmount());
        incomeExpense.setReceiptAmount(vo.getMonthReceiptAmount());
        incomeExpense.setOutAmount(vo.getMonthOutAmount());
        vo.setMonthIncomeExpense(incomeExpense);

        // 10. 生产工单状态分布
        vo.setProduceOrderStatusList(productionOrderMapper.selectStatusDistribution());

        // 11. 待审核单据分布
        List<ManagerDashboardVo.PendingAuditDist> auditDistList = new ArrayList<>();
        auditDistList.add(createAuditDist("SALES", "销售待审", pendingSalesAudit));
        auditDistList.add(createAuditDist("FINANCE", "财务待审", pendingFinanceAudit));
        auditDistList.add(createAuditDist("IN", "入库待审", pendingInAudit));
        auditDistList.add(createAuditDist("OUT", "出库待审", pendingOutAudit));
        vo.setPendingAuditDistList(auditDistList);

        // 11. 最近待审核销售订单
        vo.setRecentSalesOrderList(salesOrderMapper.selectRecentPendingAudit(5));

        // 12. 最近待生产工单
        vo.setRecentProduceOrderList(productionOrderMapper.selectRecentPending(5));

        return vo;
    }

    private ManagerDashboardVo.PendingAuditDist createAuditDist(String type, String text, long count) {
        ManagerDashboardVo.PendingAuditDist dist = new ManagerDashboardVo.PendingAuditDist();
        dist.setAuditType(type);
        dist.setAuditTypeText(text);
        dist.setCount(count);
        return dist;
    }
}

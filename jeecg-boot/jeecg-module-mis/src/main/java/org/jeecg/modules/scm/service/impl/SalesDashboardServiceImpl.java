package org.jeecg.modules.scm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.scm.mapper.SalesOrderMapper;
import org.jeecg.modules.scm.service.ISalesDashboardService;
import org.jeecg.modules.scm.vo.SalesDashboardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class SalesDashboardServiceImpl implements ISalesDashboardService {

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Override
    public SalesDashboardVo getSalesDashboard(String salesmanId) {
        SalesDashboardVo vo = new SalesDashboardVo();

        // ========== 指标卡片 ==========
        BigDecimal monthOrderAmount = salesOrderMapper.getMonthOrderAmount(salesmanId);
        vo.setMonthOrderAmount(monthOrderAmount != null ? monthOrderAmount : BigDecimal.ZERO);

        BigDecimal monthReceiptAmount = salesOrderMapper.getMonthReceiptAmount(salesmanId);
        vo.setMonthReceiptAmount(monthReceiptAmount != null ? monthReceiptAmount : BigDecimal.ZERO);

        Long pendingQuoteCount = salesOrderMapper.getPendingQuoteCount(salesmanId);
        vo.setPendingQuoteCount(pendingQuoteCount != null ? pendingQuoteCount : 0L);

        Long pendingOrderCount = salesOrderMapper.getPendingOrderCount(salesmanId);
        vo.setPendingOrderCount(pendingOrderCount != null ? pendingOrderCount : 0L);

        Long monthNewCustomerCount = salesOrderMapper.getMonthNewCustomerCount(salesmanId);
        vo.setMonthNewCustomerCount(monthNewCustomerCount != null ? monthNewCustomerCount : 0L);

        BigDecimal monthUnpaidAmount = salesOrderMapper.getMonthUnpaidAmount(salesmanId);
        vo.setMonthUnpaidAmount(monthUnpaidAmount != null ? monthUnpaidAmount : BigDecimal.ZERO);

        // ========== 待办列表 ==========
        vo.setPendingQuotes(salesOrderMapper.getPendingQuotes(salesmanId));
        vo.setPendingOrders(salesOrderMapper.getPendingOrders(salesmanId));
        vo.setPendingDeliveryOrders(salesOrderMapper.getPendingDeliveryOrders(salesmanId));
        vo.setNearDeliveryOrders(salesOrderMapper.getNearDeliveryOrders(salesmanId));
        vo.setPendingPaymentPlans(salesOrderMapper.getPendingPaymentPlans(salesmanId));

        // ========== 图表数据 ==========
        vo.setMonthTrend(salesOrderMapper.getMonthTrend(salesmanId));
        vo.setProductTop5(salesOrderMapper.getProductTop5(salesmanId,null));
        vo.setCustomerTypeDist(salesOrderMapper.getCustomerTypeDist(salesmanId));

        // 4. 新增：首页需要的全量数据
        if (StringUtils.isBlank(salesmanId)) {
            // 只有管理员/首页才查全量
            vo.setSalesmanTop5(salesOrderMapper.selectSalesmanTop5(null));
        }

        return vo;
    }

    @Override
    public SalesDashboardVo getSalesSummary(String dateRange) {
        SalesDashboardVo vo = new SalesDashboardVo();

        // ========== 指标卡片 ==========
        BigDecimal monthOrderAmount = salesOrderMapper.getMonthOrderAmount(null);
        vo.setMonthOrderAmount(monthOrderAmount != null ? monthOrderAmount : BigDecimal.ZERO);

        BigDecimal monthReceiptAmount = salesOrderMapper.getMonthReceiptAmount(null);
        vo.setMonthReceiptAmount(monthReceiptAmount != null ? monthReceiptAmount : BigDecimal.ZERO);

        Long pendingQuoteCount = salesOrderMapper.getPendingQuoteCount(null);
        vo.setPendingQuoteCount(pendingQuoteCount != null ? pendingQuoteCount : 0L);

        Long pendingOrderCount = salesOrderMapper.getPendingOrderCount(null);
        vo.setPendingOrderCount(pendingOrderCount != null ? pendingOrderCount : 0L);

        Long monthNewCustomerCount = salesOrderMapper.getMonthNewCustomerCount(null);
        vo.setMonthNewCustomerCount(monthNewCustomerCount != null ? monthNewCustomerCount : 0L);

        BigDecimal monthUnpaidAmount = salesOrderMapper.getMonthUnpaidAmount(null);
        vo.setMonthUnpaidAmount(monthUnpaidAmount != null ? monthUnpaidAmount : BigDecimal.ZERO);

        // ========== 待办列表 ==========
        vo.setPendingQuotes(salesOrderMapper.getPendingQuotes(null));
        vo.setPendingOrders(salesOrderMapper.getPendingOrders(null));
        vo.setPendingDeliveryOrders(salesOrderMapper.getPendingDeliveryOrders(null));
        vo.setNearDeliveryOrders(salesOrderMapper.getNearDeliveryOrders(null));
        vo.setPendingPaymentPlans(salesOrderMapper.getPendingPaymentPlans(null));

        // ========== 图表数据 ==========
        vo.setMonthTrend(salesOrderMapper.getMonthTrend(null));
        vo.setProductTop5(salesOrderMapper.getProductTop5(null,dateRange));
        vo.setCustomerTypeDist(salesOrderMapper.getCustomerTypeDist(null));

        // 4. 新增：首页需要的全量数据

         vo.setSalesmanTop5(salesOrderMapper.selectSalesmanTop5(dateRange));


        return vo;
    }


}

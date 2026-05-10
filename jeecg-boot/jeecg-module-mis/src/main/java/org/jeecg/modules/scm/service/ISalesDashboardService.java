package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.vo.SalesDashboardVo;

public interface ISalesDashboardService {
    SalesDashboardVo getSalesDashboard(String salesmanId);

    SalesDashboardVo getSalesSummary(String dateRange);
}

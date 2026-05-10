package org.jeecg.modules.mes.service;

import org.jeecg.modules.mes.vo.ProductionDashboardVo;

public interface IProductionDashboardService {
    /**
     * 获取生产工作台数据
     * @param viewType 视图类型：manager-管理者, worker-工人
     * @param operatorId 操作员ID（工人视图需要）
     */
    ProductionDashboardVo getProductionDashboard(String viewType, String operatorId);
}

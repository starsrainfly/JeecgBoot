package org.jeecg.modules.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.vo.WarehouseDashboardVo;

public interface IDashboardService  {

    WarehouseDashboardVo getWarehouseDashboardData();
}

package org.jeecg.modules.wms.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.service.IDashboardService;
import org.jeecg.modules.wms.service.IStockService;
import org.jeecg.modules.wms.vo.WarehouseDashboardVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "WMS-首页仪表盘")
@RestController
@RequestMapping("/wms/dashboard")
@RequiredArgsConstructor
public class DashboardController  {
    private final IDashboardService dashboardService;

    @ApiOperation(value = "仓库工作台-首页数据", notes = "统计待入库、待出库、库存预警、今日出入库量")
    @GetMapping("/warehouse")
    public Result<WarehouseDashboardVo> getWarehouseData() {
        WarehouseDashboardVo vo = dashboardService.getWarehouseDashboardData();
        return Result.OK(vo);
    }
}

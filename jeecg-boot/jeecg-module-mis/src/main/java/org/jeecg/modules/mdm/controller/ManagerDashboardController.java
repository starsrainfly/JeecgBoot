package org.jeecg.modules.mdm.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.mdm.service.IManagerDashboardService;
import org.jeecg.modules.mdm.vo.ManagerDashboardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "管理员工作台")
@RestController
@RequestMapping("/dashboard/manager")
@RequiredArgsConstructor
public class ManagerDashboardController {
    @Autowired
    private IManagerDashboardService managerDashboardService;

    @ApiOperation(value = "管理员工作台-首页数据")
    @GetMapping
    public Result<ManagerDashboardVo> getManagerDashboardData() {
        ManagerDashboardVo vo = managerDashboardService.getManagerDashboardData();
        return Result.OK(vo);
    }
}

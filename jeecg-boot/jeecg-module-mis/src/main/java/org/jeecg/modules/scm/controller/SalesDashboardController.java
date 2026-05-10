package org.jeecg.modules.scm.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.scm.service.ISalesDashboardService;
import org.jeecg.modules.scm.vo.SalesDashboardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "销售工作台")
@RestController
@RequestMapping("/scm/salesDashboard")
public class SalesDashboardController {
    @Autowired
    private ISalesDashboardService salesDashboardService;

    /**
     * 销售工作台数据（业务员隔离）
     */
    @GetMapping("/data")
    @ApiOperation(value = "获取销售工作台数据", notes = "根据当前登录用户的 salesman_id 隔离数据")
    public Result<SalesDashboardVo> getSalesDashboard() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        SalesDashboardVo vo = salesDashboardService.getSalesDashboard(sysUser.getId());
        return Result.OK(vo);
    }

    /**
     * 销售数据汇总（首页用，全量）
     */
    @GetMapping("/summary")
    @ApiOperation("销售数据汇总（首页运营驾驶舱用，全量数据）")
    public Result<SalesDashboardVo> getSalesSummary(@RequestParam(required = false, defaultValue = "week") String dateRange) {

        SalesDashboardVo vo = salesDashboardService.getSalesSummary(dateRange);
        return Result.OK(vo);
    }
}

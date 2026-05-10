package org.jeecg.modules.mes.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.mes.service.IProductionDashboardService;
import org.jeecg.modules.mes.vo.ProductionDashboardVo;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags = "生产工作台")
@RestController
@RequestMapping("/mes/productionDashboard")
public class ProductionDashboardController {
    @Autowired
    private IProductionDashboardService productionDashboardService;


    @GetMapping("/data")
    @ApiOperation(value = "获取生产工作台数据", notes = "viewType: manager-管理者(看全部), worker-工人(看自己的)")
    public Result<ProductionDashboardVo> getProductionDashboard(
            HttpServletRequest request,
            @RequestParam(defaultValue = "manager")
            @ApiParam("视图类型：manager-管理者, worker-工人") String viewType) {

        // 获取当前登录用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String operatorId = sysUser.getId();

        ProductionDashboardVo vo = productionDashboardService.getProductionDashboard(viewType, operatorId);
        return Result.OK(vo);
    }
}

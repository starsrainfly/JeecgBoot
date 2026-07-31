package org.jeecg.modules.scm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.scm.service.ISalesOrderTrackingService;
import org.jeecg.modules.scm.vo.SalesOrderTrackingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "销售订单执行跟踪")
@Slf4j
@RestController
@RequestMapping("/scm/salesOrderTracking")
public class SalesOrderTrackingController {

    @Autowired
    private ISalesOrderTrackingService salesOrderTrackingService;

    @Operation(summary = "销售订单执行跟踪-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(@RequestParam Map<String, String> params,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.OK(salesOrderTrackingService.queryPageList(params, pageNo, pageSize));
    }
}

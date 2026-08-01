package org.jeecg.modules.scm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.modules.scm.service.ICostCalcReportService;
import org.jeecg.modules.scm.vo.CostCalcReportVo;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Tag(name = "产品成本核算报表")
@Slf4j
@RestController
@RequestMapping("/scm/costCalcReport")
public class CostCalcReportController {

    @Autowired
    private ICostCalcReportService costCalcReportService;

    @Operation(summary = "产品成本核算报表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(@RequestParam Map<String, String> params,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.OK(costCalcReportService.queryPageList(params, pageNo, pageSize));
    }

    @Operation(summary = "导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, @RequestParam Map<String, String> params) {
        List<CostCalcReportVo> list = costCalcReportService.queryList(params);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "产品成本核算报表");
        mv.addObject(NormalExcelConstants.CLASS, CostCalcReportVo.class);
        mv.addObject(NormalExcelConstants.PARAMS,
                new ExportParams("产品成本核算报表", "导出人:" + JwtUtil.getUserNameByToken(request), "产品成本核算报表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }
}

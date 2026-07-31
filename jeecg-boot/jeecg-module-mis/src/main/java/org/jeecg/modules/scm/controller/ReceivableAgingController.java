package org.jeecg.modules.scm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.modules.scm.service.IReceivableAgingService;
import org.jeecg.modules.scm.vo.ReceivableAgingVo;
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

@Tag(name = "应收账龄分析")
@Slf4j
@RestController
@RequestMapping("/scm/receivableAging")
public class ReceivableAgingController  {

    @Autowired
    private IReceivableAgingService receivableAgingService;

    @Operation(summary = "应收账龄分析-列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryList(@RequestParam Map<String, String> params) {
        // 账龄分析数据量不大，一般不分页，直接返回全部
        return Result.OK(receivableAgingService.queryList(params));
    }

    @Operation(summary = "导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, @RequestParam Map<String, String> params) {
        List<ReceivableAgingVo> list = receivableAgingService.queryList(params);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "应收账龄分析");
        mv.addObject(NormalExcelConstants.CLASS, ReceivableAgingVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("应收账龄分析", "导出人:" + JwtUtil.getUserNameByToken(request), "应收账龄分析"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }
}
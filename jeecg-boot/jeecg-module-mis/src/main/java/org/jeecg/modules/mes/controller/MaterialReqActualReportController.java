package org.jeecg.modules.mes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.mes.service.IMaterialReqActualReportService;
import org.jeecg.modules.mes.vo.MaterialReqActualReportVo;
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

@Tag(name = "物料需求与领料对比报表")
@Slf4j
@RestController
@RequestMapping("/mes/materialReqActualReport")
public class MaterialReqActualReportController {

    @Autowired
    private IMaterialReqActualReportService materialReqActualReportService;

    @Operation(summary = "物料需求与领料对比报表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(@RequestParam Map<String, String> params,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.OK(materialReqActualReportService.queryPageList(params, pageNo, pageSize));
    }

    @Operation(summary = "批次称重明细")
    @GetMapping(value = "/batchDetail")
    public Result<?> batchDetail(@RequestParam(required = false) String orderId,
                                 @RequestParam(required = false) String materialId) {
        if (orderId == null || materialId == null) {
            return Result.error("参数缺失：orderId 或 materialId 不能为空");
        }
        return Result.OK(materialReqActualReportService.getBatchDetail(orderId, materialId));
    }

    @Operation(summary = "导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, @RequestParam Map<String, String> params) {
        List<MaterialReqActualReportVo> list = materialReqActualReportService.queryPageList(params, 1, Integer.MAX_VALUE).getRecords();
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "物料需求与领料对比报表");
        mv.addObject(NormalExcelConstants.CLASS, MaterialReqActualReportVo.class);
        mv.addObject(NormalExcelConstants.PARAMS,
                new ExportParams("物料需求与领料对比报表", "导出人:" + org.jeecg.common.system.util.JwtUtil.getUserNameByToken(request), "物料需求与领料对比报表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }
}
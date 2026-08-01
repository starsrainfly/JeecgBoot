package org.jeecg.modules.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.wms.mapper.StockReportMapper;
import org.jeecg.modules.wms.service.IStockReportService;
import org.jeecg.modules.wms.vo.StockInSummaryVo;
import org.jeecg.modules.wms.vo.StockMonthlyReportVo;
import org.jeecg.modules.wms.vo.StockOutSummaryVo;
import org.jeecg.modules.wms.vo.StockWarehouseSummaryVo;
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
import java.util.ArrayList;
import java.util.List;

@Tag(name = "库存统计报表")
@RestController
@RequestMapping("/wms/stockReport")
public class StockReportController {

    @Autowired
    private IStockReportService stockReportService;
    @Autowired
    private StockReportMapper stockReportMapper;

    // ==================== 1. 入库汇总表（按供应商） ====================
    @Operation(summary = "入库汇总表-按供应商")
    @GetMapping("/inSummaryBySupplier")
    public Result<IPage<StockInSummaryVo>> inSummaryBySupplier(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "startPeriod", required = false) String startPeriod,
            @RequestParam(name = "endPeriod", required = false) String endPeriod,
            @RequestParam(name = "supplierId", required = false) String supplierId) {
        Page<StockInSummaryVo> page = new Page<>(pageNo, pageSize);
        IPage<StockInSummaryVo> pageList = stockReportService.inSummaryBySupplier(page, startPeriod, endPeriod, supplierId);
        return Result.OK(pageList);
    }

    @Operation(summary = "入库汇总表-导出")
    @GetMapping("/exportInSummary")
    public ModelAndView exportInSummary(
            @RequestParam(name = "startPeriod", required = false) String startPeriod,
            @RequestParam(name = "endPeriod", required = false) String endPeriod,
            @RequestParam(name = "supplierId", required = false) String supplierId,
            HttpServletRequest request) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<StockInSummaryVo> list = stockReportMapper.selectInSummaryBySupplierForExport(startPeriod, endPeriod, supplierId);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "入库汇总表");
        mv.addObject(NormalExcelConstants.CLASS, StockInSummaryVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("入库汇总表", "导出人:" + sysUser.getRealname(), "入库汇总"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    // ==================== 2. 出库汇总表（按客户） ====================
    @Operation(summary = "出库汇总表-按客户")
    @GetMapping("/outSummaryByCustomer")
    public Result<IPage<StockOutSummaryVo>> outSummaryByCustomer(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "startPeriod", required = false) String startPeriod,
            @RequestParam(name = "endPeriod", required = false) String endPeriod,
            @RequestParam(name = "customerId", required = false) String customerId) {
        Page<StockOutSummaryVo> page = new Page<>(pageNo, pageSize);
        IPage<StockOutSummaryVo> pageList = stockReportService.outSummaryByCustomer(page, startPeriod, endPeriod, customerId);
        return Result.OK(pageList);
    }

    @Operation(summary = "出库汇总表-导出")
    @GetMapping("/exportOutSummary")
    public ModelAndView exportOutSummary(
            @RequestParam(name = "startPeriod", required = false) String startPeriod,
            @RequestParam(name = "endPeriod", required = false) String endPeriod,
            @RequestParam(name = "customerId", required = false) String customerId,
            HttpServletRequest request) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<StockOutSummaryVo> list = stockReportMapper.selectOutSummaryByCustomerForExport(startPeriod, endPeriod, customerId);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "出库汇总表");
        mv.addObject(NormalExcelConstants.CLASS, StockOutSummaryVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("出库汇总表", "导出人:" + sysUser.getRealname(), "出库汇总"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    // ==================== 3. 收发存月报（按物料+月） ====================
    @Operation(summary = "收发存月报-按物料")
    @GetMapping("/monthlyReport")
    public Result<List<StockMonthlyReportVo>> monthlyReport(
            @RequestParam(name = "period", required = false) String period,
            @RequestParam(name = "warehouseId", required = false) String warehouseId,
            @RequestParam(name = "goodsCode", required = false) String goodsCode) {
        if (period == null || period.isEmpty()) {
            return Result.OK(new ArrayList<>());
        }
        List<StockMonthlyReportVo> list = stockReportService.monthlyReport(period, warehouseId, goodsCode);
        return Result.OK(list);
    }

    @Operation(summary = "收发存月报-导出")
    @GetMapping("/exportMonthlyReport")
    public ModelAndView exportMonthlyReport(
            @RequestParam(name = "period", required = false) String period,
            @RequestParam(name = "warehouseId", required = false) String warehouseId,
            @RequestParam(name = "goodsCode", required = false) String goodsCode,
            HttpServletRequest request) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<StockMonthlyReportVo> list = (period == null || period.isEmpty())
                ? new ArrayList<>()
                : stockReportService.monthlyReport(period, warehouseId, goodsCode);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "收发存月报");
        mv.addObject(NormalExcelConstants.CLASS, StockMonthlyReportVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("收发存月报(" + (period != null ? period : "") + ")", "导出人:" + sysUser.getRealname(), "收发存月报"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    // ==================== 4. 库存收发存汇总（按仓库+月） ====================
    @Operation(summary = "仓库收发存汇总")
    @GetMapping("/warehouseSummary")
    public Result<List<StockWarehouseSummaryVo>> warehouseSummary(
            @RequestParam(name = "period", required = false) String period) {
        if (period == null || period.isEmpty()) {
            return Result.OK(new ArrayList<>());
        }
        List<StockWarehouseSummaryVo> list = stockReportService.warehouseSummary(period);
        return Result.OK(list);
    }

    @Operation(summary = "仓库收发存汇总-导出")
    @GetMapping("/exportWarehouseSummary")
    public ModelAndView exportWarehouseSummary(
            @RequestParam(name = "period", required = false) String period,
            HttpServletRequest request) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<StockWarehouseSummaryVo> list = (period == null || period.isEmpty())
                ? new ArrayList<>()
                : stockReportService.warehouseSummary(period);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "仓库收发存汇总");
        mv.addObject(NormalExcelConstants.CLASS, StockWarehouseSummaryVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("仓库收发存汇总(" + (period != null ? period : "") + ")", "导出人:" + sysUser.getRealname(), "仓库收发存"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }
}
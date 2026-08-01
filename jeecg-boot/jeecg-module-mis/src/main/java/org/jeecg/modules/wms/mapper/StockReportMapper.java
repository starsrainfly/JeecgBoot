package org.jeecg.modules.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.vo.StockInSummaryVo;
import org.jeecg.modules.wms.vo.StockMonthlyReportVo;
import org.jeecg.modules.wms.vo.StockOutSummaryVo;
import org.jeecg.modules.wms.vo.StockWarehouseSummaryVo;

import java.util.List;
import java.util.Map;

/**
 * 库存统计报表 Mapper
 */
public interface StockReportMapper extends BaseMapper<Object> {

    // ==================== 1. 入库汇总（按供应商） ====================
    IPage<StockInSummaryVo> selectInSummaryBySupplier(
            Page<StockInSummaryVo> page,
            @Param("startPeriod") String startPeriod,
            @Param("endPeriod") String endPeriod,
            @Param("supplierId") String supplierId);

    // 导出用：不分页查全部
    List<StockInSummaryVo> selectInSummaryBySupplierForExport(
            @Param("startPeriod") String startPeriod,
            @Param("endPeriod") String endPeriod,
            @Param("supplierId") String supplierId);

    // ==================== 2. 出库汇总（按客户） ====================
    IPage<StockOutSummaryVo> selectOutSummaryByCustomer(
            Page<StockOutSummaryVo> page,
            @Param("startPeriod") String startPeriod,
            @Param("endPeriod") String endPeriod,
            @Param("customerId") String customerId);

    // 导出用：不分页查全部
    List<StockOutSummaryVo> selectOutSummaryByCustomerForExport(
            @Param("startPeriod") String startPeriod,
            @Param("endPeriod") String endPeriod,
            @Param("customerId") String customerId);

    // ==================== 3. 收发存月报（按物料） ====================
    List<StockMonthlyReportVo> selectGoodsBaseList(
            @Param("warehouseId") String warehouseId,
            @Param("goodsCode") String goodsCode);

    List<Map<String, Object>> selectInByPeriod(
            @Param("period") String period,
            @Param("warehouseId") String warehouseId,
            @Param("goodsCode") String goodsCode);

    List<Map<String, Object>> selectOutByPeriod(
            @Param("period") String period,
            @Param("warehouseId") String warehouseId,
            @Param("goodsCode") String goodsCode);

    List<Map<String, Object>> selectInAfterPeriod(
            @Param("period") String period,
            @Param("warehouseId") String warehouseId,
            @Param("goodsCode") String goodsCode);

    List<Map<String, Object>> selectOutAfterPeriod(
            @Param("period") String period,
            @Param("warehouseId") String warehouseId,
            @Param("goodsCode") String goodsCode);

    // 导出用：月报不分页
    List<StockMonthlyReportVo> selectGoodsBaseListForExport(
            @Param("warehouseId") String warehouseId,
            @Param("goodsCode") String goodsCode);

    // ==================== 4. 仓库收发存汇总 ====================
    List<StockWarehouseSummaryVo> selectWarehouseBaseList();

    List<Map<String, Object>> selectInByPeriodGroupByWarehouse(@Param("period") String period);

    List<Map<String, Object>> selectOutByPeriodGroupByWarehouse(@Param("period") String period);

    List<Map<String, Object>> selectInAfterPeriodGroupByWarehouse(@Param("period") String period);

    List<Map<String, Object>> selectOutAfterPeriodGroupByWarehouse(@Param("period") String period);

    // 导出用：仓库汇总不分页
    List<StockWarehouseSummaryVo> selectWarehouseBaseListForExport();
}
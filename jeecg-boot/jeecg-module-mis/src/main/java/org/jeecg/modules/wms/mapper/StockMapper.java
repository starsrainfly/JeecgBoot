package org.jeecg.modules.wms.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.entity.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.wms.vo.StockSummaryVo;
import org.jeecg.modules.wms.vo.WarehouseDashboardVo;

/**
 * @Description: 库存记录表
 * @Author: jeecg-boot
 * @Date:   2026-03-31
 * @Version: V1.0
 */
public interface StockMapper extends BaseMapper<Stock> {

    /**
     * 批量插入库存记录
     * @param stockList 库存记录列表
     * @return 插入成功的记录数
     */
    int batchInsert(@Param("list") List<Stock> stockList);
    /**
     * 查询汇总分页
     */
    IPage<StockSummaryVo> querySummaryPage(@Param("page") Page<StockSummaryVo> page, @Param("stock") Stock stock);

    /**
     * FIFO查询可用库存
     * @param warehouseId 仓库ID
     * @param goodsId 物料ID
     * @param batchNo 批次号（可选）
     * @return 按入库时间排序的库存列表
     */
    List<Stock> selectAvailableStockByGoods(
            @Param("warehouseId") String warehouseId,
            @Param("goodsId") String goodsId,
            @Param("batchNo") String batchNo
    );

    /**
     * 增加锁定数量
     * @param id 库存ID
     * @param lockQty 锁定数量
     * @return 影响行数
     */
    int increaseLockQty(
            @Param("id") String id,
            @Param("lockQty") BigDecimal lockQty
    );

    /**
     * 确认出库：扣减库存并释放锁定
     * @param id 库存ID
     * @param qty 出库数量
     * @return 影响行数
     */
    int confirmDeduct(
            @Param("id") String id,
            @Param("qty") BigDecimal qty
    );

    /**
     * 释放锁定数量
     * @param id 库存ID
     * @param lockQty 释放数量
     * @return 影响行数
     */
    int releaseLockQty(
            @Param("id") String id,
            @Param("lockQty") BigDecimal lockQty
    );

    /**
     * 直接扣减库存（不检查锁定数量，用于扫码发货直接出库）
     * @param id 库存ID
     * @param qty 扣减数量
     * @return 影响行数
     */
    int directDeduct(
            @Param("id") String id,
            @Param("qty") BigDecimal qty
    );

    /**
     * 盘盈：直接增加库存
     * @param id
     * @param qty
     * @return
     */
    int increaseQty(@Param("id") String id, @Param("qty") BigDecimal qty);

    /**
     * 盘亏：直接扣减库存（检查实际库存，不检查锁定）
     * @param id
     * @param qty
     * @return
     */
    int decreaseQty(@Param("id") String id, @Param("qty") BigDecimal qty);
    /**
     * 查询库存占用情况
     */
    Map<String, Object> selectStockOccupancy(
            @Param("warehouseId") String warehouseId,
            @Param("goodsId") String goodsId
    );

    Long selectWarningCount();

    BigDecimal selectTotalLockedQty();

    List<WarehouseDashboardVo.WarningMaterial> selectWarningTop5();

    List<WarehouseDashboardVo.ExpiryAlertItem> selectExpiryAlertList();

    long selectPendingInAuditCount();
    long selectPendingOutAuditCount();

    /**
     *
     * @param warehouseIds
     * @param goodsId
     * @param batchNo
     * @return
     */
    List<Stock> selectAvailableStockByGoodsMultiWarehouse(
            @Param("warehouseIds") List<String> warehouseIds,
            @Param("goodsId") String goodsId,
            @Param("batchNo") String batchNo);

}

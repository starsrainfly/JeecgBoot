package org.jeecg.modules.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.Stock;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.vo.StockSummaryVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: 库存记录表
 * @Author: jeecg-boot
 * @Date:   2026-03-31
 * @Version: V1.0
 */
public interface IStockService extends IService<Stock> {

    int batchSaveStock(List<Stock> stockList);

    /**
     * 分页查询库存汇总
     */
    IPage<StockSummaryVo> querySummaryPage(Page<StockSummaryVo> page, Stock stock);

    /**
     * FIFO查询可用库存
     */
    List<Stock> selectAvailableStockByGoods(String warehouseId, String goodsId, String batchNo);

    /**
     * 增加锁定数量
     */
    boolean increaseLockQty(String id, BigDecimal lockQty);

    /**
     * 确认出库：扣减库存并释放锁定
     */
    boolean confirmDeduct(String id, BigDecimal qty);

    /**
     * 释放锁定数量
     */
    boolean releaseLockQty(String id, BigDecimal lockQty);

    /**
     * 查询库存占用情况
     */
    Map<String, Object> selectStockOccupancy(String warehouseId, String goodsId);
}

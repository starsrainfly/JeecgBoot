package org.jeecg.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.mapper.StockMapper;
import org.jeecg.modules.wms.service.IStockService;
import org.jeecg.modules.wms.vo.StockSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
/**
 * @Description: 库存记录表
 * @Author: jeecg-boot
 * @Date:   2026-03-31
 * @Version: V1.0
 */
@Slf4j
@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

    @Autowired
    private StockMapper stockMapper;

    @Override
    public int batchSaveStock(List<Stock> stockList) {
        return stockMapper.batchInsert(stockList);
    }

    @Override
    public IPage<StockSummaryVo> querySummaryPage(Page<StockSummaryVo> page, Stock stock) {
        return stockMapper.querySummaryPage(page, stock);
    }

    @Override
    public List<Stock> selectAvailableStockByGoods(String warehouseId, String goodsId, String batchNo) {
        return stockMapper.selectAvailableStockByGoods(warehouseId, goodsId, batchNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean increaseLockQty(String id, BigDecimal lockQty) {
        int rows = stockMapper.increaseLockQty(id, lockQty);
        if (rows == 0) {
            throw new RuntimeException("库存锁定失败，可能可用库存不足");
        }
        log.info("锁定库存：stockId={}, 锁定数量={}", id, lockQty);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmDeduct(String id, BigDecimal qty) {
        int rows = stockMapper.confirmDeduct(id, qty);
        if (rows == 0) {
            throw new RuntimeException("库存扣减失败，可能锁定已释放");
        }
        log.info("确认出库：stockId={}, 扣减数量={}", id, qty);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseLockQty(String id, BigDecimal lockQty) {
        int rows = stockMapper.releaseLockQty(id, lockQty);
        if (rows > 0) {
            log.info("释放锁定：stockId={}, 释放数量={}", id, lockQty);
        }
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean directDeduct(String id, BigDecimal qty) {
        int rows = stockMapper.directDeduct(id, qty);
        if (rows == 0) {
            throw new RuntimeException("库存直接扣减失败，可能可用库存不足");
        }
        log.info("直接扣减库存：stockId={}, 扣减数量={}", id, qty);
        return true;
    }

    @Override
    public Map<String, Object> selectStockOccupancy(String warehouseId, String goodsId) {
        return stockMapper.selectStockOccupancy(warehouseId, goodsId);
    }

    @Override
    public boolean increaseQty(String id, BigDecimal qty) {
        int rows = stockMapper.increaseQty(id, qty);
        if(rows == 0) {
            throw new RuntimeException("盘盈直接更新库存失败");
        }
        log.info("直接增加 更新库存 ：stockId={}, 增加数量={}", id, qty);
        return true;
    }

    @Override
    public boolean decreaseQty(String id, BigDecimal qty) {
        int rows = stockMapper.decreaseQty(id, qty);
        if(rows == 0) {
            throw new RuntimeException("盘亏 库存直接扣减失败，可能可用库存不足");
        }
        log.info("盘亏 直接扣减库存：stockId={}, 扣减数量={}", id, qty);
        return true;
    }

    @Override
    public List<Stock> selectAvailableStockByGoodsMultiWarehouse(List<String> warehouseIds, String goodsId, String batchNo) {
        return stockMapper.selectAvailableStockByGoodsMultiWarehouse(warehouseIds, goodsId, batchNo);
    }
}

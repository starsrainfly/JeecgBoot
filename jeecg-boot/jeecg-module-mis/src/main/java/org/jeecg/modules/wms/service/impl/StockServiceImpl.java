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

import java.util.List;

/**
 * @Description: 库存记录表
 * @Author: jeecg-boot
 * @Date:   2026-03-31
 * @Version: V1.0
 */
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
}

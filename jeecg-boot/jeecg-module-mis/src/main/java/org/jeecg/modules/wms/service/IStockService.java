package org.jeecg.modules.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.Stock;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.vo.StockSummaryVo;

import java.util.List;

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
}

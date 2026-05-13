package org.jeecg.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.entity.StockMove;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * @Description: 移库记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
public interface IStockMoveService extends IService<StockMove> {
    /**
     * 查询可移库库存
     */
    IPage<Stock> queryMovePendingList(Page<Stock> page, Stock stock);

    /**
     * 执行移库（支持部分移库拆分库存）
     */
    void doMove(StockMove moveRecord, BigDecimal moveQty);

    /**
     * 分页查询移库记录
     */
    IPage<StockMove> queryPageList(Page<StockMove> page, StockMove moveRecord);
}

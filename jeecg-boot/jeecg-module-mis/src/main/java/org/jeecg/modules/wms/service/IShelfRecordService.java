package org.jeecg.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.ShelfRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.entity.Stock;

import java.math.BigDecimal;

/**
 * @Description: 上架记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
public interface IShelfRecordService extends IService<ShelfRecord> {
    /**
     * 查询待上架库存列表
     */
    IPage<Stock> queryPendingList(Page<Stock> page, Stock stock);

    /**
     * 执行上架
     */
    void doShelf(ShelfRecord shelfRecord);
}

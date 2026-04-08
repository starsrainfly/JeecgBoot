package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.Stock;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 库存记录表
 * @Author: jeecg-boot
 * @Date:   2026-03-31
 * @Version: V1.0
 */
public interface IStockService extends IService<Stock> {

    int batchSaveStock(List<Stock> stockList);
}

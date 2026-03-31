package org.jeecg.modules.wms.service.impl;

import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.mapper.StockMapper;
import org.jeecg.modules.wms.service.IStockService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 库存记录表
 * @Author: jeecg-boot
 * @Date:   2026-03-31
 * @Version: V1.0
 */
@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

}

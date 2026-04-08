package org.jeecg.modules.wms.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.entity.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.wms.vo.StockSummaryVo;

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

    IPage<StockSummaryVo> querySummaryPage(@Param("page") Page<StockSummaryVo> page, @Param("stock") Stock stock);
}

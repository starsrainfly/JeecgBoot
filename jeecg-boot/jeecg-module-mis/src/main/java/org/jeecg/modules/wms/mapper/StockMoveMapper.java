package org.jeecg.modules.wms.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.entity.StockMove;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 移库记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
public interface StockMoveMapper extends BaseMapper<StockMove> {

    IPage<Stock> queryMovePendingList(Page<Stock> page, @Param("stock") Stock stock);
    /**
     * 查询目标位置是否已存在相同库存记录
     */
    Stock checkTargetStockExists(@Param("goodsId") String goodsId,
                                 @Param("batchNo") String batchNo,
                                 @Param("warehouseId") String warehouseId,
                                 @Param("areaId") String areaId,
                                 @Param("shelfId") String shelfId,
                                 @Param("locationId") String locationId);
}

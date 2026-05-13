package org.jeecg.modules.wms.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.entity.ShelfRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.wms.entity.Stock;

/**
 * @Description: 上架记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
public interface ShelfRecordMapper extends BaseMapper<ShelfRecord> {

    /**
     * 查询待上架库存列表
     */
    IPage<Stock> queryPendingList(@Param("page") Page<Stock> page,
                                  @Param("stock") Stock stock
                                 );
}

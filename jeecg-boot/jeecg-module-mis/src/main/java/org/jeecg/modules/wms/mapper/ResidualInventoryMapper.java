package org.jeecg.modules.wms.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.entity.ResidualInventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 余料库表
 * @Author: jeecg-boot
 * @Date:   2026-04-11
 * @Version: V1.0
 */
public interface ResidualInventoryMapper extends BaseMapper<ResidualInventory> {
    /**
     * 查询可用余料总量（SUM）
     */
    BigDecimal sumAvailableQty(@Param("materialId") String materialId,
                               @Param("warehouseId") String warehouseId);

    /**
     * FIFO查询可用余料列表（用于锁定）
     */
    List<ResidualInventory> selectAvailableByMaterial(@Param("materialId") String materialId,
                                                      @Param("warehouseId") String warehouseId);

    /**
     * LIFO查询被锁定的余料（用于释放锁定）
     */
    List<ResidualInventory> selectLockedByMaterial(@Param("materialId") String materialId);

    /**
     * 根据出库明细ID查询
     */
    ResidualInventory selectByStockOutDetailId(@Param("stockOutDetailId") String stockOutDetailId);

    /**
     * 增加锁定数量（原子操作）
     */
    int increaseLockQty(@Param("id") String id, @Param("lockQty") BigDecimal lockQty);

    /**
     * 减少锁定数量（原子操作）
     */
    int decreaseLockQty(@Param("id") String id, @Param("unlockQty") BigDecimal unlockQty);

    /**
     * 扣减数量（实际出库，原子操作）
     */
    int deductQty(@Param("id") String id, @Param("deductQty") BigDecimal deductQty);
}

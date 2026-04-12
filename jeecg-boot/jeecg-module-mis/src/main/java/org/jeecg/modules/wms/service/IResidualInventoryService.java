package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.ResidualInventory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * @Description: 余料库表
 * @Author: jeecg-boot
 * @Date:   2026-04-11
 * @Version: V1.0
 */
public interface IResidualInventoryService extends IService<ResidualInventory> {

    /**
     * 获取物料可用余料数量总和
     */
    BigDecimal getAvailableQty(String materialId, String warehouseId);

    /**
     * FIFO锁定余料
     */
    void lockQty(String materialId, BigDecimal qty);

    /**
     * LIFO释放锁定
     */
    void unlockQty(String materialId, BigDecimal qty);

    /**
     * 扣减余料（实际出库）
     */
    void deductQty(String residualId, BigDecimal qty);

    /**
     * 创建余料记录
     */
    void createResidual(ResidualInventory residual);

    /**
     * 根据出库明细ID删除余料记录（回滚用）
     */
    void deleteByStockOutDetailId(String stockOutDetailId);

    /**
     * 获取可用余料用于锁定（FIFO第一条）
     */
    ResidualInventory getOrCreateForLock(String materialId, String warehouseId);
}

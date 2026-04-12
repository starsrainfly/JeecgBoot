package org.jeecg.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.wms.entity.ResidualInventory;
import org.jeecg.modules.wms.mapper.ResidualInventoryMapper;
import org.jeecg.modules.wms.service.IResidualInventoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 余料库表
 * @Author: jeecg-boot
 * @Date:   2026-04-11
 * @Version: V1.0
 */
@Slf4j
@Service
public class ResidualInventoryServiceImpl extends ServiceImpl<ResidualInventoryMapper, ResidualInventory> implements IResidualInventoryService {

    @Override
    public void createResidual(ResidualInventory residualInventory) {
        this.save(residualInventory);
    }

    @Override
    public BigDecimal getAvailableQty(String materialId, String warehouseId) {
        return baseMapper.sumAvailableQty(materialId, warehouseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockQty(String materialId, BigDecimal qty) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal remaining = qty;

        // FIFO锁定
        List<ResidualInventory> list = baseMapper.selectAvailableByMaterial(materialId, null);

        for (ResidualInventory residual : list) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            // 【直接计算】可用数量 = qty - locked_qty
            BigDecimal available = residual.getQty().subtract(residual.getLockedQty());
            BigDecimal lock = available.min(remaining);

            int updated = baseMapper.increaseLockQty(residual.getId(), lock);
            if (updated <= 0) {
                throw new RuntimeException("余料锁定失败，可能被并发修改: id=" + residual.getId());
            }

            remaining = remaining.subtract(lock);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("余料库可用数量不足，剩余需锁定：" + remaining);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockQty(String materialId, BigDecimal qty) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal remaining = qty;

        // LIFO释放
        List<ResidualInventory> list = baseMapper.selectLockedByMaterial(materialId);

        for (ResidualInventory residual : list) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal locked = residual.getLockedQty();
            BigDecimal unlock = locked.min(remaining);

            int updated = baseMapper.decreaseLockQty(residual.getId(), unlock);
            if (updated <= 0) {
                throw new RuntimeException("余料释放锁定失败: id=" + residual.getId());
            }

            remaining = remaining.subtract(unlock);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("余料库锁定数量不足，剩余需释放：" + remaining);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductQty(String residualId, BigDecimal qty) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) return;

        int updated = baseMapper.deductQty(residualId, qty);
        if (updated <= 0) {
            throw new RuntimeException("余料扣减失败，id=" + residualId + ", qty=" + qty);
        }

        log.info("余料扣减成功: residualId={}, qty={}", residualId, qty);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByStockOutDetailId(String stockOutDetailId) {
        ResidualInventory residual = baseMapper.selectByStockOutDetailId(stockOutDetailId);
        if (residual != null) {
            // 逻辑删除
            residual.setDelFlag("1");
            updateById(residual);
            log.info("删除余料记录: stockOutDetailId={}, residualId={}", stockOutDetailId, residual.getId());
        }
    }

    @Override
    public ResidualInventory getOrCreateForLock(String materialId, String warehouseId) {
        List<ResidualInventory> list = baseMapper.selectAvailableByMaterial(materialId, warehouseId);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ResidualInventory> lockQtyAndReturnList(String materialId, BigDecimal qty) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) return new ArrayList<>();

        BigDecimal remaining = qty;
        List<ResidualInventory> lockedList = new ArrayList<>();

        // FIFO锁定
        List<ResidualInventory> list = baseMapper.selectAvailableByMaterial(materialId, null);

        for (ResidualInventory residual : list) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal available = residual.getQty().subtract(residual.getLockedQty());
            BigDecimal lock = available.min(remaining);

            int updated = baseMapper.increaseLockQty(residual.getId(), lock);
            if (updated <= 0) {
                throw new RuntimeException("余料锁定失败: id=" + residual.getId());
            }

            // 记录锁定的数量和余料记录
            ResidualInventory locked = new ResidualInventory();
            BeanUtils.copyProperties(residual, locked);
            locked.setLockedQty(lock); // 本次锁定的数量
            lockedList.add(locked);

            remaining = remaining.subtract(lock);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("余料库可用数量不足: " + remaining);
        }

        return lockedList;
    }


}

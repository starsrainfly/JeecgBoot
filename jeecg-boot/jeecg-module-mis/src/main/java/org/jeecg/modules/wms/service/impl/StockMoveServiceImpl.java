package org.jeecg.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.entity.StockMove;
import org.jeecg.modules.wms.entity.Warehouse;
import org.jeecg.modules.wms.mapper.StockMapper;
import org.jeecg.modules.wms.mapper.StockMoveMapper;
import org.jeecg.modules.wms.mapper.WarehouseMapper;
import org.jeecg.modules.wms.service.IStockMoveService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: 移库记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
@Service
public class StockMoveServiceImpl extends ServiceImpl<StockMoveMapper, StockMove> implements IStockMoveService {

    @Autowired
    private StockMoveMapper stockMoveMapper;

    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private ISerialNoService serialNoService;

    @Override
    public IPage<Stock> queryMovePendingList(Page<Stock> page, Stock stock) {
        return stockMoveMapper.queryMovePendingList(page, stock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doMove(StockMove moveRecord) {
        BigDecimal moveQty = moveRecord.getMoveQty();
// 1. 校验原库存
        // ==================== 1. 基础校验 ====================
        if (moveRecord.getFromStockId() == null || moveRecord.getFromStockId().trim().isEmpty()) {
            throw new JeecgBootException("原库存ID不能为空");
        }
        if (moveRecord.getToWarehouseId() == null || moveRecord.getToWarehouseId().trim().isEmpty()) {
            throw new JeecgBootException("目标仓库不能为空");
        }
        if (moveRecord.getToAreaId() == null || moveRecord.getToAreaId().trim().isEmpty()) {
            throw new JeecgBootException("目标区域不能为空");
        }
        if (moveQty == null || moveQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new JeecgBootException("移库数量必须大于0");
        }

        // ==================== 2. 查询并校验原库存 ====================
        Stock sourceStock = stockMapper.selectById(moveRecord.getFromStockId());
        if (sourceStock == null) {
            throw new JeecgBootException("原库存记录不存在");
        }
        if (sourceStock.getQuantity().compareTo(moveQty) < 0) {
            throw new JeecgBootException("移库数量不能大于库存数量，当前库存：" + sourceStock.getQuantity());
        }
        // 校验是否有位置信息（已上架才能移库）
        boolean hasLocation = (sourceStock.getShelfId() != null && !sourceStock.getShelfId().trim().isEmpty())
                || (sourceStock.getAreaId() != null && !sourceStock.getAreaId().trim().isEmpty());
        if (!hasLocation) {
            throw new JeecgBootException("该库存未上架，不能移库");
        }
        // 校验是否被锁定
        if (sourceStock.getLockedQty() != null && sourceStock.getLockedQty().compareTo(BigDecimal.ZERO) > 0) {
            throw new JeecgBootException("该库存已被锁定（锁定数量：" + sourceStock.getLockedQty() + "），不能移库");
        }

        // ==================== 3. 校验目标仓库级别与必填字段 ====================
        Warehouse targetWarehouse = warehouseMapper.selectById(moveRecord.getToWarehouseId());
        if (targetWarehouse == null) {
            throw new JeecgBootException("目标仓库不存在");
        }
        String locationLevel = targetWarehouse.getLocationLevel();

        if ("SHELF".equals(locationLevel)) {
            if (moveRecord.getToShelfId() == null || moveRecord.getToShelfId().trim().isEmpty()) {
                throw new JeecgBootException("目标仓库启用货架管理，目标货架不能为空");
            }
            moveRecord.setToLocationId(null);
        } else if ("LOCATION".equals(locationLevel)) {
            if (moveRecord.getToShelfId() == null || moveRecord.getToShelfId().trim().isEmpty()) {
                throw new JeecgBootException("目标仓库启用货位管理，目标货架不能为空");
            }
            if (moveRecord.getToLocationId() == null || moveRecord.getToLocationId().trim().isEmpty()) {
                throw new JeecgBootException("目标仓库启用货位管理，目标货位不能为空");
            }
        } else {
            // AREA级别或WAREHOUSE级别
            moveRecord.setToShelfId(null);
            moveRecord.setToLocationId(null);
        }

        // ==================== 4. 校验目标位置不能与原位置完全相同 ====================
        boolean sameWarehouse = isSame(sourceStock.getWarehouseId(), moveRecord.getToWarehouseId());
        boolean sameArea = isSame(sourceStock.getAreaId(), moveRecord.getToAreaId());
        boolean sameShelf = isSame(sourceStock.getShelfId(), moveRecord.getToShelfId());
        boolean sameLocation = isSame(sourceStock.getLocationId(), moveRecord.getToLocationId());

        if (sameWarehouse && sameArea && sameShelf && sameLocation) {
            throw new JeecgBootException("目标位置不能与原位置相同");
        }

        // ==================== 5. 填充移库记录基础信息 ====================
        String serialNo = serialNoService.generateSerialNo(SerialNoPrefixEnum.MOVE_STOCK.getPrefix());
        moveRecord.setMoveNo(serialNo);

        // 复制物品信息
        moveRecord.setGoodsId(sourceStock.getGoodsId());
        moveRecord.setGoodsType(sourceStock.getGoodsType());
        moveRecord.setIsProduct(sourceStock.getIsProduct());
        moveRecord.setGoodsCode(sourceStock.getGoodsCode());
        moveRecord.setGoodsName(sourceStock.getGoodsName());
        moveRecord.setGoodsSpec(sourceStock.getGoodsSpec());
        moveRecord.setGoodsColor(sourceStock.getGoodsColor());
        moveRecord.setUnit(sourceStock.getUnit());
        moveRecord.setBatchNo(sourceStock.getBatchNo());

        // 原库位信息
        moveRecord.setFromWarehouseId(sourceStock.getWarehouseId());
        moveRecord.setFromAreaId(sourceStock.getAreaId());
        moveRecord.setFromShelfId(sourceStock.getShelfId());
        moveRecord.setFromLocationId(sourceStock.getLocationId());

        // 移库数量
        moveRecord.setMoveQty(moveQty);

        // 操作人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser != null) {
            moveRecord.setOperatorId(sysUser.getId());
            moveRecord.setOperatorName(sysUser.getRealname());
        }
        moveRecord.setMoveTime(new Date());
        moveRecord.setCreateTime(new Date());
        moveRecord.setDelFlag("0");

        // ==================== 6. 执行库存处理（全部移库 vs 部分移库） ====================
//        if (sourceStock.getQuantity().compareTo(moveQty) == 0) {
//            // ===== 全部移库 =====
//            handleFullMove(sourceStock, moveRecord);
//        } else {
//            // ===== 部分移库 =====
//            handlePartialMove(sourceStock, moveRecord, moveQty);
//        }
        if (sourceStock.getQuantity().compareTo(moveQty) == 0) {
            // ===== 全部移库：直接更新原记录库位到新位置 =====
            sourceStock.setWarehouseId(moveRecord.getToWarehouseId());
            sourceStock.setAreaId(moveRecord.getToAreaId());
            sourceStock.setShelfId(moveRecord.getToShelfId());
            sourceStock.setLocationId(moveRecord.getToLocationId());
            sourceStock.setUpdateTime(new Date());
            stockMapper.updateById(sourceStock);

            moveRecord.setToStockId(sourceStock.getId());
        } else {
            // ===== 部分移库：拆分库存（创建新记录，不合并） =====
            // 减少原库存数量
            sourceStock.setQuantity(sourceStock.getQuantity().subtract(moveQty));
            sourceStock.setUpdateTime(new Date());
            stockMapper.updateById(sourceStock);

            // 创建新库存记录（新位置，独立明细）
            Stock newStock = new Stock();
            BeanUtils.copyProperties(sourceStock, newStock);
            newStock.setId(null);
            newStock.setWarehouseId(moveRecord.getToWarehouseId());
            newStock.setAreaId(moveRecord.getToAreaId());
            newStock.setShelfId(moveRecord.getToShelfId());
            newStock.setLocationId(moveRecord.getToLocationId());
            newStock.setQuantity(moveQty);
            newStock.setOriginalQty(moveQty);
            newStock.setLockedQty(BigDecimal.ZERO);
            newStock.setCreateTime(new Date());
            newStock.setUpdateTime(new Date());
            stockMapper.insert(newStock);

            moveRecord.setToStockId(newStock.getId());
        }
        // 保存移库记录
        this.save(moveRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMove(List<StockMove> records) {
        if (records == null || records.isEmpty()) {
            throw new JeecgBootException("移库记录不能为空");
        }
        for (StockMove record : records) {
            doMove(record);
        }
    }


    /**
     * 全部移库：直接更新原记录库位
     */
    private void handleFullMove(Stock sourceStock, StockMove moveRecord) {
        // 查询目标位置是否已有相同库存（尝试合并）
        Stock existsStock = stockMoveMapper.checkTargetStockExists(
                sourceStock.getGoodsId(),
                sourceStock.getBatchNo(),
                moveRecord.getToWarehouseId(),
                moveRecord.getToAreaId(),
                moveRecord.getToShelfId(),
                moveRecord.getToLocationId()
        );

        if (existsStock != null && !existsStock.getId().equals(sourceStock.getId())) {
            // 目标位置已有相同库存，合并数量
            existsStock.setQuantity(existsStock.getQuantity().add(moveRecord.getMoveQty()));
            existsStock.setUpdateTime(new Date());
            stockMapper.updateById(existsStock);

            // 删除原库存记录
            stockMapper.deleteById(sourceStock.getId());

            moveRecord.setToStockId(existsStock.getId());
        } else {
            // 直接更新原记录库位
            sourceStock.setWarehouseId(moveRecord.getToWarehouseId());
            sourceStock.setAreaId(moveRecord.getToAreaId());
            sourceStock.setShelfId(moveRecord.getToShelfId());
            sourceStock.setLocationId(moveRecord.getToLocationId());
            sourceStock.setUpdateTime(new Date());
            stockMapper.updateById(sourceStock);

            moveRecord.setToStockId(sourceStock.getId());
        }
    }

    /**
     * 部分移库：拆分库存
     */
    private void handlePartialMove(Stock sourceStock, StockMove moveRecord, BigDecimal moveQty) {
        // 减少原库存数量
        sourceStock.setQuantity(sourceStock.getQuantity().subtract(moveQty));
        sourceStock.setUpdateTime(new Date());
        stockMapper.updateById(sourceStock);

        // 查询目标位置是否已有相同库存（尝试合并）
        Stock existsStock = stockMoveMapper.checkTargetStockExists(
                sourceStock.getGoodsId(),
                sourceStock.getBatchNo(),
                moveRecord.getToWarehouseId(),
                moveRecord.getToAreaId(),
                moveRecord.getToShelfId(),
                moveRecord.getToLocationId()
        );

        if (existsStock != null) {
            // 合并到已有库存
            existsStock.setQuantity(existsStock.getQuantity().add(moveQty));
            existsStock.setUpdateTime(new Date());
            stockMapper.updateById(existsStock);
            moveRecord.setToStockId(existsStock.getId());
        } else {
            // 创建新库存记录
            Stock newStock = new Stock();
            BeanUtils.copyProperties(sourceStock, newStock);
            newStock.setId(null);
            newStock.setWarehouseId(moveRecord.getToWarehouseId());
            newStock.setAreaId(moveRecord.getToAreaId());
            newStock.setShelfId(moveRecord.getToShelfId());
            newStock.setLocationId(moveRecord.getToLocationId());
            newStock.setQuantity(moveQty);
            newStock.setOriginalQty(moveQty);
            newStock.setLockedQty(BigDecimal.ZERO);
            newStock.setCreateTime(new Date());
            newStock.setUpdateTime(new Date());
            stockMapper.insert(newStock);
            moveRecord.setToStockId(newStock.getId());
        }
    }

    /**
     * 辅助方法：判断两个字符串是否相同（处理null）
     */
    private boolean isSame(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}

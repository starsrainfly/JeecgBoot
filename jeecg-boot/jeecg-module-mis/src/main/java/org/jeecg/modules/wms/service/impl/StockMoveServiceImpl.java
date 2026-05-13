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

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 移库记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
@Service
public class StockMoveServiceImpl extends ServiceImpl<StockMoveMapper, StockMove> implements IStockMoveService {

    @Autowired
    private StockMoveMapper moveRecordMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private ISerialNoService serialNoService;

    @Override
    public IPage<Stock> queryMovePendingList(Page<Stock> page, Stock stock) {
        return moveRecordMapper.queryMovePendingList(page, stock);
    }

    @Override
    public void doMove(StockMove moveRecord, BigDecimal moveQty) {
// 1. 校验原库存
        Stock sourceStock = stockMapper.selectById(moveRecord.getFromStockId());
        if (sourceStock == null) {
            throw new JeecgBootException("库存记录不存在");
        }

        // 校验是否有位置信息（已上架才能移库）
        if ((sourceStock.getShelfId() == null || sourceStock.getShelfId().trim().isEmpty())
                && (sourceStock.getAreaId() == null || sourceStock.getAreaId().trim().isEmpty())) {
            throw new JeecgBootException("该库存未上架，不能移库");
        }

        if (moveQty == null || moveQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new JeecgBootException("移库数量必须大于0");
        }
        if (sourceStock.getQuantity().compareTo(moveQty) < 0) {
            throw new JeecgBootException("移库数量不能大于库存数量，当前库存：" + sourceStock.getQuantity());
        }

        // 2. 查询目标仓库级别
        Warehouse targetWarehouse = warehouseMapper.selectById(moveRecord.getToWarehouseId());
        if (targetWarehouse == null) {
            throw new JeecgBootException("目标仓库不存在");
        }
        String locationLevel = targetWarehouse.getLocationLevel();

        // 3. 根据级别校验必填字段
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
        }

        // 4. 填充移库记录
        String serialNo = serialNoService.generateSerialNo(SerialNoPrefixEnum.MOVE_STOCK.getPrefix());
        moveRecord.setMoveNo(serialNo);

        // 物品信息（完整复制）
        moveRecord.setGoodsId(sourceStock.getGoodsId());
        moveRecord.setGoodsType(sourceStock.getGoodsType());
        moveRecord.setIsProduct(sourceStock.getIsProduct());
        moveRecord.setGoodsCode(sourceStock.getGoodsCode());
        moveRecord.setGoodsName(sourceStock.getGoodsName());
        moveRecord.setGoodsSpec(sourceStock.getGoodsSpec());
        moveRecord.setGoodsColor(sourceStock.getGoodsColor());
        moveRecord.setUnit(sourceStock.getUnit());
        moveRecord.setBatchNo(sourceStock.getBatchNo());

        // 原库位
        moveRecord.setFromWarehouseId(sourceStock.getWarehouseId());
        moveRecord.setFromAreaId(sourceStock.getAreaId());
        moveRecord.setFromShelfId(sourceStock.getShelfId());
        moveRecord.setFromLocationId(sourceStock.getLocationId());

        // 移库数量
        moveRecord.setMoveQty(moveQty);

        // 操作人
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(sysUser != null){
            moveRecord.setOperatorId(sysUser.getId());
            moveRecord.setOperatorName(sysUser.getRealname());
        }

        moveRecord.setMoveTime(new Date());
        moveRecord.setCreateTime(new Date());

        this.save(moveRecord);

        // 5. 处理库存
        if (sourceStock.getQuantity().compareTo(moveQty) == 0) {
            // 全部移库：直接更新原记录库位
            sourceStock.setWarehouseId(moveRecord.getToWarehouseId());
            sourceStock.setAreaId(moveRecord.getToAreaId());
            sourceStock.setShelfId(moveRecord.getToShelfId());
            sourceStock.setLocationId(moveRecord.getToLocationId());
            sourceStock.setUpdateTime(new Date());
            stockMapper.updateById(sourceStock);
            moveRecord.setToStockId(sourceStock.getId());
        } else {
            // 部分移库：拆分库存
            sourceStock.setQuantity(sourceStock.getQuantity().subtract(moveQty));
            sourceStock.setUpdateTime(new Date());
            stockMapper.updateById(sourceStock);

            // 创建新库存记录（移库后的）
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

        this.updateById(moveRecord);
    }

    @Override
    public IPage<StockMove> queryPageList(Page<StockMove> page, StockMove moveRecord) {
        return null;
    }
}

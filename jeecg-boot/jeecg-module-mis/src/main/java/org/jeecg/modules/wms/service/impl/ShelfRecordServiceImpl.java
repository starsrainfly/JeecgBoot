package org.jeecg.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.wms.entity.*;
import org.jeecg.modules.wms.mapper.ShelfRecordMapper;
import org.jeecg.modules.wms.mapper.StockMapper;
import org.jeecg.modules.wms.mapper.WarehouseMapper;
import org.jeecg.modules.wms.service.IShelfRecordService;
import org.jeecg.modules.wms.service.IStockInDetailService;
import org.jeecg.modules.wms.service.IStockInService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 上架记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-12
 * @Version: V1.0
 */
@Service
public class ShelfRecordServiceImpl extends ServiceImpl<ShelfRecordMapper, ShelfRecord> implements IShelfRecordService {

    @Autowired
    private ShelfRecordMapper shelfRecordMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private ISerialNoService serialNoService;
    @Autowired
    private IStockInService stockInService;
    @Autowired
    private IStockInDetailService stockInDetailService;

    @Override
    public IPage<Stock> queryPendingList(Page<Stock> page, Stock stock) {
        return shelfRecordMapper.queryPendingList(page, stock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doShelf(ShelfRecord shelfRecord) {
        // 1. 校验原库存
        Stock sourceStock = stockMapper.selectById(shelfRecord.getStockId());
        if (sourceStock == null) {
            throw new JeecgBootException("库存记录不存在");
        }

        // 校验待上架状态（shelf_id为空）
        if (sourceStock.getShelfId() != null && !sourceStock.getShelfId().trim().isEmpty()) {
            throw new JeecgBootException("该库存已上架，不能重复上架");
        }

        if (shelfRecord.getShelfQty() == null || shelfRecord.getShelfQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new JeecgBootException("上架数量必须大于0");
        }
        if (sourceStock.getQuantity().compareTo(shelfRecord.getShelfQty()) < 0) {
            throw new JeecgBootException("上架数量不能大于库存数量，当前库存：" + sourceStock.getQuantity());
        }

        // 2. 查询目标仓库级别
        Warehouse targetWarehouse = warehouseMapper.selectById(shelfRecord.getToWarehouseId());
        if (targetWarehouse == null) {
            throw new JeecgBootException("目标仓库不存在");
        }
        String locationLevel = targetWarehouse.getLocationLevel();

        // 只有 SHELF/LOCATION 级别支持上架
        if (!"SHELF".equals(locationLevel) && !"LOCATION".equals(locationLevel)) {
            throw new JeecgBootException("目标仓库未启用货架/货位管理，无需上架操作");
        }

        // 3. 根据级别校验必填字段
        if ("SHELF".equals(locationLevel)) {
            if (shelfRecord.getToShelfId() == null || shelfRecord.getToShelfId().trim().isEmpty()) {
                throw new JeecgBootException("该仓库启用货架管理，目标货架不能为空");
            }
            shelfRecord.setToLocationId(null);
        } else if ("LOCATION".equals(locationLevel)) {
            if (shelfRecord.getToShelfId() == null || shelfRecord.getToShelfId().trim().isEmpty()) {
                throw new JeecgBootException("该仓库启用货位管理，目标货架不能为空");
            }
            if (shelfRecord.getToLocationId() == null || shelfRecord.getToLocationId().trim().isEmpty()) {
                throw new JeecgBootException("该仓库启用货位管理，目标货位不能为空");
            }
        }

        // 4. 填充上架记录
       // shelfRecord.setId(UUID.randomUUID().toString().replace("-", ""));
       // shelfRecord.setRecordNo("SJ" + System.currentTimeMillis());
        String serialNo = serialNoService.generateSerialNo(SerialNoPrefixEnum.SHELF_ON.getPrefix());
        StockInDetail inDetail = stockInDetailService.getById(sourceStock.getInDetailId());
        StockIn stockIn = stockInService.getById(inDetail.getStockInId());
        shelfRecord.setRecordNo(serialNo);
        shelfRecord.setSourceType(stockIn.getStockInType());
        shelfRecord.setSourceNo(stockIn.getStockInNo());

        // 物品信息（完整复制）
        shelfRecord.setGoodsId(sourceStock.getGoodsId());
        shelfRecord.setGoodsType(sourceStock.getGoodsType());
        shelfRecord.setIsProduct(sourceStock.getIsProduct());
        shelfRecord.setGoodsCode(sourceStock.getGoodsCode());
        shelfRecord.setGoodsName(sourceStock.getGoodsName());
        shelfRecord.setGoodsSpec(sourceStock.getGoodsSpec());
        shelfRecord.setGoodsColor(sourceStock.getGoodsColor());
        shelfRecord.setUnit(sourceStock.getUnit());
        shelfRecord.setBatchNo(sourceStock.getBatchNo());

        // 原库位（上架前都是暂存区）
        shelfRecord.setFromWarehouseId(sourceStock.getWarehouseId());
        shelfRecord.setFromAreaId(sourceStock.getAreaId());
        shelfRecord.setFromShelfId(null);
        shelfRecord.setFromLocationId(sourceStock.getLocationId());

        // 上架数量
        shelfRecord.setShelfQty(shelfRecord.getShelfQty());

        // 操作人

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(sysUser != null){
            shelfRecord.setOperatorId(sysUser.getId());
            shelfRecord.setOperatorName(sysUser.getRealname());
        }

        shelfRecord.setShelfTime(new Date());
        shelfRecord.setCreateTime(new Date());

        this.save(shelfRecord);

        // 5. 处理库存
        if (sourceStock.getQuantity().compareTo(shelfRecord.getShelfQty()) == 0) {
            // 全部上架：直接更新原记录库位
            sourceStock.setWarehouseId(shelfRecord.getToWarehouseId());
            sourceStock.setAreaId(shelfRecord.getToAreaId());
            sourceStock.setShelfId(shelfRecord.getToShelfId());
            sourceStock.setLocationId(shelfRecord.getToLocationId());
            sourceStock.setUpdateTime(new Date());
            stockMapper.updateById(sourceStock);
            shelfRecord.setNewStockId(sourceStock.getId());
        } else {
            // 部分上架：拆分库存
            sourceStock.setQuantity(sourceStock.getQuantity().subtract(shelfRecord.getShelfQty()));
            sourceStock.setUpdateTime(new Date());
            stockMapper.updateById(sourceStock);

            // 创建新库存记录（上架后的）
            Stock newStock = new Stock();
            BeanUtils.copyProperties(sourceStock, newStock);
            newStock.setId(null);
            newStock.setWarehouseId(shelfRecord.getToWarehouseId());
            newStock.setAreaId(shelfRecord.getToAreaId());
            newStock.setShelfId(shelfRecord.getToShelfId());
            newStock.setLocationId(shelfRecord.getToLocationId());
            newStock.setQuantity(shelfRecord.getShelfQty());
            newStock.setOriginalQty(shelfRecord.getShelfQty());
            newStock.setLockedQty(BigDecimal.ZERO);
            newStock.setCreateTime(new Date());
            newStock.setUpdateTime(new Date());
            stockMapper.insert(newStock);

            shelfRecord.setNewStockId(newStock.getId());
        }

        this.updateById(shelfRecord);

    }
}

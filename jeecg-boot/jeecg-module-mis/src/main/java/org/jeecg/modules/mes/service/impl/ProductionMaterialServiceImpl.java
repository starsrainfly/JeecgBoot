package org.jeecg.modules.mes.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mes.entity.ProductionMaterial;
import org.jeecg.modules.mes.mapper.ProductionMaterialMapper;
import org.jeecg.modules.mes.service.IProductionMaterialService;
import org.jeecg.modules.mes.vo.ProductionMaterialVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 物料需求表
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
@Slf4j
@Service
public class ProductionMaterialServiceImpl extends ServiceImpl<ProductionMaterialMapper, ProductionMaterial> implements IProductionMaterialService {

    @Autowired
    private ProductionMaterialMapper productionMaterialMapper;

    @Override
    public List<ProductionMaterialVo> getBatchesByOrder(String orderId) {
        return productionMaterialMapper.getBatchesByOrder(orderId);
    }

    @Override
    public List<ProductionMaterialVo> getMaterialSummary(List<String> batchIds, List<String> materialReqIds, String orderId) {
        return productionMaterialMapper.getMaterialSummary(batchIds, materialReqIds, orderId);
    }

    @Override
    public List<ProductionMaterialVo> getMaterialDetailByBatches(List<String> batchIds, String orderId) {
        if (batchIds == null || batchIds.isEmpty()) {
            return new ArrayList<>();
        }
        return productionMaterialMapper.getMaterialDetailByBatches(batchIds, orderId);
    }

    @Override
    public IPage<ProductionMaterialVo> getPageList(Page<ProductionMaterialVo> page, ProductionMaterial productionMaterial) {
        return productionMaterialMapper.getPageList(page,productionMaterial);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean increaseIssuedQty(String id, BigDecimal qty) {
        int rows = productionMaterialMapper.increaseIssuedQty(id, qty);
        if (rows == 0) {
            throw new RuntimeException("需求表：更新出库数量、剩余待发数量及锁定数量，失败");
        }
        log.info("需求表：requirementId={}, 出库数量={}", id, qty);
        return true;
    }

    @Override
    public boolean decreaseLockAndRemainingQty(String id, BigDecimal qty) {
        int rows = productionMaterialMapper.decreaseLockAndRemainingQty(id, qty);
        if(rows == 0) {
            throw new RuntimeException("需求表：剩余待发数量及锁定数量，失败");
        }
        log.info("需求表：requirementId={}, 出库数量={}", id, qty);
        return true;
    }

    @Override
    public boolean updateLockQty(String id, BigDecimal lockQty, BigDecimal overQty, String status, String updateBy) {
        return productionMaterialMapper.updateLockQty(id, lockQty, overQty, status, updateBy) > 0;
    }

    @Override
    public boolean unlockQty(String id, BigDecimal unlockQty, BigDecimal unOverQty, String status, String updateBy) {
        return productionMaterialMapper.unlockQty(id, unlockQty, unOverQty, status, updateBy) > 0;
    }
}

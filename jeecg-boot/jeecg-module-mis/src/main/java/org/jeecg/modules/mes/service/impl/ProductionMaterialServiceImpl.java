package org.jeecg.modules.mes.service.impl;

import org.jeecg.modules.mes.entity.ProductionMaterial;
import org.jeecg.modules.mes.mapper.ProductionMaterialMapper;
import org.jeecg.modules.mes.service.IProductionMaterialService;
import org.jeecg.modules.mes.vo.ProductionMaterialVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 物料需求表
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
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
}

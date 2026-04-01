package org.jeecg.modules.mes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.mes.entity.ProductionMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mes.vo.ProductionMaterialVo;

import java.util.List;

/**
 * @Description: 物料需求表
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
public interface IProductionMaterialService extends IService<ProductionMaterial> {
    /**
     * 根据订单获取批次列表
     */
    List<ProductionMaterialVo> getBatchesByOrder(String orderId);

    /**
     * 获取物料汇总（按物料合并）
     */
    List<ProductionMaterialVo> getMaterialSummary(List<String> batchIds, List<String> materialReqIds, String orderId);

    /**
     * 获取物料明细（按批次展开，不合并）
     */
    List<ProductionMaterialVo> getMaterialDetailByBatches(List<String> batchIds, String orderId);

    IPage<ProductionMaterialVo> getPageList(Page<ProductionMaterialVo> page, ProductionMaterial productionMaterial);
}

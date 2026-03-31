package org.jeecg.modules.mes.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mes.entity.ProductionMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.mes.vo.ProductionMaterialVo;

/**
 * @Description: 物料需求表
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
public interface ProductionMaterialMapper extends BaseMapper<ProductionMaterial> {
    /**
     * 根据订单获取批次列表
     */
    List<ProductionMaterialVo> getBatchesByOrder(@Param("orderId") String orderId);

    /**
     * 获取物料汇总（按物料合并）
     */
    List<ProductionMaterialVo> getMaterialSummary(@Param("batchIds") List<String> batchIds,
                                                  @Param("materialReqIds") List<String> materialReqIds,
                                                  @Param("orderId") String orderId);

    /**
     * 获取物料明细（按批次展开，不合并）
     */
    List<ProductionMaterialVo> getMaterialDetailByBatches(@Param("batchIds") List<String> batchIds,
                                                          @Param("orderId") String orderId);
}

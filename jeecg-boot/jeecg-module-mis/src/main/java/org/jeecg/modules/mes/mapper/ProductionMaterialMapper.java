package org.jeecg.modules.mes.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    IPage<ProductionMaterialVo> getPageList(@Param("page") Page<ProductionMaterialVo> page,
                                            @Param("productionMaterial") ProductionMaterial productionMaterial);

    int increaseIssuedQty(@Param("id") String id, @Param("qty") BigDecimal qty);

    /**
     * 更新锁定数量（XML实现）
     */
    int updateLockQty(@Param("id") String id,
                      @Param("lockQty") BigDecimal lockQty,
                      @Param("overQty") BigDecimal overQty,
                      @Param("status") String status,
                      @Param("updateBy") String updateBy);

    /**
     * 释放锁定数量（XML实现）
     */
    int unlockQty(@Param("id") String id,
                  @Param("unlockQty") BigDecimal unlockQty,
                  @Param("unOverQty") BigDecimal unOverQty,
                  @Param("status") String status,
                  @Param("updateBy") String updateBy);
}

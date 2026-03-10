package org.jeecg.modules.mes.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mes.entity.ProductionPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 生产计划
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
public interface ProductionPlanMapper extends BaseMapper<ProductionPlan> {
	   /**
     * 通过主表 id 进行更新计划状态
     * @Param planId 主表id
     * @Param planStatus 计划状态
     */
    public boolean updatePlanStatus(@Param("planId") String planId,@Param("planStatus") String planStatus);

    public boolean updatePlanStatusBatch(@Param("planIds") List<String> planIds,@Param("planStatus") String planStatus);

}

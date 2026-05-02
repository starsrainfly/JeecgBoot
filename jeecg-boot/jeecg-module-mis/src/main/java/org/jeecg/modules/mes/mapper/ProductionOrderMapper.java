package org.jeecg.modules.mes.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mdm.vo.ManagerDashboardVo;
import org.jeecg.modules.mes.entity.ProductionOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 生产订单
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
public interface ProductionOrderMapper extends BaseMapper<ProductionOrder> {

    List<ManagerDashboardVo.ProduceOrderStatus> selectStatusDistribution();
    List<ManagerDashboardVo.RecentProduceOrder> selectRecentPending(@Param("limit") int limit);
}

package org.jeecg.modules.mes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.lettuce.core.dynamic.annotation.Param;
import org.jeecg.modules.mes.vo.ProductionOrderTrackingVo;

import java.util.List;
import java.util.Map;

public interface IProductionOrderTrackingService {
    IPage<ProductionOrderTrackingVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize);

    /** 不分页查询（导出用）— 复用同一段 SQL */
    List<ProductionOrderTrackingVo> queryList(@Param("params") Map<String, String> params);
}

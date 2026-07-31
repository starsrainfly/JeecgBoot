package org.jeecg.modules.mes.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mes.vo.ProductionOrderTrackingVo;

import java.util.List;
import java.util.Map;

public interface ProductionOrderTrackingMapper {
    List<ProductionOrderTrackingVo> queryPageList(Page<ProductionOrderTrackingVo> page, @Param("params") Map<String, String> params);

    /** 不分页查询（导出用）— 复用同一段 SQL */
    List<ProductionOrderTrackingVo> queryList(@Param("params") Map<String, String> params);
}

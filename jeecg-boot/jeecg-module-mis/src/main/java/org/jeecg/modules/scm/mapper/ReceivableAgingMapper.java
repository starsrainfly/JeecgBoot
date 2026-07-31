package org.jeecg.modules.scm.mapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.jeecg.modules.scm.vo.ReceivableAgingVo;

import java.util.List;
import java.util.Map;

public interface ReceivableAgingMapper {
    List<ReceivableAgingVo> queryList(@Param("params") Map<String, String> params);
}

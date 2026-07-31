package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.vo.SalesOrderTrackingVo;

import java.util.List;
import java.util.Map;

public interface ISalesOrderTrackingService {
    IPage<SalesOrderTrackingVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize);

    List<SalesOrderTrackingVo> queryList(@Param("params") Map<String, String> params);
}

package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.scm.vo.SalesOrderTrackingVo;

import java.util.Map;

public interface ISalesOrderTrackingService {
    IPage<SalesOrderTrackingVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize);
}

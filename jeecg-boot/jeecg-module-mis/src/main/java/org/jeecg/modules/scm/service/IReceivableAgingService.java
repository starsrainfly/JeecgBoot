package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.scm.vo.ReceivableAgingVo;

import java.util.List;
import java.util.Map;

public interface IReceivableAgingService  {
    List<ReceivableAgingVo> queryList(Map<String, String> params);
}

package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.scm.mapper.ReceivableAgingMapper;
import org.jeecg.modules.scm.service.IReceivableAgingService;
import org.jeecg.modules.scm.vo.ReceivableAgingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ReceivableAgingServiceImpl implements IReceivableAgingService {

    @Autowired
    private ReceivableAgingMapper receivableAgingMapper;

    @Override
    public List<ReceivableAgingVo> queryList(Map<String, String> params) {
        return receivableAgingMapper.queryList(params);
    }


}

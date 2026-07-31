package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.scm.mapper.SalesOrderTrackingMapper;
import org.jeecg.modules.scm.service.ISalesOrderTrackingService;
import org.jeecg.modules.scm.vo.SalesOrderTrackingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SalesOrderTrackingServiceImpl implements ISalesOrderTrackingService {

    @Autowired
    private SalesOrderTrackingMapper salesOrderTrackingMapper;

    @Override
    public IPage<SalesOrderTrackingVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize) {
        Page<SalesOrderTrackingVo> page = new Page<>(pageNo, pageSize);
        return page.setRecords(salesOrderTrackingMapper.queryList(page, params));
    }
}

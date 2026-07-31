package org.jeecg.modules.mes.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.mes.mapper.ProductionOrderTrackingMapper;
import org.jeecg.modules.mes.service.IProductionOrderTrackingService;
import org.jeecg.modules.mes.vo.ProductionOrderTrackingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductionOrderTrackingServiceImpl implements IProductionOrderTrackingService {

    @Autowired
    private ProductionOrderTrackingMapper trackingMapper;

    @Override
    public IPage<ProductionOrderTrackingVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize) {
        Page<ProductionOrderTrackingVo> page = new Page<>(pageNo, pageSize);
        return page.setRecords(trackingMapper.queryPageList(page, params));
    }

    @Override
    public List<ProductionOrderTrackingVo> queryList(Map<String, String> params) {
        // 直接调 queryList，不走分页
        return trackingMapper.queryList(params);
    }
}

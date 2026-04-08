package org.jeecg.modules.wms.service.impl;

import org.jeecg.modules.wms.entity.WarehouseArea;
import org.jeecg.modules.wms.mapper.WarehouseAreaMapper;
import org.jeecg.modules.wms.service.IWarehouseAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 仓库区域
 * @Author: jeecg-boot
 * @Date:   2026-04-05
 * @Version: V1.0
 */
@Service
public class WarehouseAreaServiceImpl extends ServiceImpl<WarehouseAreaMapper, WarehouseArea> implements IWarehouseAreaService {

    @Autowired
    private WarehouseAreaMapper warehouseAreaMapper;
    @Override
    public WarehouseArea getAreaByCode(String areaCode) {
        return warehouseAreaMapper.getAreaByCode(areaCode);
    }
}

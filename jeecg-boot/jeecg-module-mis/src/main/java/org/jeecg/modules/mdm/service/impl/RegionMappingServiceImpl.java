package org.jeecg.modules.mdm.service.impl;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.mdm.entity.RegionMapping;
import org.jeecg.modules.mdm.mapper.RegionMappingMapper;
import org.jeecg.modules.mdm.service.IRegionMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 地区编码映射表
 * @Author: jeecg-boot
 * @Date:   2026-04-17
 * @Version: V1.0
 */
@Service
public class RegionMappingServiceImpl extends ServiceImpl<RegionMappingMapper, RegionMapping> implements IRegionMappingService {

    @Autowired
    private RegionMappingMapper regionMappingMapper;
    /**
     * 根据区编码前2位获取省简写
     * @param prefix 前2位，如 "44"
     * @return 简写，如 "GD"
     */
    @Override
    public String getShortCodeByPrefix(String prefix) {
       String shortCode = null;

        RegionMapping mapping = regionMappingMapper.selectByPrefix(prefix);
        if (mapping == null) {
            throw new JeecgBootException("未找到地区前缀对应关系: " + prefix);
        }
        shortCode = mapping.getShortCode();
        return shortCode;
    }
}

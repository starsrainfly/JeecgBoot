package org.jeecg.modules.mdm.service;

import org.jeecg.modules.mdm.entity.RegionMapping;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 地区编码映射表
 * @Author: jeecg-boot
 * @Date:   2026-04-17
 * @Version: V1.0
 */
public interface IRegionMappingService extends IService<RegionMapping> {
    public String getShortCodeByPrefix(String prefix);
}

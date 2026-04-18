package org.jeecg.modules.mdm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mdm.entity.RegionMapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 地区编码映射表
 * @Author: jeecg-boot
 * @Date:   2026-04-17
 * @Version: V1.0
 */
public interface RegionMappingMapper extends BaseMapper<RegionMapping> {
    RegionMapping selectByPrefix(@Param("prefix") String prefix);
}

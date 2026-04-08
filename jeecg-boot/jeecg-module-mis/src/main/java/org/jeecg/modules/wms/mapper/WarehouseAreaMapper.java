package org.jeecg.modules.wms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.entity.WarehouseArea;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 仓库区域
 * @Author: jeecg-boot
 * @Date:   2026-04-05
 * @Version: V1.0
 */
public interface WarehouseAreaMapper extends BaseMapper<WarehouseArea> {
    public WarehouseArea getAreaByCode(@Param("areaCode") String areaCode);
}

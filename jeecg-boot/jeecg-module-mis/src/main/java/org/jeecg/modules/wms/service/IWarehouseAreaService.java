package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.WarehouseArea;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 仓库区域
 * @Author: jeecg-boot
 * @Date:   2026-04-05
 * @Version: V1.0
 */
public interface IWarehouseAreaService extends IService<WarehouseArea> {
    public WarehouseArea getAreaByCode(String areaCode);
}

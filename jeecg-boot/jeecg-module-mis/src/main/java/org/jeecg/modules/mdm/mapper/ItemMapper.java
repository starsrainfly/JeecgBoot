package org.jeecg.modules.mdm.mapper;

import org.jeecg.modules.mdm.entity.Item;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 统一库存项表
 * @Author: jeecg-boot
 * @Date:   2026-01-15
 * @Version: V1.0
 */
public interface ItemMapper extends BaseMapper<Item> {
    String GetIdByMaterialId(String materialId);
}

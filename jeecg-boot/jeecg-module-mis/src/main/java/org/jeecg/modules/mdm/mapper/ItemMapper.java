package org.jeecg.modules.mdm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mdm.entity.Item;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 统一库存项目表
 * @Author: jeecg-boot
 * @Date:   2026-02-03
 * @Version: V1.0
 */
public interface ItemMapper extends BaseMapper<Item> {
    String GetIdByMaterialId(String materialId);
}

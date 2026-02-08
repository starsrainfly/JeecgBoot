package org.jeecg.modules.mdm.service;

import org.jeecg.modules.mdm.entity.Item;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 统一库存项目表
 * @Author: jeecg-boot
 * @Date:   2026-02-03
 * @Version: V1.0
 */
public interface IItemService extends IService<Item> {
    public String GetIdByMaterialId(String materialId);
}

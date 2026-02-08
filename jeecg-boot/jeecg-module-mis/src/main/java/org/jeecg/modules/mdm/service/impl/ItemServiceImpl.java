package org.jeecg.modules.mdm.service.impl;

import org.jeecg.modules.mdm.entity.Item;
import org.jeecg.modules.mdm.mapper.ItemMapper;
import org.jeecg.modules.mdm.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 统一库存项目表
 * @Author: jeecg-boot
 * @Date:   2026-02-03
 * @Version: V1.0
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public String GetIdByMaterialId(String materialId) {
        return itemMapper.GetIdByMaterialId(materialId);
    }
}

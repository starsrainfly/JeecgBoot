package org.jeecg.modules.mdm.service.impl;

import org.jeecg.modules.mdm.entity.RecipeSpec;
import org.jeecg.modules.mdm.mapper.RecipeSpecMapper;
import org.jeecg.modules.mdm.service.IRecipeSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: mis_recipe_spec
 * @Author: jeecg-boot
 * @Date:   2026-01-13
 * @Version: V1.0
 */
@Service
public class RecipeSpecServiceImpl extends ServiceImpl<RecipeSpecMapper, RecipeSpec> implements IRecipeSpecService {

    @Autowired
    RecipeSpecMapper recipeSpecMapper;

    @Override
    public RecipeSpec getByRecipeId(String RecipeId) {
        return recipeSpecMapper.getByRecipeId(RecipeId);
    }
}

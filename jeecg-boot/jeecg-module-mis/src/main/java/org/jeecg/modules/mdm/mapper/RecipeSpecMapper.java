package org.jeecg.modules.mdm.mapper;

import org.jeecg.modules.mdm.entity.RecipeSpec;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: mis_recipe_spec
 * @Author: jeecg-boot
 * @Date:   2026-01-13
 * @Version: V1.0
 */
public interface RecipeSpecMapper extends BaseMapper<RecipeSpec> {
    public RecipeSpec getByRecipeId(String RecipeId);
}

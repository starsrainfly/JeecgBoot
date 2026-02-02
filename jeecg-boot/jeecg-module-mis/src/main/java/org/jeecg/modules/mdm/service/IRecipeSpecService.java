package org.jeecg.modules.mdm.service;

import org.jeecg.modules.mdm.entity.RecipeSpec;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: mis_recipe_spec
 * @Author: jeecg-boot
 * @Date:   2026-01-13
 * @Version: V1.0
 */
public interface IRecipeSpecService extends IService<RecipeSpec> {

    public RecipeSpec getByRecipeId(String RecipeId);
}

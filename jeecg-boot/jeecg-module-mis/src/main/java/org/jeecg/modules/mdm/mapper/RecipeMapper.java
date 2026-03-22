package org.jeecg.modules.mdm.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mdm.entity.Recipe;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 配方表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
public interface RecipeMapper extends BaseMapper<Recipe> {

    int publishRecipe(@Param("id") String id, @Param("publishBy") String publishBy);
}

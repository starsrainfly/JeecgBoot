package org.jeecg.modules.mdm.service;

import org.jeecg.modules.mdm.entity.RecipeDetail;
import org.jeecg.modules.mdm.entity.Recipe;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 配方表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
public interface IRecipeService extends IService<Recipe> {

	/**
	 * 添加一对多
	 *
	 * @param recipe
	 * @param recipeDetailList
	 */
	public void saveMain(Recipe recipe,List<RecipeDetail> recipeDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param recipe
   * @param recipeDetailList
	 */
	public void updateMain(Recipe recipe,List<RecipeDetail> recipeDetailList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}

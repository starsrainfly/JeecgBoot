package org.jeecg.modules.mdm.service.impl;

import org.jeecg.modules.mdm.entity.Recipe;
import org.jeecg.modules.mdm.entity.RecipeDetail;
import org.jeecg.modules.mdm.mapper.RecipeDetailMapper;
import org.jeecg.modules.mdm.mapper.RecipeMapper;
import org.jeecg.modules.mdm.service.IRecipeService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 配方表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
@Service
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements IRecipeService {

	@Autowired
	private RecipeMapper recipeMapper;
	@Autowired
	private RecipeDetailMapper recipeDetailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(Recipe recipe, List<RecipeDetail> recipeDetailList) {
		recipeMapper.insert(recipe);
		if(recipeDetailList!=null && recipeDetailList.size()>0) {
			for(RecipeDetail entity:recipeDetailList) {
				//外键设置
				entity.setRecipeId(recipe.getId());
				recipeDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(Recipe recipe,List<RecipeDetail> recipeDetailList) {
		recipeMapper.updateById(recipe);
		
		//1.先删除子表数据
		recipeDetailMapper.deleteByMainId(recipe.getId());
		
		//2.子表数据重新插入
		if(recipeDetailList!=null && recipeDetailList.size()>0) {
			for(RecipeDetail entity:recipeDetailList) {
				//外键设置
				entity.setRecipeId(recipe.getId());
				recipeDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		recipeDetailMapper.deleteByMainId(id);
		recipeMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			recipeDetailMapper.deleteByMainId(id.toString());
			recipeMapper.deleteById(id);
		}
	}
	
}

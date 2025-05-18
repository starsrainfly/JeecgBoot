package org.jeecg.modules.mis.Recipe.service.impl;

import org.jeecg.modules.mis.Recipe.entity.RecipeDetail;
import org.jeecg.modules.mis.Recipe.mapper.RecipeDetailMapper;
import org.jeecg.modules.mis.Recipe.service.IRecipeDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 配方明细
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
@Service
public class RecipeDetailServiceImpl extends ServiceImpl<RecipeDetailMapper, RecipeDetail> implements IRecipeDetailService {
	
	@Autowired
	private RecipeDetailMapper recipeDetailMapper;
	
	@Override
	public List<RecipeDetail> selectByMainId(String mainId) {
		return recipeDetailMapper.selectByMainId(mainId);
	}
}

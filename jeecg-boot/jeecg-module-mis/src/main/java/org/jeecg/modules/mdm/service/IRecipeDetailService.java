package org.jeecg.modules.mdm.service;

import org.jeecg.modules.mdm.entity.RecipeDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 配方明细
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
public interface IRecipeDetailService extends IService<RecipeDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<RecipeDetail>
	 */
	public List<RecipeDetail> selectByMainId(String mainId);
}

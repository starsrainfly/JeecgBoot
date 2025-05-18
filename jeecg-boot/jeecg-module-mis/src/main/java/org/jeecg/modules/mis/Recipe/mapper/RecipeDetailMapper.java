package org.jeecg.modules.mis.Recipe.mapper;

import java.util.List;
import org.jeecg.modules.mis.Recipe.entity.RecipeDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 配方明细
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
public interface RecipeDetailMapper extends BaseMapper<RecipeDetail> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<RecipeDetail>
   */
	public List<RecipeDetail> selectByMainId(@Param("mainId") String mainId);
}

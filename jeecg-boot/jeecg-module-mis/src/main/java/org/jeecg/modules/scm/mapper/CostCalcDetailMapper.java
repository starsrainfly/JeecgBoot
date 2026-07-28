package org.jeecg.modules.scm.mapper;

import java.util.List;
import org.jeecg.modules.scm.entity.CostCalcDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 成本核算快照明细
 * @Author: jeecg-boot
 * @Date:   2026-07-28
 * @Version: V1.0
 */
public interface CostCalcDetailMapper extends BaseMapper<CostCalcDetail> {

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
   * @return List<CostCalcDetail>
   */
	public List<CostCalcDetail> selectByMainId(@Param("mainId") String mainId);
}

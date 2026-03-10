package org.jeecg.modules.mdm.mapper;

import java.util.List;
import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 工序步骤
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
public interface ProcessRoutingStepMapper extends BaseMapper<ProcessRoutingStep> {

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
   * @return List<ProcessRoutingStep>
   */
	public List<ProcessRoutingStep> selectByMainId(@Param("mainId") String mainId);
}

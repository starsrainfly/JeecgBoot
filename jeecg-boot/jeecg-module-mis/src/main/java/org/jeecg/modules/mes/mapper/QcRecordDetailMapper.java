package org.jeecg.modules.mes.mapper;

import java.util.List;
import org.jeecg.modules.mes.entity.QcRecordDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 质检记录明细
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
public interface QcRecordDetailMapper extends BaseMapper<QcRecordDetail> {

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
   * @return List<QcRecordDetail>
   */
	public List<QcRecordDetail> selectByMainId(@Param("mainId") String mainId);
}

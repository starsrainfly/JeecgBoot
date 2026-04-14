package org.jeecg.modules.wms.mapper;

import java.util.List;
import org.jeecg.modules.wms.entity.DeliveryDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 发货明细
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
public interface DeliveryDetailMapper extends BaseMapper<DeliveryDetail> {

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
   * @return List<DeliveryDetail>
   */
	public List<DeliveryDetail> selectByMainId(@Param("mainId") String mainId);
}

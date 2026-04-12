package org.jeecg.modules.wms.mapper;

import java.util.List;
import org.jeecg.modules.wms.entity.StockOutDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 出库明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-09
 * @Version: V1.0
 */
public interface StockOutDetailMapper extends BaseMapper<StockOutDetail> {

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
   * @return List<StockOutDetail>
   */
	public List<StockOutDetail> selectByMainId(@Param("mainId") String mainId);
}

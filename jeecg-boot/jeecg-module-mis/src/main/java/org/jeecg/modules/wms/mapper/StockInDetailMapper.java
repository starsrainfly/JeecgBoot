package org.jeecg.modules.wms.mapper;

import java.util.List;
import org.jeecg.modules.wms.entity.StockInDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 入库明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
public interface StockInDetailMapper extends BaseMapper<StockInDetail> {

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
   * @return List<StockInDetail>
   */
	public List<StockInDetail> selectByMainId(@Param("mainId") String mainId);
}

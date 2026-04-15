package org.jeecg.modules.scm.mapper;

import java.util.List;
import org.jeecg.modules.scm.entity.PriceOfferDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 报价单明细
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
public interface PriceOfferDetailMapper extends BaseMapper<PriceOfferDetail> {

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
   * @return List<PriceOfferDetail>
   */
	public List<PriceOfferDetail> selectByMainId(@Param("mainId") String mainId);
}

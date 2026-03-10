package org.jeecg.modules.mes.mapper;

import java.util.List;
import org.jeecg.modules.mes.entity.ProductionOrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 生产订单明细
 * @Author: jeecg-boot
 * @Date:   2026-03-09
 * @Version: V1.0
 */
public interface ProductionOrderDetailMapper extends BaseMapper<ProductionOrderDetail> {

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
   * @return List<ProductionOrderDetail>
   */
	public List<ProductionOrderDetail> selectByMainId(@Param("mainId") String mainId);
}

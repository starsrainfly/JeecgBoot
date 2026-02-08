package org.jeecg.modules.scm.mapper;

import java.util.List;
import org.jeecg.modules.scm.entity.SalesOrderLine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 销售订单明细表
 * @Author: jeecg-boot
 * @Date:   2026-02-07
 * @Version: V1.0
 */
public interface SalesOrderLineMapper extends BaseMapper<SalesOrderLine> {

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
   * @return List<SalesOrderLine>
   */
	public List<SalesOrderLine> selectByMainId(@Param("mainId") String mainId);
}

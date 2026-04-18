package org.jeecg.modules.scm.mapper;

import java.util.List;
import org.jeecg.modules.scm.entity.CustomerSalesman;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 客户销售员
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
public interface CustomerSalesmanMapper extends BaseMapper<CustomerSalesman> {

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
   * @return List<CustomerSalesman>
   */
	public List<CustomerSalesman> selectByMainId(@Param("mainId") String mainId);
}

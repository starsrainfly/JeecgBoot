package org.jeecg.modules.scm.mapper;

import java.util.List;
import org.jeecg.modules.scm.entity.CustomerAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 客户地址
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
public interface CustomerAddressMapper extends BaseMapper<CustomerAddress> {

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
   * @return List<CustomerAddress>
   */
	public List<CustomerAddress> selectByMainId(@Param("mainId") String mainId);
}

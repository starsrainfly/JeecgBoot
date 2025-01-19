package org.jeecg.modules.mis.currency.mapper;

import java.util.List;
import org.jeecg.modules.mis.currency.entity.ExchangeRate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 汇率表
 * @Author: jeecg-boot
 * @Date:   2024-11-16
 * @Version: V1.0
 */
public interface ExchangeRateMapper extends BaseMapper<ExchangeRate> {

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
    * @return List<ExchangeRate>
    */
	public List<ExchangeRate> selectByMainId(@Param("mainId") String mainId);

}

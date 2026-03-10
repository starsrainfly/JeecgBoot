package org.jeecg.modules.mes.mapper;

import java.util.List;
import org.jeecg.modules.mes.entity.ProductionBatchBom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 生产批次物料清单
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
public interface ProductionBatchBomMapper extends BaseMapper<ProductionBatchBom> {

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
   * @return List<ProductionBatchBom>
   */
	public List<ProductionBatchBom> selectByMainId(@Param("mainId") String mainId);
}

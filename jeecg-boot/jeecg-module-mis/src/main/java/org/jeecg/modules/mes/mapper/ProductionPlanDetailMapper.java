package org.jeecg.modules.mes.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.mes.entity.ProductionPlanDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mes.vo.ProductionPlanDetailVo;

/**
 * @Description: 生产计划明细表
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
public interface ProductionPlanDetailMapper extends BaseMapper<ProductionPlanDetail> {

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
   * @return List<ProductionPlanDetail>
   */
	public List<ProductionPlanDetail> selectByMainId(@Param("mainId") String mainId);

	/**
	 * 查询可用计划明细
	 */
	IPage<ProductionPlanDetailVo> selectAvailableForOrder(@Param("page") Page<ProductionPlanDetailVo> page,
														  @Param("planNo") String planNo,
														  @Param("productCode") String productCode);
}

package org.jeecg.modules.wms.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.StockOutDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.vo.StockOutDetailVo;

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

	/**
	 * 出库明细查询 — 分页
	 */
	IPage<StockOutDetailVo> listDetailAll(Page<StockOutDetailVo> page,
										  @Param("vo") StockOutDetailVo stockOutDetailVo);

	/**
	 * 出库明细查询 — 不分页（导出用）
	 */
	List<StockOutDetailVo> listDetailAll(@Param("vo") StockOutDetailVo stockOutDetailVo);

	/**
	 * 计算明细合计 — SUM聚合
	 */
	StockOutDetailVo calcDetailTotal(@Param("vo") StockOutDetailVo stockOutDetailVo);

	/**
	 * 汇总该批次已审核的领料成本
	 * @param batchId
	 * @return
	 */
	BigDecimal sumMaterialCostByBatchId(@Param("batchId") String batchId);
}

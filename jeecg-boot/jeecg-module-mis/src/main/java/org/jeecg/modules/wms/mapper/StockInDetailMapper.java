package org.jeecg.modules.wms.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.StockInDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.vo.StockInDetailVo;

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

	/**
	 * 分页查询入库明细（含主表信息）
	 */
	IPage<StockInDetailVo> listDetailAll(Page<StockInDetailVo> page,
										 @Param("vo") StockInDetailVo vo);

	/**
	 * 查询所有入库明细（含主表信息，用于导出）
	 */
	List<StockInDetailVo> listDetailAll(@Param("vo") StockInDetailVo vo);
}

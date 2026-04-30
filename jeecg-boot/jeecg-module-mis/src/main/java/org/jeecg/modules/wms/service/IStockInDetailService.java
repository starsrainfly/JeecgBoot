package org.jeecg.modules.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.StockInDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.vo.StockInDetailVo;

import java.util.List;

/**
 * @Description: 入库明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
public interface IStockInDetailService extends IService<StockInDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<StockInDetail>
	 */
	public List<StockInDetail> selectByMainId(String mainId);

	/**
	 * 分页查询入库明细（含主表信息）
	 */
	IPage<StockInDetailVo> listDetailAll(Page<StockInDetailVo> page, StockInDetailVo vo);

	/**
	 * 查询所有入库明细（含主表信息，用于导出）
	 */
	List<StockInDetailVo> listDetailAll(StockInDetailVo vo);
}

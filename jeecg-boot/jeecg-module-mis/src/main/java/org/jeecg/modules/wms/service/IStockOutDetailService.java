package org.jeecg.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.StockOutDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.vo.StockOutDetailVo;

import java.util.List;

/**
 * @Description: 出库明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-09
 * @Version: V1.0
 */
public interface IStockOutDetailService extends IService<StockOutDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<StockOutDetail>
	 */
	public List<StockOutDetail> selectByMainId(String mainId);

	/**
	 * 出库明细查询 — 分页
	 */
	IPage<StockOutDetailVo> listDetailAll(Page<StockOutDetailVo> page, StockOutDetailVo stockOutDetailVo);

	/**
	 * 出库明细查询 — 不分页
	 */
	List<StockOutDetailVo> listDetailAll(StockOutDetailVo stockOutDetailVo);

	/**
	 * 计算明细合计
	 */
	StockOutDetailVo calcDetailTotal(StockOutDetailVo stockOutDetailVo);
}

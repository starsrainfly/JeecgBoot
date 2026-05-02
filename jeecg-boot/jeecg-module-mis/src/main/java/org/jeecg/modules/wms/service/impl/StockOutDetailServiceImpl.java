package org.jeecg.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.StockOutDetail;
import org.jeecg.modules.wms.mapper.StockOutDetailMapper;
import org.jeecg.modules.wms.service.IStockOutDetailService;
import org.jeecg.modules.wms.vo.StockOutDetailVo;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 出库明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-09
 * @Version: V1.0
 */
@Service
public class StockOutDetailServiceImpl extends ServiceImpl<StockOutDetailMapper, StockOutDetail> implements IStockOutDetailService {
	
	@Autowired
	private StockOutDetailMapper stockOutDetailMapper;
	
	@Override
	public List<StockOutDetail> selectByMainId(String mainId) {
		return stockOutDetailMapper.selectByMainId(mainId);
	}

	@Override
	public IPage<StockOutDetailVo> listDetailAll(Page<StockOutDetailVo> page, StockOutDetailVo stockOutDetailVo) {
		return stockOutDetailMapper.listDetailAll(page, stockOutDetailVo);
	}

	@Override
	public List<StockOutDetailVo> listDetailAll(StockOutDetailVo stockOutDetailVo) {
		return stockOutDetailMapper.listDetailAll(stockOutDetailVo);
	}

	@Override
	public StockOutDetailVo calcDetailTotal(StockOutDetailVo stockOutDetailVo) {
		return stockOutDetailMapper.calcDetailTotal(stockOutDetailVo);
	}
}

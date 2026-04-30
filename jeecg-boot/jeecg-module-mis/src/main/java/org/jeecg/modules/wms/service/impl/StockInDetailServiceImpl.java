package org.jeecg.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.wms.entity.StockInDetail;
import org.jeecg.modules.wms.mapper.StockInDetailMapper;
import org.jeecg.modules.wms.service.IStockInDetailService;
import org.jeecg.modules.wms.vo.StockInDetailVo;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 入库明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
@Service
public class StockInDetailServiceImpl extends ServiceImpl<StockInDetailMapper, StockInDetail> implements IStockInDetailService {
	
	@Autowired
	private StockInDetailMapper stockInDetailMapper;
	
	@Override
	public List<StockInDetail> selectByMainId(String mainId) {
		return stockInDetailMapper.selectByMainId(mainId);
	}

	@Override
	public IPage<StockInDetailVo> listDetailAll(Page<StockInDetailVo> page, StockInDetailVo vo) {
		return stockInDetailMapper.listDetailAll(page, vo);
	}

	@Override
	public List<StockInDetailVo> listDetailAll(StockInDetailVo vo) {
		return stockInDetailMapper.listDetailAll(vo);
	}
}

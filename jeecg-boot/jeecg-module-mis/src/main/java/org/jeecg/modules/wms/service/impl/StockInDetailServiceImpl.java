package org.jeecg.modules.wms.service.impl;

import org.jeecg.modules.wms.entity.StockInDetail;
import org.jeecg.modules.wms.mapper.StockInDetailMapper;
import org.jeecg.modules.wms.service.IStockInDetailService;
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
}

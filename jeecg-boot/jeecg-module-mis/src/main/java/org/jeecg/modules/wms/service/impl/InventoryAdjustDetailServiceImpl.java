package org.jeecg.modules.wms.service.impl;

import org.jeecg.modules.wms.entity.InventoryAdjustDetail;
import org.jeecg.modules.wms.mapper.InventoryAdjustDetailMapper;
import org.jeecg.modules.wms.service.IInventoryAdjustDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 盘库调整单明细表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Service
public class InventoryAdjustDetailServiceImpl extends ServiceImpl<InventoryAdjustDetailMapper, InventoryAdjustDetail> implements IInventoryAdjustDetailService {
	
	@Autowired
	private InventoryAdjustDetailMapper inventoryAdjustDetailMapper;
	
	@Override
	public List<InventoryAdjustDetail> selectByMainId(String mainId) {
		return inventoryAdjustDetailMapper.selectByMainId(mainId);
	}
}

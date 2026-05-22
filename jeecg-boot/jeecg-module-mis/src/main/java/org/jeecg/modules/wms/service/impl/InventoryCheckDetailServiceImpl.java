package org.jeecg.modules.wms.service.impl;

import org.jeecg.modules.wms.entity.InventoryCheckDetail;
import org.jeecg.modules.wms.mapper.InventoryCheckDetailMapper;
import org.jeecg.modules.wms.service.IInventoryCheckDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 盘库明细表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Service
public class InventoryCheckDetailServiceImpl extends ServiceImpl<InventoryCheckDetailMapper, InventoryCheckDetail> implements IInventoryCheckDetailService {
	
	@Autowired
	private InventoryCheckDetailMapper inventoryCheckDetailMapper;
	
	@Override
	public List<InventoryCheckDetail> selectByMainId(String mainId) {
		return inventoryCheckDetailMapper.selectByMainId(mainId);
	}
}

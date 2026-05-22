package org.jeecg.modules.wms.service.impl;

import org.jeecg.modules.wms.entity.InventoryAdjust;
import org.jeecg.modules.wms.entity.InventoryAdjustDetail;
import org.jeecg.modules.wms.mapper.InventoryAdjustDetailMapper;
import org.jeecg.modules.wms.mapper.InventoryAdjustMapper;
import org.jeecg.modules.wms.service.IInventoryAdjustService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 盘点调整单主表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Service
public class InventoryAdjustServiceImpl extends ServiceImpl<InventoryAdjustMapper, InventoryAdjust> implements IInventoryAdjustService {

	@Autowired
	private InventoryAdjustMapper inventoryAdjustMapper;
	@Autowired
	private InventoryAdjustDetailMapper inventoryAdjustDetailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(InventoryAdjust inventoryAdjust, List<InventoryAdjustDetail> inventoryAdjustDetailList) {
		inventoryAdjustMapper.insert(inventoryAdjust);
		if(inventoryAdjustDetailList!=null && inventoryAdjustDetailList.size()>0) {
			for(InventoryAdjustDetail entity:inventoryAdjustDetailList) {
				//外键设置
				entity.setAdjustId(inventoryAdjust.getId());
				inventoryAdjustDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(InventoryAdjust inventoryAdjust,List<InventoryAdjustDetail> inventoryAdjustDetailList) {
		inventoryAdjustMapper.updateById(inventoryAdjust);
		
		//1.先删除子表数据
		inventoryAdjustDetailMapper.deleteByMainId(inventoryAdjust.getId());
		
		//2.子表数据重新插入
		if(inventoryAdjustDetailList!=null && inventoryAdjustDetailList.size()>0) {
			for(InventoryAdjustDetail entity:inventoryAdjustDetailList) {
				//外键设置
				entity.setAdjustId(inventoryAdjust.getId());
				inventoryAdjustDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		inventoryAdjustDetailMapper.deleteByMainId(id);
		inventoryAdjustMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			inventoryAdjustDetailMapper.deleteByMainId(id.toString());
			inventoryAdjustMapper.deleteById(id);
		}
	}
	
}

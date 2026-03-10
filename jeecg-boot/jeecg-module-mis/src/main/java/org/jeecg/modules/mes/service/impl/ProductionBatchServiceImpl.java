package org.jeecg.modules.mes.service.impl;

import org.jeecg.modules.mes.entity.ProductionBatch;
import org.jeecg.modules.mes.entity.ProductionBatchBom;
import org.jeecg.modules.mes.mapper.ProductionBatchBomMapper;
import org.jeecg.modules.mes.mapper.ProductionBatchMapper;
import org.jeecg.modules.mes.service.IProductionBatchService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 生产批次
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Service
public class ProductionBatchServiceImpl extends ServiceImpl<ProductionBatchMapper, ProductionBatch> implements IProductionBatchService {

	@Autowired
	private ProductionBatchMapper productionBatchMapper;
	@Autowired
	private ProductionBatchBomMapper productionBatchBomMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(ProductionBatch productionBatch, List<ProductionBatchBom> productionBatchBomList) {
		productionBatchMapper.insert(productionBatch);
		if(productionBatchBomList!=null && productionBatchBomList.size()>0) {
			for(ProductionBatchBom entity:productionBatchBomList) {
				//外键设置
				entity.setBatchId(productionBatch.getId());
				productionBatchBomMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(ProductionBatch productionBatch,List<ProductionBatchBom> productionBatchBomList) {
		productionBatchMapper.updateById(productionBatch);
		
		//1.先删除子表数据
		productionBatchBomMapper.deleteByMainId(productionBatch.getId());
		
		//2.子表数据重新插入
		if(productionBatchBomList!=null && productionBatchBomList.size()>0) {
			for(ProductionBatchBom entity:productionBatchBomList) {
				//外键设置
				entity.setBatchId(productionBatch.getId());
				productionBatchBomMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		productionBatchBomMapper.deleteByMainId(id);
		productionBatchMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			productionBatchBomMapper.deleteByMainId(id.toString());
			productionBatchMapper.deleteById(id);
		}
	}
	
}

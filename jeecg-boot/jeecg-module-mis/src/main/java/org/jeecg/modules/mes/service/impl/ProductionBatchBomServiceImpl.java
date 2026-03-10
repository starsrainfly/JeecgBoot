package org.jeecg.modules.mes.service.impl;

import org.jeecg.modules.mes.entity.ProductionBatchBom;
import org.jeecg.modules.mes.mapper.ProductionBatchBomMapper;
import org.jeecg.modules.mes.service.IProductionBatchBomService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 生产批次物料清单
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
@Service
public class ProductionBatchBomServiceImpl extends ServiceImpl<ProductionBatchBomMapper, ProductionBatchBom> implements IProductionBatchBomService {
	
	@Autowired
	private ProductionBatchBomMapper productionBatchBomMapper;
	
	@Override
	public List<ProductionBatchBom> selectByMainId(String mainId) {
		return productionBatchBomMapper.selectByMainId(mainId);
	}
}

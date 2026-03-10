package org.jeecg.modules.mes.service;

import org.jeecg.modules.mes.entity.ProductionBatchBom;
import org.jeecg.modules.mes.entity.ProductionBatch;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 生产批次
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
public interface IProductionBatchService extends IService<ProductionBatch> {

	/**
	 * 添加一对多
	 *
	 * @param productionBatch
	 * @param productionBatchBomList
	 */
	public void saveMain(ProductionBatch productionBatch,List<ProductionBatchBom> productionBatchBomList) ;
	
	/**
	 * 修改一对多
	 *
   * @param productionBatch
   * @param productionBatchBomList
	 */
	public void updateMain(ProductionBatch productionBatch,List<ProductionBatchBom> productionBatchBomList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}

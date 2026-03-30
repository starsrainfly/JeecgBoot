package org.jeecg.modules.mes.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.mes.entity.ProductionBatchBom;
import org.jeecg.modules.mes.entity.ProductionBatch;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mes.vo.ProductionBatchPage;

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

	/**
	 * 设置当前批次状态
	 * @param id
	 * @param status
	 */
	public void setStatus(String id, String status);

	/**
	 * 更新状态同时更新生产数量，称重开始时间及称重完成
	 * @param batchId
	 */
	public void updateBatchStatus(String batchId);

	/**
	 * 查询实时计算配料进度列表
	 * @param queryWrapper
	 * @return
	 */
	public List<ProductionBatchPage> queryPageWeighingProgressList(QueryWrapper<ProductionBatch> queryWrapper);
}

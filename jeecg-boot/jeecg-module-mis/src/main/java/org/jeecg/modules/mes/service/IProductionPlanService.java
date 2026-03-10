package org.jeecg.modules.mes.service;

import org.jeecg.modules.mes.entity.ProductionPlanDetail;
import org.jeecg.modules.mes.entity.ProductionPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 生产计划
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
public interface IProductionPlanService extends IService<ProductionPlan> {

	/**
	 * 添加一对多
	 *
	 * @param productionPlan
	 * @param productionPlanDetailList
	 */
	public void saveMain(ProductionPlan productionPlan,List<ProductionPlanDetail> productionPlanDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param productionPlan
   * @param productionPlanDetailList
	 */
	public void updateMain(ProductionPlan productionPlan,List<ProductionPlanDetail> productionPlanDetailList);
	
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
	* 发布计划
	 * @Param planId
	* */
	public void publishPlan(String planId);

	/**
	 * 批量发布计划
	 * @param idList
	 */
	public void publishPlanBatch(Collection<? extends Serializable> idList);
	
}

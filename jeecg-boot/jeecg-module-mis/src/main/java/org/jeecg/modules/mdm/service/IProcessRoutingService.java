package org.jeecg.modules.mdm.service;

import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
import org.jeecg.modules.mdm.entity.ProcessRouting;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 工艺管理
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
public interface IProcessRoutingService extends IService<ProcessRouting> {

	/**
	 * 添加一对多
	 *
	 * @param processRouting
	 * @param processRoutingStepList
	 */
	public void saveMain(ProcessRouting processRouting,List<ProcessRoutingStep> processRoutingStepList) ;
	
	/**
	 * 修改一对多
	 *
   * @param processRouting
   * @param processRoutingStepList
	 */
	public void updateMain(ProcessRouting processRouting,List<ProcessRoutingStep> processRoutingStepList);
	
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

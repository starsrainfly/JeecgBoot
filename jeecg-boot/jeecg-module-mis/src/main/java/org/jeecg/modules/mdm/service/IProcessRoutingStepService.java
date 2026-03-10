package org.jeecg.modules.mdm.service;

import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 工序步骤
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
public interface IProcessRoutingStepService extends IService<ProcessRoutingStep> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<ProcessRoutingStep>
	 */
	public List<ProcessRoutingStep> selectByMainId(String mainId);
}

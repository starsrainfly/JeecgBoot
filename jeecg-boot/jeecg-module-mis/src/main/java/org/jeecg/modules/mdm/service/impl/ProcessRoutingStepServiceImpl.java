package org.jeecg.modules.mdm.service.impl;

import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
import org.jeecg.modules.mdm.mapper.ProcessRoutingStepMapper;
import org.jeecg.modules.mdm.service.IProcessRoutingStepService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 工序步骤
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
@Service
public class ProcessRoutingStepServiceImpl extends ServiceImpl<ProcessRoutingStepMapper, ProcessRoutingStep> implements IProcessRoutingStepService {
	
	@Autowired
	private ProcessRoutingStepMapper processRoutingStepMapper;
	
	@Override
	public List<ProcessRoutingStep> selectByMainId(String mainId) {
		return processRoutingStepMapper.selectByMainId(mainId);
	}
}

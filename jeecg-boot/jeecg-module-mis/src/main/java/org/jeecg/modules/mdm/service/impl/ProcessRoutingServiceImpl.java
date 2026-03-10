package org.jeecg.modules.mdm.service.impl;

import org.jeecg.modules.mdm.entity.ProcessRouting;
import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
import org.jeecg.modules.mdm.mapper.ProcessRoutingStepMapper;
import org.jeecg.modules.mdm.mapper.ProcessRoutingMapper;
import org.jeecg.modules.mdm.service.IProcessRoutingService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 工艺管理
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
@Service
public class ProcessRoutingServiceImpl extends ServiceImpl<ProcessRoutingMapper, ProcessRouting> implements IProcessRoutingService {

	@Autowired
	private ProcessRoutingMapper processRoutingMapper;
	@Autowired
	private ProcessRoutingStepMapper processRoutingStepMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(ProcessRouting processRouting, List<ProcessRoutingStep> processRoutingStepList) {
		processRoutingMapper.insert(processRouting);
		if(processRoutingStepList!=null && processRoutingStepList.size()>0) {
			for(ProcessRoutingStep entity:processRoutingStepList) {
				//外键设置
				entity.setRoutingId(processRouting.getId());
				processRoutingStepMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(ProcessRouting processRouting,List<ProcessRoutingStep> processRoutingStepList) {
		processRoutingMapper.updateById(processRouting);
		
		//1.先删除子表数据
		processRoutingStepMapper.deleteByMainId(processRouting.getId());
		
		//2.子表数据重新插入
		if(processRoutingStepList!=null && processRoutingStepList.size()>0) {
			for(ProcessRoutingStep entity:processRoutingStepList) {
				//外键设置
				entity.setRoutingId(processRouting.getId());
				processRoutingStepMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		processRoutingStepMapper.deleteByMainId(id);
		processRoutingMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			processRoutingStepMapper.deleteByMainId(id.toString());
			processRoutingMapper.deleteById(id);
		}
	}
	
}

package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.CostCalcDetail;
import org.jeecg.modules.scm.mapper.CostCalcDetailMapper;
import org.jeecg.modules.scm.service.ICostCalcDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 成本核算快照明细
 * @Author: jeecg-boot
 * @Date:   2026-07-28
 * @Version: V1.0
 */
@Service
public class CostCalcDetailServiceImpl extends ServiceImpl<CostCalcDetailMapper, CostCalcDetail> implements ICostCalcDetailService {
	
	@Autowired
	private CostCalcDetailMapper costCalcDetailMapper;
	
	@Override
	public List<CostCalcDetail> selectByMainId(String mainId) {
		return costCalcDetailMapper.selectByMainId(mainId);
	}
}

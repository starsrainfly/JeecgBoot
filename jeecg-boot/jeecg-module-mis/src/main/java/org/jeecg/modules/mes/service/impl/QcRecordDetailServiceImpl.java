package org.jeecg.modules.mes.service.impl;

import org.jeecg.modules.mes.entity.QcRecordDetail;
import org.jeecg.modules.mes.mapper.QcRecordDetailMapper;
import org.jeecg.modules.mes.service.IQcRecordDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 质检记录明细
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
@Service
public class QcRecordDetailServiceImpl extends ServiceImpl<QcRecordDetailMapper, QcRecordDetail> implements IQcRecordDetailService {
	
	@Autowired
	private QcRecordDetailMapper qcRecordDetailMapper;
	
	@Override
	public List<QcRecordDetail> selectByMainId(String mainId) {
		return qcRecordDetailMapper.selectByMainId(mainId);
	}
}

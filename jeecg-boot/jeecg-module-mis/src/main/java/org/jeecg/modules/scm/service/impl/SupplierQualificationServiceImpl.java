package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.SupplierQualification;
import org.jeecg.modules.scm.mapper.SupplierQualificationMapper;
import org.jeecg.modules.scm.service.ISupplierQualificationService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 供应商质证表
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Service
public class SupplierQualificationServiceImpl extends ServiceImpl<SupplierQualificationMapper, SupplierQualification> implements ISupplierQualificationService {
	
	@Autowired
	private SupplierQualificationMapper supplierQualificationMapper;
	
	@Override
	public List<SupplierQualification> selectByMainId(String mainId) {
		return supplierQualificationMapper.selectByMainId(mainId);
	}
}

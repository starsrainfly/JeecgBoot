package org.jeecg.modules.mis.supplier.service.impl;

import org.jeecg.modules.mis.supplier.entity.SupplierQualification;
import org.jeecg.modules.mis.supplier.mapper.SupplierQualificationMapper;
import org.jeecg.modules.mis.supplier.service.ISupplierQualificationService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 供应商质证表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
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

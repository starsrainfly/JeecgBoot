package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.CustomerQualification;
import org.jeecg.modules.scm.mapper.CustomerQualificationMapper;
import org.jeecg.modules.scm.service.ICustomerQualificationService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 客户质证
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Service
public class CustomerQualificationServiceImpl extends ServiceImpl<CustomerQualificationMapper, CustomerQualification> implements ICustomerQualificationService {
	
	@Autowired
	private CustomerQualificationMapper customerQualificationMapper;
	
	@Override
	public List<CustomerQualification> selectByMainId(String mainId) {
		return customerQualificationMapper.selectByMainId(mainId);
	}
}

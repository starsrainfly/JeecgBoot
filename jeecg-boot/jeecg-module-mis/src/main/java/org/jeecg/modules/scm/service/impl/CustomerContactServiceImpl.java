package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.CustomerContact;
import org.jeecg.modules.scm.mapper.CustomerContactMapper;
import org.jeecg.modules.scm.service.ICustomerContactService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 客户联系人
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Service
public class CustomerContactServiceImpl extends ServiceImpl<CustomerContactMapper, CustomerContact> implements ICustomerContactService {
	
	@Autowired
	private CustomerContactMapper customerContactMapper;
	
	@Override
	public List<CustomerContact> selectByMainId(String mainId) {
		return customerContactMapper.selectByMainId(mainId);
	}
}

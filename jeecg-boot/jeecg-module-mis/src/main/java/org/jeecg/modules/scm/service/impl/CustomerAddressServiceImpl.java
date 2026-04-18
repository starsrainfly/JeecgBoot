package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.CustomerAddress;
import org.jeecg.modules.scm.mapper.CustomerAddressMapper;
import org.jeecg.modules.scm.service.ICustomerAddressService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 客户地址
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Service
public class CustomerAddressServiceImpl extends ServiceImpl<CustomerAddressMapper, CustomerAddress> implements ICustomerAddressService {
	
	@Autowired
	private CustomerAddressMapper customerAddressMapper;
	
	@Override
	public List<CustomerAddress> selectByMainId(String mainId) {
		return customerAddressMapper.selectByMainId(mainId);
	}
}

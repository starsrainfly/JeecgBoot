package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.CustomerSalesman;
import org.jeecg.modules.scm.mapper.CustomerSalesmanMapper;
import org.jeecg.modules.scm.service.ICustomerSalesmanService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 客户销售员
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Service
public class CustomerSalesmanServiceImpl extends ServiceImpl<CustomerSalesmanMapper, CustomerSalesman> implements ICustomerSalesmanService {
	
	@Autowired
	private CustomerSalesmanMapper customerSalesmanMapper;
	
	@Override
	public List<CustomerSalesman> selectByMainId(String mainId) {
		return customerSalesmanMapper.selectByMainId(mainId);
	}
}

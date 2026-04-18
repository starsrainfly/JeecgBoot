package org.jeecg.modules.scm.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.scm.entity.Customer;
import org.jeecg.modules.scm.entity.CustomerAddress;
import org.jeecg.modules.scm.entity.CustomerQualification;
import org.jeecg.modules.scm.entity.CustomerContact;
import org.jeecg.modules.scm.entity.CustomerSalesman;
import org.jeecg.modules.scm.mapper.CustomerAddressMapper;
import org.jeecg.modules.scm.mapper.CustomerQualificationMapper;
import org.jeecg.modules.scm.mapper.CustomerContactMapper;
import org.jeecg.modules.scm.mapper.CustomerSalesmanMapper;
import org.jeecg.modules.scm.mapper.CustomerMapper;
import org.jeecg.modules.scm.service.ICustomerService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Optional;

/**
 * @Description: 客户信息
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

	@Autowired
	private CustomerMapper customerMapper;
	@Autowired
	private CustomerAddressMapper customerAddressMapper;
	@Autowired
	private CustomerQualificationMapper customerQualificationMapper;
	@Autowired
	private CustomerContactMapper customerContactMapper;
	@Autowired
	private CustomerSalesmanMapper customerSalesmanMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(Customer customer, List<CustomerAddress> customerAddressList,List<CustomerQualification> customerQualificationList,List<CustomerContact> customerContactList,List<CustomerSalesman> customerSalesmanList) {
		customerMapper.insert(customer);

		// 2. 处理业务员逻辑
		handleSalesman(customer, customerSalesmanList);

		if(customerAddressList!=null && customerAddressList.size()>0) {
			for(CustomerAddress entity:customerAddressList) {
				//外键设置
				entity.setCustomerId(customer.getId());
				String districtName = entity.convertisDistrictCode().replace("/","");
				entity.setDistrictName(districtName);
				customerAddressMapper.insert(entity);
			}
		}
		if(customerQualificationList!=null && customerQualificationList.size()>0) {
			for(CustomerQualification entity:customerQualificationList) {
				//外键设置
				entity.setCustomerId(customer.getId());
				customerQualificationMapper.insert(entity);
			}
		}
		if(customerContactList!=null && customerContactList.size()>0) {
			for(CustomerContact entity:customerContactList) {
				//外键设置
				entity.setCustomerId(customer.getId());
				customerContactMapper.insert(entity);
			}
		}
		if(customerSalesmanList!=null && customerSalesmanList.size()>0) {
			for(CustomerSalesman entity:customerSalesmanList) {
				//外键设置
				entity.setCustomerId(customer.getId());
				customerSalesmanMapper.insert(entity);
			}
		}
	}

	/**
	 * 处理业务员逻辑（简化版）
	 */
	private void handleSalesman(Customer customer, List<CustomerSalesman> customerSalesmanList) {
		String salesmanId = customer.getSalesmanId();
		String salesmanName = customer.getSalesmanName();
		if (StrUtil.isBlank(salesmanId)) {
			return;
		}

		// 初始化list（防止null）
		if (customerSalesmanList == null) {
			customerSalesmanList = new ArrayList<>();
		}

		// 检查提交的子表list中是否有该业务员
		Optional<CustomerSalesman> submitSalesman = customerSalesmanList.stream()
				.filter(s -> salesmanId.equals(s.getSalesmanId()))
				.findFirst();

		if (submitSalesman.isPresent()) {
			// 子表中有该业务员
			CustomerSalesman submit = submitSalesman.get();

			if ("1".equals(submit.getStatus())) {
				// 有且启用 → 直接提交，不处理
				log.info("子表中业务员{}已启用", salesmanId);
			} else {
				// 有但未启用 → 更新为启用
				submit.setStatus("1");
				log.info("子表中业务员{}更新为启用", salesmanId);
			}
		} else {
			// 子表没有 → 添加一条记录（主表的业务员信息）
			CustomerSalesman newSalesman = new CustomerSalesman();
			newSalesman.setCustomerId(customer.getId());
			newSalesman.setSalesmanId(salesmanId);
			newSalesman.setSalesman(salesmanName);
			newSalesman.setStatus("1"); // 启用

			customerSalesmanList.add(newSalesman);
			log.info("子表中新增业务员{}", salesmanId);
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(Customer customer,List<CustomerAddress> customerAddressList,List<CustomerQualification> customerQualificationList,List<CustomerContact> customerContactList,List<CustomerSalesman> customerSalesmanList) {
		customerMapper.updateById(customer);

		// 处理业务员逻辑（复用同一方法）
		handleSalesman(customer, customerSalesmanList);
		
		//1.先删除子表数据
		customerAddressMapper.deleteByMainId(customer.getId());
		customerQualificationMapper.deleteByMainId(customer.getId());
		customerContactMapper.deleteByMainId(customer.getId());
		customerSalesmanMapper.deleteByMainId(customer.getId());
		
		//2.子表数据重新插入
		if(customerAddressList!=null && customerAddressList.size()>0) {
			for(CustomerAddress entity:customerAddressList) {
				//外键设置
				entity.setCustomerId(customer.getId());
				customerAddressMapper.insert(entity);
			}
		}
		if(customerQualificationList!=null && customerQualificationList.size()>0) {
			for(CustomerQualification entity:customerQualificationList) {
				//外键设置
				entity.setCustomerId(customer.getId());
				customerQualificationMapper.insert(entity);
			}
		}
		if(customerContactList!=null && customerContactList.size()>0) {
			for(CustomerContact entity:customerContactList) {
				//外键设置
				entity.setCustomerId(customer.getId());
				customerContactMapper.insert(entity);
			}
		}
		if(customerSalesmanList!=null && customerSalesmanList.size()>0) {
			for(CustomerSalesman entity:customerSalesmanList) {
				//外键设置
				entity.setCustomerId(customer.getId());
				customerSalesmanMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		customerAddressMapper.deleteByMainId(id);
		customerQualificationMapper.deleteByMainId(id);
		customerContactMapper.deleteByMainId(id);
		customerSalesmanMapper.deleteByMainId(id);
		customerMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			customerAddressMapper.deleteByMainId(id.toString());
			customerQualificationMapper.deleteByMainId(id.toString());
			customerContactMapper.deleteByMainId(id.toString());
			customerSalesmanMapper.deleteByMainId(id.toString());
			customerMapper.deleteById(id);
		}
	}
	
}

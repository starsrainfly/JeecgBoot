package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.CustomerAddress;
import org.jeecg.modules.scm.entity.CustomerQualification;
import org.jeecg.modules.scm.entity.CustomerContact;
import org.jeecg.modules.scm.entity.CustomerSalesman;
import org.jeecg.modules.scm.entity.Customer;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 客户信息
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
public interface ICustomerService extends IService<Customer> {

	/**
	 * 添加一对多
	 *
	 * @param customer
	 * @param customerAddressList
	 * @param customerQualificationList
	 * @param customerContactList
	 * @param customerSalesmanList
	 */
	public void saveMain(Customer customer,List<CustomerAddress> customerAddressList,List<CustomerQualification> customerQualificationList,List<CustomerContact> customerContactList,List<CustomerSalesman> customerSalesmanList) ;
	
	/**
	 * 修改一对多
	 *
   * @param customer
   * @param customerAddressList
   * @param customerQualificationList
   * @param customerContactList
   * @param customerSalesmanList
	 */
	public void updateMain(Customer customer,List<CustomerAddress> customerAddressList,List<CustomerQualification> customerQualificationList,List<CustomerContact> customerContactList,List<CustomerSalesman> customerSalesmanList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}

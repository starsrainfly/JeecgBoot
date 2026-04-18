package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.CustomerSalesman;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 客户销售员
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
public interface ICustomerSalesmanService extends IService<CustomerSalesman> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<CustomerSalesman>
	 */
	public List<CustomerSalesman> selectByMainId(String mainId);
}

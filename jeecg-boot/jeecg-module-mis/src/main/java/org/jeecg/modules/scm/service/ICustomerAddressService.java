package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.CustomerAddress;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 客户地址
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
public interface ICustomerAddressService extends IService<CustomerAddress> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<CustomerAddress>
	 */
	public List<CustomerAddress> selectByMainId(String mainId);
}

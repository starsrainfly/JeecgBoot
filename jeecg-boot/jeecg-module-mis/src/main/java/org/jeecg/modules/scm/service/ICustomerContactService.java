package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.CustomerContact;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 客户联系人
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
public interface ICustomerContactService extends IService<CustomerContact> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<CustomerContact>
	 */
	public List<CustomerContact> selectByMainId(String mainId);
}

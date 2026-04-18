package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.CustomerQualification;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 客户质证
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
public interface ICustomerQualificationService extends IService<CustomerQualification> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<CustomerQualification>
	 */
	public List<CustomerQualification> selectByMainId(String mainId);
}

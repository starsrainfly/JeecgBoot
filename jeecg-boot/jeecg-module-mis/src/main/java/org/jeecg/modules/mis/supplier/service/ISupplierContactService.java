package org.jeecg.modules.mis.supplier.service;

import org.jeecg.modules.mis.supplier.entity.SupplierContact;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 供应商联系人
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
public interface ISupplierContactService extends IService<SupplierContact> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<SupplierContact>
	 */
	public List<SupplierContact> selectByMainId(String mainId);
}

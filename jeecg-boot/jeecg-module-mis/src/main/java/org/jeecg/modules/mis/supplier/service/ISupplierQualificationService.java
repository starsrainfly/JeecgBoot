package org.jeecg.modules.mis.supplier.service;

import org.jeecg.modules.mis.supplier.entity.SupplierQualification;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 供应商质证表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
public interface ISupplierQualificationService extends IService<SupplierQualification> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<SupplierQualification>
	 */
	public List<SupplierQualification> selectByMainId(String mainId);
}
